package server.data;

import java.rmi.Remote;

public interface RemoteKeyValue<K,V> extends Remote {
  boolean put(K key, V value);

  V get(K key);

  boolean delete(K key);

}
