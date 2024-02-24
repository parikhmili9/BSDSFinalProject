package server.data;

import java.util.HashSet;
import java.util.Set;

public class LockKeyValueImpl<K,V> extends KeyValueImpl<K,V> implements LockKeyValue<K,V> {

  private final Set<K> lockedKeys;

  public LockKeyValueImpl() {
    super();
    this.lockedKeys = new HashSet<>();
  }

  @Override
  public boolean lock(K key) {
    if(key == null) {
      throw new IllegalArgumentException("Key provided cannot be null");
    }
    if(this.lockedKeys.contains(key)) {
      return false;
    } else {
      this.lockedKeys.add(key);
      return true;
    }
  }

  @Override
  public boolean unlock(K key) {
    if(key == null) {
      throw new IllegalArgumentException("Key provided cannot be null");
    }
    if(this.lockedKeys.contains(key)) {
      this.lockedKeys.remove(key);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean put(K key, V value) {
    if(this.lockedKeys.contains(key)) {
      return false;
    }
    return super.put(key, value);
  }

  @Override
  public boolean delete(K key) {
    if(this.lockedKeys.contains(key)) {
      return false;
    }
    return super.delete(key);
  }
}
