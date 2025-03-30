package analyzers;

import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import model.AnalysisResult;
import model.RiskLevel;
import java.util.List;

// Identify if a catch block is still printing to System error
// try{...}catch(Error e){System.err.print(e)}
// try{...}catch(Error e){printStackTrace(e)}

public class SystemErrorInCatch extends BaseAnalyzer {

    private static final String ERROR_CODE = "SYS_ERR_IN_CATCH";
    private static final String ERROR_MESSAGE = "Catch block uses System.err or printStackTrace for error handling";
    private static final String SUGGESTION = """
            Consider replacing System.err output or printStackTrace() with a proper logging framework,
            or handling the exception in a way that recovers or propagates the error appropriately.
            """;
    private static final RiskLevel RISK_LEVEL = RiskLevel.MEDIUM;

    @Override
    public void visit(CatchClause catchClause, List<AnalysisResult> results) {
        super.visit(catchClause, results);

        // check if any statement in the catch block prints to System.err or calls printStackTrace
        boolean usesSystemError = false;
        for (Statement stmt : catchClause.getBody().getStatements()) {
            String stmtStr = stmt.toString();
            if (stmtStr.contains("System.err") || stmtStr.contains("printStackTrace(")) {
                usesSystemError = true;
                break;
            }
        }

        if (usesSystemError) {
            addAnalysisResult(results, ERROR_CODE, catchClause, ERROR_MESSAGE, SUGGESTION, RISK_LEVEL);
        }
    }
}
