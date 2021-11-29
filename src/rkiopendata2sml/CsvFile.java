package rkiopendata2sml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CsvFile {
	public static String readFile(String filePath) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(filePath));
		String contents = new String(bytes, StandardCharsets.UTF_8);
		return contents;
	}
	
	public static String[][] readCsvFile(String filePath) throws IOException {
		String contents = readFile(filePath);
		String[] lines = contents.split("\\n");
		ArrayList<String[]> result = new ArrayList<>();
		for (String line : lines) {
			if (line.length() == 0) {
				continue;
			}
			String[] values = line.split(",");
			for (int i=0; i<values.length; i++) {
				String value = values[i].strip();
				if (values[i].length() == 0) {
					value = null;
				}
				values[i] = value;
			}
			result.add(values);
		}
		return result.toArray(new String[0][]);
	}
}
