package com.charlyghislain.odoo.client;

import com.charlyghislain.odoo.client.fields.ForeignKey;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class OdooParserUtils {

    public static String parseString(Object value, String desc) {
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new OdooRuntimeError("Expected a string, got " + value.getClass() + " (" + desc + ")");
        }
    }

    public static int parseInt(Object value, String desc) {
        if (value instanceof Integer) {
            return (int) value;
        } else {
            throw new OdooRuntimeError("Expected an integer, got " + value.getClass() + " (" + desc + ")");
        }
    }

    public static <T> ForeignKey<T> parseForeignKey(Object value, String desc) {
        if (value instanceof Integer) {
            return new ForeignKey<>((int) value);
        } else {
            throw new OdooRuntimeError("Expected an integer, got " + value.getClass() + " (" + desc + ")");
        }
    }

    public static boolean parseBoolean(Object value, String desc) {
        if (value instanceof Boolean) {
            return (Boolean) value;
            // Sometimes it is integers
        } else if (value instanceof Integer) {
            int intValue = (int) value;
            return intValue > 0;
            // and sometimes strings
        } else if (value instanceof String) {
            String stringValue = (String) value;
            return stringValue.equalsIgnoreCase("true");
        } else {
            throw new OdooRuntimeError("Expected a boolean, got " + value.getClass() + " (" + desc + ")");
        }
    }

    public static Object[] parseArray(Object value, String desc) {
        Object[] testArray = new Object[0];
        if (testArray.getClass().isAssignableFrom(value.getClass())) {
            return (Object[]) value;
        } else {
            throw new OdooRuntimeError("Expected an array, got " + value.getClass() + " (" + desc + ")");
        }
    }

    public static Optional<Object> parseSingleElementArray(Object value, String desc) {
        Object[] testArray = new Object[0];
        if (testArray.getClass().isAssignableFrom(value.getClass())) {
            Object[] array = (Object[]) value;
            if (array.length == 0) {
                return Optional.empty();
            } else if (array.length == 1) {
                return Optional.of(array[0]);
            } else {
                throw new OdooRuntimeError("Array has multiple elements: " + desc);
            }
        } else {
            throw new OdooRuntimeError("Expected an array, got " + value.getClass() + " (" + desc + ")");
        }
    }

    public static Map<Object, Object> parseMap(Object value, String desc) {
        if (Map.class.isAssignableFrom(value.getClass())) {
            return (Map<Object, Object>) value;
        } else {
            throw new OdooRuntimeError("Expected a map, got " + value.getClass() + " (" + desc + ")");
        }
    }

    public static LocalDateTime parseLocalDateTime(Object value, String desc) {
        String stringValue = parseString(value, "datetime field :" + desc);
        LocalDateTime dateTime = LocalDateTime.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return dateTime;
    }
}
