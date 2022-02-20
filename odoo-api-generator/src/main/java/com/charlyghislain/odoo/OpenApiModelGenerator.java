package com.charlyghislain.odoo;

import com.charlyghislain.odoo.client.OdooClient;
import com.charlyghislain.odoo.client.OdooClientConfig;
import com.charlyghislain.odoo.client.OdooModelClient;
import com.charlyghislain.odoo.client.OdooParserUtils;
import com.charlyghislain.odoo.client.OdooRuntimeError;
import com.charlyghislain.odoo.client.fields.FieldModel;
import com.charlyghislain.odoo.client.fields.OdooFieldType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.xmlrpc.XmlRpcException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpenApiModelGenerator {

    private final OdooClient odooClient;

    public OpenApiModelGenerator(OdooClient odooClient) {
        this.odooClient = odooClient;
    }

    public OpenAPI createOpenApiModel() throws XmlRpcException {
        OdooModelClient modelClient = odooClient.getModelClient("ir.model");
        List<Integer> idList = modelClient.search(List.of(
                List.of("state", "=", "base")
        ), 0, 1000);
        List<Map<Object, Object>> modelList = new ArrayList<>();
        for (int id : idList) {
            try {
                Object model = modelClient.read(id, List.of("id", "name", "model"));
                Map<Object, Object> modelsMap = OdooParserUtils.parseMap(model, "Model response as map");
                String modelName = OdooParserUtils.parseString(modelsMap.get("model"), "Model name");

                System.out.println("Read model " + modelName);
                modelList.add(modelsMap);
            } catch (Exception e) {
                System.err.println("Unable to read model #" + id + ": " + e.getMessage());
            }
        }

        OdooClientConfig clientConfig = odooClient.getClientConfig();
        String apiDb = clientConfig.getApiDb();
        OpenAPI openApi = createOpenApi(odooClient, modelList, apiDb);
        return openApi;
    }

    private OpenAPI createOpenApi(OdooClient odooClient, List<Map<Object, Object>> responseObjects, String apiDb) {
        OpenAPI openAPI = new OpenAPI();
        Info info = new Info();
        info.setTitle("Odoo REST api (db " + apiDb + ")");
        openAPI.setInfo(info);

        Paths paths = new Paths();
        Components components = new Components();

        responseObjects.forEach(modelMap -> {
            String modelName = OdooParserUtils.parseString(modelMap.get("model"), "Model name");
            createComponent(components, modelName, modelMap, odooClient);
            createModelOperations(paths, modelName, modelMap);
        });
        openAPI.setComponents(components);
        openAPI.setPaths(paths);
        return openAPI;
    }

    private void createComponent(Components components, String modelName, Map<Object, Object> modelsMap, OdooClient odooClient) {
        OdooModelClient modelClient = odooClient.getModelClient(modelName);
        ObjectSchema objectSchema = new ObjectSchema();


        Map<String, FieldModel> fields;
        try {
            fields = modelClient.getFields();
        } catch (OdooRuntimeError e) {
            System.err.println("Unable to list fields for model " + modelName + ": " + e.getMessage());
            components.addSchemas(modelName, objectSchema);
            return;
        }
        System.out.println(" read " + fields.size() + " fields for " + modelName);

        fields.entrySet().forEach(e -> {
            String fieldName = e.getKey();
            FieldModel fieldModel = e.getValue();
            Schema fieldSchema = createFieldSchema(fieldModel);

            objectSchema.addProperties(fieldName, fieldSchema);
            objectSchema.setReadOnly(fieldModel.isReadonly());
        });
        List<String> requiredFields = fields.entrySet().stream()
                .filter(e -> e.getValue().isRequired())
                .map(e -> e.getKey())
                .collect(Collectors.toList());
        objectSchema.setRequired(requiredFields);
        components.addSchemas(modelName, objectSchema);
    }

    private Schema createFieldSchema(FieldModel fieldModel) {
        OdooFieldType type = fieldModel.getType();
        switch (type) {
            case DATETIME: {
                return new DateTimeSchema();
            }
            case BOOLEAN:
                return new BooleanSchema();
            case MONETARY:
                return new NumberSchema();
            case INTEGER:
                return new IntegerSchema();
            case SELECTION: {
                StringSchema stringSchema = new StringSchema();
                stringSchema.setEnum(fieldModel.getSelectionValues());
                return stringSchema;
            }
            case CHAR:
                return new StringSchema();
            case TEXT:
                return new StringSchema();
            case FLOAT:
                return new NumberSchema();
            /**
             * See https://www.odoo.com/documentation/15.0/developer/reference/backend/orm.html#odoo.models.Model.write
             *
             * One2many and Many2many use a special command protocol detailed in the documentation to the write method.
             * The expected value of a One2many or Many2many relational field is a list of Command that manipulate the relation the implement.
             * There are a total of 7 commands: create(), update(), delete(), unlink(), link(), clear(), and set().
             *
             * eg: (SET, 0, ids)
             */
            case ONE_TO_MANY: {
                ArraySchema arraySchema = new ArraySchema();
                arraySchema.setItems(new IntegerSchema());
                return arraySchema;
            }
            case MANY_TO_MANY: {
                ArraySchema arraySchema = new ArraySchema();
                arraySchema.setItems(new IntegerSchema());
                return arraySchema;
            }
            case MANY_TO_ONE:
                return new IntegerSchema();
            case BINARY:
                return new BinarySchema();
            case DATE:
                return new DateSchema();
            case HTML:
                return new StringSchema();
            case MANY_TO_ONE_REFERENCE:
                return new StringSchema();
            case REFERENCE:
                return new StringSchema();
            default:
                throw new RuntimeException("Unhandled field type " + type);
        }
    }


    private void createModelOperations(Paths paths, String modelName, Map<Object, Object> modelsMap) {
        String modelLabel = OdooParserUtils.parseString(modelsMap.get("name"), "Model label");

        PathItem rootPathItem = new PathItem();
        Operation postOperation = createPostOperation(modelName, modelLabel);
        rootPathItem.setPost(postOperation);
        paths.addPathItem("/model/" + modelName, rootPathItem);

        PathItem idPathItem = new PathItem();
        Operation getOperation = createGetOperation(modelName, modelLabel);
        idPathItem.setGet(getOperation);

        Operation putOperation = createPutOperation(modelName, modelLabel);
        idPathItem.setPut(putOperation);

        Operation deleteOpeation = createDeleteOperation(modelName, modelLabel);
        idPathItem.setDelete(deleteOpeation);
        paths.addPathItem("/model/" + modelName + "/{id}", idPathItem);
    }

    private Operation createGetOperation(String modelName, String modelLabel) {
        Operation getOperation = new Operation();
        getOperation.setOperationId("get_" + modelName);
        getOperation.setDescription("Read a " + modelLabel);

        Parameter idPathParm = new Parameter();
        idPathParm.setName("id");
        idPathParm.setDescription("The id of the object to fetch");
        idPathParm.setIn("path");
        IntegerSchema idParamSchema = new IntegerSchema();
        idPathParm.setSchema(idParamSchema);
        idPathParm.setRequired(true);
        getOperation.addParametersItem(idPathParm);

        ApiResponse response = new ApiResponse();
        Content content = createJsonModelContent(modelName);
        response.setContent(content);
        ApiResponses apiResponses = new ApiResponses();
        apiResponses.addApiResponse("success", response);
        getOperation.setResponses(apiResponses);
        return getOperation;
    }

    private Operation createPutOperation(String modelName, String modelLabel) {
        Operation putOperation = new Operation();
        putOperation.setOperationId("put_" + modelName);
        putOperation.setDescription("Update a " + modelLabel);

        Parameter idPathParm = new Parameter();
        idPathParm.setName("id");
        idPathParm.setDescription("The id of the object to fetch");
        idPathParm.setIn("path");
        IntegerSchema idParamSchema = new IntegerSchema();
        idPathParm.setSchema(idParamSchema);
        idPathParm.setRequired(true);
        putOperation.addParametersItem(idPathParm);

        Content bodyContent = createJsonModelContent(modelName);
        RequestBody requestBody = new RequestBody();
        requestBody.setContent(bodyContent);

        ApiResponse response = new ApiResponse();
        Content responseContent = createJsonModelContent(modelName);
        response.setContent(responseContent);
        ApiResponses apiResponses = new ApiResponses();
        apiResponses.addApiResponse("success", response);
        putOperation.setResponses(apiResponses);
        return putOperation;
    }

    private Operation createPostOperation(String modelName, String modelLabel) {
        Operation putOperation = new Operation();
        putOperation.setOperationId("post_" + modelName);
        putOperation.setDescription("Create a " + modelLabel);

        Content bodyContent = createJsonModelContent(modelName);
        RequestBody requestBody = new RequestBody();
        requestBody.setContent(bodyContent);

        ApiResponse response = new ApiResponse();
        Content responseContent = createJsonModelContent(modelName);
        response.setContent(responseContent);
        ApiResponses apiResponses = new ApiResponses();
        apiResponses.addApiResponse("success", response);
        putOperation.setResponses(apiResponses);
        return putOperation;
    }

    private Operation createDeleteOperation(String modelName, String modelLabel) {
        Operation putOperation = new Operation();
        putOperation.setOperationId("delete_" + modelName);
        putOperation.setDescription("Delete a " + modelLabel);

        Parameter idPathParm = new Parameter();
        idPathParm.setName("id");
        idPathParm.setDescription("The id of the object to fetch");
        idPathParm.setIn("path");
        IntegerSchema idParamSchema = new IntegerSchema();
        idPathParm.setSchema(idParamSchema);
        idPathParm.setRequired(true);
        putOperation.addParametersItem(idPathParm);

        ApiResponse response = new ApiResponse();
        ApiResponses apiResponses = new ApiResponses();
        apiResponses.addApiResponse("success", response);
        putOperation.setResponses(apiResponses);
        return putOperation;
    }

    private Content createJsonModelContent(String modelName) {
        Content content = new Content();
        MediaType mediaType = new MediaType();
        ObjectSchema schema = new ObjectSchema();
        schema.set$ref(createComponentRef(modelName));
        mediaType.setSchema(schema);
        content.addMediaType("application/json", mediaType);
        return content;
    }

    private String createComponentRef(String modelName) {
        return "#/components/schemas/" + modelName;
    }
}
