package server.data;

public interface KeyValue<K,V> extends ImmutableKeyValue<K,V> {
  /**
   * If the key already exists in the data stored then it updates it's corresponding
   * value or else if the key doesn't exist then it adds to the data stored along
   * with it's corresponding value.
   * @param key key (integer) for the data to be stored
   * @param value value or the data associated
   * @return true if put action is successfull, false otherwise.
   * @throws IllegalArgumentException if the key or value provided is null
   */
  boolean put(K key, V value);

  /**
   * If the key already exists in the data stored, the it simple deletes the key
   * along with it's corresponding value.
   * @param key the key that is to be deleted
   * @return true if the key exists in the data and deleted, false if the key does
   * not exist
   * @throws IllegalArgumentException if the key provided is null
   */
  boolean delete(K key);

}
