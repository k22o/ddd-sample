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
        void レスポンスにセキュリティヘッダーを設定してtrueを返す() {
            final MockHttpServletRequest request = new MockHttpServletRequest();
            final MockHttpServletResponse response = new MockHttpServletResponse();

            final boolean result = interceptor.preHandle(request, response, new Object());

            assertThat(result).isTrue();
            assertThat(response.getHeader("Cache-Control")).isEqualTo("no-store");
            assertThat(response.getHeader("Strict-Transport-Security")).isEqualTo("max-age=31536000; includeSubDomains");
            assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
            assertThat(response.getHeader("X-Frame-Options")).isEqualTo("sameorigin");
            assertThat(response.getHeader("X-XSS-Protection")).isEqualTo("1; mode=block");
        }
    }
}
