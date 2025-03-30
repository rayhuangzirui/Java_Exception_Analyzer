import * as vscode from "vscode";
import { AnalysisResult, RiskLevel } from "./interfaces/HoverInfo";

function severityToColor(riskLevel: RiskLevel): string {
  switch (riskLevel) {
    case RiskLevel.HIGH:
      return "red";
    case RiskLevel.MEDIUM:
      return "yellow";
    case RiskLevel.LOW:
      return "#5b8beb";
  }
}

export function applyUnderline(hoverInfo: AnalysisResult) {
  let decorations: vscode.DecorationOptions[] = [];
  const color = severityToColor(hoverInfo.riskLevel);
  const underlineDecoration = vscode.window.createTextEditorDecorationType({
    textDecoration: `underline wavy ${color}`,
  });

  const start = new vscode.Position(hoverInfo.startLine, hoverInfo.startChar);
  const end = new vscode.Position(hoverInfo.endLine, hoverInfo.endChar);
  const decoration = { range: new vscode.Range(start, end) };
  decorations.push(decoration);

  const editor = vscode.window.activeTextEditor;
  if (editor) {
    editor.setDecorations(underlineDecoration, decorations);
  }
}

export function applyHover(hoverInfo: AnalysisResult) {
  vscode.languages.registerHoverProvider("java", {
    provideHover(document, position, token) {
      const start = new vscode.Position(hoverInfo.startLine, hoverInfo.startChar);
      const end = new vscode.Position(hoverInfo.endLine, hoverInfo.endChar);
      const vscodeRange = new vscode.Range(start, end);
      if (vscodeRange.contains(position)) {
        return new vscode.Hover(hoverInfo.message, vscodeRange);
      }
    },
  });
}
