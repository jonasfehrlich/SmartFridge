package inhalt.spezial;

import java.time.*;
import inhalt.Produkt;

public class Lebensmittel extends Produkt{
	
	double masse;
	public Lebensmittel (String name, LocalDate datum, int anzahl, double masse){
		super(name,datum,anzahl);
		this.masse=masse;
	}
	public double getMasse() {
		return masse;
	}
	public void setMasse(int masse) {
		this.masse = masse;
	}
	public void print() {
		
	}


}
