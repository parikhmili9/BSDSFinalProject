package client.clientimpl;

import server.paxos.ClientProposer;
import server.serverimpl.RemoteServer;

import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {

  private final String name;
  private final String host;
  private final int port;

  public RMIClient(String name, String host, int port) {
    if(name == null || host == null) {
      throw new IllegalArgumentException(" the name or hostname is null");
    }
    if(port < 1024 || port > 65535) {
      throw new IllegalArgumentException("Port number must be between 1024 and 65535. ");
    }
    this.name = name;
    this.host = host;
    this.port = port;
  }

  public void start() {
    try {
      Registry registry = LocateRegistry.getRegistry(this.host, this.port);

      ClientProposer clientProposer = (ClientProposer) registry.lookup(this.name);
      new ClientKeyValueController(clientProposer, new InputStreamReader(System.in), System.out).start();
    } catch (RemoteException | NotBoundException e) {
      System.out.println("Either name is not bound to any object or there was an issue " +
          "in finding the registry");
      System.out.println(e.getMessage());
    }
  }
}
