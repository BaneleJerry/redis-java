package jedis.memory;

import java.util.HashMap;
import java.util.Map;

public class CacheDB implements Database{

    Map<String, String> cacheMap = new HashMap<>();


    @Override
    public String get(String Key) {
        return cacheMap.get(Key);
    }

    @Override
    public void set(String Key, String Value) {
        cacheMap.put(Key, Value);
    }
    
}
