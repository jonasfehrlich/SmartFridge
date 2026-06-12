package inhalt;

import java.time.*;
import defalt.Grundlage;

import java.util.*;
import inhalt.spezial.*;

public class Einkaufsliste implements Grundlage{
	String name;
	LinkedList <Getraenk> gebrauchteGetraenke=new LinkedList<Getraenk>();
	LinkedList <Lebensmittel> gebrauchteLebensmittel=new LinkedList<Lebensmittel>();
	
	public Einkaufsliste(String name,LinkedList liste1, LinkedList liste2) {
		this.name=name;
		this.gebrauchteLebensmittel=liste1;
		this.gebrauchteGetraenke=liste2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedList<Getraenk> getGebrauchteGetraenke() {
		return gebrauchteGetraenke;
	}
	public LinkedList<Lebensmittel> getGebrauchteLebensmittel() {
		return gebrauchteLebensmittel;
	}

	public void addGetrenk (Getraenk hinzufügen) {
		gebrauchteGetraenke.add(hinzufügen);
	}
	
	public void deliteGetrenk(Getraenk wegmachen) {
		gebrauchteGetraenke.remove(wegmachen);
	}
	public void addLebensmittel (Lebensmittel hinzufügen) {
		gebrauchteLebensmittel.add(hinzufügen);
	}
	
	public void deliteLebensmittel(Lebensmittel wegmachen) {
		gebrauchteLebensmittel.remove(wegmachen);
	}
	public void print() {
		System.out.println("Lebensmittel:");
		for(int i=0;i<gebrauchteLebensmittel.size();i++) {
			Produkt temp= gebrauchteLebensmittel.get(i);	
			System.out.println(temp.getName());
		}
		System.out.println("Getränke:");
		for(int i=0;i<gebrauchteGetraenke.size();i++) {
			Produkt temp= gebrauchteGetraenke.get(i);
			System.out.println(temp.getName());
		}
	}

}
