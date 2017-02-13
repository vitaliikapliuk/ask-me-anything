package com.github.vitaliikapliuk.ask.core;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;

public class ExceptionListener implements ApplicationEventListener {

    @Override
    public void onEvent(ApplicationEvent event) {
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return new ExceptionRequestEventListener();
    }

    public static class ExceptionRequestEventListener implements RequestEventListener {

        private final Logger log;

        public ExceptionRequestEventListener() {
            log = LoggerFactory.getLogger(getClass());
        }

        @Override
        public void onEvent(RequestEvent event) {
            try {
                switch (event.getType()) {
                    case ON_EXCEPTION:
                        Throwable t = event.getException();
                        if (t.getCause() != null) {
                            t = t.getCause();
                        }
                        if (t instanceof WebApplicationException) {
                            String path = null;
                            String mediaType = null;
                            String method = null;
                            if (event.getContainerRequest() != null && event.getContainerRequest().getUriInfo() != null) {
                                path = event.getContainerRequest().getUriInfo().getPath();
                                if (event.getContainerRequest().getMediaType() != null) {
                                    mediaType = event.getContainerRequest().getMediaType().toString();
                                }
                                if (event.getContainerRequest().getMethod() != null) {
                                    method = event.getContainerRequest().getMethod();
                                }
                            }
                            WebApplicationException we = (WebApplicationException) t;
                            int statusCode = we.getResponse().getStatus();
                            if (statusCode >= 500) {
                                log.error("[{}][{}][{}][{}] {} ", mediaType, path, method, statusCode, t.fillInStackTrace());
                            } else {
                                log.debug("[{}][{}][{}][{}], {}", mediaType, path, method, statusCode, we.getMessage());
                            }
                        } else {
                            log.error("Non StatusException type [500] ", t);
                        }
                        break;
                    default:
                        break;
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }

    }
}
