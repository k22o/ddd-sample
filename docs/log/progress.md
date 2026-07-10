# 実装進捗ログ

最終更新: 2026-07-11

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

---

## 残タスク

### domain層
- [ ] `domain/service/OrderDomainService.java` — 在庫確認・決済の調整

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
