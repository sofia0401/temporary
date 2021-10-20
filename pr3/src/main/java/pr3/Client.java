package pr3;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket clientSocket; //сокет для общения
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    private String clientUsername;

    public Client(Socket clientSocket, String clientUsername) {
        try {
            this.clientSocket = clientSocket;
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.clientUsername = clientUsername;

        } catch (IOException e) {
            closeEverything(clientSocket, in, out);
        }
    }

    public void sendMessage() {
        try {
            out.write(clientUsername);
            out.newLine();
            out.flush();

            Scanner scanner = new Scanner(System.in);
            while (clientSocket.isConnected()) {
                String messageToSend = scanner.nextLine();
                out.write(clientUsername + ": " + messageToSend);
                out.newLine();
                out.flush();

            }
        } catch (IOException e) {
            closeEverything(clientSocket, in, out);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (clientSocket.isConnected()) {
                    try {
                        msgFromGroupChat = in.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(clientSocket, in, out);
                    }
                }
            }
        }).start();

    }

    public void closeEverything(Socket clientSocket, BufferedReader in, BufferedWriter out) {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String clientUsername = scanner.nextLine();
        Socket clientSocket = new Socket("localhost", 4040);
        Client client = new Client(clientSocket, clientUsername);
        client.listenForMessage();
        client.sendMessage();


    }
}