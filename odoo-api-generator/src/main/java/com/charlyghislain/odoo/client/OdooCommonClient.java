package com.charlyghislain.odoo.client;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class OdooCommonClient {

    private final OdooClient odooClient;
    private final XmlRpcClientConfigImpl endpointConfig;

    public OdooCommonClient(OdooClient odooClient) throws MalformedURLException {
        this.odooClient = odooClient;
        this.endpointConfig = this.createConfig();
    }

    public int authenticate() throws XmlRpcException {
        OdooClientConfig clientConfig = odooClient.getClientConfig();
        String apiDb = clientConfig.getApiDb();
        String apiUser = clientConfig.getApiUser();
        String apiPassword = clientConfig.getApiPassword();
        Object response = this.odooClient.getXmlRpcClient().execute(endpointConfig, OdooXmlRpcConstants.COMMON_METHOD_AUTHENTICATE,
                List.of(apiDb, apiUser, apiPassword, Map.of())
        );
        if (response instanceof Integer) {
            return (int) response;
        } else {
            throw new RuntimeException("Unexpected response type : " + response.getClass());
        }
    }

    private XmlRpcClientConfigImpl createConfig() throws MalformedURLException {
        String apiURl = odooClient.getClientConfig().getApiURl();
        URL endpointUrl = new URL(apiURl + OdooXmlRpcConstants.COMMON_ENDPOINT_PATH);

        XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
        commonConfig.setEnabledForExtensions(true);
        commonConfig.setServerURL(endpointUrl);
        return commonConfig;
    }
}
