package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {

    public final static String DATA_DIRECTORY = "C:\\Users\\Seba\\chat1a\\data\\";
    private final ExecutorService executorService = Executors.newFixedThreadPool(1000);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Channels channels =  new Channels(lock);

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        new Server().start(port);
    }

    private void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            listen(serverSocket);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> channels.getAllChannels().forEach(Channel::removeAllClients)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen(ServerSocket serverSocket) {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Connection establish " + socket.toString());
                ClientHandler clienthandler = new ClientHandler(socket, channels, lock);
                executorService.execute(clienthandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
