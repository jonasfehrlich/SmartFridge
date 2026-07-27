package de.hwrberlin.kuelschrank.api.model;
import java.util.List;

public class RezeptDetails {
	  private String title;
	    private String summary;
	    private int readyInMinutes;
	    private String sourceUrl;
	    private List<Ingredient> extendedIngredients;
	    private String instructions;

	    public String getTitle() {
	        return title;
	    }

	    public String getSummary() {
	        return summary;
	    }

	    public int getReadyInMinutes() {
	        return readyInMinutes;
	    }

	    public String getSourceUrl() {
	        return sourceUrl;
	    }

	    public List<Ingredient> getExtendedIngredients() {
	        return extendedIngredients;
	    }
	    public String getInstructions() {
	    	return instructions;
	    }

}
