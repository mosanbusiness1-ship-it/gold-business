package com.mo.core.matchers;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.*;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Component
public class NeedProductMatcher {

	private String normalizeDateTime(Object dateTimeObj) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	    if (dateTimeObj instanceof String str) {
	        try {
	            // Tente de parser la chaîne comme LocalDateTime (incluant les secondes si présent)
	            LocalDateTime parsed = LocalDateTime.parse(str.trim());
	            return parsed.truncatedTo(ChronoUnit.MINUTES).format(formatter);
	        } catch (Exception e) {
	            // Si parsing échoue, retourne trim()
	            return str.trim();
	        }
	    } else if (dateTimeObj instanceof LocalDateTime ldt) {
	        return ldt.truncatedTo(ChronoUnit.MINUTES).format(formatter);
	    } else {
	        return dateTimeObj.toString().trim();
	    }
	}

	
	@SuppressWarnings("unchecked")
	public boolean productMatchesNeed(AbstractProduct product, Map<String, Object> need) {
	    System.out.println("===== MATCH CHECK START =====");
	    System.out.printf("Checking product ID=%s (%s) against need: %s%n", product.getId(), product.getName(), need);

	    Map<String, Function<AbstractProduct, Object>> productFields = new HashMap<>();

	    // Champs communs
	    productFields.put("name", AbstractProduct::getName);
	    //productFields.put("description", AbstractProduct::getDescription);
	    productFields.put("price", AbstractProduct::getPrice);
	    productFields.put("currency", AbstractProduct::getCurrency);
	    productFields.put("certified", AbstractProduct::isCertified);
	    //productFields.put("photo_urls", AbstractProduct::getPhotoUrls);

	    // Champs spécifiques par type
	    switch (product.getType()) {
	        case FOOD -> {
	            FoodProduct food = (FoodProduct) product;
	            productFields.put("organic", p -> food.getOrganic());
	            productFields.put("gluten_free", p -> food.getGlutenFree());
	            productFields.put("expiry_date", p -> food.getExpiryDate());
	            productFields.put("nutritional_info", p -> food.getNutritionalInfo());
	            productFields.put("weight", p -> food.getWeight());            productFields.put("origin_country", p -> food.getOriginCountry());
            productFields.put("organic_cert_id", p -> food.getOrganicCertId());
            productFields.put("allergen_tags", p -> food.getAllergenTags());
            productFields.put("packaging_type", p -> food.getPackagingType());
            productFields.put("shelf_life_days", p -> food.getShelfLifeDays());
            productFields.put("storage_temperature", p -> food.getStorageTemperature());	        }
	        case ELECTRONIC -> {
	            ElectronicProduct e = (ElectronicProduct) product;
	            productFields.put("brand", p -> e.getBrand());
	            productFields.put("model", p -> e.getModel());
	            productFields.put("electronic_type", p -> e.getElectronicType() != null ? e.getElectronicType().name() : null);
	            productFields.put("warranty_period", p -> e.getWarrantyPeriod());
	            productFields.put("specifications", p -> e.getSpecifications());            productFields.put("release_year", p -> e.getReleaseYear());
            productFields.put("battery_health_percent", p -> e.getBatteryHealthPercent());
            productFields.put("accessories_included", p -> e.getAccessoriesIncluded());
            productFields.put("supported_networks", p -> e.getSupportedNetworks());
            productFields.put("warranty_months", p -> e.getWarrantyMonths());
            productFields.put("seller_rating", p -> e.getSellerRating());	        }
	        case FASHION -> {
	            FashionProduct f = (FashionProduct) product;
	            productFields.put("brand", p -> f.getBrand());
	            productFields.put("color", p -> f.getColor());
	            productFields.put("material", p -> f.getMaterial());
	            productFields.put("target_gender", p -> f.getTargetGender());
	            productFields.put("size", p -> f.getSize());
	            productFields.put("size_system", p -> f.getSizeSystem() != null ? f.getSizeSystem().name() : null);
	            productFields.put("fashion_type", p -> f.getFashionType() != null ? f.getFashionType().name() : null);            productFields.put("condition", p -> f.getCondition());
            productFields.put("sustainable_certifications", p -> f.getSustainableCertifications());
            productFields.put("size_fit", p -> f.getSizeFit());
            productFields.put("material_origin", p -> f.getMaterialOrigin());	        }
	        case VEHICLE -> {
	            VehicleProduct v = (VehicleProduct) product;
	            productFields.put("vehicle_type", p -> v.getVehicleType() != null ? v.getVehicleType().name() : null);
	            productFields.put("make", p -> v.getMake());
	            productFields.put("model", p -> v.getModel());
	            productFields.put("manufacturing_year", p -> v.getManufacturingYear());
	            productFields.put("mileage", p -> v.getMileage());
	            productFields.put("fuel_type", p -> v.getFuelType());
	            productFields.put("color", p -> v.getColor());
	            productFields.put("vin_number", p -> v.getVinNumber());            productFields.put("transmission", p -> v.getTransmission());
            productFields.put("trim", p -> v.getTrim());
            productFields.put("fuel_consumption_l_per_100km", p -> v.getFuelConsumptionLPer100km());
            productFields.put("doors", p -> v.getDoors());
            productFields.put("vehicle_condition", p -> v.getVehicleCondition());
            productFields.put("warranty_months", p -> v.getWarrantyMonths());
            productFields.put("seller_rating", p -> v.getSellerRating());	        }
	        case SERVICE -> {
	            ServiceProduct s = (ServiceProduct) product;
	            productFields.put("service_provider", p -> s.getServiceProvider());
	            productFields.put("location", p -> s.getLocation());
	            productFields.put("duration", p -> s.getDuration());
	            productFields.put("online_available", p -> s.getOnlineAvailable());
	            productFields.put("available_slots", p -> s.getAvailableSlots() != null ? s.getAvailableSlots() : List.of());            productFields.put("service_area_radius_km", p -> s.getServiceAreaRadiusKm());
            productFields.put("cancellation_policy", p -> s.getCancellationPolicy());
            productFields.put("languages_spoken", p -> s.getLanguagesSpoken());
            productFields.put("provider_rating", p -> s.getProviderRating());	        }
	        case REALESTATE -> {
	            RealEstateProduct r = (RealEstateProduct) product;
	            productFields.put("address", p -> r.getAddress());
	            productFields.put("city", p -> r.getCity());
	            productFields.put("surface_area", p -> r.getSurfaceArea());
	            productFields.put("room_count", p -> r.getRoomCount());
	            productFields.put("bathroom_count", p -> r.getBathroomCount());
	            productFields.put("real_estate_type", p -> r.getRealEstateType() != null ? r.getRealEstateType().name() : null);
	            productFields.put("is_for_rent", p -> r.getIsForRent());
	            productFields.put("is_for_sale", p -> r.getIsForSale());
	            productFields.put("construction_year", p -> r.getConstructionYear());
	            productFields.put("energy_class", p -> r.getEnergyClass());            productFields.put("floor", p -> r.getFloor());
            productFields.put("balcony", p -> r.getBalcony());
            productFields.put("furnished", p -> r.getFurnished());
            productFields.put("hoa_fees", p -> r.getHoaFees());
            productFields.put("parking", p -> r.getParking());
            productFields.put("energy_rating_numeric", p -> r.getEnergyRatingNumeric());
            productFields.put("neighborhood_tags", p -> r.getNeighborhoodTags());	        }
	    }

	    // Champs avec noms différents
	    Map<String, String> fieldMapping = Map.of(
	        "max_price", "price"
	    );

	    for (Map.Entry<String, Object> entry : need.entrySet()) {
	        String field = entry.getKey();
	        Object needValue = entry.getValue();

	        if (needValue == null || field.equals("all_text") || field.equals("user_id")) continue;// pas necesssaire

	        String productField = fieldMapping.getOrDefault(field, field); // 👈 use field if not mapped

	        Function<AbstractProduct, Object> getter = productFields.get(productField);
	        if (getter == null) {
	            System.out.printf("SKIP: No getter found for field '%s' (mapped as '%s')%n", field, productField);
	            continue;
	        }

	        Object productValue = getter.apply(product);
	        if (productValue == null) {
	            System.out.printf("REJECT: Product value for field '%s' is null%n", productField);
	            return false;
	        }

	        if (needValue instanceof Number && productValue instanceof Number) {
	            double needNum = ((Number) needValue).doubleValue();
	            double productNum = ((Number) productValue).doubleValue();
	            System.out.printf("Compare NUMERIC field '%s': need=%.2f, product=%.2f%n", field, needNum, productNum);

	            if (field.equals("max_price") || field.equals("weight")) {
	                if (productNum > needNum) {
	                    System.out.printf("REJECT: product %s > need %s%n", productNum, needNum);
	                    return false;
	                }
	            } else {
	                if (Double.compare(needNum, productNum) != 0) {
	                    System.out.printf("REJECT: exact number mismatch%n");
	                    return false;
	                }
	            }
	        } else if (productValue instanceof Collection<?> && needValue instanceof Collection<?>) {
	            Collection<?> productCollection = (Collection<?>) productValue;
	            Collection<?> needCollection = (Collection<?>) needValue;
	            System.out.printf("Compare COLLECTION field '%s': product=%s, need=%s%n", field, productCollection, needCollection);

	            if (field.equals("available_slots")) {
	                List<String> productSlots = productCollection.stream()
	                    .map(this::normalizeDateTime)
	                    .toList();
	                List<String> needSlots = needCollection.stream()
	                    .map(this::normalizeDateTime)
	                    .toList();

	                System.out.printf("Compare COLLECTION field '%s' (normalized): product=%s, need=%s%n", field, productSlots, needSlots);

	                if (!productSlots.containsAll(needSlots)) {
	                    System.out.printf("REJECT: product slots do not contain all need slots%n");
	                    return false;
	                }
	            }

	        } else {
	            String productStr = normalizeDateTime(productValue).toLowerCase();
	            String needStr = normalizeDateTime(needValue).toLowerCase();
	            System.out.printf("Compare STRING field '%s': product='%s', need='%s'%n", field, productStr, needStr);

	            if (!productStr.equals(needStr)) {
	                System.out.printf("REJECT: string mismatch on field '%s'%n", field);
	                return false;
	            }
	        }
	    }

	    System.out.println("MATCH: Product matches need ✅");
	    return true;
	}

	
    @SuppressWarnings("unchecked")
    public boolean productSatisfiesMandatoryFields(AbstractProduct product, Map<String, Object> need) {
        List<String> mandatoryFields = (List<String>) need.get("mandatory_fields");
        if (mandatoryFields == null || mandatoryFields.isEmpty()) return true;

        for (String field : mandatoryFields) {
            Object needValue = need.get(field);
            Object productValue = extractProductFieldValue(product, field);

            if (needValue == null || productValue == null) return false;

            if (!needValue.toString().equalsIgnoreCase(productValue.toString())) {
                return false;
            }
        }

        return true;
    }

    public boolean productSatisfiesImportantFields(AbstractProduct product, Map<String, Object> need) {
        List<String> importantFields = (List<String>) need.get("important_fields");
        if (importantFields == null || importantFields.isEmpty()) return true;

        for (String field : importantFields) {
            Object needValue = need.get(field);
            Object productValue = extractProductFieldValue(product, field);

            if (needValue == null || productValue == null) continue; // tolérance ici

            if (!textValuesMatchWithSynonyms(needValue.toString(), productValue.toString())) {
                return false;
            }
        }

        return true;
    }

    
    private Object extractProductFieldValue(AbstractProduct product, String field) {
        // Champs communs
        switch (field) {
            case "name": return product.getName();
            case "description": return product.getDescription();
            case "max_price": return product.getPrice();
            case "certified": return product.isCertified();
            case "photo_urls": return product.getPhotoUrls();
        }

        // Sous-type : FoodProduct
        if (product instanceof FoodProduct f) {
            return switch (field) {
                case "organic" -> f.getOrganic();
                case "gluten_free" -> f.getGlutenFree();
                case "expiry_date" -> f.getExpiryDate();
                case "nutritional_info" -> f.getNutritionalInfo();
                case "weight" -> f.getWeight();
                case "origin_country" -> f.getOriginCountry();
                case "organic_cert_id" -> f.getOrganicCertId();
                case "allergen_tags" -> f.getAllergenTags();
                case "packaging_type" -> f.getPackagingType();
                case "shelf_life_days" -> f.getShelfLifeDays();
                case "storage_temperature" -> f.getStorageTemperature();
                default -> null;
            };
        }

        // Sous-type : FashionProduct
        if (product instanceof FashionProduct f) {
            return switch (field) {
                case "brand" -> f.getBrand();
                case "color" -> f.getColor();
                case "material" -> f.getMaterial();
                case "target_gender" -> f.getTargetGender();
                case "size" -> f.getSize();
                case "size_system" -> f.getSizeSystem() != null ? f.getSizeSystem().name() : null;
                case "fashion_type" -> f.getFashionType() != null ? f.getFashionType().name() : null;
                case "condition" -> f.getCondition();
                case "sustainable_certifications" -> f.getSustainableCertifications();
                case "size_fit" -> f.getSizeFit();
                case "material_origin" -> f.getMaterialOrigin();
                default -> null;
            };
        }

        // Sous-type : ElectronicProduct
        if (product instanceof ElectronicProduct e) {
            return switch (field) {
                case "brand" -> e.getBrand();
                case "model" -> e.getModel();
                case "electronic_type" -> e.getElectronicType() != null ? e.getElectronicType().name() : null;
                case "warranty_period" -> e.getWarrantyPeriod();
                case "specifications" -> e.getSpecifications();
                case "release_year" -> e.getReleaseYear();
                case "battery_health_percent" -> e.getBatteryHealthPercent();
                case "accessories_included" -> e.getAccessoriesIncluded();
                case "supported_networks" -> e.getSupportedNetworks();
                case "warranty_months" -> e.getWarrantyMonths();
                case "seller_rating" -> e.getSellerRating();
                default -> null;
            };
        }

        // Sous-type : VehicleProduct
        if (product instanceof VehicleProduct v) {
            return switch (field) {
                case "vehicle_type" -> v.getVehicleType();
                case "make" -> v.getMake();
                case "model" -> v.getModel();
                case "manufacturing_year" -> v.getManufacturingYear();
                case "mileage" -> v.getMileage();
                case "fuel_type" -> v.getFuelType();
                case "color" -> v.getColor();
                case "vin_number" -> v.getVinNumber();
                case "transmission" -> v.getTransmission();
                case "trim" -> v.getTrim();
                case "fuel_consumption_l_per_100km" -> v.getFuelConsumptionLPer100km();
                case "doors" -> v.getDoors();
                case "vehicle_condition" -> v.getVehicleCondition();
                case "warranty_months" -> v.getWarrantyMonths();
                case "seller_rating" -> v.getSellerRating();
                default -> null;
            };
        }

        // Sous-type : RealEstateProduct
        if (product instanceof RealEstateProduct r) {
            return switch (field) {
                case "address" -> r.getAddress();
                case "city" -> r.getCity();
                case "surface_area" -> r.getSurfaceArea();
                case "room_count" -> r.getRoomCount();
                case "bathroom_count" -> r.getBathroomCount();
                case "real_estate_type" -> r.getRealEstateType();
                case "is_for_rent" -> r.getIsForRent();
                case "is_for_sale" -> r.getIsForSale();
                case "construction_year" -> r.getConstructionYear();
                case "energy_class" -> r.getEnergyClass();
                case "floor" -> r.getFloor();
                case "balcony" -> r.getBalcony();
                case "furnished" -> r.getFurnished();
                case "hoa_fees" -> r.getHoaFees();
                case "parking" -> r.getParking();
                case "energy_rating_numeric" -> r.getEnergyRatingNumeric();
                case "neighborhood_tags" -> r.getNeighborhoodTags();
                default -> null;
            };
        }

        // Sous-type : ServiceProduct
        if (product instanceof ServiceProduct s) {
            return switch (field) {
                case "service_provider" -> s.getServiceProvider();
                case "location" -> s.getLocation();
                case "service_location" -> s.getLocation();
                case "duration" -> s.getDuration();
                case "online_available" -> s.getOnlineAvailable();
                case "available_slots" -> s.getAvailableSlots();
                case "service_area_radius_km" -> s.getServiceAreaRadiusKm();
                case "cancellation_policy" -> s.getCancellationPolicy();
                case "languages_spoken" -> s.getLanguagesSpoken();
                case "provider_rating" -> s.getProviderRating();
                default -> null;
            };
        }

        return null;
    }
    
    
    
    private boolean textValuesMatchWithSynonyms(String a, String b) {
        return normalize(a).equalsIgnoreCase(normalize(b));
    }

    private static final Map<String, String> synonyms = new HashMap<>();

    // Initialisation statique des synonymes
    static {
        synonyms.put("voiture", "véhicule");
        synonyms.put("auto", "véhicule");
        synonyms.put("noir", "sombre");
        synonyms.put("vélo", "bicyclette");
        // Ajouter d'autres termes si nécessaire
    }

    public static String normalize(String val) {
        if (val == null || val.isBlank()) {
            return val; // Retourne tel quel si la valeur est null ou vide
        }

        // Supprimer les accents et convertir en minuscules
        String normalizedVal = Normalizer.normalize(val, Normalizer.Form.NFD)
                                         .replaceAll("\\p{M}", "")
                                         .toLowerCase();

        // Retourner le synonyme ou la version normalisée si aucun synonyme n'existe
        return synonyms.getOrDefault(normalizedVal, normalizedVal);
    }

}

//Souhaites-tu aussi la version avec une classe MatchingResult propre ?
