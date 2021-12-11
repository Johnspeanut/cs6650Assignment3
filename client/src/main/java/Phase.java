import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Phase {
  int numThreads;
  int numRequests;
  int requestPerThread;
  int idRangePerThread;
  int numResorts;
  public ExecutorService threadsPool;
  private CountDownLatch fullLatch;
  private CountDownLatch tenPercentLatch;
  Gson gson;
  String baseUrl;
  String port;
  int start;
  int end;

  /**
   *
   * @param numThreads Specific number of thread used in a phase
   * @param numRequests Specific number of request for a phase
   * @param baseUrl
   * @param port
   */
  public Phase(int numThreads,int numRequests, int idRangePerThread, int numResorts, String baseUrl, String port, int start, int end){
    this.numThreads = numThreads;
    this.numRequests = numRequests;
    this.numResorts = numResorts;
    this.requestPerThread = numRequests/numThreads;
    this.idRangePerThread = idRangePerThread;
    gson = new Gson();
    this.baseUrl = baseUrl;
    this.port = port;
    this.start = start;
    this.end = end;


    this.threadsPool = Executors.newFixedThreadPool(numThreads);
    this.fullLatch = new CountDownLatch(numThreads);
    this.tenPercentLatch = new CountDownLatch(numThreads/10);
    
  }
  
  public void start(){
    for(int i = 0; i < this.numThreads; i++){
      int startThread = i *this.idRangePerThread;
      Runnable runner = () ->{
        for (int j = 0; j < this.requestPerThread; j++) {
          Random random = new Random();
          int skierID = random.nextInt(this.idRangePerThread) + startThread;
          int resortID = random.nextInt(this.numResorts);
          int time = random.nextInt(end-start) + start;
          int liftID = random.nextInt(100);
          //String url = "http://" + this.baseUrl + ":" + this.port + "/servelet_war_exploded/";
          String url = "http://" + this.baseUrl + ":" + this.port + "/servlet-1.0-SNAPSHOT"+"/skiers/" + resortID + "/seasons/" + 2021 + "/days/"+100+"/skiers/" + skierID;
          LiftRideRequest skierRecord = new LiftRideRequest(time,liftID);
          try {
            UpicHttpClient.postSkierRecord(url, skierRecord);
          } catch (IOException e) {
            e.printStackTrace();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

        }
        this.fullLatch.countDown();
        this.tenPercentLatch.countDown();
      };
      this.threadsPool.submit(runner);
      
    }
    
  }

  public void waitTenPercent() throws InterruptedException {
    this.tenPercentLatch.await();
  }

  public void waitFullComplete() throws InterruptedException {
    this.fullLatch.await();
  }



}
