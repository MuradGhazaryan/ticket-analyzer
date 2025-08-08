import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class TicketAnalyzer {
    
    private static final String ORIGIN_CITY = "\u0412\u043b\u0430\u0434\u0438\u0432\u043e\u0441\u0442\u043e\u043a";
    private static final String DESTINATION_CITY = "\u0422\u0435\u043b\u044c-\u0410\u0432\u0438\u0432";
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TicketAnalyzer <path_to_tickets.json>");
            return;
        }
        
        try {
            List<Ticket> tickets = loadTickets(args[0]);
            List<Ticket> filteredTickets = filterTickets(tickets);
            
            calculateMinFlightTimes(filteredTickets);
            calculatePriceDifference(filteredTickets);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static List<Ticket> loadTickets(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));
        JsonNode ticketsNode = root.get("tickets");
        
        List<Ticket> tickets = new ArrayList<>();
        
        for (JsonNode ticketNode : ticketsNode) {
            Ticket ticket = new Ticket();
            ticket.originName = ticketNode.get("origin_name").asText();
            ticket.destinationName = ticketNode.get("destination_name").asText();
            ticket.departureDate = ticketNode.get("departure_date").asText();
            ticket.departureTime = ticketNode.get("departure_time").asText();
            ticket.arrivalDate = ticketNode.get("arrival_date").asText();
            ticket.arrivalTime = ticketNode.get("arrival_time").asText();
            ticket.carrier = ticketNode.get("carrier").asText();
            ticket.price = ticketNode.get("price").asInt();
            
            tickets.add(ticket);
        }
        
        return tickets;
    }
    
    private static List<Ticket> filterTickets(List<Ticket> tickets) {
        return tickets.stream()
                .filter(t -> ORIGIN_CITY.equals(t.originName) && DESTINATION_CITY.equals(t.destinationName))
                .collect(Collectors.toList());
    }
    
    private static void calculateMinFlightTimes(List<Ticket> tickets) {
        System.out.println("\u041c\u0438\u043d\u0438\u043c\u0430\u043b\u044c\u043d\u043e\u0435 \u0432\u0440\u0435\u043c\u044f \u043f\u043e\u043b\u0435\u0442\u0430 \u043c\u0435\u0436\u0434\u0443 \u0433\u043e\u0440\u043e\u0434\u0430\u043c\u0438 " + ORIGIN_CITY + " \u0438 " + DESTINATION_CITY + ":");
        
        Map<String, Long> minFlightTimes = new HashMap<>();
        
        for (Ticket ticket : tickets) {
            long flightTimeMinutes = calculateFlightTime(ticket);
            String carrier = ticket.carrier;
            
            minFlightTimes.merge(carrier, flightTimeMinutes, Math::min);
        }
        
        minFlightTimes.forEach((carrier, minutes) -> {
            long hours = minutes / 60;
            long mins = minutes % 60;
            System.out.printf("\u0410\u0432\u0438\u0430\u043a\u043e\u043c\u043f\u0430\u043d\u0438\u044f %s: %d \u0447 %d \u043c\u0438\u043d%n", carrier, hours, mins);
        });
        System.out.println();
    }
    
    private static void calculatePriceDifference(List<Ticket> tickets) {
        List<Integer> prices = tickets.stream()
                .map(t -> t.price)
                .collect(Collectors.toList());
        
        if (prices.isEmpty()) {
            System.out.println("\u041d\u0435\u0442 \u0434\u0430\u043d\u043d\u044b\u0445 \u043e \u0446\u0435\u043d\u0430\u0445");
            return;
        }
        
        double average = prices.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double median = calculateMedian(prices);
        double difference = Math.abs(average - median);
        
        System.out.printf("\u0420\u0430\u0437\u043d\u0438\u0446\u0430 \u043c\u0435\u0436\u0434\u0443 \u0441\u0440\u0435\u0434\u043d\u0435\u0439 \u0446\u0435\u043d\u043e\u0439 \u0438 \u043c\u0435\u0434\u0438\u0430\u043d\u043e\u0439: %.2f \u0440\u0443\u0431%n", difference);
        System.out.printf("\u0421\u0440\u0435\u0434\u043d\u044f\u044f \u0446\u0435\u043d\u0430: %.2f \u0440\u0443\u0431%n", average);
        System.out.printf("\u041c\u0435\u0434\u0438\u0430\u043d\u0430: %.2f \u0440\u0443\u0431%n", median);
    }
    
    private static double calculateMedian(List<Integer> prices) {
        List<Integer> sortedPrices = new ArrayList<>(prices);
        Collections.sort(sortedPrices);
        
        int size = sortedPrices.size();
        if (size % 2 == 1) {
            return sortedPrices.get(size / 2);
        } else {
            return (sortedPrices.get(size / 2 - 1) + sortedPrices.get(size / 2)) / 2.0;
        }
    }
    
    private static long calculateFlightTime(Ticket ticket) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        
        String departureDateTime = ticket.departureDate + " " + ticket.departureTime;
        String arrivalDateTime = ticket.arrivalDate + " " + ticket.arrivalTime;
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");
        
        LocalDateTime departure = LocalDateTime.parse(departureDateTime, dateTimeFormatter);
        LocalDateTime arrival = LocalDateTime.parse(arrivalDateTime, dateTimeFormatter);
        
        return ChronoUnit.MINUTES.between(departure, arrival);
    }
    
    static class Ticket {
        String originName;
        String destinationName;
        String departureDate;
        String departureTime;
        String arrivalDate;
        String arrivalTime;
        String carrier;
        int price;
    }
}