import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws ParseException {
        // Arguments: stopID, maxBuses, <relative/absolute>
        int stopID;
        int maxBuses = 0;
        boolean isRelative;
        try{
            stopID = Integer.getInteger(args[0]); // ID of the bus stop.
            maxBuses = Integer.getInteger(args[1]); // Max number of buses to output.
            isRelative = switch (args[2]) {
                case "relative" -> true;
                case "r" -> true;
                case "absolute" -> false;
                case "a" -> false;
                default ->
                        throw new Exception("Unknown representation format '" + args[2] + "'. Please use 'relative' or 'absolute'.");
            }; // Relative or absolute timing representation. 1 = relative 0 = absolute
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        HashMap<Integer, Stop> busStops = new HashMap<>();
        List<StopTime> stopTimes = new ArrayList<>();
        HashMap<String, Trip> trips = new HashMap<>();
        HashMap<Integer, Route> routes = new HashMap<>();

        try{
            readData(busStops, stopTimes, trips, routes); // reads the data from the text files and adds it to the hashmaps
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        maxBuses = 6; // DEL LATER
        stopID = 3;


        LocalTime now = LocalTime.now();
        LocalTime maxTime = LocalTime.now().plusHours(10);

        System.out.println(maxTime);

        List<StopTime> validTimes = new ArrayList<>();


        Stop stop = busStops.get(stopID);
        for(StopTime stopTime : stopTimes){
            if(maxTime.isBefore(now))
            {
                if(stopTime.stop == stop && maxBuses > 0 && (stopTime.arrivalTime.isBefore(maxTime) || stopTime.arrivalTime.isAfter(now)))
                {
                    validTimes.add(stopTime);
                    maxBuses--;
                }
            }
            {
                if(stopTime.stop == stop && maxBuses > 0 && (stopTime.arrivalTime.isBefore(maxTime) && stopTime.arrivalTime.isAfter(now)))
                {
                    validTimes.add(stopTime);
                    maxBuses--;
                }
            }
        }

        System.out.println(stop.name);
        for(StopTime stopTime : validTimes){
            System.out.println(stopTime.trip.route.shortName + ": " + LocalTime.of(stopTime.arrivalTime.getHour(), stopTime.arrivalTime.getMinute()));
        }
    }

    public static void readData(HashMap<Integer, Stop> busStop, List<StopTime> stopTimes, HashMap<String, Trip> trips, HashMap<Integer, Route> routes) throws FileNotFoundException, ParseException {
        File stopsFile = new File("src/data/stops.txt");
        File stoptimesFile = new File("src/data/stop_times.txt");
        File tripsFile = new File("src/data/trips.txt");
        File routesFile = new File("src/data/routes.txt");

        String content;
        Scanner scan;

        // Reading stops.txt
        scan = new Scanner(stopsFile);
        content = "";
        scan.nextLine();
        while(scan.hasNextLine()){
            content = scan.nextLine();
            List<String> params = Arrays.asList(content.split(","));
            int id = Integer.parseInt(params.get(0));
            Stop newStop = new Stop(id, params.get(1), params.get(2));
            busStop.put(id, newStop);
        }

        // Reading routes.txt
        scan = new Scanner(routesFile);
        content = "";
        scan.nextLine();
        while(scan.hasNextLine()){
            content = scan.nextLine();
            List<String> params = Arrays.asList(content.split(","));
            int id = Integer.parseInt(params.get(0));
            Route newRoute = new Route(id, params.get(3), params.get(2), Integer.parseInt(params.get(5)));
            routes.put(id, newRoute);
        }

        // Reading trips.txt
        scan = new Scanner(tripsFile);
        content = "";
        scan.nextLine();
        while(scan.hasNextLine()){
            content = scan.nextLine();
            List<String> params = Arrays.asList(content.split(","));
            String id = params.get(2);
            int routeId = Integer.parseInt(params.get(0));
            Route route = routes.get(routeId);
            Trip newTrip = new Trip(id, route);
            trips.put(id, newTrip);
        }

        // Reading stop_times.txt
        scan = new Scanner(stoptimesFile);
        content = "";
        scan.nextLine();
        while(scan.hasNextLine()){
            content = scan.nextLine();
            List<String> params = Arrays.asList(content.split(","));
            String sequence = params.get(4);

            String tripID = params.get(0);
            String id = tripID + sequence;

            Trip trip = trips.get(tripID);

            int stopID = Integer.parseInt(params.get(3));
            Stop stop = busStop.get(stopID);

            DateFormat format = new SimpleDateFormat("HH:mm:ss");

            StopTime newStopTime = new StopTime(trip, LocalTime.parse(params.get(1)), LocalTime.parse(params.get(2)), stop, sequence);
            stopTimes.add(newStopTime);
        }

        Collections.sort(stopTimes, new Comparator<StopTime>() {
            @Override
            public int compare(StopTime st1, StopTime st2) {
                return st1.arrivalTime.compareTo(st2.arrivalTime);
            }
        });

    }

}
