package com.charlyghislain.odoo.client;

import com.charlyghislain.odoo.client.fields.FieldModel;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OdooModelClient {

    private final OdooClient odooClient;
    private final String odooModelName;
    private final XmlRpcClientConfigImpl endpointConfig;

    private Map<String, FieldModel> fieldModels;

    public OdooModelClient(OdooClient odooClient, String odooModelName) throws MalformedURLException {
        this.odooClient = odooClient;
        this.odooModelName = odooModelName;
        this.endpointConfig = this.createConfig();
    }

    public Map<String, FieldModel> getFields() {
        if (this.fieldModels != null) {
            return this.fieldModels;
        }

        try {
            XmlRpcClient xmlRpcClient = odooClient.getXmlRpcClient();
            OdooClientConfig clientConfig = odooClient.getClientConfig();
            String apiDb = clientConfig.getApiDb();
            String apiPassword = clientConfig.getApiPassword();
            int authenticatedUserId = odooClient.getAuthenticatedUserId();

            List<Object> fieldGetParams = List.of(
                    apiDb, authenticatedUserId, apiPassword, odooModelName,
                    OdooXmlRpcConstants.OBJECTS_METHOD_EXECUTE_ARG_FIELDS_GET,
                    // Fields names in map: {'attributes': ['field1', 'field3']}
                    List.of(
//                            List.of(
//                                    List.of("state", "=", "base")
//                            )
                    ), Map.of()
            );
            Object response = xmlRpcClient.execute(endpointConfig, OdooXmlRpcConstants.OBJECTS_METHOD_EXECUTE, fieldGetParams);
            this.fieldModels = parseFields(response);
            return this.fieldModels;
        } catch (XmlRpcException e) {
            throw new OdooRuntimeError("Unable to list fields for " + odooModelName + " : " + e.getMessage(), e);
        }
    }

    public List<Object> searchRead(List<Object> filters, int offset, int limit, List<String> fields) throws XmlRpcException {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "offset", offset,
                "limit", limit
        ));
        if (!fields.isEmpty()) {
            parameters.put("fields", fields);
        }

        try {
            XmlRpcClient xmlRpcClient = odooClient.getXmlRpcClient();
            OdooClientConfig clientConfig = odooClient.getClientConfig();
            String apiDb = clientConfig.getApiDb();
            String apiPassword = clientConfig.getApiPassword();
            int authenticatedUserId = odooClient.getAuthenticatedUserId();

            List<Object> searchReadParams = List.of(
                    apiDb, authenticatedUserId, apiPassword, odooModelName,
                    OdooXmlRpcConstants.OBJECTS_METHOD_EXECUTE_ARG_SEARCH_READ,
                    filters, parameters
            );
            Object response = xmlRpcClient.execute(endpointConfig, OdooXmlRpcConstants.OBJECTS_METHOD_EXECUTE, searchReadParams);
            Object[] responseArray = OdooParserUtils.parseArray(response, "Search read response");
            return Arrays.asList(responseArray);
        } catch (XmlRpcException e) {
            throw new OdooRuntimeError("Unable to search read " + odooModelName + " : " + e.getMessage(), e);
        }
    }


    public List<Integer> search(List<Object> filters, int offset, int limit) throws XmlRpcException {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "offset", offset,
                "limit", limit
        ));

        try {
            XmlRpcClient xmlRpcClient = odooClient.getXmlRpcClient();
            OdooClientConfig clientConfig = odooClient.getClientConfig();
            String apiDb = clientConfig.getApiDb();
            String apiPassword = clientConfig.getApiPassword();
            int authenticatedUserId = odooClient.getAuthenticatedUserId();

            List<Object> searchReadParams = List.of(
                    apiDb, authenticatedUserId, apiPassword, odooModelName,
                    OdooXmlRpcConstants.OBJECTS_METHOD_EXECUTE_ARG_SEARCH,
                    List.of(filters), parameters
            );
            Object response = xmlRpcClient.execute(endpointConfig, OdooXmlRpcConstants.OBJECTS_METHOD_EXECUTE, searchReadParams);
            Object[] responseArray = OdooParserUtils.parseArray(response, "Search  response");
            return Arrays.stream(responseArray)
                    .map(o -> OdooParserUtils.parseInt(o, "Search response id"))
                    .collect(Collectors.toList());
        } catch (XmlRpcException e) {
            throw new OdooRuntimeError("Unable to search  " + odooModelName + " : " + e.getMessage(), e);
        }
    }

    public Object read(int id, List<String> fields) throws XmlRpcException {
        Map<String, Object> parameters = new HashMap<>();
        if (!fields.isEmpty()) {
            parameters.put("fields", fields);
        }

        List<Integer> idParam = List.of(id);

        try {
            XmlRpcClient xmlRpcClient = odooClient.getXmlRpcClient();
            OdooClientConfig clientConfig = odooClient.getClientConfig();
            String apiDb = clientConfig.getApiDb();
            String apiPassword = clientConfig.getApiPassword();
            int authenticatedUserId = odooClient.getAuthenticatedUserId();

            List<Object> searchReadParams = List.of(
                    apiDb, authenticatedUserId, apiPassword, odooModelName,
                    OdooXmlRpcConstants.OBJECTS_METHOD_EXECUTE_ARG_READ,
                    idParam, parameters
            );
            Object response = xmlRpcClient.execute(endpointConfig, OdooXmlRpcConstants.OBJECTS_METHOD_EXECUTE, searchReadParams);
            Object[] responseArray = OdooParserUtils.parseArray(response, "Read response");
            if (responseArray.length != 1) {
                throw new OdooRuntimeError("Unable to read " + odooModelName + " : empty response");
            }
            return responseArray[0];
        } catch (XmlRpcException e) {
            throw new OdooRuntimeError("Unable to  read " + odooModelName + " : " + e.getMessage(), e);
        }
    }

    private Map<String, FieldModel> parseFields(Object response) {
        if (Map.class.isAssignableFrom(response.getClass())) {
            Map<Object, Object> responseMap = (Map<Object, Object>) response;
            return responseMap.entrySet().stream()
                    .map(this::parseFieldModel)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
        } else {
            throw new OdooRuntimeError("Unexpected response type: " + response.getClass());
        }
    }

    private Map.Entry<String, FieldModel> parseFieldModel(Map.Entry<Object, Object> responseEntry) {
        Object key = responseEntry.getKey();
        String keyString = OdooParserUtils.parseString(key, "field model response key");
        Object value = responseEntry.getValue();
        Map<Object, Object> valueMap = OdooParserUtils.parseMap(value, "field model response value");
        FieldModel fieldModel = FieldModel.parseValueMap(valueMap);
        return Map.entry(keyString, fieldModel);
    }

    private XmlRpcClientConfigImpl createConfig() throws MalformedURLException {
        String apiURl = odooClient.getClientConfig().getApiURl();
        URL endpointUrl = new URL(apiURl + OdooXmlRpcConstants.OBJECTS_ENDPOINT_PATH);

        XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
        commonConfig.setEnabledForExtensions(true);
        commonConfig.setServerURL(endpointUrl);
        return commonConfig;
    }

}
