import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.time.*;
import java.util.*;

public class Main {

    // Normally data gathered from a DAO. Data provided in the exercise is in .txt files.
    public static HashMap<Integer, Stop> stops = new HashMap<>();
    public static List<StopTime> stopTimes = new ArrayList<>();
    public static HashMap<String, Trip> trips = new HashMap<>();
    public static HashMap<Integer, Route> routes = new HashMap<>();

    public static void main(String[] args){
        // Arguments: stopID, maxBuses, <relative/absolute>
        int stopID = 0;
        int maxBuses = 0;
        boolean isRelative = false;


        // Process args
        try{
            stopID = Integer.parseInt(args[0]); // ID of the bus stop.
            maxBuses = Integer.parseInt(args[1]); // Max number of buses to output.
            isRelative = switch (args[2]) {
                case "relative", "r" -> true;
                case "absolute", "a" -> false;
                default ->
                        throw new Exception("Unknown representation format '" + args[2] + "'. Please use 'relative' or 'absolute'.");
            }; // Relative or absolute timing representation. 1 = relative 0 = absolute
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        try{
            readData(); // Reads the data from the text files and adds it to the hashmaps
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        showStops(stopID, maxBuses, 2, isRelative); // Shows relevant stop times
    }

    // Reads data from set file paths and adds them to the HashMaps and Lists. Example data is in 'busScheduler/src/data'
    public static void readData() throws FileNotFoundException, ParseException {
        File stopsFile = new File("./data/stops.txt");
        File stoptimesFile = new File("./data/stop_times.txt");
        File tripsFile = new File("./data/trips.txt");
        File routesFile = new File("./data/routes.txt");

        String content;
        Scanner scan;

        // Reading stops.txt
        scan = new Scanner(stopsFile);
        scan.nextLine();
        while(scan.hasNextLine()){
            content = scan.nextLine();
            List<String> params = Arrays.asList(content.split(","));
            int id = Integer.parseInt(params.get(0));
            Stop newStop = new Stop(id, params.get(1), params.get(2));
            stops.put(id, newStop);
        }

        // Reading routes.txt
        scan = new Scanner(routesFile);
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
        scan.nextLine();
        while(scan.hasNextLine()){
            content = scan.nextLine();
            List<String> params = Arrays.asList(content.split(","));
            String sequence = params.get(4);

            String tripID = params.get(0);

            Trip trip = trips.get(tripID);

            int stopID = Integer.parseInt(params.get(3));
            Stop stop = stops.get(stopID);

            StopTime newStopTime = new StopTime(trip, LocalTime.parse(params.get(1)), LocalTime.parse(params.get(2)), stop, sequence);
            stopTimes.add(newStopTime);
        }

        // Sorts times from earliest to latest
        stopTimes.sort(new Comparator<>() {
            @Override
            public int compare(StopTime st1, StopTime st2) {
                return st1.arrivalTime.compareTo(st2.arrivalTime);
            }
        });

    }

    // Takes in a stop_id, a maximum number of buses, a maximum amount of time in hours to show and checks if timings should be made relative or absolute.
    // Optionally can force a time. Only the first String provided is read. Must be in "HH:mm:ss" format.
    public static void showStops(int stopID, int maxBuses, int maxTimeInHrs, boolean isRelative, String forcedTime){
        LocalTime now = LocalTime.parse(forcedTime);
        LocalTime maxTime = now.plusHours(maxTimeInHrs);

        stopsOutput(stopID, maxBuses, isRelative, now, maxTime);
    }

    // Takes in a stop_id, a maximum number of buses, a maximum amount of time in hours to show and checks if timings should be made relative or absolute.
    public static void showStops(int stopID, int maxBuses, int maxTimeInHrs, boolean isRelative){
        LocalTime now = LocalTime.now();
        LocalTime maxTime = now.plusHours(maxTimeInHrs);

        stopsOutput(stopID, maxBuses, isRelative, now, maxTime);
    }

    // Processes the relevant data and outputs it.
    public static void stopsOutput(int stopID, int maxBuses, boolean isRelative, LocalTime now, LocalTime maxTime){

        // Local class for formatting the output. Has route name and relevant times to the specified parameters
        class StopOutput{
            final String route;
            final List<String> times;
            int offset = 0; // Offset for times that get added in between midnight

            StopOutput(String route){
                this.route = route;
                this.times = new ArrayList<>();
            }

            void addTimeToStart(String time){
                times.add(offset, time);
                offset++;
            }

            void addTimeToEnd(String time){
                times.add(time);
            }
        }

        HashMap<String, StopOutput> relevantTimes = new HashMap<>();

        Stop stop = stops.get(stopID);
        for(StopTime stopTime : stopTimes){
            String route = stopTime.trip.route.shortName;
            LocalTime time = stopTime.arrivalTime;
            String timeStr = time.getHour() + ":" + (time.getMinute() < 10 ? "0" : "") + time.getMinute();

            StopOutput relevantStop;

            // Additionally checks to see if time overflows past midnight (00:00:00)
            if(stopTime.stop == stop && maxBuses > 0 && maxTime.isBefore(now))
            {

                // Case if hours pass midnight is the union of maxTime and now.
                if(time.isBefore(maxTime))
                {
                    if(!relevantTimes.containsKey(route)){
                        relevantStop = new StopOutput(route);
                        relevantTimes.put(route, relevantStop);
                    }
                    relevantStop = relevantTimes.get(route);

                    if(isRelative){
                        long recalculation = Duration.between(LocalTime.parse("00:00:00"), time).toMinutes() +  (1440 + Duration.between(now, LocalTime.parse("00:00:00")).toMinutes() );
                        timeStr = recalculation + "min";
                    }
                    relevantStop.addTimeToEnd(timeStr);
                    maxBuses--;
                }
                if(time.isAfter(now)){
                    if(!relevantTimes.containsKey(route)){
                        relevantStop = new StopOutput(route);
                        relevantTimes.put(route, relevantStop);
                    }
                    relevantStop = relevantTimes.get(route);
                    if(isRelative){
                        timeStr = Duration.between(time, now).negated().toMinutes() + "min";
                    }
                    relevantStop.addTimeToStart(timeStr);
                    maxBuses--;
                }
            }
            else if(stopTime.stop == stop && maxBuses > 0 && maxTime.isAfter(now)){
                // Case if hours do not pass midnight is the intersection of maxTime and now.
                if(time.isBefore(maxTime) && time.isAfter(now))
                {
                    // If route is already registered in relevantRoutes
                    if(!relevantTimes.containsKey(route)){
                        relevantStop = new StopOutput(route);
                        relevantTimes.put(route, relevantStop);
                    }
                    relevantStop = relevantTimes.get(route);

                    if(isRelative){
                        timeStr = Duration.between(time, now).negated().toMinutes() + "min";
                    }
                    relevantStop.addTimeToEnd(timeStr);
                    maxBuses--;
                }
            }
        }

        // Prints out the result
        System.out.println(stop.name);
        for(StopOutput output : relevantTimes.values()){
            System.out.print(output.route + ": ");
            for(String time : output.times){
                System.out.print(time + ", ");
            }
            System.out.println();
        }
    }

}
