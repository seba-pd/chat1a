package chat.server;

import chat.util.FileHistoryUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.List;

@Setter
@Getter
public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private String clientName;
    private Channel actualChannel;
    private Channels channels;
    private UIResolver ui;
    private ChattingOnChannelService chattingOnChannelResolver;
    private List<ClientHandler> clientList;

    public ClientHandler(Socket socket, Channels channels, List<ClientHandler> clientList) {
            this.socket = socket;
            this.channels = channels;
            this.clientList = clientList;
    }

    @Override
    @SneakyThrows
    public void run() {
        start();
        while (!socket.isClosed()) {
                ui.showMainOption();
                selectOption(bufferedReader.readLine());
        }
        close();
    }

    @SneakyThrows
    private void start(){
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        this.chattingOnChannelResolver = new ChattingOnChannelService(this);
        this.ui = new UIResolver(printWriter);
        this.clientName = ui.selectName(bufferedReader, clientList);
        ui.welcome(clientName);
    }

    private void selectOption(String option) {
        switch (option) {
            case "1" -> showChannels();
            case "2" -> addNewChannel();
            case "3" -> chatOnChannel();
            case "4" -> exitChat();
            default -> wrongCommand();
        }
    }

    private void showChannels() {
        channels.getAllChannels().forEach(channel -> printWriter.println(channel));
    }

    private void addNewChannel() {
        channels.addChannel(new Channel(ui.addChannel(this)));
        FileHistoryUtil.createHistoryFile(actualChannel.getChannelName());
    }

    private void chatOnChannel() {
        enterClientToChannel(ui.selectChannel(this));
        chattingOnChannel();
        exitClientFromChannel();
    }

    @SneakyThrows
    private void exitChat() {
        printWriter.println("/exit");
        System.out.println("Connection disconnect " + socket.toString());
        clientList.remove(this);
        socket.close();
    }

    private void wrongCommand() {
        printWriter.println("Wrong command!");
    }

    private void enterClientToChannel(String channelName) {
        actualChannel = channels.selectChannel(channelName);
        actualChannel.addClient(this);
    }

    private void chattingOnChannel() {
        ui.showChannelOption();
        chattingOnChannelResolver.chatOnChannel();
    }

    private void exitClientFromChannel() {
        actualChannel.removeClient(this);
    }

    @SneakyThrows
    private void close() {
            printWriter.close();
            bufferedReader.close();
    }
}
