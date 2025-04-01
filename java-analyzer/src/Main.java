import analyzers.*;
import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import model.AnalysisResult;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
//            String filePath = "resources/sample-java/EmptyCatchExample.java";
            if (args.length < 1) {
                System.err.println("Please provide the path to the Java file to analyze.");
                return;
            }
            String filePath = args[0];
            File file = new File(filePath);
            CompilationUnit cu = StaticJavaParser.parse(file);
            List<AnalysisResult> results = getAnalysisResults(cu);

            if (!results.isEmpty()) {
                System.out.println(AnalysisResult.toJsonArray(results));
            }

            // Don't write to file, or else vs-code extension will not work
            // Path outputPath = Path.of("output.json");
            // try (FileWriter writer = new FileWriter(outputPath.toFile())) {
            //     writer.write(AnalysisResult.toJsonArray(results));
            // }
        } catch (Exception e) {
            System.err.println("Analysis failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<AnalysisResult> getAnalysisResults(CompilationUnit cu) {
        List<AnalysisResult> results = new ArrayList<>();
        List<BaseAnalyzer> analyzers = List.of(
                new EmptyCatch(),
                new UnclosedResource(),
                new UndeclaredException(),
                new UnhandledException(),
                new LoopTryCatch(),
                new RedundantTryCatch(),
                new ExceptionPropagation(),
                new AlwaysTriggeredCatch(),
                new SystemErrorInCatch()
        );

        analyzers.forEach(analyzer -> analyzer.analyze(cu, results));
        return results;
    }
}