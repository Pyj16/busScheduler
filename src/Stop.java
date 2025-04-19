public class Stop {
    int id;
    String code;
    String name;
    // Redundant
    String description;
    int latitude;
    int longitude;
    int zoneId;
    String url;
    Stop parentStation;
    int timezone;
    boolean wheelchairBoarding;

    public Stop(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }
}
