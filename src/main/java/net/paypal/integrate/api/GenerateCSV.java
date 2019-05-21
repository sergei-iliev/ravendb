package net.paypal.integrate.api;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

public enum GenerateCSV {
INSTANCE;
	
	private static final char DEFAULT_SEPARATOR = ',';
	
	 //https://tools.ietf.org/html/rfc4180
    private String format(String value) {

        String result = value;
        if(value==null){
        	return "";
        }
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }
    
    public  void writeLine(Writer w, Collection<String> values) throws IOException {

        boolean first = true;

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(DEFAULT_SEPARATOR);
            }
         
                sb.append(format(value));

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());


    }	
}
