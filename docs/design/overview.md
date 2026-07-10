# 設計概要：ECサイト注文管理

## ユースケース

| # | ユースケース | 説明 |
|---|-------------|------|
| UC-1 | 注文作成 | 顧客が商品を選択し、注文を作成する |
| UC-2 | 注文確定 | 在庫確認 → 決済処理 → 注文を確定状態にする |
| UC-3 | 注文取得 | 注文IDで注文詳細を取得する |

---

## ドメインモデル

### Aggregate

#### Order（集約ルート）
| フィールド | 型 | 説明 |
|-----------|-----|------|
| id | OrderId | 注文ID（Value Object） |
| customerId | CustomerId | 顧客ID（Value Object） |
| status | OrderStatus | PENDING / CONFIRMED / CANCELLED |
| items | List\<OrderItem\> | 注文明細（集約内Entity） |
| totalAmount | Money | 合計金額（Value Object） |
| shippingAddress | Address | 配送先（Value Object） |
| paymentId | String | 外部決済サービスの支払いID |
| createdAt | LocalDateTime | 注文日時 |

**ドメインルール（Order内）:**
- 注文明細は1件以上必須
- `CANCELLED` 状態からの遷移は不可
- `CONFIRMED` 状態はキャンセル不可
- `totalAmount` は items から自動計算

#### OrderItem（Order集約内のEntity）
| フィールド | 型 | 説明 |
|-----------|-----|------|
| id | OrderItemId | 明細ID（Value Object） |
| productId | ProductId | 商品ID（Value Object） |
| productName | String | 注文時の商品名（スナップショット） |
| unitPrice | Money | 注文時の単価（スナップショット） |
| quantity | Quantity | 数量（Value Object） |

#### Customer（集約）
| フィールド | 型 | 説明 |
|-----------|-----|------|
| id | CustomerId | 顧客ID |
| name | String | 氏名 |
| email | String | メールアドレス |
| address | Address | 住所 |

#### Product（参照用集約）
| フィールド | 型 | 説明 |
|-----------|-----|------|
| id | ProductId | 商品ID |
| name | String | 商品名 |
| price | Money | 価格 |
| description | String | 説明 |

---

### Value Object

| 名前 | フィールド | 説明 |
|------|-----------|------|
| OrderId | value: String | UUIDベース |
| CustomerId | value: String | UUIDベース |
| ProductId | value: String | UUIDベース |
| OrderItemId | value: String | UUIDベース |
| Money | amount: BigDecimal, currency: String | 金額。負値不可 |
| Address | postalCode, prefecture, city, street | 住所 |
| Quantity | value: int | 数量。1以上の正整数 |

---

### Domain Service

#### OrderDomainService
注文確定時に複数の集約・外部サービスにまたがる処理を担当。

```
confirmOrder(order, inventoryClient, paymentClient):
  1. 各 OrderItem の在庫を InventoryClient で確認・引当
  2. PaymentClient で決済処理
  3. Order の status を CONFIRMED に遷移
```

---

### Repository Interface（domain層に定義）

```java
interface OrderRepository {
    Order findById(OrderId id);
    List<Order> findByCustomerId(CustomerId customerId);
    void save(Order order);
}

interface CustomerRepository {
    Customer findById(CustomerId id);
}

interface ProductRepository {
    Product findById(ProductId id);
    List<Product> findByIds(List<ProductId> ids);
}
```

---

### Client Interface（domain層に定義）

```java
interface PaymentClient {
    PaymentResult charge(CustomerId customerId, Money amount);
    void cancel(String paymentId);
}

interface InventoryClient {
    boolean checkStock(ProductId productId, Quantity quantity);
    String reserve(ProductId productId, Quantity quantity);   // 引当ID を返す
    void cancelReservation(String reservationId);
}
```

---

## レイヤー構成と依存関係

```
Presentation → Application → Domain ← Infrastructure
                                  ↑
                          (Repository/Client の実装)
```

Infrastructure の Repository/Client 実装が、Domain で定義した interface を implements することで依存関係を逆転（DIP）。
