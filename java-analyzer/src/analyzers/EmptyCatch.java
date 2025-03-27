package analyzers;

import com.github.javaparser.ast.stmt.CatchClause;
import model.AnalysisResult;
import model.RiskLevel;
import java.util.List;

/* Identify empty catch exception blocks
* Example violation:
* try {
*    // errorCode that may throw an exception
* } catch (Exception e) {
*   // empty catch block
* }
* */

public class EmptyCatch extends BaseAnalyzer{
    private static final String ERROR_CODE = "EMPTY_CATCH";
    private static final String ERROR_MESSAGE = "Empty catch block found";
    private static final String SUGGESTION = """
            Add proper handling of the exception:
            - Log the exception
            - Add a comment explaining why the exception is ignored
            - Refactor the code to handle the exception
            - Remove the empty catch block if the exception is not needed
            """;
    private static final RiskLevel RISK_LEVEL = RiskLevel.HIGH;

    @Override
    public void visit(CatchClause catchClause, List<AnalysisResult> results) {
        super.visit(catchClause, results);
        if (catchClause.getBody().getStatements().isEmpty()) {
            addAnalysisResult(results, ERROR_CODE, catchClause, ERROR_MESSAGE, SUGGESTION, RISK_LEVEL);
        }
    }
}
