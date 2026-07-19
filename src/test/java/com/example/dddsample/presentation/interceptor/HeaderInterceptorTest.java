package com.example.dddsample.presentation.interceptor;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link HeaderInterceptor} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class HeaderInterceptorTest {

    private final HeaderInterceptor interceptor = new HeaderInterceptor();

    @Nested
    class PreHandle {

        @Test
        void レスポンスにCache_Control以外のセキュリティヘッダーを設定してtrueを返す() {
            final MockHttpServletRequest request = new MockHttpServletRequest();
            final MockHttpServletResponse response = new MockHttpServletResponse();

            final boolean result = interceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
            assertThat(response.getHeader("Cache-Control")).isNull();
            assertThat(response.getHeader("Strict-Transport-Security")).isEqualTo("max-age=31536000; includeSubDomains");
            assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
            assertThat(response.getHeader("X-Frame-Options")).isEqualTo("SAMEORIGIN");
            assertThat(response.getHeader("X-XSS-Protection")).isEqualTo("1; mode=block");
        }
    }

    @Nested
    class PostHandle {

        @Test
        void Cache_Controlが未設定の場合はデフォルト値を設定する() {
            final MockHttpServletRequest request = new MockHttpServletRequest();
            final MockHttpServletResponse response = new MockHttpServletResponse();

            interceptor.postHandle(request, response, new Object(), null);

            assertThat(response.getHeader("Cache-Control")).isEqualTo("private,no-store");
        }

        @Test
        void Cache_Controlが既に設定されている場合は上書きしない() {
            final MockHttpServletRequest request = new MockHttpServletRequest();
            final MockHttpServletResponse response = new MockHttpServletResponse();
            response.setHeader("Cache-Control", "public,max-age=60");

            interceptor.postHandle(request, response, new Object(), null);

            assertThat(response.getHeader("Cache-Control")).isEqualTo("public,max-age=60");
        }
    }
}
