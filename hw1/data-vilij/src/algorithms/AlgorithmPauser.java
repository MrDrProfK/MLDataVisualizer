// Aaron Knoll
package algorithms;

/**
 * Pauses and resumes execution of a thread.
 * @author aaronknoll
 */
public class AlgorithmPauser {

    private boolean paused;
    
    public AlgorithmPauser() {
        paused = false;
    }

    /**
     * Prepares AlgorithmPauser to pause an Algorithm.
     */
    public synchronized void pause() {
        paused = true;
    }
    
    /**
     * Causes the invoking thread to wait for a notification to resume 
     * execution.
     * @throws InterruptedException
     */
    public synchronized void shouldIPause() throws InterruptedException {
        while (paused) {
            wait();
        }
    }

    /**
     * Wakeup thread waiting on this monitor and resume its execution.
     */
    public synchronized void resume() {
        paused = false;
        notifyAll();
    }
    
    /**
     * Getter for boolean value paused.
     * @return true if thread is paused, and false if thread is NOT paused
     */
    public synchronized boolean isPaused(){
        return paused;
    }
    
}
