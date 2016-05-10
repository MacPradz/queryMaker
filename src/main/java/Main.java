import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        String loadsetId = "12384";
        ChangeCurveNames query = new ChangeCurveNames(loadsetId,getInputPath(args));
        String answer = query.getQuery();

        String outputPath = getOutputPath(args);
        try ( PrintWriter out = new PrintWriter(outputPath) ) {
            out.println(answer);
        }
    }

    private static String getLoadsetId(List<CSVRecord> records) {
        CSVRecord commentLine = records.get(0);
        if ( commentLine.hasComment() ){
            String comment = commentLine.getComment();

            Pattern pattern = Pattern.compile("loadset_ID:.*?(\\d+).*");
            Matcher matcher = pattern.matcher(comment);
            matcher.find();
            return matcher.group(1);
        }
        return null;
    }

    private static String getOutputPath(String[] args) {
        String outputPath;
        if ( args.length==2  ){
            outputPath = args[1];
        }else{
            outputPath = "output_query.txt";
        }
        return outputPath;
    }

    private static String getInputPath(String[] args) {
        String inputPath;
        if ( args.length>=1 ){
            inputPath = args[0];
        }else{
            inputPath = "./input.txt";
        }
        return inputPath;
    }
}
