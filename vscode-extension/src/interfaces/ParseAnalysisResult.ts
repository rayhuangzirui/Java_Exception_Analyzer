import * as fs from 'fs';
import * as path from 'path';
import { AnalysisResult, Code, RiskLevel } from '../interfaces/HoverInfo';

/**
 * Reads a JSON file and parses it into an array of AnalysisResult objects.
 * @param filePath The path to the JSON file.
 * @returns An array of AnalysisResult objects.
 */
export function parseAnalysisResults(filePath: string): AnalysisResult[] {
    try {
        // Read the JSON file
        const absolutePath = path.resolve(filePath);
        const fileContent = fs.readFileSync(absolutePath, 'utf-8');

        // Parse the JSON content
        const jsonData = JSON.parse(fileContent);

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
        console.error('Error parsing AnalysisResult JSON file:', error);
        throw error;
    }
}