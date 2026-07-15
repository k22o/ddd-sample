# DB 設計

## 使用DB

**H2（インメモリDB）+ MyBatis** を使用する。JPAは使わない。  
アプリ起動時に `schema.sql` が自動実行されてスキーマが作成され、終了時にリセットされる。

```gradle
// build.gradle
implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.4'
runtimeOnly 'com.h2database:h2'
```

```yaml
# application.yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  sql:
    init:
      mode: always   # 起動時に schema.sql を自動実行
  h2:
    console:
      enabled: true  # http://localhost:8080/h2-console でデータ確認可能

mybatis:
  configuration:
    map-underscore-to-camel-case: true   # snake_case → camelCase の自動変換
```

スキーマは `src/main/resources/schema.sql` に定義する（JPA の `ddl-auto` は使わない）。

---

## テーブル一覧

| テーブル | 説明 |
|---------|------|
| customers | 顧客 |
| products | 商品 |
| orders | 注文 |
| order_items | 注文明細 |

---

## DDL

```sql
CREATE TABLE customers (
    id          VARCHAR(36)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    postal_code VARCHAR(10),
    prefecture  VARCHAR(50),
    city        VARCHAR(100),
    street      VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE products (
    id              VARCHAR(36)    NOT NULL,
    name            VARCHAR(255)   NOT NULL,
    price_amount    DECIMAL(10, 2) NOT NULL,
    price_currency  VARCHAR(3)     NOT NULL DEFAULT 'JPY',
    description     TEXT,
    created_at      TIMESTAMP      NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE orders (
    id               VARCHAR(36)    NOT NULL,
    customer_id      VARCHAR(36)    NOT NULL,
    status           VARCHAR(20)    NOT NULL,   -- PENDING / CONFIRMED / CANCELLED
    total_amount     DECIMAL(10, 2) NOT NULL,
    total_currency   VARCHAR(3)     NOT NULL DEFAULT 'JPY',
    postal_code      VARCHAR(10),
    prefecture       VARCHAR(50),
    city             VARCHAR(100),
    street           VARCHAR(255),
    payment_id       VARCHAR(255),              -- 外部決済サービスの支払いID
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP      NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE order_items (
    id                  VARCHAR(36)    NOT NULL,
    order_id            VARCHAR(36)    NOT NULL,
    product_id          VARCHAR(36)    NOT NULL,
    product_name        VARCHAR(255)   NOT NULL,   -- 注文時のスナップショット
    unit_price_amount   DECIMAL(10, 2) NOT NULL,   -- 注文時のスナップショット
    unit_price_currency VARCHAR(3)     NOT NULL DEFAULT 'JPY',
    quantity            INT            NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (order_id) REFERENCES orders (id)
);
```

---

## 補足

- `order_items.product_name` / `unit_price_amount` は注文時点の値をスナップショット保存する。商品情報が後から変更されても注文内容が変わらないようにするため。
- `orders.payment_id` は注文確定時に外部決済サービスから払い出されるIDを保存する。キャンセル時の決済取消に使用する。
- IDはすべてUUID（アプリケーション側で生成）。
- `customers` テーブルへの登録経路は本サンプルのスコープ外とする。ユースケース（UC-1〜UC-3、`docs/design/overview.md`参照）は顧客登録を含まず、`CustomerRepository`も`findById`のみを提供する。そのため`created_at`が`NOT NULL`であっても、顧客登録UseCaseやレコード投入APIは実装しない。動作確認・テスト用のレコードは`schema.sql`とは別に、テストコード内やDBシーディング手段で直接投入する前提とする。将来、顧客登録UC（サインアップ等）を追加する場合は、その時点で`CustomerRepository#save`とマッパーのINSERT定義を追加する。
