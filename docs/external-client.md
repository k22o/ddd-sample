# 外部APIクライアント設計

## 概要

| クライアント | 役割 | 呼び出しタイミング |
|-------------|------|------------------|
| PaymentClient | 決済処理 | 注文確定時・キャンセル時 |
| InventoryClient | 在庫確認・引当 | 注文確定時・キャンセル時 |

---

## PaymentClient

### Domain Interface

```java
interface PaymentClient {
    PaymentResult charge(CustomerId customerId, Money amount);
    void cancel(String paymentId);
}
```

### 外部API仕様（モック想定）

#### 決済実行
`POST /payments`

```json
// Request
{
  "customerId": "c1d2e3f4-...",
  "amount": 3000,
  "currency": "JPY"
}

// Response 200
{
  "paymentId": "pay_abc123",
  "status": "SUCCEEDED"
}

// Response 402 (決済失敗)
{
  "error": "PAYMENT_FAILED",
  "message": "残高不足"
}
```

#### 決済キャンセル
`DELETE /payments/{paymentId}`

```json
// Response 200
{
  "paymentId": "pay_abc123",
  "status": "CANCELLED"
}
```

---

## InventoryClient

### Domain Interface

```java
interface InventoryClient {
    boolean checkStock(ProductId productId, Quantity quantity);
    String reserve(ProductId productId, Quantity quantity);   // 引当ID を返す
    void cancelReservation(String reservationId);
}
```

### 外部API仕様（モック想定）

#### 在庫確認
`GET /inventory/{productId}?quantity={quantity}`

```json
// Response 200
{
  "productId": "p1d2e3f4-...",
  "available": true,
  "stock": 10
}
```

#### 在庫引当
`POST /inventory/reservations`

```json
// Request
{
  "productId": "p1d2e3f4-...",
  "quantity": 2
}

// Response 201
{
  "reservationId": "rsv_xyz789",
  "productId": "p1d2e3f4-...",
  "quantity": 2
}

// Response 409 (在庫不足)
{
  "error": "INSUFFICIENT_STOCK",
  "message": "在庫が不足しています"
}
```

#### 引当キャンセル
`DELETE /inventory/reservations/{reservationId}`

```json
// Response 200
{
  "reservationId": "rsv_xyz789",
  "status": "CANCELLED"
}
```

---

## 注文確定時のフロー

```
PlaceOrderUseCase.confirm(orderId)
  │
  ├─ InventoryClient.checkStock(productId, quantity)  ← 在庫確認
  │    └─ 在庫不足 → InsufficientStockException
  │
  ├─ InventoryClient.reserve(productId, quantity)     ← 在庫引当
  │
  ├─ PaymentClient.charge(customerId, totalAmount)    ← 決済
  │    └─ 失敗 → InventoryClient.cancelReservation() → PaymentFailedException
  │
  └─ OrderRepository.save(order)                      ← DB保存（status=CONFIRMED）
```

