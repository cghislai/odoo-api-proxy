package com.charlyghislain.odoo.client;

import lombok.Getter;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;

import java.net.MalformedURLException;

public class OdooClient {

    @Getter
    private final OdooClientConfig clientConfig;
    @Getter
    private final XmlRpcClient xmlRpcClient;

    @Getter
    private Integer authenticatedUserId = null;
    private OdooCommonClient commonClient = null;

    public OdooClient(OdooClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.xmlRpcClient = new XmlRpcClient();

    }

    public OdooModelClient getModelClient(String modelName) {
        try {
            OdooModelClient odooModelClient = new OdooModelClient(this, modelName);
            return odooModelClient;
        } catch (MalformedURLException e) {
            throw new OdooRuntimeError("Unable to create model client", e);
        }
    }

    public boolean isAuthenticated() {
        return this.authenticatedUserId != null;
    }

    public int getAuthenticatedUserId() {
        if (!isAuthenticated()) {
            this.authenticatedUserId = authenticate();
        }
        return this.authenticatedUserId;
    }

    private int authenticate() {
        try {
            if (this.commonClient == null) {
                this.commonClient = new OdooCommonClient(this);
            }
            return this.commonClient.authenticate();
        } catch (MalformedURLException | XmlRpcException e) {
            throw new OdooRuntimeError(e);
        }
    }

}
