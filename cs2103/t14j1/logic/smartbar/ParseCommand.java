package cs2103.t14j1.logic.smartbar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.TaskLists;
import cs2103.t14j1.taskmeter.reminder.Reminder;

/**
 * @author Song Yangyu
 * A time wrapper for the time
 * Creating this wrapper for internal use now; it can be move to the task
 *  package if others found it useful
 */
class Time implements Comparable<Time>{
	private Long time = null;
	Time(Long time){
		this.time = time;
	}
	
	public void setTime(Long time){
		this.time = time;
	}
	
	public void setTime(Integer time){
		this.time = (long)time;
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
 *  <pre>
 *  {@code
 *  // smartBarStr is the String passed from smart bar GUI
 *  SmartBarParseCommand newCommand = new SmartBarParseCommand(smartBarStr); 
 *	 
 *	// to get the command type
 *	newCommend.[the properity you want to get]
 *	}
 * 	</pre> 	
 * @author Song Yangyu
 */
public class ParseCommand {

	// @group regex: regular expressions for match
	
		// to differ between words -- a spacer is not a digit character or alphabet
	private static final String regWordSpacer = "([^\\d\\w#!]|(^)|($))";
	private static final String regNonSpaceWordSpacer = "([^\\d\\w\\ ]|(^)|($))";
	private static final String regDateSpacer = "[,-/. ]";
	
		// regular expression for matching the time
	private static final String regTimePointAmPm = // match the 5am/pm, 5 am/pm, or 5:00 am/pm. 
			"(1[012]|\\d)(:[0-5]\\d){0,2}((\\ )?[ap]m)";
	private static final String regTimePoint24H = 
			"([01]?\\d|2[0-3])(:[0-5]\\d){1,2}";
	private static final String regTimeFormat = // can be either 24 hour, or am/pm
			"((at" + regWordSpacer + ")?" + "(" + regTimePointAmPm + "|" + regTimePoint24H +")" +  ")";
	
		// regular expression for matching the date
	private static final String regMonthText =
			"(January|Jan|February|Feb|March|Mar|April|Apr|May|June|Jun|July|Jul|" + 
			"August|Aug|September|Sept|October|Oct|November|Nov|December|Dec)";
	private static final String regWeekText =
			"(Monday|Mon|Tuesday|Tue|Wednesday|Wed|Thursday|Thur|Friday|Fri|Saturday|Sat|Sunday|Sun)";
	private static final String regDayOrder = "((this)?|next)";

			// date format
	private static final String regDateFormat_dd_$mm$_$yy$yy$$ = 
			"(([12][0-9]|3[01]|(0)?[1-9])" + regDateSpacer + "(0[1-9]|1[012])(" + 
			regDateSpacer + "(19|20)?\\d\\d)?)";
	private static final String regDateFormat_dd_$M$_$yy$yy$$ = // When month is text, year should be a full string if exist
			"(([12][0-9]|3[01]|(0)?[1-9])(st|nd|rd|th)?" + regDateSpacer + "(\\ )?" +
			regMonthText + "(" + regDateSpacer + "(19|20)\\d\\d)?)";
	private static final String regDateFormat_dd_$mm$M$_$yy$yy$$ = 
			"((on\\ )?(" + regDateFormat_dd_$M$_$yy$yy$$+ "|" + regDateFormat_dd_$mm$_$yy$yy$$ + "))";
	
	private static final String regDateFormat_order_weekD = 
			"((on\\ )?(" + regDayOrder + regWordSpacer + ")?" + regWeekText + ")";
	
	private static final String regDateFormat_today_tomorrow = 
			"(today|tomorrow|tmr)";
	
		// the date format for mm/dd/yy; leave it here for the possible use in the future
	private static final String regDateFormat_mm_dd_yy =
			"(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])([- /.](19|20)\\d\\d)?";
	private static final String regTimeUnit = 
			"((hour(s)?|h)|(minute(s)?|min)|(second(s)?|sec)|(day(s)?))";
	
	private static final String regDateOverallFormat =
			"(" + regDateFormat_dd_$mm$M$_$yy$yy$$ + "|" + regDateFormat_order_weekD + 
			"|" + regDateFormat_today_tomorrow + ")";
	
	private static final String regDateTimeOverallFormat = "(" +
//				regTimeFormat +
//			regDateOverallFormat + regWordSpacer + regTimeFormat + 
			"(" + regDateOverallFormat+ "(" + regWordSpacer + regTimeFormat + ")?)|" + 	// date (time)?
			"(" + regTimeFormat + "("+ regWordSpacer + regDateOverallFormat + ")?)" +  // time (date)?
			")";
	private static final String regDurationFormat = "(for(\\ [\\d]+\\ " + regTimeUnit + ")+)";
	private static final String regReminderAfterTimeFormat = "(in(\\ [\\d]+\\ " + regTimeUnit + ")+)";
	private static final String regPlaceFormat = 
		"((@[\\w]+)|(@\\([^\\)]+\\)))";	// format: @ + word; or: @ + (words)
	private static final String regPriorityFormat = "(![123])";
	private static final String regListFormat = "((#[\\w]+)|(#\\([^\\)]+\\)))";//"((#[\\w]+)|(#\\([.]+\\)))";
	
	
	private static final String regDeleteTaskCmd = "^(delete|del)\\ [\\d]+$";
	private static final String regDeleteListCmd = "^(delete|del)\\ "+ regListFormat + "$";
	private static final String regDisplayTaskCmd = "^(display|dis)\\ [\\d]+";
	
	private static final String regMoveTaskToListCmd = "^(move|mv)\\ [\\d]+\\ (" + regListFormat + "|#)$";
	private static final String regEditTaskCmd = "^(edit)\\ [\\d]+$";	// simply signal an edit
	private static final String regMarkAsCompleteCmd = "(^(done)\\ [\\d]+$)|(^[\\d]+\\ (done)$)";	// Syntax 1: [num] + done;  Syntax 2: done + [num]
	private static final String regSetPriorityCmd = "^[\\d]\\ " + regPriorityFormat + "$";
	private static final String regAddListCmd = "^(add)\\ " + regListFormat + "$";
	private static final String regRenameListCmd = "^(rename)\\ " + regListFormat + "\\ " + regListFormat + "$";
	private static final String regEditListCmd = "^(edit)\\ " + regListFormat + "$";
	private static final String regReminderGeneralCmd = "^(remind)\\ [\\d]+\\ .+";
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
	
	// for rename list
	private String newListName = null;
	
	// fields of this class
	private String commandStr;
	private String list;
	private Time deadlineTime;
	private Calendar deadlineDate;
	private Calendar searchBeforeDate;
	private Time searchBeforeTime;
	private Time searchAfterTime;
	private Calendar searchAfterDate;
	private Reminder reminderType;
	private Calendar reminderTime;
	
	// some magic string used in defining the property
	private static final int EARLIEST_TIME	= 0;
	private static final int LATEST_TIME	= 1;
	private static final int CURRENT_TIME	= 2;
	private static final int NO_CHANGE = 3;
	
	
	
	// fields for other commands
	private Integer taskNum = null;
		
	private static void outputErr(String msg){
		try{
			// throw new exception for print stack
			throw new Exception(msg);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @param reg
	 * 	-- the regular expression used to match
	 * @return
	 * 	-- the matched string; null on no match
	 */
	private static String matcherMatched(String reg,String matchStr,boolean ignoreCase){
		Pattern regPattern = Pattern.compile(
				reg,
				ignoreCase?Pattern.CASE_INSENSITIVE:0);
		Matcher regMatcher = regPattern.matcher(matchStr);
		return regMatcher.find()?regMatcher.group():null;
	}
	
	
	/** The constructor
	 * @param command - the command passed from the smart bar GUI
	 */
	public ParseCommand(String command){
		// set the time zone to
//		TimeZone.setDefault(TimeZone.getTimeZone("GMT-0"));
		
		// initiation
		this.commandStr = command;
		this.startTime = new Time(null);
		this.endTime = new Time(null);
		this.duration = new Time(null);
		this.searchBeforeTime = new Time(null);
		this.searchAfterTime = new Time(null);
		this.deadlineTime = new Time(null);
		
		/**
		 * An overall matcher for date&time
		 */
		Pattern regDateTimePointOverallPattern = Pattern.compile(
				regWordSpacer + regDateTimeOverallFormat + regWordSpacer,Pattern.CASE_INSENSITIVE);
		
		/**
		 * Period Format:
		 *  [any date/time] ~ [any date/time]
		 */
		Pattern regTimePeriodPattern = Pattern.compile(
				regWordSpacer + regDateTimeOverallFormat + "(\\ )?[\\-\\~](\\ )?" + regDateTimeOverallFormat + regWordSpacer,
				Pattern.CASE_INSENSITIVE);
		
		/**
		 * Duration Format:
		 *  (for) + number + [time unit]
		 */
		Pattern regDurationFormatPattern = Pattern.compile(
				regWordSpacer + regDurationFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		
		/**
		 * Place Format:
		 *  @ + word
		 *  @ + ( + words + )
		 */
		Pattern regPlaceFormatPattern = Pattern.compile(
				regWordSpacer + regPlaceFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		
		/**
		 * Deadline Format:
		 *  by + [space] + [any time point]
		 */
		Pattern regDeadlinePattern = Pattern.compile(
				regWordSpacer + "by\\ " + regDateTimeOverallFormat + regWordSpacer,Pattern.CASE_INSENSITIVE);
		
		/**
		 * Priority Format:
		 * 	! + [1,2,3]
		 */
		Pattern regPriorityFormatPattern = Pattern.compile(
				regWordSpacer + regPriorityFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		
		/**
		 * List Format:
		 * 	# + word
		 *  # + ( + words + )
		 */
		Pattern regListFormatPattern = Pattern.compile(
				regWordSpacer + regListFormat + regWordSpacer, Pattern.CASE_INSENSITIVE);
		
		// then the command matching
		Pattern regDeleteListCmdPattern = Pattern.compile(regDeleteListCmd,Pattern.CASE_INSENSITIVE);
		Matcher regDeleteListCmdMatcher = regDeleteListCmdPattern.matcher(commandStr);
		
		// start parsing
		
		String matchedStr = null;
		
		if(commandStr == null || commandStr.length() < 1){
			commandType = Commands.INVALID;
			return;
		}
		else if(commandStr.charAt(0) == '/'){	// search
			commandType = Commands.SEARCH;
			command = command.substring(1);
			commandStr = command;
		} else if((		// switch list
				matcherMatched("^" + regListFormat + "$", commandStr, true)) != null){
			commandType = Commands.SWITCH_LIST;	// this would be used for parsing later
			this.list = regListFormatProcess(commandStr);
			return;
		} else if(commandStr.trim().length() == 1 && commandStr.charAt(0) == '#'){
			commandType = Commands.SWITCH_LIST;
			this.list = TaskLists.INBOX;	// default to inbox
			return;
		} else if(commandStr.trim().compareToIgnoreCase("exit") == 0){	// exit
			//return Commands.
			commandType = Commands.EXIT;
			return;
		} else if(matcherMatched(regDeleteTaskCmd, commandStr, true) != null){		// delete task
			commandType = Commands.DELETE_TASK;
			String delParams[] = commandStr.split("\\ ");
			taskNum = Integer.parseInt(delParams[1]);
			return;
		} 
		// when it's to delete a list; list would be handled by the task
		else if(regDeleteListCmdMatcher.find()){	// delete list
			commandType = Commands.DELETE_LIST;
		} else if((matcherMatched(regDisplayTaskCmd, commandStr, true))!=null){	// display task
			commandType = Commands.DISPLAY_TASK;
			String disParams[] = commandStr.split("\\ ");
			taskNum = Integer.parseInt(disParams[1]);
			return;
		} else if(commandStr.compareToIgnoreCase("dis") == 0 || 
				commandStr.compareToIgnoreCase("display") == 0){	// display list 
			commandType = Commands.DISPLAY_LISTS;
			return;
		} else if((	// move task
				matcherMatched(regMoveTaskToListCmd, commandStr, true))!=null){
			commandType = Commands.MOVE_TASK;
			String params[] = commandStr.split("\\ ");
			taskNum = Integer.parseInt(params[1]);	// task num
			this.list = regListFormatProcess(params[2]);		// list name
			return;
		} else if((	// mark as complete
				matcherMatched(regMarkAsCompleteCmd, commandStr, true))!=null){
			this.commandType = Commands.MARK_COMPLETE;
			String params[] = commandStr.split("\\ ");
			if(params[0].compareToIgnoreCase("done") == 0){	// done [num]
				taskNum = Integer.parseInt(params[1]);
			} else{	// [number] done
				taskNum = Integer.parseInt(params[0]);
			}
			return;
		} else if((	// edit task : edit + [taskNum]
				matcherMatched(regEditTaskCmd, commandStr, true))!= null){
			this.commandType = Commands.EDIT_TASK;
			String params[] = commandStr.split("\\ ");
			taskNum = Integer.parseInt(params[1]);
			return;
		} else if(	// mark priority
				matcherMatched(regSetPriorityCmd, commandStr, true)!=null){
			this.commandType = Commands.MARK_PRIORITY;
			String params[] = commandStr.split("\\ ");
			this.taskNum = Integer.parseInt(params[0]);
			this.priority = regPriorityFormatProcess(params[1]);
			return;
		} else if(	// regAddListCmd
				matcherMatched(regAddListCmd, commandStr, true)!=null){
			this.commandType = Commands.ADD_LIST;
			String params[] = commandStr.split("\\ ");
			this.list = regListFormatProcess(params[1]);
			return;
		} else if(	// rename list
				matcherMatched(regRenameListCmd, commandStr, true)!=null){
			this.commandType = commandType.RENAME_LIST;
			String params[] = commandStr.split("\\ ");
			this.list = regListFormatProcess(params[1]);
			this.newListName = regListFormatProcess(params[2]);
			return;
		} else if(	// edit list
				matcherMatched(regEditListCmd, commandStr, true)!=null){
			this.commandType = commandType.EDIT_LIST;
			String params[] = commandStr.split("\\ ");
			this.list = regListFormatProcess(params[1]);
			return;
		}
		else if( // add task
				(commandStr.length() > 3)
				&& commandStr.substring(0, 4).compareToIgnoreCase("add ") == 0){
			commandStr = commandStr.substring(4);
			commandType = Commands.ADD_TASK;
		} else if(	// reminder TODO: not complete yet
				matcherMatched(regReminderGeneralCmd, commandStr, true)!=null){
			String stripedOut = commandStr.substring(commandStr.indexOf(' ')+1);
			String taskNStr = stripedOut.substring(0,stripedOut.indexOf(' '));
			stripedOut = stripedOut.substring(stripedOut.indexOf(' ')+1).trim();
			// get the task id
			this.taskNum = Integer.parseInt(taskNStr);
			this.commandType = Commands.ADD_REMINDER;
			
			if(stripedOut.compareToIgnoreCase("start") == 0){
				this.reminderType = Reminder.START;
			} else if(stripedOut.compareToIgnoreCase("end") == 0){
				this.reminderType = Reminder.END;
			} else if(stripedOut.compareToIgnoreCase("deadline") == 0){
				this.reminderType = Reminder.DEADLINE;
			} else if(null != (matchedStr = 
					matcherMatched(regReminderAfterTimeFormat, stripedOut, true))){

				this.commandType = commandType.ADD_REMINDER;
				String pureDurationStr = matchedStr.substring(matchedStr.indexOf(' '));
				Integer res = regDurationPartsProess(pureDurationStr);
				if(res == null){
					this.reminderType = Reminder.INVALID;
					return;
				}
				
				this.reminderTime = Calendar.getInstance();
				reminderTime.add(Calendar.SECOND, (int) res);
				this.reminderType = Reminder.CUSTOM;
				// clean the matchedStr after use
				matchedStr = null;
			} else if(null != (matchedStr =
				matcherMatched(regDateTimeOverallFormat, stripedOut, true))){
				Time newTime = new Time(null);
				this.reminderTime = dateTimeProcess(newTime, matchedStr, null);
				System.err.println(reminderTime.getTime());
				if(reminderTime == null){	// invalid case
					this.reminderType = Reminder.INVALID;
					return;
				} else{
					this.reminderType = Reminder.CUSTOM;
				}
				this.reminderType = Reminder.CUSTOM;
				// clean the matchedStr after use
				matchedStr = null;
			} else{
				outputErr("Problem with reminder, striped str: " + stripedOut);
				this.reminderType = Reminder.INVALID;
			}
			return;	// finish exectuion, return
		}
		 else{
			commandType = Commands.INVALID;
			return;
		}
		
		command = commandStr;
		
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
			 * Similar to before time format
			 */
			Pattern regAfterDateTimePattern = Pattern.compile(
					regWordSpacer + "after\\ " + regDateTimeOverallFormat + regWordSpacer,Pattern.CASE_INSENSITIVE);
			Matcher regAfterDateTimeMatcher = regAfterDateTimePattern.matcher(command);
			
			if(regBeforeDateTimeMatcher.find()){
				String timeDateStr = removeTheLeadingAndTailingWordSpacer(matchedStr = regBeforeDateTimeMatcher.group());
				searchBeforeDate = dateTimeProcess(searchBeforeTime, timeDateStr, null);
			}
			
			if(regAfterDateTimeMatcher.find()){
				String timeDateStr = removeTheLeadingAndTailingWordSpacer(matchedStr = regBeforeDateTimeMatcher.group());
				searchAfterDate = dateTimeProcess(searchAfterTime, timeDateStr, null);
			}
		}
		
		
		
		// need to perform these first cuz the date/time may be separate by other parameters
		// place
		Matcher regPlaceFormatMatcher = regPlaceFormatPattern.matcher(command);
		if(regPlaceFormatMatcher.find()){
			if(null == (place = regPlaceFormatProcess(
					removeTheLeadingAndTailingWordSpacer(matchedStr = regPlaceFormatMatcher.group())))){
				outputErr("Place Parsing Problem.");
			}
		}
		command = removeMatchedString(command, matchedStr);matchedStr=null;
		
		// the priority
		Matcher regPriorityFormatMatcher = regPriorityFormatPattern.matcher(command);
		if(regPriorityFormatMatcher.find()){
			if(null == (priority=regPriorityFormatProcess(
					removeTheLeadingAndTailingWordSpacer(matchedStr = regPriorityFormatMatcher.group())))){
				outputErr("Priority Parsing Problem. Matched String: " + matchedStr);
			}
		}
		command = removeMatchedString(command, matchedStr);matchedStr=null;
		
		// the list TODO: might need to consider the case of multiple lists
		Matcher regListFormatMatcher = regListFormatPattern.matcher(command);
		if(regListFormatMatcher.find()){
			if(null == (this.list = regListFormatProcess(
					removeTheLeadingAndTailingWordSpacer(matchedStr = regListFormatMatcher.group())))){
				outputErr("DEBUG: List Parsing Problem. Matched String: " + matchedStr);
			}
		}
		command = removeMatchedString(command, matchedStr);matchedStr=null;
		
		// use this to mark if the period match ([] ~ []) has been performed
		// if performed, the match for start time should not be executed
		boolean periodMatchedProfermed = false;
		
		// before matching the date/time, need to check the period first
		Matcher regTimePeriodMatcher = regTimePeriodPattern.matcher(command);
		if(regTimePeriodMatcher.find()){
			if(!periodProcess(removeTheLeadingAndTailingWordSpacer(matchedStr = regTimePeriodMatcher.group()))){
				outputErr("End date is eailier than the start list. (Not Sure How to deal with this now :)");
			}
			periodMatchedProfermed = true;
			command = removeMatchedString(command, matchedStr);matchedStr=null;
		}
		Matcher regDeadlineMatcher = regDeadlinePattern.matcher(command);
		if(regDeadlineMatcher.find()){	// check the deadline first, cuz it's more restrictive
			if(!regDeadlineProcess(removeTheLeadingAndTailingWordSpacer(matchedStr = regDeadlineMatcher.group()))){
				outputErr("Deadline processing got problem :)");
			}
		}
		command = removeMatchedString(command, matchedStr);matchedStr=null;
		
		Matcher regDateTimePointOverallMatcher = regDateTimePointOverallPattern.matcher(command);
		if(!periodMatchedProfermed && regDateTimePointOverallMatcher.find()){	// then try match the date&time
			if(null == (startDate = dateTimeProcess(startTime,
					removeTheLeadingAndTailingWordSpacer(matchedStr = regDateTimePointOverallMatcher.group()),
					null))){
				// got problem here
				outputErr("Date Parsing Problem: error in parsing the date with format.");
			}
			command = removeMatchedString(command, matchedStr);matchedStr=null;
		}
		
		// then the duration
		Matcher regDurationFormatMatcher = regDurationFormatPattern.matcher(command);
		if(regDurationFormatMatcher.find()){
			if(!regDurationFormatProcess(
					removeTheLeadingAndTailingWordSpacer(matchedStr = regDurationFormatMatcher.group()))){
				outputErr("Duration Parsing Problem.");
			}
		}
		command = removeMatchedString(command, matchedStr);matchedStr=null;
		
		this.taskTitle = command.trim();
	}
	
	private boolean regDeadlineProcess(String deadlineStr) {
		// eliminate the "by "
		deadlineStr = deadlineStr.substring(3);
		deadlineDate = dateTimeProcess(deadlineTime, deadlineStr, null);
		return deadlineDate != null;
	}

	private boolean periodProcess(String periodStr) {
		String[] timePoint = periodStr.split("[\\~\\-]");
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


	private static Calendar dateTimeProcess(Time time, String timeDateStr, Calendar dateSubstitute){
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
		
//		System.err.println("Time Str: " + timeDateStr);
		
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
			if(null == (date = regDateFormat_today_tomorrow_Process(
					removeTheLeadingAndTailingWordSpacer(matchedStr = regDateFormat_today_tomorrow_Matcher.group())))){
				outputErr("Date Parsing Problem: error in parse the date with format today|tomorrow");
			}
		} else if(regDateFormat_order_weekD_Matcher.find()){
			if(null == (date = regDateFormat_order_weekD_Process(
					matchedStr = removeTheLeadingAndTailingWordSpacer(regDateFormat_order_weekD_Matcher.group())))){
				outputErr("Date Parsing Problem: error in parsing the (this|next) day_of_week ");
			}
		}
		
		timeDateStr = removeMatchedString(timeDateStr,matchedStr);
		
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
					date = regDateFormat_today_tomorrow_Process("today");
				}
			}
		}
		
		if(date != null && time.getTime() != null){
			long secNum = time.getTime();
			System.out.println("Time in sec: " + (int)secNum);
			date.set(Calendar.HOUR_OF_DAY, 0);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, (int)secNum);
			System.out.println("Date: " + date.getTime());
		}
		
		return date;
	}


	/** For a given "matchedStr", remove this string from the "stringToBeOperated"
	 * @param stringToBeOperated
	 * @param matchedStr
	 * @return the result string
	 */
	private static String removeMatchedString(String stringToBeOperated, String matchedStr) {
		if(matchedStr != null && stringToBeOperated != null){
			String replacedWith = "";
			if(Character.isSpaceChar(matchedStr.charAt(0)) && Character.isSpaceChar(matchedStr.charAt(matchedStr.length() - 1))){
				replacedWith = " ";
			}
			stringToBeOperated = stringToBeOperated.replace(matchedStr, replacedWith);	// remove the matched String
			matchedStr = null;
		}
		return stringToBeOperated;
	}


	private String regPlaceFormatProcess(String string) {
		String place = null;
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
		
		return place;
	}


	private static String regListFormatProcess(String listStr) {
		
		String list = null;
		if(listStr.charAt(0) == '#'){
			listStr = listStr.substring(1);	// simply leave out the first character (#)
		}
		if(listStr.length() == 0){
			listStr = TaskLists.INBOX;	// by default return inbox (listStr == '#')
		}
		if(listStr.charAt(0)=='('){
			listStr = listStr.substring(1);
		}
		if(listStr.charAt(listStr.length()-1) == ')'){
			listStr = listStr.substring(0,listStr.length()-1);
		}
		list = listStr;
		return list;
	}

	/**
	 * @param priority
	 *  the Priority Enum
	 * @param priorityStr
	 *  currently, 1 for Important, 2 for Normal, 3 for Low (defined by Zhuochun)
	 * @return
	 */
	private static Priority regPriorityFormatProcess(String priorityStr) {
		if(priorityStr.charAt(0) == '!'){
			priorityStr = priorityStr.substring(1);
		}
		int numPriority = Integer.parseInt(priorityStr);
		
		Priority priority = null;
		
		switch(numPriority){
		case 1: priority = Priority.IMPORTANT; break;
		case 2: priority = Priority.NORMAL; break;
		case 3: priority = Priority.LOW; break;
		}
		
		return priority;
	}

	/**
	 * Extract the "duration"; it would also set the end date/time if it's not 
	 * 	set
	 * 
	 * @param duration
	 *  Long -- would store the result into this; unit is second.
	 *  E.g., if the 
	 * @param durationStr
	 *   
	 * @return
	 */
	private boolean regDurationFormatProcess(String durationStr) {
		String pureDurationStr = durationStr.substring(durationStr.indexOf(' '));
		
		Integer res = regDurationPartsProess(pureDurationStr);
		if(res == null){
			return false;	// processing duration unsuccessful
		}
		
		duration.setTime(res);
		
		// after getting the duration, if the end_date is not set, set the end date/time
		if(startTime.getTime() != null){
			endTime.setTime(startTime.getTime() + duration.getTime());
			if(endDate == null){
				endDate = (Calendar) startDate.clone();
			}
		}
		
		return true;
	}

	private static Integer regDurationPartsProess(String durationStr){
		String durationParts[] = durationStr.trim().split("\\ ");
		int res = 0;
		
		// then try to tell the duration information
		for(int i=0; i<durationParts.length; i+=2){
			long base = Long.parseLong(durationParts[i]);
			String unit = durationParts[i+1];
			if(unit == null || unit.compareTo("")==0){
				continue;
			}
			if(unit.length() >= 3 && unit.substring(0, 3).compareToIgnoreCase("sec") == 0){
				res += base;
			} else if(unit.length() >= 3 && unit.substring(0, 3).compareToIgnoreCase("min") == 0){
				res += base * 60;
			} else if(unit.length() >= 1 && unit.substring(0, 1).compareToIgnoreCase("h") == 0){
				res += base * 3600;
			} else if(unit.length() >= 3 && unit.substring(0, 3).compareToIgnoreCase("day") == 0){
				res += base * 3600 * 24;
			} else {
				return null;	// when failed to process duration
			}
		}
		return res;
	}
	
	private static boolean regTimeFormatProcess(Time timeObj, String timeStr) {
		// first level: separate by :
		Long time = new Long(0); 	// initialization
		timeStr = timeStr.toLowerCase();
		// capture the [a|p]m. 
		// This tag is no longer useful after this operation
		if(timeStr.contains("pm")){
			time += 3600 * 12;	// the afternoon
		}
		
		// the case when "timeStr contains "at"
		if(timeStr.length() > 3 && timeStr.substring(0, 3).compareToIgnoreCase("at ") == 0){
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

	private static Calendar regDateFormat_order_weekD_Process(String weekDStr) {
		Calendar date = Calendar.getInstance();
		
		// spacer is here
		String[] dayInfo = weekDStr.split(regWordSpacer);
		weekDStr = dayInfo[dayInfo.length - 1];
		
		boolean nextWeek = false;
		
		if(dayInfo.length > 1 && dayInfo[dayInfo.length - 2].matches("next")){
			nextWeek = true;
		}
		
		int dayOfTheWeek = dateParseGetDayOfWeekFromText(weekDStr);
		
		int currentDay = date.get(Calendar.DAY_OF_WEEK);
		
		if(currentDay == -1){	// failure in day parsing
			return null;
		}
		
		if(nextWeek || currentDay > dayOfTheWeek){	// indicating the day of next week
			date.set(Calendar.DAY_OF_WEEK_IN_MONTH, date.get(Calendar.DAY_OF_WEEK_IN_MONTH)+1);
			date.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
		} else{	// indicating this week. if on the same date, it's still this week
			date.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
		}
		return date;
	}

	private static Calendar regDateFormat_today_tomorrow_Process(String today_tomorrow) {
		Calendar date = Calendar.getInstance();
		if(today_tomorrow.compareTo("today") == 0){
			// do nothing here
		} else if(today_tomorrow.compareTo("tomorrow") == 0 || today_tomorrow.compareTo("tmr") == 0){
			date.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH) + 1);
		} else{
			return null;
		}
		
		return date;
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
		
		if(day.substring(0, 3).compareToIgnoreCase("Mon") == 0){
			return Calendar.MONDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Tue") == 0){
			return Calendar.TUESDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Wed") == 0){
			return Calendar.WEDNESDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Thu") == 0){
			return Calendar.THURSDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Fri") == 0){
			return Calendar.FRIDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Sat") == 0){
			return Calendar.SATURDAY;
		} else if(day.substring(0, 3).compareToIgnoreCase("Sun") == 0){
			return Calendar.SUNDAY;
		} else{
			outputErr("Error in day parsing, String: " + day.substring(0,3));
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
			monthNum--;	// cuz the month in Calendar is starting with 0
		} catch (NumberFormatException e) {
			// if it's not integer, then it's text format --> need to translate
			return dateParseGetMonthFromText(month);
		}
		
		return (monthNum<12)?monthNum:-1;	// return -1 on false
	}
	
	private static Calendar dateFormat_dd_mm_$yy$yy$$_Process(String dateStr) {
		Calendar date = Calendar.getInstance();
		date.clear();
		
		// remove the leading "on " if it exist
		if(dateStr.length() > 3 && dateStr.substring(0, 3).compareToIgnoreCase("on ") == 0){
			dateStr = dateStr.substring(3);
		}
		
		String[] dateInfoArr = dateStr.split(regDateSpacer);
		
			// set the day of a month
		String dayStr = dateInfoArr[0];
		
		// eliminate the "st/nd/rd/th" which specifies the order
		if(dayStr.length() > 2 && Character.isLetter(dayStr.charAt(2))){
			dayStr = dayStr.substring(0, dayStr.length()-2);
		}
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
	
	
	
	public static void main(String[] args){
		// test match here
		String testStr = "remind 3 in 3 min 2 h";
			/* test cases to be added for Unit Test:
			 * Reminder : 
			 * 	"remind 3 4pm tomorrow";
			 * 	"remind 3 19:00 tomorrow"
			 *  "remind 3 in 3h 2m"
			 *  "remind 3 in 3 min 2 h" -- note: currently cannot support string like 3m2h yet
			 */
		
 		// for testing
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader("src/cs2103/t14j1/logic/smartbar/test.txt"));
			// when under linux, put: src/cs2103/t14j1/logic/smartbar/test.txt
			// when under windows, put: src\\cs2103\\t14j1\\logic\\smartbar\\test.txt
			
			String strLine;
			boolean skip = false;
			while ((strLine = in.readLine()) != null)   {
				if(testStr != null){
					strLine = testStr;
				}
				
				if(skip){
					skip = false;
					continue;
				} else if(strLine.compareTo("")==0){
					skip = true;
					continue;
				}
				
				// Print the content on the console
				System.out.println ("input: " + strLine);
				
				ParseCommand test = new ParseCommand(strLine);
				System.out.println("Command Type: " + test.extractCommand());
				System.out.println("Task Title: " + test.extractTaskName());
				System.out.println("Task Num: " + test.extractTaskNum());
				System.out.println("List: " + test.extractListName());
				System.out.println("Place: " + test.extractPlace());
				System.out.println("Priority: " + test.extractPriority());
				System.out.println("Duration: " + test.extractDuration());
				System.out.println("Start Date: " + test.extractStartDate());
				System.out.println("Start Time: " + test.extractStartTime());
				System.out.println("End Date: " + test.extractEndDate());
				System.out.println("End Time: " + test.extractEndTime());
				System.out.println("Deadline Date: " + test.extractDeadlineDate());
				System.out.println("Deadline Time: " + test.extractDeadlineTime());
				System.out.println("Reminder Type: " + test.getRemindParamter());
				System.out.println("Reminder Time: " + test.getRemindTime());
				
				if(testStr != null){
					break;
				}
			}
			in.close();
				
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
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
	 * This is the first method to be called of the Parse Command
	 * The Types of command are defined in Commands Enum 
	 * @return the type of the Command for the Input String
	 */
	public Commands extractCommand() {
		return commandType==null?Commands.INVALID:commandType;
	}
	
	/**
	 * @return
	 *  Get the task name; it has removed the properties of the task.
	 *  For example, if the input String is:
	 *  <quote>
	 *  	Meeting with Shubham tomorrow 3pm @(PGP Canteen) !3
	 *   it would return Meeting with Shubham
	 *  </quote>
	 */
	public String extractTaskName() {
		return this.commandStr; // if TaskTitle is not found, return null
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
		return list;
	}

	/**
	 * @return
	 * 	the priority; if not specified, then it should be null
	 */
	public Priority extractPriority() {
		return priority;
	}
	
	public String extractPlace(){
		return this.place;
	}
	
	/**
	 * Since Zhuochun decided to use the date only, so we don't put this one public first
	 * @return
	 *  the number of seconds from the start of the day, as a start time
	 */
	public Long extractStartTime() {
		return startTime.getTime();
	}
	
	/**
	 * Helper function to help set the Date&Time when "extractDate" is necessary
	 * 
	 * @param date
	 * @param time
	 * @return
	 */
	private static Date _extractDateHelper(Calendar date, Time time, int defaultTimeOnNull){
		if(date == null){
			return null;
		}
		
		Long definedTime = time.getTime();
		int setHour = 0;
		int setMinute = 0;
		int setSecond = 0;
		
		if(definedTime !=null){
			setHour = (int) (definedTime/3600);
			setMinute = (int) (definedTime%3600)/60;
			setSecond = (int) (definedTime%60);
		} else if(defaultTimeOnNull == EARLIEST_TIME){
			setHour	= 0;
			setMinute = 0;
			setSecond = 0;
		} else if(defaultTimeOnNull == LATEST_TIME){
			setHour = 23;
			setMinute = 59;
			setSecond = 59;
		} else if(defaultTimeOnNull == NO_CHANGE){
			Long timeLong = time.getTime();
			
			if(timeLong == null){
				return date.getTime();
			} else{	// set the date to the time's date
				setHour = (int) (timeLong/3600);
				setMinute = (int)(timeLong%3600)%60;
				setSecond =(int) (timeLong%60);
			}
		}
		
		date.set(Calendar.HOUR_OF_DAY, setHour);
		date.set(Calendar.MINUTE, setMinute);
		date.set(Calendar.SECOND, setSecond);
		return date.getTime();
	}
	
	/**
	 * @return
	 * 	The start date and time for a task
	 */
	public Date extractStartDate() {
		/* Zhuochun: use DateFormat.strToDate(str) or DateFormat.strToDateLong(str) 
		 * Yangyu's reply: this would not work because: 
		 *   1. the date string may following different format; simply using that would cause Exception
		 *     (for example, we also consider 'tomorrow' as a date, but this cannot be parsed using strToDate)
		 *   2. the date can be extracted in the constructor; no point of using this again 
		 */
		
		/* Because zhuochun don't want the return type to be change to (Long 
		 *   we've discussed --), nor does Calendar... do I have to use the 
		 *   seemingly depreciated "Date" class here.
		 */
		return _extractDateHelper(startDate,startTime, CURRENT_TIME);
	}
	
	/**
	 * @return
	 *  the number of seconds for the duration
	 *  @code null @endcode on doesn't exist
	 */
	public Long extractDuration(){
		
		// call this two method to add the "time" to Date
		_extractDateHelper(startDate,startTime,CURRENT_TIME);
		_extractDateHelper(endDate,endTime,CURRENT_TIME);
		
		// when there is a duration already
		// don't necessarily have a startDate, because sometimes the user only
		// define the duration. For example, input like "jogging on Saturday for 2 hours".
		if(duration.getTime() != null){
			return duration.getTime();
		}
		
		// when there is no duration, no start date, then return null
		else if(startDate == null){
			return null;
		}
		
		// when there's a start date, and an end date also, then return the 
		// difference
		else if(endDate != null){
			return (endDate.getTimeInMillis() - startDate.getTimeInMillis())/1000;
		}
		
		// when there is start time, but a start date; and no duration.
		// return 0
		else if(startTime.getTime() != null){
			return (long)0;
		}
		
		// when there's a no start time, with a date, then return null
		else if(startTime.getTime() == null){
			return null;
		}
		
		// this is an not-considered case; have to output an error here.
		else{
			outputErr("Nor-considered case in pase duration");
			return null;
		}
	}
	
	/**
	 * 
	 * *******************************************
	 * Initially: Currently not supported in this phase
	 * 
	 * Changes on 2011-10-9 10:41:50:
	 * 	It seems that the team want this method. so put it in.
	 * *******************************************
	 * @return
	 * 	The end date and time as a Date class 
	 */
	public Date extractEndDate() {
		return _extractDateHelper(endDate, endTime,CURRENT_TIME);
	}
	
	public Long extractEndTime() {
		return endTime.getTime();
	}
	
	/**
	 * @return
	 * 	the date and time for a deadline.
	 *  As long as a task has a start date, we'll assume it has a deadline 
	 *  (the last second of a day); and
	 *   the deadline has a fixed time
	 */
	public Date extractDeadlineDate() {
		
		if(this.deadlineDate != null){
			return _extractDateHelper(this.deadlineDate,this.deadlineTime,LATEST_TIME);
		}
		
		// the only null case is when startDate is not specified
		if(startDate == null){
			return null;
		}
		
		// set it as the last minute of the day
		if(deadlineTime.getTime() == null){
			
			deadlineTime.setTime((long)3600 * 24 -1);
		}
		
		return _extractDateHelper(deadlineDate, deadlineTime,LATEST_TIME);
	}
	
	/**
	 * Should make this history function private; initially wanted to use it to extract
	 *  the time; but later on Zhuochun proposed a more systematic way
	 * 
	 * But currently leave it so for consistency with Shubham
	 * 
	 * @return the number of seconds from the start of the day, as a deadline time
	 */
	public Long extractDeadlineTime(){
		return deadlineTime.getTime();
	}
	
	/* These four method are for search command only */
	public Date extractSearchBeforeDate() {
		return _extractDateHelper(searchBeforeDate, searchBeforeTime,LATEST_TIME);
	}
	
	
	public Long extractSearchBeforeTime(){
		return searchBeforeTime.getTime();
	}
	
	public Date extractSearchAfterDate() {
		return _extractDateHelper(searchAfterDate, searchAfterTime,EARLIEST_TIME);
	}
	
	public Long extractSearchAfterTime(){
		return searchAfterTime.getTime();
	}
	/* These four method are for search command only */
	
	/* START: These methods are for other commands, not add task */
	
	/** 
	 * called by edit task, delete task; used to specify the task number
	 * @return The task number
	 */
	public Integer extractTaskNum(){
		return taskNum;
	}
	
	/** called when it's "rename list"
	 * @return
	 * 	the new list name to be saved.
	 */
	public String extractNewListName(){
		return this.newListName;
	}
	/* END: These methods are for other commands, not add task */
	
	public Reminder getRemindParamter(){
		return this.reminderType;
	}
	
	public Date getRemindTime(){
		return _extractDateHelper(this.reminderTime,new Time(null),this.NO_CHANGE);
	}
}

