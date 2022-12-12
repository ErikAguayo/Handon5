package examples.doctor;
import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.domain.FIPANames;
import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Scanner;


public class transmitter extends Agent {
	
	Scanner console = new Scanner(System.in);
	String name;
	private int numResponder;

	protected void setup() { 
  	Object[] args = getArguments();
  	if (args != null && args.length > 0) {
  		numResponder = args.length;
  		System.out.println("Diagnosis of the responder "+numResponder+" responders.");
  		
  		// Fill the CFP message
  		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
  		for (int i = 0; i < args.length; ++i) {
  			msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
  		}
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			System.out.println("What are your symptom: ");
			name = console.nextLine();
			msg.setContent("\""+name+"\"");
			
			addBehaviour(new ContractNetInitiator(this, msg) {
				
				protected void handlePropose(ACLMessage propose, Vector v) {
					System.out.println("Agent "+propose.getSender().getName()+" proposed "+propose.getContent());
				}
				
				protected void handleRefuse(ACLMessage refuse) {
					System.out.println("Agent "+refuse.getSender().getName()+" refused");
				}
				
				protected void handleFailure(ACLMessage failure) {
					if (failure.getSender().equals(myAgent.getAMS())) {
						
						System.out.println("Responder does not exist");
					}
					else {
						System.out.println("Agent "+failure.getSender().getName()+" failed");
					}
					
					numResponder--;
				}
				
				protected void handleAllResponses(Vector responses, Vector acceptances) {
					if (responses.size() < numResponder) {
						
						System.out.println("Timeout expired: missing "+(numResponder - responses.size())+" responses");
					}
					
					String bestProposal = "";
					AID bestProposer = null;
					ACLMessage accept = null;
					Enumeration e = responses.elements();
					while (e.hasMoreElements()) {
						ACLMessage msg = (ACLMessage) e.nextElement();
						if (msg.getPerformative() == ACLMessage.PROPOSE) {
							ACLMessage reply = msg.createReply();
							reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
							acceptances.addElement(reply);
							String proposal = msg.getContent();
							
							if (proposal != bestProposal) {
								bestProposal = proposal;
								bestProposer = msg.getSender();
								accept = reply;
							}
						}
					}
				
					if (accept != null) {
						System.out.println("Accepting proposal "+bestProposal+" from responder "+bestProposer.getName());
						accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					}						
				}
				
				protected void handleInform(ACLMessage inform) {
					System.out.println("Agent "+inform.getSender().getName()+" successfully performed the requested action. "+inform.getContent());
					
					System.out.println("This is your treatment "+inform.getContent());
				}
			} );
  	}
  	else {
  		System.out.println("No responder specified.");
  	}
  } 
}
