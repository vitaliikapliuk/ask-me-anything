package com.github.vitaliikapliuk.ask.core;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class StatusException extends WebApplicationException {

    public StatusException(final int statusCode, final String message) {
        super(message, Response.status(statusCode).entity(message).type(MediaType.TEXT_PLAIN).build());
    }

}
