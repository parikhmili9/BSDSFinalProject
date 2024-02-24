package server.data;

import java.util.HashMap;
import java.util.Map;
import server.data.KeyValue;

/**
 * This class is the implementation of the KeyValue interface and has methods to implement operations on key and value
 * pairs like get, put and delete.
 */
public class KeyValueImpl implements KeyValue<Integer, String> {

  private static KeyValue<Integer, String> instance;
  private final Map<Integer, String> keyValue;

  private KeyValueImpl() {
    this.keyValue = new HashMap<Integer, String>();
  }

  public static KeyValue<Integer, String> instance() {
    if (instance == null) {
      instance = new KeyValueImpl();
      instance.put(1, "one");
      instance.put(2, "two");
      instance.put(3, "three");
      instance.put(4, "four");
      instance.put(5, "five");
    }
    return instance;
  }

  @Override
  public void put(Integer key, String value) {
    if (key == null || value == null) {
      throw new IllegalArgumentException("Key or value is null!");
    }
    this.keyValue.put(key, value);
  }

  @Override
  public String get(Integer key) {
    if (key == null) {
      throw new IllegalArgumentException("The key is null.");
    }
    return this.keyValue.getOrDefault(key, null);
  }

  @Override
  public boolean delete(Integer key) {
    if (key == null) {
      throw new IllegalArgumentException("The key is null.");
    }
    if (!this.keyValue.containsKey(key)) {
      return false;
    }
    this.keyValue.remove(key);
    return true;
  }
}
