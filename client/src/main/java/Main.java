
public class Main {
  public static void main(String[] args) throws InterruptedException {
    /**
     * Scenario 1
     * number of threads in client:64
     * number of Tomcat servers: 1
     * number of channels in each Tomcat server: 64
     * number of threads in the consumer: 128
     * basicQos for each channel in consumer: 10
     */

//    int numThread = 64;
    /**
     * Scenario 2
     * number of threads in client:128
     * number of Tomcat servers: 1
     * number of channels in each Tomcat server: 64
     * number of threads in the consumer: 128
     * basicQos for each channel in consumer: 10
     */
    int numThread = 128;

    /**
     * Scenario 3
     * number of threads in client:256
     * number of Tomcat servers: 1
     * number of channels in each Tomcat server: 64
     * number of threads in the consumer: 128
     * basicQos for each channel in consumer: 10
     */
//    int numThread = 256;

    /**
     * Scenario 4
     * number of threads in client:512
     * number of Tomcat servers: 1
     * number of channels in each Tomcat server: 64
     * number of threads in the consumer: 128
     * basicQos for each channel in consumer: 10
     */
//    int numThread = 512;

    int fullNumSkiers  = 100000;  //skier's ID range
    int numLifts = 40;
    int liftPerSkier = 10;
    int numResorts = 10;
    String port = "8080";
//    String baseUrl = "18.204.118.23";
    String baseUrl = "localhost";
    int skiTimeDuration = 420;
    // First phase 1-90; Second phase 91-360; Third phase 361-420
    int numRequestPhase1 = fullNumSkiers/skiTimeDuration * 90;
    int numRequestPhase2 = fullNumSkiers/skiTimeDuration * 360 - numRequestPhase1;
    int numRequestPhase3 = fullNumSkiers - numRequestPhase1 - numRequestPhase2;

//    int[] numThreads = new int[]{32,64,128,256};
      int numThreadPhase1 = numThread/4;
      int numThreadPhase2 = numThread;
      int numThreadPhase3 = numThread/4;
      int idRangePerThreadPhase1 = fullNumSkiers/numThreadPhase1;
      int idRangePerThreadPhase2 = fullNumSkiers/numThreadPhase2;
      int idRangePerThreadPhase3 = fullNumSkiers/numThreadPhase3;
      long startTime = System.currentTimeMillis();
      Phase phase1 = new Phase(numRequestPhase1,numRequestPhase1,idRangePerThreadPhase1,numResorts,baseUrl,port,1,90);
      Phase phase2 = new Phase(numRequestPhase2,numRequestPhase2,idRangePerThreadPhase2,numResorts,baseUrl,port,91,360);
      Phase phase3 = new Phase(numRequestPhase3,numRequestPhase3,idRangePerThreadPhase3,numResorts,baseUrl,port,361,420);
      phase1.start();
      phase1.waitTenPercent();
      phase2.start();
      phase2.waitTenPercent();
      phase3.start();
      phase1.waitFullComplete();
      phase2.waitFullComplete();
      phase3.waitFullComplete();

      long endTime = System.currentTimeMillis();
      long wallTime = endTime - startTime;
      int totalRequest = Counter.getNumFailure() + Counter.getNumSuccess();
      System.out.println("Number requests: " + totalRequest);
      System.out.println("Number of success: " + Counter.getNumSuccess());
      System.out.println("Number of failure: " + Counter.getNumFailure());
      System.out.printf("Throughput in requests per second: ", Counter.getNumSuccess() / (wallTime/1000));
    System.out.println("Mean response time (milliseconds): " + wallTime/totalRequest);
    System.out.println("Wall time (second): " + wallTime/1000);
    Counter.reset();
  }

}
