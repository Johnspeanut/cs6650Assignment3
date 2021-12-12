import com.google.gson.Gson;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.EventCountCircuitBreaker;
import org.springframework.http.HttpStatus;

public class UpicHttpClient {
  private static HttpClient httpClientInstance;
  private static Gson gson = new Gson();
  private static EventCountCircuitBreaker breaker = new EventCountCircuitBreaker(10, 1, TimeUnit.MINUTES, 5, 1, TimeUnit.MINUTES);
  public static HttpClient getInstance(){
    if(httpClientInstance == null){
      httpClientInstance = HttpClient.newBuilder().build();
    }
    return httpClientInstance;
  }

  public static void postSkierRecord(String url, LiftRideRequest skierRecord)
      throws IOException, InterruptedException {
    String body = gson.toJson(skierRecord);

    HttpRequest postRequest = HttpRequest.newBuilder().uri(URI.create(url)).POST(BodyPublishers.ofString(body)).build();
//    HttpResponse<String> response = UpicHttpClient.getInstance().send(postRequest, HttpResponse.BodyHandlers.ofString());
//    HttpResponse<String> response = UpicHttpClient.getInstance().send(postRequest, HttpResponse.BodyHandlers.ofString());
    handleRequest(postRequest);
  }

  //https://commons.apache.org/proper/commons-lang/javadocs/api-3.9/org/apache/commons/lang3/concurrent/EventCountCircuitBreaker.html
  // Accept 5 errors in 2 minutes; if this limit is reached, the service is given a rest time of 1 minutes:
  public static void handleRequest(HttpRequest postRequest) {
    if (breaker.checkState()) {
      try {
        HttpResponse<String> response = UpicHttpClient.getInstance().send(postRequest, HttpResponse.BodyHandlers.ofString());
        if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
          // the request was successful
          System.out.println("response:" + response.body());
          Counter.successAdd();
        }
      } catch (IOException | InterruptedException ex) {
        breaker.incrementAndCheckState();
      }
    } else {
      // return an error code, use an alternative service, etc.
      Counter.failureAdd();
    }
  }

}
