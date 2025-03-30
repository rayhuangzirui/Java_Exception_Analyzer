package analyzersTests;

import analyzers.SystemErrorInCatch;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.CatchClause;
import model.AnalysisResult;
import model.RiskLevel;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SystemErrorInCatchTest {

    @Test
    public void detectsSystemErrorInCatch() {
        String path = "../resources/sample-java/SystemErrorInCatchExample.java";
        try {
            String code = Files.readString(Path.of(path));
            CatchClause cc = getFirstCatchClause(code);
            List<AnalysisResult> results = analyze(cc);

            // expect analysis result flagged.
            assertEquals(1, results.size());
            AnalysisResult result = results.get(0);
            assertEquals("SYS_ERR_IN_CATCH", result.errorCode());
            assertEquals("Catch block uses System.err or printStackTrace for error handling", result.message());
            assertEquals("Consider replacing System.err output or printStackTrace() with a proper logging framework,\n" +
                    "or handling the exception in a way that recovers or propagates the error appropriately.\n", result.suggestion());
            assertEquals(RiskLevel.MEDIUM, result.riskLevel());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noDetectionWhenNoSystemErrorUsage() {
        String path = "../resources/sample-java/NoSystemErrorInCatchExample.java";
        try {
            String code = Files.readString(Path.of(path));
            CatchClause cc = getFirstCatchClause(code);
            List<AnalysisResult> results = analyze(cc);

            // we check that none of the results have the "SYS_ERR_IN_CATCH" error code.
            boolean systemErrorFlagPresent = results.stream()
                    .anyMatch(result -> result.errorCode().equals("SYS_ERR_IN_CATCH"));
            assertFalse("SystemErrorInCatch flag should not be present", systemErrorFlagPresent);
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
        SystemErrorInCatch analyzer = new SystemErrorInCatch();
        List<AnalysisResult> results = new ArrayList<>();
        analyzer.visit(cc, results);
        return results;
    }
}
