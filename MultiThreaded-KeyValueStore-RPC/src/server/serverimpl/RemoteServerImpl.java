package server.serverimpl;

import logger.Logger;
import server.data.ImmutableKeyValue;
import server.servermanager.ServerCoordinator;

import java.rmi.RemoteException;

public class RemoteServerImpl implements RemoteServer {

  private final ImmutableKeyValue<Integer, String> keyValue;
  private final ServerCoordinator coordinator;
  private Logger logger;

  public RemoteServerImpl(ImmutableKeyValue<Integer, String> keyValue,
                          ServerCoordinator coordinator, Logger logger) {
    if(keyValue == null) {
      throw new IllegalArgumentException("The keyValue is null.");
    }
    if(coordinator == null) {
      throw new IllegalArgumentException("The coordinator is nulL");
    }
    this.keyValue = keyValue;
    this.coordinator = coordinator;
    this.logger = logger;
  }

  @Override
  public String get(Integer key) throws RemoteException {
    String getKey = this.keyValue.get(key);
//    this.logInfo(String.format("GET(%d) -> %s", key, getKey));
    return getKey;
  }

  @Override
  public boolean put(Integer key, String value) throws RemoteException {
    boolean res = this.coordinator.put(key, value);
//    this.logInfo(String.format("PUT(%d, %s) -> %s", key, value, res));
    return res;
  }

  @Override
  public boolean delete(Integer key) throws RemoteException {
    boolean res = this.coordinator.delete(key);
//    this.logInfo(String.format("DELETE(%d) -> %s", key, res));
    return res;
  }

//  private void logType(String type, String message) {
//    if (message == null || type == null) {
//      throw new IllegalArgumentException("message or type is null.");
//    }
//    this.logger.log(String.format("%s - %s", type, message));
//  }
//
//  private void logInfo(String msg) {
//    this.logType("Info", msg);
//  }
}
