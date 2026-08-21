package com.mo.core.visitors.need_visitors;

import com.mo.core.documents.needs.*;
import com.mo.core.documents.products.*;

import com.mo.core.model.needs.*;
import com.mo.core.visitors.Visitor;

import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Visitor("userNeedIndexerVisitor")
public class UserNeedIndexerVisitor implements UserNeedVisitor<AbstractUserNeedDocument> {


    @Override
    public AbstractUserNeedDocument visit(FashionNeed need) {
        return FashionNeedDocument.builder()
                .id(need.getId())
                .type(need.getType())
                .userId(need.getUser().getId())
                .name(need.getName())
                .description(need.getDescription())
                .maxPrice(need.getMaxPrice())
                .currency(need.getCurrency())
                .quantity(need.getQuantity())
                .photoUrls(need.getPhotoUrls())
                .brand(need.getFashionBrand())
                .color(need.getFashionColor())
                .material(need.getMaterial())
                .targetGender(need.getTargetGender())
                .size(need.getSize())
                .sizeSystem(need.getSizeSystem())
                .fashionType(need.getFashionType())
                .preferredBrands(need.getPreferredBrands())
                .fitPreference(need.getFitPreference())
                .materialPreference(need.getMaterialPreference())
                .styleTags(need.getStyleTags())
                .genderNeutral(need.getGenderNeutral())
                .notifySimilarProducts(need.isNotifySimilarProducts())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(need))
                .businessTags(List.of(need.getType().name(), "fashion", need.isAutoPurchase() ? "auto_purchase" : "manual"))
                .allText(Stream.of(
                        need.getFashionBrand(), need.getFashionColor(), need.getMaterial(),
                        need.getTargetGender(), need.getSize(), need.getSizeSystem(),
                        need.getFashionType(), need.getPreferredBrands(), need.getFitPreference(),
                        need.getMaterialPreference(), need.getStyleTags(), need.getGenderNeutral()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    @Override
    public AbstractUserNeedDocument visit(ElectronicNeed need) {
        return ElectronicNeedDocument.builder()
                .id(need.getId())
                .type(need.getType())
                .userId(need.getUser().getId())
                .name(need.getName())
                .description(need.getDescription())
                .maxPrice(need.getMaxPrice())
                .currency(need.getCurrency())
                .quantity(need.getQuantity())
                .photoUrls(need.getPhotoUrls())
                .brand(need.getElectronicBrand())
                .model(need.getElectronicModel())
                .electronicType(need.getElectronicType())
                .warrantyPeriod(need.getWarrantyPeriod())
                .specifications(need.getSpecifications())
                .minStorageGB(need.getMinStorageGB())
                .minRAMGB(need.getMinRAMGB())
                .preferredOS(need.getPreferredOS())
                .maxAgeYears(need.getMaxAgeYears())
                .warrantyRequired(need.getWarrantyRequired())
                .notifySimilarProducts(need.isNotifySimilarProducts())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(need))
                .businessTags(List.of(need.getType().name(), "electronic", need.isAutoPurchase() ? "auto_purchase" : "manual"))
                .allText(Stream.of(
                        need.getElectronicBrand(), need.getElectronicModel(),
                        need.getElectronicType(), need.getSpecifications(),
                        need.getMinStorageGB(), need.getMinRAMGB(),
                        need.getPreferredOS(), need.getMaxAgeYears(),
                        need.getWarrantyRequired()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    @Override
    public AbstractUserNeedDocument visit(FoodNeed need) {
        return FoodNeedDocument.builder()
                .id(need.getId())
                .type(need.getType())
                .userId(need.getUser().getId())
                .name(need.getName())
                .description(need.getDescription())
                .maxPrice(need.getMaxPrice())
                .currency(need.getCurrency())
                .quantity(need.getQuantity())
                .photoUrls(need.getPhotoUrls())
                .foodCategory(need.getFoodCategory())
                .organic(need.getOrganic())
                .glutenFree(need.getGlutenFree())
                .expiryDate(need.getExpiryDate())
//                .expiryDate(toInstant(need.getExpiryDate()))
                .nutritionalInfo(need.getNutritionalInfo())
                .weight(need.getWeight())
                .dietaryRestrictions(need.getDietaryRestrictions())
                .minShelfLifeDays(need.getMinShelfLifeDays())
                .preferredOrigin(need.getPreferredOrigin())
                .deliveryTemperatureRequired(need.getDeliveryTemperatureRequired())
                .notifySimilarProducts(need.isNotifySimilarProducts())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(need))
                .businessTags(List.of(need.getType().name(), "food", need.isAutoPurchase() ? "auto_purchase" : "manual"))
                .allText(Stream.of(
                        need.getName(),
                        need.getFoodCategory(),
                        need.getNutritionalInfo(),
                        need.getWeight(),
                        need.getDietaryRestrictions(),
                        need.getMinShelfLifeDays(),
                        need.getPreferredOrigin(),
                        need.getDeliveryTemperatureRequired()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    @Override
    public AbstractUserNeedDocument visit(VehicleNeed need) {
        return VehicleNeedDocument.builder()
                .id(need.getId())
                .type(need.getType())
                .userId(need.getUser().getId())
                .name(need.getName())
                .description(need.getDescription())
                .maxPrice(need.getMaxPrice())
                .currency(need.getCurrency())
                .quantity(need.getQuantity())
                .photoUrls(need.getPhotoUrls())
                .vehicleType(need.getVehicleType())
                .make(need.getMake())
                .model(need.getModel())
                .manufacturingYear(need.getManufacturingYear())
                .mileage(need.getMileage())
                .fuelType(need.getFuelType())
                .color(need.getColor())
                .vinNumber(need.getVinNumber())
                .maxMileage(need.getMaxMileage())
                .preferredTransmission(need.getPreferredTransmission())
                .minYear(need.getMinYear())
                .vehicleConditionPreferred(need.getVehicleConditionPreferred())
                .locationRadiusKm(need.getLocationRadiusKm())
                .acceptImported(need.getAcceptImported())
                .notifySimilarProducts(need.isNotifySimilarProducts())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(need))
                .businessTags(List.of(need.getType().name(), "vehicle", need.isAutoPurchase() ? "auto_purchase" : "manual"))
                .allText(Stream.of(
                        need.getMake(), need.getModel(),
                        need.getVehicleType(), need.getFuelType(),
                        need.getColor(), need.getVinNumber(),
                        need.getMaxMileage(), need.getPreferredTransmission(),
                        need.getMinYear(), need.getVehicleConditionPreferred(),
                        need.getLocationRadiusKm(), need.getAcceptImported()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    @Override
    public AbstractUserNeedDocument  visit(RealEstateNeed need) {
        return RealEstateNeedDocument.builder()
                .id(need.getId())
                .type(need.getType())
                .userId(need.getUser().getId())
                .name(need.getName())
                .description(need.getDescription())
                .maxPrice(need.getMaxPrice())
                .currency(need.getCurrency())
                .quantity(need.getQuantity())
                .photoUrls(need.getPhotoUrls())
                .city(need.getCity())
                .address(need.getAddress())
                .surfaceArea(need.getSurfaceArea())
                .roomCount(need.getRoomCount())
                .bathroomCount(need.getBathroomCount())
                .realEstateType(need.getRealEstateType())
                .isForRent(need.getIsForRent())
                .isForSale(need.getIsForSale())
                .constructionYear(need.getConstructionYear())
                .energyClass(need.getEnergyClass())
                .moveInDate(need.getMoveInDate())
                .maxHOAFee(need.getMaxHOAFee())
                .minBedrooms(need.getMinBedrooms())
                .preferredNeighborhoods(need.getPreferredNeighborhoods())
                .schoolDistrict(need.getSchoolDistrict())
                .petFriendly(need.getPetFriendly())
                .notifySimilarProducts(need.isNotifySimilarProducts())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(need))
                .businessTags(List.of(need.getType().name(), "real_estate", need.isAutoPurchase() ? "auto_purchase" : "manual"))
                .allText(Stream.of(
                        need.getName(), need.getDescription(),
                        need.getCity(), need.getAddress(),
                        need.getRealEstateType(), need.getEnergyClass(),
                        need.getMoveInDate(), need.getMaxHOAFee(),
                        need.getMinBedrooms(), need.getPreferredNeighborhoods(),
                        need.getSchoolDistrict(), need.getPetFriendly()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    @Override
    public AbstractUserNeedDocument  visit(ServiceNeed need) {
        return ServiceNeedDocument.builder()
                .id(need.getId())
                .type(need.getType())
                .userId(need.getUser().getId())
                .name(need.getName())
                .description(need.getDescription())
                .maxPrice(need.getMaxPrice())
                .currency(need.getCurrency())
                .quantity(need.getQuantity())
                .photoUrls(need.getPhotoUrls())
                .serviceProvider(need.getServiceProvider())
                .location(need.getLocation())
                .duration(need.getDuration())
                .onlineAvailable(need.getOnlineAvailable())
                .availableAfter(need.getAvailableAfter())
                .availableSlots(need.getAvailableSlots())
                .serviceAreaRadiusKm(need.getServiceAreaRadiusKm())
                .preferredLanguages(need.getPreferredLanguages())
                .minimumProviderRating(need.getMinimumProviderRating())
                .cancellationPolicyPreference(need.getCancellationPolicyPreference())
                .notifySimilarProducts(need.isNotifySimilarProducts())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(need))
                .businessTags(List.of(need.getType().name(), "service", need.isAutoPurchase() ? "auto_purchase" : "manual"))
                .allText(Stream.of(
                        need.getName(), need.getDescription(),
                        need.getServiceProvider(), need.getLocation(),
                        need.getAvailableAfter(), need.getAvailableSlots(),
                        need.getServiceAreaRadiusKm(), need.getPreferredLanguages(),
                        need.getMinimumProviderRating(), need.getCancellationPolicyPreference()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    private double computePriority(AbstractUserNeed need) {
        double priority = 1.0;
        if (need.isAutoPurchase()) {
            priority += 0.5;
        }
        if (need.isNotifySimilarProducts()) {
            priority += 0.2;
        }
        if (need.getMaxPrice() != null) {
            priority += Math.max(0.0, 1.0 - need.getMaxPrice().doubleValue() / 10000.0);
        }
        return Math.round(priority * 100.0) / 100.0;
    }
    
}


