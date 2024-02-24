package server.paxos;

import server.helpers.Promise;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerProposer extends Remote {

  void promise(Promise promise) throws RemoteException;

  int getProposerId() throws RemoteException;
}
