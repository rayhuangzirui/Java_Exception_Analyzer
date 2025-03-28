package analyzersTests;

import analyzers.UnhandledException;
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

public class UnhandledExceptionTest {
    @Test
    public void detectsUnhandledFileOperation() {
        String code = """
            public class Example {
                public void exampleMethod() {
                    FileReader reader = new FileReader("test.txt");
                    int data = reader.read();
                    reader.close();
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(2, results.size());
        assertEquals("UNHANDLED_EXCEPTION", results.get(0).errorCode());
        assertTrue(results.get(0).message().contains("Unhandled potential exception"));
    }

    @Test
    public void detectsUnhandledDatabaseOperation() {
        String code = """
            public class Example {
                public void exampleMethod() {
                    Connection conn = DriverManager.getConnection("localhost:3000/test");
                    conn.close();
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(1, results.size());
        assertEquals("UNHANDLED_EXCEPTION", results.get(0).errorCode());
    }

    @Test
    public void acceptsHandledExceptionInTryCatch() {
        String code = """
            public class Example {
                public void exampleMethod() {
                    try {
                        FileReader reader = new FileReader("test.txt");
                        int data = reader.read();
                        reader.close();
                    } catch (IOException e) {
                        //
                    }
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(0, results.size());
    }

    @Test
    public void acceptsMethodWithThrowsDeclaration() {
        String code = """
            public class Example {
                public void exampleMethod() throws IOException {
                    FileReader reader = new FileReader("test.txt");
                    int data = reader.read();
                    reader.close();
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(0, results.size());
    }

    @Test
    public void detectsPartiallyHandledExceptions() {
        String code = """
            public class Example {
                public void exampleMethod() {
                    try {
                        FileReader reader = new FileReader("test.txt");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                    // outside try-catch 
                    FileWriter writer = new FileWriter("output.txt");
                    writer.write("Test data");
                    writer.close();
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(2, results.size());
        assertEquals("UNHANDLED_EXCEPTION", results.get(0).errorCode());
    }

    @Test
    public void ignoresCommonSafeMethods() {
        String code = """
            public class Example {
                public void exampleMethod() {
                    String text = "Hello World";
                    text.length();
                    text.charAt(0);
                    text.substring(0, 5);
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
        UnhandledException analyzer = new UnhandledException();
        List<AnalysisResult> results = new ArrayList<>();
        analyzer.visit(method, results);
        return results;
    }
}