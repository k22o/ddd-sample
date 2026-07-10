# ディレクトリ構成

```
src/main/java/com/example/dddsample/
│
├── presentation/
│   ├── controller/
│   │   └── OrderController.java
│   ├── request/
│   │   ├── PlaceOrderRequest.java
│   │   └── OrderItemRequest.java
│   ├── response/
│   │   ├── OrderResponse.java
│   │   └── OrderConfirmResponse.java
│   └── exception/
│       └── GlobalExceptionHandler.java
│
├── application/
│   ├── usecase/
│   │   ├── PlaceOrderUseCase.java       # UC-1: 注文作成
│   │   ├── ConfirmOrderUseCase.java     # UC-2: 注文確定
│   │   └── GetOrderUseCase.java         # UC-3: 注文取得
│   └── dto/
│       ├── PlaceOrderDto.java
│       ├── OrderItemDto.java
│       └── OrderResultDto.java
│
├── domain/
│   ├── model/
│   │   ├── order/
│   │   │   ├── Order.java               # 集約ルート
│   │   │   ├── OrderItem.java           # 集約内 Entity
│   │   │   ├── OrderId.java             # Value Object
│   │   │   ├── OrderItemId.java         # Value Object
│   │   │   └── OrderStatus.java         # Enum
│   │   ├── customer/
│   │   │   ├── Customer.java
│   │   │   └── CustomerId.java          # Value Object
│   │   ├── product/
│   │   │   ├── Product.java
│   │   │   └── ProductId.java           # Value Object
│   │   └── shared/
│   │       ├── Money.java               # Value Object
│   │       ├── Address.java             # Value Object
│   │       └── Quantity.java            # Value Object
│   ├── repository/
│   │   ├── OrderRepository.java         # interface
│   │   ├── CustomerRepository.java      # interface
│   │   └── ProductRepository.java       # interface
│   ├── client/
│   │   ├── PaymentClient.java           # interface
│   │   └── InventoryClient.java         # interface
│   ├── service/
│   │   └── OrderDomainService.java      # 在庫確認・決済の調整
│   └── specification/
│       └── OrderCancellableSpecification.java
│
└── infrastructure/
    ├── db/
    │   ├── record/                               # MyBatisのSELECT結果を受け取るPOJO
    │   │   ├── OrderRecord.java
    │   │   ├── OrderItemRecord.java
    │   │   ├── CustomerRecord.java
    │   │   └── ProductRecord.java
    │   ├── mapper/                               # MyBatis @Mapper interface（SQL定義）
    │   │   ├── OrderMapper.java
    │   │   ├── OrderItemMapper.java
    │   │   ├── CustomerMapper.java
    │   │   └── ProductMapper.java
    │   └── repository/
    │       ├── OrderRepositoryImpl.java          # OrderRepository の実装（Record ↔ Domain 変換も担当）
    │       ├── CustomerRepositoryImpl.java
    │       └── ProductRepositoryImpl.java
    └── client/
        ├── PaymentClientImpl.java                # PaymentClient の実装
        ├── InventoryClientImpl.java              # InventoryClient の実装
        └── dto/
            ├── PaymentRequest.java
            ├── PaymentResponse.java
            ├── InventoryReserveRequest.java
            └── InventoryReserveResponse.java
```

---

## データの詰め替え

| 変換 | 担当層 | 担当クラス |
|------|--------|-----------|
| Request → Command | Presentation | Controller |
| Dto → Domain | Application | UseCase |
| Domain → Dto | Application | UseCase |
| Dto → Response | Presentation | Controller |
| Record ↔ Domain | Infrastructure/db | RepositoryImpl（インライン） |
| External API ↔ Domain | Infrastructure/client | ClientImpl |
