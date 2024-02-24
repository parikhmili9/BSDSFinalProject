package server.paxos;

import server.helpers.AcceptAcknowledgment;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Learner extends Remote {

  void accept(AcceptAcknowledgment acceptAcknowledgment) throws RemoteException;

  String get(Integer key) throws RemoteException;

  int getLearnerId() throws RemoteException;
}
