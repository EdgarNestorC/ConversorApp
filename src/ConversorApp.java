import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.Gson;
import java.util.Map;

public class ConversorApp {

    private static final String API_KEY = "17f7f9d20c62c5fcf2077cb1";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("=== Conversor de Monedas a USD ===");
            System.out.println("1. Pesos Argentinos (ARS)");
            System.out.println("2. Euros (EUR)");
            System.out.println("3. Pesos Mexicanos (MXN)");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();

            String monedaOrigen = "";
            switch (opcion) {
                case 1:
                    monedaOrigen = "ARS";
                    break;
                case 2:
                    monedaOrigen = "EUR";
                    break;
                case 3:
                    monedaOrigen = "MXN";
                    break;
                case 4:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción inválida.");
                    continue;
            }

            if (opcion >= 1 && opcion <= 3) {
                System.out.print("Ingrese el monto en " + monedaOrigen + ": ");
                double monto = scanner.nextDouble();
                double tasaCambio = obtenerTasaCambio(monedaOrigen, "USD");
                if (tasaCambio != -1) {
                    double convertido = monto * tasaCambio;
                    System.out.printf("Equivalente en USD: %.2f\n\n", convertido);
                } else {
                    System.out.println("Error al obtener la tasa de cambio.\n");
                }
            }

        } while (opcion != 4);

        scanner.close();
    }

    public static double obtenerTasaCambio(String monedaOrigen, String monedaDestino) {
        try {
            String urlStr = BASE_URL + API_KEY + "/latest/" + monedaOrigen;
            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            Reader reader = new InputStreamReader(conexion.getInputStream());
            TasaCambioRespuesta respuesta = gson.fromJson(reader, TasaCambioRespuesta.class);

            if ("success".equals(respuesta.result)) {
                Double tasa = respuesta.conversion_rates.get(monedaDestino);
                return tasa != null ? tasa : -1;
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return -1;
    }

    // Clase auxiliar para mapear la respuesta JSON
    static class TasaCambioRespuesta {
        String result;
        Map<String, Double> conversion_rates;
    }
}
