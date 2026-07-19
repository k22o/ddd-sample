package com.example.dddsample.config;

import java.time.Duration;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

/**
 * 外部APIクライアント用の {@link RestClient} Bean を定義するコンフィグクラス。
 */
@Configuration
@NullMarked
public class RestClientConfig {

    private final String paymentBaseUrl;

    private final String inventoryBaseUrl;

    private final Duration connectTimeout;

    private final Duration readTimeout;

    /**
     * コンストラクタ。
     *
     * @param paymentBaseUrl   決済サービスのベースURL
     * @param inventoryBaseUrl 在庫管理サービスのベースURL
     * @param connectTimeout   接続タイムアウト
     * @param readTimeout      読み取りタイムアウト
     */
    public RestClientConfig(
            @Value("${client.payment.base-url}") final String paymentBaseUrl,
            @Value("${client.inventory.base-url}") final String inventoryBaseUrl,
            @Value("${client.connect-timeout:1s}") final Duration connectTimeout,
            @Value("${client.read-timeout:1s}") final Duration readTimeout
    ) {
        this.paymentBaseUrl = paymentBaseUrl;
        this.inventoryBaseUrl = inventoryBaseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * 決済サービス向け {@link RestClient} を生成する。
     *
     * @return 決済サービス用 {@link RestClient}
     */
    @Bean
    public RestClient paymentRestClient() {
        return buildRestClient(paymentBaseUrl);
    }

    /**
     * 在庫管理サービス向け {@link RestClient} を生成する。
     *
     * @return 在庫管理サービス用 {@link RestClient}
     */
    @Bean
    public RestClient inventoryRestClient() {
        return buildRestClient(inventoryBaseUrl);
    }

    /**
     * 指定されたベースURLと {@link JsonMapper} を用いて {@link RestClient} を生成する。
     *
     * @param baseUrl    APIのベースURL
     * @return 生成された {@link RestClient}
     */
    private RestClient buildRestClient(final String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(buildRequestFactory())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * 接続タイムアウト・読み取りタイムアウトを設定した {@link ClientHttpRequestFactory} を生成する。
     *
     * @return 生成された {@link ClientHttpRequestFactory}
     */
    private ClientHttpRequestFactory buildRequestFactory() {
        final HttpClientSettings httpClientSettings = HttpClientSettings.defaults()
                .withConnectTimeout(connectTimeout)
                .withReadTimeout(readTimeout);
        return ClientHttpRequestFactoryBuilder.detect().build(httpClientSettings);
    }
}
