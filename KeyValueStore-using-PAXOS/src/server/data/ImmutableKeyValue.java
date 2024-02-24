package server.data;

public interface ImmutableKeyValue<K,V> {
  /**
   *If the key already exists in the data stored, then it returns the value of that
   * particular key.
   * @param Key the key that is being looked in the database
   * @return if the key exists then the corresponding value, else null
   * @throws IllegalArgumentException if the key given is null
   */
  V get(K Key);

}
