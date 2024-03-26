
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import org.cloudbus.cloudsim.customDataCenterBroker;

public class Vmcreation
{
	private static List<Vm> vmList;
	private static List<Vm> vmListminmin;
	private static List<Cloudlet> cloudletList;
	private static List<Cloudlet> cloudletListminmin;
	static int CloudletsCount=1000;
	private static void CloudSimInitialse(){
		
		try {
		
		int num_user = 1;   // number of cloud users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false;  // mean trace events

		CloudSim.init(num_user, calendar, trace_flag);
		}
		 catch (Exception e) {
				e.printStackTrace();				
			}	
	}
	
	
	@SuppressWarnings("deprecation")
	
	public static void main( String[] args) throws Exception
	{


		CloudSimInitialse();
		long [] dataArray=dataset();
		ArrayList<AlgorithmMetric> Metrics=new ArrayList<>();
//		//create Datacenter
		@SuppressWarnings("unused")
		Datacenter datacenter0 = createDatacenter("Datacenter_0");
		
		
//		DatacenterBroker broker =createBroker();
		ArrayList<customDataCenterBroker> BrokerArray = new ArrayList<>();
		
		int[] chilo_mips= {900, 920, 970, 1040, 1095, 1130,1190, 1250, 1320,1380 , 1430, 1510, 1560, 1590, 1640, 1700, 1770, 1850, 1950, 2070, 2250, 2300, 2370, 2420, 2540, 2850, 3350, 3780, 3850, 4070, 4220, 4500};
		

		
		BrokerArray.add(new MinminBroker("MinMinBroker"));
		BrokerArray.add(new MCTBroker("MCTBroker"));
		BrokerArray.add(new FCFSBroker("FCFSBroker"));
		BrokerArray.add(new RoundRobinBroker("RoundRobinBroker"));
		BrokerArray.add(new SJFBroker("SJFBroker"));
//		BrokerArray.add(new SelectiveBroker("SelectiveBroker"));
		BrokerArray.add(new EnhancedMaxMinBroker("EnhancedMaxMinBroker"));
		BrokerArray.add(new SuffrageBroker("SuffrageBroker"));
		BrokerArray.add(new RASABroker("RASABroker"));
		BrokerArray.add(new TASABroker("TASABroker"));
		
	
		int Algorithm_no=0;
		
		for(customDataCenterBroker broker :BrokerArray) {
			
			
			int brokerId= broker.getId();
	
	//		createVm(2,32);
			
			vmList=new ArrayList<Vm>();
			
			long size =10000;
			int ram =1024;
			long bw = 1000;//bandwidth
			int pesNumber =1;//no of cpus you want
			String vmm = "Xen";
			for(int i=0;i<chilo_mips.length;i++)
			{
				Vm vm= new Vm(i,brokerId,chilo_mips[i],pesNumber,ram , bw,size,vmm, new CloudletSchedulerTimeShared());
				vmList.add(vm);
				
			}
			System.out.println("VMs Created");
	//		//submit vm list to the broker
			broker.submitVmList(vmList);
			
			 cloudletList = new ArrayList<Cloudlet>();
			 
				int cid=0;
				long length=4000;//instructions
				long fileSize=300;
				long outputSize=300;
				UtilizationModel utilmodel=new UtilizationModelFull();//describe whether we can use all the resource of data center or not.
				
				int no=dataArray.length;
				//(int)dataArray[i][j]
				for(int i=0;i<CloudletsCount; i++) {
				 
					
					  Cloudlet cloudlet = new Cloudlet(i,dataArray[i], pesNumber, fileSize, outputSize, utilmodel, utilmodel, utilmodel);
					cloudlet.setUserId(brokerId);
				
						cloudletList.add(cloudlet);   
						
				}
				broker.submitCloudletList(cloudletList);	
				broker.scheduleTaskstoVms();		
			}
			CloudSim.startSimulation();
			
			
			CloudSim.stopSimulation();
			
			
			double[][] LoadImbalance_Vm = new double[32][BrokerArray.size()];
			for(customDataCenterBroker broker:BrokerArray)
			{
				List<Cloudlet> list = broker.getCloudletReceivedList();	
				double[] vmMakespan = new double[vmList.size()];
				for(Cloudlet cloudlet:list) {
					double ActualCPUTime =cloudlet.getActualCPUTime();
					 double submissionTime = cloudlet.getSubmissionTime();
					 double completionTime = ActualCPUTime +submissionTime;
					 int vmid= cloudlet.getVmId();
					 
					if(vmMakespan[vmid]<completionTime) {
						vmMakespan[vmid]=completionTime;
					}
				}
				double avgmakspan=0, makespan=0;
				 
				for(int i=0; i<32; i++) {
					avgmakspan+=vmMakespan[i];
					if(vmMakespan[i]>makespan) {
						makespan=vmMakespan[i];
					}
//					 Log.printLine("The completion time of vmid "+ i +" is "+ vmMakespan[i]);
				}
				avgmakspan/=32;
				double throughput = list.size() / makespan;
				double ARUR=avgmakspan/makespan;
				double CompSharej=0;
				
				double[] clt = new double[vmList.size()];
				double vmks=0;
				
				double[] comp_sharej = new double[vmList.size()];
				for (Cloudlet cloudlet : list)  {	
					int vmid= cloudlet.getVmId();		
						clt[vmid] += cloudlet.getCloudletLength();
				}
				
				for(int i=0; i<chilo_mips.length; i++) {
					vmks += chilo_mips[i];
				}
				
				int totalcomp_share=0;
				for(int i=0; i<comp_sharej.length; i++) {
					comp_sharej[i]=(clt[i]*chilo_mips[i])/vmks;
					totalcomp_share+=comp_sharej[i];
				}
				
				double[] comp_sharej_percent = new double[vmList.size()];
				for(int i=0; i<comp_sharej.length; i++) {
					comp_sharej_percent[i]= comp_sharej[i]/totalcomp_share;
				}
				
				double LoadImbalance=0;
			
				double []Load = new double[vmList.size()];
				double AverageLoad=0;
				
				for (Cloudlet cloudlet :list)  {
					int vmid= cloudlet.getVmId();		
					Load[vmid]+=cloudlet.getSubmissionTime()+ cloudlet.getActualCPUTime();
					AverageLoad+=cloudlet.getSubmissionTime()+  cloudlet.getActualCPUTime();

				
				}
				AverageLoad /=vmList.size();

				
			    for(int i=0; i<vmList.size(); i++) {
			    	
			    	LoadImbalance_Vm[i][Algorithm_no]=Math.abs((Load[i]-AverageLoad)/AverageLoad);
			   
			    	LoadImbalance = LoadImbalance + Math.abs((Load[i]-AverageLoad)/AverageLoad);
//			    Log.printLine(Load[i] + i +"TH");;
			    }
			    
			    
				Log.printLine("Load Imbalance: "+ LoadImbalance);
				Log.printLine("Broker Name :"+broker.getName());
				Log.printLine("Makespan :"+makespan);
				Log.printLine("Average Makespan :"+avgmakspan);
		        Log.printLine("The throughput for vms "+ throughput);
		        Log.printLine("The ARUR for vms "+ ARUR);
				Log.printLine();
				Metrics.add(new AlgorithmMetric(broker.getName(),makespan,avgmakspan,throughput,ARUR, comp_sharej_percent, LoadImbalance));
				Algorithm_no++;
			}
		    
			LoadImbalance_Vm[0][0]=9;

			PlotResults.plotMetrics(Metrics);
		    
			
			 DecimalFormat df = new DecimalFormat("#.###");
		
			
			Formatter fmt = new Formatter();  
			fmt.format("%14s %15s %15s %15s %15s %15s %15s %15s %15s %15s\n", "VmId", "Minmin","MCT", "FCFS", "RoundRobin", "SJFS", "MaxMin", "Sufferage", "RASA", "TASA");  
		
		  int i=0;
			for (double[] row : LoadImbalance_Vm) {
				
				fmt.format("%14s %15s %15s %15s %15s %15s %15s %15s %15s %15s\n", i, df.format(row[0]), df.format(row[1]), df.format(row[2]), df.format(row[3]), df.format(row[4]), df.format(row[5]), df.format(row[6]), df.format(row[7]), df.format(row[8]));
	        i++;
			}
			
			System.out.println(fmt);  
	}
			
	
	private static long[] dataset() 
	{
		long[] cloudletsMI = null;
		String fileName = "C:\\Users\\akash\\OneDrive\\Desktop\\java\\Task-scheduling-algorithms-implementation-on-cloudsim\\1024x32\\GoCJ_Dataset_1000.txt";
		 try {
	            // Replace 'yourBinaryFile.bin' with the actual file path.
	            FileInputStream fileInputStream = new FileInputStream(fileName);
	            DataInputStream dataInputStream = new DataInputStream(fileInputStream);	            
	            System.out.println();      	            
	            // Create a 2D array to store the data.
	            cloudletsMI = new long[CloudletsCount];

	            // Read and store the data.
	            for (int i = 0; i < CloudletsCount; i++) {
                    String value = dataInputStream.readLine();
                    cloudletsMI[i] = Long.parseLong(value);
	               
	            }

	            // Close the input streams.
	            dataInputStream.close();
	            fileInputStream.close();

	            // You now have the data in the 'dataArray' 2D array.
	            // You can access individual values like dataArray[row][column].

	            // For example, let's print the first value:
	            System.out.println("First value: " + cloudletsMI[0]);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		return cloudletsMI;
		
	}
	
	private static Datacenter createDatacenter(String name)
	{
		List<Host> hostList =new ArrayList<Host>();
		int mips=1000000;
		List<Pe> peList =new ArrayList<Pe>();
		peList.add(new Pe(0, new PeProvisionerSimple(mips)));
		int hostId=0;
		int ram=1024000;
		int storage=10000000;
		int bw=1000000;
		
		Host host0= new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList, new VmSchedulerTimeShared(peList));
		
		hostList.add(host0);
		
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
		// devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}
	
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}	
}