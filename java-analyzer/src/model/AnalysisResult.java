package model;
import com.github.javaparser.ast.Node;
import com.google.gson.Gson;

public record AnalysisResult(String errorCode, Node node, int startLine, int endLine, int startChar,
                             int endChar, String message, String suggestion, RiskLevel riskLevel) {
    private static final Gson gson = new Gson();

    public String toJson() {
        return gson.toJson(this);
    }

    public static class AnalysisResultBuilder {
        private String code;
        private int startLine;
        private int endLine;
        private int startChar;
        private int endChar;
        private Node node;
        private String message;
        private String suggestion;
        private RiskLevel riskLevel;

        public AnalysisResultBuilder setCode(String code) {
            this.code = code;
            return this;
        }

        public AnalysisResultBuilder setStartLine(int startLine) {
            this.startLine = startLine;
            return this;
        }

        public AnalysisResultBuilder setEndLine(int endLine) {
            this.endLine = endLine;
            return this;
        }

        public AnalysisResultBuilder setStartChar(int startChar) {
            this.startChar = startChar;
            return this;
        }

        public AnalysisResultBuilder setEndChar(int endChar) {
            this.endChar = endChar;
            return this;
        }

        public AnalysisResultBuilder setNode(Node node) {
            this.node = node;
            return this;
        }

        public AnalysisResultBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public AnalysisResultBuilder setSuggestion(String suggestion) {
            this.suggestion = suggestion;
            return this;
        }

        public AnalysisResultBuilder setRiskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public AnalysisResult build() {
            return new AnalysisResult(code, node, startLine, endLine, startChar, endChar, message, suggestion, riskLevel);
        }
    }
}
