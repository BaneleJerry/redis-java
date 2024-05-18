package jedis.memory;

/**
 * Databases
 */
public interface Database {

    void set(String Key, String Value);
    public void set(String key, String value, long ttl);
    String get(String Key);
}