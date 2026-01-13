package com.jipple.error;

/**
 * Exception thrown when the main user code is run as a child process (e.g. pyspark) and we want
 * the parent JippleSubmit process to exit with the same exit code.
 */
public class JippleUserAppException extends JippleException {
    private final int exitCode;
    
    public JippleUserAppException(int exitCode) {
        super("User application exited with " + exitCode);
        this.exitCode = exitCode;
    }
    
    public int getExitCode() {
        return exitCode;
    }
}

