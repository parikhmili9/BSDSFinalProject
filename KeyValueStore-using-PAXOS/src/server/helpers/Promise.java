package server.helpers;

import java.io.Serializable;

public class Promise implements Serializable {
  private long proposalId;
  private int acceptorId;
  private AcceptAcknowledgment acceptAcknowledgment;

  public Promise(long proposalId, int acceptorId, AcceptAcknowledgment acceptAcknowledgment) {
    this.proposalId = proposalId;
    this.acceptorId = acceptorId;
    this.acceptAcknowledgment = acceptAcknowledgment;
  }

  public Promise(long proposalId, int acceptorId) {
    this(proposalId, acceptorId, null);
  }

  public long getProposalId() {
    return this.proposalId;
  }

  public int getAcceptorId() {
    return this.acceptorId;
  }

  public AcceptAcknowledgment getAcceptAcknowledgment() {
    return this.acceptAcknowledgment;
  }
}
