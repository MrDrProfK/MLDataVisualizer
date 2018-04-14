// Aaron Knoll
package components;

/**
 * Used for storing specific algorithm configuration data.
 * 
 * @author aaronknoll
 */
public class AlgorithmConfiguration {
    int maxIterations;
    int updateInterval;
    boolean continuousRun;
    boolean clustering;
    int numOfClusteringLabels;
    
    public AlgorithmConfiguration(int maxIterations, int updateInterval, boolean continuousRun, boolean clustering, int numOfClusteringLabels){
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.continuousRun = continuousRun;
        this.clustering = clustering;
        this.numOfClusteringLabels = numOfClusteringLabels;
    }
}
