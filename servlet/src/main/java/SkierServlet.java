import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {
  int CHANNELS = 64;
  BlockingDeque<Channel> blockingDeque;
  public void init(){
    blockingDeque = new LinkedBlockingDeque<>();
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("localhost");
    Connection connection = null;
    try {
      connection = connectionFactory.newConnection();
      for(int i = 0; i < CHANNELS; i++){
        blockingDeque.add(connection.createChannel());
      }
    } catch (TimeoutException | IOException e) {
      e.printStackTrace();
    }
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("text/plain");
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      // TODO: process url params in `urlParts`
      res.getWriter().write("It works!");
    }
  }

  private boolean isUrlValid(String[] urlPath) {
    // TODO: validate the request url path according to the API spec
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
    return true;
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    String urlPath = request.getPathInfo();
    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters");
      return;
    }
    String[] urlParts = urlPath.split("/");
    // Validate url path and return the response status code
    if(!isPostUrlValid(urlParts)){
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("Error: invalid post url");
    }else{
      String bodyJson = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
      LiftRideRequest liftRideRequest = new Gson().fromJson(bodyJson, LiftRideRequest.class);
      // urlParts = [, 1, seasons, 2019, days, 1, skiers, 123]
      // /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
      int sortId = Integer.valueOf(urlParts[1]);
      int seasonId = Integer.valueOf(urlParts[3]);
      int dayId = Integer.valueOf(urlParts[5]);
      int skierId = Integer.valueOf(urlParts[7]);
      LiftRide liftRide = new LiftRide(sortId,seasonId,dayId,skierId,liftRideRequest.getTime(),liftRideRequest.getLiftId());
      if(liftRideRequest.getLiftId() == null || liftRideRequest.getTime() == null){
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("Error: invalid post request");
      }else{
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("Success: " + urlPath + " " + liftRide.toString());
        try {
          Channel channel = blockingDeque.take();
          // https://subscription.packtpub.com/book/application-development/9781849516501/1/ch01lvl1sec14/broadcasting-messages
//          String myExchange = "broadcast";
//          channel.exchangeDeclare(myExchange, "fanout");
          channel.queueDeclare("post", false, false, false, null);
          String message = String.format("%d,%d,%d,%d,%d,%d",
              liftRide.getResortId(),liftRide.getSeasonId(),liftRide.getDayId(),liftRide.getSkierId()
              ,liftRide.getTime(), liftRide.getLiftId());
          channel.basicPublish("", "post", null, new Gson().toJson(message).getBytes());
          blockingDeque.add(channel);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

  }

  private boolean isPostUrlValid(String[] urlPath) {
    if (urlPath == null) {
      return false;
    }
    if (urlPath.length == 8) {
      return isValidSkierLiftUrl(urlPath);
    } else {
      return false;
    }
  }

  private boolean isValidSkierLiftUrl(String[] urlPath) {
    if (urlPath[2].equals("seasons") && urlPath[4].equals("days") && urlPath[6].equals("skiers")) {
      return true;
    } else {
      return false;
    }
  }
}
