package server.data;

/**
 * Represents a key and value pair. Operations like put, get and delete can be performed on this key and value pair.
 * @param <K> data type of key
 * @param <V> data type of value
 */
public interface KeyValue<K, V> {

  /**
   * This puts the value for a particular key and if the key already exists then it updates the value for that key.
   * @param key key (integer)
   * @param value string which needs to be stored with the key
   */
  void put(K key, V value);

  /**
   * Retrieves the value at a particular key.
   * @param key key (integer)
   * @return string associated with the key
   */
  V get(K key);

  /**
   * Deletes the data at a particular key.
   * @param key key (integer)
   * @return true if successfully deleted and false if not deleted
   */
  boolean delete(K key);


}
