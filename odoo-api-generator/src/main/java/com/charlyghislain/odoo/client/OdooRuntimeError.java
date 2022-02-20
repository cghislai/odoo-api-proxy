package com.charlyghislain.odoo.client;

public class OdooRuntimeError extends RuntimeException {
    public OdooRuntimeError() {
    }

    public OdooRuntimeError(String message) {
        super(message);
    }

    public OdooRuntimeError(String message, Throwable cause) {
        super(message, cause);
    }

    public OdooRuntimeError(Throwable cause) {
        super(cause);
    }

    public OdooRuntimeError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
