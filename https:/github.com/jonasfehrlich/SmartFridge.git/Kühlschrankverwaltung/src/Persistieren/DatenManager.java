package Persistieren;

import java.io.*;
import inhalt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DatenManager {
	
	Gson gson =new GsonBuilder().setPrettyPrinting().create();
	
	public void kühlschrankSpeichern(Kühlschrank kühlschrank) {
		try {
			FileWriter write = new FileWriter("kuelschrank.json");
			gson.toJson(kühlschrank);
			write.close();
			}
		catch(IOException e){
			System.err.println(e.toString());
		}
	}
	public Kühlschrank KühlschrnakLaden() {
	
		try {
			FileReader reader =new FileReader("kuelschrank.json");
			Kühlschrank k= gson.fromJson(reader, Kühlschrank.class);
			return k;
		}
		catch(IOException e){
			System.err.println(e.toString());
		}
		return null;
		
	}
	
	
	public void einkaufslisteSpeichern(Einkaufsliste liste) {
		try {
			FileWriter write = new FileWriter("einkaufsliste.json");
			gson.toJson(liste);
			write.close();
			}
		catch(IOException e){
			System.err.println(e.toString());
		}
	}
	public  Einkaufsliste einkaufslisteLaden() {
	
		try {
			FileReader reader =new FileReader("kuelschrank.json");
			return gson.fromJson(reader, Einkaufsliste.class);
			
		}
		catch(IOException e){
			System.err.println(e.toString());
		}
		return null;
	}

}
