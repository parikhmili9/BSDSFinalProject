package server.helpers;

import server.serverimpl.Action;

public class ServerProposal extends Proposal {
  private Action action;

  public ServerProposal(long proposalId, long proposerId, int logPosition, Action action) {
    super(proposalId, proposerId, logPosition);
    this.action = action;
  }

  public Action getAction() {
    return this.action;
  }
}
