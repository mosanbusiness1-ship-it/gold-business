package com.mo.core.documents.organisations;

public interface OrganisationStatProjection {
    Long getTotalProducts();
    Long getActiveMembers();
    Long getChildOrganisations();
}

//@Query("SELECT "
//    + "COUNT(p) as totalProducts, "
//    + "COUNT(m) as activeMembers, "
//    + "COUNT(c) as childOrganisations "
//    + "FROM Organisation o "
//    + "LEFT JOIN o.products p "
//    + "LEFT JOIN o.memberships m "
//    + "LEFT JOIN o.children c "
//    + "WHERE o.id = :organisationId")
//OrganisationStatProjection getStats(@Param("organisationId") Long organisationId);
