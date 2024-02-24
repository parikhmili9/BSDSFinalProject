package server.serverimpl;

import logger.Logger;
import server.data.LockKeyValue;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExecutableRemoteServerImpl implements ExecutableRemoteServer {

  private final LockKeyValue<Integer, String> keyValue;
  private final Map<String, Action> actionsMap;

  private final Logger logger;

  public ExecutableRemoteServerImpl(LockKeyValue<Integer, String> keyValue,
                                    Logger logger) {
    if(keyValue == null){
      throw new IllegalArgumentException("Data storage is null");
    }
    this.keyValue = keyValue;
    this.actionsMap = new ConcurrentHashMap<>();
    this.logger = logger;
  }

  @Override
  public boolean prepare(Action action, String actionId) throws RemoteException {
    if(action == null || actionId == null) {
      throw new IllegalArgumentException("Arguments are null");
    }
    this.actionsMap.put(actionId, action);

    boolean res = this.keyValue.lock(action.key());
//    this.logInfo(String.format("Prepare operation %s - %s", actionId, res));
    return res;
  }

  @Override
  public boolean execute(String actionId) throws RemoteException {
    if(!this.actionsMap.containsKey(actionId)) {
//      this.logInfo(String.format("Execute operation %s - %s; no such operation", actionId, false));
      return false;
    }
    Action action =actionsMap.get(actionId);
    if(!this.keyValue.unlock(action.key())) {
//      this.logInfo(String.format("Execute operation %s - %s; couldn't unlock", actionId, false));
      return false;
    }

    boolean executed = action.execute(this.keyValue);
    if(executed) {
      actionsMap.remove(actionId);
    }
//    this.logInfo(String.format("Execute operation %s - %s", actionId, executed));
    return executed;
  }

  @Override
  public boolean abort(String actionId) throws RemoteException {
    if(!this.actionsMap.containsKey(actionId)) {
//      this.logInfo(String.format("Abort operation %s - %s; no such operation", actionId, false));
      return false;
    }
    Action action = this.actionsMap.get(actionId);

    this.keyValue.unlock(action.key());
    this.actionsMap.remove(actionId);
//    this.logInfo(String.format("Abort operation %s - %s", actionId, true));
    return true;
  }

//  private void logType(String type, String message) {
//    if (message == null || type == null) {
//      throw new IllegalArgumentException("message or type is null.");
//    }
//    this.logger.log(String.format("%s - %s", type, message));
//  }

//  private void logRequest(String msg) {
//    this.logType("Request", msg);
//  }
//
//  private void logResponse(String msg) {
//    this.logType("Response", msg);
//  }

//  private void logInfo(String msg) {
//    this.logType("Info", msg);
//  }

//  private void logError(String msg) {
//    this.logType("Error", msg);
//  }

}
