package de.hwrberlin.kuehlschrank.rezept;
import de.hwrberlin.kuehlschrank.model.Rezept;
import java.util.List;

/**
 * Interface fuer Rezept-Anbieter.
 * Vorlesung 2.1.4: Interface-Definition. Ermoeglicht Polymorphie (lokal/online).
 */
public interface RezeptAnbieter {
    List<Rezept> rezepteSuchen(List<String> verfuegbareZutaten);
    String getAnbieterName();
}
