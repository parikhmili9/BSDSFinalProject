package server.serverimpl;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ExecutableRemoteServer extends Remote {

  /**
   * Prepares the server to execute the commit. Upon receiving the message,
   * places a lock on the resource about to be modified so that no other thread can change its value.
   * @param action the operation to be performed
   * @param actionId unique identifier for the operation
   * @return whether the server prepared for the operation. returns false if the resource couldn't be locked.
   */
  boolean prepare(Action action, String actionId) throws RemoteException;

  /**
   * Executes the operation with the specified operationId on the datastore. MUST prepare the operation first. Unlocks the resource
   * on which the operation is to be performed before executing.
   * @param actionId unique identifier of the operation to be executed
   * @return whether the operation was executed. Returns false if no such operation is committed or if the resource couldn't be unlocked.
   */
  boolean execute(String actionId) throws RemoteException;

  /**
   * Aborts the commit represented by the given operationId.
   * @param actionId unique identifier for the operation to be aborted
   * @return whether the operation was aborted
   */
  boolean abort(String actionId) throws RemoteException;
}
