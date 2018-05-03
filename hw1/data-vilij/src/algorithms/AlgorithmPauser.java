// Aaron Knoll
package algorithms;

/**
 * Pauses and resumes execution of a thread.
 * @author aaronknoll
 */
public class AlgorithmPauser {

    private boolean isPaused;
    
    public AlgorithmPauser() {
        isPaused = false;
    }

    /**
     * Prepares AlgorithmPauser to pause an Algorithm.
     */
    public synchronized void pause() {
        isPaused = true;
    }
    
    /**
     * Causes the invoking thread to wait for a notification to resume 
     * execution.
     * @throws InterruptedException
     */
    public synchronized void shouldIPause() throws InterruptedException {
        while (!isPaused) {
            wait();
        }
    }

    /**
     * Wakeup thread waiting on this monitor and resume its execution.
     */
    public synchronized void resume() {
        isPaused = false;
        notifyAll();
    }
}
