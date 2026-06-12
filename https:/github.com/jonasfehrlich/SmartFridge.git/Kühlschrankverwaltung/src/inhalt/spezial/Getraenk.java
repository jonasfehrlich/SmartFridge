package inhalt.spezial;

import java.time.*;
import inhalt.Produkt;

public class Getraenk extends Produkt{
	double volumen;
	public Getraenk(String name, LocalDate datum, int anzahl, double volumen){
		super(name,datum,anzahl);
		this.volumen=volumen;
	}
	public double getVolumen() {
		return volumen;
	}
	public void setVolumen(double volumen) {
		this.volumen = volumen;
	}

}
