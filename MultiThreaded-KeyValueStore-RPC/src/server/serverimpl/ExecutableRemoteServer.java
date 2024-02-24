package server.serverimpl;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ExecutableRemoteServer extends Remote {

  boolean prepare(Action action, String actionId) throws RemoteException;

  boolean execute(String actionId) throws RemoteException;

  boolean abort(String actionId) throws RemoteException;
}
