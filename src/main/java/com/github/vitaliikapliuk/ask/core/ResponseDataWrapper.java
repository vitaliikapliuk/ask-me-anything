package com.github.vitaliikapliuk.ask.core;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class ResponseDataWrapper implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        int status = responseContext.getStatus();
        if (requestContext.getUriInfo().getPath().startsWith("api/") && status >= 200 && status < 300) {
            Object entity = responseContext.getEntity();
            DataResponse<Object> response = new DataResponse<>();
            response.setData(entity);
            responseContext.setEntity(response);
        }
    }

}
