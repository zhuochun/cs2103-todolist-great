package cs2103.t14j1.Logic.smartBarParse;

import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Define the basic property of the task
 * for the int type, -1 means doesn't exist;
 * for the object type, null means doesn't exist 
 * 
 * @author Song Yangyu
 * 
 */
class BasicTask{

	
	String title;
	
	String date;
	
	String place;
	
	/**
	 * Unit: minutes
	 */
	int duration;
	
	/**
	 * Initially to be 1~3
	 */
	int priority;
	
	/**
	 * Assume to have one initially
	 */
	String tag;
	
	boolean completed;
	
	BasicTask(String taskStr){
		title = taskStr;
		
		
		
		// calendar
		
	}
	
	@Override
	public String toString(){
		String res = "Title: " + this.title;
		
		return res;
	}
}

public class SmartBarParseCommand {

	// @group: regular expressions for match
	
	// always match am/pm first, then match the 24h time point
	static final String regTimePointAmPm =
			"(0?[1-9]|1[012])(:\\ [0-5]\\d){0,2}(\\ [AP]M)";
	static final String regTimePoint24H = 
			"([01]?\\d|2[0-3])(:[0-5]\\d){0,2}";
	static final String regTimePointInteger =
			"(1[012]|[1-9])([AP]M)";
	static final String regTimeFormat = 
			"(" + regTimePointAmPm + "|" + regTimePoint24H + ")";
	
	static final String regYear =
			"(19|20)\\d\\d)";
	static final String reg =
			"(19|20)\\d\\d)";
	
	static final String regMonth =
			"(Jan|January|Feb|)";
	static final String regDateFormatDM = 
			"(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])([- /.](19|20)\\d\\d)?";
	static final String regDateFormatMD = 
			"(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])([- /.](19|20)\\d\\d)?";
	static final String regMatchCalendar = "at";
	// @endgroup
	
	
	// filed of this class
	String command;
	
	/** The constructor
	 * @param command - 
	 */
	public SmartBarParseCommand(String command) {
		this.command = command;
	}
	
	
	/** Get the start time of the task
	 * @return
	 */
	public Integer getStartTime(){
		int startTime = 0;
		return 1;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		// test match here
		String taskStr = "at 10:00 am ~ 12:00 pm";	// test time
		Pattern timePoint = Pattern.compile(regTimeFormat);
		Matcher timePointMatcher = timePoint.matcher(taskStr);
		while(timePointMatcher.find()){
			System.out.println(timePointMatcher.group());
		}
		// code for getting user input to test
//		taskStr = in.nextLine();
//		while(taskStr.trim().compareToIgnoreCase("exit") != 0){
//			BasicTask task = new BasicTask(taskStr);
//			System.out.println(task);
//			
//			taskStr = in.nextLine();
//		}
	}

}
