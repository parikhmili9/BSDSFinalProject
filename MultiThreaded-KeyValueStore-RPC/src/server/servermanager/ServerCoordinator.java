package server.servermanager;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerCoordinator extends Remote {

  boolean put(Integer key, String value) throws RemoteException;

  boolean delete(Integer key) throws RemoteException;
}
