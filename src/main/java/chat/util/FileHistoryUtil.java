package chat.util;

import chat.server.Server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileHistoryUtil {

    public static void createHistoryFile(String channelName) {
        new File(Server.DATA_DIRECTORY + channelName + "\\").mkdir();
        try {
            new File(Server.DATA_DIRECTORY + channelName, "history.txt").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getHistoryFromFile(String channelHistoryFilePath, String clientName) {
        String clientStartHistory = clientName + " : joined the channel ";
        String clientEndHistory =  clientName + " : has left from channel ";
        List<String> channelHistory = new ArrayList<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(channelHistoryFilePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.substring(18).equals(clientStartHistory)) {
                    while ((line = bufferedReader.readLine()) != null && !line.substring(18).equals(clientEndHistory)){
                        channelHistory.add(line);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return channelHistory;
    }
}
