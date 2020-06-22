import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class DNS {

    private ServerSocket dnsSocket;
    private HashMap<String, Integer> banksAndPorts;

    public DNS(int dnsPort) throws IOException {
        dnsSocket = new ServerSocket(dnsPort);
        banksAndPorts = new HashMap<>();
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

    private void runServer() throws IOException {
        while (true) {
            getResponse();
        }
    }

    private void handleAction(String incomeMessage, DataOutputStream dataOutputStream) throws IOException {
        if (incomeMessage.startsWith("server")) {
            addBankServer(incomeMessage, dataOutputStream);
        } else if (incomeMessage.startsWith("client")) {
            navigateBankClient(incomeMessage, dataOutputStream);
        }
    }

    private void navigateBankClient(String incomeMessage, DataOutputStream dataOutputStream) throws IOException {
        String[] incomeSplit = incomeMessage.split(":");
        Integer bankPort;
        String bankName = incomeSplit[1];
        bankPort = banksAndPorts.get(bankName);
        dataOutputStream.writeUTF("DNS:" + bankPort);
        dataOutputStream.flush();
    }

    private void addBankServer(String incomeMessage, DataOutputStream dataOutputStream) throws IOException {
        String[] incomeSplit = incomeMessage.split(":");
        Integer bankPort = Integer.parseInt(incomeSplit[1]);
        String bankName = incomeSplit[2];
        banksAndPorts.put(bankName, bankPort);
        dataOutputStream.writeUTF("done");
        dataOutputStream.flush();
    }

    private void getResponse() throws IOException {
        Socket clientSocket = dnsSocket.accept();
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        String incomeMessage = dataInputStream.readUTF();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        handleAction(incomeMessage, dataOutputStream);
    }

    public int getBankServerPort(String bankName) {
        if (banksAndPorts.containsKey(bankName))
            return banksAndPorts.get(bankName);
        return -1;
    }
}
