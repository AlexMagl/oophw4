import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Chatbot {
    // API keys for OpenWeatherMap and ExchangeRate-API
    private static final String WEATHER_API_KEY = "5c510d376c1ca5b42c60a4c537594b29";
    private static final String EXCHANGE_RATE_API_KEY = "08273c8854ebbd70c132bf75";

    // URLs for weather and exchange rate APIs
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String EXCHANGE_RATE_URL = "https://v6.exchangerate-api.com/v6/" + EXCHANGE_RATE_API_KEY + "/latest/";

    public static void main(String[] args) {
        Chatbot chatbot = new Chatbot(); // Create an instance of the Chatbot class
        Scanner scanner = new Scanner(System.in); // Create a Scanner object to read user input
        String input;

        System.out.println("Welcome to the Chatbot! You can ask for 'weather' or 'exchange rate'. Type 'exit' to quit.");

        while (true) {
            System.out.print("You: ");
            input = scanner.nextLine().trim().toLowerCase(); // Read user input and convert to lowercase

            if (input.equals("exit")) { // If the user types "exit"
                System.out.println("Goodbye!");
                break; // Exit the loop
            } else if (input.equals("weather")) { // If the user types "weather"
                System.out.print("Enter the location for the weather (e.g., Tbilisi,ge): ");
                String location = scanner.nextLine().trim(); // Read the location input
                System.out.println(chatbot.getWeatherInfo(location)); // Call the getWeatherInfo method and print the result
            } else if (input.equals("exchange rate")) { // If the user types "exchange rate"
                System.out.print("Enter the source currency code (e.g., USD): ");
                String fromCurrency = scanner.nextLine().trim().toUpperCase(); // Read the source currency code and convert to uppercase
                System.out.print("Enter the target currency code (e.g., GEL): ");
                String toCurrency = scanner.nextLine().trim().toUpperCase(); // Read the target currency code and convert to uppercase
                System.out.println(chatbot.getExchangeRate(fromCurrency, toCurrency)); // Call the getExchangeRate method and print the result
            } else {
                System.out.println("Sorry, I didn't understand that. Please ask for 'weather' or 'exchange rate'."); // If the input is not recognized
            }
        }

        scanner.close(); // Close the Scanner object
    }

    // Method to get weather information for a given location
    public String getWeatherInfo(String location) {
        try {
            String response = sendGetRequest(WEATHER_URL + location + "&APPID=" + WEATHER_API_KEY); // Send a GET request to the OpenWeatherMap API
            String weatherDescription = parseJson(response, "\"description\":\"", "\""); // Parse the weather description from the JSON response
            String tempStr = parseJson(response, "\"temp\":", ","); // Parse the temperature from the JSON response
            double temperature = Double.parseDouble(tempStr) - 273.15; // Convert temperature from Kelvin to Celsius
            return "The weather in " + location + " is " + weatherDescription + " with a temperature of " + String.format("%.2f", temperature) + "°C."; // Return the weather information
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to get weather information for " + location + "."; // Return an error message if an exception occurs
        }
    }

    // Method to get the exchange rate between two currencies
    public String getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            String response = sendGetRequest(EXCHANGE_RATE_URL + fromCurrency); // Send a GET request to the ExchangeRate-API
            String exchangeRateStr = parseJson(response, "\"" + toCurrency + "\":", ","); // Parse the exchange rate from the JSON response
            double exchangeRate = Double.parseDouble(exchangeRateStr);
            return "The exchange rate from " + fromCurrency + " to " + toCurrency + " is " + exchangeRate + "."; // Return the exchange rate information
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to get exchange rate information."; // Return an error message if an exception occurs
        }
    }

    // Method to send a GET request to a URL and return the response as a String
    private String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    // Method to parse a JSON string and extract a value between two delimiters
    private String parseJson(String json, String startDelimiter, String endDelimiter) {
        int startIndex = json.indexOf(startDelimiter) + startDelimiter.length();
        int endIndex = json.indexOf(endDelimiter, startIndex);
        return json.substring(startIndex, endIndex);
    }
}