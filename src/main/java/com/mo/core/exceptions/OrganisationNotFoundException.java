package com.mo.core.exceptions;

public class OrganisationNotFoundException extends RuntimeException {
    public OrganisationNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
}
