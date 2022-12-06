package examples.messaging;
 
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
 
public class AgenteEmisor extends Agent {

    protected void setup() {
        addBehaviour(new EmisorComportaminento());
    }

   private class EmisorComportaminento extends SimpleBehaviour {
        boolean fin = false;
      
        public void action() {
            System.out.println(getLocalName() +": Preparandose para enviar un mensaje a receptor");
            AID id = new AID();
            id.setLocalName("receptor");
 
        // Creación del objeto ACLMessage
            ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
 
        //Rellenar los campos necesarios del mensaje


                 System.out.println("Invoking Tell"); 
              clips.eval("(clear)");
              mensaje.clips.build("(assert (dolor de cabeza) ");
              mensaje.clips.build("(assert (posible covid) ");
              mensaje.clips.build(" (assert (dio positivo) (frio) ");
              mensaje.clips.build(" (assert (dio Estornuda) ");
              mensaje.clips.eval("(reset)");

            mensaje.setSender(getAID());
            mensaje.setLanguage("English");
            mensaje.addReceiver(new AID("AgenteReceptor", AID.ISLOCALNAME));
            
 
        //Envia el mensaje a los destinatarios
            send(mensaje);
 
            System.out.println(getLocalName() +": Mensaje enviado");
            System.out.println(mensaje.toString());
            fin = true;
        }
 
        public boolean done()
        {
            return fin;
        }
    }

}
