package server.paxos;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.data.KeyValue;
import server.data.KeyValueImpl;
import server.helpers.*;
import server.serverimpl.Action;
import server.serverimpl.DeleteAction;
import server.serverimpl.PutAction;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NodePaxosImpl implements NodePaxos{

  private final KeyValue<Integer, String> keyValue;

  private final Map<Long, ServerProposal> proposalsSentById;

  private final Map<Integer, List<Promise>> promisesReceivedForLogPosition;

  private final Map<Integer, Proposal> proposalsReceivedForLogPosition;

  private final Map<Integer, List<AcceptAcknowledgment>> acceptAcknowledgmentsForLogPosition;

  private final Map<Integer, AcceptAcknowledgment> acceptAcknowledgmentMap;

  private final Map<Integer, AcceptRequest> proposalsAccepted;

  private final Map<Integer, ServerProposer> proposersById;

  private final Map<Integer, Learner> learnersById;

  private final Map<Integer, Acceptor> acceptorsById;

  private int currLogPosition;
  private final int acceptorId;
  private final int proposerId;
  private final int learnerId;

  private final Logger ACCEPTOR_LOGGER;
  private final Logger PROPOSER_LOGGER;
  private final Logger LEARNER_LOGGER;
  private final Logger PAXOS_LOGGER;

  public NodePaxosImpl() {
    this.keyValue = new KeyValueImpl<>();
    this.keyValue.put(1, "One");
    this.keyValue.put(2, "Two");
    this.keyValue.put(3, "Three");
    this.keyValue.put(4, "Four");
    this.keyValue.put(5, "Five");

    this.proposalsSentById = new ConcurrentHashMap<>();
    this.promisesReceivedForLogPosition = new ConcurrentHashMap<>();
    this.proposalsReceivedForLogPosition = new ConcurrentHashMap<>();
    this.acceptAcknowledgmentsForLogPosition = new ConcurrentHashMap<>();
    this.proposalsAccepted = new ConcurrentHashMap<>();
    this.acceptAcknowledgmentMap = new ConcurrentHashMap<>();
    this.currLogPosition = 0;
    int maxBound = 999999;
    int minBound = 1000;

    int randomId = getRandomIntBtw(maxBound, minBound);
    this.acceptorId = randomId;
    this.proposerId = randomId;
    this.learnerId = randomId;

    this.acceptorsById = new HashMap<>();
    this.acceptorsById.put(this.acceptorId, this);

    this.proposersById = new HashMap<>();
    this.proposersById.put(this.proposerId, this);

    this.learnersById = new HashMap<>();
    this.learnersById.put(this.learnerId, this);

    ACCEPTOR_LOGGER = LogManager.getLogger(String.format("Acceptor@%s", acceptorId));
    LEARNER_LOGGER = LogManager.getLogger(String.format("Learner@%s", learnerId));
    PROPOSER_LOGGER = LogManager.getLogger(String.format("Proposer@%s", proposerId));
    PAXOS_LOGGER = LogManager.getLogger("PaxosNode");
  }

  @Override
  public void propose(Proposal proposal) throws RemoteException {

    ACCEPTOR_LOGGER.info(String.format("Proposal %s: Received for Log #%s",
        proposal.getProposalId(), proposal.getLogPosition()));
    // Returning default to save writing unnecessary if-else
    Proposal previousProposal =
        proposalsReceivedForLogPosition.getOrDefault(proposal.getLogPosition(), proposal);


    if(proposal.getProposalId() <= previousProposal.getProposalId()) {
      ACCEPTOR_LOGGER.info("Proposal %s: Failed; Another proposal with higher ID received.");
      return;
    }

    // Now we know that this is the Max ID for this log position that we have seen
    if(proposalsAccepted.containsKey(proposal.getLogPosition())) {
      // Since we've already accepted a proposal, we want to piggyback the value
      AcceptRequest acceptedProposal = proposalsAccepted.get(proposal.getLogPosition());

      ACCEPTOR_LOGGER.info(String.format("Proposal %s: Piggybacking value to proposal before sending."
      ,proposal.getProposalId()));

      System.out.printf("Proposal %s: - Enter 'spr' to send to promise to proposer or" +
          " 'ipr' to ignore proposal.%n", proposal.getProposalId());

      Scanner scanner = new Scanner(System.in);
      while (true) {
        String input = scanner.next();
        if(input.equalsIgnoreCase("spr")) {
          break;
        }
        if(input.equalsIgnoreCase("ipr")) {
          return;
        }
      }

      this.proposalsReceivedForLogPosition.put(proposal.getLogPosition(), proposal);
      Promise promise = new Promise(proposal.getProposalId(), this.acceptorId,
          new AcceptAcknowledgment(this.acceptorId, acceptedProposal));

      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            proposersById.get(proposal.getProposerId()).promise(promise);
          } catch (RemoteException re) {
            throw new RuntimeException(re);
          }
        }
      }).start();
    } else {
      System.out.printf("Proposal %s: - Enter 'spr' to send to promise to proposer or 'ipr'" +
          " to ignore proposal.%n", proposal.getProposalId());
      Scanner scanner = new Scanner(System.in);
      while (true) {
        String input = scanner.next();
        if(input.equalsIgnoreCase("spr")) {
          break;
        }
        if(input.equalsIgnoreCase("ipr")) {
          return;
        }
      }
      // We can safely say that this proposal is going to be accepted.
      this.proposalsReceivedForLogPosition.put(proposal.getLogPosition(), proposal);

      // We know that we have not accepted any proposals for the log position yet.
      // So we just send back the promise
      ACCEPTOR_LOGGER.info(String.format("Proposal %s: Promising", proposal.getProposalId()));

      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            proposersById.get(proposal.getProposerId())
                .promise(new Promise(proposal.getProposalId(), acceptorId));
          } catch (RemoteException re) {
            throw new RuntimeException(re);
          }
        }
      }).start();
    }
  }

  @Override
  public void accept(AcceptRequest acceptRequest) throws RemoteException {
    ACCEPTOR_LOGGER.info(String.format("Proposal %s: Accept Request received"
        , acceptRequest.getProposalId()));
    if(!this.proposalsReceivedForLogPosition.containsKey(acceptRequest.getLogPosition())) {
      ACCEPTOR_LOGGER.info(String.format("Proposal %s: Accept request received for a proposal " +
              "which was not promised", acceptRequest.getProposerId()));
//      System.out.printf("Proposal %s: Accept request received for a proposal which" +
//          " was not promised", acceptRequest.getProposerId());
      return;
    }

    if(acceptRequest.getProposalId() <
        this.proposalsReceivedForLogPosition.get(acceptRequest.getLogPosition()).getProposalId()) {
      ACCEPTOR_LOGGER.info(String.format("Proposal %s: Accept rejected; promised another proposal " +
              "with higher ID", acceptRequest.getProposalId()));
//      System.out.printf("Proposal %s: Accept rejected; promised another proposal with higher ID"
//      , acceptRequest.getProposalId());
      return;
    }

    // Now we are certain that we have to accept this request.
    AcceptAcknowledgment acceptAcknowledgment =
        new AcceptAcknowledgment(this.acceptorId, acceptRequest);
    System.out.printf("Proposal %s: - Enter 'sack' to send to accept ACKs to learners or" +
        " 'iack' to ignore accept request.%n", acceptRequest.getProposalId());
    Scanner scanner = new Scanner(System.in);

    while (true) {
      String input = scanner.next();
      if(input.equalsIgnoreCase("sack")) {
        break;
      }
      if(input.equalsIgnoreCase("iack")) {
        return;
      }
    }

    //Mark the request as accepted
    this.proposalsAccepted.put(acceptRequest.getLogPosition(), acceptRequest);

    //Send accept acknowledgment to each learner
    for(Learner learner : learnersById.values()) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            ACCEPTOR_LOGGER.info(String.format("Proposal %s: Sending Accept ACK to learner %s",
                acceptRequest.getProposalId(), learner.getLearnerId()));
//            System.out.printf("Proposal %s: Sending Accept ACK to learner %s" ,
//                acceptRequest.getProposalId(), learner.getLearnerId());
            learner.accept(acceptAcknowledgment);
          } catch (RemoteException re) {
            throw new RuntimeException(re);
          }
        }
      }).start();
    }
  }

  @Override
  public int getAcceptorId() throws RemoteException {
    return this.acceptorId;
  }

  @Override
  public void put(Integer key, String value) throws RemoteException {
    Action action = new PutAction(key, value);
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          sendNewProposalForAction(action);
        } catch (RemoteException re) {
          throw new RuntimeException(re);
        }
      }
    }).start();
  }

  @Override
  public void delete(Integer key) throws RemoteException {
    Action action = new DeleteAction(key);
    this.sendNewProposalForAction(action);
  }

  @Override
  public void accept(AcceptAcknowledgment acceptAcknowledgment) throws RemoteException {
    LEARNER_LOGGER.info(String.format("Proposal %s: Received ACK from %s",
        acceptAcknowledgment.getProposalId(), acceptAcknowledgment.getAcceptorId()));
//    System.out.printf("Proposal %s: Received ACK from %s",
//        acceptAcknowledgment.getProposalId(), acceptAcknowledgment.getAcceptorId());

    if(this.acceptAcknowledgmentMap.containsKey(acceptAcknowledgment.getLogPosition())) {
      return;
    }
    appendToMapKey(this.acceptAcknowledgmentsForLogPosition, acceptAcknowledgment.getLogPosition(),
        acceptAcknowledgment);
    List<AcceptAcknowledgment> acks =
        this.acceptAcknowledgmentsForLogPosition.get(acceptAcknowledgment.getLogPosition());

    int majorityNumber = this.acceptorsById.size() / 2 + 1;

    if(acks.size() < majorityNumber) {
      return;
    }

    acceptAcknowledgment.getAction().execute(this.keyValue);
    this.acceptAcknowledgmentMap.put(acceptAcknowledgment.getLogPosition(), acceptAcknowledgment);

    LEARNER_LOGGER.info(String.format("Proposal %s: Operation %s executed for log position %s",
        acceptAcknowledgment.getProposalId(), acceptAcknowledgment.getAction().toString(),
        acceptAcknowledgment.getLogPosition()));

//    System.out.printf("Proposal %s: Operation %s executed for log position %s",
//        acceptAcknowledgment.getProposalId(), acceptAcknowledgment.getAction().toString(),
//        acceptAcknowledgment.getLogPosition());
  }

  @Override
  public String get(Integer key) throws RemoteException {
    return this.keyValue.get(key);
  }

  @Override
  public int getLearnerId() throws RemoteException {
    return this.learnerId;
  }

  @Override
  public void registerNode(String host, String name, int port) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(host, port);
      NodePaxos nodePaxos = (NodePaxos) registry.lookup(name);
      this.acceptorsById.put(nodePaxos.getAcceptorId(), nodePaxos);
      this.proposersById.put(nodePaxos.getProposerId(), nodePaxos);
      this.learnersById.put(nodePaxos.getLearnerId(), nodePaxos);

      PAXOS_LOGGER.info(String.format("Registered remote paxos node %s:%s", host, port));
//      System.out.printf("Registered remote paxos node %s:%s", host, port);
    } catch (RemoteException | NotBoundException e) {
      PAXOS_LOGGER.error(String.format("Couldn't add %s:%d : %s", host, port, e.getMessage()));
//      System.out.printf("Couldn't add %s:%d : %s", host, port, e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void promise(Promise promise) throws RemoteException {
    PROPOSER_LOGGER.info(String.format("Proposal %s: Received promise from Acceptor %s",
        promise.getProposalId(), promise.getAcceptorId()));
//    System.out.printf("Proposal %s: Received promise from Acceptor %s",
//        promise.getProposalId(), promise.getAcceptorId());

    ServerProposal proposal = proposalsSentById.get(promise.getProposalId());

    appendToMapKey(promisesReceivedForLogPosition, proposal.getLogPosition(), promise);

    List<Promise> promises = this.promisesReceivedForLogPosition.get(proposal.getLogPosition());
    int majorityNumber = this.acceptorsById.size() / 2 + 1;
    int numberOfPromises = promises.size();
    if(numberOfPromises < majorityNumber) {
      return;
    }

    Action actionToBeSent = proposal.getAction();

    // Now that we have attained majority, we want to check if any promises has a value piggybacked to it.
    // If there is a value piggybacked to it, we will find the value with the highest accepted ID.
    if(promises.stream().anyMatch(p -> p.getAcceptAcknowledgment() != null)) {
      Promise promiseWithMaxAcceptedId = findAcceptedPromiseWithHighestId(promises);

      actionToBeSent = promiseWithMaxAcceptedId.getAcceptAcknowledgment().getAction();
      PROPOSER_LOGGER.info(String.format("Proposal %s: Found piggybacked operation %s",
          proposal.getProposalId(), actionToBeSent.toString()));
//      System.out.printf("Proposal %s: Found piggybacked operation %s",
//          proposal.getProposalId(), actionToBeSent.toString());
    }

    // Send an accept request to all acceptors
    AcceptRequest acceptRequest = new AcceptRequest(proposal.getProposalId(),
        this.proposerId, proposal.getLogPosition(), actionToBeSent);

    for (Acceptor acceptor : acceptorsById.values()) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            PROPOSER_LOGGER.info(String.format("Proposal %s: Accept request sent to Acceptor %s",
                proposal.getProposalId(), acceptor.getAcceptorId()));
//            System.out.printf("Proposal %s: Accept request sent to Acceptor %s",
//                proposal.getProposalId(), acceptor.getAcceptorId());
            acceptor.accept(acceptRequest);
          } catch (RemoteException re) {
            throw new RuntimeException(re);
          }
        }
      }).start();
    }
    this.promisesReceivedForLogPosition.get(proposal.getLogPosition()).clear();
  }

  @Override
  public int getProposerId() throws RemoteException {
    return this.proposerId;
  }

  private int getRandomIntBtw(int max, int min) {
    Random r = new Random();
    return r.nextInt(max - min) + min;
  }

  private void sendNewProposalForAction(Action action) throws RemoteException {
    ServerProposal proposal = new ServerProposal(System.nanoTime(), this.proposerId,
        currLogPosition, action);
    this.proposalsSentById.put(proposal.getProposalId(), proposal);

    PROPOSER_LOGGER.info(String.format("Proposal %s: for operation %s and log position %s created",
        proposal.getProposalId(), proposal.getAction().toString(), proposal.getLogPosition()));

//    System.out.printf("Proposal %s: for operation %s and log position %s created",
//        proposal.getProposalId(), proposal.getAction().toString(), proposal.getLogPosition());
    currLogPosition++;

    System.out.println("Proposal %s: Proposal - Enter 'sp' to send to acceptors or 'ip' to ignore.");
    Scanner scanner = new Scanner(System.in);
    while (true) {
      String input = scanner.next();
      if(input.equalsIgnoreCase("sp")) {
        break;
      }
      if(input.equalsIgnoreCase("ip")) {
        return;
      }
    }

    for (Acceptor acceptor : acceptorsById.values()) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            PROPOSER_LOGGER.info(String.format("Proposal %s: Sent to Acceptor %s",
                proposal.getProposalId(), acceptor.getAcceptorId()));
//            System.out.printf("Proposal %s: Sent to Acceptor %s",
//                proposal.getProposalId(), acceptor.getAcceptorId());
            acceptor.propose(proposal);
          } catch (RemoteException re) {
            throw new RuntimeException(re);
          }
        }
      }).start();
    }
  }

  private Promise findAcceptedPromiseWithHighestId(List<Promise> promises) {
    return promises
        .stream()
        .reduce(
            new Promise(0, 0,
                new AcceptAcknowledgment(0, 0, 0, null)),
            (maxPromise, currPromise) -> {
              if (currPromise.getAcceptAcknowledgment() == null) return maxPromise;

              if (maxPromise.getAcceptAcknowledgment().getProposalId()
                  < currPromise.getAcceptAcknowledgment().getProposalId()) {
                return currPromise;
              } else {
                return maxPromise;
              }
            });
  }

  private <K, V> void appendToMapKey(Map<K, List<V>> map, K key, V value) {
    if (!map.containsKey(key)) {
      map.put(key, new ArrayList<>());
    }
    map.get(key).add(value);
  }
}
