# Build instructions

This is how Matthew is building the jar:
```
javac -cp "lib/*" -d out $(find src -name "*.java") && jar -cfm java-analyzer.jar MANIFEST.MF -C out . 
```