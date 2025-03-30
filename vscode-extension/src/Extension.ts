// This is the entry point for extension logic

import * as vscode from "vscode";
import { ExtensionContext } from "vscode";
import { applyHover, applyUnderline } from "./Highlight";
import { AnalysisResult } from "./interfaces/HoverInfo";
import { parseAnalysisResults } from "./interfaces/ParseAnalysisResult";
import path = require("path");

export function activate(context: ExtensionContext) {
  registerHello(context);

  const filePath = path.join(context.extensionPath, "..", "resources", "analysis-output", "output.json");
  const analysisResults = parseAnalysisResults(filePath);

  registerUnderline(context, analysisResults);
}

export function registerHello(context: ExtensionContext) {
  let disposable = vscode.commands.registerCommand("vscode-extension.sayHello", () => {
    vscode.window.showInformationMessage("Is this compiling? 2");
  });
  context.subscriptions.push(disposable);
}

export function registerUnderline(context: ExtensionContext, analysisResults: AnalysisResult[]) {
  let disposable = vscode.commands.registerCommand("vscode-extension.underlineText", () => {
    for (const analysisResult of analysisResults) {
      applyUnderline(analysisResult);
      applyHover(analysisResult);
    }
  });

  context.subscriptions.push(disposable);
}

export function deactivate() {}
