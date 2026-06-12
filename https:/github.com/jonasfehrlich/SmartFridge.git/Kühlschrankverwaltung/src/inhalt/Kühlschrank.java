package inhalt;

import java.util.*;
import java.io.*;
import inhalt.spezial.*;
import defalt.Grundlage;

public class Kühlschrank implements Grundlage{
	
LinkedList <Getraenk> getraenke=new LinkedList<Getraenk>();
LinkedList <Lebensmittel> lebensmittel=new LinkedList<Lebensmittel>();

public Kühlschrank(LinkedList liste1,LinkedList liste2) {
	this.lebensmittel=liste1;
	this.getraenke=liste2;

}

public void addGetrenk (Getraenk hinzufügen) {
	getraenke.add(hinzufügen);
}

public void deliteGetrenk(Getraenk wegmachen) {
	getraenke.remove(wegmachen);
}
public void addLebensmittel (Lebensmittel hinzufügen) {
	lebensmittel.add(hinzufügen);
}

public void deliteLebensmittel(Lebensmittel wegmachen) {
	lebensmittel.remove(wegmachen);

}
public void print() {
	System.out.println("Lebensmittel:");
	for(int i=0;i<lebensmittel.size();i++) {
		Produkt temp= lebensmittel.get(i);	
		System.out.println(temp.getName());
	}
	System.out.println("Getränke:");
	for(int i=0;i<getraenke.size();i++) {
		Produkt temp= getraenke.get(i);
		System.out.println(temp.getName());
	}
}

}
