package analyzers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ThrowStmt;
import model.AnalysisResult;
import model.RiskLevel;

import java.util.*;

public class ExceptionPropagation extends BaseAnalyzer {
    // Build a call graph, and check how many functions might encounter this error
    public static final String ERROR_CODE = "EXCEPTION_PROPAGATION";
    public static final String ERROR_MESSAGE_PART1 = "Number of functions that might encounter this error: ";
    public static final String SUGGESTION_PART1 = "Please consider catching the error in following functions: ";
    public static final RiskLevel RISK_LEVEL = RiskLevel.LOW;

    private final Map<String, Set<String>> methodCallGraph = new HashMap<>();
    private Boolean CallGraphBuilt = false;



    @Override
    public void analyze(CompilationUnit cu, List<AnalysisResult> results) {
        // Build the call graph first
        if (!CallGraphBuilt) {
            CallGraphBuilder callGraphBuilder = new CallGraphBuilder();
            cu.accept(callGraphBuilder, results);
            CallGraphBuilt = true;
        }

        cu.accept(this, results);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void visit(ThrowStmt throwStmt, List<AnalysisResult> results) {
        super.visit(throwStmt, results);

        Optional<MethodDeclaration> surroundingMethodOpt = throwStmt.findAncestor(MethodDeclaration.class);

        if (surroundingMethodOpt.isPresent()) {
            String throwingMethodName = surroundingMethodOpt.get().getNameAsString();

            Set<String> affectedMethods = findAllCallers(throwingMethodName);

            int affectedCount = affectedMethods.size();

            String message = ERROR_MESSAGE_PART1 + affectedCount;
            String suggestion = SUGGESTION_PART1 + String.join(", ", affectedMethods);

            addAnalysisResult(results, ERROR_CODE, throwStmt, message, suggestion, RISK_LEVEL);
        }
    }

    private Set<String> findAllCallers(String throwingMethodName) {
        Set<String> callingMethods = new HashSet<>();
        Queue<String> directCallerQueue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        directCallerQueue.offer(throwingMethodName);
        visited.add(throwingMethodName);

        while (!directCallerQueue.isEmpty()) {
            String callee = directCallerQueue.poll();
            for (Map.Entry<String, Set<String>> entry : methodCallGraph.entrySet()) {
                String caller = entry.getKey();
                Set<String> callees = entry.getValue();
                if (callees.contains(callee) && !visited.contains(caller)) {
                    callingMethods.add(caller);
                    visited.add(caller);
                    directCallerQueue.offer(caller);
                }
            }
        }

        return callingMethods;
    }



    private class CallGraphBuilder extends BaseAnalyzer {
        @Override
        public void visit(MethodDeclaration method, List<AnalysisResult> results) {
            String methodName = method.getNameAsString();
            methodCallGraph.putIfAbsent(methodName, new HashSet<>());
            method.findAll(MethodCallExpr.class).forEach(call -> methodCallGraph.get(methodName).add(call.getNameAsString()));

            super.visit(method, results);
        }

    }

}
