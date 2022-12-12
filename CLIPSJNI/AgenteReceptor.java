//Aguayo Urincho Erik Antonio
//javac -cp lib\jade.jar;lib\CLIPSJNI.jar -d classes src\examples\doctor\*.java
//java -cp lib\jade.jar;lib\CLIPSJNI.jar;classes jade.Boot -gui -agents resp0:examples.doctor.receiver;resp1:examples.doctor.receiver;resp2:examples.doctor.receiver

Receiver

package examples.doctor;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import net.sf.clipsrules.jni.*;

public class receiver extends Agent {
	private Environment clips;

	public void infodoctor(){
		System.out.println("loading knowledge base"); 
                try{
                    clips.build("(deftemplate disease(slot name)(multislot symptom)(multislot treatment))");
                    clips.build("(deftemplate foundDisease (slot foundName))");
					
					
                   
                    clips.build("(deffacts diseases"+
                    	"(disease (name headache) (symptom \"luz encandescente\" \"visibilidad borrosa\" \"fatiga\" )"+
                    	"(treatment \"descanso\" \"medicamento por doctor\" \"dejar pantallas\"))"+

                    	"(disease (name temperature) (symptom \"cabeza caliente\" \"escalofrios\")"+
                    	"(treatment \"descanso\" \"paños humedos\" \"dormir\" ))"+

                    	"(disease (name flu) (symptom \"nariz congestionada\" \"estornudos seguidos\" \"tos\")"+
                    	"(treatment \"antigripales\" \"descanso\" \"medicamentos controlados\" \"Antiinflamatorios\" \"Antibiotico\"))"+

                    	"(disease (name covid) (symptom \"temperatura\" \"cuerpo cortado\" \"dificultad respiratoria\" \"dolor de garganta\")"+
                    	"(treatment   \"paracetamol\" \"mucho descanso\" \"si es necesario ir a urgencias\"))"+

                    	"(disease (name allergy) (symptom \"Comezon en la zona afectada\" \"hinchazon\"  \"asfixia\")"+
                    	"(treatment \"Antiinflamatorios\" \"alejarse de la causa\" \"descanso\" \"no rascarse\")))");
                    clips.reset();
                } catch (Exception e) {
                  e.printStackTrace();
                }
	}



	protected void setup() {
		
        clips = new Environment(); 

		infodoctor();

		System.out.println("Agent "+getLocalName()+" waiting for request");
		
		MessageTemplate template = MessageTemplate.and(
		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
		MessageTemplate.MatchPerformative(ACLMessage.CFP) );
		
		addBehaviour(new ContractNetResponder(this, template) {
			
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent "+getLocalName()+": CFP received from "+cfp.getSender().getName()+". Action is "+cfp.getContent());
				ACLMessage propose = cfp.createReply();
				
				try{
					clips.build("(defrule diagnosis (disease (name ?n) (symptom $? "+cfp.getContent()+" $?)) => (assert (foundDisease (foundName ?n) ) ))");
					clips.run();
					FactAddressValue fv = clips.findFact("foundDisease");
					String disease = fv.getSlotValue("foundName").toString();
					System.out.println("Agent "+getLocalName()+": Proposing "+disease);
					
					propose.setPerformative(ACLMessage.PROPOSE);
					propose.setContent(String.valueOf(disease));
					
				}catch (Exception e) {
					e.printStackTrace();
				  }
				return propose;
				
			}

			@Override
			protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
				System.out.println("Agent "+getLocalName()+": Proposal accepted");
					
					
					ACLMessage inform = accept.createReply();
				 	System.out.println("Agent "+getLocalName()+": Action successfully performed");
					
					 try{
						
						clips.build("(defrule treatment ?f <-(disease (name  "+propose.getContent()+")(treatment $?b )) => (assert (foundTreatment (fact-slot-value ?f treatment  ) )))");
						clips.run();
						//t=1;
						FactAddressValue fv = clips.findFact("foundTreatment");
						String treatment = fv.getSlotValue("implied").toString();
						System.out.println("Agent "+getLocalName()+": Proposing "+treatment);
						inform.setPerformative(ACLMessage.INFORM);
						inform.setContent(String.valueOf(treatment));
					}catch (Exception e) {
						e.printStackTrace();
				 	}

					return inform;


			}

			protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
				System.out.println("Agent "+getLocalName()+": Proposal rejected");
			}
		} );
	}

	
}
