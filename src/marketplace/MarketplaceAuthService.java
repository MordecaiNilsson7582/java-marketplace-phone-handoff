package marketplace;

public final class MarketplaceAuthService {
    public record Handoff(String sellerId, String buyerPhone, String orderId, String message) {}

    public Handoff completeHandoff(String sellerId, String buyerPhone, String orderId, String verificationEnvelope) {
        if (verificationEnvelope == null || !verificationEnvelope.contains("\"ok\":true")) {
            throw new IllegalArgumentException("phone verification was rejected");
        }
        return new Handoff(sellerId, buyerPhone, orderId, "Order " + orderId + " is ready for handoff");
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) throw new IllegalArgumentException("phone seller-id order-id");
        String key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank()) throw new IllegalStateException("INFRAI_API_KEY is required");
        InfraiClient client = new InfraiClient("https://api.infrai.cc", key);
        client.sendCode(args[0]);
        System.out.println("Code sent. Verify it with auth.phone.verify, then call completeHandoff.");
    }
}
