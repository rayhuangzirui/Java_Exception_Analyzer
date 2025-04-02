import * as vscode from "vscode";
import { AnalysisResult, Code, RiskLevel } from "./interfaces/HoverInfo";
import { getConfig } from "./Extension";

let hoverProvidersDisposable: vscode.Disposable[] = [];
let underlineDecorations: vscode.TextEditorDecorationType[] = [];

function resultToRiskLevel(result: AnalysisResult) {
  const config = getConfig();
  const severityOverride = config?.["risk-levels"]?.[result.errorCode];
  if (severityOverride) {
    return severityOverride;
  }
  return result.riskLevel;
}

function severityToColor(riskLevel: RiskLevel): string {
  const config = getConfig();
  const colorOverride = config?.["risk-colors"]?.[riskLevel];
  if (colorOverride) {
    return colorOverride;
  }
  switch (riskLevel) {
    case RiskLevel.HIGH:
      return "red";
    case RiskLevel.MEDIUM:
      return "yellow";
    case RiskLevel.LOW:
      return "#5b8beb";
  }
}

export function applyUnderlines(results: AnalysisResult[]) {
  for (const decoration of underlineDecorations) {
    decoration.dispose();
  }
  underlineDecorations = [];

  for (const result of results) {
    applyUnderline(result);
  }
}

function applyUnderline(result: AnalysisResult) {
  let decorations: vscode.DecorationOptions[] = [];
  const riskLevel = resultToRiskLevel(result);
  const color = severityToColor(riskLevel);
  const underlineDecoration = vscode.window.createTextEditorDecorationType({
    textDecoration: `underline wavy ${color}`,
  });
  underlineDecorations.push(underlineDecoration);

  const start = new vscode.Position(result.startLine - 1, result.startChar - 1);
  const end = new vscode.Position(result.endLine - 1, result.endChar - 1);
  const decoration = { range: new vscode.Range(start, end) };
  decorations.push(decoration);

  const editor = vscode.window.activeTextEditor;
  if (editor) {
    editor.setDecorations(underlineDecoration, decorations);
  }
}

export function applyHovers(results: AnalysisResult[]) {
  for (const provider of hoverProvidersDisposable) {
    provider.dispose();
  }
  hoverProvidersDisposable = [];

  for (const result of results) {
    const hoverProvider = makeHoverDisposable(result);
    hoverProvidersDisposable.push(hoverProvider);
  }
}

function makeHoverDisposable(result: AnalysisResult) {
  // Register a new hover provider
  return vscode.languages.registerHoverProvider("java", {
    provideHover(document, position, token) {
      const start = new vscode.Position(result.startLine - 1, result.startChar - 1);
      const end = new vscode.Position(result.endLine - 1, result.endChar - 1);
      const vscodeRange = new vscode.Range(start, end);
      if (vscodeRange.contains(position)) {
        const hoverMessage = new vscode.MarkdownString(`**${result.message}**\n\n${result.suggestion}`);
        return new vscode.Hover(hoverMessage, vscodeRange);
      }
    },
  });
}
