package marketplace;

public final class MarketplaceAuthServiceTest {
    public static void main(String[] args) {
        MarketplaceAuthService service = new MarketplaceAuthService();
        MarketplaceAuthService.Handoff handoff = service.completeHandoff("seller-42", "+15551234567", "order-1001", "{\"ok\":true,\"data\":{}}");
        if (!handoff.message().contains("order-1001")) throw new AssertionError("accepted handoff lost order id");
        try {
            service.completeHandoff("seller-42", "+15551234567", "order-1001", "{\"ok\":false,\"error\":{}}");
            throw new AssertionError("rejected verification was accepted");
        } catch (IllegalArgumentException expected) {
            System.out.println("MarketplaceAuthServiceTest passed");
        }
    }
}
