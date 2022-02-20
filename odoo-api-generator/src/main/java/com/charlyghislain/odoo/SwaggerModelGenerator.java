package com.charlyghislain.odoo;

import com.charlyghislain.odoo.client.OdooClient;
import com.charlyghislain.odoo.client.OdooClientConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.xmlrpc.XmlRpcException;
import org.openapitools.codegen.serializer.SerializerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class SwaggerModelGenerator {

    public static void main(String[] args) throws XmlRpcException, IOException {
        String apiUri = System.getProperty("odoo.api.uri");
        String apiUser = System.getProperty("odoo.api.user");
        String apiKey = System.getProperty("odoo.api.key");
        String apiDb = System.getProperty("odoo.api.db");

        OdooClientConfig clientConfig = new OdooClientConfig();
        clientConfig.setApiDb(apiDb);
        clientConfig.setApiPassword(apiKey);
        clientConfig.setApiURl(apiUri);
        clientConfig.setApiUser(apiUser);

        OdooClient odooClient = new OdooClient(clientConfig);
        OpenApiModelGenerator modelGenerator = new OpenApiModelGenerator(odooClient);
        OpenAPI openApiModel = modelGenerator.createOpenApiModel();

        String yaml = SerializerUtils.toYamlString(openApiModel);
        BufferedReader yamlReader = new BufferedReader(new StringReader(yaml));
        String filteredYaml = yamlReader.lines()
                .filter(l -> !l.contains("exampleSetFlag:"))
                .collect(Collectors.joining("\n"));

        String outputDir = System.getProperty("spec.outputDir");
        Path outputDirPath = Paths.get(outputDir);
        Files.createDirectories(outputDirPath);
        Path outputFile = outputDirPath.resolve("openapi.yml");
        Files.writeString(outputFile, filteredYaml);
    }

}
