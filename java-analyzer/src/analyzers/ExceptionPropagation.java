package analyzers;

import model.RiskLevel;

public class ExceptionPropagation extends BaseAnalyzer {
    // Build a call graph, and check how many functions might encounter this error
    // Redundant try-catch blocks (e.g. we can use Files.exists() to check file existence rather than catching FileNotFoundException)
    public static final String ERROR_CODE = "EXCEPTION_PROPAGATION";
    public static final String ERROR_MESSAGE = "Number of functions that might encounter this error: ";
    public static final RiskLevel RISK_LEVEL = RiskLevel.LOW;
}
