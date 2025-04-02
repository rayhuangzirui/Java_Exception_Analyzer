# Instructions on how to use the extension


## Build the extension:
```
npm run package
```

## Install the extension
```
code --install-extension vscode-extension-1.0.0.vsix
```
Note you will need to restart VSCode for changes to take effect. To run the extension, open the command pallette. Supported commands include:
* Analyze Java
* Configure Java Analyzer

## Using the extension
You can run the extension by opening the command pallette and running the following commands:
* `Analyze Java`: This will run the java analysis on the current file. It will also re-run the analysis on file save
* `Configure Java Analyzer`: This will open up the settings json, allowing you to configure the risk levels and coloring of our Java Analyzer

## Uninstall the extension
```
code --uninstall-extension undefined_publisher.vscode-extension
```

## Installation FAQ

### command not found: code
You need to add code to your path. You can do this automatically in VSCode by running the following, from the command pallette: `Shell Command: Install 'code' command in PATH`