public class Counter {
  private static int numSuccess = 0;
  private static int numFailure = 0;

  public static synchronized void successAdd(){
    Counter.numSuccess++;
  }

  public static synchronized  void failureAdd(){
    Counter.numFailure++;
  }

  public static synchronized void reset(){
    Counter.numSuccess = 0;
    Counter.numFailure = 0;
  }

  public static int getNumSuccess() {
    return numSuccess;
  }

  public static int getNumFailure() {
    return numFailure;
  }
}
