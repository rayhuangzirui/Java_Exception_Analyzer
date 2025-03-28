package analyzers;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import model.AnalysisResult;
import model.RiskLevel;

import java.util.*;

public class UnclosedResource extends BaseAnalyzer {
    private static final String ERROR_CODE = "UNCLOSED_RESOURCE";
    private static final String ERROR_MESSAGE = "Resource not properly closed";
    private static final String SUGGESTION = """
            Ensure opened resources are properly closed:
            - close resource in a finally block 
            """;
    private static final RiskLevel RISK_LEVEL = RiskLevel.HIGH;

    // set to store all resources that need to be closedd
    private static final Set<String> RESOURCECS = Set.of(
            "InputStream", "FileInputStream", "ByteArrayInputStream", "ObjectInputStream", "PipedInputStream", "BufferedInputStream", "DataInputStream",
            "OutputStream", "FileOutputStream", "ByteArrayOutputStream", "ObjectOutputStream", "PipedOutputStream", "BufferedOutputStream", "DataOutputStream",
            "Reader", "FileReader", "BufferedReader", "StringReader", "PipedReader", "CharArrayReader",
            "Writer", "FileWriter", "BufferedWriter", "PrintWriter", "StringWriter", "PipedWriter", "CharArrayWriter",
            "Socket", "ServerSocket", "DatagramSocket", "MulticastSocket", "HttpURLConnection", "HttpsURLConnection",
            "Connection", "Statement", "PreparedStatement", "ResultSet", "CallableStatement", "DataSource",
            "RandomAccessFile", "FileChannel", "ZipFile", "ZipInputStream", "ZipOutputStream", "GZIPInputStream", "GZIPOutputStream", "JarFile", "JarInputStream", "JarOutputStream",
            "SocketChannel", "ServerSocketChannel", "DatagramChannel", "Selector",
            "Stream", "IntStream", "LongStream", "DoubleStream",
            "Process", "ProcessBuilder.Redirect",
            "Cipher", "KeyStore", "Signature", "Mac",
            "Scanner", "PrintStream", "Clipboard", "ImageInputStream", "ImageOutputStream", "ExecutorService", "WatchService", "Context"
    );

    // during evaluation, add the resource to the map when it's opened
    // to track its open/close status
    private final Map<String, Boolean> closedResources = new HashMap<>();

    @Override
    public void visit(MethodDeclaration method, List<AnalysisResult> results) {
        closedResources.clear();
        super.visit(method, results);

        for (Map.Entry<String, Boolean> entry : closedResources.entrySet()) {
            if (!entry.getValue()) {
                addUnclosedResourceResult(method, results, entry.getKey());
            }
        }
    }

    private boolean isResource(String resourceType) {
        return RESOURCECS.contains(resourceType);
    }

    private void addUnclosedResourceResult(MethodDeclaration method, List<AnalysisResult> results, String resourceName) {
        String detailedMessage = ERROR_MESSAGE + ": " + resourceName;
        addAnalysisResult(results, ERROR_CODE, method, detailedMessage, SUGGESTION, RISK_LEVEL);
    }

    @Override
    public void visit(VariableDeclarationExpr declaration, List<AnalysisResult> results) {
        super.visit(declaration, results);

        for (VariableDeclarator variable : declaration.getVariables()) {
            if (variable.getInitializer().isPresent() && variable.getInitializer().get() instanceof ObjectCreationExpr) {
                ObjectCreationExpr creation = (ObjectCreationExpr) variable.getInitializer().get();
                if (isResource(creation.getType().getNameAsString())) {
                    // register resource as unclosed when first declared
                    closedResources.put(variable.getNameAsString(), false);
                }
            } else {
                String typeName = variable.getType().asString();
                if (isResource(typeName)) {
                    closedResources.put(variable.getNameAsString(), false);
                }
            }
        }
    }

    @Override
    public void visit(TryStmt tryStmt, List<AnalysisResult> results) {
        super.visit(tryStmt, results);

        // check try-with-resources
        tryStmt.getResources().forEach(resource -> {
            if (resource instanceof VariableDeclarationExpr) {
                VariableDeclarationExpr declaration = (VariableDeclarationExpr) resource;
                declaration.getVariables().forEach(variable -> {
                    // try-with-resources will automatically close resources
                    closedResources.put(variable.getNameAsString(), true);
                });
            }
        });

        // check finally block
        if (tryStmt.getFinallyBlock().isPresent()) {
            checkBlockForResourceClosing(tryStmt.getFinallyBlock().get());
        }
    }

    private void checkBlockForResourceClosing(BlockStmt block) {
        block.findAll(MethodCallExpr.class).forEach(methodCall -> {
            if (methodCall.getNameAsString().equals("close") && methodCall.getScope().isPresent()) {
                String resourceName = methodCall.getScope().get().toString();
                // mark as closed
                if (closedResources.containsKey(resourceName)) {
                    closedResources.put(resourceName, true);
                }
            }
        });
    }

    @Override
    public void visit(MethodCallExpr methodCall, List<AnalysisResult> results) {
        super.visit(methodCall, results);

        if (methodCall.getNameAsString().equals("new") && RESOURCECS.contains(methodCall.toString())) {
            // for cases like: fis = new FileInputStream("file.txt")
            if (methodCall.getParentNode().isPresent()) {
                String varName = methodCall.getParentNode().get().toString().split("=")[0].trim();
                closedResources.put(varName, false);
            }
        }

        if (methodCall.getNameAsString().equals("close") && methodCall.getScope().isPresent()) {
            Expression scope = methodCall.getScope().get();
            String resourceName = scope.toString();
            // close resource
            if (closedResources.containsKey(resourceName)) {
                closedResources.put(resourceName, true);
            }
        }
    }
}
