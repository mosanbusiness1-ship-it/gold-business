package com.mo.core.repositories.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mo.core.enums.OrganisationType;
import com.mo.core.enums.ProductType;
import com.mo.core.model.organisations.Organisation;
import com.mo.core.model.products.AbstractProduct;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long>, JpaSpecificationExecutor<Organisation> {

    // BASIC CRUD OPERATIONS (hérités de JpaRepository)
    
    // FIND METHODS
    Optional<Organisation> findByName(String name);
    
    List<Organisation> findByType(OrganisationType type);
    
    List<Organisation> findByOwnerId(Long ownerId);
    
    List<Organisation> findByParentId(Long parentId);
    
    Optional<Organisation> findByJoinToken(String joinToken);

    Page<Organisation> findByVerifiedTrue(Pageable pageable);

    
    @EntityGraph(attributePaths = {"children", "products"})
    Optional<Organisation> findWithChildrenAndProductsById(Long id);

    // EXISTS METHODS
    boolean existsByName(String name);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);
    
    boolean existsByParentId(Long parentId);

    // COUNT METHODS
    long countByType(OrganisationType type);
    
    @Query("SELECT COUNT(o) FROM Organisation o WHERE o.parent.id = :parentId")
    long countChildrenByParentId(Long parentId);

    // PRODUCT-RELATED QUERIES
    @Query("SELECT p FROM Organisation o JOIN o.products p WHERE o.id = :orgId AND p.price > :minPrice")
    List<AbstractProduct> findProductsByOrgAndMinPrice(Long orgId, BigDecimal minPrice);
    
    @Query("SELECT p FROM Organisation o JOIN o.products p WHERE o.id = :orgId AND p.type = :productType AND p.price > :minPrice")
    List<AbstractProduct> findProductsByOrgAndTypeAndMinPrice(Long orgId,ProductType productType, BigDecimal minPrice);
    
    @Query("SELECT DISTINCT p FROM Organisation o JOIN o.products p WHERE o.id = :orgId AND p.type = :productType")
    List<AbstractProduct> findProductsByOrgAndType(Long orgId, ProductType productType);
    
    @Query("SELECT o FROM Organisation o JOIN o.products p WHERE p.id = :productId")
    List<Organisation> findByProductId(Long productId);

    // HIERARCHY QUERIES
    @Query(value = "WITH RECURSIVE org_hierarchy AS ("
            + "SELECT * FROM organisation WHERE id = :orgId "
            + "UNION ALL "
            + "SELECT o.* FROM organisation o JOIN org_hierarchy oh ON o.parent_id = oh.id"
            + ") SELECT * FROM org_hierarchy", nativeQuery = true)
    List<Organisation> findFullHierarchy(Long orgId);
    
    
    @Query("SELECT o FROM Organisation o WHERE o.parent IS NULL")
    List<Organisation> findRootOrganisations();

    // BULK OPERATIONS
    @Modifying
    @Query("UPDATE Organisation o SET o.owner.id = :newOwnerId WHERE o.owner.id = :oldOwnerId")
    int transferOwnership(Long oldOwnerId, Long newOwnerId);
    
    @Modifying
    @Query("DELETE FROM Organisation o WHERE o.id IN :ids")
    int deleteByIds(List<Long> ids);

    @Query("SELECT DISTINCT o FROM Organisation o " +
       "LEFT JOIN FETCH o.children " +
       "LEFT JOIN FETCH o.products")
    List<Organisation> findAllWithChildrenAndProducts();


    // ADVANCED FILTERING
    @Query("SELECT o FROM Organisation o WHERE "
            + "(:name IS NULL OR o.name LIKE %:name%) AND "
            + "(:type IS NULL OR o.type = :type) AND "
            + "(:minProductCount IS NULL OR (SELECT COUNT(p) FROM o.products p) >= :minProductCount)")
    List<Organisation> searchOrganisations(
            @Param("name") String name,
            @Param("type") OrganisationType type,
            @Param("minProductCount") Long minProductCount);
}



