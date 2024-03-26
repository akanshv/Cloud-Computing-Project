	
import java.util.Formatter;  
public class table {
	public static void main(String args[]) throws Exception   
	{  
	int num[] = {10, 21, 13, 4, 15, 6, 27, 8, 19};  
	Formatter fmt = new Formatter();  
	fmt.format("%15s %15s %15s\n", "Number", "Square", "Cube");  
	for (int n : num)   
	{  
	fmt.format("%14s %14s %17s\n", n, (n*n), (n*n*n));  
	}  
	System.out.println(fmt);  
	}  	
}
