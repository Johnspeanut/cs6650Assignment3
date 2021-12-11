public class LiftRide {
  int resortId;
  int seasonId;
  int dayId;
  int skierId;
  int time;
  int liftId;

  public LiftRide(int resortId, int seasonId, int dayId, int skierId, int time, int liftId) {
    this.resortId = resortId;
    this.seasonId = seasonId;
    this.dayId = dayId;
    this.skierId = skierId;
    this.time = time;
    this.liftId = liftId;
  }

  public int getResortId() {
    return resortId;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public int getDayId() {
    return dayId;
  }

  public int getSkierId() {
    return skierId;
  }

  public int getTime() {
    return time;
  }

  public int getLiftId() {
    return liftId;
  }

  public void setResortId(int resortId) {
    this.resortId = resortId;
  }

  public void setSeasonId(int seasonId) {
    this.seasonId = seasonId;
  }

  public void setDayId(int dayId) {
    this.dayId = dayId;
  }

  public void setSkierId(int skierId) {
    this.skierId = skierId;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public void setLiftId(int liftId) {
    this.liftId = liftId;
  }
}
