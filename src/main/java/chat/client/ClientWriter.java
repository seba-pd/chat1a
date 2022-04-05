package chat.client;

import chat.util.FileUtil;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;

public class ClientWriter {

    private final PrintWriter printWriter;
    private final DataOutputStream dataOutputStream;
    private final String clientName;
    private final Socket socket;
    private final BufferedReader reader;

    public ClientWriter(String clientName, Socket socket, BufferedReader reader) throws IOException {
        this.socket = socket;
        this.printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.clientName = clientName;
        this.reader = reader;
    }

 /*   private void shutDown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            printWriter.println("/ec");
            printWriter.println("/exit");
        }));
    }*/

    public void sendMessage() throws IOException {
        printWriter.println(clientName);
        while (!socket.isClosed()) {;
            String message = reader.readLine();
            if (message.equals("/sf")) {
                sendFile(message);
            } else {
                printWriter.println(message);
            }
        }
        close();
    }

    private void sendFile(String message) {
        try {
            printWriter.println(message);
            System.out.println("Enter the file name: ");
            printWriter.println(reader.readLine());
            System.out.println("Enter receiver name: ");
            printWriter.println(reader.readLine());
            System.out.println("Enter path to the file: ");
            String path = reader.readLine();
            FileUtil.sendfile(Path.of(path), dataOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(){
        try {
            reader.close();
            printWriter.close();
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
