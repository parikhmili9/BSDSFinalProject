package server.servermanager;

import java.rmi.RemoteException;

public interface ServerManager extends ServerCoordinator {

  void addServer(int port, String host, String serverName) throws RemoteException;

  void removeServer(String serverName) throws RemoteException;
}
