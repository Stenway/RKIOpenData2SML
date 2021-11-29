package rkiopendata2sml;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import ucd2sml.PrettySmlOutput;

public class CsvHospitalisierungenDeutschland {
	public class Datensatz {
		public final LocalDate Datum;
		public final Bundesland Bundesland;
		public final String Altersgruppe;
		public final int Faelle;
		public final double Inzidenz;
		
		public Datensatz(LocalDate datum, Bundesland bundesland, String altersgruppe, int faelle, double inzidenz) {
			Datum = datum;
			Bundesland = bundesland;
			Altersgruppe = altersgruppe;
			Faelle = faelle;
			Inzidenz = inzidenz;
		}
		
		public String Datenelement(int index) {
			if (index == 0) {
				return Integer.toString(Faelle);
			} else {
				return String.format("%.2f", Inzidenz);
				//return Double.toString(Inzidenz);
			}
		}
		
		public String Key() {
			return Bundesland + "_" + Altersgruppe+ "_" + Datum.toString();
		}
	}
	
	public ArrayList<Datensatz> Daten = new ArrayList<>();
	public LinkedHashSet<LocalDate> Datumsliste = new LinkedHashSet<>();
	public LocalDate AktuellesDatum;
	public LinkedHashSet<String> Altersgruppen = new LinkedHashSet<>();
	
	String[] altersgruppen = new String[] {"00-04", "05-14", "15-34", "35-59", "60-79", "80+"};
	String[] altersgruppenLabel = new String[] {"0-4", "5-14", "15-34", "35-59", "60-79", "80+"};
	
	public CsvHospitalisierungenDeutschland(String[][] csvData) {
		AktuellesDatum = LocalDate.parse(csvData[1][0]);
				
		for (int i=1; i<csvData.length; i++) {
			String[] csvLine = csvData[i];
			LocalDate datum = LocalDate.parse(csvLine[0]);
			int bundeslandId = Integer.parseInt(csvLine[2]);
			Bundesland bundesland = Bundesland.getById(bundeslandId);
			String altersgruppe = csvLine[3];
			int siebenTage_Hospitalisierung_Faelle = Integer.parseInt(csvLine[4]);
			double siebenTage_Hospitalisierung_Inzidenz = Double.parseDouble(csvLine[5]);
			
			Datensatz datensatz = new Datensatz(datum, bundesland, altersgruppe, 
					siebenTage_Hospitalisierung_Faelle, siebenTage_Hospitalisierung_Inzidenz);
			Daten.add(datensatz);
			
			if (!Datumsliste.contains(datum)) {
				Datumsliste.add(datum);
			}
			if (!Altersgruppen.contains(altersgruppe)) {
				Altersgruppen.add(altersgruppe);
			}
			
			String key = datensatz.Key();
			lookup.put(key, datensatz);
		}
	}
	
	private LinkedHashMap<String, Datensatz> lookup = new LinkedHashMap<>();
	
	public Datensatz get(Bundesland bundesland, String altersgruppe, LocalDate datum) {
		String key = bundesland + "_" + altersgruppe+ "_" + datum.toString();
		if (lookup.containsKey(key)) {
			return lookup.get(key);
		}
		throw new IllegalStateException();
	}
	
	private void convert(PrettySmlOutput sml, Table table, Bundesland bundesland, String altersgruppe, String altersgruppeLabel, int datenelementIndex) {
		ArrayList<String> line = table.addLine();
		line.add(altersgruppeLabel);
		for (LocalDate datum : Datumsliste) {
			Datensatz datensatz = get(bundesland, altersgruppe, datum);
			line.add(datensatz.Datenelement(datenelementIndex));
		}
	}
	
	private void convert(PrettySmlOutput sml, Table table, Bundesland bundesland, int datenelementIndex) {
		ArrayList<String> line = table.addLine();
		line.add(bundesland.Name());
		for (String altersgruppe : altersgruppen) {
			Datensatz datensatz = get(bundesland, altersgruppe, AktuellesDatum);
			line.add(datensatz.Datenelement(datenelementIndex));
		}
		
		line.add("|");
		Datensatz datensatz = get(bundesland, "00+", AktuellesDatum);
		line.add(datensatz.Datenelement(datenelementIndex));
	}
	
	private void convertNachBundeslandUndAltersgruppe(PrettySmlOutput sml, int datenelementIndex) {
		sml.beginElement("NachBundeslandUndAltersgruppe");
		Table table = new Table();
		sml.writeAttribute("Datum", AktuellesDatum.toString());
		ArrayList<String> line = table.addLine();
		line.add("Bundesland\\Altersgruppe");
		for (int i=0; i<altersgruppen.length; i++) {
			line.add(altersgruppenLabel[i]);
		}
		line.add("|");
		line.add("Gesamt");
			
		for (Bundesland bundesland : Bundesland.values()) {
			if (bundesland == Bundesland.BUNDESGEBIET) {
				continue;
			}
			convert(sml, table, bundesland, datenelementIndex);
		}
		
		sml.writeTable(table.toArray(), true);
		
		sml.closeElement();
	}
	
	private void convertVerlaufNachBundesland(PrettySmlOutput sml, int datenelementIndex) {
		sml.beginElement("VerlaufNachBundesland");
		Table table = new Table();
		
		ArrayList<String> line = table.addLine();
			line.add("Bundesland\\Datum");
			for (LocalDate datum : Datumsliste) {
				line.add(datum.toString());
			}
			
		for (Bundesland bundesland : Bundesland.values()) {
			if (bundesland == Bundesland.BUNDESGEBIET) {
				continue;
			}
			convert(sml, table, bundesland, "00+", bundesland.Name(), datenelementIndex);
			
		}
		line = table.addLine();
		line.add("---");
		for (LocalDate datum : Datumsliste) {
			line.add("---");
		}
		convert(sml, table, Bundesland.BUNDESGEBIET, "00+", "Bundesgebiet", datenelementIndex);

		sml.writeTable(table.toArray(), true);
		
		sml.closeElement();
	}
	
	private void convertVerlaufNachBundeslandUndAltersgruppe(PrettySmlOutput sml, int datenelementIndex) {
		sml.beginElement("VerlaufNachBundeslandUndAltersgruppe");
		for (Bundesland bundesland : Bundesland.values()) {
			sml.beginElement(bundesland.Name());
			Table table = new Table();
			
			ArrayList<String> line = table.addLine();
			line.add("Altersgruppe\\Datum");
			for (LocalDate datum : Datumsliste) {
				line.add(datum.toString());
			}
		
			for (int i=0; i<altersgruppen.length; i++) {
				String altersgruppe = altersgruppen[i];
				String altersgruppeLabel = altersgruppenLabel[i];
				convert(sml, table, bundesland, altersgruppe, altersgruppeLabel, datenelementIndex);
			}
			
			line = table.addLine();
			line.add("---");
			for (LocalDate datum : Datumsliste) {
				line.add("---");
			}
			convert(sml, table, bundesland, "00+", "Gesamt", datenelementIndex);
			
			sml.writeTable(table.toArray(), true);
			sml.closeElement();
		}
		sml.closeElement();
	}
	
	private void convert(PrettySmlOutput sml, String name, int datenelementIndex) {
		sml.beginElement(name);
		convertNachBundeslandUndAltersgruppe(sml, datenelementIndex);
		convertVerlaufNachBundesland(sml, datenelementIndex);
		convertVerlaufNachBundeslandUndAltersgruppe(sml, datenelementIndex);
		sml.closeElement();
	}
	
	public void convert(String filePath) throws IOException {
		PrettySmlOutput sml = new PrettySmlOutput("HospitalisierungDeutschland", "Ende");
		sml.writeAttribute("DatensatzDatum", AktuellesDatum.toString());
		sml.writeAttribute("Quelle", "https://github.com/robert-koch-institut/COVID-19-Hospitalisierungen_in_Deutschland");
		
		convert(sml, "7-Tage-Fallzahl der hospitalisierten COVID-19-Fälle", 0);
		convert(sml, "7-Tage-Inzidenz der hospitalisierten COVID-19-Fälle", 1);
		
		sml.finish();
		sml.save(filePath);
	}
	
	public static CsvHospitalisierungenDeutschland load(String filePath) throws IOException {
		String[][] csvData = CsvFile.readCsvFile(filePath);
		CsvHospitalisierungenDeutschland result = new CsvHospitalisierungenDeutschland(csvData);
		return result;
	}
}
