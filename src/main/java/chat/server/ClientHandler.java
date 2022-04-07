package chat.server;

import chat.util.FileHistoryUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReadWriteLock;

@Setter
@Getter
public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String clientName;
    private Channel actualChannel;
    private Channels channels;
    private UIResolver ui;
    private ReadWriteLock lock;

    public ClientHandler(Socket socket, Channels channels, ReadWriteLock lock) {

        try {
            this.socket = socket;
            this.lock = lock;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            this.channels = channels;
            this.clientName = bufferedReader.readLine();
            this.ui = new UIResolver(printWriter, clientName);
            ui.welcome();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                ui.showMainOption();
                selectOption(bufferedReader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        close();
    }

    private void selectOption(String option) throws IOException {
        switch (option) {
            case "1" -> showChannels();
            case "2" -> addNewChannel();
            case "3" -> selectChannel();
            case "4" -> exitChat();
            default -> wrongCommand();
        }
    }

    private void showChannels() {
        channels.getAllChannels().forEach(channel -> printWriter.println(channel));
    }

    private void addNewChannel() throws IOException {
        String channel;
        while (channels.isPresent(channel = bufferedReader.readLine())) {
            printWriter.println("Channel already exist!");
        }
        channels.addChannel(new Channel(channel,lock));
        FileHistoryUtil.createHistoryFile(actualChannel.getChannelName());
    }

    private void selectChannel() throws IOException {
        String selectedChannel;
        printWriter.println("Enter channel name.");
        while (!channels.isPresent(selectedChannel = bufferedReader.readLine())) {
            printWriter.println("Wrong channel name!");
        }
        enterClientToChannel(selectedChannel);
        chattingOnChannel();
        exitClientFromChannel();
    }

    private void exitChat() throws IOException {
        printWriter.println("/exit");
        System.out.println("Connection disconnect " + socket.toString());
        socket.close();
    }

    private void wrongCommand() {
        printWriter.println("Wrong command!");
    }

    private void getHistory() {
        actualChannel.getHistory(this).forEach(s -> printWriter.println(s));
    }

    private void showChannelClients() {
        actualChannel.getClientNames().forEach(c -> printWriter.println(c));
    }

    private void sendFileToClient() throws IOException {
        String fileName = bufferedReader.readLine();
        String receiverName = bufferedReader.readLine();
        try (DataOutputStream receiverDataOutputStream = new DataOutputStream(actualChannel.getClient(receiverName).getDataOutputStream());
             PrintWriter receiverPrintWriter = new PrintWriter(actualChannel.getClient(receiverName).getPrintWriter(), true)) {
            receiverPrintWriter.println("/file");
            receiverPrintWriter.println(this.clientName + " send a file :" + fileName);
            dataInputStream.transferTo(receiverDataOutputStream);
        }
    }

    private void enterClientToChannel(String channelName) {
        actualChannel = channels.selectChannel(channelName);
        actualChannel.addClient(this);
    }

    private void exitClientFromChannel() {
        actualChannel.removeClient(this);
    }

    private void chattingOnChannel() throws IOException {
        ui.showChannelOption();
        String message;
        while (!(message = bufferedReader.readLine()).equals("/ec")) {
            switch (message) {
                case "/sf" -> sendFileToClient();
                case "/sh" -> getHistory();
                case "/sc" -> showChannelClients();
                default -> actualChannel.broadcast(message, this.clientName);
            }
        }
    }

    private void close() {
        try {
            printWriter.close();
            bufferedReader.close();
            dataOutputStream.close();
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
