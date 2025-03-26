package model;
import com.google.gson.Gson;

public record AnalysisResult(String code, int line, String message, String suggestion, RiskLevel riskLevel) {
    private static final Gson gson = new Gson();

    public String toJson() {
        return gson.toJson(this);
    }

    public static class AnalysisResultBuilder {
        private String code;
        private int line;
        private String message;
        private String suggestion;
        private RiskLevel riskLevel;

        public AnalysisResultBuilder setCode(String code) {
            this.code = code;
            return this;
        }

        public AnalysisResultBuilder setLine(int line) {
            this.line = line;
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
            return new AnalysisResult(code, line, message, suggestion, riskLevel);
        }
    }
}
