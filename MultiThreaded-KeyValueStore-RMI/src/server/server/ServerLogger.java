package server.server;

import server.extras.Logger;
import server.extras.TimeStampLogger;

import java.io.Writer;

/**
 * Logger class for the server, extends the TimeStampLogger.
 */
public class ServerLogger extends TimeStampLogger {

  private static Logger instance;

  private ServerLogger(Writer log) {
    super(log);
  }

  /**
   * This method creates an instance of the logger.
   * @param log Writer object for all log entries
   * @return instance of the logger
   */
  public static Logger instance(Writer log) {
    if (instance == null) {
      instance = new ServerLogger(log);
    }

    return instance;
  }

  /**
   * This method creates an instance of the logger for servers.
   * @return instance of the logger
   */
  public static Logger instance() {
    if (instance == null) {
      throw new IllegalArgumentException("The logger has not been created."
          + " Please provide an appendable to create one.");
    }
    return instance;
  }

}
