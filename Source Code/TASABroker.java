import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.customDataCenterBroker;
import org.cloudbus.cloudsim.Vm;

public class TASABroker extends customDataCenterBroker {

	public TASABroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

	//scheduling function
	

	public void scheduleTaskstoVms(){
		int reqTasks= cloudletList.size();
		int reqVms= vmList.size();
		//int k=0;
		
		ArrayList<Cloudlet> clist = new ArrayList<Cloudlet>(cloudletList);
		ArrayList<Vm> vlist = new ArrayList<Vm>(vmList);
		
	
//
		
		double completionTime[][] = new double[reqTasks][reqVms];
		double execTime[][] = new double[reqTasks][reqVms];
		double[] suffrageScores = new double[reqTasks];
		
		double time =0.0;
		
		for(int i=0; i<reqTasks; i++){
			for(int j=0; j<reqVms; j++){
				time = getCompletionTime(clist.get(i), vlist.get(j));
				completionTime[i][j]= time;
				time = getExecTime(clist.get(i), vlist.get(j));
				execTime[i][j]= time;	
			}
		}
		
		for (int i = 0; i < reqTasks; i++) {
            int bestVMIndex = 0;
            int secondBestVMIndex = 0;
            double bestCompletionTime = Double.MAX_VALUE;
            double secondBestCompletionTime = Double.MAX_VALUE;

            for (int j = 0; j < reqVms; j++) {
                if (completionTime[i][j] < bestCompletionTime) {
                    secondBestVMIndex = bestVMIndex;
                    secondBestCompletionTime = bestCompletionTime;
                    bestVMIndex = j;
                    bestCompletionTime = completionTime[i][j];
                } else if (completionTime[i][j] < secondBestCompletionTime) {
                    secondBestVMIndex = j;
                    secondBestCompletionTime = completionTime[i][j];
                }
            }

            suffrageScores[i] = bestCompletionTime - secondBestCompletionTime;
        }
		
		int minCloudlet=0;
		int minVm=0;
		double min=-1.0d;
		
		for(int c=0; c< clist.size(); c++){
			if(clist.size()%2==1) {
				for(int i=0;i<clist.size();i++){
					for(int j=0;j<(vlist.size()-1);j++){
						if(completionTime[i][j+1] > completionTime[i][j] && completionTime[i][j+1] > 0.0){
							minCloudlet=i;
						}
					}
				}
					
					for(int j=0; j<vlist.size(); j++){
						time = getExecTime(clist.get(minCloudlet), vlist.get(j));
						if(j==0){
							min=time;
						}
						if(time < min && time > -1.0){
							minVm=j;
							min=time;
						}
				}
				
				bindCloudletToVm(minCloudlet, minVm);
				clist.remove(minCloudlet);
				
				for(int i=0; i<vlist.size(); i++){
					completionTime[minCloudlet][i]=-1.0;
				}
			}
			else {
				int maxSuffrageIndex = 0;
	            double maxSuffrage = suffrageScores[0];
	            for (int i = 1; i < clist.size(); i++) {
	                if (suffrageScores[i] > maxSuffrage) {
	                    maxSuffrageIndex = i;
	                    maxSuffrage = suffrageScores[i];
	                }
	            }

	            int bestVMIndex = 0;
	            double bestCompletionTime = Double.MAX_VALUE;

	            Cloudlet selectedCloudlet = clist.get(maxSuffrageIndex);
	            for (int j = 0; j < vlist.size(); j++) {
	                double compTime = completionTime[maxSuffrageIndex][j];
	                if (compTime < bestCompletionTime) {
	                    bestVMIndex = j;
	                    bestCompletionTime = compTime;
	                }
	            }

	            bindCloudletToVm(maxSuffrageIndex, bestVMIndex);
	            clist.remove(maxSuffrageIndex);
			}
		}
		
		
	}	
	
	
	private double getCompletionTime(Cloudlet cloudlet, Vm vm){
		double waitingTime = cloudlet.getWaitingTime();
		double execTime = cloudlet.getCloudletLength() / (vm.getMips()*vm.getNumberOfPes());
		
		double completionTime = execTime + waitingTime;
		
		return completionTime;
	}
	
	private double getExecTime(Cloudlet cloudlet, Vm vm){
		return cloudlet.getCloudletLength() / (vm.getMips()*vm.getNumberOfPes());
	}
}