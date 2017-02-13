package com.github.vitaliikapliuk.ask.core;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Config {
    private static final String CONF_FILE_NAME = "app.properties";

    private Properties props = new Properties();

    public String serverAddress;
    public int timeFrameMaxIsoCallsPerSecond;

    public String elasticsearchHost;
    public int elasticsearchPort;

    public Config() {
        try {
            File localSettings = new File(CONF_FILE_NAME);
            if (localSettings.exists() && localSettings.canRead()) {
                props.load(new FileInputStream(localSettings));
            } else {
                props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONF_FILE_NAME));
            }
            initProperties();
        } catch (Exception e) {
            throw new IllegalStateException("Can`t load CONFIG from file " + CONF_FILE_NAME, e);
        }
    }

    private void initProperties() {
        serverAddress = getField("server.address");
        timeFrameMaxIsoCallsPerSecond = Integer.parseInt(getField("time_frame.max_iso_calls_per_second"));
        elasticsearchHost = getField("elasticsearch.host");
        elasticsearchPort = Integer.parseInt(getField("elasticsearch.port"));

    }

    private String getField(final String key) {
        final String retval = props.getProperty(key);
        if (retval == null) {
            throw new NullPointerException("Can`t load field from key " + key);
        }
        return retval;
    }

}
