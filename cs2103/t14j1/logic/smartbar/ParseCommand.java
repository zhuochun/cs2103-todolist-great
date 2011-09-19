package cs2103.t14j1.logic.smartbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.internal.C;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.logic.ExtractParsedCommand;

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
public class ParseCommand {
/*
	// @group regex: regular expressions for match
	
		// to differ between words -- a spacer is not a digit character or alphabet
	private static final String regWordSpacer = "([^\\d\\w]|[$^])";
	private static final String regDateSpacer = "[,-/. ]";

		// regular expression for matching the time
	private static final String regTimePointAmPm = // match the 5am/pm, 5 am/pm, or 5:00 am/pm. 
			"(1[012]|\\d)(:[0-5]\\d){0,2}((\\ )?[ap]m)";
	private static final String regTimePoint24H = 
			"([01]?\\d|2[0-3])(:[0-5]\\d){0,2}";
	private static final String regTimePointInteger =
			"(1[012]|[1-9])([AP]M)";
	private static final String regTimeFormat = // can be either 24 hour, or am/pm
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
	
	private static final String regDateFormat_order_weekD = 
			"((" + regDayOrder + regWordSpacer + ")?" + regWeekText + ")";
	
	private static final String regDateFormat_today_tomorrow = 
			"(today|tomorrow)";
	
		// the date format for mm/dd/yy; leave it here for the possible use in the future
	private static final String regDateFormat_mm_dd_yy =
			"(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])([- /.](19|20)\\d\\d)?";
	
		
		// regex for checking time 
	
	
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
	 * @throws Exception 
	 */
	/*
	public SmartBarParseCommand(String command) throws Exception {
		// set the time zone to 
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-0"));
		
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
	
	/*
		Pattern regDateFormat_dd_mm_$yy$yy$$_Pattern = Pattern.compile(
				regWordSpacer + regDateFormat_dd_$mm$M$_$yy$yy$$ + regWordSpacer,Pattern.CASE_INSENSITIVE);
		Matcher regDateFormat_dd_mm_$yy$yy$$_Matcher = regDateFormat_dd_mm_$yy$yy$$_Pattern.matcher(command);
		
		Pattern regDateFormat_today_tomorrow_Pattern = Pattern.compile(
				regWordSpacer + regDateFormat_today_tomorrow + regWordSpacer);	// this is case sensitive
		Matcher regDateFormat_today_tomorrow_Matcher = regDateFormat_today_tomorrow_Pattern.matcher(command);
		
		Pattern regDateFormat_order_weekD_Pattern = Pattern.compile(
				regDateFormat_order_weekD,Pattern.CASE_INSENSITIVE);	// this is case sensitive
		Matcher regDateFormat_order_weekD_Matcher = regDateFormat_order_weekD_Pattern.matcher(command);
		
		Pattern regTimeFormatAllPattern = Pattern.compile(
				regWordSpacer + regTimeFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		Matcher regTimeFormatAllMatcher = regTimeFormatAllPattern.matcher(command);
		
		
		if(regDateFormat_dd_mm_$yy$yy$$_Matcher.find()){	// date is in dd/mm/yy format
			if(!dateFormat_dd_mm_$yy$yy$$_Process(startDate,
					removeTheLeadingAndTailingWordSpacer(regDateFormat_dd_mm_$yy$yy$$_Matcher.group()))){
				// got problem here.
				throw new Exception("Date Parsing Problem: error in parsing the date with format dd/mm|M/yy(yy)");
			}
		}else if(regDateFormat_today_tomorrow_Matcher.find()){
			if(!regDateFormat_today_tomorrow_Process(startDate,
					removeTheLeadingAndTailingWordSpacer(regDateFormat_today_tomorrow_Matcher.group()))){
				throw new Exception("Date Parsing Problem: error in parse the date with format today|tomorrow");
			}
		} else if(regDateFormat_order_weekD_Matcher.find()){
			if(!regDateFormat_order_weekD_Process(startDate,
					removeTheLeadingAndTailingWordSpacer(regDateFormat_order_weekD_Matcher.group()))){
				throw new Exception("Date Parsing Problem: error in parsing the (this|next) day_of_week ");
			}
		}
		else{
			// TODO: for debug purpose only; would remove it later
			System.out.println("DEBUG: Date Not found");
		}
		
		// then check the time
		if(regTimeFormatAllMatcher.find()){
			if(!regTimeFormatProcess(startTime,
					removeTheLeadingAndTailingWordSpacer(regTimeFormatAllMatcher.group()))){
				throw new Exception("Time Parsing Problem: wrong number format.");
			}
		}
		
		
		// then the duration
		
		// then the place
		
		// the priority
		
		// then the deadline
	}
	
	private boolean regTimeFormatProcess(Long time,
			String timeStr) {
		// first level: separate by :
		time = (long) 0; 	// initialization
		System.out.println(timeStr);
		System.out.println(timeStr.contains("pm"));
		// capture the [a|p]m. 
		// This tag is no longer useful after this operation
		if(timeStr.contains("pm")){
			time += 3600 * 12;	// the afternoon
			System.out.println(timeStr);
		}
		String[] purifiedTime = timeStr.split("[a|p]m");
		timeStr = purifiedTime[0];
		
		String[] timeOptions = timeStr.split(":");
		
		long amplifier = 3600;
		try{
			for(int i=0;i<timeOptions.length;i++,amplifier/=60){
				time += Long.parseLong(timeOptions[i]) * amplifier;
			}
		} catch(NumberFormatException e){
			return false;
		}
		
		System.out.println(time);
		
		return true;
	}

	private static boolean regDateFormat_order_weekD_Process(Calendar date, String weekDStr) {
		date = Calendar.getInstance();
		
		// spacer is here
		String[] dayInfo = weekDStr.split(regWordSpacer);
		
		
		
		int dayOfTheWeek = dateParseGetDayOfWeekFromText(weekDStr);
		int currentDay = date.get(Calendar.DAY_OF_WEEK);
		
		if(currentDay == -1){	// failure in day parsing
			return false;
		}
		
		if(currentDay > dayOfTheWeek){	// indicating the day of next week
			date.set(Calendar.DAY_OF_WEEK_IN_MONTH, date.get(Calendar.DAY_OF_WEEK_IN_MONTH)+1);
			date.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
		} else{	// indicating this week. if on the same date, it's still this week
			date.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
		}
		return true;
	}

	private boolean regDateFormat_today_tomorrow_Process(Calendar date, String today_tomorrow) {
		date = Calendar.getInstance();
		if(today_tomorrow.compareTo("today") == 0){
			// do nothing here
		} else if(today_tomorrow.compareTo("tomorrow") == 0){
			date.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH) + 1);
		} else{
			return false;
		}
		
		return true;
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
	
	private static String removeTheLeadingAndTailingWordSpacer(String inStr){
		// the leading word spacer
		if(inStr.substring(0, 1).matches(regWordSpacer)){
			inStr = inStr.substring(1);
		}
		
		// the tailing word spacer
		if(inStr.substring(inStr.length()-1).matches(regWordSpacer)){
			inStr = inStr.substring(0, inStr.length()-1);
		}
		
		return inStr;
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
		date = Calendar.getInstance();date.clear();
		
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
	 * @throws Exception 
	 */
	/*
	public static void main(String[] args) throws Exception {
		// test match here
		String taskStr = "this Wednesday 4:00 pm";	// test time
		
//		System.out.println(taskStr.matches(regTimeFormat));
		
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

	/*
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
		
		Pattern regTimePointAmPmPattern = Pattern.compile(
				regTimePointAmPm + "(^|$)",Pattern.CASE_INSENSITIVE);
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

	@Override
	public String extractTaskTitle() {
		// TODO Auto-generated method stub
		return null;
	}
*/
}
