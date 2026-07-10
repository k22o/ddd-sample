# REST API 設計

Base URL: `/api/v1`

---

## 注文 API

### 注文作成
`POST /orders`

**Request Body**
```json
{
  "customerId": "c1d2e3f4-...",
  "shippingAddress": {
    "postalCode": "100-0001",
    "prefecture": "東京都",
    "city": "千代田区",
    "street": "千代田1-1"
  },
  "items": [
    {
      "productId": "p1d2e3f4-...",
      "quantity": 2
    }
  ]
}
```

**Response** `201 Created`
```json
{
  "orderId": "o1d2e3f4-...",
  "status": "PENDING",
  "totalAmount": {
    "amount": 3000,
    "currency": "JPY"
  },
  "createdAt": "2026-07-10T10:00:00"
}
```

---

### 注文確定
`POST /orders/{orderId}/confirm`

在庫確認 → 決済処理 → ステータスを `CONFIRMED` に更新。

**Response** `200 OK`
```json
{
  "orderId": "o1d2e3f4-...",
  "status": "CONFIRMED",
  "paymentId": "pay_abc123"
}
```

**エラー**
| HTTP Status | 理由 |
|-------------|------|
| 404 | 注文が存在しない |
| 409 | 在庫不足 |
| 402 | 決済失敗 |

---

### 注文取得
`GET /orders/{orderId}`

**Response** `200 OK`
```json
{
  "orderId": "o1d2e3f4-...",
  "customerId": "c1d2e3f4-...",
  "status": "CONFIRMED",
  "shippingAddress": {
    "postalCode": "100-0001",
    "prefecture": "東京都",
    "city": "千代田区",
    "street": "千代田1-1"
  },
  "items": [
    {
      "productId": "p1d2e3f4-...",
      "productName": "サンプル商品",
      "unitPrice": { "amount": 1500, "currency": "JPY" },
      "quantity": 2,
      "subtotal": { "amount": 3000, "currency": "JPY" }
    }
  ],
  "totalAmount": { "amount": 3000, "currency": "JPY" },
  "createdAt": "2026-07-10T10:00:00"
}
```

