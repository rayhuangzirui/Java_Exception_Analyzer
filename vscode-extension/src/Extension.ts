// This is the entry point for extension logic

import * as path from "path";
import * as vscode from "vscode";
import { ExtensionContext } from "vscode";
import { applyHovers, applyUnderline } from "./Highlight";
import { runJavaAnalyzer } from "./interfaces/ParseAnalysisResult";
import * as fs from "fs";

let globalContext: ExtensionContext;

export function activate(context: ExtensionContext) {
  globalContext = context;
  getConfig();
  registerHello(context);
  registerOpenConfigFile(context);
  registerResetConfig(context);
  registerAnalyzeJava(context);
}

function getConfigPath() {
  const configPath = path.join(globalContext.globalStorageUri.fsPath, "config.json");
  return configPath;
}

function getDefaultConfigPath() {
  const defaultConfigPath = path.join(globalContext.extensionPath, "src", "default-config.json");
  return defaultConfigPath;
}

function initConfig() {
  const configPath = getConfigPath();
  const defaultConfigPath = getDefaultConfigPath();

  // Create config directoy it it idoesn't exist
  if (!fs.existsSync(globalContext.globalStorageUri.fsPath)) {
    fs.mkdirSync(globalContext.globalStorageUri.fsPath, { recursive: true });
  }

  // Create the config file if it doesn’t exist
  if (!fs.existsSync(configPath)) {
    fs.copyFileSync(defaultConfigPath, configPath);
  }
}

export function getConfig() {
  const configPath = getConfigPath();
  const defaultConfigPath = getDefaultConfigPath();
  try {
    initConfig();
    const rawConfig = fs.readFileSync(configPath, "utf8");
    const config = JSON.parse(rawConfig);
    return config;
  } catch (error) {
    vscode.window.showErrorMessage("Error parsing config.json. Using default values");
    const rawConfig = fs.readFileSync(defaultConfigPath, "utf8");
    const config = JSON.parse(rawConfig);
    return config;
  }
}

export async function registerOpenConfigFile(context: ExtensionContext) {
  const configPath = getConfigPath();
  const defaultConfigPath = getDefaultConfigPath();
  let disposable = vscode.commands.registerCommand("vscode-extension.configJavaAnalyzer", async () => {
    initConfig();
    const document = await vscode.workspace.openTextDocument(configPath);
    vscode.window.showTextDocument(document);
  });
  context.subscriptions.push(disposable);
}

export async function registerResetConfig(context: ExtensionContext) {
  const configPath = path.join(context.globalStorageUri.fsPath, "config.json");
  const defaultConfigPath = path.join(context.extensionPath, "src", "default-config.json");
  let disposable = vscode.commands.registerCommand("vscode-extension.configReset", async () => {
    initConfig();
    fs.copyFileSync(defaultConfigPath, configPath);
  });
  context.subscriptions.push(disposable);
}

export function registerHello(context: ExtensionContext) {
  let disposable = vscode.commands.registerCommand("vscode-extension.sayHello", () => {
    vscode.window.showInformationMessage("Hello World!");
  });
  context.subscriptions.push(disposable);
}

async function analyzeJavaFile() {
  // Get current file
  const editor = vscode.window.activeTextEditor;
  if (!editor) {
    vscode.window.showInformationMessage("No active editor found.");
    return;
  }
  const filePath = editor.document.uri.fsPath;
  if (!filePath.endsWith(".java")) {
    vscode.window.showErrorMessage("Please open a Java file.");
    return;
  }
  if (!filePath.endsWith(".java")) {
    vscode.window.showErrorMessage("Please open a Java file.");
    return;
  }

  // Run the Java analyzer
  const outputJsonPath = path.join(globalContext.extensionPath, "..", "resources", "analysis-output", "output.json");
  const analysisResults = await runJavaAnalyzer(filePath, outputJsonPath, globalContext);

  // Apply underlining and hover information
  applyHovers(analysisResults);
  for (const analysisResult of analysisResults) {
    applyUnderline(analysisResult);
  }
  vscode.window.showInformationMessage("Java analysis completed.");
}

export function registerAnalyzeJava(context: ExtensionContext) {
  let disposable = vscode.commands.registerCommand("vscode-extension.analyzeJava", async () => {
    await analyzeJavaFile();
  });
  context.subscriptions.push(disposable);

  // Trigger analysis when a Java file is opened
  vscode.workspace.onDidOpenTextDocument(
    (document) => {
      if (document.languageId === "java") {
        analyzeJavaFile();
      }
    },
    null,
    context.subscriptions
  );

  // Trigger analysis when a Java file is modified
  vscode.workspace.onDidChangeTextDocument(
    (event) => {
      if (event.document.languageId === "java") {
        analyzeJavaFile();
      }
    },
    null,
    context.subscriptions
  );
}

export function deactivate() {}
