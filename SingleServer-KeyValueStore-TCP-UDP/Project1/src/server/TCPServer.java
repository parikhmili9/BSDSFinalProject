package server;

import server.logger.Logger;
import server.logger.ServerLogger;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends AbstractServer{

  public TCPServer(int port) throws IOException {
    super(port);
  }

  @Override
  protected void start() throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);

    String log = String.format("Server started and is running at: %s:%s"
        , serverSocket.getInetAddress(), serverSocket.getLocalPort());

    System.out.println(log);
    ServerLogger.instance().log(log);

    while (true) {
      Socket socket = serverSocket.accept();
      new TCPHandler(socket).execute();
    }
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException("No port given.");
    }

    if (args.length > 1) {
      throw new IllegalArgumentException("More than one argument given. Only port is required.");
    }

    try {
      int port = Integer.parseInt(args[0]);
      Logger log = ServerLogger.instance(new FileWriter("./tcp-server-log.txt"));
      new TCPServer(port).start();
    } catch (NumberFormatException nfe) {
      System.out.println("Port must be an integer.");
      System.exit(1);
    } catch (IllegalArgumentException | IllegalStateException e) {
      ServerLogger.instance().log("Error: " + e.getMessage());
      System.exit(1);
    } catch (IOException ioe) {
      System.out.println("Could not start the server: " + ioe.getMessage());
      ServerLogger.instance().log("Could not start the server: " + ioe.getMessage());
      System.exit(1);
    }

  }
}
