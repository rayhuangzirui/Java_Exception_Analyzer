package analyzers;

import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import model.AnalysisResult;
import model.RiskLevel;
import com.github.javaparser.ast.Node;
import java.util.List;

public abstract class BaseAnalyzer extends VoidVisitorAdapter<List<AnalysisResult>> {
    protected int getLineOfCode(Node node) {
        return node.getRange().map(range -> range.begin.line).orElse(-1);
    }

    protected void addAnalysisResult(List<AnalysisResult> results, String errorCode, Node node, String message, String suggestion, RiskLevel riskLevel) {
        int startLine = getLineOfCode(node);
        int endLine = node.getRange().map(range -> range.end.line).orElse(-1); // -1 if not available (e.g., only one line or 0 lines)
        int startChar = node.getRange().map(range -> range.begin.column).orElse(-1);
        int endChar = node.getRange().map(range -> range.end.column).orElse(-1);
        results.add(new AnalysisResult(errorCode, node, startLine, endLine, startChar, endChar, message, suggestion, riskLevel));
    }
}
