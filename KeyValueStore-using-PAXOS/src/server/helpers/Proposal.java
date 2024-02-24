package server.helpers;

import java.io.Serializable;

public class Proposal implements Serializable {
  private final long proposalId;
  private final long proposerId;
  private final int logPosition;

  public Proposal(long proposalId, long proposerId, int logPosition) {
    this.proposalId = proposalId;
    this.proposerId = proposerId;
    this.logPosition = logPosition;
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
}
