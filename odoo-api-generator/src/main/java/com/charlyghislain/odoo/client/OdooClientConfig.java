package com.charlyghislain.odoo.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OdooClientConfig {

    private String apiURl;
    private String apiDb;
    private String apiPassword;
    private String apiUser;

    @Deprecated //FIXME
    private Integer apiUserId;

}
