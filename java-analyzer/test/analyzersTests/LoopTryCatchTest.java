package analyzersTests;

import analyzers.LoopTryCatch;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.TryStmt;
import model.AnalysisResult;
import model.RiskLevel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

package analyzersTests;

import analyzers.LoopTryCatch;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.TryStmt;
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

public class LoopTryCatchTest {

    @Test
    public void detectsTryCatchInLoop() {
        String path = "../resources/sample-java/LoopTryCatchExample.java";
        try {
            String code = Files.readString(Path.of(path));
            TryStmt tryStmt = getFirstTryStmt(code);
            List<AnalysisResult> results = analyze(tryStmt);

            // expect analysis result for a try catch block inside a loop.
            assertEquals(1, results.size());
            AnalysisResult result = results.get(0);
            assertEquals("LOOP_TRY_CATCH", result.code());
            assertEquals("Try-catch block inside a loop detected", result.message());
            assertEquals(RiskLevel.MEDIUM, result.riskLevel());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noDetectionWhenTryCatchNotInLoop() {
        String path = "../resources/sample-java/NoLoopTryCatchExample.java";
        try {
            String code = Files.readString(Path.of(path));
            TryStmt tryStmt = getFirstTryStmt(code);
            List<AnalysisResult> results = analyze(tryStmt);

            // expect no issues flagged because the try catch block is not inside a loop
            assertEquals(0, results.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TryStmt getFirstTryStmt(String code) {
        CompilationUnit cu = StaticJavaParser.parse(code);
        Optional<TryStmt> tryStmt = cu.findFirst(TryStmt.class);
        return tryStmt.orElseThrow();
    }

    private List<AnalysisResult> analyze(TryStmt tryStmt) {
        LoopTryCatch analyzer = new LoopTryCatch();
        List<AnalysisResult> results = new ArrayList<>();
        analyzer.visit(tryStmt, results);
        return results;
    }
}
