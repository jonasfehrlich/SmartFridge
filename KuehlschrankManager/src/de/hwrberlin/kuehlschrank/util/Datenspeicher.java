package de.hwrberlin.kuehlschrank.util;
import java.io.*;
/**
 * Generische Serialisierungs-Hilfsklasse.
 * Vorlesung 2.1.5: ObjectOutputStream/InputStream (persistente Objekte).
 * Vorlesung 2.1.8: Generische Methoden <T extends Serializable>.
 */
public class Datenspeicher {
    private Datenspeicher() {}

    public static <T extends Serializable> void speichern(T objekt, String dateipfad) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dateipfad))) {
            oos.writeObject(objekt);
        } catch (IOException e) {
            throw new KuehlschrankAusnahme("Fehler beim Speichern: " + dateipfad, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T laden(String dateipfad) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dateipfad))) {
            return (T) ois.readObject();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            throw new KuehlschrankAusnahme("Fehler beim Laden: " + dateipfad, e);
        }
    }
}
