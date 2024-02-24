package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import server.logger.BadRequestException;
import server.logger.ServerLogger;
import server.logger.Logger;

public class TCPHandler implements ClientHandler{

  private final Socket socket;
  private final DataOutputStream dataOutputStream;
  private final DataInputStream dataInputStream;
  private final Logger logger = ServerLogger.instance();

  public TCPHandler(Socket socket) throws IOException {
    if (socket == null) {
      throw new IllegalArgumentException("Did not receive a socket!");
    }
    this.socket = socket;

    try {
      this.dataInputStream = new DataInputStream(this.socket.getInputStream());
      this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
    } catch (IOException ioe) {
      socket.close();
      throw new IllegalStateException("Can't read the input streams: " + ioe.getMessage());
    }
  }

  @Override
  public void response(String msg) {
    if (msg == null) {
      throw new IllegalArgumentException("the message is null.");
    }
    try {
      this.dataOutputStream.writeUTF(msg);
      this.logResponse(msg);
    } catch (IOException ioe) {
      throw new IllegalStateException("An unexpected IOException occurred while responding to" +
          " " + this.clientAddress());
    }
  }

  @Override
  public void execute() {
    logMsgClientAddress("Connected");

    try {
      while (true) {
        String input = this.dataInputStream.readUTF();
        logRequest(input);

        if(input.equalsIgnoreCase("q") || input.equalsIgnoreCase("Q")) {
        break;
        }

        try {
          new ServerCommandController(this).processCommand(input);
        } catch (BadRequestException bre) {
          this.logError(bre.getMessage());
          this.response(bre.getMessage());
        }
      }
    } catch (SocketException se) {
      this.logError("Disconnected");
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    } catch (IllegalArgumentException | IllegalStateException ie) {
      this.logError(ie.getMessage());
      this.response("Internal error occurred in sever. Try again.");
    }
    finally {
      close();
    }

  }

  private void logMsgClientAddress(String msg) {
    if (msg == null) {
      throw new IllegalArgumentException("Message is null");
    }
    this.logger.log(String.format("%s :: %s", this.clientAddress(), msg));
  }

  private String clientAddress() {
    return String.format("%s:%d", this.socket.getInetAddress(),
        this.socket.getPort());
  }

  private void close() {
    try {
      this.dataOutputStream.close();
      this.dataInputStream.close();
      this.socket.close();
      this.logMsgClientAddress("Disconnected");
    } catch (IOException ioe) {
      throw new IllegalStateException("Couldn't close the connection with " +
          this.clientAddress() + " properly.");
    }
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

  private void logType(String type, String msg) {
    if (type == null) {
      throw new IllegalArgumentException("Type is null.");
    }
    this.logMsgClientAddress(String.format("%s - %s", type, msg));
  }

}
