package rkiopendata2sml;

public class RKIOpenData2SML {

	public static void main(String[] args) {
		try {
			String filePath = "d:\\RKIDaten\\Aktuell_Deutschland_COVID-19-Hospitalisierungen.csv";
			CsvHospitalisierungenDeutschland hospitalisierungDE = CsvHospitalisierungenDeutschland.load(filePath);
			hospitalisierungDE.convert("d:\\RKIDaten\\HospitalisierungDE.sml");
			
		} catch (Exception e) {
			System.out.println("[ERROR] "+e.getMessage());
		}
	}
	
}
