import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;

public class BlockingRequestsTest {
    static DNS dnsServer;
    static BankServer server1;
    static BankServer server2;
    static final int DNS_PORT = 8090;


    @BeforeClass
    public static void createServers_B() throws IOException {
        dnsServer = new DNS(DNS_PORT);
        assertEquals(-1, dnsServer.getBankServerPort("mellat"));
        server1 = new BankServer("mellat", DNS_PORT);
        assertNotEquals(-1, dnsServer.getBankServerPort("mellat"));
        server2 = new BankServer("melli", DNS_PORT);
    }

    @Test
    public void testClientCreation_B() throws IOException {
        int priorNumberOfClients = server2.getNumberOfConnectedClients();
        new BankClient("melli", DNS_PORT);
        assertEquals(priorNumberOfClients + 1, server2.getNumberOfConnectedClients());
        //...
    }

    @Test
    public void testSingleServerSingleClient_B() throws IOException {
        BankClient client1 = new BankClient("mellat", DNS_PORT);
        assertEquals(0, server1.getBalance(111));
        assertEquals(0, server1.getBalance(222));
        client1.sendTransaction(111, +7);
        client1.sendTransaction(222, +7);
        client1.sendTransaction(111, +5);
        assertEquals(7, server1.getBalance(222));
        assertEquals(12, server1.getBalance(111));
        //...
    }


    @Test
    public void testSingleServerMultiClient_B() throws IOException {
        BankClient client1 = new BankClient("mellat", DNS_PORT);
        BankClient client2 = new BankClient("mellat", DNS_PORT);
        BankClient client3 = new BankClient("mellat", DNS_PORT);
        BankClient client4 = new BankClient("mellat", DNS_PORT);
        BankClient client5 = new BankClient("mellat", DNS_PORT);
        client1.sendTransaction(111, +7);
        client2.sendTransaction(111, -5);
        client3.sendTransaction(111, +5);
        client4.sendTransaction(111, -6);
        assertEquals(1, server1.getBalance(111));
//        assertEquals(6, server1.getBalance(222));
//        client1.sendTransaction(111, +7);
//        client1.sendTransaction(222, +7);
//        client1.sendTransaction(111, +5);

    }
}
