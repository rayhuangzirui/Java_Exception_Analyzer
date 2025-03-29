package analyzersTests;

import analyzers.ExceptionPropagation;
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

public class ExceptionPropagationTest {
    @Test
    public void detectsFileNotFound() {
        String path = "../resources/sample-java/ExceptionPropagationExample.java";
        try {
            String code = Files.readString(Path.of(path));
            System.out.println("Code: " + code);
            CompilationUnit cu = StaticJavaParser.parse(code);
            CatchClause cc = getFirstCatchClause(cu);
            System.out.println("Issue: " + cc);
            List<AnalysisResult> results = analyze(cu);

            assertEquals(1, results.size());
            assertEquals(ExceptionPropagation.ERROR_CODE, results.getFirst().errorCode());
            assertEquals(5, results.getFirst().startLine());
            assertEquals("Number of functions that might encounter this error: 2", results.getFirst().message());
            assertEquals(cc, results.getFirst().node());
            assertEquals("Function names: T1, T2", results.getFirst().suggestion());
            assertEquals("LOW", results.getFirst().riskLevel().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CatchClause getFirstCatchClause(CompilationUnit cu) {
        Optional<CatchClause> catchClause = cu.findFirst(CatchClause.class);
        return catchClause.orElseThrow();
    }

    private List<AnalysisResult> analyze(CompilationUnit cu) {
        ExceptionPropagation analyzer = new ExceptionPropagation();
        List<AnalysisResult> results = new ArrayList<>();
        analyzer.visit(cu, results);
        return results;
    }
}
