import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class BankClient {

//    private static final String PATH = "./src/";          // intellij
    private static final String PATH = "./src/main/java/";  // quera

    private Socket bankSocket;
    private DataOutputStream serverOutputStream;
    private DataInputStream serverInputStream;

    public BankClient(String bankName, int dnsServerPort) throws IOException {

        Socket dnsSocket = new Socket("127.0.0.1", dnsServerPort);
        DataOutputStream dnsOutputStream = new DataOutputStream(new BufferedOutputStream(dnsSocket.getOutputStream()));
        DataInputStream dnsInputStream = new DataInputStream(new BufferedInputStream(dnsSocket.getInputStream()));
        dnsOutputStream.writeUTF("client:" + bankName);
        dnsOutputStream.flush();
        String bankServerPortString = dnsInputStream.readUTF().split(":")[1];
        bankSocket = new Socket("127.0.0.1", Integer.parseInt(bankServerPortString));
        serverInputStream = new DataInputStream(new BufferedInputStream(bankSocket.getInputStream()));
        if (serverInputStream.readUTF().equals("done"))
            serverOutputStream = new DataOutputStream(new BufferedOutputStream(bankSocket.getOutputStream()));
    }


    public void sendTransaction(int userId, int amount) {
        String message = userId + "::" + amount ;
        try {
            serverOutputStream.writeUTF(message);
            serverOutputStream.flush();
            serverInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendAllTransactions(String fileName, final int timeBetweenTransactions) {
        final File file = new File(PATH + fileName);
        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String[] transaction = fileScanner.nextLine().split("\\s+");
                int accountNum = Integer.parseInt(transaction[0]);
                int transactionAmount = Integer.parseInt(transaction[1]);
                sendTransaction(accountNum, transactionAmount);
                if (timeBetweenTransactions > 0) {
                    try {
                        sleep(timeBetweenTransactions);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
