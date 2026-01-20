# Java Exception Analyzer
A Java static analysis tool and VS Code extension that detect improper exception-handling patterns. Implemented analysis modules using JavaParser and integrated diagnostics into a command-based VS Code workflow. The visualization component includes:
* Syntax highlighting: Underline areas of code which are identified as problematic. The colors for the highlighted sections correspond to the associated "risk level"
* Error messaging and suggestions: When hovering the cursor over underlined sections of code, a message describing what the error is will be displayed, as well as a suggestion on how to address the issue
* Custom configuration of risk levels and colors: The user is able to provide custom configurations on the risk levels of each type of identified error, as well as provide custom coloring for the syntax highlighting

Our static analyzer identifies the following exception-related issues: 
- Empty catch exception blocks 
- Expensive Exception Control Flow: identify try-catch blocks inside loops 
- Unclosed Resources 
- Unhandled Exceptions 
- Undeclared Exceptions 
- System Error in Catch: identify catch blocks that use System.err or printStackTrace 
- Always Triggered Catch 
- Redundant Try-Catch: find cases where conditional checks can replace exception handling 
- Exception Propagation: count number of functions affected by exceptions 

## Java Analyzer
We implemented our java analysis using JavaParser in Java and parsed analysis results to a JSON object using Gson. The source code for the analysis can be found in `java-analyzer`. We build this into a JAR file, which can then be used in our VS-code extension.

## VS-code extension
The visualization component is implemented as a VSCode extension in TypeScript. It gets analysis results from JSON that `java-analyzer` provides. 

# Instructions on how to use the extension

## Environment for installation
- Make sure you have the following installed:
    - VSCode (v1.98.0 or later)
    - Node.js (v18 or later)
    - npm (or yarn)


## Build the extension:
```
npm run package
```

## Install the extension
```
code --install-extension vscode-extension-1.0.0.vsix
```
- Make sure to check the extension is installed by running the following command:
```
// You should see rayhuangzirui.vscode-extension in the list
code --list-extensions
```

- Note you will need to restart VSCode for changes to take effect.
  To run the extension, open the command palette. Supported commands include:
* Analyze Java
* Configure Java Analyzer

## Using the extension
You can run the extension by opening the command palette and running the following commands:
* `Analyze Java`: This will run the java analysis on the current file. It will also re-run the analysis on file save
* `Configure Java Analyzer`: This will open up the settings json, allowing you to configure the risk levels and coloring of our Java Analyzer

## Uninstall the extension
```
code --uninstall-extension rayhuangzirui.vscode-extension
```

## Installation FAQ

### command not found: code
You need to add code to your path. You can do this automatically in VSCode by running the following, from the command palette: `Shell Command: Install 'code' command in PATH`