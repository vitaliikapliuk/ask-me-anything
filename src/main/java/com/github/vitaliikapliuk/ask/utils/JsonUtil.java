package com.github.vitaliikapliuk.ask.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    private final ObjectMapper json;

    public JsonUtil() {
        json = new ObjectMapper();
    }

    public static ObjectMapper json() {
        return getInstance().json;
    }

    private static JsonUtil getInstance() {
        return InstanceHolder.database;
    }

    private static class InstanceHolder {
        static JsonUtil database = new JsonUtil();

    }

}
