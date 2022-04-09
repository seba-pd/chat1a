package chat.server;

import lombok.Getter;

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
    private final ReadWriteLock membersLock = new ReentrantReadWriteLock();

    public Channel(String name) {
        this.channelName = name;
        this.channelHistoryRepository = new FileChannelHistoryRepository(name);
    }

    public void addClient(ClientHandler clientHandler) {
        membersLock.writeLock().lock();
        channelMembers.add(clientHandler);
        membersLock.writeLock().unlock();
        channelHistoryRepository.saveMessage(String.format("%s : joined the channel", clientHandler.getClientName()));
        Broadcaster.broadcast(String.format("%s joined the channel %s", clientHandler.getClientName(), channelName), channelMembers);
    }

    public ClientHandler getClient(String name) {
        membersLock.readLock().lock();
        ClientHandler client = channelMembers.stream()
                .filter(clientHandler -> clientHandler.getClientName().equals(name))
                .findAny().orElseThrow(NoSuchElementException::new);
        membersLock.readLock().unlock();
        return client;
    }

    public Stream<String> getClientNames() {
        membersLock.readLock().unlock();
        Stream<String> clientNames = channelMembers.stream()
                .map(ClientHandler::getClientName);
        membersLock.readLock().unlock();
        return clientNames;
    }

    public void removeClient(ClientHandler clientHandler) {
        membersLock.writeLock().lock();
        channelMembers.remove(clientHandler);
        Broadcaster.broadcast(String.format("%s has left from channel %s", clientHandler.getClientName(), channelName), channelMembers);
        channelHistoryRepository.saveMessage(String.format("%s : has left from channel", clientHandler.getClientName()));
        membersLock.writeLock().unlock();
    }

    public void removeAllClients() {
        membersLock.writeLock().lock();
        channelMembers.forEach(this::removeClient);
        membersLock.writeLock().unlock();
    }

    public List<String> getHistory(ClientHandler clientHandler) {
        return channelHistoryRepository.getHistory(clientHandler.getClientName());
    }

    public void broadcast(String message, String name) {
        Broadcaster.broadcast(String.format("%s : %s", name, message), channelMembers);
        channelHistoryRepository.saveMessage(String.format("%s : %s", name, message));
    }
}
