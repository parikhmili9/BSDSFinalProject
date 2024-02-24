package client;

import server.logger.Logger;

import java.io.IOException;

/**
 * Abstract class for client (TCP & UDP both). Contains welcome() method with a welcome message and
 * start() method to start the client.
 */
abstract class AbstractClient {
  protected int port;
  protected  String host;
  protected Logger logger = ClientLogger.instance();

  protected AbstractClient(int port, String host) {
    if(host == null || host.length() == 0) {
      throw new IllegalArgumentException("Invalid host");
    }
    if(port < 1024 || port > 65535) {
      throw new IllegalArgumentException("Port number should be between 1024 and 65535");
    }
    this.port = port;
    this.host = host;
  }

  protected String welcome() {

    return "Welcome! \n" +
        "You can enter the following case-insensitive commands to perform operations on the key and value: \n" +
        "\t\tPUT, <(integer) key>, <(string) value>\n" +
        "\t\tGET, <(integer) key>\n" +
        "\t\tDELETE, <(integer) key>\n" +
        "or Enter 'q' or 'Q' to quit\n";
  }

  protected abstract void start() throws IOException;

}
