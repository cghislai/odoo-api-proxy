package com.charlyghislain.odoo.client;

public class OdooXmlRpcConstants {

    public static final String COMMON_ENDPOINT_PATH = "/xmlrpc/2/common";
    public static final String COMMON_METHOD_AUTHENTICATE = "authenticate";


    public static final String OBJECTS_ENDPOINT_PATH = "/xmlrpc/2/object";
    public static final String OBJECTS_METHOD_EXECUTE = "execute_kw";
    public static final String OBJECTS_METHOD_EXECUTE_ARG_FIELDS_GET = "fields_get";
    public static final String OBJECTS_METHOD_EXECUTE_ARG_SEARCH_READ = "search_read";
    public static final String OBJECTS_METHOD_EXECUTE_ARG_SEARCH = "search";
    public static final String OBJECTS_METHOD_EXECUTE_ARG_READ = "read";

    public static final String FIELD_TYPE_DATETIME = "datetime";

    public static final String CONTEXT_APPEND_TYPE_TO_TAX_NAME_BOOL = "append_type_to_tax_name";
}
