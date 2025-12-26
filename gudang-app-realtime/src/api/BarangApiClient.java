package api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import model.Barang;

public class BarangApiClient {
    private static final String BASE_URL = "http://localhost/gudang-application-tier-php/public/barang";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson;

    public BarangApiClient() {
        // Register LocalDate adapter inline
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, type, context) -> LocalDate.parse(json.getAsString(),
                                DateTimeFormatter.ISO_LOCAL_DATE))
                .registerTypeAdapter(LocalDate.class,
                        (JsonSerializer<LocalDate>) (date, type, context) -> context
                                .serialize(date.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .create();
    }

    public List<Barang> findAll() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ApiResponse<ArrayList<Barang>> apiResp = gson.fromJson(response.body(),
                new TypeToken<ApiResponse<ArrayList<Barang>>>() {
                }.getType());
        if (!apiResp.success)
            throw new Exception(apiResp.message);
        return apiResp.data;
    }

    public void create(Barang b) throws Exception {
        String json = gson.toJson(b);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + extractErrorMessage(response.body()));
        }
        System.out.println("Raw response:\n" + response.body());
        ApiResponse<?> apiResp = gson.fromJson(response.body(), ApiResponse.class);
        if (!apiResp.success)
            throw new Exception(apiResp.message);
    }

    public void update(Barang b) throws Exception {
        var requestBody = new HashMap<String, Object>();
        requestBody.put("nama_barang", b.getNamaBarang());
        requestBody.put("kategori", b.getKategori());
        requestBody.put("stok", b.getStok());
        requestBody.put("tanggal_masuk", b.getTanggalMasuk() != null ? b.getTanggalMasuk().toString() : null);
        String json = gson.toJson(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + b.getIdBarang()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Raw response:\n" + response.body());
        handleResponse(response);
    }

    public void delete(int id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Raw response:\n" + response.body());
        handleResponse(response);
    }

    private static class ApiResponse<T> {
        boolean success;
        T data;
        String message;
    }

    private void handleResponse(HttpResponse<String> response) throws Exception {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + extractErrorMessage(response.body()));
        }
        ApiResponse<?> apiResp = gson.fromJson(response.body(), ApiResponse.class);
        if (!apiResp.success)
            throw new Exception(apiResp.message);
    }

    private String extractErrorMessage(String body) {
        try {
            ApiResponse<?> resp = gson.fromJson(body, ApiResponse.class);
            return resp.message != null ? resp.message : "Unknown server error";
        } catch (Exception e) {
            return "Server returned invalid response: " + body;
        }
    }
}