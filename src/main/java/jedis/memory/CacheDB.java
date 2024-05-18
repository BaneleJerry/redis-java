package jedis.memory;

import java.util.HashMap;
import java.util.Map;

public class CacheDB implements Database{

    private class CacheDBEntry {
        String Value;
        long expiry;

        
        protected CacheDBEntry(String Value, long expiryTime) {
            this.Value = Value;
            this.expiry = expiryTime;
        }
        
    }

    Map<String, CacheDBEntry> cacheMap = new HashMap<>();


    @Override
    public String get(String Key) {
        CacheDBEntry entry = cacheMap.get(Key);
        if (entry == null) {
            return null;
        }
        if (entry.expiry > 0 && entry.expiry < System.currentTimeMillis()) {
            cacheMap.remove(Key);
            return null;
        }
        return entry.Value;
    }

    @Override
    public void set(String Key, String Value) {
        set(Key, Value, -1);
    }

    @Override
    public void set(String key, String value, long ttl) {
        long expiryTime = (ttl > 0) ? System.currentTimeMillis() + ttl : -1;
        cacheMap.put(key, new CacheDBEntry(value, expiryTime));
    }
    
}
