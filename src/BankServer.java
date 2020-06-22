import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class BankServer {

    private Socket dnsSocket;
    private HashMap<Integer, Integer> customerIdsAndBalance;
    private int connectedClientsCount;
    private ServerSocket bankServer;


    public BankServer(String bankName, int dnsPort) throws IOException {
        customerIdsAndBalance = new HashMap<>();
        dnsSocket = new Socket("127.0.0.1", dnsPort);
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(dnsSocket.getOutputStream()));
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(dnsSocket.getInputStream()));
        bankServer = new ServerSocket(0);
        dataOutputStream.writeUTF("server:" + bankServer.getLocalPort() + ":" + bankName);
        dataOutputStream.flush();
        if (dataInputStream.readUTF().equals("done")) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        runServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void runServer() throws IOException {
        while (true) {
            Socket clientSocket = bankServer.accept();
            DataInputStream clientInputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            DataOutputStream clientOutputStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            new Thread(new Runnable() {
                public void run() {
                    handleClient(clientInputStream, clientOutputStream);
                }
            }).start();
            connectedClientsCount++;
            clientOutputStream.writeUTF("done");
            clientOutputStream.flush();
        }
    }

    public int getBalance(int userId) {
        if (customerIdsAndBalance.containsKey(userId))
            return customerIdsAndBalance.get(userId);
        return 0;
    }

    public int getNumberOfConnectedClients() {
        return connectedClientsCount;
    }


    private void handleClient(DataInputStream clientInputStream, DataOutputStream clientOutputStream) {
        String input;
        try {
            while (true) {
                input = clientInputStream.readUTF();
                String[] inputSplit = input.split("::");
                int userId = Integer.parseInt(inputSplit[0]);
                int amount = Integer.parseInt(inputSplit[1]);
                doTransaction(userId, amount);
                clientOutputStream.writeUTF("done");
                clientOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void doTransaction(int userId, int amount) {
        if (!customerIdsAndBalance.containsKey(userId)) {
            if (amount < 0)
                return;
            registerUser(userId);
        }
        int oldCurrency = customerIdsAndBalance.get(userId);
        if (oldCurrency + amount >= 0) {
            customerIdsAndBalance.replace(userId, oldCurrency + amount);
        }
    }

    private void registerUser(int userId) {
        customerIdsAndBalance.put(userId, 0);
    }
}

