package server.serverimpl;

import server.data.KeyValue;

import java.io.Serializable;

public interface Action extends Serializable {

  /**
   * Execute the operation on the datastore.
   * @param keyValue that datastore on which the operation is to be performed.
   * @return whether the operation was successful.
   */
  boolean execute(KeyValue<Integer, String> keyValue);

  /**
   * Get the datastore key on which the operation is to be performed.
   */
  Integer key();
}
