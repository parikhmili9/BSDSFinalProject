package client;

import server.logger.Logger;
import server.logger.TimeStampLogger;

import java.io.Writer;

/**
 * This class represents a logger for the client.
 */
public class ClientLogger extends TimeStampLogger {

  private static Logger instance;

  private ClientLogger(Writer log) {
    super(log);
  }

  /**
   * This method creates an instance of the logger.
   * @param log Writer object for all log entries
   * @return instance of the logger
   */
  public static Logger instance(Writer log) {
    if (instance == null) {
      instance = new ClientLogger(log);
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
