import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by uc198829 on 10/5/2016.
 */
public class ChangeCurveNames {
    private String loadsetId;
    private String inputFilePath;
    static String DW_CURVE_UPDATE = "UPDATE dw.curve SET name = REGEXP_REPLACE(name, '%s', '%s') WHERE name LIKE '%s' AND id = oldRow.curve_id;";
    static String LOAD_SET2_CURVE_UPDATE = "UPDATE load_set2_curve SET external_id = REGEXP_REPLACE(external_id, '%s', '%s') WHERE external_id LIKE '%s' AND curve_id = oldRow.curve_id;";
    static String DW_CURVE_REMOVE = "DELETE dw.curve WHERE name LIKE '%s';";
    static String LOAD_SET2_CURVE_REMOVE = "DELETE load_set2_curve WHERE external_id LIKE '%s';";

    private static final String QUERY_START = "DECLARE\n" +
            "    v_code  NUMBER;\n" +
            "    v_errm  VARCHAR2(255);\n" +
            "BEGIN\n" +
            "FOR oldRow IN ( SELECT curve_id FROM load_set2_curve WHERE load_set_id = %LOADSET_ID%) LOOP\n" +
            "BEGIN\n" +
            "    DBMS_OUTPUT.PUT_LINE ( '---' ||oldRow.curve_id);\n" +
            " -- DBMS_OUTPUT.PUT_LINE('Renaming curve for id=' ||oldRow||);\n" +
            " \n";

    private static final String QUERY_END = "\nEXCEPTION  WHEN OTHERS  THEN\n" +
            "    v_code := SQLCODE;\n" +
            "    v_errm := SUBSTR(SQLERRM, 1, 250);\n" +
            "    DBMS_OUTPUT.PUT_LINE (v_code || ' ' || v_errm);\n" +
            "END;\n" +
            "END LOOP;\n" +
            "end;";

    public ChangeCurveNames(String loadsetId, String inputFilePath) {
        this.loadsetId = loadsetId;
        this.inputFilePath = inputFilePath;
    }

    public String getQuery() throws IOException {
        StringBuilder finalQuery = new StringBuilder(QUERY_START.replace("%LOADSET_ID%", loadsetId));

        CSVParser csv = getCsv();
        List<CSVRecord> records = csv.getRecords();
        for ( CSVRecord record : records ) {
            if ( record.get(0).equals("curvename piece to be replaced ") ) continue;
            String fromString = record.get(0);
            String toString = record.get(1);
            String whereLike =record.get(2);

            finalQuery.append("--UPDATE from: " + fromString + " to: " + toString + " where curve like: " + whereLike + "'\n");
            finalQuery.append(String.format(DW_CURVE_UPDATE, fromString, toString, whereLike) + "\n");
            finalQuery.append(String.format(LOAD_SET2_CURVE_UPDATE, fromString, toString, whereLike) + "\n");

            finalQuery.append("--REMOVE " + " where curve like: " + whereLike + "'\n");
            finalQuery.append(String.format(DW_CURVE_REMOVE, whereLike) + "\n");
            finalQuery.append(String.format(LOAD_SET2_CURVE_REMOVE, whereLike) + "\n\n");

        }
        return finalQuery.append(QUERY_END).toString();
    }

    private CSVParser getCsv() {
        File inputFile = new File(inputFilePath);
        return getCsvFromFile(inputFile);
    }

    private CSVParser getCsvFromFile(File inputFile) {
        CSVFormat format = CSVFormat.DEFAULT
                .withIgnoreSurroundingSpaces()
                .withCommentMarker('#');
        CSVParser csvDocument = null;
        try {
            csvDocument = new CSVParser(new FileReader(inputFile), format);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return csvDocument;
    }
}
