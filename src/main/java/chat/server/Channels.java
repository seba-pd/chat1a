package chat.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Channels {

    private final Map<String, Channel> channels = new HashMap<>();
    private final ReadWriteLock channelLock = new ReentrantReadWriteLock();

    public Channels() {
        channels.put("global", new Channel("Global"));
    }

    public Channel selectChannel(String channelName) {
        return channels.get(channelName);
    }

    public boolean isPresent(String channelName) {
        return channels.containsKey(channelName);
    }

    public void addChannel(Channel channel) {
        channelLock.writeLock().lock();
        channels.put(channel.getChannelName(), channel);
        channelLock.writeLock().unlock();
    }

   /* public synchronized void removeChannel(Channel channel){
        channels.remove(channel.getChannelName());
    }*/

    public List<Channel> getAllChannels() {
        channelLock.readLock().lock();
        List<Channel> channelList = channels.values().stream().toList();
        channelLock.readLock().unlock();
        return channelList;
    }

}
