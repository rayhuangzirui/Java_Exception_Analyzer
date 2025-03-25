package analyzers;

public class SystemErrorInCatch {
    // Identify if a catch block is still printing to System error
    // try{...}catch(Error e){System.err.print(e)}
    // try{...}catch(Error e){printStackTrace(e)}
}
