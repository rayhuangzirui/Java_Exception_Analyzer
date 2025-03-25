import { HoverInfo, Severity } from "../interfaces/HoverInfo";

export const mockHoverInfo: HoverInfo = {
    ranges: [
        {
            start: { line: 0, character: 0 },
            end: { line: 0, character: 10 },
        },
    ],
    severity: Severity.Hint,
    hoverMessage: "This is a mock hover message",
}