package chat.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ChattingOnChannelResolver {

    private final ClientHandler clientHandler;

    public ChattingOnChannelResolver(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void chatOnChannel() {
        String message;
        try {
            while (!(message = clientHandler.getBufferedReader().readLine()).equals("/ec")) {
                switch (message) {
                    case "/sf" -> sendFileToClient();
                    case "/sh" -> getHistory();
                    case "/sc" -> showChannelClients();
                    default -> clientHandler.getActualChannel().broadcast(message, clientHandler.getClientName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showChannelClients() {
        clientHandler.getActualChannel().getClientNames().forEach(c -> clientHandler.getPrintWriter().println(c));
    }

    private void sendFileToClient() {
        String fileName = null;
        String receiverName = null;
        try {
            fileName = clientHandler.getBufferedReader().readLine();
            receiverName = clientHandler.getBufferedReader().readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (DataOutputStream receiverDataOutputStream = new DataOutputStream(clientHandler.getActualChannel().getClient(receiverName).getDataOutputStream());
             PrintWriter receiverPrintWriter = new PrintWriter(clientHandler.getActualChannel().getClient(receiverName).getPrintWriter(), true)) {
            receiverPrintWriter.println("/file");
            receiverPrintWriter.println(clientHandler.getClientName() + " send a file :" + fileName);
            clientHandler.getDataInputStream().transferTo(receiverDataOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getHistory() {
        clientHandler.getActualChannel().getHistory(clientHandler).forEach(s -> clientHandler.getPrintWriter().println(s));
    }
}
