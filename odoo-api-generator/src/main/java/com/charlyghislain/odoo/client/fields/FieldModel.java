package com.charlyghislain.odoo.client.fields;

import com.charlyghislain.odoo.client.OdooParserUtils;
import com.charlyghislain.odoo.client.OdooRuntimeError;
import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class FieldModel {
    private boolean changeDefault;
    private boolean readonly;
    private String label;
    private List<String> depends;
    private boolean companyDependant;
    private String name;
    private String groups;
    private boolean sortable;
    private boolean store;
    private OdooFieldType type;
    private boolean manual;
    private boolean required;
    private boolean searchable;
    private boolean translate;
    private boolean trim;
    private boolean attachment;
    private String domain;
    private Map<String, Object> context;
    private String help;
    // TODO enum type. values 'sum'
    private String groupOperator;
    private String curencyField;
    private Integer size;

    // Nullable: for relational fields
    private String relation;
    // Nullable: for relational fields oneToMany
    private String relationField;

    private List<String> selectionValues;

    public static FieldModel parseValueMap(Map<Object, Object> valueMap) {
        FieldModel fieldModel = new FieldModel();
        valueMap.entrySet()
                .forEach(e -> parseMapEntry(e, fieldModel));
        return fieldModel;
    }

    private static void parseMapEntry(Map.Entry<Object, Object> entry, FieldModel fieldModel) {
        Object key = entry.getKey();
        Object value = entry.getValue();
        String keyString = OdooParserUtils.parseString(key, "field model map key");
        switch (keyString) {
            case "domain":
            case "related":
            case "digits":
            case "exportable": // bool
            case "deprecated": // bool
            case "selectable": // bool
            case "strip_classes": //bool
            case "strip_style"://bool
            case "sanitize_attributes"://bool
            case "sanitize_style"://bool
            case "sanitize"://bool
            case "sanitize_tags"://bool
            case "states":// {paused=[Ljava.lang.Object;@1e0b4072, draft=[Ljava.lang.Object;@791f145a, open=[Ljava.lang.Object;@38cee291}
            case "reference": //
            case "many2one_reference": //
            case "context": {
                // TODO
                break;
            }
            case "selection": {
                Object[] valueArray = OdooParserUtils.parseArray(value, "field model 'selection'");
                // each value is an array of
                // 0 -> int or string
                // 1 -> label
                List<String> selectionValues = Arrays.stream(valueArray)
                        .map(i -> {
                            Object[] itemArray = OdooParserUtils.parseArray(i, "field model 'selection' value");
                            if (itemArray.length != 2) {
                                throw new OdooRuntimeError("Unable to parse selection items: " + itemArray.length + " entries while expecting 2");
                            }
                            Object firstItem = itemArray[0];
                            if (firstItem instanceof String) {
                                return (String) firstItem;
                            } else if (firstItem instanceof Integer) {
                                return "" + firstItem;
                            } else {
                                throw new OdooRuntimeError("Unable to parse selection items: unexpected value " + firstItem);
                            }
                        })
                        .collect(Collectors.toList());
                fieldModel.setSelectionValues(selectionValues);
                break;
            }

            case "string": {
                String stringValue = OdooParserUtils.parseString(value, "field model 'string'");
                fieldModel.setLabel(stringValue);
                break;
            }
            case "depends": {
                Object[] object = OdooParserUtils.parseArray(value, "field model 'depends'");
                List<String> stringList = Arrays.stream(object)
                        .map(o -> OdooParserUtils.parseString(o, "field model 'depends' value"))
                        .collect(Collectors.toList());
                fieldModel.setDepends(stringList);
                break;
            }
            case "company_dependent": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'company_dependent'");
                fieldModel.setCompanyDependant(booleanValue);
                break;
            }
            case "groups": {
                String stringValue = OdooParserUtils.parseString(value, "field model 'groups'");
                fieldModel.setGroups(stringValue);
                break;
            }
            case "sortable": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'sortable'");
                fieldModel.setSortable(booleanValue);
                break;
            }
            case "store": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'store'");
                fieldModel.setStore(booleanValue);
                break;
            }
            case "type": {
                String stringValue = OdooParserUtils.parseString(value, "field model 'type'");
                OdooFieldType fieldType = OdooFieldType.fromTypeName(stringValue)
                        .orElseThrow(() -> new OdooRuntimeError("Unhandled field type " + stringValue));
                fieldModel.setType(fieldType);
                break;
            }
            case "manual": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'manual'");
                fieldModel.setManual(booleanValue);
                break;
            }
            case "required": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'required'");
                fieldModel.setRequired(booleanValue);
                break;
            }
            case "searchable": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'searchable'");
                fieldModel.setSearchable(booleanValue);
                break;
            }
            case "relation": {
                String stringValue = OdooParserUtils.parseString(value, "field model 'relation'");
                fieldModel.setRelation(stringValue);
                break;
            }
            case "change_default": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'change_default'");
                fieldModel.setChangeDefault(booleanValue);
                break;
            }
            case "readonly": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'readonly'");
                fieldModel.setReadonly(booleanValue);
                break;
            }
            case "name": {
                String stringValue = OdooParserUtils.parseString(value, "field model 'name'");
                fieldModel.setName(stringValue);
                break;
            }
            case "translate": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'translate'");
                fieldModel.setTranslate(booleanValue);
                break;
            }
            case "trim": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'trim'");
                fieldModel.setTrim(booleanValue);
                break;
            }
            case "attachment": {
                boolean booleanValue = OdooParserUtils.parseBoolean(value, "field model 'attachment'");
                fieldModel.setAttachment(booleanValue);
                break;
            }
            case "relation_field": {
                String stringvalue = OdooParserUtils.parseString(value, "field model 'relation_field'");
                fieldModel.setRelationField(stringvalue);
                break;
            }
            case "help": {
                String stringValue = OdooParserUtils.parseString(value, "field model 'help'");
                fieldModel.setHelp(stringValue);
                break;
            }
            case "group_operator": {
                String stringValue = OdooParserUtils.parseString(value, "field model 'group_operator'");
                fieldModel.setGroupOperator(stringValue);
                break;
            }
            case "size": {
                int intValue = OdooParserUtils.parseInt(value, "field model 'size'");
                fieldModel.setSize(intValue);
                break;
            }
            case "currency_field": {
                String stringValue = OdooParserUtils.parseString(value, "field model 'currency_field'");
                fieldModel.setCurencyField(stringValue);
                break;
            }
            default: {
                throw new OdooRuntimeError("Unhandled field model field '" + keyString + "' with value " + value);
            }
        }
    }

    public String toConstantString() {
        String upperCaseName = name.toUpperCase();
        ModelFieldType<?> modelFieldType = ModelFieldType.fromOdooFieldType(type)
                .orElseThrow(() -> new RuntimeException("Model field not found: " + type));
        String javaTypeName = modelFieldType.getJavaFieldType().getSimpleName();

        return MessageFormat.format("public static OdooModelField<{0}> {1} = new OdooModelField<>("
                        + "\"{2}\", ModelFieldType.{3}, {4},{5},{6},{7});",
                javaTypeName,
                upperCaseName,
                name,
                type.name(),
                required ? "true" : "false",
                readonly ? "true" : "false",
                relation != null ? "\"" + relation + "\"" : "null",
                help != null ? "\"" + help.replaceAll("\n", "\\n").replaceAll("\"", "'") + "\"" : "null"
        );

    }
}
