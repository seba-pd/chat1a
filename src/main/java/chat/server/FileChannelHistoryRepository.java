package chat.server;

import chat.util.FileHistoryUtil;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileChannelHistoryRepository  implements ChannelHistoryRepository {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
            .withLocale( Locale.ENGLISH )
            .withZone( ZoneId.systemDefault() );
    private final String channelName;
    private final ReadWriteLock historyLock = new ReentrantReadWriteLock();

    public FileChannelHistoryRepository(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public  void saveMessage(String message) {

        historyLock.writeLock().lock();
        try (FileWriter fileWriter = new FileWriter((Server.DATA_DIRECTORY + channelName + "\\" + "history.txt"), true)) {
            fileWriter.write(String.format("%s : %s %n", formatter.format(Instant.now()),  message));
        } catch (IOException e) {
            e.printStackTrace();
        }
        historyLock.readLock().unlock();
    }

    @Override
    public  List<String> getHistory(String clientName) {
        historyLock.readLock().lock();
        List<String> history = FileHistoryUtil.getHistoryFromFile(Server.DATA_DIRECTORY + channelName + "\\history.txt", clientName);
        historyLock.readLock().unlock();
        return history;
    }
}
