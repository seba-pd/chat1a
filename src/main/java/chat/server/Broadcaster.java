package chat.server;

import java.io.PrintWriter;
import java.util.List;

public class Broadcaster {

    public static void broadcast(String message, List<ClientHandler> clientHandlers) {

        clientHandlers.forEach(clientHandler -> {
                PrintWriter printWriter = clientHandler.getPrintWriter();
                printWriter.println(message);
        });
    }
}
