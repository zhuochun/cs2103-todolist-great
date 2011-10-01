package cs2103.t14j1.logic.smartbar;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.templates.GlobalTemplateVariables.WordSelection;
import org.eclipse.ui.SubActionBars;
import org.omg.CORBA.DynAnyPackage.Invalid;

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
 * @author SongYY
 * A time wrapper for the time
 * Creating this wrapper for internal use now; it can be move to the task
 *  package if others found it useful
 */
class Time{
	private Long time = null;
	Time(Long time){
		this.time = time;
	}
	
	public void setTime(Long time){
		this.time = time;
	}
	
	public Long getTime(){
		return this.time;
	}
	
	public int compareTo(Time b){
		return (int)(this.getTime() - b.getTime());
	}
}

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
 *	newCommend.[the properity you want to get]
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
			"([12][0-9]|3[01]|(0)?[1-9])(st|nd|rd|th)?" + regDateSpacer + regMonthText + "(" + regDateSpacer + "(19|20)\\d\\d)?";
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
	
	private static final String regDateOverallFormat = 
			"(" + regDateFormat_dd_$mm$M$_$yy$yy$$ + "|" + regDateFormat_order_weekD + 
			"|" + regDateFormat_today_tomorrow + ")";
	
	private static final String regDateTimeOverallFormat = 
			"(" + "(" + regDateOverallFormat + "((" + regWordSpacer + ")?" + regTimeFormat + ")?)|((" +
			regDateOverallFormat+ "(" + regWordSpacer + ")?)?" + regTimeFormat + ")|((" + 
			regTimeFormat + "(" + regWordSpacer + ")?)?" + regDateOverallFormat + ")|(" +
			regTimeFormat + "(" +  regDateOverallFormat + "(" + regWordSpacer + ")?)?))";
	
	private static final String regDurationFormat = 
			"(for(\\ )+[\\d]+(\\ )?" + regTimeUnit + ")";
	private static final String regPlaceFormat = 
		"(@[\\w]+|@\\([.]+\\))";	// format: @ + word; or: @ + (words)
	private static final String regPriorityFormat = "(![123])";
	private static final String regListFormat = "(#[\\w]+|#\\([.]+\\))";
	
	// @endgroup regex
	
	// basic properities 
	private Calendar startDate = null;
	private Calendar endDate = null;
	private Time startTime = null;
	private Time endTime = null;
	private Time duration = null;
	private Priority priority = null;
	private String place = null;
	private Commands commandType = null;
	
	private String taskTitle = null;
	
	// filed of this class
	private String commandStr;
	private String list;
	private Time deadlineTime;
	private Calendar deadlineDate;
	private Calendar beforeDate;
	private Time beforeTime;
	private Time afterTime;
	private Calendar afterDate;
	
	
	private void outputErr(String msg){
		System.err.println("Command: " + ((this.commandStr == null)?"Not Specified.":this.commandStr));
		try{
			// throw new exception for print stack
			throw new Exception(msg);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/* The constructor
	 * @param command - the command passed from the smart bar GUI
	 * @throws Exception */ 
	public ParseCommand(String command){
		// set the time zone to
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-0"));
		
		// initiation
		this.commandStr = command;
		this.startTime = new Time(null);
		this.endTime = new Time(null);
		this.duration = new Time(null);
		this.beforeTime = new Time(null);
		this.afterTime = new Time(null);
		this.deadlineTime = new Time(null);
		
		/**
		 * An overall matcher for date&time
		 */
		Pattern regDateTimePointOverallPattern = Pattern.compile(
				regWordSpacer + regDateTimeOverallFormat + regWordSpacer,Pattern.CASE_INSENSITIVE);
		Matcher regDateTimePointOverallMatcher = regDateTimePointOverallPattern.matcher(command);
		
		/**
		 * Period Format:
		 *  [any date/time] ~ [any date/time]
		 */
		Pattern regTimePeriodPattern = Pattern.compile(
				regWordSpacer + regDateTimeOverallFormat + "(\\ )?~(\\ )?" + regDateTimeOverallFormat + regWordSpacer,
				Pattern.CASE_INSENSITIVE);
		Matcher regTimePeriodMatcher = regTimePeriodPattern.matcher(command);
		
		/**
		 * Duration Format:
		 *  (for) + number + [time unit]
		 */
		Pattern regDurationFormatPattern = Pattern.compile(
				regWordSpacer + regDurationFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		Matcher regDurationFormatMatcher = regDurationFormatPattern.matcher(command);
		
		/** 
		 * Place Format:
		 *  @ + word
		 *  @ + ( + words + )
		 */
		Pattern regPlaceFormatPattern = Pattern.compile(
				regWordSpacer + regPlaceFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		Matcher regPlaceFormatMatcher = regPlaceFormatPattern.matcher(command);
		
		/**
		 * Deadline Format:
		 *  by + [space] + [any time point]
		 */
		Pattern regDeadlinePattern = Pattern.compile(
				regWordSpacer + "by\\ " + regDateTimeOverallFormat + regWordSpacer,Pattern.CASE_INSENSITIVE);
		Matcher regDeadlineMatcher = regDeadlinePattern.matcher(command);
		
		
		/**
		 * Priority Format:
		 * 	! + [1,2,3]
		 */
		Pattern regPriorityFormatPattern = Pattern.compile(
				regWordSpacer + regPriorityFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		Matcher regPriorityFormatMatcher = regPriorityFormatPattern.matcher(command);
		
		/**
		 * List Format:
		 * 	# + word
		 *  # + ( + words + )
		 */
		Pattern regListFormatPattern = Pattern.compile(
				regWordSpacer + regListFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		Matcher regListFormatMatcher = regListFormatPattern.matcher(command);
		
		// start parsing
		
		
		if(command.charAt(0) == '/'){
			commandType = Commands.SEARCH;
			command = command.substring(1);
		} else if(commandStr.charAt(0) == '#'){
			commandType = Commands.SWITCH;	// this would be used for parsing later
		} else{
			commandType = Commands.ADD_TASK;
		}
		
		String matchedStr = null;
		
		// use this to mark if the period match ([] ~ []) has been performed
		// if performed, the match for start time should not be executed
		boolean periodMatchedProfermed = false;
		
		// before matching the date/time, need to check the period first
		if(regTimePeriodMatcher.find()){
			if(!periodProcess(removeTheLeadingAndTailingWordSpacer(matchedStr = regDateTimePointOverallMatcher.group()))){
				outputErr("Date Parsing Problem: error in parsing the date with format dd/mm|M/yy(yy).");
			}
			periodMatchedProfermed = true;
			removeMatchedString(command, matchedStr);
		} 
		if(regDeadlineMatcher.find()){	// check the deadline first, cuz it's more restrictive
			if(!regDeadlineProcess(removeTheLeadingAndTailingWordSpacer(matchedStr = regDeadlineMatcher.group()))){
				
			}
		} 
		if(!periodMatchedProfermed && regDateTimePointOverallMatcher.find()){	// then try match the date&time
			if(null == (startDate = dateTimeProcess(startTime,
					removeTheLeadingAndTailingWordSpacer(matchedStr = regDateTimePointOverallMatcher.group()),
					null))){
				// got problem here
				outputErr("Date Parsing Problem: error in parsing the date with format dd/mm|M/yy(yy).");
			}
			removeMatchedString(command, matchedStr);
		}
		
		
		// then the duration
		if(regDurationFormatMatcher.find()){
			if(!regDurationFormatProcess(duration,
					removeTheLeadingAndTailingWordSpacer(matchedStr = regDurationFormatMatcher.group()))){
				outputErr("Duration Parsing Problem.");
			}
		}
		removeMatchedString(command, matchedStr);
		
		// then the place 
		if(regPlaceFormatMatcher.find()){
			if(!regPlaceFormatProcess(place,
					removeTheLeadingAndTailingWordSpacer(matchedStr = regPlaceFormatMatcher.group()))){
				outputErr("Place Parsing Problem.");
			}
		}
		removeMatchedString(command, matchedStr);
		
		// the priority
		if(regPriorityFormatMatcher.find()){
			if(!regPriorityFormatProcess(priority,
					removeTheLeadingAndTailingWordSpacer(matchedStr = regPriorityFormatMatcher.group()))){
				outputErr("Priority Parsing Problem. Matched String: " + matchedStr);
			}
		}
		removeMatchedString(command, matchedStr);
		
		// the list TODO: might need to consider the case of multiple lists
		if(regListFormatMatcher.find()){
			if(!regListFormatProcess(list,
					removeTheLeadingAndTailingWordSpacer(matchedStr = regPriorityFormatMatcher.group()))){
				outputErr("DEBUG: List Parsing Problem. Matched String: " + matchedStr);
			}
		}
		removeMatchedString(command, matchedStr);
		
		// what's left would be the task title
		this.taskTitle = command.trim();
		
		// extra parameter for search: before/after time
		if(this.commandType == Commands.SEARCH){
			/**
			 * Before Time Format:
			 *  before + [space] + [any time point]
			 */
			Pattern regBeforeDateTimePattern = Pattern.compile(
					regWordSpacer + "before\\ " + regDateTimeOverallFormat + regWordSpacer,Pattern.CASE_INSENSITIVE);
			Matcher regBeforeDateTimeMatcher = regBeforeDateTimePattern.matcher(command);
			
			/**
			 * Similar to before time foramt
			 */
			Pattern regAfterDateTimePattern = Pattern.compile(
					regWordSpacer + "after\\ " + regDateTimeOverallFormat + regWordSpacer,Pattern.CASE_INSENSITIVE);
			Matcher regAfterDateTimeMatcher = regAfterDateTimePattern.matcher(command);
			
			if(regBeforeDateTimeMatcher.find()){
				String timeDateStr = removeTheLeadingAndTailingWordSpacer(matchedStr = regBeforeDateTimeMatcher.group());
				beforeDate = dateTimeProcess(beforeTime, timeDateStr, null);
			}
			
			if(regAfterDateTimeMatcher.find()){
				String timeDateStr = removeTheLeadingAndTailingWordSpacer(matchedStr = regBeforeDateTimeMatcher.group());
				afterDate = dateTimeProcess(afterTime, timeDateStr, null);
			}
		}
	}
	
	private boolean regDeadlineProcess(String deadlineStr) {
		deadlineDate = dateTimeProcess(deadlineTime, deadlineStr, null);
		return deadlineDate != null;
	}


	private boolean periodProcess(String periodStr) {
		String[] timePoint = periodStr.split("~");
		// eliminate the space. Possible case: 12th Setp ~ 12th Oct, the space between ~ would be removed
		String startStr = removeTheLeadingAndTailingWordSpacer(timePoint[0]);
		String endStr = removeTheLeadingAndTailingWordSpacer(timePoint[1]);
		startDate = dateTimeProcess(startTime, startStr, null);
		endDate = dateTimeProcess(endTime, endStr, startDate);
		
		if(startDate.compareTo(endDate) > 0){
			// when > 0, means endDate is earlier than start date
			return false;
		} else if((startDate.compareTo(endDate) == 0) && (startTime.compareTo(endTime) > 0)){
			return false;
		}
		
		// after all, specify the duration:
		Long duration = (endDate.getTimeInMillis() - startDate.getTimeInMillis())/1000
			+ endTime.getTime() - startTime.getTime();
		this.duration.setTime(duration); 
		
		return true;
	}


	private Calendar dateTimeProcess(Time time, String timeDateStr, Calendar dateSubstitute){
		Calendar date = null;
		
		// check the date
		/**
		 * Date format: 
		 *  1) dd/[mm|M](/yy(yy)?)?		where M means Jan~Dec, or January ~ December;
		 *  	"/" here can also be " "
		 *  2) today, tomorrow
		 *  3) Mon ~ Sun, or Monday ~ Sunday --> indicates the next day.
		 *  4) next Mon ~ Sun, or Monday ~ Sunday --> indicates the day of next week 
		 */
		Pattern regDateFormat_dd_mm_$yy$yy$$_Pattern = Pattern.compile(
				regWordSpacer + regDateFormat_dd_$mm$M$_$yy$yy$$ + regWordSpacer,Pattern.CASE_INSENSITIVE);
		Matcher regDateFormat_dd_mm_$yy$yy$$_Matcher = regDateFormat_dd_mm_$yy$yy$$_Pattern.matcher(timeDateStr);
		
		Pattern regDateFormat_today_tomorrow_Pattern = Pattern.compile(
				regWordSpacer + regDateFormat_today_tomorrow + regWordSpacer);	// this is case sensitive
		Matcher regDateFormat_today_tomorrow_Matcher = regDateFormat_today_tomorrow_Pattern.matcher(timeDateStr);
		
		Pattern regDateFormat_order_weekD_Pattern = Pattern.compile(
				regWordSpacer + regDateFormat_order_weekD + regWordSpacer,Pattern.CASE_INSENSITIVE);	// this is case sensitive
		Matcher regDateFormat_order_weekD_Matcher = regDateFormat_order_weekD_Pattern.matcher(timeDateStr);
		
		/**
		 * Time Format:
		 *  1) hh:mm:ss (am|pm)?
		 *  2) hh:mm (am|pm)?
		 *  3) hh (am|pm)
		 */
		Pattern regTimeFormatAllPattern = Pattern.compile(
				regWordSpacer + regTimeFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		Matcher regTimeFormatAllMatcher = regTimeFormatAllPattern.matcher(timeDateStr);
		
		
		
		String matchedStr = null;
		
		if(regDateFormat_dd_mm_$yy$yy$$_Matcher.find()){	// date is in dd/mm/yy format
			if(null == (date = dateFormat_dd_mm_$yy$yy$$_Process(
					matchedStr = removeTheLeadingAndTailingWordSpacer(regDateFormat_dd_mm_$yy$yy$$_Matcher.group())))){
				// got problem here
				outputErr("Date Parsing Problem: error in parsing the date with format dd/mm|M/yy(yy).");
			}
		}else if(regDateFormat_today_tomorrow_Matcher.find()){
			if(!regDateFormat_today_tomorrow_Process(date,
					removeTheLeadingAndTailingWordSpacer(matchedStr = regDateFormat_today_tomorrow_Matcher.group()))){
				outputErr("Date Parsing Problem: error in parse the date with format today|tomorrow");
			}
		} else if(regDateFormat_order_weekD_Matcher.find()){
			if(!regDateFormat_order_weekD_Process(date,
					matchedStr = removeTheLeadingAndTailingWordSpacer(regDateFormat_order_weekD_Matcher.group()))){
				outputErr("Date Parsing Problem: error in parsing the (this|next) day_of_week ");
			}
		}
		removeMatchedString(timeDateStr,matchedStr);
		
		// then check the time
		if(regTimeFormatAllMatcher.find()){
			if(!regTimeFormatProcess(time,
					removeTheLeadingAndTailingWordSpacer(matchedStr = regTimeFormatAllMatcher.group()))){
				outputErr("Time Parsing Problem.");
			}
			if(date == null){
				if(dateSubstitute != null){
					date = (Calendar) dateSubstitute.clone();
				} else{
					regDateFormat_today_tomorrow_Process(date,"today");
				}
			}
		}
		
		return date;
	}


	/** For a given "matchedStr", remove this string from the "stringToBeOperated"
	 * @param stringToBeOperated
	 * @param matchedStr
	 */
	private static void removeMatchedString(String stringToBeOperated, String matchedStr) {
		if(matchedStr != null && stringToBeOperated != null){
			String replacedWith = "";
			if(Character.isSpaceChar(matchedStr.charAt(0)) && Character.isSpaceChar(matchedStr.charAt(matchedStr.length() - 1))){
				replacedWith = " ";
			}
			stringToBeOperated = stringToBeOperated.replace(matchedStr, replacedWith);	// remove the matched String
			matchedStr = null;
		}
	}


	private boolean regPlaceFormatProcess(String place, String string) {
		// remove the leading @
		if(string.charAt(0) == '@'){
			string = string.substring(1);
		}
		
		// remove the leading and tailing bracket
		if(string.charAt(0) == '('){
			string = string.substring(1);
		}
		if(string.charAt(string.length() -1) == ')'){
			string = string.substring(0, string.length() - 1);
		}
		
		place = string;
		
		return true;
	}


	private static boolean regListFormatProcess(String list, String listStr) {
		listStr = listStr.substring(1);	// simply leave out the first character (#)
		if(listStr.charAt(0)=='('){
			listStr = listStr.substring(1);
		}
		if(listStr.charAt(listStr.length()-1) == '('){
			listStr = listStr.substring(0,listStr.length()-1);
		}
		list = listStr;
		return true;
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
	private static boolean regDurationFormatProcess(Time duration, String durationStr) {
		String[] durationParts = durationStr.split("\\ ");
		long base = Long.parseLong(durationParts[1]);
		
		// then try to tell the duration information
		String unit = durationParts[2];
		if(unit.substring(0, 3).compareToIgnoreCase("sec") == 0){
			duration.setTime(base);
		} else if(unit.substring(0, 3).compareToIgnoreCase("min") == 0){
			duration.setTime(base * 60);
		} else if(unit.substring(0, 1).compareToIgnoreCase("h") == 0){
			duration.setTime(base * 3600);
		} else if(unit.substring(0, 3).compareToIgnoreCase("day") == 0){
			duration.setTime(base * 3600 * 24);
		} else {
			return false;
		}
		
		return true;
	}

	private boolean regTimeFormatProcess(Time timeObj, String timeStr) {
		// first level: separate by :
		Long time = new Long(0); 	// initialization
		// capture the [a|p]m. 
		// This tag is no longer useful after this operation
		if(timeStr.contains("pm")){
			time += 3600 * 12;	// the afternoon
		}
		
		// the case when "timeStr contains "at"
		if(timeStr.substring(0, 3).compareToIgnoreCase("at ") == 0){
			timeStr = timeStr.substring(3);
		}
		// remove the am/pm tag
		String[] purifiedTime = timeStr.split("[a|p]m");
		timeStr = purifiedTime[0];
		
		String[] timeOptions = timeStr.split(":");
		
		long amplifier = 3600;
		try{
			for(int i=0;i<timeOptions.length;i++,amplifier/=60){
				time += Long.parseLong(timeOptions[i].trim()) * amplifier;
			}
		} catch(NumberFormatException e){
			outputErr("Error in processing time format: Num Handling");
			e.printStackTrace();
		}
		
		timeObj.setTime(time);
		
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

	/**
	 * For a given String specifying the month, change it to the "month" of int
	 * according to the format of Calendar
	 * @param month
	 * @return
	 */
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
	
	/** For a given String specifying the day of a week, change it to the int 
	 *  with Calendar type 
	 * @param day
	 * @return
	 */
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
	
	/** 
	 * For a given string, if the first letter/ last letter is not word string
	 * (a-z, A-Z), then remove it
	 * @param inStr
	 * @return
	 */
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
	
	private Calendar dateFormat_dd_mm_$yy$yy$$_Process(String dateStr) {
		Calendar date = Calendar.getInstance();
		date.clear();
		
		String[] dateInfoArr = dateStr.split(regDateSpacer);
		
			// set the day of a month
		String dayStr = dateInfoArr[0];
		
		// eliminate the "st/nd/rd/th" which specifies the order
		dayStr = (Character.isLetter(dayStr.charAt(2))?dayStr.substring(0, dayStr.length()-2):dayStr);
		date.set(Calendar.DATE, Integer.parseInt(dayStr));
			// set the month of a year
		int monthNum = dateParseGetMonth(dateInfoArr[1]);

		if(monthNum == -1){
			return null;	// parsing of the month failed
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
		
		return date;
	}
	

	
	public static void main(String[] args) throws Exception {
		// test match here
		String taskStr = "Jogging on 12th Sept at 4:00 am for 2 hours @Climenti";	// test time
		
//		System.out.println(taskStr.matches(regTimeFormat));
		
		ParseCommand test = new ParseCommand(taskStr);
		System.out.println(test.extractStartDate());
	
		
//		taskStr = in.nextLine();
//		Scanner in = new Scanner(System.in);
//		while(taskStr.trim().compareToIgnoreCase("exit") != 0){
//			BasicTask task = new BasicTask(taskStr);
//			System.out.println(task);
//			
//			taskStr = in.nextLine();
//		}
	}


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
	 * This function would return the type of the Command for the Input String
	 * @param commandStr
	 * @return
	 */
	public Commands extractCommand() {
		return (commandType==null?Commands.INVALID:commandType);
	}
	
	/**
	 * @return
	 *  Get the task name; it has removed the properties of the task.
	 *  For example, if the input String is:
	 *  @group
	 *  	Meeting with Shubham tomorrow 3pm @(PGP Canteen) !3
	 *  @endgroup
	 *   it would return Meeting with Shubham
	 */
	public String extractTaskName() {
		return commandStr; // if TaskTitle is not found, return null
	}

	/**
	 * Songyy's Note: personally I think it would be better to return 
	 * 	@code null @endcode
	 * when the list doesn't exist -- so that it would be more flexible
	 * @return
	 * 	the list name; if not specified, return the default inbox
	 */
	public String extractListName() {
		// if list name is not specified, use inbox list
		return (list==null)?list:cs2103.t14j1.storage.TaskLists.INBOX;
	}

	public Priority extractPriority() {
		return (priority == null)?priority:Priority.NORMAL; // if priority is not specified, use normal
	}

	/**
	 * 
	 * @return
	 */
	public Long extractStartTime() {
		return startTime.getTime();
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
			return startDate.getTime();
		}
	}
	
	/**
	 * @return
	 *  The number of seconds for the duration.
	 *  @code null @endcode on doesn't exist
	 */
	public Long extractDuration(){
		return duration.getTime();
	}
	
	/**
	 * Currently not supported in this phase
	 * @return
	 */
	public Date extractEndDate() {
		if(endDate == null){
			return null;
		}
		return endDate.getTime();
	}
	
	public Long extractEndTime() {
		return endTime.getTime();
	}
	
	public Date extractDeadlineDate() {
		return deadlineDate==null?null:deadlineDate.getTime();
	}
	
	public Long extractDeadlineTime(){
		return deadlineTime.getTime();
	}
	
	public Date extractBeforeDate() {
		return beforeDate==null?null:beforeDate.getTime();
	}
	
	public Long extractBeforeTime(){
		return beforeTime.getTime();
	}
	
	public Date extractAfterDate() {
		return afterDate==null?null:afterDate.getTime();
	}
	
	public Long extractAfterTime(){
		return afterTime.getTime();
	}
	
}

