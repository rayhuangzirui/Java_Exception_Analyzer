package analyzersTests;

import analyzers.AlwaysTriggeredCatch;
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

public class AlwaysTriggeredCatchTest {
    @Test
    public void detectedAlwaysTriggeredCatch() {
        String path = "../resources/sample-java/AlwaysTriggeredCatchExample1.java";
        try {
            String code = Files.readString(Path.of(path));
            System.out.println("Code: " + code);
            CompilationUnit cu = StaticJavaParser.parse(code);
            CatchClause cc = getFirstCatchClause(cu);
            System.out.println("Issue: " + cc);
            List<AnalysisResult> results = analyze(cu);

            assertEquals(1, results.size());
            assertEquals(AlwaysTriggeredCatch.ERROR_CODE, results.getFirst().errorCode());
            assertEquals(18, results.getFirst().startLine());
            assertEquals(AlwaysTriggeredCatch.ERROR_MESSAGE, results.getFirst().message());
            assertEquals(cc, results.getFirst().node());
            assertEquals(AlwaysTriggeredCatch.SUGGESTION, results.getFirst().suggestion());
            assertEquals("HIGH", results.getFirst().riskLevel().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void errorWithConditionShouldNotBeDetected1() {
        String path = "../resources/sample-java/AlwaysTriggeredCatchExample2.java";
        try {
            String code = Files.readString(Path.of(path));
            System.out.println("Code: " + code);
            CompilationUnit cu = StaticJavaParser.parse(code);
            List<AnalysisResult> results = analyze(cu);

            assertEquals(0, results.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void errorWithConditionShouldNotBeDetected2() {
        String path = "../resources/sample-java/AlwaysTriggeredCatchExample3.java";
        try {
            String code = Files.readString(Path.of(path));
            System.out.println("Code: " + code);
            CompilationUnit cu = StaticJavaParser.parse(code);
            List<AnalysisResult> results = analyze(cu);
            System.out.println("Issue: " + results);
            assertEquals(0, results.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CatchClause getFirstCatchClause(CompilationUnit cu) {

        Optional<CatchClause> catchClause = cu.findFirst(CatchClause.class);
        return catchClause.orElseThrow();
    }

    private List<AnalysisResult> analyze(CompilationUnit cu) {
        AlwaysTriggeredCatch analyzer = new AlwaysTriggeredCatch();
        List<AnalysisResult> results = new ArrayList<>();
        analyzer.analyze(cu, results);
        return results;
    }
}
