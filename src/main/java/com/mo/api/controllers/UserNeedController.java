package com.mo.api.controllers;

import com.mo.core.dtos.CreateNeedWithOrganisationsRequest;
import com.mo.core.dtos.userNeedsDTO.AbstractUserNeedDto;
import com.mo.core.model.needs.AbstractUserNeed;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.products.AbstractProduct;
import com.mo.core.services.OrganisationService;
import com.mo.core.services.UserNeedService;
import com.mo.mappers.needMappers.NeedMapperJackson;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/public/needs")
public class UserNeedController {

    private static final Logger log = LoggerFactory.getLogger(UserNeedController.class);
    private final UserNeedService userNeedService;
    private final OrganisationService organisationService;
    private final NeedMapperJackson needmapper;

    public UserNeedController(
        UserNeedService userNeedService,
        NeedMapperJackson needmapper,
        OrganisationService organisationService
    ) {
        this.userNeedService = userNeedService;
        this.needmapper = needmapper;
        this.organisationService = organisationService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a need",
        description = "Create a user need and return the saved need with matched products",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Need with currency",
                        value = "{\"name\": \"Recherche iPhone reconditionné\", \"max_price\": 200000, \"quantity\": 1, \"currency\": \"XAF\", \"type\": \"ELECTRONIC\", \"description\": \"Cherche iPhone en bon état\", \"user_id\": \"1\", \"photo_urls\": [], \"mandatory_fields\": [\"brand\", \"model\"], \"electronic_type\": \"PHONE\", \"brand\": \"Apple\", \"model\": \"iPhone 11\", \"specifications\": \"{ \\\"min_ram\\\": \\\"4GB\\\" }\", \"warranty_period\": \"6\"}"
                    )
                }
            )
        )
    )
    public ResponseEntity<?> createNeed(@RequestBody AbstractUserNeedDto needDto) {
        log.info("➡️ Received request to create need: {}", needDto);

        AbstractUserNeed savedNeed = userNeedService.createFromDto(needDto);
        log.info("✅ Need saved: id={}, type={}, data={}", savedNeed.getId(), savedNeed.getType(), savedNeed);

        List<Map<String, Object>> matchingProducts = userNeedService.searchMatchingProducts(savedNeed);

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("savedNeed", needmapper.mapToDto(savedNeed));
        responseMap.put("matchingProducts", matchingProducts);

        log.info("✅ Response ready to be returned");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }
    
    
   
    @PostMapping("/toorg")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a need for organisations",
        description = "Create a need and associate it with organisations, returning the saved need and matching products",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Need for organisations",
                        value = "{\"name\": \"Recherche SUV familial\", \"max_price\": 1200000, \"quantity\": 1, \"currency\": \"XAF\", \"type\": \"VEHICLE\", \"description\": \"Véhicule 5 places, faible kilométrage\", \"user_id\": \"1\", \"photo_urls\": [], \"mandatory_fields\": [\"vehicle_type\", \"make\"], \"vehicle_type\": \"CAR\", \"make\": \"Toyota\", \"model\": \"Auris\", \"manufacturing_year\": 2016, \"mileage\": 70000.0, \"fuel_type\": \"Essence\", \"color\": \"Gris\"}"
                    )
                }
            )
        )
    )
    public ResponseEntity<?> createNeedForOrganisations(@RequestBody AbstractUserNeedDto needDto) {
        log.info("➡️ Received request to create need for organisations: {}", needDto);

        AbstractUserNeed savedNeed = userNeedService.createFromDto(needDto);
        log.info("✅ Need saved: id={}, type={}", savedNeed.getId(), savedNeed.getType());

        // Optional search for matching products
        List<Map<String, Object>> matchingProducts = userNeedService.searchMatchingProducts(savedNeed);

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("savedNeed", needmapper.mapToDto(savedNeed));
        responseMap.put("matchingProducts", matchingProducts);

        log.info("✅ Response ready to be returned");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }
    
    
    @PostMapping("/with-organisations")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create need with organisations", description = "Create a need and attach it to the requested organisations")
    @Transactional
    public ResponseEntity<?> createNeedAndAttachToOrganisations(
            @RequestBody @Valid CreateNeedWithOrganisationsRequest request) {

        log.info("➡️ Received request (need + organisations): orgIds={}, need={}",
                request.organisationIds(), request.need());

        AbstractUserNeed savedNeed = userNeedService.createFromDto(request.need());
        log.info("✅ Need saved: id={}, type={}", savedNeed.getId(), savedNeed.getType());

        // 2) Attach to organisations (owner side = Organisation)
        List<Organisation> attached = organisationService.attachNeedToOrganisations(
                savedNeed, request.organisationIds());

        // 4) Optional search for matching products
        List<Map<String, Object>> matchingProducts = userNeedService.searchMatchingProducts(savedNeed);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("savedNeed", savedNeed);
        resp.put("attachedOrganisations", attached.stream().map(Organisation::getId).toList());
        resp.put("matchingProducts", matchingProducts);

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }




















    // ✅ Liste des besoins d’un utilisateur
    @GetMapping("/user/{userId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get user needs", description = "Return all needs created by the specified user")
    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<AbstractUserNeed> getUserNeeds(@PathVariable Long userId) {
        return userNeedService.getNeedsByUserId(userId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete need", description = "Delete a user need by ID")
   // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNeed(@PathVariable Long id) {
        userNeedService.deleteNeed(id);
    }


    // ✅ Produits correspondant aux besoins d’un utilisateur
    @GetMapping("/user/{userId}/matching-products")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get matching products", description = "Return products matching the user's needs")
    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<AbstractProduct> getMatchingProducts(@PathVariable Long userId) {
        return userNeedService.findMatchingProductsForUser(userId);
    }
}


