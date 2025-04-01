import * as fs from "fs";
import * as path from "path";
import * as vscode from "vscode";
import { AnalysisResult, Code, RiskLevel } from "../interfaces/HoverInfo";
import { exec, execFile } from "child_process";
import { ExtensionContext } from "vscode";

/**
 * Reads a JSON file and parses it into an array of AnalysisResult objects.
 * @param filePath The path to the JSON file.
 * @returns An array of AnalysisResult objects.
 */
export function parseAnalysisResults(analysisOutput: string): AnalysisResult[] {
  try {
    // Parse the JSON content
    const jsonData = JSON.parse(analysisOutput);

    // Map the JSON data to AnalysisResult objects
    const results: AnalysisResult[] = jsonData.map((item: any) => ({
      errorCode: item.errorCode as Code,
      node: item.node,
      startLine: item.startLine,
      startChar: item.startChar,
      endLine: item.endLine,
      endChar: item.endChar,
      message: item.message,
      suggestion: item.suggestion,
      riskLevel: item.riskLevel as RiskLevel,
    }));

    return results;
  } catch (error) {
    console.error("Error parsing AnalysisResult JSON file:", error);
    throw error;
  }
}

// Runs the Java analyzer program on the given file
export async function runJavaAnalyzer(inputFilePath: string, outputJsonPath: string, context: ExtensionContext): Promise<AnalysisResult[]> {
  // Path to the Java analyzer JAR file (adjust this path as needed)
  // const jarPath = path.join(__dirname, "..", "..", "java-analyzer", "target", "java-analyzer.jar");
  const jarPath = path.join(context.extensionPath, "src/jars/java-analyzer.jar");
  const libPath = path.join(context.extensionPath, "src/jars/lib/*");
  const jarDependencies = [
    path.join(context.extensionPath, "src/jars/lib/javaparser-core-3.26.3.jar"), 
    path.join(context.extensionPath, "src/jars/lib/gson-2.12.1.jar")
  ].join(":");

  return new Promise((resolve, reject) => {
  execFile("java", ["-cp", `${jarPath}${path.delimiter}${libPath}`, 'Main', inputFilePath], (error, stdout, stderr) => {
    if (error) {
      vscode.window.showErrorMessage(`Error: ${error.message}`);
      reject(error);
    }
    if (stderr) {
      vscode.window.showErrorMessage(`Error Output: ${stderr}`);
      reject(stderr);
    }
    const analysisResults = parseAnalysisResults(stdout);
    resolve(analysisResults)
  })})
}
