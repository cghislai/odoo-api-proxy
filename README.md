# Odoo api proxy

The goal of this project is to generate a swagger/openapi spec from an Odoo database instance and implement a server to implement it using the xmlrpc api as a backend.

This project is still work in progress.

The spec can be generated, but some models may fail generation due to unexpected values in their field definition, or serialization errors from the odoo backend.

The actual proxy is not implemented yet, so the spec can only be used to generate the component models.

## Generating the openapi document

You need to run the `SwaggerModelGenerator` main class passing the following system properties:

- odoo.api.uri: The api uri (eg: https://<mydb>.odoo.com) 
- odoo.api.user: The api user (eg: myuser@mailaddress.com)
- odoo.api.key: The api key/user password
- odoo.api.db: The db to use
- spec.outputDir: A directory under which the openapi.yml file will be generated


