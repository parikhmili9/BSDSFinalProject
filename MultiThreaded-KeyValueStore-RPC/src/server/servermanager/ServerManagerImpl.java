package server.servermanager;

import logger.Logger;
import server.serverimpl.Action;
import server.serverimpl.DeleteAction;
import server.serverimpl.ExecutableRemoteServer;
import server.serverimpl.PutAction;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServerManagerImpl implements ServerManager {

  private final Map<String, ExecutableRemoteServer> servers;
  private Logger logger;

  public ServerManagerImpl() {
    this.servers = new HashMap<>();
  }

  @Override
  public boolean put(Integer key, String value) throws RemoteException {
    if(this.servers.isEmpty()){
      System.out.println("There are no servers to perform 'PUT' action. ");
//      this.logInfo("There are no servers to perform 'PUT' action. ");
      return false;
    }

    Action action = new PutAction(key, value);
    String actionId = String.format("PUT@%d", randomInteger());
//    this.logInfo(String.format("Created action %s: %s", actionId, action));

    boolean res = this.prepareAndExecute(action, actionId);
//    this.logInfo(String.format("PUT(%d, %s) -> %s", key, value, res));
    return res;
  }

  // start here
  @Override
  public boolean delete(Integer key) throws RemoteException {
    if(this.servers.isEmpty()) {
//      this.logInfo("No servers are there to perform DELETE action.");
      return false;
    }

    Action action = new DeleteAction(key);
    String actionId = String.format("DELETE@%d", randomInteger());
//    this.logInfo(String.format("Created operation %s", actionId));
    boolean res = this.prepareAndExecute(action, actionId);
//    this.logInfo(String.format("DELETE(%d) -> %s", key, res));
    return res;
  }

  @Override
  public void addServer(int port, String host, String serverName) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(host, port);
      ExecutableRemoteServer server = (ExecutableRemoteServer) registry.lookup(serverName);
      servers.put(serverName, server);
//      this.logInfo(String.format("Added Remote Server from %s:%d - %s", host, port, serverName));
    } catch (RemoteException | NotBoundException e) {
      System.out.println(e.getMessage());
//      this.logError(String.format("Couldn't add %s:%d::%s : %s",
//          host, port, serverName, e.getMessage()));
    }
  }

  @Override
  public void removeServer(String serverName) throws RemoteException {
    this.servers.remove(serverName);
//    this.logInfo("Removed " + serverName + " server.");
  }

  private int randomInteger() {
    Random random = new Random();
    return 10000 + random.nextInt(999999 - 10000 + 1);
  }

  private boolean prepareAndExecute(Action action, String actionId){
    boolean someServersNotReady = this.servers.values().stream().map(s -> {
      try {
        return s.prepare(action, actionId);
      } catch (RemoteException re){
//        this.logError("Couldn't connect to some servers to prepare" + re.getMessage());
        return false;
      }
    }).anyMatch(s -> !s);

    if(someServersNotReady) {
      this.servers.values().forEach(s -> {
        try {
          s.abort(actionId);
        } catch (RemoteException re) {
//          this.logError("Couldn't connect to some servers to abort. " + re.getMessage());
        }
      });
    } else {
      this.servers.values().forEach(s -> {
        try {
          s.execute(actionId);
        } catch (RemoteException re) {
//          this.logError("Couldn't connect to some servers to execute. " + re.getMessage());
        }
      });
    }
    return !someServersNotReady;
  }


//  private void logType(String type, String message) {
//    if (message == null || type == null) {
//      throw new IllegalArgumentException("message or type is null.");
//    }
//    this.logger.log(String.format("%s - %s", type, message));
//  }
//
//  private void logRequest(String msg) {
//    this.logType("Request", msg);
//  }
//
//  private void logResponse(String msg) {
//    this.logType("Response", msg);
//  }
//
//  private void logInfo(String msg) {
//    this.logType("Info", msg);
//  }
//
//  private void logError(String msg) {
//    this.logType("Error", msg);
//  }
}
