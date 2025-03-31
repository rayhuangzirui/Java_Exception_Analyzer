package analyzers;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import model.AnalysisResult;
import model.RiskLevel;

import java.util.*;

public class AlwaysTriggeredCatch extends BaseAnalyzer {
    public static final String ERROR_CODE = "ALWAYS_TRIGGERED_CATCH";
    public static final String ERROR_MESSAGE = "This try-catch block always triggers because the called method unconditionally throws an exception.";
    public static final String SUGGESTION = "Refactor the called method to prevent always throwing exceptions or handle the exception at the source.";
    public static final RiskLevel RISK_LEVEL = RiskLevel.HIGH;

    private final Set<String> alwaysThrowingMethods = new HashSet<>();
    private final Map<String, List<String>> methodCallGraph = new HashMap<>();

    @Override
    public void visit(MethodDeclaration method, List<AnalysisResult> results) {
        super.visit(method, results);
        String methodName = method.getNameAsString();

        // Track method calls inside this method. If there is an unconditional method call, add it to the graph.
        methodCallGraph.putIfAbsent(methodName, new ArrayList<>());
        if (hasUnconditionalMethodCall(method)) {
            method.findAll(MethodCallExpr.class).forEach(call -> methodCallGraph.get(methodName).add(call.getNameAsString()));
        }

        // Check if the method has any unconditional throw statements.
        if (!method.findAll(ThrowStmt.class).isEmpty() && hasUnconditionalThrow(method)) {
            alwaysThrowingMethods.add(methodName);
        }
    }

    @Override
    public void visit(TryStmt tryStmt, List<AnalysisResult> results) {
        super.visit(tryStmt, results);

        for (CatchClause catchClause : tryStmt.getCatchClauses()) {
            for (MethodCallExpr callExpr : tryStmt.findAll(MethodCallExpr.class)) {
                if (isAlwaysThrowing(callExpr.getNameAsString())) {
                    addAnalysisResult(results, ERROR_CODE, catchClause, ERROR_MESSAGE, SUGGESTION, RISK_LEVEL);
                    return;
                }
            }
        }
    }

    private boolean hasUnconditionalThrow(MethodDeclaration method) {
        List<ThrowStmt> throwStatements = method.findAll(ThrowStmt.class);
        if (throwStatements.isEmpty()) {
            return false;
        }

        boolean hasReturnOrOtherControlFlow = !method.findAll(ReturnStmt.class).isEmpty() ||
                !method.findAll(BreakStmt.class).isEmpty() ||
                !method.findAll(ContinueStmt.class).isEmpty();

        return !hasReturnOrOtherControlFlow;
    }

    private boolean hasUnconditionalMethodCall(MethodDeclaration method) {
        List<MethodCallExpr> methodCallStatements = method.findAll(MethodCallExpr.class);
        if (methodCallStatements.isEmpty()) {
            return false;
        }

        boolean hasReturnOrOtherControlFlow = !method.findAll(ReturnStmt.class).isEmpty() ||
                !method.findAll(BreakStmt.class).isEmpty() ||
                !method.findAll(ContinueStmt.class).isEmpty();

        return !hasReturnOrOtherControlFlow;
    }


    private boolean isAlwaysThrowing(String methodName) {
        if (alwaysThrowingMethods.contains(methodName)) {
            return true;
        }


        if (methodCallGraph.containsKey(methodName)) {

            if (methodCallGraph.get(methodName).isEmpty()) {
                return false;
            }

            return methodCallGraph.get(methodName).stream().allMatch(this::isAlwaysThrowing);
        }
        return false;
    }
}