package com.mo.core.visitors.product_visitors;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.mo.core.documents.products.AbstractProductDocument;
import com.mo.core.documents.products.ElectronicProductDocument;
import com.mo.core.documents.products.FashionProductDocument;
import com.mo.core.documents.products.FoodProductDocument;
import com.mo.core.documents.products.RealEstateProductDocument;
import com.mo.core.documents.products.ServiceProductDocument;
import com.mo.core.documents.products.VehicleProductDocument;
import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;
import com.mo.core.visitors.Visitor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Visitor("productIndexerVisitor")
public class ProductIndexerVisitor implements ProductVisitor<AbstractProductDocument> {

    @org.springframework.beans.factory.annotation.Qualifier("qualityEvaluationVisitor")
    @org.springframework.beans.factory.annotation.Autowired
    private com.mo.core.visitors.product_visitors.ProductVisitor<Double> qualityEvaluationVisitor;

    @Override
    public AbstractProductDocument visit(FashionProduct product) {
        return FashionProductDocument.builder()
                .id(product.getId())
                .type(product.getType())
                .ownerId(product.getOwner().getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .photoUrls(product.getPhotoUrls())
                .createdAt(toInstant(product.getCreatedAt()))
                .updatedAt(toInstant(product.getUpdatedAt()))
                .brand(product.getBrand())
                .color(product.getColor())
                .material(product.getMaterial())
                .targetGender(product.getTargetGender())
                .size(product.getSize())
                .sizeSystem(product.getSizeSystem())
                .fashionType(product.getFashionType())
                .condition(product.getCondition())
                .sustainableCertifications(product.getSustainableCertifications())
                .sizeFit(product.getSizeFit())
                .materialOrigin(product.getMaterialOrigin())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(product))
                .qualityScore(product.accept(qualityEvaluationVisitor))
                .businessTags(List.of(product.getType().name(), "fashion", product.isPlatformOwner() ? "platform_owner" : "external"))
                .allText(Stream.of(
                        product.getBrand(), product.getColor(), product.getMaterial(),
                        product.getTargetGender(), product.getSize(), product.getSizeSystem(),
                        product.getFashionType(), product.getCondition(), product.getSizeFit(),
                        product.getMaterialOrigin(), product.getSustainableCertifications()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    @Override
    public AbstractProductDocument visit(ElectronicProduct product) {
        return ElectronicProductDocument.builder()
                .id(product.getId())
                .type(product.getType())
                .ownerId(product.getOwner().getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .photoUrls(product.getPhotoUrls())
                .createdAt(toInstant(product.getCreatedAt()))
                .updatedAt(toInstant(product.getUpdatedAt()))
                .brand(product.getBrand())
                .model(product.getModel())
                .electronicType(product.getElectronicType())
                .warrantyPeriod(product.getWarrantyPeriod())
                .specifications(product.getSpecifications())
                .releaseYear(product.getReleaseYear())
                .batteryHealthPercent(product.getBatteryHealthPercent())
                .accessoriesIncluded(product.getAccessoriesIncluded())
                .supportedNetworks(product.getSupportedNetworks())
                .warrantyMonths(product.getWarrantyMonths())
                .sellerRating(product.getSellerRating())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(product))
                .qualityScore(product.accept(qualityEvaluationVisitor))
                .businessTags(List.of(product.getType().name(), "electronic", product.isPlatformOwner() ? "platform_owner" : "external"))
                .allText(Stream.of(
                        product.getBrand(), product.getModel(),
                        product.getElectronicType(), product.getSpecifications(),
                        product.getReleaseYear(), product.getBatteryHealthPercent(),
                        product.getAccessoriesIncluded(), product.getSupportedNetworks(),
                        product.getWarrantyMonths(), product.getSellerRating()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    @Override
    public AbstractProductDocument visit(FoodProduct product) {
        return FoodProductDocument.builder()
                .id(product.getId())
                .type(product.getType())
                .ownerId(product.getOwner().getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .photoUrls(product.getPhotoUrls())
                .createdAt(toInstant(product.getCreatedAt()))
                .updatedAt(toInstant(product.getUpdatedAt()))
                .category(product.getCategory())
                .organic(product.getOrganic())
                .glutenFree(product.getGlutenFree())
                .expiryDate(product.getExpiryDate())
                .nutritionalInfo(product.getNutritionalInfo())
                .weight(product.getWeight())
                .originCountry(product.getOriginCountry())
                .organicCertId(product.getOrganicCertId())
                .allergenTags(product.getAllergenTags())
                .packagingType(product.getPackagingType())
                .shelfLifeDays(product.getShelfLifeDays())
                .storageTemperature(product.getStorageTemperature())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(product))
                .qualityScore(product.accept(qualityEvaluationVisitor))
                .businessTags(List.of(product.getType().name(), "food", product.isPlatformOwner() ? "platform_owner" : "external"))
                .allText(Stream.of(
                        product.getName(),
                        product.getCategory(),
                        product.getNutritionalInfo(),
                        product.getWeight(),
                        product.getOriginCountry(),
                        product.getOrganicCertId(),
                        product.getAllergenTags(),
                        product.getPackagingType(),
                        product.getShelfLifeDays(),
                        product.getStorageTemperature()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    @Override
    public AbstractProductDocument visit(VehicleProduct product) {
        return VehicleProductDocument.builder()
                .id(product.getId())
                .type(product.getType())
                .ownerId(product.getOwner().getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .photoUrls(product.getPhotoUrls())
                .createdAt(toInstant(product.getCreatedAt()))
                .updatedAt(toInstant(product.getUpdatedAt()))
                .vehicleType(product.getVehicleType())
                .make(product.getMake())
                .model(product.getModel())
                .manufacturingYear(product.getManufacturingYear())
                .mileage(product.getMileage())
                .fuelType(product.getFuelType())
                .color(product.getColor())
                .vinNumber(product.getVinNumber())
                .transmission(product.getTransmission())
                .trim(product.getTrim())
                .fuelConsumptionLPer100km(product.getFuelConsumptionLPer100km())
                .doors(product.getDoors())
                .vehicleCondition(product.getVehicleCondition())
                .warrantyMonths(product.getWarrantyMonths())
                .sellerRating(product.getSellerRating())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(product))
                .qualityScore(product.accept(qualityEvaluationVisitor))
                .businessTags(List.of(product.getType().name(), "vehicle", product.isPlatformOwner() ? "platform_owner" : "external"))
                .allText(Stream.of(
                        product.getMake(), product.getModel(),
                        product.getVehicleType(), product.getFuelType(),
                        product.getColor(), product.getVinNumber(),
                        product.getTransmission(), product.getTrim(),
                        product.getFuelConsumptionLPer100km(), product.getDoors(),
                        product.getVehicleCondition(), product.getWarrantyMonths(),
                        product.getSellerRating()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    @Override
    public AbstractProductDocument visit(RealEstateProduct product) {
        return RealEstateProductDocument.builder()
                .id(product.getId())
                .type(product.getType())
                .ownerId(product.getOwner().getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .photoUrls(product.getPhotoUrls())
                .createdAt(toInstant(product.getCreatedAt()))
                .updatedAt(toInstant(product.getUpdatedAt()))
                .city(product.getCity())
                .address(product.getAddress())
                .surfaceArea(product.getSurfaceArea())
                .roomCount(product.getRoomCount())
                .bathroomCount(product.getBathroomCount())
                .realEstateType(product.getRealEstateType())
                .isForRent(product.getIsForRent())
                .isForSale(product.getIsForSale())
                .constructionYear(product.getConstructionYear())
                .energyClass(product.getEnergyClass())
                .floor(product.getFloor())
                .balcony(product.getBalcony())
                .furnished(product.getFurnished())
                .hoaFees(product.getHoaFees())
                .parking(product.getParking())
                .energyRatingNumeric(product.getEnergyRatingNumeric())
                .neighborhoodTags(product.getNeighborhoodTags())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(product))
                .qualityScore(product.accept(qualityEvaluationVisitor))
                .businessTags(List.of(product.getType().name(), "real_estate", product.isPlatformOwner() ? "platform_owner" : "external"))
                .allText(Stream.of(
                        product.getName(), product.getDescription(),
                        product.getCity(), product.getAddress(),
                        product.getRealEstateType(), product.getEnergyClass(),
                        product.getFloor(), product.getBalcony(),
                        product.getFurnished(), product.getHoaFees(),
                        product.getParking(), product.getEnergyRatingNumeric(),
                        product.getNeighborhoodTags()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }

    @Override
    public AbstractProductDocument visit(ServiceProduct product) {
        return ServiceProductDocument.builder()
                .id(product.getId())
                .type(product.getType())
                .ownerId(product.getOwner().getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .photoUrls(product.getPhotoUrls())
                .createdAt(toInstant(product.getCreatedAt()))
                .updatedAt(toInstant(product.getUpdatedAt()))
                .serviceProvider(product.getServiceProvider())
                .location(product.getLocation())
                .duration(product.getDuration())
                .onlineAvailable(product.getOnlineAvailable())
                .availableAfter(product.getAvailableAfter())
                .availableSlots(product.getAvailableSlots())
                .serviceAreaRadiusKm(product.getServiceAreaRadiusKm())
                .cancellationPolicy(product.getCancellationPolicy())
                .languagesSpoken(product.getLanguagesSpoken())
                .providerRating(product.getProviderRating())
//                .availableSlots(product.getAvailableSlotsAsInstants())
                .indexedAt(Instant.now())
                .indexStatus("indexed")
                .businessPriority(computePriority(product))
                .qualityScore(product.accept(qualityEvaluationVisitor))
                .businessTags(List.of(product.getType().name(), "service", product.isPlatformOwner() ? "platform_owner" : "external"))
                .allText(Stream.of(
                        product.getName(), product.getDescription(),
                        product.getServiceProvider(), product.getLocation(),
                        product.getServiceAreaRadiusKm(), product.getCancellationPolicy(),
                        product.getLanguagesSpoken(), product.getProviderRating()
                ).filter(Objects::nonNull)
                 .map(Object::toString)
                 .collect(Collectors.joining(" ")))
                .build();
    }
    
    private Instant toInstant(LocalDateTime ldt) {
        return ldt == null ? null : ldt.atZone(ZoneId.systemDefault()).toInstant();
    }

    private double computePriority(com.mo.core.model.products.AbstractProduct product) {
        double priority = 1.0;
        if (product.isPlatformOwner()) {
            priority += 0.5;
        }
        if (!product.isEnabled()) {
            priority -= 0.5;
        }
        if (product.getPrice() != null) {
            priority += Math.max(0.0, 1.0 - product.getPrice().doubleValue() / 10000.0);
        }
        return Math.round(priority * 100.0) / 100.0;
    }

}
