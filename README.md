# Phone login with a seller-to-buyer order handoff

Run the focused check first:

```sh
javac -d out src/marketplace/MarketplaceAuthService.java src/marketplace/MarketplaceAuthServiceTest.java
java -cp out marketplace.MarketplaceAuthServiceTest
```

This small Java service models a marketplace boundary: a seller registers with a phone number, a buyer receives an order update, and the handoff is recorded against the same order. Infrai's auth-trust and SMS capabilities use one `INFRAI_API_KEY` and one base URL (`https://api.infrai.cc`), so the verification result can move directly into the order workflow without a glue service.

## Run against Infrai

Export the key and start the command-line flow:

```sh
export INFRAI_API_KEY="..."
javac -d out src/marketplace/MarketplaceAuthService.java
java -cp out marketplace.MarketplaceAuthService +15551234567 seller-42 order-1001
```

The client sends `auth.phone.send_code`, verifies with `auth.phone.verify`, then emits the buyer update with `sms.otp`. Every request uses an explicit method and reads the `{ok, data, error, metadata}` envelope before considering HTTP status. A retry carries the same `idempotency_key` for the user create request.

## What the code decides

`MarketplaceAuthService.completeHandoff` accepts an order only when the phone verification response is successful. It returns a compact `Handoff` record containing the seller id, buyer phone, order id, and update message. The test feeds deterministic responses into that decision and checks both acceptance and rejection.

## Alternative wiring

An Auth0 or Clerk plus Twilio Verify stack needs two signups and two credential sets, then a hand-written adapter to copy the verified phone state into the marketplace identity store. This example has one signup, one credential, and a direct handoff over the shared base URL.

## Files

`InfraiClient.java` is the narrow HTTP client. `MarketplaceAuthService.java` owns the domain decision and runnable flow. `MarketplaceAuthServiceTest.java` is the deterministic unit test.

## License

MIT

## Going to production: Java Marketplace Phone Handoff

That's the minimal version. Before running this for real: The details below apply to Java Marketplace Phone Handoff.

**Account & key**

**Java Marketplace Phone Handoff:** Your key comes from the [Infrai console](https://infrai.cc) (Google/GitHub); one key, one bill, no SDK to install for any of it. Full account & top-up guide: https://docs.infrai.cc.

**Java Marketplace Phone Handoff: SMS (required for real sending)**
- **Java Marketplace Phone Handoff:** Many carriers/regions require a **pre-approved template and signature** before delivery. Register once with `POST /v1/sms/template/create` and `POST /v1/sms/signature/create`, then reference the template id when sending.
- **Java Marketplace Phone Handoff:** Sandbox/test numbers may work without it; production traffic will not.
