export enum Code {
    ALWAYS_TRIGGERED_CATCH="ALWAYS_TRIGGERED_CATCH",
    EMPTY_CATCH="EMPTY_CATCH",
    EXCEPTION_PROPAGATION="EXCEPTION_PROPAGATION",
    LOOP_TRY_CATCH="LOOP_TRY_CATCH",
    REDUNDANT_TRY_CATCH="REUNDANT_TRY_CATCH",
    SYSTEM_ERROR_IN_CATCH="SYSTEM_ERROR_IN_CATCH",
    UNCLOSED_RESOURCE="UNCLOSED_RESOURCE",
    UNDECLARED_EXCEPTION="UNDECLARED_EXCEPTION",
    UNHANDLED_EXCEPTION="UNHANDLED_EXCEPTION"
}

export enum RiskLevel {
    HIGH="HIGH", MEDIUM="MEDIUM", LOW="LOW"
}

export interface AnalysisResult {
    errorCode: Code;
    node: any;
    startLine: number;
    startChar: number;
    endLine: number;
    endChar: number;
    message: string;
    suggestion: string;
    riskLevel: RiskLevel;
}