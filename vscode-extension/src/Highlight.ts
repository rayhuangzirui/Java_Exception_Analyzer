import * as vscode from "vscode";
import { HoverInfo, Severity } from "./interfaces/HoverInfo";

function severityToColor(severity: Severity): string {
  switch (severity) {
    case Severity.Error:
      return "red";
    case Severity.Warning:
      return "yellow";
    case Severity.Information:
      return "grey";
    case Severity.Hint:
      return "#5b8beb";
  }

}

export function applyUnderline(hoverInfo: HoverInfo) {
  let decorations: vscode.DecorationOptions[] = [];
  const color = severityToColor(hoverInfo.severity)
  const underlineDecoration = vscode.window.createTextEditorDecorationType({
    textDecoration: `underline wavy ${color}`,
  });

  hoverInfo.ranges.forEach((range) => {
    const start = new vscode.Position(range.start.line, range.start.character);
    const end = new vscode.Position(range.end.line, range.end.character);
    const decoration = { range: new vscode.Range(start, end) };
    decorations.push(decoration);
  });

  const editor = vscode.window.activeTextEditor;
  if (editor) {
    editor.setDecorations(underlineDecoration, decorations);
  }
}

export function applyHover(hoverInfo: HoverInfo) {
  vscode.languages.registerHoverProvider('java', {
    provideHover(document, position, token) {
        for (const range of hoverInfo.ranges) {
            const start = new vscode.Position(range.start.line, range.start.character);
            const end = new vscode.Position(range.end.line, range.end.character);
            const vscodeRange = new vscode.Range(start, end);

            if (vscodeRange.contains(position)) {
                return new vscode.Hover(hoverInfo.hoverMessage);
            }
        }
    }
});
}
