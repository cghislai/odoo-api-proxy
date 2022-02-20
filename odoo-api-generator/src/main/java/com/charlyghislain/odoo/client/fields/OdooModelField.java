package com.charlyghislain.odoo.client.fields;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OdooModelField<T> {

    private String fieldName;
    private ModelFieldType<T> fieldType;
    private boolean required;
    private boolean readOnly;
    private String fkModelName;
    private String help;

}
