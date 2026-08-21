package com.mo.core.visitors.product_visitors;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;
import com.mo.core.model.products.*;
import com.mo.core.visitors.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import co.elastic.clients.elasticsearch._types.FieldValue;

@Visitor("matchingVisitor")
@Component
public class MatchingUserNeedVisitor implements ProductVisitor<NativeQuery> {

    @Override
    public NativeQuery visit(VehicleProduct product) {
        List<Query> shouldQueries = new ArrayList<>();
        List<Query> mustQueries = new ArrayList<>();

        // term (type = VEHICLE)
        Query typeQuery = Query.of(q -> q
            .term(t -> t
                .field("type.keyword")
                .value("VEHICLE")
            )
        );
        
     // ➕ Critère obligatoire : notify_similar_products == true
        mustQueries.add(Query.of(q -> q
            .term(t -> t
                .field("notify_similar_products")
                .value(true)
            )
        ));

        if (product.getMake() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("make.keyword")
                    .value(product.getMake())
                    .boost(2.0f)
                )
            ));
        }

        if (product.getModel() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("model.keyword")
                    .value(product.getModel())
                    .boost(2.0f)
                )
            ));
        }

        if (product.getManufacturingYear() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("manufacturing_year")
                        .gte(product.getManufacturingYear().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getVehicleType() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("vehicle_type.keyword")
                    .value(product.getVehicleType().name())
                    .boost(2.0f)
                )
            ));
        }

        if (product.getFuelType() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("fuel_type.keyword")
                    .value(product.getFuelType())
                    .boost(1.5f)
                )
            ));
        }

        if (product.getColor() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("color.keyword")
                    .value(product.getColor())
                    .boost(1.0f)
                )
            ));
        }

        if (product.getMileage() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("mileage")
                        .lte(product.getMileage().doubleValue()) // ✅ conversion BigDecimal → double
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getVinNumber() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("vin_number.keyword")
                    .value(product.getVinNumber())
                    .boost(3.0f)
                )
            ));
        }

        // Enriched fields
        if (product.getTransmission() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("transmission.keyword")
                    .value(product.getTransmission())
                    .boost(1.3f)
                )
            ));
        }

        if (product.getTrim() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("trim.keyword")
                    .value(product.getTrim())
                    .boost(1.1f)
                )
            ));
        }

        if (product.getFuelConsumptionLPer100km() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("fuel_consumption_l_per_100km")
                        .lte(product.getFuelConsumptionLPer100km().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getDoors() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("doors")
                        .gte(product.getDoors().doubleValue())
                        .boost(1.1f)
                    )
                )
            ));
        }

        if (product.getVehicleCondition() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("vehicle_condition.keyword")
                    .value(product.getVehicleCondition())
                    .boost(1.4f)
                )
            ));
        }

        if (product.getWarrantyMonths() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("warranty_months")
                        .gte(product.getWarrantyMonths().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getSellerRating() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("seller_rating")
                        .gte(product.getSellerRating().doubleValue())
                        .boost(1.3f)
                    )
                )
            ));
        }

        if (product.getPrice() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("max_price")
                        .gte(product.getPrice().doubleValue()) // ✅ conversion BigDecimal → double
                        .boost(2.0f)
                    )
                )
            ));
//            shouldQueries.add(Query.of(q -> q
//                    .term(t -> t
//                        .field("currency.keyword")
//                        .value(product.getCurrency().name())
//                        .boost(1.8f)
//                    )
//            ));
        }

        if (product.getName() != null || product.getDescription() != null) {
            String keywords = Stream.of(product.getName(), product.getDescription())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
            if (!keywords.isBlank()) {
                shouldQueries.add(Query.of(q -> q
                    .match(m -> m
                        .field("all_text")
                        .query(keywords)
                        .boost(1.5f)
                    )
                ));
            }
        }

        
        mustQueries.add(typeQuery);

        BoolQuery boolQuery = BoolQuery.of(b -> b
            .must(mustQueries)
            .should(shouldQueries)
            .minimumShouldMatch("1")
        );

        return NativeQuery.builder()
            .withQuery(Query.of(q -> q.bool(boolQuery)))
            .build();
    }
    
    
    @Override
    public NativeQuery visit(ElectronicProduct product) {
        List<Query> shouldQueries = new ArrayList<>();
        List<Query> mustQueries = new ArrayList<>();

        // term (type = ELECTRONIC)
        Query typeQuery = Query.of(q -> q
            .term(t -> t
                .field("type.keyword")
                .value("ELECTRONIC")
            )
        );
        
     // ➕ Critère obligatoire : notify_similar_products == true
        mustQueries.add(Query.of(q -> q
            .term(t -> t
                .field("notify_similar_products")
                .value(true)
            )
        ));

        if (product.getBrand() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("brand.keyword")
                    .value(product.getBrand())
                    .boost(2.0f)
                )
            ));
        }

        if (product.getModel() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("model.keyword")
                    .value(product.getModel())
                    .boost(2.0f)
                )
            ));
        }

        if (product.getElectronicType() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("electronic_type.keyword")
                    .value(product.getElectronicType().name())
                    .boost(1.5f)
                )
            ));
        }

        if (product.getWarrantyPeriod() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("warranty_period")
                    .value(product.getWarrantyPeriod())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getSpecifications() != null) {
            shouldQueries.add(Query.of(q -> q
                .match(m -> m
                    .field("specifications")
                    .query(product.getSpecifications())
                    .boost(1.0f)
                )
            ));
        }

        // Enriched fields
        if (product.getReleaseYear() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("release_year")
                        .gte(product.getReleaseYear().doubleValue())
                        .boost(1.1f)
                    )
                )
            ));
        }

        if (product.getBatteryHealthPercent() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("battery_health_percent")
                        .gte(product.getBatteryHealthPercent().doubleValue())
                        .boost(1.3f)
                    )
                )
            ));
        }

        if (product.getAccessoriesIncluded() != null && !product.getAccessoriesIncluded().isEmpty()) {
            List<FieldValue> accessoryValues = product.getAccessoriesIncluded().stream()
                .map(FieldValue::of)
                .toList();
            shouldQueries.add(Query.of(q -> q
                .terms(t -> t
                    .field("accessories_included.keyword")
                    .terms(terms -> terms
                        .value(accessoryValues)
                    )
                    .boost(1.2f)
                )
            ));
        }

        if (product.getSupportedNetworks() != null && !product.getSupportedNetworks().isEmpty()) {
            List<FieldValue> networkValues = product.getSupportedNetworks().stream()
                .map(FieldValue::of)
                .toList();
            shouldQueries.add(Query.of(q -> q
                .terms(t -> t
                    .field("supported_networks.keyword")
                    .terms(terms -> terms
                        .value(networkValues)
                    )
                    .boost(1.2f)
                )
            ));
        }

        if (product.getWarrantyMonths() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("warranty_months")
                        .gte(product.getWarrantyMonths().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getSellerRating() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("seller_rating")
                        .gte(product.getSellerRating().doubleValue())
                        .boost(1.3f)
                    )
                )
            ));
        }

        if (product.getPrice() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("max_price")
                        .gte(product.getPrice().doubleValue())
                        .boost(2.0f)
                    )
                )
            ));
//            shouldQueries.add(Query.of(q -> q
//                    .term(t -> t
//                        .field("currency.keyword")
//                        .value(product.getCurrency().name())
//                        .boost(1.8f)
//                    )
//            ));
        }

        
        mustQueries.add(typeQuery);

        BoolQuery boolQuery = BoolQuery.of(b -> b
            .must(mustQueries)
            .should(shouldQueries)
            .minimumShouldMatch("1")
        );

        return NativeQuery.builder()
            .withQuery(Query.of(q -> q.bool(boolQuery)))
            .build();
    }
    
    @Override
    public NativeQuery visit(FashionProduct product) {
        List<Query> shouldQueries = new ArrayList<>();
        List<Query> mustQueries = new ArrayList<>();

        // term (type = FASHION)
        Query typeQuery = Query.of(q -> q
            .term(t -> t
                .field("type.keyword")
                .value("FASHION")
            )
        );
        
     // ➕ Critère obligatoire : notify_similar_products == true
        mustQueries.add(Query.of(q -> q
            .term(t -> t
                .field("notify_similar_products")
                .value(true)
            )
        ));

        if (product.getBrand() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("brand.keyword")
                    .value(product.getBrand())
                    .boost(2.0f)
                )
            ));
        }

        if (product.getColor() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("color.keyword")
                    .value(product.getColor())
                    .boost(1.5f)
                )
            ));
        }

        if (product.getMaterial() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("material.keyword")
                    .value(product.getMaterial())
                    .boost(1.5f)
                )
            ));
        }

        if (product.getTargetGender() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("target_gender.keyword")
                    .value(product.getTargetGender())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getSize() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("size.keyword")
                    .value(product.getSize())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getSizeSystem() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("size_system.keyword")
                    .value(product.getSizeSystem().name())
                    .boost(1.0f)
                )
            ));
        }

        if (product.getFashionType() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("fashion_type.keyword")
                    .value(product.getFashionType().name())
                    .boost(1.5f)
                )
            ));
        }

        // Enriched fields
        if (product.getCondition() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("condition.keyword")
                    .value(product.getCondition())
                    .boost(1.3f)
                )
            ));
        }

        if (product.getSustainableCertifications() != null && !product.getSustainableCertifications().isEmpty()) {
            List<FieldValue> certValues = product.getSustainableCertifications().stream()
                .map(FieldValue::of)
                .toList();
            shouldQueries.add(Query.of(q -> q
                .terms(t -> t
                    .field("sustainable_certifications.keyword")
                    .terms(terms -> terms
                        .value(certValues)
                    )
                    .boost(1.2f)
                )
            ));
        }

        if (product.getSizeFit() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("size_fit.keyword")
                    .value(product.getSizeFit())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getMaterialOrigin() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("material_origin.keyword")
                    .value(product.getMaterialOrigin())
                    .boost(1.1f)
                )
            ));
        }

        if (product.getPrice() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("max_price")
                        .gte(product.getPrice().doubleValue())
                        .boost(2.0f)
                    )
                )
            ));
//            shouldQueries.add(Query.of(q -> q
//                    .term(t -> t
//                        .field("currency.keyword")
//                        .value(product.getCurrency().name())
//                        .boost(1.8f)
//                    )
//            ));
        }

        
        mustQueries.add(typeQuery);

        BoolQuery boolQuery = BoolQuery.of(b -> b
            .must(mustQueries)
            .should(shouldQueries)
            .minimumShouldMatch("1")
        );

        return NativeQuery.builder()
            .withQuery(Query.of(q -> q.bool(boolQuery)))
            .build();
    }
    
    @Override
    public NativeQuery visit(FoodProduct product) {
        List<Query> shouldQueries = new ArrayList<>();
        List<Query> mustQueries = new ArrayList<>();

        // term (type = FOOD)
        Query typeQuery = Query.of(q -> q
            .term(t -> t
                .field("type.keyword")
                .value("FOOD")
            )
        );
        
     // ➕ Critère obligatoire : notify_similar_products == true
        mustQueries.add(Query.of(q -> q
            .term(t -> t
                .field("notify_similar_products")
                .value(true)
            )
        ));

        if (product.getCategory() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("food_category.keyword")
                    .value(product.getCategory().name())
                    .boost(2.0f)
                )
            ));
        }

        if (product.getOrganic() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("organic")
                    .value(product.getOrganic())
                    .boost(1.5f)
                )
            ));
        }

        if (product.getExpiryDate() != null) {
            shouldQueries.add(
                Query.of(q -> q
                    .range(r -> r
                        .date(d -> d
                            .field("expiry_date")
                            .lte(product.getExpiryDate().toString())
                        )
                    )
                    //.boost(1.2f)  // boost appliqué ici au niveau du Query
                )
            );
        }

        if (product.getExpiryDate() != null) {
            shouldQueries.add(
                Query.of(q -> q
                    .range(r -> r
                        .date(d -> d
                            .field("expiry_date")
                            .lte(product.getExpiryDate().toString())
                        )
                    )
//                    .boost(1.2f)  // boost appliqué ici au niveau du Query
                )
            );
        }

        if (product.getNutritionalInfo() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("nutritional_info.keyword")
                    .value(product.getNutritionalInfo())
                    .boost(1.0f)
                )
            ));
        }

        if (product.getWeight() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("weight")
                        .gte(product.getWeight().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        // Enriched fields
        if (product.getOriginCountry() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("origin_country.keyword")
                    .value(product.getOriginCountry())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getOrganicCertId() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("organic_cert_id.keyword")
                    .value(product.getOrganicCertId())
                    .boost(1.3f)
                )
            ));
        }

        if (product.getAllergenTags() != null && !product.getAllergenTags().isEmpty()) {
            List<FieldValue> tagValues = product.getAllergenTags().stream()
                .map(FieldValue::of)
                .toList();
            shouldQueries.add(Query.of(q -> q
                .terms(t -> t
                    .field("allergen_tags.keyword")
                    .terms(terms -> terms
                        .value(tagValues)
                    )
                    .boost(1.4f)
                )
            ));
        }

        if (product.getPackagingType() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("packaging_type.keyword")
                    .value(product.getPackagingType())
                    .boost(1.1f)
                )
            ));
        }

        if (product.getShelfLifeDays() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("shelf_life_days")
                        .gte(product.getShelfLifeDays().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getStorageTemperature() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("storage_temperature.keyword")
                    .value(product.getStorageTemperature())
                    .boost(1.1f)
                )
            ));
        }

        if (product.getPrice() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("max_price")
                        .gte(product.getPrice().doubleValue())
                        .boost(2.0f)
                    )
                )
            ));
//            shouldQueries.add(Query.of(q -> q
//                    .term(t -> t
//                        .field("currency.keyword")
//                        .value(product.getCurrency().name())
//                        .boost(1.8f)
//                    )
//            ));
        }


        mustQueries.add(typeQuery);
        mustQueries.addAll(shouldQueries);

        BoolQuery boolQuery = BoolQuery.of(b -> b
            .must(mustQueries)
        );

        return NativeQuery.builder()
            .withQuery(Query.of(q -> q.bool(boolQuery)))
            .build();
    }


    @Override
    public NativeQuery visit(RealEstateProduct product) {
        List<Query> shouldQueries = new ArrayList<>();
        List<Query> mustQueries = new ArrayList<>();
        
        // term (type = REAL_ESTATE)
        Query typeQuery = Query.of(q -> q
            .term(t -> t
                .field("type.keyword")
                .value("REAL_ESTATE")
            )
        );
        
     // ➕ Critère obligatoire : notify_similar_products == true
        mustQueries.add(Query.of(q -> q
            .term(t -> t
                .field("notify_similar_products")
                .value(true)
            )
        ));

        if (product.getCity() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("city.keyword")
                    .value(product.getCity())
                    .boost(1.5f)
                )
            ));
        }

        if (product.getAddress() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("address.keyword")
                    .value(product.getAddress())
                    .boost(1.5f)
                )
            ));
        }

        if (product.getSurfaceArea() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("surface_area")
                        .lte(product.getSurfaceArea().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getRoomCount() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("room_count")
                        .lte(product.getRoomCount().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getBathroomCount() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("bathroom_count")
                        .lte(product.getBathroomCount().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getRealEstateType() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("real_estate_type.keyword")
                    .value(product.getRealEstateType().name())
                    .boost(1.5f)
                )
            ));
        }

        if (product.getIsForRent() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("is_for_rent")
                    .value(product.getIsForRent())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getIsForSale() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("is_for_sale")
                    .value(product.getIsForSale())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getConstructionYear() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("construction_year")
                        .gte(product.getConstructionYear().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getEnergyClass() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("energy_class.keyword")
                    .value(product.getEnergyClass())
                    .boost(1.0f)
                )
            ));
        }

        // Enriched fields
        if (product.getFloor() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("floor")
                        .gte(product.getFloor().doubleValue())
                        .boost(1.1f)
                    )
                )
            ));
        }

        if (product.getBalcony() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("balcony")
                    .value(product.getBalcony())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getFurnished() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("furnished")
                    .value(product.getFurnished())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getHoaFees() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("hoa_fees")
                        .lte(product.getHoaFees().doubleValue())
                        .boost(1.1f)
                    )
                )
            ));
        }

        if (product.getParking() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("parking.keyword")
                    .value(product.getParking())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getEnergyRatingNumeric() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("energy_rating_numeric")
                        .gte(product.getEnergyRatingNumeric().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getNeighborhoodTags() != null && !product.getNeighborhoodTags().isEmpty()) {
            List<FieldValue> tagValues = product.getNeighborhoodTags().stream()
                .map(FieldValue::of)
                .toList();
            shouldQueries.add(Query.of(q -> q
                .terms(t -> t
                    .field("neighborhood_tags.keyword")
                    .terms(terms -> terms
                        .value(tagValues)
                    )
                    .boost(1.1f)
                )
            ));
        }

        if (product.getPrice() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("max_price")
                        .gte(product.getPrice().doubleValue())
                        .boost(2.0f)
                    )
                )
            ));
//            shouldQueries.add(Query.of(q -> q
//                    .term(t -> t
//                        .field("currency.keyword")
//                        .value(product.getCurrency().name())
//                        .boost(1.8f)
//                    )
//            ));
        }

//        BoolQuery boolQuery = BoolQuery.of(b -> b
//            .must(typeQuery)
//            .should(shouldQueries)
//        );
        
        mustQueries.add(typeQuery);
        mustQueries.addAll(shouldQueries);

        BoolQuery boolQuery = BoolQuery.of(b -> b
            .must(mustQueries)
        );

        return NativeQuery.builder()
            .withQuery(Query.of(q -> q.bool(boolQuery)))
            .build();
    }
    
    @Override
    public NativeQuery visit(ServiceProduct product) {
        List<Query> shouldQueries = new ArrayList<>();
        List<Query> mustQueries = new ArrayList<>();
        
        // term (type = SERVICE)
        Query typeQuery = Query.of(q -> q
            .term(t -> t
                .field("type.keyword")
                .value("SERVICE")
            )
        );
        
     // ➕ Critère obligatoire : notify_similar_products == true
        mustQueries.add(Query.of(q -> q
            .term(t -> t
                .field("notify_similar_products")
                .value(true)
            )
        ));

        if (product.getServiceProvider() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("service_provider.keyword")
                    .value(product.getServiceProvider())
                    .boost(2.0f)
                )
            ));
        }

        if (product.getLocation() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("location.keyword")
                    .value(product.getLocation())
                    .boost(1.8f)
                )
            ));
        }

        if (product.getDuration() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("duration")
                        .gte(product.getDuration().doubleValue()) // conversion si besoin
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getOnlineAvailable() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("online_available")
                    .value(product.getOnlineAvailable())
                    .boost(1.5f)
                )
            ));
        }

        if (product.getAvailableSlots() != null && !product.getAvailableSlots().isEmpty()) {
        	
        	List<FieldValue> slotsAsFieldValues = product.getAvailableSlots().stream()
        		    .map(FieldValue::of) // FieldValue accepte Instant (ou configurez mapper si besoin)
        		    .toList();

        	shouldQueries.add(Query.of(q -> q
        	    .terms(t -> t
        	        .field("available_slots")
        	        .terms(terms -> terms
        	            .value(slotsAsFieldValues)
        	        )
        	        .boost(1.5f)
        	    )
        	));

        }

        // Enriched fields
        if (product.getServiceAreaRadiusKm() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("service_area_radius_km")
                        .gte(product.getServiceAreaRadiusKm().doubleValue())
                        .boost(1.2f)
                    )
                )
            ));
        }

        if (product.getCancellationPolicy() != null) {
            shouldQueries.add(Query.of(q -> q
                .term(t -> t
                    .field("cancellation_policy.keyword")
                    .value(product.getCancellationPolicy())
                    .boost(1.2f)
                )
            ));
        }

        if (product.getLanguagesSpoken() != null && !product.getLanguagesSpoken().isEmpty()) {
            List<FieldValue> langValues = product.getLanguagesSpoken().stream()
                .map(FieldValue::of)
                .toList();
            shouldQueries.add(Query.of(q -> q
                .terms(t -> t
                    .field("languages_spoken.keyword")
                    .terms(terms -> terms
                        .value(langValues)
                    )
                    .boost(1.1f)
                )
            ));
        }

        if (product.getProviderRating() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("provider_rating")
                        .gte(product.getProviderRating().doubleValue())
                        .boost(1.3f)
                    )
                )
            ));
        }

        if (product.getPrice() != null) {
            shouldQueries.add(Query.of(q -> q
                .range(r -> r
                    .number(n -> n
                        .field("max_price")
                        .gte(product.getPrice().doubleValue())
                        .boost(2.0f)
                    )
                )
            ));
//            shouldQueries.add(Query.of(q -> q
//                    .term(t -> t
//                        .field("currency.keyword")
//                        .value(product.getCurrency().name())
//                        .boost(1.8f)
//                    )
//            ));
        }


        mustQueries.add(typeQuery);
        mustQueries.addAll(shouldQueries);

        BoolQuery boolQuery = BoolQuery.of(b -> b
            .must(mustQueries)
        );

        return NativeQuery.builder()
            .withQuery(Query.of(q -> q.bool(boolQuery)))
            .build();
    }



}

//Objectif fonctionnel
//Le besoin est un sous-ensemble du produit
//Autrement dit :
//🟩 Si un champ est absent du besoin, on l'ignore
//🟥 Si un champ est présent dans le besoin mais pas dans le produit → on exclut ce besoin
//🟨 Si le champ est dans les deux → on compare


