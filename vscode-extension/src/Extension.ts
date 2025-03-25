// This is the entry point for extension logic

import * as vscode from "vscode";
import { ExtensionContext } from "vscode";
import { applyHover, applyUnderline } from "./Highlight";
import { mockHoverInfo } from "./resources/mockHoverInfo";

export function activate(context: ExtensionContext) {
  sayHello(context);
  underline(context);
}

export function sayHello(context: ExtensionContext) {
  let disposable = vscode.commands.registerCommand("vscode-extension.sayHello", () => {
    vscode.window.showInformationMessage("Is this compiling? 2");
  });
  context.subscriptions.push(disposable);
}

export function underline(context: ExtensionContext) {
  let disposable = vscode.commands.registerCommand("vscode-extension.underlineText", () => {
    applyUnderline(mockHoverInfo);
    applyHover(mockHoverInfo);
  });

  context.subscriptions.push(disposable);
}

export function deactivate() {}
