package com.revolut.transfersvc.jersey.exceptions;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class EntityNotFoundException extends WebApplicationException {

    public EntityNotFoundException() {
        super("No entity found", NOT_FOUND);
    }
}
