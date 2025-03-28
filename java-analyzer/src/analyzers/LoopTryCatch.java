package analyzers;

public class LoopTryCatch {
    // Preventing expensive exception-based control flow
    private static final String ERROR_CODE = "LOOP_TRY_CATCH";
    private static final String ERROR_MESSAGE = "Try-catch block inside a loop detected";
    private static final String SUGGESTION = """
            Consider refactoring your code to move the try-catch block outside the loop,
            or handle exceptions differently to avoid potential expenseve performance issues caused by 
            exception-based control flow inside loops.
            """;
    private static final RiskLevel RISK_LEVEL = RiskLevel.MEDIUM;

    @Override
    public void visit(TryStmt tryStmt, List<AnalysisResult> results) {
        super.visit(tryStmt, results);

        // check if this try statement is inside a loop
        if (isWithinLoop(tryStmt)) {
            addAnalysisResult(results, ERROR_CODE, tryStmt, ERROR_MESSAGE, SUGGESTION, RISK_LEVEL);
        }
    }

    /**
     * traverse up the parent chain of the given node to determine if it is in nested loop.
     *
     * @param node The node to start checking from
     * @return true if the node is inside a loop; false otherwise.
     */
    private boolean isWithinLoop(Node node) {
        Node current = node;
        while (current.getParentNode().isPresent()) {
            current = current.getParentNode().get();
            if (current instanceof ForStmt ||
                    current instanceof WhileStmt ||
                    current instanceof DoStmt ||
                    current instanceof ForEachStmt) {
                return true;
            }
        }
        return false;
    }
}
