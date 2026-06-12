package inhalt;

	//importierte Klasse um Datumstyp für Variablen zu verwendne
import java.time.*;
import java.time.temporal.ChronoUnit;
import defalt.Grundlage;
import java.io.*;	//für Serializable

public abstract class Produkt implements Grundlage, Serializable{
		
	String name;
	LocalDate ablaufdatum;
	long restzeit;
	int anzahl;
	
	public Produkt (String name, LocalDate datum, int anzahl){
		this.name=name;
		this.ablaufdatum=datum;
		this.anzahl= anzahl;
	}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public LocalDate getAblaufdatum() {
			return ablaufdatum;
		}
		public void setAblaufdatum(LocalDate ablaufdatum) {
			this.ablaufdatum = ablaufdatum;
		}
		public int getAnzahl() {
			return anzahl;
		}
		public void setAnzahl(int anzahl) {
			this.anzahl = anzahl;
		}
		public long getRestzeit() {
			return restzeit=ChronoUnit.DAYS.between(LocalDate.now(),ablaufdatum);
		}
		public void print() {
			
		}
}
