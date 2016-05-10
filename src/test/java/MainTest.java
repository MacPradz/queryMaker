import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Test
    public void testMain() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File inputFile = new File(classLoader.getResource("input.txt").getFile());
        String inputFilePath = inputFile.getAbsolutePath();

        File outputFile = File.createTempFile("output",".txt");
        String outputFilePath = outputFile.getAbsolutePath();

        Main.main(new String[]{inputFilePath, outputFilePath});

        File expectedOutputFile = new File(classLoader.getResource("expectedOutput.txt").getFile());

        assertEquals("output file should match exactly the expected output",new String(Files.readAllBytes(expectedOutputFile.toPath())),new String(Files.readAllBytes(outputFile.toPath())));
    }
}