import java.io.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Floor implements Runnable{
	/**
	 * this class store read in event Time, floor or elevator number, and button into a list of ControlData stcture
	 * @author Zewen Chen
	 */
	File file;
	private Date date;
	private Time time;
	private int floor;
	private boolean floorButton; // true if up, false if down c
	private int destinationFloor;
	private ArrayList<elevatorEvent> datas;
	private SimpleDateFormat sdf;
	public Floor() {
		this.file = new File("/Users/admin/eclipse-workspace/SYSC3303Project/data.txt");
		this.datas = new ArrayList<elevatorEvent>();
		sdf = new SimpleDateFormat("hh:mm:ss.mmm");
	}
	@Override
	public void run() {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while((str = br.readLine())!=null){
				//System.out.println(str);
				String[] x = str.split(" ");
				for(int i=0;i<x.length;i++) {
					if(i==0) {
						//time
						date = sdf.parse(x[i]);
						time = new Time(date.getTime());
					}
					if(i==1) {
						//floor
						floor = Integer.parseInt(x[i]);
					}
					if(i==2) {
						//floorButton
						if(x[i].equals("Up")) {
							floorButton=true;
						}
						if(x[i].equals("Down")) {
							floorButton=false;
						}
					}
					if(i==3) {
						//destinationFloor
						destinationFloor = Integer.parseInt(x[i]);
					}
				}
			datas.add(new elevatorEvent(time,floor,floorButton,destinationFloor));
			}
			System.out.print(datas.get(3).getTime());
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Time valueOf(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	public static void main(String[] args) {
		Thread f = new Thread(new Floor());
		f.start();
		
	}

	
}




