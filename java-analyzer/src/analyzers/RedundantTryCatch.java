package analyzers;

import com.github.javaparser.ast.stmt.CatchClause;
import model.AnalysisResult;
import model.RiskLevel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedundantTryCatch extends BaseAnalyzer {
    // Redundant try-catch blocks (e.g. we can use Files.exists() to check file existence rather than catching FileNotFoundException)
    public static final String ERROR_CODE = "REDUNDANT_TRY_CATCH";
    public static final String ERROR_MESSAGE = "Redundant try-catch block for exception: ";
    public static final String SUGGESTION_PART1 = "Consider checking ";
    public static final String SUGGESTION_PART2 = " instead to avoid unnecessary try-catch blocks for ";
    public static final RiskLevel RISK_LEVEL = RiskLevel.LOW;

    private static final Set<String> REDUNDANT_EXCEPTIONS = Set.of(
            "FileNotFoundException", "NullPointerException", "IndexOutOfBoundsException",
            "ArithmeticException", "NoSuchElementException"
    );

    private static final Map<String, String> POSSIBLE_FIX = Map.of(
            "FileNotFoundException", "File.exists()",
            "NullPointerException", "Objects.nonNull(obj)",
            "IndexOutOfBoundsException", "list.size() > index",
            "ArithmeticException", "<denominator> != 0",
            "NoSuchElementException", "Scanner.hasNext()"
    );

    @Override
    public void visit(CatchClause catchClause, List<AnalysisResult> results) {
        super.visit(catchClause, results);
        String exceptionName = catchClause.getParameter().getType().asString();
        if (REDUNDANT_EXCEPTIONS.contains(exceptionName)) {
            String suggestion = SUGGESTION_PART1 + POSSIBLE_FIX.get(exceptionName) + SUGGESTION_PART2 + exceptionName;
            addAnalysisResult(results, ERROR_CODE, catchClause, ERROR_MESSAGE + exceptionName, suggestion, RISK_LEVEL);
        }
    }
}
