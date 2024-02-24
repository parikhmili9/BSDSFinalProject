package server.server;

import server.data.KeyValue;
import server.data.KeyValueImpl;
import server.extras.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * The server class is used to build a server and initializes the server objects
 */
public class RMIServer extends AbstractServer{

  private final String name;
  private static final String DEFAULT_NAME = "localKeyValue";
  private static final int DEFAULT_PORT = 1029;

  /**
   * Constructs the rmi server with port and name.
   * @param port port number
   * @param name name
   */
  public RMIServer(int port, String name) {
    super(port);
    if(name == null){
      throw new IllegalArgumentException("Host name is null");
    }
    this.name = name;
  }

  /**
   *  The server starts a registry at the specified port after the start method has been called, and
   *  it binds the KeyValue remote object to the specified name. Any RemoteExceptions that may arise while
   *  the program is running are caught by the start method, which then prints the message to the console.
   */
  @Override
  protected void start() {
    try{
      KeyValue<Integer, String> keyValue = KeyValueImpl.instance();
      System.out.println("Created an instance of the data (KeyValue).");
      ServerLogger.instance().log("Created an instance of the data (KeyValue).");
      ServerLogger.instance().log("""
          Pre-populated values are:\s
           1 - one
           2 - two
           3 - three
           4 - four
           5 - five""");

      Registry registry = LocateRegistry.createRegistry(port);
      System.out.println("Registry created.");
      ServerLogger.instance().log("Registry created.");

      keyValue = (KeyValue<Integer, String>) UnicastRemoteObject.exportObject(keyValue, 0);
      System.out.println("KeyValue data exported");
      ServerLogger.instance().log("KeyValue data exported");

      registry.rebind(this.name, keyValue);
      System.out.println("Server started.");
      ServerLogger.instance().log("Server has started.");
    } catch (RemoteException re){
      ServerLogger.instance().log("Error: " + re.getMessage());
      re.getMessage();
    }

  }

  /**
   * Main method to start the server.
   * @param args Takes Name and port number as command line arguments.
   */
  public static void main(String[] args){
    String name = DEFAULT_NAME;
    int port = DEFAULT_PORT;

    List<String> argsList = new ArrayList<String>(List.of(args));

    if(argsList.contains("-n")){
      int index = argsList.indexOf("-n");
      if(argsList.size() > index + 1){
        name = argsList.get(index + 1);
      } else {
        System.out.println("No name name provided. Using default name : " + DEFAULT_NAME);
      }
    }

    if(argsList.contains("-p")){
      int index = argsList.indexOf("-p");
      if(argsList.size() > index + 1 && isInteger(argsList.get(index + 1))){
        port = Integer.parseInt(argsList.get(index + 1));
      } else {
        System.out.println("Invalid port entered. Using default port : " + DEFAULT_PORT);
      }
    }

    System.out.println("Port: " + port);
    System.out.println("Host: " + name);
    System.out.println();

    try {
      Logger log = ServerLogger.instance(new FileWriter("./rmi-server-log.txt"));
      new RMIServer(port, name).start();
    } catch (RuntimeException re){
      ServerLogger.instance().log("Error: " + re.getMessage());
      System.out.println(re.getMessage());
    } catch (IOException e) {
      ServerLogger.instance().log("Could not start the server: " + e.getMessage());
      System.out.println("Issue in logging: " + e.getMessage());
    }
  }

  private static boolean isInteger(String str){
    try{
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException nfe){
      return false;
    }
  }
}
