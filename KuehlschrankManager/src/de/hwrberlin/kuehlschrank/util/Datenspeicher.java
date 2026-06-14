package de.hwrberlin.kuehlschrank.util;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * Generische Serialisierungs-Hilfsklasse.
 * Vorlesung 2.1.5: ObjectOutputStream/InputStream (persistente Objekte).
 * Vorlesung 2.1.8: Generische Methoden <T extends Serializable>.
 */
public class Datenspeicher {
    private Datenspeicher() {}
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public static <T> void speichern(T objekt, String dateipfad) {
    	try (FileWriter writer =new FileWriter(dateipfad)) {

    		gson.toJson(objekt, writer);

    	}catch(IOException e) {
    		throw new RuntimeException("Fehler beim Speichern",e);
    	}
    }

    @SuppressWarnings("unchecked")
    public static <T> T laden(String dateipfad,Class<T> typ) {
    	try (FileReader reader =new FileReader(dateipfad)) {

    		return gson.fromJson(reader,typ);

    	}catch(FileNotFoundException e) {
        		return null;
        	}
    	catch(IOException e) {

    		throw new RuntimeException("Fehler beim Laden",e);
    	}
    	
    }
}
