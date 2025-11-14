package main.java.com.example.genericos;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

class APIRestModelo {

    /*
     * Complete the 'getTotalGoals' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     *  1. STRING team
     *  2. INTEGER year
     */

    public static int getTotalGoals(String team, int year) {
        // Return a quantidade de gols desse time, nesse ano
        int golsTime1 = getAllGolsTime(team, year, "team1");
        int golsTime2 = getAllGolsTime(team, year, "team2");
        return golsTime1 + golsTime2;
    }

    private static int getAllGolsTime(String team, int year, String teamPosition) {
        int totalGols = 0;
        int page = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            try {
                // Codificar o nome do time para a URL
                String encodedTeam = URLEncoder.encode(team, StandardCharsets.UTF_8.toString());

                String urlTemplate = "https://jsonmock.hackerrank.com/api/football_matches?year=%s&%s=%s&page=%d";
                String urlCompletada = String.format(urlTemplate, year, teamPosition, encodedTeam, page);

                // Fazer GET para chamar o time, seja time1 ou time2
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlCompletada))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Status da Resposta: " + response.statusCode());
                String responseBody = response.body();
                System.out.println("Resposta Completa: " + responseBody);

                // Verificar se a resposta contém a chave 'data'
                JSONObject rootNode = new JSONObject(responseBody);
                JSONArray dataNode = rootNode.optJSONArray("data");

                if (dataNode == null || dataNode.length() == 0) {
                    // Se não houver dados, significa que não há partidas para esse time e ano
                    System.out.println("Nenhum dado encontrado para o time " + team + " no ano " + year);
                    hasMorePages = false;
                } else {
                    // Iterar sobre os jogos para somar os gols
                    for (int i = 0; i < dataNode.length(); i++) {
                        JSONObject match = dataNode.getJSONObject(i);
                        if (teamPosition.equals("team1")) {
                            totalGols += match.getInt("team1goals");
                        } else {
                            totalGols += match.getInt("team2goals");
                        }
                    }
                }

                // Verificar se há mais páginas para percorrer
                int totalPages = rootNode.getInt("total_pages");
                hasMorePages = page < totalPages;
                page++;

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        return totalGols;
    }
}