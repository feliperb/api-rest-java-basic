package main.java.com.example.genericos;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

class Result {

    public static int getTotalGoals(String team, int year) {
        int golsTime1 = getAllGolsTime(team, year, "team1");
        int golsTime2 = getAllGolsTime(team, year, "team2");
        return golsTime1 + golsTime2;
    }

	private static int getAllGolsTime(String team, int year, String team1or2) {
		
		try {
			String URL = "https://jsonmock.hackerrank.com/api/football_matches?year=%d&%s=%s&page=1";
			String URL_COMPLETA = String.format(URL, year, team1or2, team);
			
			
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL_COMPLETA)).build();
			
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			String responseBody = response.body();
			
			responseBody.indexOf("data");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
    
    
    
}