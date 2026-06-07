package rozkladbot.enums;

public enum ScheduleType {
  THIS_WEEK,
  NEXT_WEEK,
  CUSTOM;

  public static ScheduleType fromCachePeriod(CachePeriod mode) {
    switch (mode) {
      case NEXT_WEEK -> {
        return NEXT_WEEK;
      }
      case THIS_WEEK -> {
        return THIS_WEEK;
      }
      default -> {
        return CUSTOM;
      }
    }
  }
}