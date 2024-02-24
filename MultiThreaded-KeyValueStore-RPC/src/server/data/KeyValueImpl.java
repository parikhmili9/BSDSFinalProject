package server.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueImpl<K,V> implements KeyValue<K,V> {

  protected final Map<K, V> storage;

  public KeyValueImpl() {
    this.storage = new ConcurrentHashMap<>();
  }

  @Override
  public V get(K key) {
    if(key == null) {
      throw new IllegalArgumentException("Key cannot be null");
    }
    return this.storage.getOrDefault(key, null);
  }

  @Override
  public boolean put(K key, V value) {
    if(key == null || value == null) {
      throw new IllegalArgumentException("Key or value cannot be null");
    }
    this.storage.put(key, value);
    return true;
  }

  @Override
  public boolean delete(K key) {
    if(key == null) {
      throw new IllegalArgumentException("Key cannot be null");
    }
    if(this.storage.containsKey(key)){
      this.storage.remove(key);
      return true;
    } else {
      return false;
    }
  }
}
