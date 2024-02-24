package server.data;

public interface LockKeyValue<K,V> extends KeyValue<K,V> {
  /**
   * Puts a lock on the key-value pair. Once the lock is put then the key-value
   * pair cannot be updated or deleted but it can still be read.
   * After the lock, actions "put" and "delete" cannot be performed but action "get"
   * can be performed.
   * If a lock is put on some key that does not exist is the current data stored then
   * until that key is unlocked,a value with that particular key cannot be added, that is,
   * if lock(15) is performed and 15 does not exist in the database then put(15, "HI")
   * cannot be performed.
   * @param key key that is to be locked
   * @return true if action is implemented and false if they key is already locked
   */
  boolean lock(K key);

  /**
   * Unlocks the key-value pair that is locked before. Once unlock is executed
   * then the key-value pair can be updated or deleted.
   * After unlocking, actions "put" and "delete" can be performed along with
   * the action "get".
   * @param key key that is to be unlocked
   * @return true if the unlock action is implemented and false if the key that is requested
   * to unlock doesn't hold a lock on it.
   */
  boolean unlock(K key);
}
