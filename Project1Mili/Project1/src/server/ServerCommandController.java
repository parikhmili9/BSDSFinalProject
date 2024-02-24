package server;

import server.data.KeyValue;
import server.data.KeyValueImpl;
import server.logger.BadRequestException;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.Arrays;


public class ServerCommandController implements CommandController {

  private final ClientHandler clientHandler;
  private final KeyValue<Integer, String> keyValue;

  public ServerCommandController(ClientHandler clientHandler) {
    if(clientHandler == null) {
      throw new IllegalArgumentException("Client handler should not be null.");
    }
    this.clientHandler = clientHandler;
    this.keyValue = KeyValueImpl.instance();
  }

  @Override
  public void processCommand(String command) {
    String[] commandComp = command.split(",");

    if (commandComp.length < 1) {
      throw new BadRequestException("Command parameters should be seperated by ',', but got: " + command);
    }

    commandComp = Arrays.stream(commandComp)
        .map(String::trim).toList()
        .toArray(new String[commandComp.length]);

    String[] commandParam = Arrays.copyOfRange(commandComp, 1,
        commandComp.length);

    switch (commandComp[0].toLowerCase()) {
      case "put" -> processPut(commandParam);
      case "get" -> processGet(commandParam);
      case "delete" -> processDelete(commandParam);
      default -> throw new BadRequestException("Unknown command: " + commandComp[0]);
    }
  }

  private void processPut(String[] commandParam) {
    if (commandParam.length != 2) {
      throw new BadRequestException("Expected exactly 2 parameters <key>, <value> but got: "
          + Arrays.toString(commandParam));
    }

    String inputKey = commandParam[0];
    String value = commandParam[1];

    if (isInteger(inputKey)) {
      throw new BadRequestException("The key must be an integer, but got: " + inputKey);
    }

    try {
      int key = Integer.parseInt(inputKey);
      this.keyValue.put(key, value);
      this.clientHandler.response(String.format("Success: PUT(%d, %s) executed.", key, value));
    } catch (IllegalArgumentException iae) {
      throw new BadRequestException("Couldn't perform the PUT operation: " + iae.getMessage());
    }
  }

  private void processGet(String[] commandParam) {
    if (commandParam.length != 1) {
      throw new BadRequestException("Expected exactly 1 parameter: <key> but got: "
          + Arrays.toString(commandParam));
    }
    String inputKey = commandParam[0];

    if (isInteger(inputKey)) {
      throw new BadRequestException("The key must be an integer, but got: " + inputKey);
    }
    int key = Integer.parseInt(inputKey);
    String value = this.keyValue.get(key);

    if (value == null) {
      this.clientHandler.response(String.format("Key %d not found.", key));
    } else {
      this.clientHandler.response(String.format("Success: GET(%d) = %s", key, value));
    }
  }

  private void processDelete(String[] commandParam) {
    if (commandParam.length != 1) {
      throw new BadRequestException("Expected exactly 1 parameter: <key> but got: "
          + Arrays.toString(commandParam));
    }
    String inputKey = commandParam[0];

    if (isInteger(inputKey)) {
      throw new BadRequestException("The key must be an integer, but got: " + inputKey);
    }
    int key = Integer.parseInt(inputKey);

    boolean isDeleted = this.keyValue.delete(key);

    if (!isDeleted) {
      this.clientHandler.response(String.format("Key %d not found.", key));
    } else {
      this.clientHandler.response(String.format("Success: DELETE(%d) executed.", key));
    }

  }

  private boolean isInteger(String number) {
    try {
      Integer.parseInt(number);
      return false;
    } catch (NumberFormatException nfe) {
      return true;
    }
  }
}
