package client.driver;

import client.clientimpl.RMIClient;


public class ClientDriver {

  public static void main(String[] args) {
    String host = "";
    String name = "";
    int port = -1;

    int argumentLength = args.length;

    for(int i = 0; i < argumentLength; i += 2) {
      switch (args[i].toLowerCase()) {
        case "-h":
          if(i + 1 < argumentLength) {
            host = args[i + 1];
          }
          break;

        case "-n":
          if(i + 1 < argumentLength) {
            name = args[i + 1];
          }
          break;

        case "-p":
          if (i + 1 < argumentLength && isInteger(args[i + 1])) {
            port = Integer.parseInt(args[i + 1]);
          }
          break;

        default:
          break;
      }
    }

    if(host.isBlank() || name.isBlank() || port < 0) {
      System.out.println("Enter valid arguments! ");
      return;
    }

    System.out.println("Port: " + port);
    System.out.println("Host: " + host);
    System.out.println("Name: " + name);

    try{
      new RMIClient(name, host, port).start();
    } catch (RuntimeException e) {
      System.out.println("Crashed! Runtime exception occurred : " +e.getMessage());
      e.printStackTrace();
    }


  }

  private static boolean isInteger(String n) {
    try {
      Integer.parseInt(n);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
