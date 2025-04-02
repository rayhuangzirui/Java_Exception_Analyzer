# Build instructions

This is how Matthew is building the jar (Mac OS):
```
javac -cp "lib/*" -d out $(find src -name "*.java") && jar -cfm java-analyzer.jar MANIFEST.MF -C out . 
```

This is how Ray is building the jar (Windows OS):
- Open a terminal and navigate to the `java-analyzer` directory.
- Run the following command to compile the Java files:
```
javac -d out -cp "lib/*" src/*.java src/model/*.java src/analyzers/*.java
```
- After the compilation is complete, run the following command to create the jar file:
```
jar -cvf java-analyzer.jar -C out .
```
- The jar file will be created in the `java-analyzer` directory.
- Test the jar file by running the following command:
```
// replace the path with the path to your input file
java -cp "java-analyzer.jar;lib/*" Main ../resources/sample-java/EmptyCatchExample.java
```
- Move the jar file along with the `lib` folder to the `vscode-extension/jars` directory.
