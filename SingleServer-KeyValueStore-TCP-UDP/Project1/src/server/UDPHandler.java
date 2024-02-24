package server;

import server.logger.BadRequestException;
import server.logger.Logger;
import server.logger.ServerLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPHandler implements ClientHandler{

  private final DatagramPacket requestPacket;
  private final DatagramSocket serverSocket;
  private final Logger logger = ServerLogger.instance();

  public UDPHandler(DatagramPacket requestPacket, DatagramSocket serverSocket) {
    if (requestPacket == null || serverSocket == null) {
      throw new IllegalArgumentException("The packet or server socket is null.");
    }
    this.requestPacket = requestPacket;
    this.serverSocket = serverSocket;
  }

  @Override
  public void response(String msg) {
    if (msg == null) {
      throw new IllegalArgumentException("Message is null.");
    }
    byte[] messageBytes = msg.getBytes();

    try {
      DatagramPacket response = new DatagramPacket(messageBytes,
          messageBytes.length,
          this.requestPacket.getAddress(), this.requestPacket.getPort());
      serverSocket.send(response);
      logResponse(msg);
    } catch (IOException ioe) {
      throw new IllegalStateException("An unexpected IOException occurred while responding to "
          + this.clientAddress());
    }

  }

  @Override
  public void execute() {
    String input = new String(this.requestPacket.getData());

    try {
      new ServerCommandController(this).processCommand(input);
    } catch (BadRequestException bre) {
      this.logError(bre.getMessage());
      this.response(bre.getMessage());
    } catch (IllegalArgumentException | IllegalStateException e) {
      this.logError(e.getMessage());
      this.response("Internal error occurred in sever. Try again.");
    }
  }

  private String clientAddress() {
    return String.format("%s:%d", this.requestPacket.getAddress(),
        this.requestPacket.getPort());
  }

  private void logMsgClientAddress(String msg) {
    if (msg == null) {
      throw new IllegalArgumentException("Message is null");
    }

    assert this.logger != null;
    this.logger.log(String.format("%s :: %s", this.clientAddress(), msg));
  }

  private void logType(String type, String msg) {
    if (type == null) {
      throw new IllegalArgumentException("Type is null.");
    }
    this.logMsgClientAddress(String.format("%s - %s", type, msg));
  }

  private void logResponse(String msg) {
    this.logType("Response", msg);
  }

  private void logError(String msg) {
    this.logType("Error", msg);
  }

}
