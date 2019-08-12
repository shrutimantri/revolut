package com.revolut.transfersvc.jersey.exceptions;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class EntityHasIdentifierException extends WebApplicationException {

    public EntityHasIdentifierException() {
        super("Entity already has identifier", BAD_REQUEST);
    }

}
