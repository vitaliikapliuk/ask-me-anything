package com.github.vitaliikapliuk.ask.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.vitaliikapliuk.ask.utils.JsonUtil.*;

public class IsoPredictor {

    private static final Logger LOG = LoggerFactory.getLogger(IsoPredictor.class);
    private static final String GEO_IP_HOST = "http://freegeoip.net/json/%s";
    private static final String DEFAULT_ISO = "LV";
    private static final Client CLIENT;
    static {
        ClientConfig configuration = new ClientConfig();
        configuration.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        configuration.property(ClientProperties.READ_TIMEOUT, 1000);
        CLIENT = ClientBuilder.newClient(configuration);
        CLIENT.register(new JacksonJsonProvider(json()));
    }

    private static Map<String, String> dummyIpIsoCache = new HashMap<>();

    public static String getIso(String remoteIp) throws IOException {
        String isoCountryCode;
        try {
            Response resp = CLIENT.target(String.format(GEO_IP_HOST, remoteIp)).request().accept(MediaType.APPLICATION_JSON).get();
            final String respString = resp.readEntity(String.class);
            GeoIpResponse geoIpResponse = JsonUtil.json().readValue(respString, GeoIpResponse.class);
            if (resp.getStatus() == 200 && StringUtils.isNotBlank(geoIpResponse.getCountry_code())) {
                isoCountryCode = geoIpResponse.getCountry_code();
            } else {
                isoCountryCode = DEFAULT_ISO;
            }
        } catch (Throwable t) {
            LOG.warn("Something went wrong with request to GeoIp, will be used default country ", t);
            isoCountryCode = DEFAULT_ISO;
        }
        dummyIpIsoCache.putIfAbsent(remoteIp, isoCountryCode);
        return dummyIpIsoCache.get(remoteIp);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GeoIpResponse {
        private String country_code;

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
        }
    }

}
