import java.io.IOException;

public class ServerMain {
    static DNS dnsServer;
    static BankServer server1;
    static BankServer server2;
    static final int DNS_PORT = 8090;

    public static void main(String[] args) throws IOException {
        dnsServer = new DNS(DNS_PORT);
        server1 = new BankServer("mellat", DNS_PORT);
        server2 = new BankServer("melli", DNS_PORT);

        BankClient client1 = new BankClient("mellat", DNS_PORT);
        client1.sendAllTransactions("testA", 0);
        System.out.println(server1.getBalance(11111));

    }
}
