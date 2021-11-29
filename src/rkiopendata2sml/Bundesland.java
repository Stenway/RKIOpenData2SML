package rkiopendata2sml;

public enum Bundesland {
	BUNDESGEBIET(0, "Bundesgebiet"),
	BADEN_WUERTTEMBERG(8, "Baden-Württemberg"),
	BAYERN(9, "Bayern"),
	BERLIN(11, "Berlin"),
	BRANDENBURG(12, "Brandenburg"),
	BREMEN(4, "Bremen"),
	HAMBURG(2, "Hamburg"),
	HESSEN(6, "Hessen"),
	MECKLENBURG_VORPOMMERN(13, "Mecklenburg-Vorpommern"),
	NIEDERSACHSEN(3, "Niedersachsen"),
	NORDRHEIN_WESTFALEN(5, "Nordrhein-Westfalen"),
	RHEINLAND_PFALZ(7, "Rheinland-Pfalz"),
	SAARLAND(10, "Saarland"),
	SACHSEN(14, "Sachsen"),
	SACHSEN_ANHALT(15, "Sachsen-Anhalt"),
	SCHLESWIG_HOLSTEIN(1, "Schleswig-Holstein"),
	THUERINGEN(16, "Thüringen");
	
	private final int id;
	private final String name;

	Bundesland(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int Id() { return id; }
	public String Name() { return name; }
	
	public static Bundesland getById(int id) {
        for (Bundesland bundesland : Bundesland.values()) {
			if (bundesland.Id() == id) { return bundesland; }
		}
		throw new IllegalArgumentException();
    }
}
