package chat.server;

import chat.util.FileHistoryUtil;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

public class FileChannelHistoryRepository  implements ChannelHistoryRepository {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
            .withLocale( Locale.ENGLISH )
            .withZone( ZoneId.systemDefault() );
    private final String channelName;

    public FileChannelHistoryRepository(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public synchronized void saveMessage(String message) {

        try (FileWriter fileWriter = new FileWriter((Server.DATA_DIRECTORY + channelName + "\\" + "history.txt"), true)) {
            fileWriter.write(String.format("%s : %s %n", formatter.format(Instant.now()),  message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<String> getHistory(String clientName) {
            return FileHistoryUtil.getHistoryFromFile(Server.DATA_DIRECTORY + channelName + "\\history.txt", clientName);
    }
}
