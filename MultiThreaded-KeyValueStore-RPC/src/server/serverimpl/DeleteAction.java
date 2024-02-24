package server.serverimpl;

import server.data.KeyValue;

import java.rmi.RemoteException;

public class DeleteAction extends AbstractAction {

  public DeleteAction(Integer key) throws RemoteException {
    super(key);
  }

  @Override
  public boolean execute(KeyValue<Integer, String> keyValue) {
    if(keyValue == null) {
      throw new IllegalArgumentException("Key value is null.");
    }
    return keyValue.delete(this.key);
  }

  @Override
  public String toString() {
    return String.format("DELETE(%d)", key);
  }
}
