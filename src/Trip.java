public class Trip {
    String id; // ID is not an integer and contains ASCII characters. Using String instead.
    Route route;

    public Trip(String id, Route route) {
        this.id = id;
        this.route = route;
    }
}
