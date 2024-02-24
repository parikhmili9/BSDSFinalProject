package server.paxos;

import server.helpers.AcceptRequest;
import server.helpers.Proposal;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Acceptor extends Remote {

  void propose(Proposal proposal) throws RemoteException;

  void accept(AcceptRequest acceptRequest) throws RemoteException;

  int getAcceptorId() throws RemoteException;
}
