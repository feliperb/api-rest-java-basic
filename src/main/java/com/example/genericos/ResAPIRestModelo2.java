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

class ResAPIRestModelo2 {

    /*
     * Complete the 'getWinnerTotalGoals' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     *  1. STRING competition
     *  2. INTEGER year
     */
    public static int getWinnerTotalGoals(String competition, int year) {
        try {
            // Codificando o nome da competição para garantir que espaços e caracteres especiais sejam tratados corretamente
            String encodedCompetition = URLEncoder.encode(competition, StandardCharsets.UTF_8.toString());

            // URL para buscar a competição e seus dados (ano e nome)
            String urlTemplate = "https://jsonmock.hackerrank.com/api/football_competitions?name=%s&year=%d";
            String urlCompletada = String.format(urlTemplate, encodedCompetition, year);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlCompletada))
                    .GET()
                    .build();

            // Enviar a requisição e obter a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // Analisar a resposta JSON
            JSONObject rootNode = new JSONObject(responseBody);
            JSONArray dataNode = rootNode.optJSONArray("data");

            if (dataNode == null || dataNode.length() == 0) {
                System.out.println("Nenhuma competição encontrada");
                return 0;
            }

            // Pegamos a competição do JSON
            JSONObject competitionData = dataNode.getJSONObject(0);
            String winner = competitionData.getString("winner"); // Vencedor da competição
            System.out.println("Vencedor da competição: " + winner);

            // Agora que temos o vencedor, buscamos as partidas que ele jogou
            return getTotalGoalsForWinner(winner, encodedCompetition, year);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Função para buscar as partidas e somar os gols do vencedor
    private static int getTotalGoalsForWinner(String winner, String competition, int year) {
        int totalGoals = 0;
        int page = 1;
        boolean hasMorePages = true;

        // Buscando partidas em que o time pode ser o "team1" ou "team2"
        while (hasMorePages) {
            // Codifica o nome do time para garantir que caracteres especiais sejam tratados
            try {
                String encodedTeam1 = URLEncoder.encode(winner, StandardCharsets.UTF_8.toString());

                // URL para buscar partidas onde o time é "team1"
                String urlTemplate1 = "https://jsonmock.hackerrank.com/api/football_matches?competition=%s&year=%d&team1=%s&page=%d";
                String urlCompletada1 = String.format(urlTemplate1, competition, year, encodedTeam1, page);

                // URL para buscar partidas onde o time é "team2"
                String urlTemplate2 = "https://jsonmock.hackerrank.com/api/football_matches?competition=%s&year=%d&team2=%s&page=%d";
                String urlCompletada2 = String.format(urlTemplate2, competition, year, encodedTeam1, page);

                HttpClient client = HttpClient.newHttpClient();

                // Primeira requisição: onde o time é "team1"
                HttpRequest request1 = HttpRequest.newBuilder()
                        .uri(URI.create(urlCompletada1))
                        .GET()
                        .build();
                HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
                String responseBody1 = response1.body();
                JSONObject rootNode1 = new JSONObject(responseBody1);
                JSONArray matches1 = rootNode1.getJSONArray("data");

                // Segunda requisição: onde o time é "team2"
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create(urlCompletada2))
                        .GET()
                        .build();
                HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
                String responseBody2 = response2.body();
                JSONObject rootNode2 = new JSONObject(responseBody2);
                JSONArray matches2 = rootNode2.getJSONArray("data");

                // Processa as partidas onde o time é "team1"
                totalGoals += processMatches(matches1, winner);

                // Processa as partidas onde o time é "team2"
                totalGoals += processMatches(matches2, winner);

                // Verifica se há mais páginas de partidas
                int totalPages1 = rootNode1.getInt("total_pages");
                int totalPages2 = rootNode2.getInt("total_pages");

                if (page >= totalPages1 && page >= totalPages2) {
                    hasMorePages = false;
                } else {
                    page++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return totalGoals;
    }

    // Função auxiliar para processar as partidas e somar os gols
    private static int processMatches(JSONArray matches, String winner) {
        int totalGoals = 0;
        for (int i = 0; i < matches.length(); i++) {
            JSONObject match = matches.getJSONObject(i);
            int team1Goals = match.getInt("team1goals");
            int team2Goals = match.getInt("team2goals");

            // Se o vencedor for o "team1", somamos os gols de team1
            if (match.getString("team1").equals(winner)) {
                totalGoals += team1Goals;
            } else if (match.getString("team2").equals(winner)) {
                totalGoals += team2Goals;
            }
        }
        return totalGoals;
    }
}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        // Lê o nome da competição e o ano
        String competition = bufferedReader.readLine();
        int year = Integer.parseInt(bufferedReader.readLine().trim());

        // Chama a função que vai retornar o total de gols do vencedor
        int result = Result.getWinnerTotalGoals(competition, year);

        // Escreve o resultado final
        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
