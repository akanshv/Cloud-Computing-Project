import java.util.ArrayList;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.customDataCenterBroker;
import org.cloudbus.cloudsim.Vm;

public class SuffrageBroker extends customDataCenterBroker {

    public SuffrageBroker(String name) throws Exception {
        super(name);
    }

    public void scheduleTaskstoVms() {
        int numTasks = cloudletList.size();
        int numVMs = vmList.size();

        ArrayList<Cloudlet> cloudletCopy = new ArrayList<>(cloudletList);
        ArrayList<Vm> vmCopy = new ArrayList<>(vmList);

        double[][] completionTimes = new double[numTasks][numVMs];
        double[] suffrageScores = new double[numTasks];

        for (int i = 0; i < numTasks; i++) {
            Cloudlet cloudlet = cloudletCopy.get(i);
            for (int j = 0; j < numVMs; j++) {
                Vm vm = vmCopy.get(j);
                double executionTime = getExecutionTime(cloudlet, vm);
                double completionTime = executionTime + cloudlet.getWaitingTime();
                completionTimes[i][j] = completionTime;
            }
        }

        for (int i = 0; i < numTasks; i++) {
            int bestVMIndex = 0;
            int secondBestVMIndex = 0;
            double bestCompletionTime = Double.MAX_VALUE;
            double secondBestCompletionTime = Double.MAX_VALUE;

            for (int j = 0; j < numVMs; j++) {
                if (completionTimes[i][j] < bestCompletionTime) {
                    secondBestVMIndex = bestVMIndex;
                    secondBestCompletionTime = bestCompletionTime;
                    bestVMIndex = j;
                    bestCompletionTime = completionTimes[i][j];
                } else if (completionTimes[i][j] < secondBestCompletionTime) {
                    secondBestVMIndex = j;
                    secondBestCompletionTime = completionTimes[i][j];
                }
            }

            suffrageScores[i] = bestCompletionTime - secondBestCompletionTime;
        }

        while (!cloudletCopy.isEmpty()) {
            int maxSuffrageIndex = 0;
            double maxSuffrage = suffrageScores[0];
            for (int i = 1; i < cloudletCopy.size(); i++) {
                if (suffrageScores[i] > maxSuffrage) {
                    maxSuffrageIndex = i;
                    maxSuffrage = suffrageScores[i];
                }
            }

            int bestVMIndex = 0;
            double bestCompletionTime = Double.MAX_VALUE;

            Cloudlet selectedCloudlet = cloudletCopy.get(maxSuffrageIndex);
            for (int j = 0; j < vmCopy.size(); j++) {
                double completionTime = completionTimes[maxSuffrageIndex][j];
                if (completionTime < bestCompletionTime) {
                    bestVMIndex = j;
                    bestCompletionTime = completionTime;
                }
            }

            bindCloudletToVm(maxSuffrageIndex, bestVMIndex);
            cloudletCopy.remove(maxSuffrageIndex);
        }
    }

    private double getExecutionTime(Cloudlet cloudlet, Vm vm) {
        return cloudlet.getCloudletLength() / (vm.getMips() * vm.getNumberOfPes());
    }
}