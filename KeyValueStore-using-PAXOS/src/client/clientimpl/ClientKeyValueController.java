package client.clientimpl;

import org.apache.log4j.Logger;
import server.paxos.ClientProposer;
import server.serverimpl.RemoteServer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientKeyValueController implements ClientController {

  private ClientProposer clientProposer;
  private final Readable in;
  private final Appendable out;

  public ClientKeyValueController(ClientProposer clientProposer, Readable in, Appendable out) {
    if (clientProposer == null) {
      throw new IllegalArgumentException("The client proposer is null.");
    }
    if (in == null) {
      throw new IllegalArgumentException("Readable is null. ");
    }
    if (out == null) {
      throw new IllegalArgumentException("Appendable is null. ");
    }
    this.clientProposer = clientProposer;
    this.in = in;
    this.out = out;
  }

  @Override
  public void start() throws RemoteException {
    Scanner scanner = new Scanner(in);

    printMsg(welcome());

    while (true) {
      printMsg("Enter command : ");
      String input = scanner.nextLine();

      if(input.equalsIgnoreCase("q") || input.equalsIgnoreCase("Q")) {
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

  private void processDelete(String[] commandParam) throws RemoteException {
    if(commandParam.length != 1){
      printMsg("Expected exactly 1 parameters: <(Integer) key>" +
          "but got : " + Arrays.toString(commandParam));
      return;
    }
    String inputKey = commandParam[0];
    if(isInteger(inputKey)){
      return;
    }

    int key = Integer.parseInt(inputKey);

    this.clientProposer.delete(key);
    printMsg(String.format("DELETE(%s): Sent.", key));

  }

  private void processGet(String[] commandParam) throws RemoteException{
    if(commandParam.length != 1){
      printMsg("Expected exactly 1 parameter: <(Integer) key>" +
          "but got : " + Arrays.toString(commandParam));
      return;
    }

    String inputKey = commandParam[0];
    if(isInteger(inputKey)){
      printMsg("The key must be an integer, but got: " + inputKey);
      return;
    }

    int key = Integer.parseInt(inputKey);
    String value = this.clientProposer.get(key);
    if(value == null) {
      printMsg(String.format("Failed : Key %d not found.", key));
    } else {
      printMsg(String.format("Success: GET(%d) = %s", key, value));
    }

  }

  private void processPut(String[] commandParam) {
    if(commandParam.length != 2){
      printMsg("Expected exactly 2 parameters: <(Integer) key, (String) value " +
          "but got : " + Arrays.toString(commandParam));
      return;
    }

    String inputKey = commandParam[0];
    String value = commandParam[1];

    if(isInteger(inputKey)){
      printMsg("The key must be an integer, but got: " + inputKey);
      return;
    }

    try{
      int key = Integer.parseInt(inputKey);
      this.clientProposer.put(key, value);
      printMsg(String.format("PUT(%d, %s): Sent.", key, value));
    } catch (IllegalArgumentException | RemoteException e){
      printMsg("Couldn't perform the PUT operation: " + e.getMessage());
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
//    this.logResponse("Welcome message is displayed.");
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

//  private void logType(String type, String message) {
//    if (message == null || type == null) {
//      throw new IllegalArgumentException("message or type is null.");
//    }
//
//    this.logger.log(String.format("%s - %s", type, message));
//  }
//
//  private void logRequest(String msg) {
//    this.logType("Request", msg);
//  }
//
//  private void logResponse(String msg) {
//    this.logType("Response", msg);
//  }
//
//  private void logError(String msg) {
//    this.logType("Error", msg);
//  }

}

