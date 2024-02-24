package server.paxos;

import java.rmi.RemoteException;

public interface NodePaxos extends Acceptor, Learner, ServerProposer, ClientProposer {

  /**
   * Registers a Remote PAXOS node. This PAXOS node can then start communicating
   * with the newly added nodes.
   * @param host host name
   * @param name name
   * @param port port number
   * @throws RemoteException if connection fails
   */
  void registerNode(String host, String name, int port) throws RemoteException;
}
