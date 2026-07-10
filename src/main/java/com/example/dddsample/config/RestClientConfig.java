package com.example.dddsample.config;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    /**
     * コンストラクタ。
     *
     * @param paymentBaseUrl   決済サービスのベースURL
     * @param inventoryBaseUrl 在庫管理サービスのベースURL
     */
    public RestClientConfig(
            @Value("${client.payment.base-url}") final String paymentBaseUrl,
            @Value("${client.inventory.base-url}") final String inventoryBaseUrl) {
        this.paymentBaseUrl = paymentBaseUrl;
        this.inventoryBaseUrl = inventoryBaseUrl;
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
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
