package de.hwrberlin.kuehlschrank.util;
/**
 * Eigene Ausnahme-Klasse. Vorlesung 2.1.6: spezialisierte Fehlerklassen.
 */
public class KuehlschrankAusnahme extends RuntimeException {
    public KuehlschrankAusnahme(String nachricht)               { super(nachricht); }
    public KuehlschrankAusnahme(String nachricht, Throwable u)  { super(nachricht, u); }
}
