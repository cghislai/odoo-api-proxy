package com.charlyghislain.odoo.client.fields;

import lombok.Getter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public class ModelFieldType<T> {

    public static final ModelFieldType<LocalDateTime> DATETIME = new ModelFieldType<>(OdooFieldType.DATETIME, LocalDateTime.class);
    public static final ModelFieldType<Boolean> BOOLEAN = new ModelFieldType<>(OdooFieldType.BOOLEAN, Boolean.class);
    public static final ModelFieldType<MonetaryValue> MONETARY = new ModelFieldType<>(OdooFieldType.MONETARY, MonetaryValue.class);
    public static final ModelFieldType<Integer> INTEGER = new ModelFieldType<>(OdooFieldType.INTEGER, Integer.class);
    public static final ModelFieldType<Enum> SELECTION = new ModelFieldType<>(OdooFieldType.SELECTION, Enum.class);
    public static final ModelFieldType<String> CHAR = new ModelFieldType<>(OdooFieldType.CHAR, String.class);
    public static final ModelFieldType<String> TEXT = new ModelFieldType<>(OdooFieldType.TEXT, String.class);
    public static final ModelFieldType<BigDecimal> FLOAT = new ModelFieldType<>(OdooFieldType.FLOAT, BigDecimal.class);
    public static final ModelFieldType<List> MANY_TO_MANY = new ModelFieldType<>(OdooFieldType.MANY_TO_MANY, List.class);
    public static final ModelFieldType<ForeignKey> MANY_TO_ONE = new ModelFieldType<>(OdooFieldType.MANY_TO_ONE, ForeignKey.class);
    public static final ModelFieldType<List> ONE_TO_MANY = new ModelFieldType<>(OdooFieldType.ONE_TO_MANY, List.class);
    public static final ModelFieldType<byte[]> BINARY = new ModelFieldType<>(OdooFieldType.BINARY, byte[].class);
    public static final ModelFieldType<LocalDate> DATE = new ModelFieldType<>(OdooFieldType.DATE, LocalDate.class);
    public static final ModelFieldType<String> HTML = new ModelFieldType<>(OdooFieldType.HTML, String.class);

    private OdooFieldType odooFieldType;
    private Class<T> javaFieldType;

    public ModelFieldType(OdooFieldType odooFieldType, Class<T> javaFieldType) {
        this.odooFieldType = odooFieldType;
        this.javaFieldType = javaFieldType;
    }

    public static Optional<? extends ModelFieldType<?>> fromOdooFieldType(OdooFieldType fieldType) {
        Class<ModelFieldType> declaringClass = ModelFieldType.class;
        Field[] fields = declaringClass.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(f -> f.getName().equals(fieldType.name()))
                .map(f -> {
                    try {
                        return (ModelFieldType<?>) f.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Cannot access field: " + e);
                    }
                })
                .findAny();
    }


}
