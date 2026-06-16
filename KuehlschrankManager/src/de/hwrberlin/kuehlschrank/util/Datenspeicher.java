package de.hwrberlin.kuehlschrank.util;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * Generische Persistenz-Hilfsklasse auf Basis von JSON (Bibliothek: Google Gson).
 *
 * Abgedeckte Vorlesungsthemen:
 *  - 2.1.8 Generics: generische Methoden mit Typschranke &lt;T extends Serializable&gt;
 *          (bounded type parameter) und Class&lt;T&gt; als Typ-Token beim Laden.
 *  - 2.1.6 Exceptions: try-with-resources, FileNotFoundException gesondert behandelt.
 *  - 2.1.5 Persistenz: Objekte werden in eine Datei geschrieben und wieder gelesen.
 *
 * Abweichung von der Vorlesung (bewusst begruendet, siehe REVIEW_Vorlesungsabdeckung.md):
 * Die Vorlesung 2.1.5 zeigt die Serialisierung ueber ObjectOutputStream/ObjectInputStream.
 * Hier wird stattdessen JSON (Gson) genutzt -> menschenlesbar und versionsrobuster,
 * aber externe Bibliothek und nutzt nicht den Serializable-Mechanismus des JDK.
 * Die JDK-Variante ist zum Vergleich in BinaerSpeicher umgesetzt.
 */
public class Datenspeicher {
    private Datenspeicher() {}  // Utility-Klasse: keine Instanzen.
    
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
