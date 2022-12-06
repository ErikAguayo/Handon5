package examples.messaging;
 
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
 
public class AgenteReceptor extends Agent {
   
    protected void setup() {
        addBehaviour(new ReceptorComportaminento());
    }

   private class ReceptorComportaminento extends CyclicBehaviour {
            private boolean fin = false;
       
            public void action() {
                System.out.println(" Preparandose para recibir");

                ACLMessage mensaje = receive();
 
                if (mensaje!= null) {
                    System.out.println(getLocalName() + ": acaba de recibir el siguiente mensaje: ");
                    System.out.println(mensaje.toString());
                   
                }


                 System.out.println("Invoking Tell"); 
              clips.eval("(clear)");
              clips.build("(defrule r1 (estornuda) (frio) => (printout  t "tiene resfriado" crlf) (assert (dolor de cabeza)) ");
              clips.build("(defrule r2 (dolor de cabeza) (garganta irritada) => (printout  t "dolor en garganta" crlf) (assert (posible covid)) ");
              clips.build("(defrule r3 (posible covid) (cuerpo cortado) => (printout  t "se realizo prueba covid" crlf) (assert (dio positivo)) ");
              clips.build("(defrule r4 (dio positivo) => (printout  t "covid verificado" crlf) ");
              clips.build("(defrule r5 (estornuda) (calor)=> (printout  t "tiene temperatura" crlf) ");
              clips.eval("(reset)");

 
            //Obtiene el primer mensaje de la cola de mensajes
                
            }

            public boolean done() {
                return fin;
            }
    }
}
