package chat.server;

import lombok.Getter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

public class Channel {

    @Getter
    private final String channelName;
    private final List<ClientHandler> channelMembers = new LinkedList<>();
    private final ChannelHistoryRepository channelHistoryRepository;
    private final ReadWriteLock channelMembersLock = new ReentrantReadWriteLock();

    public Channel(String name) {
        this.channelName = name;
        this.channelHistoryRepository = new FileChannelHistoryRepository(name);
    }

    public void addClient(ClientHandler clientHandler) {
        channelMembersLock.writeLock().lock();
        channelMembers.add(clientHandler);
        channelMembersLock.writeLock().unlock();
        channelHistoryRepository.saveMessage(String.format("%s : joined the channel", clientHandler.getClientName()));
        Broadcaster.broadcast(String.format("%s joined the channel %s", clientHandler.getClientName(), channelName), channelMembers);
    }

    public ClientHandler getClient(String name) {
        channelMembersLock.readLock().lock();
        ClientHandler client = channelMembers.stream()
                .filter(clientHandler -> clientHandler.getClientName().equals(name))
                .findAny().orElseThrow(NoSuchElementException::new);
        channelMembersLock.readLock().unlock();
        return client;
    }

    public Stream<String> getClientNames() {
        channelMembersLock.readLock().unlock();
        Stream<String> clientNames = channelMembers.stream()
                .map(ClientHandler::getClientName);
        channelMembersLock.readLock().unlock();
        return clientNames;
    }

    public void removeClient(ClientHandler clientHandler) {
        channelMembersLock.writeLock().lock();
        channelMembers.remove(clientHandler);
        channelMembersLock.writeLock().unlock();
        Broadcaster.broadcast(String.format("%s has left from channel %s", clientHandler.getClientName(), channelName), channelMembers);
        channelHistoryRepository.saveMessage(String.format("%s : has left from channel", clientHandler.getClientName()));
    }

    public void removeAllClients() {
        channelMembersLock.writeLock().lock();
        channelMembers.forEach(this::removeClient);
        channelMembersLock.writeLock().unlock();
    }

    public List<String> getHistory(ClientHandler clientHandler) {
        return channelHistoryRepository.getHistory(clientHandler.getClientName());
    }

    public void broadcast(String message, String name) {
        Broadcaster.broadcast(String.format("%s : %s", name, message), channelMembers);
        channelHistoryRepository.saveMessage(String.format("%s : %s", name, message));
    }
}
