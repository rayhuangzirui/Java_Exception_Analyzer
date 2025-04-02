# Group12Project2
We built a tool to analyze Java files. We are doing a static analysis using Java ANTLR, and we are visualizing our results using a VS-Code extension. The visualization component includes:
* Syntax highlighting: We underline areas of code which we have identified as problematic. The colors for the highlighted sections correspond to the associated "risk level"
* Error messaging and suggestions: When hovering the cursor over underlined sections of code, we will display a message describing what the error is, as well as a suggestion on how to address the issue
* Custom configuration of risk levels and colors: The user is able to provide custom configurations on the risk levels of each type of identified error, as well as provide custom coloring for the syntax highlighting

Our static analyzer identifies the following exception-related issues: 
- Empty catch exception blocks 
- Expensive Exception Control Flow: identify try-catch blocks inside loops 
- Unclosed Resources 
- Unhandled Exceptions 
- Undeclared Exceptions 
- System Error in Catch: identify catch blocks that use System.err or printStackTrace 
- Always Triggered Catch 
- Redundant Try-Catch: find cases where conditional checks can replace excception handling 
- Exception Propagation: count number of functions affected by excecptions 

## Java Analyzer
We implemented our java analysis using ANTLR. The source code for the analysis can be found in `java-analyzer`. We build this into a JAR file, which can then be used in our VS-code extension.

## VS-code extension
The visualization component is implemented as a VSCode extension. Instructions on how to install and use the extension are included in `vscode-extension/README.md`