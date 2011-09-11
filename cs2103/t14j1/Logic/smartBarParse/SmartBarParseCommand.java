package cs2103.t14j1.Logic.smartBarParse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.internal.C;

import cs2103.t14j1.Logic.Commands;
import cs2103.t14j1.Logic.ExtractParsedCommand;

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
public class SmartBarParseCommand implements ExtractParsedCommand {

	// @group regex: regular expressions for match
	
		// to differ between words -- a spacer is not a digit character or alphabet
	private static final String regWordSpacer = "([^\\d\\w]|^)";
	private static final String regDateSpacer = "[,-/. ]";

		// regular expression for matching the time
	private static final String regTimePointAmPm =
			"(1[012]|\\d)(:[0-5]\\d){0,2}((\\ )?[ap]m)";
	private static final String regTimePoint24H = 
			"([01]?\\d|2[0-3])(:[0-5]\\d){0,2}";
	private static final String regTimePointInteger =
			"(1[012]|[1-9])([AP]M)";
	private static final String regTimeFormat = 
			"(" + regTimePointAmPm + "|" + regTimePoint24H + ")";
	
	
		// regular expression for matching the date
	private static final String regMonthText =
			"(January|Jan|February|Feb|March|Mar|April|Apr|May|June|Jun|July|Jul|" + 
			"August|Aug|September|Sept|October|Oct|November|Nov|December|Dec)";
	private static final String regWeekText =
			"(Monday|Mon|Tuesday|Tue|Wednesday|Wed|Thursday|Thur|Friday|Fri|Saturday|Sat|Sunday|Sun)";
	private static final String regDayOrder = "((this)?|next)";

			// date format
	private static final String regDateFormat_dd_$mm$_$yy$yy$$ = 
			"([12][0-9]|3[01]|(0)?[1-9])" + regDateSpacer + "(0[1-9]|1[012])(" + 
			regDateSpacer + "(19|20)?\\d\\d)?";
	private static final String regDateFormat_dd_$M$_$yy$yy$$ = // When month is text, year should be a full string if exist
			"([12][0-9]|3[01]|(0)?[1-9])" + regDateSpacer + regMonthText + "(" + regDateSpacer + "(19|20)\\d\\d)?";
	private static final String regDateFormat_dd_$mm$M$_$yy$yy$$ = 
			"(" + regDateFormat_dd_$M$_$yy$yy$$+ ")|(" + regDateFormat_dd_$mm$_$yy$yy$$ + ")";
	private static final String regDayFormat_order_D = 
			"(" + regDayOrder + regWeekText + ")";
	
		// the date format for mm/dd/yy; leave it here for the possible use in the future
	private static final String regDateFormat_mm_dd_yy =
			"(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])([- /.](19|20)\\d\\d)?";
	private static final String regMatchCalendar = "at";
	// @endgroup regex
	
	
	// @group regex-match
	
	// @end group regex-match
	
	// basic information 
	private Calendar startDate = null;
	Long startTime = null;
	Long endTime = null;
	Long duration = null;
	Integer priority = null;
	
	String place = null;
	
	
	// filed of this class
	String command;
	
	/** The constructor
	 * @param command - the command passed from the smart bar GUI
	 */
	public SmartBarParseCommand(String command) {
		this.command = command;
		
		// check the date
		/**
		 * Date format: 
		 *  1) dd/[mm|M](/yy(yy)?)?		where M means Jan~Dec, or January ~ December;
		 *  	"/" here can also be " "
		 *  2) today, tomorrow
		 *  3) Mon ~ Sun, or Monday ~ Sunday --> indicates the next day.
		 *  4) next Mon ~ Sun, or Monday ~ Sunday --> indicates the day of next week 
		 */
			// check for 1)
		Pattern regDateFormat_dd_mm_$yy$yy$$_Pattern = Pattern.compile(regDateFormat_dd_$mm$M$_$yy$yy$$ + regWordSpacer,Pattern.CASE_INSENSITIVE);
		Matcher regDateFormat_dd_mm_$yy$yy$$_Matcher = regDateFormat_dd_mm_$yy$yy$$_Pattern.matcher(command);
		if(regDateFormat_dd_mm_$yy$yy$$_Matcher.find()){	// date is in dd/mm/yy format
			dateFormat_dd_mm_$yy$yy$$_Process(startDate, regDateFormat_dd_mm_$yy$yy$$_Matcher.group()); 
			System.out.println(regDateFormat_dd_mm_$yy$yy$$_Matcher.group());
			System.out.println(regDateFormat_dd_mm_$yy$yy$$_Pattern.pattern());
		}else{
			System.out.println("Not found");
		}
		
		// then check the time
		
		
		// then the duration
		
		// then the place
		
		// the priority
		
		// then the deadline
	}
	
	private static int dateParseGetMonthFromText(String month){
		if(month.compareToIgnoreCase("Jan") == 0 || 
			 month.compareToIgnoreCase("January") == 0){
			return Calendar.JANUARY;
		} else if(month.compareToIgnoreCase("Feb") == 0 || 
				 month.compareToIgnoreCase("February") == 0){
			return Calendar.FEBRUARY;
		} else if(month.compareToIgnoreCase("Mar") == 0 || 
				 month.compareToIgnoreCase("March") == 0){
			return Calendar.MARCH;
		} else if(month.compareToIgnoreCase("Apr") == 0 || 
				 month.compareToIgnoreCase("April") == 0){
			return Calendar.APRIL;
		} else if(month.compareToIgnoreCase("May") == 0){ 
			return Calendar.MAY;
		} else if(month.compareToIgnoreCase("Jun") == 0 || 
				 month.compareToIgnoreCase("June") == 0){
			return Calendar.JUNE;
		} else if(month.compareToIgnoreCase("Jul") == 0 || 
				 month.compareToIgnoreCase("July") == 0){
			return Calendar.JULY;
		} else if(month.compareToIgnoreCase("Aug") == 0 || 
				 month.compareToIgnoreCase("August") == 0){
			return Calendar.AUGUST;
		} else if(month.compareToIgnoreCase("Sept") == 0 || 
				 month.compareToIgnoreCase("September") == 0){
			return Calendar.SEPTEMBER;
		} else if(month.compareToIgnoreCase("Oct") == 0 || 
				 month.compareToIgnoreCase("October") == 0){
			return Calendar.OCTOBER;
		} else if(month.compareToIgnoreCase("Nov") == 0 || 
				 month.compareToIgnoreCase("November") == 0){
			return Calendar.NOVEMBER;
		} else if(month.compareToIgnoreCase("Dec") == 0 || 
				 month.compareToIgnoreCase("December") == 0){
			return Calendar.DECEMBER;
		} else{
			return -1;
		}
	}
	
	private static int dateParseGetDayOfWeekFromText(String day) {
		if(day.substring(0, 2).compareToIgnoreCase("Mon") == 0){
			return Calendar.MONDAY;
		} else if(day.substring(0, 2).compareToIgnoreCase("Tue") == 0){
			return Calendar.TUESDAY;
		} else if(day.substring(0, 2).compareToIgnoreCase("Wed") == 0){
			return Calendar.WEDNESDAY;
		} else if(day.substring(0, 2).compareToIgnoreCase("Thu") == 0){
			return Calendar.THURSDAY;
		} else if(day.substring(0, 2).compareToIgnoreCase("Fri") == 0){
			return Calendar.FRIDAY;
		} else if(day.substring(0, 2).compareToIgnoreCase("Sat") == 0){
			return Calendar.SATURDAY;
		} else if(day.substring(0, 2).compareToIgnoreCase("Sun") == 0){
			return Calendar.SUNDAY;
		} else{
			return -1;
		}
		
	}
	
	private static int dateParseGetMonth(String month){
		int monthNum = 0;
		try{
			// if can parse successful, just return the parse value
			monthNum = Integer.parseInt(month);
		} catch (NumberFormatException e) {
			// if it's not integer, then it's text format --> need to translate
			return dateParseGetMonthFromText(month);
		}
		
		return (monthNum<=12)?monthNum:0;	// return 0 on false
	}
	
	private static boolean dateFormat_dd_mm_$yy$yy$$_Process(Calendar date, String dateStr) {
		date = Calendar.getInstance();
		
		String[] dateInfoArr = dateStr.split(regDateSpacer);
		
			// set the day of a month
		date.set(Calendar.DATE, Integer.parseInt(dateInfoArr[0]));
			// set the month of a year
		int monthNum = dateParseGetMonth(dateInfoArr[1]);
		System.out.println("Month Num: " + monthNum);
		if(monthNum == -1){
			return false;	// parsing of the month failed
		} else{
			date.set(Calendar.MONTH,monthNum);
		}
			// set the year, if possible
		int year = 0;
		if(dateInfoArr.length > 2){
			year = Integer.parseInt(dateInfoArr[2]);
			
			if(year < 50){	// form of dd/mm/10 -> should be 2010
				year += 2000;
			}else if(year < 100){	// form of dd/mm/65 -> should be 1965
				year += 1900;
			}
		}else{	// not specified, default to this year
			year = Calendar.getInstance().get(Calendar.YEAR);
		}
		date.set(Calendar.YEAR, year);
		
		return true;
	}
	
//	private static boolean 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// test match here
		String taskStr = "this wednesday";	// test time
		
		
		SmartBarParseCommand test = new SmartBarParseCommand(taskStr);
		test.extractStartTime();
		
//		taskStr = in.nextLine();
//		Scanner in = new Scanner(System.in);
//		while(taskStr.trim().compareToIgnoreCase("exit") != 0){
//			BasicTask task = new BasicTask(taskStr);
//			System.out.println(task);
//			
//			taskStr = in.nextLine();
//		}
	}

	@Override
	public Commands extractCommand() {
		if(command.charAt(0) == '/'){
			return Commands.SEARCH;
		} else if(command.charAt(0) == '#'){
			return Commands.SWITCHLIST;
		} else{
			return Commands.ADD;
		}
	}

	@Override
	public Long extractStartDate() {
		return (startDate != null)?startDate.getTimeInMillis()/1000:null;
	}

	@Override
	public Long extractStartTime() {
		Long startTime = null;
		
		Pattern regTimePointAmPmPattern = Pattern.compile(regTimePointAmPm,Pattern.CASE_INSENSITIVE);
		Matcher regTimePointAmPmMatcher = regTimePointAmPmPattern.matcher(command);
		
		if(regTimePointAmPmMatcher.find()){
			System.out.println(regTimePointAmPmMatcher.group());
		} else{
			System.out.println("Output.");
		}
		
		// code for getting user input to test
		
		
		return startTime;
	}

	@Override
	public Long extractEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long extractEndTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long extractDuration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractPlace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] extractListName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long extractDeadline() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long extractBeforeTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long extractAfterTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer extractPriority() {
		// TODO Auto-generated method stub
		return null;
	}

}
