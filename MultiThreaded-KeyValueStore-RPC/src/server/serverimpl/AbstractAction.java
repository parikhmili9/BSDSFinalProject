package server.serverimpl;

import java.rmi.RemoteException;

public abstract class AbstractAction implements Action {

  protected Integer key;

  protected AbstractAction(Integer key) throws RemoteException {
    super();
    if(key == null){
      throw new IllegalArgumentException("The key is null.");
    }
    this.key = key;
  }

  @Override
  public Integer key() {
    return this.key;
  }

}
