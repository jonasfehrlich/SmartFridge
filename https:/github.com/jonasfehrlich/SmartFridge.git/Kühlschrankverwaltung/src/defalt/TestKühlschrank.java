package defalt;

import inhalt.spezial.*;
import inhalt.*;
import java.util.*;
import java.time.*;

public class TestKühlschrank {
	
	public static void main(String[] args) {
		
		Lebensmittel gehacktes=new Lebensmittel("Gehacktes",LocalDate.of(2026,6,15),1,500.0);
		Lebensmittel käse=new Lebensmittel("käse",LocalDate.of(2026,07,15),1,250.0);
		Getraenk milch=new Getraenk("Milch",LocalDate.of(2026,8,26),2,1000);
		Getraenk orangensaft=new Getraenk("Orangensaft",LocalDate.of(2026,10,26),2,1000);
		
		LinkedList <Lebensmittel> kühlschrankInhalt= new LinkedList<Lebensmittel>();
		kühlschrankInhalt.add(käse);
		kühlschrankInhalt.add(gehacktes);
		
		LinkedList <Getraenk> gekühlteGetränke= new LinkedList<Getraenk>();
		gekühlteGetränke.add(milch);
		gekühlteGetränke.add(orangensaft);
		
		Kühlschrank schrank=new Kühlschrank(kühlschrankInhalt,gekühlteGetränke);
		Einkaufsliste liste=new Einkaufsliste("testlist",kühlschrankInhalt,gekühlteGetränke);
		
		System.out.println("Kühlschrank");
		schrank.print();
		System.out.println("Einkaufsliste");
		liste.print();
	}

}
