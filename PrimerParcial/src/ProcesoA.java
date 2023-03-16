import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
public class ProcesoA {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "JOGG_QUEUE";
    private static String subject2 = "Mensaje_Modificado";
    public void producirMensaje(){
        MessageProducer messageProducer;
        TextMessage textMessage;

        try {

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(subject);

            messageProducer = session.createProducer(destination);
            textMessage = session.createTextMessage();

            textMessage.setText("¡Octavio!");
            System.out.println("Sending the following message: " + textMessage.getText());
            messageProducer.send(textMessage);
            boolean goodByeReceived = false;

            try {

                ActiveMQConnectionFactory connectionFactory2 = new ActiveMQConnectionFactory(url);
                Connection connection2 = connectionFactory.createConnection();
                connection2.start();

                Session session2 = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);

                Destination destination2 = session.createQueue(subject2);

                MessageConsumer messageConsumer = session.createConsumer(destination2);
                System.out.println("¿Quién es el mejor profesor del ITAM y por qué?");
                TextMessage textMessage2 = (TextMessage) messageConsumer.receive();
                System.out.println(textMessage2.getText());
                System.out.println();
                messageConsumer.close();
                session2.close();
                connection2.close();

            } catch (JMSException e) {
                e.printStackTrace();
            }

            messageProducer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new ProcesoA().producirMensaje();
    }
}
