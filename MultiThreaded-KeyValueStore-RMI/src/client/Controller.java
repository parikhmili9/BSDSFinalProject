package client;

import java.rmi.RemoteException;

/**
 * The controller is in charge of receiving user input, processing it, performing
 * simple validations, and invoking the necessary methods on the KeyValue remote object.
 */
public interface Controller {

  void start() throws RemoteException;
}
