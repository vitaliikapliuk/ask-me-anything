package com.github.vitaliikapliuk.ask.utils;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class SmartClone extends BeanUtilsBean {

    private SmartClone() {}

    @Override
    public void copyProperty(Object bean, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if (value == null) {
            return;
        }
        super.copyProperty(bean, name, value);
    }

    public static void copy(Object dest, Object orig)
            throws InvocationTargetException, IllegalAccessException {
        InstanceHolder.instance.copyProperties(dest, orig);
    }

    private static class InstanceHolder {
        private static final SmartClone instance = new SmartClone();
    }
}
