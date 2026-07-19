package com.example.dddsample.presentation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * APIレスポンスへセキュリティ関連の共通HTTPヘッダーを設定するインターセプタ。
 */
@Component
@NullMarked
public class HeaderInterceptor implements HandlerInterceptor {

    private static final String DEFAULT_CACHE_CONTROL = "private,no-store";

    /**
     * {@inheritDoc}
     *
     * <p>ハンドラ実行前にレスポンスへセキュリティヘッダーを設定する。レスポンスボディの書き込みより先に
     * ヘッダーを確定させるため、{@code preHandle} で設定する。{@code Cache-Control}のみ、エンドポイント側で
     * 個別に上書きできるよう{@link #postHandle}で扱う。
     *
     * @return 常に{@code true}（後続のハンドラ処理を継続する）
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("X-Content-Type-Options", "nosniff");
        // APIだといらないけど、サンプルなのでつけておく
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>ハンドラ実行後、{@code Cache-Control}が未設定の場合にのみデフォルト値
     * （{@value #DEFAULT_CACHE_CONTROL}）を設定する。エンドポイント固有のキャッシュポリシーが必要な場合、
     * コントローラ側で{@code ResponseEntity}の{@code cacheControl(...)}などを用いてあらかじめ設定しておけば、
     * ここでのデフォルト値設定は行われない。
     *
     * <p>本メソッドはレスポンスボディの書き込み後に呼ばれるため、レスポンスが既にコミット済みの場合は
     * ヘッダー設定が反映されない点に留意する。本アプリケーションが返す小さなJSONレスポンスでは通常問題ないが、
     * 大きなレスポンスやストリーミングでは注意が必要である。
     */
    @Override
    public void postHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final @Nullable ModelAndView modelAndView) {
        if (response.getHeader(HttpHeaders.CACHE_CONTROL) == null) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, DEFAULT_CACHE_CONTROL);
        }
    }
}
