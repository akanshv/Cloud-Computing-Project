import java.util.ArrayList;
import org.cloudbus.cloudsim.Cloudlet;
//import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.customDataCenterBroker;

public class MCTBroker extends customDataCenterBroker{

    public MCTBroker(String name) throws Exception {
        super(name);
    }

    public void scheduleTaskstoVms() {
        int numTasks = cloudletList.size();
        int numVMs = vmList.size();

        ArrayList<Cloudlet> cloudletCopy = new ArrayList<>(cloudletList);
        ArrayList<Vm> vmCopy = new ArrayList<>(vmList);

        double[] completionTimes = new double[numTasks];
        double[] executionTimes = new double[numTasks];

        for (int i = 0; i < numTasks; i++) {
            Cloudlet cloudlet = cloudletCopy.get(i);
            for (int j = 0; j < numVMs; j++) {
                Vm vm = vmCopy.get(j);
                double executionTime = getExecutionTime(cloudlet, vm);
                double completionTime = executionTime + cloudlet.getWaitingTime();
                if (completionTime < completionTimes[i]) {
                    completionTimes[i] = completionTime;
                    executionTimes[i] = executionTime;
                }
            }
        }

        while (!cloudletCopy.isEmpty()) {
            int maxIndex = 0;
            double maxCompletionTime = completionTimes[0];
            for (int i = 1; i < cloudletCopy.size(); i++) {
                if (completionTimes[i] > maxCompletionTime) {
                    maxIndex = i;
                    maxCompletionTime = completionTimes[i];
                }
            }

            int selectedVMIndex = -1;
            double minExecutionTime = Double.MAX_VALUE;

            Cloudlet selectedCloudlet = cloudletCopy.get(maxIndex);
            for (int j = 0; j < vmCopy.size(); j++) {
                Vm vm = vmCopy.get(j);
                double executionTime = getExecutionTime(selectedCloudlet, vm);
                if (executionTime < minExecutionTime) {
                    selectedVMIndex = j;
                    minExecutionTime = executionTime;
                }
            }

            bindCloudletToVm(maxIndex, selectedVMIndex);
            cloudletCopy.remove(maxIndex);
        }
    }

    private double getExecutionTime(Cloudlet cloudlet, Vm vm) {
        return cloudlet.getCloudletLength() / (vm.getMips() * vm.getNumberOfPes());
    }
}