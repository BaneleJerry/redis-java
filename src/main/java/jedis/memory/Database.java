package jedis.memory;

/**
 * Databases
 */
public interface Database {

    void set(String Key, String Value);
    String get(String Key);
}