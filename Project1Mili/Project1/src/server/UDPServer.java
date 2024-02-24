package server;

import server.logger.Logger;
import server.logger.ServerLogger;

import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer extends AbstractServer {

  private final static int DATAGRAM_BUFFER_SIZE_BYTES = 1000;

  public UDPServer(int port) {
    super(port);
  }

  @Override
  protected void start() throws IOException {
    DatagramSocket serverSocket = new DatagramSocket(port);

    String log = String.format("Server started and is running at: %s:%s"
        , serverSocket.getLocalAddress(), serverSocket.getLocalPort());
    System.out.println(log);
    ServerLogger.instance().log(log);

    while (true) {
      try {
        byte[] buffer = new byte[DATAGRAM_BUFFER_SIZE_BYTES];
        DatagramPacket request = new DatagramPacket(buffer, DATAGRAM_BUFFER_SIZE_BYTES);
        serverSocket.receive(request);
        new UDPHandler(request, serverSocket).execute();
      } catch (IllegalArgumentException | IllegalStateException | IOException e) {
        ServerLogger.instance().log("Error: " + e.getMessage());
      }
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
      Logger log = ServerLogger.instance(new FileWriter("./udp-server-log.txt"));
      new UDPServer(port).start();
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
