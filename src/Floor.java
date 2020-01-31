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
	private ArrayList<ControlDate> datas;
	private SimpleDateFormat sdf;
	private Buffer buffer;
	
	public Floor(Buffer buffer) {
		this.buffer = buffer;
		this.file = new File("data.txt");
		this.datas = new ArrayList<ControlDate>();
		sdf = new SimpleDateFormat("hh:mm:ss.mmm");
		getDataFromFile();

	}
	
	private void getDataFromFile() {
		
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
			
			datas.add(new ControlDate(time,floor,floorButton,destinationFloor));
			}
		
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

	public void receiveDataFromElevator(ControlDate c) {
		System.out.format("Floor received elevator info from scheduler: moved from floor %d to %d\n", c.getFloor(),
				c.getDestinationFloor());
	}
	
	@Override
	public void run() {
		for (ControlDate c : datas) {
			buffer.putFloorRequest(c);
		}
		
	}

	
}




