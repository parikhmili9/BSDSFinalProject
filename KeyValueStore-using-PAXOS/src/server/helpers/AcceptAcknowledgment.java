package server.helpers;

import server.serverimpl.Action;

import java.io.Serializable;

public class AcceptAcknowledgment implements Serializable {
  private final long proposalId;
  private final int logPosition;
  private final int acceptorId;
  private final Action action;

  public AcceptAcknowledgment(long proposalId, int logPosition, int acceptorId, Action action) {
    this.proposalId = proposalId;
    this.logPosition = logPosition;
    this.acceptorId = acceptorId;
    this.action = action;
  }

  public AcceptAcknowledgment(int acceptorId, AcceptRequest acceptRequest) {
    this(acceptRequest.getProposalId(), acceptRequest.getLogPosition(), acceptorId, acceptRequest.getAction());
  }

  public long getProposalId() {
    return this.proposalId;
  }

  public int getLogPosition() {
    return this.logPosition;
  }

  public int getAcceptorId() {
    return this.acceptorId;
  }

  public Action getAction() {
    return this.action;
  }
}
