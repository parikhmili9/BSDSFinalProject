package server.driver;

import server.paxos.NodePaxos;
import server.paxos.NodePaxosImpl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ServerDriver {
  public static void main(String[] args) {
    if(args.length < 4) {
      System.out.println("Please enter correct arguments.");
      return;
    }

    int port = -1;
    String name = "";
    String filePath = "";

    for(int i = 0; i < args.length; i += 2) {
      switch (args[i].toLowerCase()) {
        case "-p":
          if(ServerDriver.isIntegerParseable(args[i+1])) {
            port = Integer.parseInt(args[i+1]);
          }
          break;

        case "-n":
          name = args[i+1];
          break;

        case "-f":
          filePath = args[i+1];
          break;

        default:
          break;
      }
    }

    if(port == -1 || name.isBlank()) {
      System.out.printf("Please enter correct arguments!");
      return;
    }

    NodePaxos nodePaxos = new NodePaxosImpl();

    try {
      Registry registry = LocateRegistry.createRegistry(port);
      UnicastRemoteObject.exportObject(nodePaxos, 0);
      registry.rebind(name, nodePaxos);
      System.out.println(String.format("Exported the remote object %s on port %s", name, port));

      Scanner scanner;
      if(filePath.isBlank()) {
        scanner = new Scanner(System.in);
      } else {
        Readable rd = new FileReader(filePath);
        scanner = new Scanner(rd);
      }

      System.out.println("Once all servers are ready, enter hostname, name and port number" +
          " of other servers on separate lines. " +
          "Enter 'qe' after you're finished entering.");
      while (true) {
        String input = scanner.nextLine();
        if(input.equalsIgnoreCase("qe")) {
          break;
        }
        String[] components = input.split(",");
        String hostname = components[0].strip();
        String sName = components[1].strip();
        if(!isIntegerParseable(components[2].strip())) {
          System.out.println("Enter a valid port number!");
          continue;
        }
        int sPort = Integer.parseInt(components[2].strip());
        nodePaxos.registerNode(hostname, sName, sPort);
      }
      System.out.println("Ready to start the actions!");
    } catch (RemoteException re) {
      System.out.println("Remote Exception occurred: " + re.getMessage());
    } catch (RuntimeException re) {
      System.out.println("Oops.. Server crashes! " + re.getMessage());
    } catch (FileNotFoundException fnfe) {
      System.out.println("Config file not found." + fnfe.getMessage());
    }
  }

  private static boolean isIntegerParseable(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
