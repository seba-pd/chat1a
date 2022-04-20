package chat.server;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;

public class UIResolver {

    private final PrintWriter printWriter;

    public UIResolver(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public void welcome(String clientName) {
        printWriter.println(String.format("Welcome %s !", clientName));
    }

    public void showMainOption() {
        printWriter.println("\nType \"1\" to view available channels.");
        printWriter.println("Type \"2\" to add new channel.");
        printWriter.println("Type \"3\" to select channel.");
        printWriter.println("Type \"4\" to exit chat.");
    }

    public void showChannelOption() {
        printWriter.println("\nType /ec to exit actual channel.");
        printWriter.println("Type /sf to to send file to another channel member.");
        printWriter.println("Type /sh to show history of the channel.");
        printWriter.println("Type /sc to show channel members");
    }

    @SneakyThrows
    public String addChannel(ClientHandler clientHandler) {
        String channel;
        printWriter.println("Enter channel name: ");
        while (clientHandler.getChannels().isPresent(channel = clientHandler.getBufferedReader().readLine())) {
            printWriter.println("Channel already exist!");
        }
        return channel;
    }

    @SneakyThrows
    public String selectChannel(ClientHandler clientHandler) {
        String selectedChannel;
        printWriter.println("Enter channel name.");
        while (!clientHandler.getChannels().isPresent(selectedChannel = clientHandler.getBufferedReader().readLine())) {
            printWriter.println("Wrong channel name!");
        }
        return selectedChannel;
    }

    @SneakyThrows
    public String selectName(BufferedReader bufferedReader, List<ClientHandler> clientList) {
        String clientName;
        List<String> clients = clientList.stream().map(ClientHandler::getClientName).toList();
        while (clients.contains(clientName = bufferedReader.readLine())) {
            printWriter.println("Name already exist!");
        }
        return clientName;
    }
}
