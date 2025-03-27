package analyzersTests;

import analyzers.UndeclaredException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import model.AnalysisResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UndeclaredExceptionTest {
    @Test
    public void detectsUndeclaredException() {
        String code = """
            public class Example {
                public void exampleMethod() {
                    throw new IOException("example message");
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(1, results.size());
        assertEquals("UNDECLARED_EXCEPTION", results.get(0).errorCode());
        assertTrue(results.get(0).message().contains("Exception thrown is not declared in signature"));
    }

    @Test
    public void detectsMultipleUndeclaredException() {
        String code = """
            public class Example {
                public void exampleMethod() {
                    if (true) {
                        throw new IOException("example message");
                    } else {
                        throw new SQLException("Database error");
                    }
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(2, results.size());
        assertEquals("UNDECLARED_EXCEPTION", results.get(0).errorCode());
        assertEquals("UNDECLARED_EXCEPTION", results.get(1).errorCode());
    }

    @Test
    public void ignoresRuntimeExceptions() {
        String code = """
            public class Example {
                public void exampleMethod() {
                    throw new IllegalArgumentException("Invalid argument");
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(0, results.size());
    }

    @Test
    public void ignoresErrors() {
        String code = """
            public class Example {
                public void exampleMethod() {
                    throw new AssertionError("Assertion failed");
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(0, results.size());
    }

    @Test
    public void testProperlyDeclaredException() {
        String code = """
            public class Example {
                public void exampleMethod() throws IOException {
                    throw new IOException("File not found");
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(0, results.size());
    }

    @Test
    public void testParentExceptionDeclaration() {
        String code = """
            public class Example {
                public void exampleMethod() throws Exception {
                    throw new IOException("File not found");
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(0, results.size());
    }

    @Test
    public void detectsUndeclaredExceptionVariable() {
        String code = """
            public class Example {
                public void exampleMethod() {
                    IOException ex = new IOException("File not found");
                    throw ex;
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(1, results.size());
        assertEquals("UNDECLARED_EXCEPTION", results.get(0).errorCode());
    }

    @Test
    public void testDeclaredExceptionVariable() {
        String code = """
            public class Example {
                public void exampleMethod() throws Exception {
                    IOException ex = new IOException("File not found");
                    throw ex;
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(0, results.size());
    }

    private MethodDeclaration getFirstMethod(String code) {
        CompilationUnit cu = StaticJavaParser.parse(code);
        Optional<MethodDeclaration> method = cu.findFirst(MethodDeclaration.class);
        return method.orElseThrow();
    }

    private List<AnalysisResult> analyze(MethodDeclaration method) {
        UndeclaredException analyzer = new UndeclaredException();
        List<AnalysisResult> results = new ArrayList<>();
        analyzer.visit(method, results);
        return results;
    }
}