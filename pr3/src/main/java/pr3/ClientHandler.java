package pr3;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public ArrayList<String> messages = new ArrayList<>();
    private Socket clientSocket;
    private BufferedReader in; // поток чтения из сокета

    private BufferedWriter out; // поток записи в сокет
    private String clientUsername;

    public ClientHandler(Socket clientSocket) throws InterruptedException {
        try {
            this.clientSocket = clientSocket;
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientUsername = in.readLine();
            clientHandlers.add(this);

            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");

        } catch (IOException e) {
            closeEverything(clientSocket, in, out);
        }
    }



//    long time = System.nanoTime();

    @Override
    public void run() { //принимаем сообщение от клиента

//        long time = System.nanoTime();
//        while ((TimeUnit.NANOSECONDS.convert((System.nanoTime() - time), TimeUnit.NANOSECONDS)) <= 5000) {
            String messageFromClient;
            while (clientSocket.isConnected()) {
//                System.out.println(TimeUnit.NANOSECONDS.convert((System.nanoTime() - time), TimeUnit.NANOSECONDS));
                try {
                    messageFromClient = in.readLine();
                    messages.add(messageFromClient);
//                broadcastMessage(messageFromClient);
                } catch (IOException e) {

                    closeEverything(clientSocket, in, out);
//                mailing(messages);
                    break;
                }
                mailing(messages);
            }





    }


    long timeToSleep = 5L;
    public void mailing(ArrayList<String> msgs) {

        TimeUnit unit = TimeUnit.SECONDS;
        try {
            unit.sleep(timeToSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messages.forEach(message->{
            broadcastMessage(message);
        });

    }

    public void broadcastMessage(String messageToSend) {
//        unit.sleep(duration);
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.out.write(messageToSend);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            } catch (IOException e) {
                closeEverything(clientSocket,in,out);

            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: "+ clientUsername + " has left the chat");

    }

        public void closeEverything(Socket clientSocket, BufferedReader in, BufferedWriter out) {
        removeClientHandler();
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



}
