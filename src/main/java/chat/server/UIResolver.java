package chat.server;

import java.io.PrintWriter;

public class UIResolver {

    private final PrintWriter printWriter;
    private final String clientName;

    public UIResolver(PrintWriter printWriter, String clientName) {
        this.printWriter = printWriter;
        this.clientName = clientName;
    }

    public void welcome(){
        printWriter.println(String.format("Welcome %s !", clientName));
    }

    public void showMainOption() {
        printWriter.println("\nType \"1\" to view available channels.");
        printWriter.println("Type \"2\" to add new channel.");
        printWriter.println("Type \"3\" to select channel.");
        printWriter.println("Type \"4\" to exit chat.");
    }

    public void showChannelOption(){
        printWriter.println("\nType /ec to exit actual channel.");
        printWriter.println("Type /sf to to send file to another channel member.");
        printWriter.println("Type /sh to show history of the channel.");
        printWriter.println("Type /sc to show channel members");
    }
}
