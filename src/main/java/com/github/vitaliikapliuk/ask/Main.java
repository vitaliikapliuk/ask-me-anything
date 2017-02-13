package com.github.vitaliikapliuk.ask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static Server server;

    public static void main(String[] args) throws Throwable {
        try {
            server = Server.getInstance();
            server.start();
            synchronized (Main.class) {
                Main.class.wait();
            }
        } catch (Throwable t) {
            log.error("Main exception. ", t);
            throw new Throwable("Server shutdown");
        } finally {
            server.shutdown();
        }
    }

}
