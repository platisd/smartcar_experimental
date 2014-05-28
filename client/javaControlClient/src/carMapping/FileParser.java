package carMapping;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
/***
 *  a simple test case parse data file to SonarData object for exploring.  
 * @author lijiaxin
 *
 */

public class FileParser {
	
	int pos = 0;
	Scanner scanner;
	ArrayList<SonarData> packages;
	int [] record = new int [SonarData.Nr] ;
	public FileParser(File file) throws IOException {
		scanner = new Scanner(file);
		
	}
	
	public ArrayList<SonarData> read() {
		packages = new ArrayList<SonarData>();
	    while(scanner.hasNext()){
	    	//System.out.println(scanner.nextLine());
	    	 record = new int [SonarData.Nr] ;
	        String [] tokens = scanner.nextLine().split(",");	        
	        for (int i =0 ;i < tokens.length; i++){
	        	record [i] = Integer.parseInt(tokens[i] );
	        }
	        packages.add(new SonarData(record));	        
	    }
	    return packages;
	}
	
	
	
}
