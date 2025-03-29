package analyzersTests;

import analyzers.RedundantTryCatch;
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

public class RedundantCatchTest {
    @Test
    public void detectsFileNotFound() {
        // Get errorCode from resources/sample-java/EmptyCatchExample.java by replacing the path
        String path = "../resources/sample-java/RedundantCatchExample.java";
        // Get the errorCode from the file
        try {
            String code = Files.readString(Path.of(path));
            System.out.println("Code: " + code);
            CatchClause cc = getFirstCatchClause(code);
            System.out.println("Issue: " + cc);
            List<AnalysisResult> results = analyze(cc);

            assertEquals(1, results.size());
            assertEquals(RedundantTryCatch.ERROR_CODE, results.getFirst().errorCode());
            assertEquals(12, results.getFirst().startLine());
            assertEquals("Redundant try-catch block for exception: FileNotFoundException", results.getFirst().message());
            assertEquals(cc, results.getFirst().node());
            assertEquals("Consider checking File.exists() instead to avoid unnecessary try-catch blocks for FileNotFoundException", results.getFirst().suggestion());
            assertEquals("LOW", results.getFirst().riskLevel().toString());
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
        RedundantTryCatch analyzer = new RedundantTryCatch();
        List<AnalysisResult> results = new ArrayList<>();
        analyzer.visit(cc, results);
        return results;
    }
}
