package analyzersTests;

import analyzers.UnclosedResource;
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

public class UnclosedResourceTest {
    @Test
    public void detectsUnclosedResource() {
        String code = """
            public class Example {
                public void exampleMethod() throws Exception {
                    FileInputStream fis = new FileInputStream("file.txt");
                    try {
                        int data = fis.read();
                    } catch (Exception e) {
                        //
                    }
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(1, results.size());
        assertEquals("UNCLOSED_RESOURCE", results.get(0).errorCode());
        assertTrue(results.get(0).message().contains("Resource not properly closed"));
    }

    @Test
    public void testProperlyClosedResource() {
        String code = """
            public class Example {
                public void goodMethod() throws Exception {
                    try (FileInputStream fis = new FileInputStream("file.txt")) {
                        int data = fis.read();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(0, results.size());
    }

    @Test
    public void testManuallyClosedResource() {
        String code = """
            public class ManuallyClosedResourceExample {
                public void goodMethod() throws Exception {
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream("file.txt");
                        int data = fis.read();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (fis != null) {
                            fis.close();
                        }
                    }
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(0, results.size());
    }

    @Test
    public void detectNotManuallyClosedResource() {
        String code = """
            public class ManuallyClosedResourceExample {
                public void goodMethod() throws Exception {
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream("file.txt");
                        int data = fis.read();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (fis != null) {
                        }
                    }
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(1, results.size());
        assertEquals("UNCLOSED_RESOURCE", results.get(0).errorCode());
        assertTrue(results.get(0).message().contains("Resource not properly closed"));
    }

    @Test
    public void detectNotManuallyOneClosedResource() {
        String code = """
            public class ManuallyClosedResourceExample {
                public void goodMethod() throws Exception {
                    FileInputStream fis = null;
                    FileInputStream fis2 = null;
                    try {
                        fis = new FileInputStream("file.txt");
                        fis2 = new FileInputStream("file.txt");
                        int data = fis.read();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (fis != null) {
                            fis.close();
                        }
                    }
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(1, results.size());
        assertEquals("UNCLOSED_RESOURCE", results.get(0).errorCode());
        assertTrue(results.get(0).message().contains("Resource not properly closed"));
    }

    @Test
    public void detectNotManuallyTwoClosedResource() {
        String code = """
            public class ManuallyClosedResourceExample {
                public void goodMethod() throws Exception {
                    FileInputStream fis = null;
                    FileInputStream fis2 = null;
                    try {
                        fis = new FileInputStream("file.txt");
                        fis2 = new FileInputStream("file.txt");
                        int data = fis.read();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (fis != null) {
                        }
                    }
                }
            }
            """;

        MethodDeclaration method = getFirstMethod(code);
        List<AnalysisResult> results = analyze(method);

        assertEquals(2, results.size());
        assertEquals("UNCLOSED_RESOURCE", results.get(0).errorCode());
        assertTrue(results.get(0).message().contains("Resource not properly closed"));
        assertEquals("UNCLOSED_RESOURCE", results.get(1).errorCode());
        assertTrue(results.get(1).message().contains("Resource not properly closed"));
    }

    private MethodDeclaration getFirstMethod(String code) {
        CompilationUnit cu = StaticJavaParser.parse(code);
        Optional<MethodDeclaration> method = cu.findFirst(MethodDeclaration.class);
        return method.orElseThrow();
    }

    private List<AnalysisResult> analyze(MethodDeclaration method) {
        UnclosedResource analyzer = new UnclosedResource();
        List<AnalysisResult> results = new ArrayList<>();
        analyzer.visit(method, results);
        return results;
    }
}