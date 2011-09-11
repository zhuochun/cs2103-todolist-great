package cs2103.t14j1.Logic.smartBarParse;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class would parse the command from SmartBar.
 *  
 *  To use it, you would create an intense of it using its constructor, with the
 *   passed command (String) as a parameter.
 *  
 *  Example:
 *  
 *  @code
 *  // smartBarStr is the String passed from smart bar GUI
 *  SmartBarParseCommand newCommand = new SmartBarParseCommand(smartBarStr); 
 *	 
 *	// to get the command type
 *	newCommend
 * 	@endcode
 * 	
 * @author Song Yangyu
 */
public class SmartBarParseCommand {

	// @group regex: regular expressions for match
	
	// always match am/pm first, then match the 24h time point
	private static final String regTimePointAmPm =
			"(0?[1-9]|1[012])(:\\ [0-5]\\d){0,2}(\\ [AP]M)";
	private static final String regTimePoint24H = 
			"([01]?\\d|2[0-3])(:[0-5]\\d){0,2}";
	private static final String regTimePointInteger =
			"(1[012]|[1-9])([AP]M)";
	private static final String regTimeFormat = 
			"(" + regTimePointAmPm + "|" + regTimePoint24H + ")";
	
	private static final String regYear =
			"(19|20)\\d\\d)";
	private static final String reg =
			"(19|20)\\d\\d)";
	
	private static final String regMonth =
			"(Jan|January|Feb|)";
	private static final String regDateFormatDM = 
			"(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])([- /.](19|20)\\d\\d)?";
	private static final String regDateFormatMD = 
			"(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])([- /.](19|20)\\d\\d)?";
	private static final String regMatchCalendar = "at";
	// @endgroup regex
	
	
	// @group regex-match
	
	// @endgroup regex-match
	
	// filed of this class
	String command;
	
	/** The constructor
	 * @param command - the command passed from the smart bar GUI
	 */
	public SmartBarParseCommand(String command) {
		this.command = command;
	}
	
	/**
	 * @return the start date of the task; timestamp of the mid-night of that date
	 */
	public Integer getStartDate(){
		return 0;
	}
	
	/** Get the start time of the task; the number of seconds relevative to the
	 *  start of the day.
	 *  Example, if I 
	 * @return
	 */
	public Integer getStartTime(){
		int startTime = 0;
		return 1;
	}
	
	public Integer getEndTime(){
		return 1;
	}
	
	public Integer getDuration(){
		return 1;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		// test match here
		String taskStr = "at 10:00 am ~ 12:00 pm";	// test time
		Pattern timePoint = Pattern.compile(regTimePointAmPm);
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
