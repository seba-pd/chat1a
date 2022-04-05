package chat.client;

import chat.util.FileUtil;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientListener {

    private final BufferedReader socketReader;
    private final Socket socket;
    private final DataInputStream dataInputStream;
    private final BufferedReader reader;


    public ClientListener(Socket socket,BufferedReader reader) throws IOException {
        this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.socket = socket;
        this.reader = reader;
    }

    public void listenForMessage() {
        new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    String message = socketReader.readLine();
                    switch (message){
                        case "/file" -> receiveFile();
                        case "/exit" -> exit();
                        default -> System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void exit() throws IOException {
        System.out.println("Bye");
        dataInputStream.close();
        socketReader.close();
        reader.close();
        socket.close();
    }

    private void receiveFile() {
        try {
            System.out.println(socketReader.readLine());
            System.out.println("Enter path to save file : ");
            FileUtil.receiveFile(reader.readLine(), dataInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
