// This is the entry point for extension logic

import * as vscode from "vscode";
import { ExtensionContext } from "vscode";
import { applyHover, applyUnderline } from "./Highlight";
import { AnalysisResult } from "./interfaces/HoverInfo";
import { parseAnalysisResults, runJavaAnalyzer } from "./interfaces/ParseAnalysisResult";
import * as path from "path";

export function activate(context: ExtensionContext) {
  registerHello(context);
  registerAnalyzeJava(context);
}

export function registerHello(context: ExtensionContext) {
  let disposable = vscode.commands.registerCommand("vscode-extension.sayHello", () => {
    vscode.window.showInformationMessage("Is this compiling? 2");
  });
  context.subscriptions.push(disposable);
}

export function registerAnalyzeJava(context: ExtensionContext) {
  let disposable = vscode.commands.registerCommand("vscode-extension.analyzeJava", async () => {
    // 1. Get current file
    const editor = vscode.window.activeTextEditor;
    if (!editor) {
      vscode.window.showInformationMessage("No active editor found.");
      return;
    }
    const filePath = editor.document.uri.fsPath;
    if (!filePath.endsWith('.java')) {
        vscode.window.showErrorMessage('Please open a Java file.');
        return;
    }

    // 2. Run the Java analyzer
    const outputJsonPath = path.join(context.extensionPath, "..", "resources", "analysis-output", "output.json");
    const analysisResults = await runJavaAnalyzer(filePath, outputJsonPath, context);

    // 3. Apply underlining and hover information
    for (const analysisResult of analysisResults) {
      applyUnderline(analysisResult);
      applyHover(analysisResult);
    }
    vscode.window.showInformationMessage("Java analysis completed.");
  });

  context.subscriptions.push(disposable);
}

export function deactivate() {}
