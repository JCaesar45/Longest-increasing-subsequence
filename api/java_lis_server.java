import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class LisServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/healthz", exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendJson(exchange, 405, """
                        {"error":"Method not allowed"}
                        """);
                return;
            }

            sendJson(exchange, 200, """
                    {"status":"ok"}
                    """);
        });

        server.createContext("/api/lis", exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendJson(exchange, 405, """
                        {"error":"Method not allowed"}
                        """);
                return;
            }

            try {
                String body = readBody(exchange.getRequestBody());
                List<Double> values = parseValues(body);

                if (values.isEmpty()) {
                    throw new IllegalArgumentException("values must not be empty");
                }

                List<Double> result = findSequence(values);
                sendJson(exchange, 200, toJson(result));
            } catch (Exception error) {
                sendJson(exchange, 400, """
                        {"error":"Bad request"}
                        """);
            }
        });

        server.start();
        System.out.println("Aurelia LIS Java API listening on http://localhost:8080");
    }

    static List<Double> findSequence(List<Double> values) {
        int n = values.size();
        int[] lengths = new int[n];
        int[] parents = new int[n];

        Arrays.fill(lengths, 1);
        Arrays.fill(parents, -1);

        int bestIndex = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (values.get(j) < values.get(i) && lengths[j] + 1 >= lengths[i]) {
                    lengths[i] = lengths[j] + 1;
                    parents[i] = j;
                }
            }

            if (lengths[i] >= lengths[bestIndex]) {
                bestIndex = i;
            }
        }

        List<Double> result = new ArrayList<>();
        int current = bestIndex;

        while (current != -1) {
            result.add(values.get(current));
            current = parents[current];
        }

        Collections.reverse(result);
        return result;
    }

    static List<Double> parseValues(String body) {
        int start = body.indexOf('[');
        int end = body.lastIndexOf(']');

        if (start < 0 || end < 0 || end <= start) {
            throw new IllegalArgumentException("Missing values array");
        }

        String inner = body.substring(start + 1, end).trim();

        if (inner.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }

        List<Double> values = new ArrayList<>();

        for (String part : inner.split(",")) {
            values.add(Double.parseDouble(part.trim()));
        }

        return values;
    }

    static String readBody(InputStream input) throws IOException {
        return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }

    static String toJson(List<Double> result) {
        char quote = '"';
        StringBuilder builder = new StringBuilder();

        builder.append('{')
               .append(quote).append("result").append(quote)
               .append(": [");

        for (int i = 0; i < result.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }

            builder.append(result.get(i));
        }

        builder.append("], ")
               .append(quote).append("length").append(quote)
               .append(": ")
               .append(result.size())
               .append('}');

        return builder.toString();
    }

    static void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
