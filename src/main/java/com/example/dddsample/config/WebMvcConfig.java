package com.example.dddsample.config;

import com.example.dddsample.presentation.interceptor.HeaderInterceptor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVCのインターセプタ登録を行うコンフィグクラス。
 */
@Configuration
@RequiredArgsConstructor
@NullMarked
public class WebMvcConfig implements WebMvcConfigurer {

    private final HeaderInterceptor headerInterceptor;

    /**
     * {@inheritDoc}
     *
     * <p>API配下のリクエストに {@link HeaderInterceptor} を適用する。
     */
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(headerInterceptor).addPathPatterns("/api/**");
    }
}
