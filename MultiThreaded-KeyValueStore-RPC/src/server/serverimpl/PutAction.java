package server.serverimpl;

import server.data.KeyValue;

import java.rmi.RemoteException;

public class PutAction extends AbstractAction{

  private String value;

  public PutAction(Integer key, String value) throws RemoteException {
    super(key);

    if(value == null){
      throw new IllegalArgumentException("The value is null");
    }
    this.value = value;
  }

  @Override
  public boolean execute(KeyValue<Integer, String> keyValue) {
    if(keyValue == null){
      throw new IllegalArgumentException("Keyvalue is null");
    }
    return keyValue.put(this.key, this.value);
  }

  @Override
  public String toString() {
    return String.format("PUT(%d, %s)", this.key, this.value);
  }
}
