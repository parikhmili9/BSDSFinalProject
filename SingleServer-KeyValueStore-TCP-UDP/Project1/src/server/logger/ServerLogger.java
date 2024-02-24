package server.logger;

import java.io.Writer;

public class ServerLogger extends TimeStampLogger{

  private static Logger instance;

  private ServerLogger(Writer log) {
    super(log);
  }

  public static Logger instance(Writer log) {
    if (instance == null) {
      instance = new ServerLogger(log);
    }

    return instance;
  }

  public static Logger instance() {
    if (instance == null) {
      throw new IllegalArgumentException("The logger has not been created."
          + " Please provide an appendable to create one.");
    }
    return instance;
  }

}
