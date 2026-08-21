package com.mo.core.visitors.product_visitors;

import org.springframework.stereotype.Component;

import com.mo.core.dtos.InventoryDTO;
import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;
import com.mo.core.visitors.Visitor;

@Visitor("inventory")
@Component
public class InventoryVisitor implements ProductVisitor<InventoryDTO> {
	
	    @Override
	    public InventoryDTO visit(FashionProduct product) {
	        String inventoryCode = product.getBrand().substring(0, 3) + 
	                             "-" + product.getFashionType() + 
	                             "-" + product.getColor().toUpperCase();
	        
	        return new InventoryDTO(
	            product.getId(),
	            inventoryCode,
	            "FASHION-" + product.getSize()
	        );
	    }

       
        @Override
        public InventoryDTO visit(ServiceProduct product) {
            String inventoryCode = product.getServiceProvider().substring(0, 3).toUpperCase() +
                                 "-" + (product.getOnlineAvailable() ? "ONLINE" : "ONSITE") +
                                 "-" + product.getLocation().toUpperCase().substring(0, 3);

            return new InventoryDTO(product.getId(), inventoryCode, "SERVICE");
        }

        @Override
        public InventoryDTO visit(RealEstateProduct product) {
            String inventoryCode = product.getRealEstateType() +
                                 "-" + product.getAddress().toUpperCase().substring(0, 3) +
                                 "-" + product.getSurfaceArea();

            return new InventoryDTO(product.getId(), inventoryCode, "REAL_ESTATE");
        }

        @Override
        public InventoryDTO visit(VehicleProduct product) {
            String inventoryCode = product.getMake().substring(0, 3).toUpperCase() +
                                 "-" + product.getVehicleType() +
                                 "-" + product.getVinNumber().substring(product.getVinNumber().length() - 6);

            return new InventoryDTO(product.getId(), inventoryCode, "VEHICLE");
        }

        @Override
        public InventoryDTO visit(ElectronicProduct product) {
            String inventoryCode = product.getBrand().substring(0, 3).toUpperCase() +
                                 "-" + product.getElectronicType() +
                                 "-" + product.getModel().replaceAll("\\s+", "").toUpperCase();

            return new InventoryDTO(product.getId(), inventoryCode, "ELECTRONIC");
        }

        @Override
        public InventoryDTO visit(FoodProduct product) {
            String inventoryCode = product.getCategory() +
                                 "-" + (product.getOrganic() ? "BIO" : "STD") +
                                 "-" + product.getExpiryDate().toString().replaceAll("-", "");

            return new InventoryDTO(product.getId(), inventoryCode, "FOOD");
        }

        // ... other visit methods ...
}
