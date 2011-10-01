package cs2103.t14j1.logic.smartbar;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.storage.Priority;

/*
Functions completed:
	extractTaskName() // simply return the string passed in
	extractListName() // only support one list now
	extractPriority()  // as Zhuochun requested, 1 measn High, 3 means low
	extractStartDate() // returning a Date class as Zhuochun requested; personally I prefer Calendar class. or simply a Long integer with the meaning we've defined;
	extractStartTime() // return an Long, is the number of seconds passed since 0:00.
	extractDuration() // return an Long.
	Function Partially Completed:
	extractCommand:
	I've only done for search task, switch task, and add task; cuz we didn't define syntax for the rest of the commands
	Function not implemented:
	extractEndDate: don't know how we're doing with this now...
*/

/**
 * this class would parse the command from SmartBar.
 *  
 * To use it, you would create an intense of it using its constructor, with the
 * passed command (String) as a parameter.
 *  
 * Example:
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

	// @group regex: regular expressions for match
	
		// to differ between words -- a spacer is not a digit character or alphabet
	private static final String regWordSpacer = "([^\\d\\w]|(^)|($))";
	private static final String regNonSpaceWordSpacer = "([^\\d\\w\\ ]|(^)|($))";
	private static final String regDateSpacer = "[,-/. ]";

		// regular expression for matching the time
	private static final String regTimePointAmPm = // match the 5am/pm, 5 am/pm, or 5:00 am/pm. 
			"(1[012]|\\d)(:[0-5]\\d){0,2}((\\ )?[ap]m)";
	private static final String regTimePoint24H = 
			"([01]?\\d|2[0-3])(:[0-5]\\d){0,2}";
	private static final String regTimeFormat = // can be either 24 hour, or am/pm
			"((at" + regWordSpacer + ")?" + regTimePointAmPm + "|" + regTimePoint24H + ")";
	
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
			"((on" + regWordSpacer + ")?(" + regDayOrder + regWordSpacer + ")?" + regWeekText + ")";
	
	private static final String regDateFormat_today_tomorrow = 
			"(today|tomorrow)";
	
		// the date format for mm/dd/yy; leave it here for the possible use in the future
	private static final String regDateFormat_mm_dd_yy =
			"(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])([- /.](19|20)\\d\\d)?";
	private static final String regTimeUnit = 
			"((hour(s)?|h)|(minute(s)?|min)|(second(s)?|sec)|(day(s)?))";
	
	private static final String regDurationFormat = 
			"(for(\\ )+[\\d]+(\\ )?" + regTimeUnit + ")";
	private static final String regPlaceFormat = 
		"((on|at|in)(\\ )+[\\w\\ ]" + regNonSpaceWordSpacer + ")";
	private static final String regPriorityFormat = "(![123])";
	private static final String regListFormat = "(#[\\w]+)";
	
	// @endgroup regex
	
	
	// @group regex-match
	
	// @end group regex-match
	
	// basic information 

	private Calendar startDate = null;
	private Long startTime = null;
	private Long endTime = null;
	private Long duration = null;
	private Priority priority = null;
	
	private String place = null;

	// filed of this class
	private String command;
	private String list;
	
	
	
	/* The constructor
	 * @param command - the command passed from the smart bar GUI
	 * @throws Exception */ 
	public ParseCommand(String command) throws Exception {
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
		
		Pattern regDurationFormatPattern = Pattern.compile(
				regWordSpacer + regDurationFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		Matcher regDurationFormatMatcher = regDurationFormatPattern.matcher(command);
		
		// TODO: need to further consider this part
		Pattern regPlaceFormatPattern = Pattern.compile(
				regWordSpacer + regPlaceFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		Matcher regPlaceFormatMatcher = regPlaceFormatPattern.matcher(command);
		
		Pattern regPriorityFormatPattern = Pattern.compile(
				regWordSpacer + regPriorityFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		Matcher regPriorityFormatMatcher = regPriorityFormatPattern.matcher(command);
		
		Pattern regListFormatPattern = Pattern.compile(
				regWordSpacer + regListFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		Matcher regListFormatMatcher = regListFormatPattern.matcher(command);
		
		
		String matchedStr = null;
		if(regDateFormat_dd_mm_$yy$yy$$_Matcher.find()){	// date is in dd/mm/yy format
			if(!dateFormat_dd_mm_$yy$yy$$_Process(startDate,
					matchedStr = removeTheLeadingAndTailingWordSpacer(regDateFormat_dd_mm_$yy$yy$$_Matcher.group()))){
				// got problem here.
				throw new Exception("Date Parsing Problem: error in parsing the date with format dd/mm|M/yy(yy)");
			}
		}else if(regDateFormat_today_tomorrow_Matcher.find()){
			if(!regDateFormat_today_tomorrow_Process(startDate,
					matchedStr = removeTheLeadingAndTailingWordSpacer(regDateFormat_today_tomorrow_Matcher.group()))){
				throw new Exception("Date Parsing Problem: error in parse the date with format today|tomorrow");
			}
		} else if(regDateFormat_order_weekD_Matcher.find()){
			if(!regDateFormat_order_weekD_Process(startDate,
					matchedStr = removeTheLeadingAndTailingWordSpacer(regDateFormat_order_weekD_Matcher.group()))){
				throw new Exception("Date Parsing Problem: error in parsing the (this|next) day_of_week ");
			}
		}
		else{
			// TODO: for debug purpose only; would remove it later
			System.out.println("DEBUG: Date Not found");
		}
		
		if(matchedStr != null){
			command = command.replace(matchedStr, "");	// remove the matched String
			matchedStr = null;
		}

		
		// then check the time
		if(regTimeFormatAllMatcher.find()){
			if(!regTimeFormatProcess(startTime,
					matchedStr = removeTheLeadingAndTailingWordSpacer(regTimeFormatAllMatcher.group()))){
				throw new Exception("DEBUG: Time Parsing Problem.");
			}
			if(startDate == null){
				regDateFormat_today_tomorrow_Process(startDate,"today");
			}
		}
		if(matchedStr != null){
			command = command.replace(matchedStr,"");	// remove the matched time
			matchedStr = null;
		}
		
		
		// then the duration
		if(regDurationFormatMatcher.find()){
			if(!regDurationFormatProcess(duration,
					matchedStr = removeTheLeadingAndTailingWordSpacer(regDurationFormatMatcher.group()))){
				throw new Exception("DEBUG: Duration Parsing Problem.");
			}
		}
		if(matchedStr != null){
			command = command.replace(matchedStr, "");
			matchedStr = null;
		}
		
		// then the place 
		// TODO: controversial about how to extract the Place.. should finish this later
		
		
		// the priority
		if(regPriorityFormatMatcher.find()){
			if(!regPriorityFormatProcess(priority,
					matchedStr = removeTheLeadingAndTailingWordSpacer(regPriorityFormatMatcher.group()))){
				throw new Exception("DEBUG: Priority Parsing Problem. Matched String: " + matchedStr);
			}
		}
		if(matchedStr != null){
			command = command.replace(matchedStr, "");
			matchedStr = null;
		}
		
		// the list TODO: might need to consider the case of mutiple lists
		if(regListFormatMatcher.find()){
			if(!regListFormatProcess(list,
					matchedStr = removeTheLeadingAndTailingWordSpacer(regPriorityFormatMatcher.group()))){
				throw new Exception("DEBUG: List Parsing Problem. Matched String: " + matchedStr);
			}
		}
		if(matchedStr != null){
			command = command.replace(matchedStr, "");
			matchedStr = null;
		}
		
		// then the deadline
		
		
		// TODO final test, remove later
		System.out.println(command);
	}
	
	private static boolean regListFormatProcess(String list, String listStr) {
		list = listStr.substring(1);	// simply leave out the first character (#)
		return false;
	}

	/**
	 * @param priority
	 *  the Priority Enum
	 * @param priorityStr
	 *  currently, 1 for Important, 2 for Normal, 3 for Low (defined by Zhuochun)
	 * @return
	 */
	private static boolean regPriorityFormatProcess(Priority priority, String priorityStr) {
		int numPriority = Integer.parseInt(priorityStr.substring(1));
		
		switch(numPriority){
		case 1: priority = Priority.IMPORTANT; break;
		case 2: priority = Priority.NORMAL; break;
		case 3: priority = Priority.LOW; break;
		default: return false;
		}
		
		return true;
	}

	/**
	 * @param duration
	 *  Long -- would store the result into this; unit is second.
	 *  E.g., if the 
	 * @param durationStr
	 *   
	 * @return
	 */
	private static boolean regDurationFormatProcess(Long duration, String durationStr) {
		String[] durationParts = durationStr.split("\\ ");
		long base = Long.parseLong(durationParts[1]);
		
		// then try to tell the duration information
		String unit = durationParts[2];
		if(unit.substring(0, 3).compareToIgnoreCase("sec") == 0){
			duration = base;
		} else if(unit.substring(0, 3).compareToIgnoreCase("min") == 0){
			duration = base * 60;
		} else if(unit.substring(0, 1).compareToIgnoreCase("h") == 0){
			duration = base * 3600;
		} else if(unit.substring(0, 3).compareToIgnoreCase("day") == 0){
			duration = base * 3600 * 24;
		} else {
			return false;
		}
		
		return true;
	}

	private boolean regTimeFormatProcess(Long time, String timeStr) {
		// first level: separate by :
		time = (long) 0; 	// initialization
		// capture the [a|p]m. 
		// This tag is no longer useful after this operation
		if(timeStr.contains("pm")){
			time += 3600 * 12;	// the afternoon
		}
		String[] purifiedTime = timeStr.split("[a|p]m");
		timeStr = purifiedTime[0];
		
		String[] timeOptions = timeStr.split(":");
		
		long amplifier = 3600;
//		try{  /* TODO: Not putting the exception handling here for debugging */
			for(int i=0;i<timeOptions.length;i++,amplifier/=60){
				time += Long.parseLong(timeOptions[i].trim()) * amplifier;
			}
//		} catch(NumberFormatException e){
//			return false;
//		}
		
		
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
		
		return (monthNum<=12)?monthNum:-1;	// return -1 on false
	}
	
	private boolean dateFormat_dd_mm_$yy$yy$$_Process(Calendar date, String dateStr) {
		date = Calendar.getInstance();date.clear();
		
		String[] dateInfoArr = dateStr.split(regDateSpacer);
		
			// set the day of a month
		date.set(Calendar.DATE, Integer.parseInt(dateInfoArr[0]));
			// set the month of a year
		int monthNum = dateParseGetMonth(dateInfoArr[1]);

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

	
	public static void main(String[] args) throws Exception {
		// test match here
		String taskStr = "Jogging on this Wednesday 4:00 am for 2 hours at Climenti";	// test time
		
//		System.out.println(taskStr.matches(regTimeFormat));
		
		ParseCommand test = new ParseCommand(taskStr);
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
	 * 
	 */
	/**
	 **** Zhuochun's Last Words: 
	 * 
	 * Yangyu, the following function names cannot be changed, only modify inside
	 * and return the correct type
	 * 
	 * Zhuochun
	 * 
	 ****
	 * Yangyu's Reply:
	 *   We've only defined the "search task", "switch list", and "add task"
	 *   Therefore I only support these 3 in this phase.
	 * 
	 ****
	 * This function would return the command: 
	 * @param command
	 * @return
	 */
	public Commands extractCommand() {
		/*
		 * check the Commands class for new command
		 * 
		 * zhuochun
		 */ 
		if(command.charAt(0) == '/'){
			return Commands.SEARCH;
		} else if(command.charAt(0) == '#'){
			return Commands.SWITCH_LIST;
		} else{
			return Commands.ADD_TASK;
		}
//		return Commands.INVALID; // if Command is not found, return INVALID
	}
	
	public String extractTaskName() {
		
		return command; // if TaskTitle is not found, return null
	}

	public String extractListName() {
		return (list==null)?list:"inbox"; // if list name is not specified, use inbox list
	}

	public Priority extractPriority() {
		return (priority == null)?priority:Priority.NORMAL; // if priority is not specified, use normal
	}

	public Long extractStartTime() {
		return startTime;
	}
	
	public Date extractStartDate() {
		/* Zhuochun: use DateFormat.strToDate(str) or DateFormat.strToDateLong(str) 
		 * Yangyu's reply: this would not work because: 
		 *   1. the date string may following different format; simply using that would cause Exception
		 *     (for example, we also consider 'tomorrow' as a date, but this cannot be parsed using strToDate)
		 *   2. the date can be extracted in the constructor; no point of using this again 
		 */
		
		/* Because zhuochun don't want the return type to be cahnged to (Long 
		 *   we've discusseed --), nor does Calendar... do I have to use the 
		 *   seemingly depreciated "Date" class here.
		 */
		if(startDate == null){
			return null;
		} else{
			return new Date(startDate.getTimeInMillis());
		}
	}
	
	public Long extractDuration(){
		return duration;
	}
	
	/**
	 * Currently not supported in this phase
	 * @return
	 */
	public Date extractEndDate() {
		// TODO Auto-generated method stub
		
		return null; // if date is not specified, return null
	}

}

