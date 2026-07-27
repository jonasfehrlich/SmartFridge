package de.hwrberlin.kuelschrank.api;

import java.util.LinkedList;
import java.util.List;


import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.service.*;

public class TestAPI {

	public static void main(String[] args) {
		 RezeptAPIClient client = new RezeptAPIClient("33f9c011c2a14681b1bb71041e3f4081");
		 KuehlschrankProdukt testItem1= new KuehlschrankProdukt("tomato", null, 1, "stück", null, 2, null);
		 KuehlschrankProdukt testItem2= new KuehlschrankProdukt("cheese", null, 1, "stück", null, 2, null);
		 LinkedList<KuehlschrankProdukt> liste=new LinkedList<KuehlschrankProdukt>();
		 liste.add(testItem1);
		 liste.add(testItem2);
		 

		    // Produkte anlegen

		 List<RezeptSuchtreffer> treffer = client.sucheRezepte(liste, 2);

		 for (RezeptSuchtreffer rezept : treffer) {
			 System.out.println( rezept.toString());
			    
		 }
		 Rezept rezept = client.ladeRezepte(treffer.get(0).getId());

		 System.out.println(rezept.getName());
		 System.out.println();
		 System.out.println(rezept.getBeschreibung());
		 System.out.println();
		 System.out.println(rezept.getZubereitungszeit());
		 System.out.println();
		 System.out.println(rezept.getQuelle());
		 System.out.println();
		 System.out.println(rezept.getZutaten());
	}

}
