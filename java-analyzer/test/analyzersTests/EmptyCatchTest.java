package analyzersTests;

import analyzers.EmptyCatch;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.CatchClause;
import model.AnalysisResult;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class EmptyCatchTest {
    @Test
    public void detectsSimpleEmptyCatch(){
        // Get code from resources/sample-java/EmptyCatchExample.java by replacing the path
        String path = "../resources/sample-java/EmptyCatchExample.java";
        // Get the code from the file
        try {
            String code = Files.readString(Path.of(path));
            System.out.println("Code: " + code);
            CatchClause cc = getFirstCatchClause(code);
            System.out.println("Issue: " + cc);
            List<AnalysisResult> results = analyze(cc);

            assertEquals(1, results.size());
            assertEquals("EMPTY_CATCH", results.get(0).code());
            assertEquals(5, results.get(0).line());
            assertEquals("Empty catch block found", results.get(0).message());
            assertEquals("Add proper handling of the exception:\n" +
                    "- Log the exception\n" +
                    "- Add a comment explaining why the exception is ignored\n" +
                    "- Refactor the code to handle the exception\n" +
                    "- Remove the empty catch block if the exception is not needed\n", results.get(0).suggestion());
            assertEquals("HIGH", results.get(0).riskLevel().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private CatchClause getFirstCatchClause(String code) {
        CompilationUnit cu = StaticJavaParser.parse(code);
        Optional<CatchClause> catchClause = cu.findFirst(CatchClause.class);
        return catchClause.orElseThrow();
    }

    private List<AnalysisResult> analyze(CatchClause cc) {
        EmptyCatch analyzer = new EmptyCatch();
        List<AnalysisResult> results = new ArrayList<>();
        analyzer.visit(cc, results);
        return results;
    }

}
