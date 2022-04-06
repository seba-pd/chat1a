package chat.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

public class Channels {

    private final Map<String, Channel> channels = new HashMap<>();

    public Channels(ReadWriteLock lock) {

        channels.put("global", new Channel("Global", lock));
    }

    public synchronized Channel selectChannel(String channelName){
        return channels.get(channelName);
    }
    public boolean isPresent(String channelName){
        return channels.containsKey(channelName);
    }

    public synchronized void  addChannel(Channel channel){
        channels.put(channel.getChannelName(), channel);
    }

   /* public synchronized void removeChannel(Channel channel){
        channels.remove(channel.getChannelName());
    }*/

    public synchronized List<Channel> getAllChannels(){
        return channels.values().stream().toList();
    }

}
