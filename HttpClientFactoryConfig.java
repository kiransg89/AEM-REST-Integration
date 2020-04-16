package com.example.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.example.constants.Constants;

@ObjectClassDefinition(name = "Http Client API Configuration", description = "Http Client API Configuration")
public @interface HttpClientFactoryConfig {

    @AttributeDefinition(name = "API Host Name", description = "API host name, e.g. https://example.com", type = AttributeType.STRING)
    String apiHostName() default Constants.DEFAULT_API_HOST_NAME;

    @AttributeDefinition(name = "API URI Type Path", description = "API URI type path, e.g. /services/int/v2", type = AttributeType.STRING)
    String uriType() default Constants.DEFAULT_API_URI_TYPE_PATH;

    @AttributeDefinition(name = "API URI Type Path", description = "API URI type path, e.g. /services/ext/v2", type = AttributeType.STRING)
    String uriExternalType() default Constants.DEFAULT_API_URI_EXTERNAL_TYPE_PATH;

    @AttributeDefinition(name = "Relaxed SSL", description = "Defines if self-certified certificates should be allowed to SSL transport", type = AttributeType.BOOLEAN)
    boolean relaxedSSL() default Constants.DEFAULT_RELAXED_SSL;

    @AttributeDefinition(name = "Store Locator API Host Name", description = "Store Locator API host name, e.g. https://example.com", type = AttributeType.STRING)
    String apiStoreLocatorHostName() default Constants.DEFAULT_STORE_LOCATOR_API_HOST_NAME;

    @AttributeDefinition(name = "Maximum number of total open connections", description = "Set maximum number of total open connections, default 5", type = AttributeType.INTEGER)
    int maxTotalOpenConnections() default Constants.DEFAULT_MAXIMUM_TOTAL_OPEN_CONNECTION;

    @AttributeDefinition(name = "Maximum number of concurrent connections per route", description = "Set the maximum number of concurrent connections per route, default 5", type = AttributeType.INTEGER)
    int maxConcurrentConnectionPerRoute() default Constants.DEFAULT_MAXIMUM_CONCURRENT_CONNECTION_PER_ROUTE;

    @AttributeDefinition(name = "Default Keep alive connection in seconds", description = "Default Keep alive connection in seconds, default value is 1", type = AttributeType.LONG)
    int defaultKeepAliveconnection() default Constants.DEFAULT_KEEP_ALIVE_CONNECTION;

    @AttributeDefinition(name = "Default connection timeout in seconds", description = "Default connection timout in seconds, default value is 30", type = AttributeType.LONG)
    long defaultConnectionTimeout() default Constants.DEFAULT_CONNECTION_TIMEOUT;

    @AttributeDefinition(name = "Default socket timeout in seconds", description = "Default socket timeout in seconds, default value is 30", type = AttributeType.LONG)
    long defaultSocketTimeout() default Constants.DEFAULT_SOCKET_TIMEOUT;

    @AttributeDefinition(name = "Default connection request timeout in seconds", description = "Default connection request timeout in seconds, default value is 30", type = AttributeType.LONG)
    long defaultConnectionRequestTimeout() default Constants.DEFAULT_CONNECTION_REQUEST_TIMEOUT;
}
