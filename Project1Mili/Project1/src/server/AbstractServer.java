package server;

import java.io.IOException;

abstract class AbstractServer {
  protected int port;

  protected AbstractServer(int port) {
    if (port > 65535 || port < 1024) {
      throw new IllegalArgumentException("Port number should be between 1024 and 65535");
    }
    this.port = port;
  }

  protected abstract void start() throws IOException;

}
