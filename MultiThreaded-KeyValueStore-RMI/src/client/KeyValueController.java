package client;

import server.data.KeyValue;
import server.extras.BadRequestException;
import server.extras.Logger;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class KeyValueController implements Controller{

  private final KeyValue<Integer, String> keyValue;
  private final Readable in;
  private final Appendable out;
  private final Logger logger = ClientLogger.instance();

  /**
   * Creates an instance of KeyValueController.
   * @param keyValue a KeyValue object
   * @param in a Readable for reading inputs
   * @param out an Appendable for printing results
   */
  public KeyValueController(KeyValue<Integer, String> keyValue, Readable in, Appendable out) {
    if(keyValue == null){
      throw new IllegalArgumentException("Key-value data is null.");
    }
    if(in == null){
      throw new IllegalArgumentException("Readable is null");
    }
    if(out == null){
      throw new IllegalArgumentException("Appendable is null");
    }

    this.keyValue = keyValue;
    this.in = in;
    this.out = out;
  }

  @Override
  public void start() throws RemoteException {
    Scanner sc = new Scanner(this.in);

    printMsg(welcome());

    while (true) {
      printMsg("Enter command: ");
      String input = sc.nextLine();
      this.logRequest(input);

      if(input.equalsIgnoreCase("q") || input.equalsIgnoreCase("Q")){
        break;
      }

      String[] commandComp = input.split(",");
      if(commandComp.length < 1){
        printMsg("Parameters entered should be separated by ',' but instead got : " + input);
      }

      commandComp = Arrays.stream(commandComp).map(String::trim)
          .collect(Collectors.toList()).toArray(new String[commandComp.length]);

      String[] commandParam = Arrays.copyOfRange(commandComp, 1, commandComp.length);

      switch (commandComp[0].toLowerCase()) {
        case "put" -> processPut(commandParam);
        case "get" -> processGet(commandParam);
        case "delete" -> processDelete(commandParam);
        default -> printMsg("Unknown command: " + commandComp[0]);
      }
    }

  }

  private void processDelete(String[] commandParam) {
    if(commandParam.length != 1){
      printMsg("Expected exactly 1 parameters: <(Integer) key>" +
          "but got : " + Arrays.toString(commandParam));
      this.logError("Expected exactly 1 parameters: <(Integer) key>" +
          "but got : " + Arrays.toString(commandParam));
      return;
    }
    String inputKey = commandParam[0];
    if(isInteger(inputKey)){
      this.logError("The key must be an integer, but got: " + inputKey);
      throw new BadRequestException("The key must be an integer, but got: " + inputKey);
    }

    int key = Integer.parseInt(inputKey);

    try{
      boolean isDeleted = this.keyValue.delete(key);
      if(!isDeleted){
        printMsg(String.format("Key %d not found.", key));
        this.logResponse(String.format("Key %d not found.", key));
      } else {
        printMsg(String.format("Success: DELETE(%d) executed.", key));
        this.logResponse(String.format("Success: DELETE(%d) executed.", key));
      }
    } catch (RemoteException e){
      this.logError("Couldn't perform the DELETE operation");
      throw new BadRequestException("Couldn't perform the DELETE operation: " + e.getMessage());
    }

  }

  private void processGet(String[] commandParam) {
    if(commandParam.length != 1){
      printMsg("Expected exactly 1 parameter: <(Integer) key>" +
          "but got : " + Arrays.toString(commandParam));
      this.logError("Expected exactly 1 parameter: <(Integer) key>" +
          "but got : " + Arrays.toString(commandParam));
      return;
    }

    String inputKey = commandParam[0];
    if(isInteger(inputKey)){
      this.logError("The key must be an integer, but got: " + inputKey);
      throw new BadRequestException("The key must be an integer, but got: " + inputKey);
    }

    int key = Integer.parseInt(inputKey);
    try{
      String value = this.keyValue.get(key);
      if(value == null) {
        this.logResponse(String.format("Key %d not found in the data stored. ", key));
        printMsg(String.format("Key %d not found in the data stored. ", key));
      } else {
        this.logResponse(String.format("Success: GET(%d) = %s", key, value));
        printMsg(String.format("Success: GET(%d) = %s", key, value));
      }
    } catch (RemoteException e){
      this.logError("Couldn't perform the GET operation: " + e.getMessage());
      throw new BadRequestException("Couldn't perform the GET operation: " + e.getMessage());
    }

  }

  private void processPut(String[] commandParam) {
    if(commandParam.length != 2){
      printMsg("Expected exactly 2 parameters: <(Integer) key, (String) value " +
          "but got : " + Arrays.toString(commandParam));
      this.logError("Expected exactly 2 parameters: <(Integer) key, (String) value " +
          "but got : " + Arrays.toString(commandParam));
      return;
    }

    String inputKey = commandParam[0];
    String value = commandParam[1];

    if(isInteger(inputKey)){
      this.logError("The key must be an integer, but got: " + inputKey);
      throw new BadRequestException("The key must be an integer, but got: " + inputKey);
    }

    try{
      int key = Integer.parseInt(inputKey);
      this.keyValue.put(key, value);
      printMsg(String.format("Success: PUT(%d, %s) executed.", key, value));
      this.logResponse(String.format("Success: PUT(%d, %s) executed.", key, value));
    } catch (IllegalArgumentException | RemoteException e){
      this.logError("Couldn't perform the PUT operation: " + e.getMessage());
      throw new BadRequestException("Couldn't perform the PUT operation: " + e.getMessage());
    }

  }

  private void printMsg(String msg){
    try{
      this.out.append(msg);
      this.out.append("\n");
    } catch (IOException ioe){
      System.out.println("Faced a problem while appending : " + ioe.getMessage());
    }
  }

  private String welcome(){
    this.logResponse("Welcome message is displayed.");
    return """
        Welcome!\s
        You can enter the following case-insensitive commands to perform operations on the key and value:\s
        \t\tPUT, <(integer) key>, <(string) value>
        \t\tGET, <(integer) key>
        \t\tDELETE, <(integer) key>
        or Enter 'q' or 'Q' to quit
        """;
  }

  private boolean isInteger(String number) {
    try {
      Integer.parseInt(number);
      return false;
    } catch (NumberFormatException nfe) {
      return true;
    }
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
}
