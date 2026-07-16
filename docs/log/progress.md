# 実装進捗ログ

最終更新: 2026-07-16

---

## 完了

### 設計
- [x] `docs/design/overview.md` — ユースケース・ドメインモデル設計
- [x] `docs/design/api.md` — REST API設計
- [x] `docs/design/database.md` — DB設計（H2 + MyBatis）
- [x] `docs/design/directory-structure.md` — ディレクトリ構成
- [x] `docs/design/external-client.md` — 外部APIクライアント設計

### プロジェクト設定
- [x] `build.gradle` — JSpecify追加
- [x] `lombok.config` — `@Qualifier` をコンストラクタ引数へコピーする設定
- [x] `application.yaml` — 外部クライアントのbase-url設定

### domain層
- [x] `domain/model/shared/Money.java` — Value Object
- [x] `domain/model/shared/Quantity.java` — Value Object
- [x] `domain/model/customer/CustomerId.java` — Value Object
- [x] `domain/model/product/ProductId.java` — Value Object
- [x] `domain/client/PaymentClient.java` — interface
- [x] `domain/client/InventoryClient.java` — interface
- [x] `domain/exception/PaymentFailedException.java`
- [x] `domain/exception/InsufficientStockException.java`

### infrastructure層（client）
- [x] `infrastructure/client/dto/PaymentRequest.java`
- [x] `infrastructure/client/dto/PaymentResponse.java`
- [x] `infrastructure/client/dto/InventoryStockResponse.java`
- [x] `infrastructure/client/dto/InventoryReserveRequest.java`
- [x] `infrastructure/client/dto/InventoryReserveResponse.java`
- [x] `infrastructure/client/PaymentClientImpl.java`
- [x] `infrastructure/client/InventoryClientImpl.java`

### config層
- [x] `config/RestClientConfig.java`

### domain層
- [x] `domain/model/shared/Address.java` — Value Object
- [x] `domain/model/order/OrderId.java` — Value Object
- [x] `domain/model/order/OrderItemId.java` — Value Object
- [x] `domain/model/order/OrderStatus.java` — Enum
- [x] `domain/model/order/OrderItem.java` — 集約内Entity
- [x] `domain/model/order/Order.java` — 集約ルート
- [x] `domain/model/customer/Customer.java` — 集約
- [x] `domain/model/product/Product.java` — 集約
- [x] `domain/repository/OrderRepository.java` — interface
- [x] `domain/repository/CustomerRepository.java` — interface
- [x] `domain/repository/ProductRepository.java` — interface
- [x] `domain/exception/OrderNotFoundException.java`
- [x] `domain/exception/CustomerNotFoundException.java`
- [x] `domain/exception/ProductNotFoundException.java`
- [x] `domain/model/shared/Money.java` — `multiply(int)` を追加（小計計算用）

### infrastructure層（DB）
- [x] `build.gradle` — MyBatis・H2依存追加
- [x] `application.yaml` — H2・MyBatis設定追加
- [x] `src/main/resources/schema.sql` — テーブル定義
- [x] `infrastructure/db/record/OrderRecord.java`
- [x] `infrastructure/db/record/OrderItemRecord.java`
- [x] `infrastructure/db/record/CustomerRecord.java`
- [x] `infrastructure/db/record/ProductRecord.java`
- [x] `infrastructure/db/mapper/OrderMapper.java` — MyBatis @Mapper
- [x] `infrastructure/db/mapper/OrderItemMapper.java` — MyBatis @Mapper
- [x] `infrastructure/db/mapper/CustomerMapper.java` — MyBatis @Mapper
- [x] `infrastructure/db/mapper/ProductMapper.java` — MyBatis @Mapper
- [x] `infrastructure/db/repository/OrderRepositoryImpl.java`
- [x] `infrastructure/db/repository/CustomerRepositoryImpl.java`
- [x] `infrastructure/db/repository/ProductRepositoryImpl.java`

> 注: このサンドボックス環境にはJDKが導入されておらず、`./gradlew compileJava` によるビルド確認は未実施。ローカル環境での確認を推奨。

### infrastructure層（DB）の改善 — コードレビュー指摘事項（2026-07-11）対応（2026-07-15）
- [x] `ProductMapper#findByIds` に空リストを渡すとSQL構文エラーになる不具合を修正（`ProductRepositoryImpl#findByIds`に空リストガードを追加）
- [x] `CustomerRepositoryImpl` / `OrderRepositoryImpl` / `ProductRepositoryImpl` および対応するMapperの単体テストを追加（`@MybatisTest`使用、`docs/guideline/unit-test.md`準拠でメソッド単位に`@Nested`グルーピング）
  - `build.gradle` — `spring-boot-starter-test` と `mybatis-spring-boot-starter-test` を追加
  - `infrastructure/db/mapper/*MapperTest.java`（Customer, Product, Order, OrderItem）
  - `infrastructure/db/repository/*RepositoryImplTest.java`（Customer, Product, Order）
- [x] `OrderRepositoryImpl#findByCustomerId` のN+1クエリを解消（`OrderItemMapper#findByOrderIds`を追加しIN句で一括取得、`Collectors.groupingBy`で注文ごとに紐付け）
- [x] `OrderRepositoryImpl#save` のcheck-then-actによる競合状態（TOCTOU）を解消（事前の`findById`確認をやめ、`insert`を直接試みて`DuplicateKeyException`発生時に更新へフォールバックする方式に変更）
- [x] `infrastructure/db/record/*Record.java` の行末コメントとJavadoc `@param` の重複を解消（行末コメントを削除しJavadocのみ残す）
- [x] `customers.created_at NOT NULL` に対して顧客登録経路が未実装である点を設計として整理（`docs/design/database.md`に、本サンプルのユースケース（UC-1〜UC-3）が顧客登録を含まずスコープ外である旨を明記）

> 注: 本サンドボックス環境にはJDKが導入されておらず、上記変更についても`./gradlew test`によるビルド・テスト実行確認は未実施。ローカル環境での確認を推奨。

### domain層（2026-07-16）
- [x] `domain/service/OrderDomainService.java` — 在庫確認・引当 → 決済 → `Order#confirm` による確定。在庫不足・決済失敗時は、それまでに引き当てた在庫を`InventoryClient#cancelReservation`で補償ロールバックする
- [x] `domain/service/OrderDomainServiceTest.java` — `InventoryClient`/`PaymentClient`をモック化した単体テスト（`@Nested`でメソッド単位グルーピング）。正常系・在庫不足時のロールバック・決済失敗時のロールバックを検証

> 注: 本サンドボックス環境にはJDKが導入されておらず、`./gradlew test`によるビルド・テスト実行確認は未実施。ローカル環境での確認を推奨。

---

## 残タスク

### application層
- [ ] `application/dto/PlaceOrderDto.java`
- [ ] `application/dto/OrderItemDto.java`
- [ ] `application/dto/OrderResultDto.java`
- [ ] `application/usecase/PlaceOrderUseCase.java` — UC-1: 注文作成
- [ ] `application/usecase/ConfirmOrderUseCase.java` — UC-2: 注文確定
- [ ] `application/usecase/GetOrderUseCase.java` — UC-3: 注文取得

### presentation層
- [ ] `presentation/request/PlaceOrderRequest.java`
- [ ] `presentation/request/OrderItemRequest.java`
- [ ] `presentation/response/OrderResponse.java`
- [ ] `presentation/response/OrderConfirmResponse.java`
- [ ] `presentation/controller/OrderController.java`
- [ ] `presentation/exception/GlobalExceptionHandler.java`
