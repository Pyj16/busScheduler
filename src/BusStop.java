public class BusStop {
    int id;
    String code;
    String name;
    String description;
    int latitude;
    int longitude;
    int zoneId;
    String url;
    BusStop parentStation;
    int timezone;
    boolean wheelchairBoarding;

    public BusStop(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }
}
