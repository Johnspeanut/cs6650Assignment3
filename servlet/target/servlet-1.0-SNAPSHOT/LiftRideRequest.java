public class LiftRideRequest {
  private Integer time;
  private Integer liftId;

  public LiftRideRequest(Integer time, Integer liftId) {
    this.time = time;
    this.liftId = liftId;
  }

  public Integer getTime() {
    return time;
  }

  public Integer getLiftId() {
    return liftId;
  }

  @Override
  public String toString() {
    return "LiftRideRequest{" +
        "time=" + time +
        ", liftId=" + liftId +
        '}';
  }
}
