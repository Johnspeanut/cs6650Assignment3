import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;

public class UpicHttpClient {
  private static HttpClient httpClientInstance;
  private static Gson gson = new Gson();

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
    HttpResponse<String> response = UpicHttpClient.getInstance().send(postRequest, HttpResponse.BodyHandlers.ofString());
    if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
      // the request was successful
      System.out.println("response:" + response.body());
      Counter.successAdd();
    }else{
      Counter.failureAdd();
    }
  }

}
