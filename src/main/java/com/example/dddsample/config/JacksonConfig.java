package com.example.dddsample.config;

import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson の {@link JsonMapper} Bean を定義するコンフィグクラス。
 * アプリケーション全体でこの Bean を共有して使用する。
 */
@Configuration
@NullMarked
public class JacksonConfig {

    /**
     * アプリケーション共通の {@link JsonMapper} を生成する。
     * カスタマイズにそなえて、一応、インスタンス化しておく
     *
     * @return {@link JsonMapper} インスタンス
     */
    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .build();
    }
}
