package client;

import server.logger.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 * This is the class for implementing the UDP client.
 */
public class UDPClient extends AbstractClient {

  /**
   * This constructs the UDP client.
   * @param port integer value between 1024 and 65535
   * @param host string value - host name
   */
  public UDPClient(int port, String host) {
    super(port, host);
  }

  @Override
  protected void start() throws IOException {
    try {
      DatagramSocket socket = new DatagramSocket();
      Scanner scanner = new Scanner(System.in);

      InetAddress inetAddress = InetAddress.getByName(this.host);
      System.out.println(welcome());

      while (true) {
        try {
          System.out.print("Enter command : ");
          String input = scanner.nextLine();

          if(input.equalsIgnoreCase("q") || input.equalsIgnoreCase("Q")) {
            break;
          }

          sendMsg(input, socket, inetAddress);

          byte[] buff = new byte[1000]; //1000 = datagram buffer size
          DatagramPacket reply = new DatagramPacket(buff, buff.length);
          socket.setSoTimeout(5000);
          socket.receive(reply);

          String response = new String(reply.getData()).trim();
          this.logResponse(response);
          System.out.printf("Response from server is %s.\n", response);
        } catch (SocketTimeoutException ste) {
          String timeout = "Server timeout. Did not receive a response from server in 5 seconds";
          System.out.println(timeout);
          this.logError(timeout);
        }
      }
    } finally {
      this.logger.log("UDP Client Terminated");
    }
  }

  private void sendMsg(String msg, DatagramSocket socket, InetAddress inetAddress) throws IOException {
    DatagramPacket req = new DatagramPacket(msg.getBytes(), msg.length(), inetAddress, this.port);
    socket.send(req);
    this.logRequest(msg);
  }

  private void logType(String type, String message) {
    if (message == null || type == null) {
      throw new IllegalArgumentException("message or type is null.");
    }

    this.logger.log(String.format("%s - %s", type, message));
  }

  private void logRequest(String msg) {
    this.logType("Request", msg);
  }

  private void logResponse(String msg) {
    this.logType("Response", msg);
  }

  private void logError(String msg) {
    this.logType("Error", msg);
  }

  /**
   * Driver method for UDP Client. This method also generates the log file (.txt) for UDP client.
   * @param args port (integer) and host (string) is taken as command line arguments
   */
  public static void main(String[] args) {
    if(args.length == 0) {
      throw new IllegalArgumentException("No arguments provided. Please provide <port> and <host>");
    }

    if (args.length != 2) {
      throw new IllegalArgumentException("Exactly two arguments are needed : <port> <host> ");
    }

    try {
      String host = args[1];
      int port = Integer.parseInt(args[0]);
      Logger logger = ClientLogger.instance(new FileWriter("./udp-client-log.txt"));
      new UDPClient(port, host).start();
    } catch (NumberFormatException nfe) {
      System.out.println("Invalid arguments provided");
      System.exit(1);
    } catch (IOException ioe) {
      System.out.println("Could not connect to the server");
      System.exit(1);
    }
  }
}
