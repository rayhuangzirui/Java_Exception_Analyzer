export enum Severity {
    Error = 0,
    Warning = 1,
    Information = 2,
    Hint = 3,
}

export interface HoverInfo {
    ranges: {
        start: { line: number; character: number };
        end: { line: number; character: number };
    }[];
    severity: Severity;
    hoverMessage: string;
}