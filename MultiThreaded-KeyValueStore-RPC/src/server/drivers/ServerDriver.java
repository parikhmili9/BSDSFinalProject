package server.drivers;

import logger.Logger;
import server.data.LockKeyValue;
import server.data.LockKeyValueImpl;
import server.serverimpl.ExecutableRemoteServer;
import server.serverimpl.ExecutableRemoteServerImpl;
import server.serverimpl.RemoteServer;
import server.serverimpl.RemoteServerImpl;
import server.servermanager.ServerManager;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ServerDriver {

  private LockKeyValue<Integer, String> keyValue;
  private int port;
  private String name;
  private static Logger logger;

  public ServerDriver(int port, String name) {
    if(port < 1024 || port > 65535) {
      throw new IllegalArgumentException("Port number must be between 1024 and 65535. ");
    }
    if(name == null) {
      throw new IllegalArgumentException("Name cannot be null. ");
    }
    this.port = port;
    this.name = name;
    this.keyValue = new LockKeyValueImpl<>();
    this.keyValue.put(1, "One");
    this.keyValue.put(2, "Two");
    this.keyValue.put(3, "Three");
    this.keyValue.put(4, "Four");
    this.keyValue.put(5, "Five");
  }

  public int getPort() {
    return this.port;
  }

  public String getName() {
    return this.name;
  }

  public void start(int managerPort, String managerName, String managerHost)
      throws RemoteException, NotBoundException {
    System.out.println("Enter q or Q to Quit.");

    ServerManager manager;
    try {
      Registry managerRegistry = LocateRegistry.getRegistry(managerHost, managerPort);
      manager = (ServerManager) managerRegistry.lookup(managerName);
    } catch (RuntimeException | NotBoundException e) {
      System.out.println("Could not find the server manager / coordinator. " + e.getMessage());
//      logError("Could not find the server manager / coordinator. " + e.getMessage());
      return;
    }

    RemoteServer remoteServer = new RemoteServerImpl(keyValue, manager, logger);
    ExecutableRemoteServer executableRemoteServer = new ExecutableRemoteServerImpl(keyValue, logger);

    Registry registry = LocateRegistry.createRegistry(port);
    UnicastRemoteObject.exportObject(remoteServer, 0);
    registry.rebind(name, remoteServer);
//    logInfo(String.format("Successfully bound RemoteServer to: %s", name));

    UnicastRemoteObject.exportObject(executableRemoteServer, 0);
    String executableName = String.format("%s@Executable", name);
    registry.rebind(executableName, executableRemoteServer);
//    logInfo(String.format("Successfully bound ExecutableServer to: %s", executableName));

    manager.addServer(port, "localhost", executableName);

    Scanner scanner = new Scanner(System.in);
    String answer = "";

    do {
      answer = scanner.next();
      if(answer.equalsIgnoreCase("q") || answer.equalsIgnoreCase("Q")) {
        manager.removeServer(executableName);
        registry.unbind(name);
        registry.unbind(executableName);
        UnicastRemoteObject.unexportObject(remoteServer, true);
        UnicastRemoteObject.unexportObject(executableRemoteServer, true);
      }

    } while (!answer.equalsIgnoreCase("q") || !answer.equalsIgnoreCase("Q"));

  }

  public static void main(String[] args) {
    String name = "";
    int port = -1;
    int managerPort = -1;
    String managerName = "";
    String managerHostName = "";

    int argumentLength = args.length;

    if(argumentLength < 10) {
      System.out.println("Please enter valid arguments! ");
    }

    for(int i = 0; i < argumentLength; i+=2) {
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

        case "-mp" :
          if (!(i + 1 < argumentLength) || !(isInteger(args[i + 1]))) {
            break;
          }
          managerPort = Integer.parseInt(args[i + 1]);
          break;

        case "-mn" :
          if (!(i + 1 < argumentLength)) {
            break;
          }
          managerName = args[i + 1];
          break;

        case "-mh" :
          if (!(i + 1 < argumentLength)) {
            break;
          }
          managerHostName = args[i + 1];
          break;

        default:
          break;
      }
    }

    if(name.isBlank() || port < 0 || managerPort < 0 || managerName.isBlank()
    || managerHostName.isBlank()) {
      System.out.println("Enter valid arguments. ");
      return;
    }

    try {
      new ServerDriver(port, name).start(managerPort, managerName, managerHostName);
    } catch (NotBoundException nbe) {
      System.out.println("Not bound : " + nbe.getMessage());
    } catch (RemoteException re) {
      System.out.println("Remote Exception occurred : " + re.getMessage());
    } catch (RuntimeException re) {
      System.out.println("Runtime Exception occurred. Program Crashed! " + re.getMessage());
      re.printStackTrace();
    }
  }

  private static boolean isInteger(String number) {
    try {
      Integer.parseInt(number);
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    }
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
