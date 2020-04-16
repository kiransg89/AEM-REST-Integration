package com.example.core.services;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;

/**
 * Factory for building pre-configured HttpClient Fluent Executor and Request objects
 * based a configure host, port and (optionally) username/password.
 *
 * Factories will generally be accessed by service lookup using the factory.name property.
 */
public interface HttpClientFactory {

    /**
     * Get the configured Executor object from this factory.
     *
     * @return an Executor object
     */
    Executor getExecutor();

    /**
     * Create a GET request using the base hostname and port defined in the factory configuration.
     *
     * @param partialUrl the portion of the URL after the port (and slash)
     *
     * @return a fluent Request object
     */
    Request get(String partialUrl);

    /**
     * Create a PUT request using the base hostname and port defined in the factory configuration.
     *
     * @param partialUrl the portion of the URL after the port (and slash)
     *
     * @return a fluent Request object
     */
    Request put(String partialUrl);

    /**
     * Create a POST request using the base hostname and port defined in the factory configuration.
     *
     * @param partialUrl the portion of the URL after the port (and slash)
     *
     * @return a fluent Request object
     */
    Request post(String partialUrl);

    /**
     * Create a DELETE request using the base hostname and port defined in the factory configuration.
     *
     * @param partialUrl the portion of the URL after the port (and slash)
     *
     * @return a fluent Request object
     */
    Request delete(String partialUrl);

    /**
     * Create a OPTIONS request using the base hostname and port defined in the factory configuration.
     *
     * @param partialUrl the portion of the URL after the port (and slash)
     *
     * @return a fluent Request object
     */
    Request options(String partialUrl);

    /**
     * Get External URI type is form the factory configuration.
     *
     * @return External URI Type
     */
    String getExternalURIType();

    /**
     * Get apiStoreLocatorHostName URI type is form the factory configuration.
     *
     * @return API StoreLocatorHost
     */
    String getApiStoreLocatorHostName();

    Request postWithAbsolute(String absolutelUrl);
}
