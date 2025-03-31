package analyzers;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.TryStmt;
import model.AnalysisResult;
import model.RiskLevel;

import java.util.*;

/* Suggest adding a try-catch block to methods that potentially generate errors if the user didn't include it
 * */

public class UnhandledException extends BaseAnalyzer {
    private static final String ERROR_CODE = "UNHANDLED_EXCEPTION";
    private static final String ERROR_MESSAGE = "Unhandled potential exception";
    private static final String SUGGESTION = """
            Consider to add a try-catch block, or:
            - declare the exception in the method signature
            - Use try-with-resources 
            """;
    private static final RiskLevel RISK_LEVEL = RiskLevel.MEDIUM;

    // methods that commonly throw exceptions
    private static final Set<String> EXCEPTION_PRONE_METHODS = Set.of(
            "read", "write", "close", "open", "connect", "load", "parse", "execute",
            "query", "get", "find", "search", "delete", "update", "insert", "create"
    );

    // classes that commonly throw exceptions
    private static final Set<String> EXCEPTION_PRONE_CLASSES = Set.of(
            "File", "FileReader", "FileWriter", "FileInputStream", "FileOutputStream",
            "Socket", "ServerSocket", "URL", "Connection", "Statement", "ResultSet",
            "Class", "XMLParser", "JSONParser", "HttpClient", "HttpURLConnection"
    );

    private final Set<MethodCallExpr> safeMethodCalls = new HashSet<>();

    @Override
    public void visit(MethodDeclaration method, List<AnalysisResult> results) {
        safeMethodCalls.clear();

        // skip if already declared in signature
        if (!method.getThrownExceptions().isEmpty()) {
            return;
        }

        super.visit(method, results);
    }

    @Override
    public void visit(TryStmt tryStmt, List<AnalysisResult> results) {
        // all calls within try block are safe
        tryStmt.getTryBlock().findAll(MethodCallExpr.class).forEach(methodCall ->
                safeMethodCalls.add(methodCall)
        );

        super.visit(tryStmt, results);
    }

    @Override
    public void visit(MethodCallExpr methodCall, List<AnalysisResult> results) {
        super.visit(methodCall, results);

        // skip if it's in a safe method call
        if (safeMethodCalls.contains(methodCall)) {
            return;
        }

        // skip if not risky
        if (!isPotentiallyThrowing(methodCall)) {
            return;
        }

        // check if it's within a try block
        boolean isWithinTryBlock = isWithinTryBlock(methodCall);
        if (!isWithinTryBlock) {
            String detailedMessage = ERROR_MESSAGE + ": " + methodCall.getNameAsString() + "()";
            addAnalysisResult(results, ERROR_CODE, methodCall, detailedMessage, SUGGESTION, RISK_LEVEL);
        }
    }

    private boolean isPotentiallyThrowing(MethodCallExpr methodCall) {
        // check method name
        String methodName = methodCall.getNameAsString();
        if (EXCEPTION_PRONE_METHODS.contains(methodName)) {
            return true;
        }

        // check if the method is on EXCEPTION_PRONE_CLASSES
        if (methodCall.getScope().isPresent()) {
            String scope = methodCall.getScope().get().toString();
            for (String className : EXCEPTION_PRONE_CLASSES) {
                if (scope.contains(className)) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean isWithinTryBlock(MethodCallExpr methodCall) {
        // check if the method has an ancestor that has a trystmt
        return methodCall.findAncestor(TryStmt.class).isPresent();
    }
}