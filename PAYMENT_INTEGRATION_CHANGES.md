# Payment Integration & Currency Support Summary

This document outlines the changes made to integrate Midtrans payment gateway and add multi-currency support with automatic conversion.

## ✅ Changes Completed

### 1. **Currency Support**
- Added `currency` field to `Product`, `Order`, and `OrderItem` entities (default: "IDR")
- Updated sample product prices from USD to IDR (Indonesian Rupiah)
  - REAL SHYT Hoodie: Rp 1,440,000
  - Graffiti Tee: Rp 736,000
  - Urban Joggers: Rp 1,280,000
  - Street Cap: Rp 576,000
  - Smoke Jacket: Rp 2,080,000
  - Drip Shorts: Rp 960,000

### 2. **Currency Conversion Service**
- Created `CurrencyConversionService.java` with:
  - Automatic conversion between 12+ currencies and IDR
  - Support for USD, EUR, GBP, SGD, MYR, AUD, JPY, CNY, HKD, KRW, THB, IDR
  - Round-trip conversion (any currency → IDR → any currency)
  - Region-based currency detection

### 3. **Complete DTOs**
- **PaymentRequest.java** - Already complete with customer details
- **PaymentResponse.java** - Completed with all payment details including snap token
- **MidtransNotification.java** - Completed with all Midtrans webhook fields

### 4. **Payment Service Improvements**
- Added currency conversion for multi-region users
- Fixed RestTemplate bean injection
- Improved error handling and logging
- Automatic conversion of prices to IDR for Midtrans API
- Returns display amount in user's local currency

### 5. **Configuration Updates**
- Added Midtrans API configuration to `application.properties`
- Created RestTemplate bean in `SecurityConfig.java`
- Fixed deprecated frameOptions() method in SecurityConfig
- Updated Order status enum to include `PENDING_PAYMENT`

### 6. **Dependencies**
All required dependencies are already present in `build.gradle`:
- `spring-boot-starter-web` (for RestTemplate)
- `spring-boot-starter-webflux` (for HTTP client)
- `jackson-databind` (for JSON processing)
- `commons-codec` (for signature verification)

## 🔧 Configuration Required

Update `application.properties` with your Midtrans credentials:

```properties
# Get these from https://dashboard.sandbox.midtrans.com/
midtrans.server-key=SB-Mid-server-YOUR_ACTUAL_KEY_HERE
midtrans.client-key=SB-Mid-client-YOUR_ACTUAL_KEY_HERE
midtrans.merchant-id=YOUR_MERCHANT_ID

# For production
midtrans.is-production=true
midtrans.api-url=https://app.midtrans.com/snap/v1/transactions
```

## 📋 API Endpoints

### Create Payment
```bash
POST /api/payments/create
Content-Type: application/json

{
  "orderId": 1,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "081234567890"
}
```

**Response:**
```json
{
  "paymentId": 1,
  "orderId": 1,
  "midtransOrderId": "ORDER-1-1234567890",
  "snapToken": "a1b2c3d4e5f6...",
  "snapRedirectUrl": "https://app.sandbox.midtrans.com/...",
  "amount": 1440000.00,
  "status": "PENDING",
  "message": "Payment created successfully"
}
```

### Handle Notification (Webhook)
```bash
POST /api/payments/notification
Content-Type: application/json

{ Midtrans webhook payload }
```

### Get Payment Status
```bash
GET /api/payments/order/{orderId}
```

## 🌍 Currency Conversion

The system automatically:
1. Detects user's currency preference (default: IDR)
2. Converts prices from user's currency to IDR for Midtrans
3. Returns display amount in user's local currency
4. Stores payment in IDR in database

### Supported Conversion Rates
- IDR (Indonesian Rupiah) - Base currency
- USD: 1 USD ≈ 16,000 IDR
- EUR: 1 EUR ≈ 17,300 IDR
- GBP: 1 GBP ≈ 20,200 IDR
- SGD: 1 SGD ≈ 11,800 IDR
- MYR: 1 MYR ≈ 3,400 IDR
- AUD: 1 AUD ≈ 10,500 IDR
- JPY: 1 JPY ≈ 105 IDR
- And more...

## 🚀 How It Works

1. **User places order** with their currency preference
2. **Create payment** - converts order amount to IDR
3. **Midtrans API** - processes payment in IDR
4. **Display to user** - shows amount in their local currency
5. **Webhook notification** - updates payment status automatically

## 📝 Database Changes

New columns added:
- `products.currency` (VARCHAR(3), default: 'IDR')
- `orders.currency` (VARCHAR(3), default: 'IDR')
- `order_items.currency` (VARCHAR(3), default: 'IDR')
- `orders.total_price` (computed from total_amount for backward compatibility)

## ⚠️ Important Notes

1. **Currency rates** in `CurrencyConversionService` are approximate. For production, use a real-time currency API like OpenExchangeRates or Fixer.io

2. **Midtrans credentials** must be updated in `application.properties` before testing

3. **Webhook URL** in Midtrans dashboard should be: `http://your-domain.com/api/payments/notification`

4. **Testing** - Use Midtrans Sandbox credentials for development

5. **Production deployment** - Update `midtrans.is-production=true` and use production credentials

## 🔒 Security

- Signature verification implemented in `handleMidtransNotification()`
- Basic authentication for Midtrans API
- CORS configured for frontend access

## 📊 Order Status Flow

1. **PENDING** - Order created
2. **PENDING_PAYMENT** - Payment initiated
3. **PROCESSING** - Payment successful, order processing
4. **SHIPPED** - Order shipped
5. **DELIVERED** - Order delivered
6. **CANCELLED** - Order cancelled

## ✅ Testing Checklist

- [ ] Update Midtrans credentials in application.properties
- [ ] Test payment creation endpoint
- [ ] Verify currency conversion works
- [ ] Test Midtrans webhook notification
- [ ] Verify payment status updates
- [ ] Test with different currencies (USD, EUR, etc.)
- [ ] Verify database schema updates applied

---

**Last Updated:** December 2024
**Developer:** Realshyt Team

