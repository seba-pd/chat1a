package chat.server;

import java.util.List;

public interface ChannelHistoryRepository {
     void saveMessage(String message);
     List<String> getHistory(String clientName);
}
