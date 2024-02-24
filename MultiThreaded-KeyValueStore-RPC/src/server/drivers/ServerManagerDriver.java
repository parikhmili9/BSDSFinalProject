package server.drivers;

import logger.Logger;
import logger.ServerLogger;
import server.servermanager.ServerManager;
import server.servermanager.ServerManagerImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerManagerDriver {

  private final int port;
  private final String name;

  private static Logger logger;

  public ServerManagerDriver(int port, String name) {
    if(port < 1024 || port > 65535) {
      throw new IllegalArgumentException("Port number must be between 1024 and 65535.");
    }
    if(name == null) {
      throw new IllegalArgumentException("Name cannot be null.");
    }
    this.port = port;
    this.name = name;
  }

  public int getPort(){
    return this.port;
  }

  public String getName() {
    return this.name;
  }

  public void start() throws RemoteException {
    ServerManager serverManager = new ServerManagerImpl();
    Registry registry = LocateRegistry.createRegistry(this.port);
//    this.logInfo("Successfully created registry at : " + this.port);

    serverManager = (ServerManager) UnicastRemoteObject.exportObject(serverManager, 0);
    registry.rebind(this.name, serverManager);
//    this.logInfo("Successfully bound the server manager to : " + this.name);
  }

  private static boolean isInteger(String number) {
    try {
      Integer.parseInt(number);
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    }
  }

  public static void main(String[] args) {
    String name = "Server Manager";
    int port = 4000;

    int argumentLength = args.length;

    if(argumentLength > 1 && argumentLength % 2 == 0) {
      for(int i = 0; i < argumentLength; i += 2) {
        switch (args[i].toLowerCase()) {
          case "-p" :
            if (!(i + 1 < argumentLength) || !(isInteger(args[i + 1]))) {
              break;
            }
            port = Integer.parseInt(args[i + 1]);
            break;

          case "-n" :
            if (!(i + 1 < argumentLength)) {
              break;
            }
            name = args[i + 1];
            break;

          default:
            break;
        }
      }
    }

    System.out.println("Port : " + port);
    System.out.println("Name : " + name);

    try {
      new ServerManagerDriver(port, name).start();
    } catch (RemoteException | RuntimeException re) {
      System.out.println(re.getMessage());
//      logError("Remote Exception occurred : " + re.getMessage());
    } //      logError("Crashed. Runtime exception occured : " + e.getMessage());


  }

//  private static void logType(String type, String message) {
//    if (message == null || type == null) {
//      throw new IllegalArgumentException("message or type is null.");
//    }
//    logger.log(String.format("%s - %s", type, message));
//  }
//
//  private static void logInfo(String msg) {
//    logType("Info", msg);
//  }
//
//  private static void logError(String msg) {
//    logType("Error", msg);
//  }
}
