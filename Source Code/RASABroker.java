import java.util.ArrayList;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.customDataCenterBroker;
import org.cloudbus.cloudsim.Vm;

public class RASABroker extends customDataCenterBroker {

    public RASABroker(String name) throws Exception {
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
        	if(cloudletCopy.size()%2==1) {
	            int minIndex = 0;
	            double minCompletionTime = completionTimes[0];
	            for (int i = 1; i < cloudletCopy.size(); i++) {
	                if (completionTimes[i] < minCompletionTime) {
	                    minIndex = i;
	                    minCompletionTime = completionTimes[i];
	                }
	            }
	
	            int selectedVMIndex = -1;
	            double maxExecutionTime = 0;
	
	            Cloudlet selectedCloudlet = cloudletCopy.get(minIndex);
	            for (int j = 0; j < vmCopy.size(); j++) {
	                Vm vm = vmCopy.get(j);
	                double executionTime = getExecutionTime(selectedCloudlet, vm);
	                if (executionTime > maxExecutionTime) {
	                    selectedVMIndex = j;
	                    maxExecutionTime = executionTime;
	                }
	            }
	
	            bindCloudletToVm(minIndex, selectedVMIndex);
	            cloudletCopy.remove(minIndex);
        	}
        	else {
        		int minIndex = 0;
	            double minCompletionTime = completionTimes[0];
	            for (int i = 1; i < cloudletCopy.size(); i++) {
	                if (completionTimes[i] < minCompletionTime) {
	                    minIndex = i;
	                    minCompletionTime = completionTimes[i];
	                }
	            }
	
	            int selectedVMIndex = -1;
	            double minExecutionTime = 0;
	
	            Cloudlet selectedCloudlet = cloudletCopy.get(minIndex);
	            for (int j = 0; j < vmCopy.size(); j++) {
	                Vm vm = vmCopy.get(j);
	                double executionTime = getExecutionTime(selectedCloudlet, vm);
	                if (executionTime < minExecutionTime) {
	                    selectedVMIndex = j;
	                    minExecutionTime = executionTime;
	                }
	            }
	
	            bindCloudletToVm(minIndex, selectedVMIndex);
	            cloudletCopy.remove(minIndex);
        	}
        }
    }

    private double getExecutionTime(Cloudlet cloudlet, Vm vm) {
        return cloudlet.getCloudletLength() / (vm.getMips() * vm.getNumberOfPes());
    }
}