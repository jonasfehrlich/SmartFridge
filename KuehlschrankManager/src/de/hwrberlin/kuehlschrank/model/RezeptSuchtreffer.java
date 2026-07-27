package de.hwrberlin.kuehlschrank.model;
import com.google.gson.annotations.SerializedName;

public class RezeptSuchtreffer {
	private int id;
	@SerializedName("title")
    private String titel;

    @SerializedName("image")
    private String bildUrl;

    @SerializedName("missedIngredientCount")
    private int fehlendeZutaten;

    @SerializedName("usedIngredientCount")
    private int verwendeteZutaten;
	
	public RezeptSuchtreffer() {}//lehrer konstrucktor für verwendung von json
	 
	public RezeptSuchtreffer(int id,String titel, String bildUrl, int fehlendezutaten, int verwendeteZutaten) {
		this.id=id;
		this.titel=titel;
		this.bildUrl=bildUrl;
		this.fehlendeZutaten=fehlendezutaten;
		this.verwendeteZutaten=verwendeteZutaten;
	}
	
	public int getId() {
		return id;
	}

	public String getTitel() {
		return titel;
	}

	public String getBildUrl() {
		return bildUrl;
	}

	public int getFehlendeZutaten() {
		return fehlendeZutaten;
	}

	public int getVerwendeteZutaten() {
		return verwendeteZutaten;
	}

	@Override
	public String toString() {
		return titel + " (" +
		           verwendeteZutaten + " Zutaten vorhanden, " +
		           fehlendeZutaten + " fehlen)"+
		           bildUrl;
	}
}
