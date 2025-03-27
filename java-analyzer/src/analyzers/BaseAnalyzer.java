package analyzers;

import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import model.AnalysisResult;
import model.RiskLevel;
import com.github.javaparser.ast.Node;
import java.util.List;

public abstract class BaseAnalyzer extends VoidVisitorAdapter<List<AnalysisResult>> {
    protected int getLineOfCode(Node node) {
        return node.getRange().map(range -> range.begin.line).orElse(-1);
    }

    protected void addAnalysisResult(List<AnalysisResult> results, String code, Node node, String message, String suggestion, RiskLevel riskLevel) {
        int line = getLineOfCode(node);
        results.add(new AnalysisResult(code, node, line, message, suggestion, riskLevel));
    }

    @Override
    public void visit(TryStmt tryStmt, List<AnalysisResult> results) {
        // Visit try statement with try block, catch clauses and finally block
    }

    @Override
    public void visit(ThrowStmt throwStmt, List<AnalysisResult> results) {}
}
