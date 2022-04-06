package chat.server;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Stream;

public class Channel {

    @Getter
    private final String channelName;
    private final List<ClientHandler> channelMembers = new LinkedList<>();
    private final ChannelHistoryRepository channelHistoryRepository;
    private final ReadWriteLock lock;

    public Channel(String name, ReadWriteLock lock) {
        this.channelName = name;
        this.lock = lock;
        this.channelHistoryRepository = new FileChannelHistoryRepository(name);
    }

    public void addClient(ClientHandler clientHandler) {
        lock.writeLock().lock();
        channelMembers.add(clientHandler);
        channelHistoryRepository.saveMessage(String.format("%s : joined the channel", clientHandler.getClientName()));
        lock.writeLock().unlock();
        Broadcaster.broadcast(String.format("%s joined the channel %s", clientHandler.getClientName(), channelName), channelMembers);
    }

    public ClientHandler getClient(String name) {
        lock.readLock().lock();
        ClientHandler client = channelMembers.stream()
                .filter(clientHandler -> clientHandler.getClientName().equals(name))
                .findAny().orElseThrow(NoSuchElementException::new);
        lock.readLock().unlock();
        return client;
    }

    public Stream<String> getClientNames() {
        lock.readLock().unlock();
        Stream<String> clientNames = channelMembers.stream()
                .map(ClientHandler::getClientName);
        lock.readLock().unlock();
        return clientNames;
    }

    public void removeClient(ClientHandler clientHandler) {
        lock.writeLock().lock();
        channelMembers.remove(clientHandler);
        Broadcaster.broadcast(String.format("%s has left from channel %s", clientHandler.getClientName(), channelName), channelMembers);
        channelHistoryRepository.saveMessage(String.format("%s : has left from channel", clientHandler.getClientName()));
        lock.writeLock().unlock();
    }

    public void removeAllClients() {
        channelMembers.forEach(this::removeClient);
    }

    public List<String> getHistory(ClientHandler clientHandler) {
        lock.readLock().lock();
        List<String> channelList = channelHistoryRepository.getHistory(clientHandler.getClientName());
        lock.readLock().unlock();
        return channelList;
    }

    public void broadcast(String message, String name) {
        Broadcaster.broadcast(String.format("%s : %s", name, message), channelMembers);
        channelHistoryRepository.saveMessage(String.format("%s : %s", name, message));
    }
}
