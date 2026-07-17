package com.example.dddsample;

import org.jspecify.annotations.NullMarked;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * テストコード全体で使用する共通ユーティリティ。
 */
@NullMarked
public final class TestUtil {

    private TestUtil() {
    }

    /**
     * クラスパス上のJSONファイルを {@link Resource} として読み込む。
     *
     * <p>{@code src/test/resources} からの相対パスを指定する。
     *
     * @param path クラスパスルートからのJSONファイルのパス
     * @return JSONファイルを表す {@link Resource}
     */
    public static Resource readJson(final String path) {
        return new ClassPathResource(path);
    }
}
