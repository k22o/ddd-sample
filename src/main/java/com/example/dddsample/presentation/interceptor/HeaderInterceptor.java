package com.example.dddsample.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * APIレスポンスへセキュリティ関連の共通HTTPヘッダーを設定するインターセプタ。
 */
@Component
@NullMarked
public class HeaderInterceptor implements HandlerInterceptor {

    /**
     * {@inheritDoc}
     *
     * <p>ハンドラ実行前にレスポンスへセキュリティヘッダーを設定する。レスポンスボディの書き込みより先に
     * ヘッダーを確定させるため、{@code preHandle} で設定する。
     *
     * @return 常に{@code true}（後続のハンドラ処理を継続する）
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private,no-store");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("X-Content-Type-Options", "nosniff");
        // APIだといらないけど、サンプルなのでつけておく
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        return true;
    }
}
