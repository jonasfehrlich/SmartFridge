package de.hwrberlin.kuehlschrank.util;

public class APIAusnahme extends RuntimeException{
	public APIAusnahme(String nachricht) {
		super(nachricht);
		}
	public APIAusnahme(String nachricht, Throwable ursache) {
	    super(nachricht, ursache);
	}
}
