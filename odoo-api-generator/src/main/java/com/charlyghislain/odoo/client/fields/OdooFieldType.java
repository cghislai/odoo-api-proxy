package com.charlyghislain.odoo.client.fields;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

public enum OdooFieldType {
    DATETIME("datetime"),
    BOOLEAN("boolean"),
    MONETARY("monetary"),
    INTEGER("integer"),
    SELECTION("selection"),
    CHAR("char"),
    TEXT("text"),
    FLOAT("float"),
    MANY_TO_MANY("many2many"),
    MANY_TO_ONE("many2one"),
    ONE_TO_MANY("one2many"),
    BINARY("binary"),
    DATE("date"),
    HTML("html"),
    REFERENCE("reference"),
    MANY_TO_ONE_REFERENCE("many2one_reference"),
    ;

    @Getter
    private final String typeName;

    OdooFieldType(String typeName) {
        this.typeName = typeName;
    }

    public static Optional<OdooFieldType> fromTypeName(String typename) {
        return Arrays.stream(OdooFieldType.values())
                .filter(v -> v.getTypeName().equalsIgnoreCase(typename))
                .findAny();
    }
}
