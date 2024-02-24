package server.serverimpl;

import server.data.KeyValue;

import java.io.Serializable;

public interface Action extends Serializable {
  boolean execute(KeyValue<Integer, String> keyValue);

  Integer key();
}
