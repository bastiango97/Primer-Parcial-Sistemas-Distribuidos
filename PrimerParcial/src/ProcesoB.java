import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
public class ProcesoB {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    // default broker URL is : tcp://localhost:61616"

    // Name of the queue we will receive messages from
    private static String subject = "JOGG_QUEUE";
    private static String subject2 = "Mensaje_Modificado";
    public void recibirMensaje() {

        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(subject);

            MessageConsumer messageConsumer = session.createConsumer(destination);
            while (true) {
                System.out.println("¿Quién es el mejor profesor del ITAM?");
                TextMessage textMessage = (TextMessage) messageConsumer.receive();
                if (textMessage != null) {
                    System.out.print("El mejor profesor es: ");
                    System.out.println(textMessage.getText());
                    System.out.println();
                    System.out.println("Reenviando via sockets TCP a Proceso C...");
                    //reenviar mensaje a TCP
                    Socket s = null;

                    try {
                        int serverPort = 49152;

                        s = new Socket("localhost", serverPort);
                        //s = new Socket("127.0.0.1", serverPort);
                        DataInputStream in = new DataInputStream(s.getInputStream());
                        DataOutputStream out = new DataOutputStream(s.getOutputStream());

                        out.writeUTF(textMessage.getText());            // UTF is a string encoding

                        String data = in.readUTF();
                        System.out.println("Reenviando a Proceso A: " + data);

                        MessageProducer messageProducer;
                        TextMessage textMessage2;

                        //Enviar mensaje modificado a proceso A
                        try{
                            ActiveMQConnectionFactory connectionFactory2 = new ActiveMQConnectionFactory(url);
                            Connection connection2 = connectionFactory2.createConnection();
                            connection2.start();

                            Session session2 = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
                            Destination destination2 = session.createQueue(subject2);

                            messageProducer = session.createProducer(destination2);
                            textMessage2 = session.createTextMessage();

                            textMessage2.setText(data);
                            messageProducer.send(textMessage2);
                        }   catch (JMSException e) {
                            e.printStackTrace();
                        }
                    } catch (UnknownHostException e) {
                        System.out.println("Sock:" + e.getMessage());
                    } catch (EOFException e) {
                        System.out.println("EOF:" + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("IO:" + e.getMessage());
                    } finally {
                        if (s != null) try {
                            s.close();
                        } catch (IOException e) {
                            System.out.println("close:" + e.getMessage());
                        }
                    }
                }
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        new ProcesoB().recibirMensaje();
    }
}
