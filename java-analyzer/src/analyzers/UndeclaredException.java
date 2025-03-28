package analyzers;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ReferenceType;
import model.AnalysisResult;
import model.RiskLevel;

import java.util.*;

public class UndeclaredException extends BaseAnalyzer {
    private static final String ERROR_CODE = "UNDECLARED_EXCEPTION";
    private static final String ERROR_MESSAGE = "Exception thrown is not declared in signature";
    private static final String SUGGESTION = """
            Consider to either:
            - add the exception to method sigature 
            - catch the exception in try-catch 
            """;
    private static final RiskLevel RISK_LEVEL = RiskLevel.MEDIUM;

    // Uncheked Exceptions don't need to bbe declared
    private static final Set<String> UNCHECKED_EXCEPTIONS = Set.of(
            "RuntimeException", "ArithmeticException", "ArrayStoreException", "ClassCastException",
            "IllegalArgumentException", "IllegalStateException", "IndexOutOfBoundsException",
            "NoSuchElementException", "NullPointerException", "UnsupportedOperationException",
            "Error", "AssertionError", "LinkageError", "VirtualMachineError"
    );

    private final Map<String, List<ThrowStmt>> methodThrows = new HashMap<>();

    @Override
    public void visit(MethodDeclaration method, List<AnalysisResult> results) {
        methodThrows.clear();
        methodThrows.put(method.getNameAsString(), new ArrayList<>());

        Set<String> declaredExceptions = new HashSet<>();
        for (ReferenceType exceptionType : method.getThrownExceptions()) {
            declaredExceptions.add(exceptionType.asString());
        }

        super.visit(method, results);

        for (ThrowStmt throwStmt : methodThrows.get(method.getNameAsString())) {
            String exceptionName = getExceptionName(throwStmt);

            // skip unchecked exception
            if (isUnchecked(exceptionName)) {
                continue;
            }

            // check signature
            if (!isDeclared(exceptionName, declaredExceptions)) {
                String detailedMessage = ERROR_MESSAGE + ": " + exceptionName;
                addAnalysisResult(results, ERROR_CODE, throwStmt, detailedMessage, SUGGESTION, RISK_LEVEL);
            }
        }
    }

    @Override
    public void visit(ThrowStmt throwStmt, List<AnalysisResult> results) {
        super.visit(throwStmt, results);

        // add throw statement to the method that throws it
        Optional<MethodDeclaration> methodDecl = throwStmt.findAncestor(MethodDeclaration.class);
        if (methodDecl.isPresent()) {
            String methodName = methodDecl.get().getNameAsString();
            methodThrows.get(methodName).add(throwStmt);
        }
    }

    private String getExceptionName(ThrowStmt throwStmt) {
        // get exception type
        String exprString = throwStmt.getExpression().toString();
        if (exprString.contains("new ")) {
            // e.g. throw new SomeException()
            return exprString.substring(exprString.indexOf("new ") + 4, exprString.indexOf("("));
        } else {
            // case: throw someException
            return extractExceptionType(exprString);
        }
    }

    private String extractExceptionType(String exprString) {
        // determine type, case: someException
        if (exprString.contains("Exception") || exprString.contains("Error")) {
            return exprString;
        }
        return "Exception"; // default if we can't determine
    }

    private boolean isUnchecked(String exceptionName) {
        // check if it's checked or not
        for (String unchecked : UNCHECKED_EXCEPTIONS) {
            if (exceptionName.endsWith(unchecked)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDeclared(String exceptionName, Set<String> declaredExceptions) {
        // check if it's decalred
        for (String declared : declaredExceptions) {
            if (exceptionName.endsWith(declared)) {
                return true;
            }
        }
        return false;
    }
}