package com.mo.core.visitors.need_visitors;

import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.elasticsearch.core.query.Criteria;

import com.mo.core.model.needs.*;
import com.mo.core.visitors.Visitor;

@Component
@Visitor("matchingVisitorForNeeds")
public class UserNeedMatchingVisitor implements UserNeedVisitor<Criteria> {

    @Override
    public Criteria visit(VehicleNeed need) {
        Criteria criteria = new Criteria("type").is("VEHICLE");

        if (need.getMake() != null)
            criteria = criteria.and("make").is(need.getMake());

        if (need.getModel() != null)
            criteria = criteria.and("model").is(need.getModel());

        if (need.getManufacturingYear() != null)
            criteria = criteria.and("manufacturing_year").greaterThanEqual(need.getManufacturingYear());

        if (need.getVehicleType() != null)
            criteria = criteria.and("vehicle_type").is(need.getVehicleType().name());

        if (need.getFuelType() != null)
            criteria = criteria.and("fuel_type").is(need.getFuelType());

        if (need.getColor() != null)
            criteria = criteria.and("color").is(need.getColor());

        if (need.getMileage() != null)
            criteria = criteria.and("mileage").greaterThanEqual(need.getMileage());

        if (need.getMaxPrice() != null)
            criteria = criteria.and("price").lessThanEqual(need.getMaxPrice());

        return criteria;
    }
    

    @Override
    public Criteria visit(ElectronicNeed need) {
        Criteria criteria = new Criteria("type").is("ELECTRONIC");

        if (need.getElectronicBrand() != null)
            criteria = criteria.and("brand").is(need.getElectronicBrand());

        if (need.getElectronicModel() != null)
            criteria = criteria.and("model").is(need.getElectronicModel());

        if (need.getElectronicType() != null)
            criteria = criteria.and("electronic_type").is(need.getElectronicType().name());

        if (need.getWarrantyPeriod() != null)
            criteria = criteria.and("warranty_period").is(need.getWarrantyPeriod());

        if (need.getSpecifications() != null)
            criteria = criteria.and("specifications").is(need.getSpecifications());

        if (need.getMaxPrice() != null)
            criteria = criteria.and("price").lessThanEqual(need.getMaxPrice());

        return criteria;
    }

    @Override
    public Criteria visit(FashionNeed need) {
        Criteria criteria = new Criteria("type").is("FASHION");

        if (need.getFashionBrand() != null)
            criteria = criteria.and("brand").is(need.getFashionBrand());

        if (need.getSize() != null)
            criteria = criteria.and("size").is(need.getSize());

        if (need.getFashionColor() != null)
            criteria = criteria.and("color").is(need.getFashionColor());

        if (need.getMaterial() != null)
            criteria = criteria.and("material").is(need.getMaterial());

        if (need.getTargetGender() != null)
            criteria = criteria.and("target_gender").is(need.getTargetGender());

        if (need.getSizeSystem() != null)
            criteria = criteria.and("size_system").is(need.getSizeSystem());

        if (need.getFashionType() != null)
            criteria = criteria.and("fashion_type").is(need.getFashionType());

        if (need.getMaxPrice() != null)
            criteria = criteria.and("price").lessThanEqual(need.getMaxPrice());

        return criteria;
    }
    

    @Override
    public Criteria visit(FoodNeed need) {
        Criteria criteria = new Criteria("type").is("FOOD");

        if (need.getFoodCategory() != null)
            criteria = criteria.and("category").is(need.getFoodCategory().toString());

        if (need.getOrganic() != null)
            criteria = criteria.and("organic").is(need.getOrganic());

        if (need.getGlutenFree() != null)
            criteria = criteria.and("gluten_free").is(need.getGlutenFree());

        if (need.getExpiryDate() != null)
            criteria = criteria.and("expiry_date").greaterThanEqual(need.getExpiryDate());

        if (need.getNutritionalInfo() != null)
            criteria = criteria.and("nutritional_info").is(need.getNutritionalInfo());

        if (need.getWeight() != null)
            criteria = criteria.and("weight").greaterThanEqual(need.getWeight());

        if (need.getMaxPrice() != null)
            criteria = criteria.and("price").lessThanEqual(need.getMaxPrice());

        return criteria;
    }


    @Override
    public Criteria visit(RealEstateNeed need) {
        Criteria criteria = new Criteria("type").is("REALESTATE");

        if (need.getRealEstateType() != null)
            criteria = criteria.and("real_estate_type").is(need.getRealEstateType().name());

        if (need.getCity() != null)
            criteria = criteria.and("city").is(need.getCity());

        if (need.getAddress() != null)
            criteria = criteria.and("address").is(need.getAddress());

        if (need.getSurfaceArea() != null)
            criteria = criteria.and("surface_area").greaterThanEqual(need.getSurfaceArea());

        if (need.getRoomCount() != null)
            criteria = criteria.and("room_count").greaterThanEqual(need.getRoomCount());

        if (need.getBathroomCount() != null)
            criteria = criteria.and("bathroom_count").greaterThanEqual(need.getBathroomCount());

        if (need.getIsForRent() != null)
            criteria = criteria.and("is_for_rent").is(need.getIsForRent());

        if (need.getIsForSale() != null)
            criteria = criteria.and("is_for_sale").is(need.getIsForSale());

        if (need.getConstructionYear() != null)
            criteria = criteria.and("construction_year").is(need.getConstructionYear());

        if (need.getEnergyClass() != null)
            criteria = criteria.and("energy_class").is(need.getEnergyClass());

        if (need.getMaxPrice() != null)
            criteria = criteria.and("price").lessThanEqual(need.getMaxPrice());

        return criteria;
    }

    @Override
    public Criteria visit(ServiceNeed need) {
        Criteria criteria = new Criteria("type").is("SERVICE");

        if (need.getServiceProvider() != null)
            criteria = criteria.and("service_provider").is(need.getServiceProvider());

        if (need.getLocation() != null)
            criteria = criteria.and("location").is(need.getLocation());

        if (need.getOnlineAvailable() != null)
            criteria = criteria.and("online_available").is(need.getOnlineAvailable());

        if (need.getDuration() != null)
            criteria = criteria.and("duration").lessThanEqual(need.getDuration());

        if (need.getAvailableSlots() != null && !need.getAvailableSlots().isEmpty()) {
            // Recherche si un créneau disponible est >= au premier slot souhaité
//        	criteria = criteria.and("available_slots").in(need.getAvailableSlotsAsInstants().get(0));
        	criteria = criteria.and("available_slots").in(need.getAvailableSlots().get(0));
        }

        if (need.getMaxPrice() != null)
            criteria = criteria.and("price").lessThanEqual(need.getMaxPrice());
        
        if (need.getName() != null || need.getDescription() != null) {
            String keywords = Stream.of(need.getName(), need.getDescription())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" "));
            if (!keywords.isBlank()) {
                criteria = criteria.and("all_text").matches(keywords);
            }
        }

        return criteria;
    }
}




