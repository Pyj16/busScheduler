import java.time.LocalTime;

public class StopTime {
    String id; // id is the concatenation of trip_id and sequence
    Trip trip;
    LocalTime arrivalTime;
    LocalTime departureTime;
    Stop stop;
    String sequence;

    public StopTime(Trip trip, LocalTime arrivalTime, LocalTime departureTime, Stop stop, String sequence) {
        this.trip = trip;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stop = stop;
        this.sequence = sequence;
        this.id = trip.id + sequence;
    }
}
