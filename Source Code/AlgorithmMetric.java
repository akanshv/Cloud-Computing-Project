
public class AlgorithmMetric {
	public String algorithmName;
    public double ARUR;
    public double makespan;
    public double averageMakespan;
    public double throughput;
    public double []comp_sharej_percent;
    public double LoadImbalance;

    public AlgorithmMetric(String algorithmName,  double makespan, double averageMakespan, double throughput,double ARUR, double[] comp_sharej_percent, double LoadImbalance) {
        this.algorithmName = algorithmName;
        this.ARUR = ARUR;
        this.makespan = makespan;
        this.averageMakespan = averageMakespan;
        this.throughput = throughput;
        this.comp_sharej_percent=comp_sharej_percent;
        this.LoadImbalance=LoadImbalance;
    }
}
