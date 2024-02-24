package server.helpers;

import server.serverimpl.Action;

import java.io.Serializable;

public class AcceptRequest implements Serializable {
  private final long proposalId;
  private final long proposerId;
  private final int logPosition;
  private final Action action;

  public AcceptRequest(long proposalId, long proposerId, int logPosition, Action action) {
    this.proposalId = proposalId;
    this.proposerId = proposerId;
    this.logPosition = logPosition;
    this.action = action;
  }

  public long getProposalId() {
    return this.proposalId;
  }

  public long getProposerId() {
    return this.proposerId;
  }

  public int getLogPosition() {
    return this.logPosition;
  }

  public Action getAction() {
    return this.action;
  }
}
