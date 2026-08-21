package com.mo.core.kafka.consumers;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.auth.User;
import com.mo.core.documents.needs.AbstractUserNeedDocument;
import com.mo.core.dtos.ProductAndMatchedNeedsDTO;
import com.mo.core.dtos.ProductForUserNotification;
import com.mo.core.dtos.autoPurchase.AutoPurchaseDTO;
import com.mo.core.dtos.NotificationData;
import com.mo.core.kafka.producers.MessageProducer;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.services.ElasticsearchService;
import com.mo.core.services.ProductService;
import com.mo.core.services.UserNeedService;
import com.mo.core.services.UserService;
import com.mo.core.visitors.need_visitors.UserNeedIndexerVisitor;
import com.mo.mappers.productsMappers.ProductMapperJackson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageConsumer {

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
	private UserNeedService userNeedService;
	
	@Autowired
	ProductMapperJackson productMapperVisitorJackson;
	
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private static final String INDEX_NAME = "userneeds";

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private UserNeedIndexerVisitor indexerVisitor;
	
	
	@KafkaListener(topics = "filteredSimilarMatches", groupId = "gb")
	public void listenSimilarMatches(String payload, ConsumerRecord<String, String> record) {
		//System.out.println("Recieved similar : "+payload);
	    if (payload == null || payload.isBlank()) {
	        log.warn("Received null or empty payload from topic: {}", record.topic());
	        return;
	    }

	    try {
	        ProductAndMatchedNeedsDTO dto = objectMapper.readValue(payload, ProductAndMatchedNeedsDTO.class);
	        //System.out.println("similaires :"+dto);

	        if (dto.getProduct() == null) {
	            log.warn("No product found in the message: {}", dto);
	            return;
	        }

	        if (dto.getNeeds() == null || dto.getNeeds().isEmpty()) {
	            log.warn("No needs found in the message: {}", dto);
	            return;
	        }

	        List<AbstractUserNeed> validNeeds = dto.getNeeds().stream()
	                .map(this::convertToAbstractUserNeed)
	                .filter(need -> need != null && need.getUser() != null)
	                .toList();

	        for (AbstractUserNeed need : validNeeds) {
	            try {
	                User user = need.getUser();
	                NotificationData data = new NotificationData();

	                data.setUserName(user.getName());
	                data.setUserEmail(user.getEmail());
	                data.setUserPhoneNumber(user.getPhoneNumber());
	                data.setProductName(dto.getProduct().getName());
	                data.setProductAmount(dto.getProduct().getPrice());
	                data.setProductCurrency(dto.getProduct().getCurrency());
	                data.setProductQuantity(dto.getProduct().getQuantity());

	                String message = objectMapper.writeValueAsString(data);
	                messageProducer.send("nytify-user-for-similarMatches", message);

	                log.info("Notification sent to user {} for product {}", user.getId(), dto.getProduct().getId());
	            } catch (Exception ex) {
	                log.error("Error while sending notification for need: {}", need, ex);
	            }
	        }

	    } catch (JsonProcessingException ex) {
	        log.error("Failed to deserialize payload from topic {}: {}", record.topic(), payload, ex);
	    } catch (Exception ex) {
	        log.error("Unexpected error while processing message from topic {}: {}", record.topic(), payload, ex);
	    }
	}


   

	@KafkaListener(topics = "strictMatches", groupId = "strictMatches-group")
	public void listenStrictMatches(String payload) {
		System.out.println("Recieved strict : "+payload);
	    if (payload == null || payload.isBlank()) {
	        log.warn("⚠️ Message reçu vide : {}", payload);
	        return;
	    }

	    try {
	        ProductAndMatchedNeedsDTO dto = objectMapper.readValue(payload, ProductAndMatchedNeedsDTO.class);
	        System.out.println("stricts :"+dto);


	        AbstractProduct needProduct = dto.getProduct();
	        if (needProduct == null) {
	            log.error("❌ Produit manquant dans le message : {}", payload);
	            return;
	        }

	        List<AbstractUserNeed> strictMatches = Optional.ofNullable(dto.getNeeds())
	                .orElse(List.of()).stream()
	                .map(this::convertToAbstractUserNeed)
	                .filter(need -> need != null && need.getUser() != null) // 🔥 Fixe ici
	                .collect(Collectors.toList());

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

	                // 🔁 Auto-Purchase
	                //if (Boolean.TRUE.equals(need.isAutoPurchase())) {
	                    int qty = Optional.ofNullable(need.getQuantity()).orElse(0);
	                    if (qty <= 0) {
	                        log.warn("❌ Quantité invalide pour le besoin : {}", need);
	                        continue;
	                    }

	                    BigDecimal price = Optional.ofNullable(needProduct.getPrice()).orElse(BigDecimal.ZERO);
	                    BigDecimal amount = price.multiply(BigDecimal.valueOf(qty));

	                    AutoPurchaseDTO autoPurchaseDto = new AutoPurchaseDTO(
                            user.getExternalWalletId(),
                            productOwner.getExternalWalletId(),
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
               // }

           
                NotificationData data = new NotificationData();
	                data.setUserName(user.getName());
	                data.setUserEmail(user.getEmail());
	                data.setUserPhoneNumber(user.getPhoneNumber());
	                data.setProductName(needProduct.getName());
	                data.setProductAmount(needProduct.getPrice());
	                data.setProductCurrency(needProduct.getCurrency());
	                data.setProductQuantity(needProduct.getQuantity());

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

	    } catch (JsonProcessingException e) {
	        log.error("❌ Erreur de parsing JSON du message reçu : {}", payload, e);
	    } catch (Exception e) {
	        log.error("❌ Erreur inattendue dans le listener strictMatches", e);
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

    
}
