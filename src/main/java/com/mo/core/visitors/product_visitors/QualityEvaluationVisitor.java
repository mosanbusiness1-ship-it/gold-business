package com.mo.core.visitors.product_visitors;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.mo.core.model.products.ElectronicProduct;
import com.mo.core.model.products.FashionProduct;
import com.mo.core.model.products.FoodProduct;
import com.mo.core.model.products.RealEstateProduct;
import com.mo.core.model.products.ServiceProduct;
import com.mo.core.model.products.VehicleProduct;
import com.mo.core.visitors.Visitor;

@Visitor("qualityEvaluationVisitor")
@Component
public class QualityEvaluationVisitor implements ProductVisitor<Double> {

	@Override
	public Double visit(ServiceProduct product) {
	    double score = 0.0;

	    // 1. Disponibilité en ligne → +0.2
	    if (Boolean.TRUE.equals(product.getOnlineAvailable())) {
	        score += 0.2;
	    }

	    // 2. Nombre de créneaux disponibles (max 5 créneaux pris en compte) → jusqu’à +0.3
	    int slots = product.getAvailableSlots() != null ? product.getAvailableSlots().size() : 0;
	    score += Math.min(0.3, slots * 0.05); // chaque slot ajoute +0.05

	    // 3. Date de disponibilité (plus tôt = mieux) → jusqu’à +0.2
	    if (product.getAvailableAfter() != null) {
	        long days = Duration.between(LocalDateTime.now(), product.getAvailableAfter()).toDays();
	        if (days <= 0) {
	            score += 0.2; // déjà dispo
	        } else if (days <= 7) {
	            score += 0.15;
	        } else if (days <= 30) {
	            score += 0.1;
	        }
	    }

	    // 4. Durée du service (plus court est mieux) → jusqu’à +0.2
	    if (product.getDuration() != null) {
	        long duration = product.getDuration();
	        if (duration <= 1) {
	            score += 0.2;
	        } else if (duration <= 3) {
	            score += 0.15;
	        } else if (duration <= 7) {
	            score += 0.1;
	        }
	    }

	    // 5. Localisation renseignée → petit bonus +0.1
	    if (product.getLocation() != null && !product.getLocation().isBlank()) {
	        score += 0.1;
	    }

	    // Clamp final à [0,1]
	    return Math.min(1.0, score);
	}


	@Override
	public Double visit(RealEstateProduct product) {
	    double score = 0.0;

	    // 1. Surface (score max 0.3, idéale entre 50 et 200 m²)
	    if (product.getSurfaceArea() != null) {
	        double surface = product.getSurfaceArea();
	        if (surface >= 50 && surface <= 200) {
	            score += 0.3;
	        } else if (surface > 200 && surface <= 300) {
	            score += 0.2;
	        } else if (surface >= 30 && surface < 50) {
	            score += 0.1;
	        }
	    }

	    // 2. Nombre de pièces (score max 0.2)
	    int roomCount = product.getRoomCount() != null ? product.getRoomCount() : 0;
	    if (roomCount >= 3 && roomCount <= 6) {
	        score += 0.2;
	    } else if (roomCount == 2 || roomCount == 7) {
	        score += 0.1;
	    }

	    // 3. Ville (bonus si renseignée) → +0.1
	    if (product.getCity() != null && !product.getCity().isBlank()) {
	        score += 0.1;
	    }

	    // 4. Type de transaction correspondante (vente ou location) → +0.1
	    if (Boolean.TRUE.equals(product.getIsForRent()) || Boolean.TRUE.equals(product.getIsForSale())) {
	        score += 0.1;
	    }

	    // 5. Année de construction récente (score max 0.2)
	    if (product.getConstructionYear() != null) {
	        int age = java.time.Year.now().getValue() - product.getConstructionYear();
	        if (age <= 5) {
	            score += 0.2;
	        } else if (age <= 15) {
	            score += 0.1;
	        }
	    }

	    // 6. Classe énergétique (bonus si A ou B) → +0.1
	    if ("A".equalsIgnoreCase(product.getEnergyClass()) || "B".equalsIgnoreCase(product.getEnergyClass())) {
	        score += 0.1;
	    }

	    // Clamp final à [0,1]
	    return Math.min(1.0, score);
	}


	@Override
	public Double visit(VehicleProduct product) {
	    double score = 0.0;

	    // 1. Année de fabrication (score max 0.4)
	    if (product.getManufacturingYear() != null) {
	        int age = java.time.Year.now().getValue() - product.getManufacturingYear();
	        if (age <= 1) {
	            score += 0.4;
	        } else if (age <= 5) {
	            score += 0.3;
	        } else if (age <= 10) {
	            score += 0.2;
	        } else if (age <= 15) {
	            score += 0.1;
	        }
	    }

	    // 2. Kilométrage (score max 0.3)
	    if (product.getMileage() != null) {
	        double mileage = product.getMileage();
	        if (mileage <= 20000) {
	            score += 0.3;
	        } else if (mileage <= 50000) {
	            score += 0.2;
	        } else if (mileage <= 100000) {
	            score += 0.1;
	        }
	    }

	    // 3. Marque et modèle renseignés → +0.1
	    if (product.getMake() != null && !product.getMake().isBlank() &&
	        product.getModel() != null && !product.getModel().isBlank()) {
	        score += 0.1;
	    }

	    // 4. Type de carburant renseigné → +0.1
	    if (product.getFuelType() != null && !product.getFuelType().isBlank()) {
	        score += 0.1;
	    }

	    // 5. Couleur renseignée → +0.1
	    if (product.getColor() != null && !product.getColor().isBlank()) {
	        score += 0.1;
	    }

	    // Clamp final à [0,1]
	    return Math.min(1.0, score);
	}


	@Override
	public Double visit(ElectronicProduct product) {
	    double score = 0.0;

	    // 1. Marque renseignée → +0.2
	    if (product.getBrand() != null && !product.getBrand().isBlank()) {
	        score += 0.2;
	    }

	    // 2. Modèle renseigné → +0.2
	    if (product.getModel() != null && !product.getModel().isBlank()) {
	        score += 0.2;
	    }

	    // 3. Spécifications renseignées → +0.3
	    if (product.getSpecifications() != null && !product.getSpecifications().isBlank()) {
	        score += 0.3;
	    }

	    // 4. Garantie → +0.5 si renseignée et valide
	    if (product.getWarrantyPeriod() != null && !product.getWarrantyPeriod().isBlank()) {
	        score += 0.5;
	    }

	    // Clamp final à [0,1]
	    return Math.min(1.0, score);
	}


	@Override
	public Double visit(FoodProduct product) {
	    double score = 0.0;

	    // 1. Date de péremption (score max 0.4)
	    if (product.getExpiryDate() != null) {
	        long daysToExpiry = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), product.getExpiryDate());
	        if (daysToExpiry >= 30) {
	            score += 0.4;
	        } else if (daysToExpiry >= 14) {
	            score += 0.3;
	        } else if (daysToExpiry >= 7) {
	            score += 0.2;
	        } else if (daysToExpiry >= 1) {
	            score += 0.1;
	        }
	    }

	    // 2. Organic → +0.2
	    if (Boolean.TRUE.equals(product.getOrganic())) {
	        score += 0.2;
	    }

	    // 3. Gluten-free → +0.1
	    if (Boolean.TRUE.equals(product.getGlutenFree())) {
	        score += 0.1;
	    }

	    // 4. Poids (score max 0.1, poids > 0) 
	    if (product.getWeight() != null && product.getWeight() > 0) {
	        score += 0.1;
	    }

	    // 5. Nutritional info renseignée → +0.2
	    if (product.getNutritionalInfo() != null && !product.getNutritionalInfo().isBlank()) {
	        score += 0.2;
	    }

	    // Clamp final à [0,1]
	    return Math.min(1.0, score);
	}


	@Override
	public Double visit(FashionProduct product) {
	    double score = 0.0;

	    // 1. Marque renseignée → +0.3
	    if (product.getBrand() != null && !product.getBrand().isBlank()) {
	        score += 0.3;
	    }

	    // 2. Type de produit et taille renseignés → +0.2
	    if (product.getFashionType() != null && product.getSize() != null && !product.getSize().isBlank()) {
	        score += 0.2;
	    }

	    // 3. Couleur renseignée → +0.1
	    if (product.getColor() != null && !product.getColor().isBlank()) {
	        score += 0.1;
	    }

	    // 4. Matériau premium → +0.3
	    if (product.getMaterial() != null && !product.getMaterial().isBlank()) {
	        score += 0.3;
	    }

	    // 5. Genre ciblé renseigné → +0.1
	    if (product.getTargetGender() != null && !product.getTargetGender().isBlank()) {
	        score += 0.1;
	    }

	    // Clamp final à [0,1]
	    return Math.min(1.0, score);
	}

}

