package client;

import server.data.KeyValue;
import server.extras.Logger;
import server.server.ServerLogger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

/**
 * The client class finds a registry, initializes the client, and finds the distant object.
 */
public class RMIClient {

  private final String name;
  private final String host;
  private final int port;

  private static final String DEFAULT_NAME = "localKeyValue";
  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_PORT = 1029;

  /**
   * The constructor of our RMI client, RMIClient.java, accepts three parameters: name, host, and port.
   * @param name name
   * @param host host ip/name
   * @param port port number
   */
  public RMIClient(String name, String host, int port) {
    if(name == null || host == null){
      throw new IllegalArgumentException("Name is null");
    }
    if (port > 65535 || port < 1024) {
      throw new IllegalArgumentException("Port number should be between 1024 and 65535");
    }
    this.name = name;
    this.host = host;
    this.port = port;
  }

  /**
   * When this method is invoked, the client uses the specified host and port to find a registry.
   * After locating this register, it searches the registry for the remote object bound to name.
   * The remaining tasks are then handed off to the Controller. Additionally, the client detects any
   * RemoteExceptions or NotBoundException that might arise from the registry not being located or the
   * object not being tied to the specified name, and emits an error message to the console.
   */
  public void start(){
    try {
      Registry registry = LocateRegistry.getRegistry(this.host, this.port);
      ClientLogger.instance().log("Registry located.");

      KeyValue<Integer, String> keyValue = (KeyValue<Integer, String>) registry.lookup(this.name);
      ClientLogger.instance().log("Found the registry bound to the name.");

      new KeyValueController(keyValue, new InputStreamReader(System.in), System.out).start();
    } catch (NotBoundException e){
      ClientLogger.instance().log("Error: " + e.getMessage());
      System.out.println("Name is not bound to any registry.");
      System.out.println(e.getMessage());
    } catch (RemoteException re){
      ClientLogger.instance().log("Error: " + re.getMessage());
      System.out.println("There was a problem finding the registry.");
      System.out.println(re.getMessage());
    }
  }


  /**
   * Main method to start the client. Can have multiple instances of client running together.
   * @param args Takes Name, host ip/name and port number as command line arguments.
   */
  public static void main(String[] args){
    String host = DEFAULT_HOST;
    String name = DEFAULT_NAME;
    int port = DEFAULT_PORT;

    List<String> argsList = new ArrayList<String>(List.of(args));

    if(argsList.contains("-h")){
      int index = argsList.indexOf("-h");
      if(argsList.size() > index + 1){
        host = argsList.get(index + 1);
      } else {
        System.out.println("No host host provided. Using default host : " + DEFAULT_HOST);
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

    if(argsList.contains("-n")){
      int index = argsList.indexOf("-n");
      if(argsList.size() > index + 1){
        name = argsList.get(index + 1);
      } else {
        System.out.println("No name host provided. Using default name : " + DEFAULT_NAME);
      }
    }

    System.out.println("Port: " + port);
    System.out.println("Host: " + host);
    System.out.println("Name: " + name);

    try{
      Logger log = ClientLogger.instance(new FileWriter("./rmi-client-log.txt"));
      new RMIClient(name, host, port).start();
    } catch (RuntimeException re){
      ClientLogger.instance().log("Error: " + re.getMessage());
      System.out.println("Some error occurred in running the client: " + re.getMessage());
    } catch (IOException e) {
      ClientLogger.instance().log("Error: " + e.getMessage());
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
