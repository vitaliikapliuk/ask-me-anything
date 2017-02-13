package com.github.vitaliikapliuk.ask.core;

import com.github.vitaliikapliuk.ask.Server;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Database {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Client esClient;

    private Database() {
        try {
            esClient = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(
                            InetAddress.getByName(Server.getConfig().elasticsearchHost),
                            Server.getConfig().elasticsearchPort));

            log.info("Init DB");
        } catch (UnknownHostException e) {
            log.error("Database instantiation exception. ", e);
        }
    }

    public static Client getESClient() {
        return getInstance().esClient;
    }

    private static Database getInstance() {
        return InstanceHolder.database;
    }

    private static class InstanceHolder {
        private static final Database database = new Database();
    }
}
