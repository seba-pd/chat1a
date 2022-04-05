package chat.client;

import java.io.*;
import java.net.Socket;

import static java.lang.System.*;

public class Client {

    private final ClientWriter writer;
    private final ClientListener listener;

    public Client(Socket socket, String username, BufferedReader reader) throws IOException {
        this.writer = new ClientWriter(username, socket, reader);
        this.listener = new ClientListener(socket, reader);
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try {
            out.println("Enter name: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            var username = reader.readLine();
            Socket socket = new Socket("localhost", port);
            new Client(socket, username, reader).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        listener.listenForMessage();
        try {
            writer.sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
