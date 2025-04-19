import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class StopTime {
    String id;
    Trip trip;
    LocalTime arrivalTime;
    LocalTime departureTime;
    BusStop stop;
    String sequence;

    public StopTime(Trip trip, LocalTime arrivalTime, LocalTime departureTime,BusStop stop, String sequence) {
        this.trip = trip;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stop = stop;
        this.sequence = sequence;
        this.id = trip.id + sequence;
    }
}
