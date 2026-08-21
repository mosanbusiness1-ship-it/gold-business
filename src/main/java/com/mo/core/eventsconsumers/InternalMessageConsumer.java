package com.mo.core.eventsconsumers;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.Instant;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.auth.User;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.events.*;
import com.mo.core.dtos.ProductAndMatchedNeedsDTO;
import com.mo.core.dtos.autoPurchase.AutoPurchaseDTO;
import com.mo.core.dtos.NotificationData;
import com.mo.core.kafka.consumers.MessageConsumer;
import com.mo.core.kafka.producers.MessageProducer;
import com.mo.core.model.auctions.Auction;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.services.AuctionService;
import com.mo.core.services.ElasticsearchService;
import com.mo.core.services.ProductService;
import com.mo.core.services.UserNeedService;
import com.mo.core.services.UserService;
import com.mo.core.visitors.need_visitors.UserNeedIndexerVisitor;
import com.mo.core.visitors.product_visitors.ProductVisitor;
import com.mo.mappers.productsMappers.ProductMapperJackson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InternalMessageConsumer {

	@Autowired
	private MessageProducer messageProducer;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ElasticsearchService elasticsearchService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuctionService auctionService;
	
	@Autowired
	private UserNeedService userNeedService;
	
	@Autowired
	private ProductVisitor<Double> qualityEvaluationVisitor;
	
	@Autowired
	ProductMapperJackson productMapperVisitorJackson;
	
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private static final String INDEX_NAME = "userneeds";

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private UserNeedIndexerVisitor indexerVisitor;
    
    private final String viewDetailsLink = "http://localhost:3000/gb/product";

    @EventListener
    public void onFilteredSimilarMatch(FilteredSimilarMatchEvent event) {
        ProductAndMatchedNeedsDTO data = event.getData();

        if (data.getProduct() == null) {
            log.warn("No product found in the filteredSimilarMatch event");
            return;
        }

        if (data.getNeeds() == null || data.getNeeds().isEmpty()) {
            log.warn("No needs found in the filteredSimilarMatch event");
            return;
        }

        List<AbstractUserNeed> validNeeds = data.getNeeds().stream()
                .map(this::convertToAbstractUserNeed)
                .filter(need -> need != null && need.getUser() != null)
                .toList();

        for (AbstractUserNeed need : validNeeds) {
            try {
                User user = need.getUser();
                NotificationData notificationData = new NotificationData();

                notificationData.setUserName(user.getName());
                notificationData.setUserEmail(user.getEmail());
                notificationData.setUserPhoneNumber(user.getPhoneNumber());
                notificationData.setProductName(data.getProduct().getName());
                notificationData.setProductAmount(data.getProduct().getPrice());
                notificationData.setProductCurrency(data.getProduct().getCurrency());
                notificationData.setProductQuantity(data.getProduct().getQuantity());
                notificationData.setViewDetailsLink(viewDetailsLink);
                notificationData.setEventType("match_event");
                notificationData.setMatchType("similar");
                notificationData.setMatchCount(validNeeds.size());
                notificationData.setProductId(data.getProduct().getId());
                notificationData.setEventTimestamp(Instant.now().toString());
                notificationData.setDetails("Similar match notification for product and user needs.");

                if (need.getId() != null) {
                    notificationData.setNeedId(need.getId());
                }
          

                String message = objectMapper.writeValueAsString(notificationData);
                messageProducer.send("nytify-user-for-similarMatches", message);

                log.info("Notification sent to user {} for product {}", user.getId(), data.getProduct().getId());
            } catch (Exception ex) {
                log.error("Error while sending notification for need: {}", need, ex);
            }
        }
    }

    @EventListener
    public void onStrictMatch(StrictMatchEvent event) {
        ProductAndMatchedNeedsDTO dto = event.getData();

        AbstractProduct needProduct = dto.getProduct();
        if (needProduct == null) {
            log.error("❌ Produit manquant dans le strictMatch event");
            return;
        }

        List<AbstractUserNeed> strictMatches = Optional.ofNullable(dto.getNeeds())
                .orElse(List.of()).stream()
                .map(this::convertToAbstractUserNeed)
                .filter(need -> need != null && need.getUser() != null)
                .toList();

        if (strictMatches.isEmpty()) {
            log.info("ℹ️ Aucun besoin strict valide trouvé.");
            return;
        }

        for (AbstractUserNeed need : strictMatches) {
            try {
                User user = need.getUser();
                if (user == null) {
                    log.warn("⚠️ Besoin sans utilisateur : {}", need);
                    continue;
                }

                User productOwner = needProduct.getOwner();
                if (productOwner == null) {
                    log.warn("⚠️ Produit sans propriétaire : {}", needProduct);
                    continue;
                }

                int qty = Optional.ofNullable(need.getQuantity()).orElse(0);
                if (qty <= 0) {
                    log.warn("❌ Quantité invalide pour le besoin : {}", need);
                    continue;
                }

                BigDecimal price = Optional.ofNullable(needProduct.getPrice()).orElse(BigDecimal.ZERO);
                BigDecimal amount = price.multiply(BigDecimal.valueOf(qty));

                AutoPurchaseDTO autoPurchaseDto = new AutoPurchaseDTO(
                        //user.getExternalWalletId(),
                        //productOwner.getExternalWalletId(),
                		"WALLET1234",
                		"WALLET1235",
                        needProduct.getId(),
                        needProduct.getName(),
                        qty,
                        needProduct.getCurrency(),
                        amount
                );

                messageProducer.send("auto-purchase", objectMapper.writeValueAsString(autoPurchaseDto))
                        .thenAccept(result -> log.info("✔ AutoPurchase envoyé : {}", autoPurchaseDto))
                        .exceptionally(ex -> {
                            log.error("❌ Erreur Kafka pour AutoPurchaseDTO : {}", autoPurchaseDto, ex);
                            return null;
                        });

                NotificationData data = new NotificationData();
                data.setUserName(user.getName());
                data.setUserEmail(user.getEmail());
                data.setUserFullName(user.getFullName());
                data.setUserPhoneNumber(user.getPhoneNumber());
                data.setProductName(needProduct.getName());
                data.setNeedDescription(need.getDescription());
                data.setProductAmount(needProduct.getPrice());
                data.setProductCurrency(needProduct.getCurrency());
                data.setProductQuantity(needProduct.getQuantity());
                data.setViewDetailsLink(viewDetailsLink);
                    data.setEventType("match_event");
                    data.setMatchType("strict");
                    data.setMatchCount(strictMatches.size());
                    data.setProductId(needProduct.getId());
                    data.setNeedId(need.getId());
                    data.setEventTimestamp(Instant.now().toString());
                    data.setDetails("Strict match notification with auto-purchase option.");

                messageProducer.send("notify-user-for-strictMatches", objectMapper.writeValueAsString(data))
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.error("❌ Erreur Kafka pour Notification : {}", data, ex);
                            } else {
                                log.info("✔ Notification envoyée : topic={}, partition={}, offset={}",
                                        result.getRecordMetadata().topic(),
                                        result.getRecordMetadata().partition(),
                                        result.getRecordMetadata().offset());
                            }
                        });

            } catch (Exception e) {
                log.error("❌ Erreur lors du traitement d’un besoin : {}", need, e);
            }
        }
    }
    
    
	private AbstractUserNeed convertToAbstractUserNeed(Map<String, Object> map) {
	    try {
	        AbstractUserNeed need = objectMapper.convertValue(map, AbstractUserNeed.class);

	        Object userIdObj = map.get("user_id");
	        if (userIdObj != null) {
	            try {
	                Long userId = Long.parseLong(userIdObj.toString());
	                Optional<User> optionalUser = userService.findById(userId);

	                if (optionalUser.isPresent()) {
	                    User user = optionalUser.get();
	                    Hibernate.initialize(user.getRoles()); // ou autres collections nécessaires
	                    need.setUser(user);
	                } else {
	                    log.warn("Aucun utilisateur trouvé avec l'ID : {}", userId);
	                }
	            } catch (NumberFormatException e) {
	                log.warn("ID utilisateur invalide : {}", userIdObj);
	            }
	        } else {
	            log.warn("Aucune clé 'user_id' dans la map.");
	        }

	        return need;
	    } catch (IllegalArgumentException e) {
	        log.error("Erreur de conversion de la map en AbstractUserNeed : {}", map, e);
	        return null;
	    }
	}
	
	
	@EventListener
	public void onAuctionEnded(AuctionEndedEvent event) {
	    Auction auction = event.getAuction();
	    List<AbstractProduct> products = auctionService.getProducts(auction.getId());

	    if (products.isEmpty()) {
	        log.warn("No products found for auction {}", auction.getId());
	        return;
	    }

	    BigDecimal maxPrice = auction.getNeed().getMaxPrice();
	    if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) <= 0) {
	        log.warn("Auction {} has invalid maxPrice", auction.getId());
	        return;
	    }

	    double alpha = 0.6;

	    AbstractProduct winningProduct = products.stream()
	        .max(Comparator.comparingDouble(p -> {
	            double quality = p.accept(qualityEvaluationVisitor);
	            double priceScore = 1 - (p.getPrice().doubleValue() / maxPrice.doubleValue());
	            return alpha * priceScore + (1 - alpha) * quality;
	        }))
	        .orElse(null);

	    if (winningProduct == null) {
	        log.warn("No winning product for auction {}", auction.getId());
	        return;
	    }

	    AbstractUserNeed need = auction.getNeed();
	    User user = need.getUser();
	    if (user == null) {
	        log.warn("Auction {} need has no user assigned", auction.getId());
	        return;
	    }

	    try {
	        NotificationData notificationData = new NotificationData();
	        notificationData.setUserName(user.getName());
	        notificationData.setUserEmail(user.getEmail());
	        notificationData.setUserPhoneNumber(user.getPhoneNumber());
	        notificationData.setProductName(winningProduct.getName());
	        notificationData.setProductAmount(winningProduct.getPrice());
	        notificationData.setProductCurrency(winningProduct.getCurrency());
	        notificationData.setProductQuantity(need.getQuantity());
	        notificationData.setViewDetailsLink(viewDetailsLink);
            notificationData.setEventType("auction_event");
            notificationData.setMatchType("auction_win");
            notificationData.setAuctionId(auction.getId());
            notificationData.setNeedId(need.getId());
            notificationData.setProductId(winningProduct.getId());
            notificationData.setEventTimestamp(Instant.now().toString());
            notificationData.setDetails("Auction ended and winning product selected based on price/quality score.");

	        String message = objectMapper.writeValueAsString(notificationData);
	        messageProducer.send("notify-user-for-winningAuction", message);
	        log.info("Notification sent to user {} for winning product {} in auction {}",
	                user.getId(), winningProduct.getId(), auction.getId());

	        if (need.isAutoPurchase()) {
	            AutoPurchaseDTO autoPurchaseDto = new AutoPurchaseDTO(
                    user.getExternalWalletId(),
                    winningProduct.getOwner().getExternalWalletId(),
	                    winningProduct.getId(),
	                    winningProduct.getName(),
	                    need.getQuantity(),
	                    winningProduct.getCurrency(),
	                    winningProduct.getPrice().multiply(BigDecimal.valueOf(need.getQuantity()))
	            );
	            messageProducer.send("auto-purchase", objectMapper.writeValueAsString(autoPurchaseDto))
	                    .thenAccept(result -> log.info("✔ AutoPurchase envoyé : {}", autoPurchaseDto))
	                    .exceptionally(ex -> {
	                        log.error("❌ Erreur Kafka pour AutoPurchaseDTO : {}", autoPurchaseDto, ex);
	                        return null;
	                    });
	        }

	    } catch (Exception ex) {
	        log.error("Error while sending notification for auction {}: {}", auction.getId(), ex.getMessage(), ex);
	    }

	    auction.setActived(false);
	    auctionService.save(auction);
	}


}
