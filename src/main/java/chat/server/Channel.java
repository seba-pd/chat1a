package chat.server;

import lombok.Getter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Channel {

    @Getter
    private final String channelName;
    private final List<ClientHandler> channelMembers = new LinkedList<>();
    private final ChannelHistoryRepository channelHistoryRepository;

    public Channel(String name) {
        this.channelName = name;
        this.channelHistoryRepository = new FileChannelHistoryRepository(name);
    }

    public synchronized void addClient(ClientHandler clientHandler) {
        channelMembers.add(clientHandler);
        channelHistoryRepository.saveMessage(String.format("%s : joined the channel", clientHandler.getClientName()));
        Broadcaster.broadcast(String.format("%s joined the channel %s", clientHandler.getClientName(), channelName), channelMembers);
    }

    public synchronized ClientHandler getClient(String name) {
        return channelMembers.stream()
                .filter(clientHandler -> clientHandler.getClientName().equals(name))
                .findAny().orElseThrow();
    }

    public synchronized Stream<String> getClientNames() {
        return channelMembers.stream()
                .map(ClientHandler::getClientName);
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        Broadcaster.broadcast(String.format("%s has left from channel %s", clientHandler.getClientName(), channelName), channelMembers);
        channelHistoryRepository.saveMessage(String.format("%s : has left from channel", clientHandler.getClientName()));
        channelMembers.remove(clientHandler);
    }

    public  void removeAllClients(){
        channelMembers.forEach(this::removeClient);
    }

    public  List<String> getHistory(ClientHandler clientHandler){
        return channelHistoryRepository.getHistory(clientHandler.getClientName());
    }

    public void broadcast(String message, String name) {
        Broadcaster.broadcast(String.format("%s : %s", name, message), channelMembers);
        channelHistoryRepository.saveMessage(String.format("%s : %s",name,message));
    }
}
