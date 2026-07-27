package de.hwrberlin.kuelschrank.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import de.hwrberlin.kuehlschrank.util.APIAusnahme;
import de.hwrberlin.kuehlschrank.util.Datenspeicher;
import de.hwrberlin.kuelschrank.api.model.Ingredient;
import de.hwrberlin.kuelschrank.api.model.RezeptDetails;
import de.hwrberlin.kuehlschrank.model.*;

public class RezeptAPIClient {

	private final HttpClient httpClient;
	private final String apiKey;
	private final String basisURL="https://api.spoonacular.com";
	
	public RezeptAPIClient(String apiKey) {
	    this.apiKey = apiKey;
	    this.httpClient = HttpClient.newHttpClient();
	}
	
	public List<RezeptSuchtreffer> sucheRezepte(List<KuehlschrankProdukt> produkte,int anzahl) {

		HttpRequest request = HttpRequest.newBuilder()
		        .uri(URI.create(uriBauen(produkte,anzahl))) //uri nicht als string sondern als objekt in java verwendet-> kann überprüft erden, ob es eine uri ist
		        .GET()				//gibt die http methode an
		        .build();			//befehl, die reques zu erstellen

		try {
			HttpResponse<String> response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
			
			String json = response.body();
			Type typ = new TypeToken<List<RezeptSuchtreffer>>() {}.getType();
			List<RezeptSuchtreffer> treffer = Datenspeicher.ausJson(json, typ);
			return treffer;
			
		} catch (IOException e) {
			throw new APIAusnahme("Die Verbindung zur Rezept-API konnte nicht hergestellt werden.",e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new APIAusnahme("Die Verbindung zur Rezept-API konnte nicht hergestellt werden.",e);
		}	
	}
	
	public Rezept ladeRezepte(int rezeptId) {
		HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(detailUriBauen(rezeptId)))
	            .GET()
	            .build();

	    try {
	        HttpResponse<String> response = httpClient.send(
	                request,
	                HttpResponse.BodyHandlers.ofString()
	        );

	        String json = response.body();

	        return rezeptAusJson(json);

	    } catch (IOException e) {
	        throw new APIAusnahme("Die Verbindung zur Rezept-API konnte nicht hergestellt werden.", e);
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        throw new APIAusnahme("Die Verbindung zur Rezept-API konnte nicht hergestellt werden.", e);
	    }
	}
	
	private String zutatenAufgelistet(List<KuehlschrankProdukt> produkte) {
		
		StringBuilder zutatenBuilder = new StringBuilder();
		for (KuehlschrankProdukt produkt : produkte) {
			if(!zutatenBuilder.isEmpty()) {zutatenBuilder.append(',');}
			zutatenBuilder.append(produkt.getName());
		}
		return zutatenBuilder.toString();
	}
	
	private String uriBauen(List<KuehlschrankProdukt> produkte,int anzahl) {
		
		StringBuilder uriBuilder= new StringBuilder();
		uriBuilder.append(basisURL);
		uriBuilder.append("/recipes/findByIngredients");
		uriBuilder.append("?");
		uriBuilder.append("ingredients=");
		uriBuilder.append(zutatenAufgelistet(produkte));
		uriBuilder.append("&number=");
		uriBuilder.append(anzahl);
		uriBuilder.append("&apiKey=");
		uriBuilder.append(apiKey);

		String url = uriBuilder.toString();
		System.out.println(url);
		return url;
		//return uriBuilder.toString();
	}
	private String detailUriBauen(int rezeptId) {
	    StringBuilder uriBuilder = new StringBuilder();
	    uriBuilder.append(basisURL);
	    uriBuilder.append("/recipes/");
	    uriBuilder.append(rezeptId);
	    uriBuilder.append("/information");
	    uriBuilder.append("?apiKey=");
	    uriBuilder.append(apiKey);

	    return uriBuilder.toString();
	}
	private List<String> leseZutaten(List<Ingredient> zutaten) {
		List<String> liste=new ArrayList<String>();
		for (int i=0;i<zutaten.size();i++) {
			Ingredient aktuelleZutat=zutaten.get(i);
			liste.add(aktuelleZutat.getOriginal());
		}
		return liste;
	}
	private Rezept rezeptAusJson(String json) {

	    RezeptDetails details = Datenspeicher.ausJson(json, RezeptDetails.class);
	    
	    List<String> zutaten = leseZutaten(details.getExtendedIngredients());

	    return new Rezept(
	    	    details.getTitle(),
	    	    bereinigeBeschreibung(details.getInstructions()),
	    	    zutaten,
	    	    details.getReadyInMinutes() + " Minuten",
	    	    details.getSourceUrl()
	    	    );
	}
	private String bereinigeBeschreibung(String beschreibung) {

	    if (beschreibung == null) {
	        return "";
	    }
	    return beschreibung.replaceAll("<[^>]*>", "");
	}
		
}
