package com.example.core.services.impl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.core.services.HttpClientFactory;
import com.example.core.services.config.HttpClientFactoryConfig;

/**
 * Implementation of @{@link HttpClientFactory}.
 * <p>
 * HttpClientFactory provides service to handle API connection and executor.
 */
@Component(service = HttpClientFactory.class)
@Designate(ocd = HttpClientFactoryConfig.class)
public class HttpClientFactoryImpl implements HttpClientFactory {

    private static final Logger log = LoggerFactory.getLogger(HttpClientFactoryImpl.class);

    private Executor executor;
    private String baseUrl;
    private String uriExternalType;
    private String apiStoreLocatorHostName;
    private CloseableHttpClient httpClient;
    private HttpClientFactoryConfig config;

    @Reference
    private HttpClientBuilderFactory httpClientBuilderFactory;

    @Activate
    @Modified
    protected void activate(HttpClientFactoryConfig config) throws Exception {

        log.info("########### OSGi Configs Start ###############");
        log.info("API Host Name : {}", config.apiHostName());
        log.info("URI Type: {}", config.uriType());
        log.info("########### OSGi Configs End ###############");

        closeHttpConnection();

        this.config = config;
        if (this.config.apiHostName() == null) {
            log.debug("Configuration is not valid. Both hostname is mandatory.");
            throw new IllegalArgumentException("Configuration is not valid. Both hostname is mandatory.");
        }

        this.uriExternalType = this.config.uriExternalType();
        this.apiStoreLocatorHostName = this.config.apiStoreLocatorHostName();
        this.baseUrl = StringUtils.join(this.config.apiHostName(), config.uriType());

        initExecutor();
    }

    private void initExecutor() throws Exception {

        PoolingHttpClientConnectionManager connMgr = null;

        RequestConfig requestConfig = initRequestConfig();

        HttpClientBuilder builder = httpClientBuilderFactory.newBuilder();
        builder.setDefaultRequestConfig(requestConfig);

        if (config.relaxedSSL()) {

            connMgr = initPoolingConnectionManagerWithRelaxedSSL();

        } else {

            connMgr = new PoolingHttpClientConnectionManager();
        }

        connMgr.closeExpiredConnections();

        connMgr.setMaxTotal(config.maxTotalOpenConnections());
        connMgr.setDefaultMaxPerRoute(config.maxConcurrentConnectionPerRoute());

        builder.setConnectionManager(connMgr);

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept", "application/json"));
        builder.setDefaultHeaders(headers);
        builder.setKeepAliveStrategy(keepAliveStratey);

        httpClient = builder.build();

        executor = Executor.newInstance(httpClient);
    }

    private PoolingHttpClientConnectionManager initPoolingConnectionManagerWithRelaxedSSL()
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        PoolingHttpClientConnectionManager connMgr;
        SSLContextBuilder sslbuilder = new SSLContextBuilder();
        sslbuilder.loadTrustMaterial(new TrustAllStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslbuilder.build(),
                NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
        connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        return connMgr;
    }

    private RequestConfig initRequestConfig() {

        return RequestConfig.custom()
                .setConnectTimeout(Math.toIntExact(TimeUnit.SECONDS.toMillis(config.defaultConnectionTimeout())))
                .setSocketTimeout(Math.toIntExact(TimeUnit.SECONDS.toMillis(config.defaultSocketTimeout())))
                .setConnectionRequestTimeout(
                        Math.toIntExact(TimeUnit.SECONDS.toMillis(config.defaultConnectionRequestTimeout())))
                .build();
    }

    @Deactivate
    protected void deactivate() {
        closeHttpConnection();
    }

    private void closeHttpConnection() {
        if (null != httpClient) {
            try {
                httpClient.close();
            } catch (final IOException exception) {
                log.debug("IOException while clossing API, {}", exception.getMessage());
            }
        }
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public Request get(String partialUrl) {
        String url = baseUrl + partialUrl;
        return Request.Get(url);
    }

    @Override
    public Request post(String partialUrl) {
        String url = baseUrl + partialUrl;
        return Request.Post(url);
    }

    @Override
    public Request postWithAbsolute(String absolutelUrl) {
        return Request.Post(absolutelUrl);
    }

    @Override
    public Request put(String partialUrl) {
        String url = baseUrl + partialUrl;
        return Request.Put(url);
    }

    @Override
    public Request delete(String partialUrl) {
        String url = baseUrl + partialUrl;
        return Request.Delete(url);
    }

    @Override
    public Request options(String partialUrl) {
        String url = baseUrl + partialUrl;
        return Request.Options(url);
    }

    @Override
    public String getExternalURIType() {
        return uriExternalType;
    }

    @Override
    public String getApiStoreLocatorHostName() {
        return apiStoreLocatorHostName;
    }

    ConnectionKeepAliveStrategy keepAliveStratey = new ConnectionKeepAliveStrategy() {

        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            /*
             * HeaderElementIterator headerElementIterator = new BasicHeaderElementIterator(
             * response.headerIterator(HTTP.CONN_KEEP_ALIVE));
             *
             * while (headerElementIterator.hasNext()) { HeaderElement headerElement =
             * headerElementIterator.nextElement(); String param = headerElement.getName();
             * String value = headerElement.getValue(); if (value != null &&
             * param.equalsIgnoreCase("timeout")) { return
             * TimeUnit.SECONDS.toMillis(Long.parseLong(value)); } }
             */

            return TimeUnit.SECONDS.toMillis(config.defaultKeepAliveconnection());
        }
    };
}
