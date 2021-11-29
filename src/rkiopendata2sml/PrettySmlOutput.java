package ucd2sml;

import com.stenway.reliabletxt.ReliableTxtDocument;
import com.stenway.wsv.WsvSerializer;
import java.io.IOException;

public class PrettySmlOutput {
	StringBuilder sb = new StringBuilder();
	int indentationLevel = 0;	
	
	StringBuilder sbTemp = new StringBuilder();
	String endKeyword;
	
	public PrettySmlOutput(String rootName, String endKeyword) {
		beginElement(rootName);
		this.endKeyword = endKeyword;
	}
	
	private void appendIndentation() {
		for (int i=0; i<indentationLevel*2; i++) {
			sb.append(' ');
		}
	}
	
	private int getLength(String value) {
		sbTemp.setLength(0);
		WsvSerializer.serializeValue(sbTemp, value);
		return sbTemp.length();
	}
	
	private String serializeValue(String value) {
		sbTemp.setLength(0);
		WsvSerializer.serializeValue(sbTemp, value);
		return sbTemp.toString();
	}
	
	private int[] getColumnWidths(String[][] lines, int startIndex, int count) {
		int numColumns = 0;
		for (String[] values : lines) {
			numColumns = Math.max(numColumns, values.length);
		}
		
		StringBuilder sbTemp = new StringBuilder();
		
		int[] result = new int[numColumns];
		int endIndex = lines.length;
		if (count > 0) {
			Math.min(startIndex+count, lines.length);
		}
		for (int i=startIndex; i<endIndex; i++) {
			String[] values = lines[i];
			for (int j=0; j<values.length; j++) {
				String curValue = values[j];
				int curLength = getLength(curValue);
				result[j] = Math.max(result[j],curLength);
			}
		}
		for (int i=0; i<numColumns; i++) {
			result[i] += 1 + (result[i] + 1) % 2;
		}
		return result;
	}
	
	public void writeTable(String[][] lines, boolean padLeft) {
		int[] columnWidths = getColumnWidths(lines, 0, -1);;
		for (int i=0; i<lines.length; i++) {
			String[] values = lines[i];
			appendIndentation();
			for (int j=0; j<values.length; j++) {
				String curValue = values[j];
				int curLength = getLength(curValue);
				String serializedValue = sbTemp.toString();
				
				int restLength = columnWidths[j] - curLength;
				if (padLeft && j > 0) {
					for (int s=0; s<restLength; s++) {
						sb.append(' ');
					}
				}
				sb.append(serializedValue);
				if (j == values.length-1) {
					break;
				}
				if (!(padLeft && j > 0)) {
					for (int s=0; s<restLength; s++) {
						sb.append(' ');
					}
				}
			}
			sb.append('\n');
		}
	}
	
	public void writeAttribute(String[] attributeLine) {
		appendIndentation();
		for (int i=0; i<attributeLine.length; i++) {
			String curValue = attributeLine[i];
			String serializedValue = serializeValue(curValue);
			sb.append(serializedValue);
			if (i == attributeLine.length-1) {
				break;
			}
			
			sb.append(' ');
		}
		sb.append('\n');
	}
	
	public void writeAttribute(String name, String... attributeLine) {
		appendIndentation();
		sb.append(serializeValue(name));
		sb.append(' ');
		for (int i=0; i<attributeLine.length; i++) {
			String curValue = attributeLine[i];
			String serializedValue = serializeValue(curValue);
			sb.append(serializedValue);
			if (i == attributeLine.length-1) {
				break;
			}
			
			sb.append(' ');
		}
		sb.append('\n');
	}
	
	public void beginElement(String name) {
		appendIndentation();
		sb.append(serializeValue(name)+"\n");
		indentationLevel++;
	}
	
	public void closeElement() {
		indentationLevel--;
		appendIndentation();
		sb.append(endKeyword+"\n");
	}
	
	public void finish() {
		indentationLevel--;
		appendIndentation();
		sb.append(endKeyword);
	}

	@Override
	public String toString() {
		return sb.toString();
	}
	
	public void save(String filePath) throws IOException {
		ReliableTxtDocument.save(toString(), filePath);
	}
}
