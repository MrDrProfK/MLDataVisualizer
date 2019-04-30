// Aaron Knoll
package components;

/**
 * Used for storing specific algorithm configuration data.
 *
 * @author aaronknoll
 */
public class AlgorithmConfiguration {

    public int maxIterations;
    public int updateInterval;
    public boolean continuousRun;
    private boolean clustering;
    public int numOfClusteringLabels;

    public AlgorithmConfiguration(int maxIterations, int updateInterval, boolean continuousRun, boolean clustering, int numOfClusteringLabels) {
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.continuousRun = continuousRun;
        this.clustering = clustering;
        this.numOfClusteringLabels = numOfClusteringLabels;
    }

    public boolean isClustering() {
        return clustering;
    }
}
