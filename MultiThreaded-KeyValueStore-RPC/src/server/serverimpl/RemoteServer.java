package server.serverimpl;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {
  String get(Integer key) throws RemoteException;

  boolean put(Integer key, String value) throws RemoteException;

  boolean delete(Integer key) throws RemoteException;
}
