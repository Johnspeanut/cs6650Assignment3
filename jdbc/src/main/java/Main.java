import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class Main {
  private final static String QUEUE_NAME = "post";
  //  private static String ipAddress = "18.210.38.98";
  private static String ipAddress = "localhost";
  private static String userName = "guest";
  private static String passwd = "guest";
  private static int THREAD_NUMBER = 128;
  private final static ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();

  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(ipAddress);
//    factory.setUsername(userName);
//    factory.setPassword(passwd);
    final Connection connection = factory.newConnection();
    LiftRideDao liftRideDao = new LiftRideDao();

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try{
          final Channel channel = connection.createChannel();
          channel.queueDeclare(QUEUE_NAME, false, false,false,null);
          channel.basicQos(10); //max one message per receiver
          System.out.println("[x] Awaiting PRC requests");

          Object monitor = new Object();
          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new BasicProperties.Builder().correlationId(delivery.getProperties().getCorrelationId()).build();
            String response = "";
            try {
              String message = new String(delivery.getBody(), "UTF-8");
              message = message.replaceAll("^\"+|\"+$", "");
              System.out.println("Receive : " + message);
              String[] result = message.split(",");
              int resortId = Integer.valueOf(result[0]);
              int seasonId = Integer.valueOf(result[1]);
              int dayId = Integer.valueOf(result[2]);
              int skierId = Integer.valueOf(result[3]);
              int takeTime = Integer.valueOf(result[4]);
              int liftId = Integer.valueOf(result[5]);

              liftRideDao.createLiftRide(new LiftRide(resortId, seasonId, dayId, skierId, takeTime, liftId));
            }catch (RuntimeException e){
              System.out.println("Error : " + e.toString());
            }finally {
              channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
              synchronized (monitor){
                monitor.notify();
              }
            }

          };

          // process messages
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
          while(true){
            synchronized (monitor){
              try{
                monitor.wait();
              }catch (InterruptedException e){
                e.printStackTrace();
              }
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    for(int i = 0; i < THREAD_NUMBER; i++){
      Thread thread = new Thread(runnable);
      thread.start();
    }
  }
}
