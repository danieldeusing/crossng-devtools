package de.danieldeusing.crossng.devtools.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate() {
        CloseableHttpClient httpClient = getHttpClient();
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    private CloseableHttpClient getHttpClient() {
        try {
            return HttpClients.custom()
                .setConnectionManager(
                    PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(
                            SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(
                                    SSLContexts.custom()
                                        .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                                        .build())
                                .setHostnameVerifier((s, sslSession) -> true)
                                .build())
                        .build())
                .setDefaultRequestConfig(RequestConfig.custom()
                    .setRedirectsEnabled(false)
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

