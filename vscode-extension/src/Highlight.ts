import * as vscode from "vscode";
import { AnalysisResult, Code, RiskLevel } from "./interfaces/HoverInfo";
import { getConfig } from "./Extension";

let hoverProviderDisposable: vscode.Disposable | undefined;

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

export function applyUnderline(hoverInfo: AnalysisResult) {
  let decorations: vscode.DecorationOptions[] = [];
  const riskLevel = resultToRiskLevel(hoverInfo);
  const color = severityToColor(riskLevel);
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
  // Dispose of previous hover
  if (hoverProviderDisposable) {
    hoverProviderDisposable.dispose();
  }

  // Register a new hover provider
  hoverProviderDisposable = vscode.languages.registerHoverProvider("java", {
    provideHover(document, position, token) {
      const start = new vscode.Position(hoverInfo.startLine, hoverInfo.startChar);
      const end = new vscode.Position(hoverInfo.endLine, hoverInfo.endChar);
      const vscodeRange = new vscode.Range(start, end);
      if (vscodeRange.contains(position)) {
        const hoverMessage = new vscode.MarkdownString(`**${hoverInfo.message}**\n\n${hoverInfo.suggestion}`);
        return new vscode.Hover(hoverMessage, vscodeRange);
      }
    },
  });
}
