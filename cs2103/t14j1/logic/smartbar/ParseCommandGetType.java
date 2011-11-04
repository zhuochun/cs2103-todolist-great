package cs2103.t14j1.logic.smartbar;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs2103.t14j1.logic.Commands;


/**
 * @author SongYY
 * this class is meant for internal use only
 * Type of command fully handled:
 * 		UNDO,		REDO,		EXIT
 */
public abstract class ParseCommandGetType {
	public static final boolean IGNORE_CASE = true;
	public static final boolean CARE_CASE = false;
	
		/* define the regex */
	protected static final String regexWordSpacer = "([^\\d\\w#!]|(^)|($))";
	protected static final String regexSpacer = "((\\s)|(^)|($))";
	protected static final String regexNonSpaceWordSpacer = "([^\\d\\w\\ ]|(^)|($))";
	private static final String regexDateSpacer = "[,-/. ]";
	
		// time related
	// regular expression for matching the time
	private static final String regTimePointAmPm = // match the 5am/pm, 5 am/pm, or 5:00 am/pm. 
			"(1[012]|\\d)(:[0-5]\\d){0,2}((\\ )?[ap]m)";
	private static final String regTimePoint24H = 
			"([01]?\\d|2[0-3])(:[0-5]\\d){1,2}";
	private static final String regTimeFormat = // can be either 24 hour, or am/pm
			"((at" + regexWordSpacer + ")?" + "(" + regTimePointAmPm + "|" + regTimePoint24H +")" +  ")";
	
		// regular expression for matching the date
	private static final String regMonthText =
			"(January|Jan|February|Feb|March|Mar|April|Apr|May|June|Jun|July|Jul|" + 
			"August|Aug|September|Sept|October|Oct|November|Nov|December|Dec)";
	private static final String regWeekText =
			"(Monday|Mon|Tuesday|Tue|Wednesday|Wed|Thursday|Thur|Friday|Fri|Saturday|Sat|Sunday|Sun)";
	private static final String regDayOrder = "((this)?|next)";

			// date format
	private static final String regDateFormat_dd_$mm$_$yy$yy$$ = 
			"(([12][0-9]|3[01]|(0)?[1-9])" + regexDateSpacer + "(0[1-9]|1[012])(" + 
			regexDateSpacer + "(19|20)?\\d\\d)?)";
	private static final String regDateFormat_dd_$M$_$yy$yy$$ = // When month is text, year should be a full string if exist
			"(([12][0-9]|3[01]|(0)?[1-9])(st|nd|rd|th)?" + regexDateSpacer + "(\\ )?" +
			regMonthText + "(" + regexDateSpacer + "(19|20)\\d\\d)?)";
	private static final String regDateFormat_dd_$mm$M$_$yy$yy$$ = 
			"((on\\ )?(" + regDateFormat_dd_$M$_$yy$yy$$+ "|" + regDateFormat_dd_$mm$_$yy$yy$$ + "))";
	
	private static final String regDateFormat_order_weekD = 
			"((on\\ )?(" + regDayOrder + regexWordSpacer + ")?" + regWeekText + ")";
	
	private static final String regDateFormat_today_tomorrow = 
			"(today|tomorrow|tmr)";
	
		// the date format for mm/dd/yy; leave it here for the possible use in the future
	private static final String regDateFormat_mm_dd_yy =
			"(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])([- /.](19|20)\\d\\d)?";
	private static final String regTimeUnit = 
			"((hour(s)?|h)|(minute(s)?|min)|(second(s)?|sec)|(day(s)?))";
	private static final String regTimeUnitForSearch = 
			"((this|next)\\ (" + regTimeUnit + "|week|month|year|" + regWeekText + "|" + regMonthText + "))";
	
	private static final String regDateOverallFormat =
			"(" + regDateFormat_dd_$mm$M$_$yy$yy$$ + "|" + regDateFormat_order_weekD + 
			"|" + regDateFormat_today_tomorrow + ")";
	
	private static final String regDateTimeOverallFormat = "(" +
//				regTimeFormat +
//			regDateOverallFormat + regWordSpacer + regTimeFormat + 
			"(" + regDateOverallFormat+ "(" + regexWordSpacer + regTimeFormat + ")?)|" + 	// date (time)?
			"(" + regTimeFormat + "("+ regexWordSpacer + regDateOverallFormat + ")?)" +  // time (date)?
			")";
	
	
	
	private static final String regDurationFormat = "(for(\\ [\\d]+\\ " + regTimeUnit + ")+)";
	private static final String regReminderAfterTimeFormat = "(in(\\ [\\d]+\\ " + regTimeUnit + ")+)";
	private static final String regPlaceFormat = 
		"((@[\\w]+)|(@\\([^\\)]+\\)))";	// format: @ + word; or: @ + (words)
	private static final String regPriorityFormat = "(![123])";
	protected static final String regexList = "((#[\\w]+)|(#\\([^\\)]+\\)))";
	
	protected static final String regexSearchCommand = "^/(" + regexSpacer +")*";
	protected static final String regexAddCommand = "^add(" + regexSpacer +")*";
	protected static final String regexSwitchListCommand = "^" + regexList + "(" + regexSpacer +")*$";
	protected static final String regexDeleteTaskCommand = "^(delete|del)\\ [\\d]+$";	// TODO: would need to change later
	protected static final String regexDeleteListCommand = "^(delete|del)\\ "+ regexList + "$";
	protected static final String regexDisplayTaskCommand = "^(display|dis)\\ [\\d]+";
	protected static final String regexDisplayListCommand = "^(display|dis)\\ " + regexList;
	protected static final String regMoveTaskToListCmd = "^(move|mv)\\ [\\d]+\\ (" + regexList+ "|#)$";
	protected static final String regMarkAsCompleteCmd = "(^(done)\\ [\\d]+$)|(^[\\d]+\\ (done)$)";	// Syntax 1: [num] + done;  Syntax 2: done + [num]
	protected static final String regEditTaskCmd = "^(edit)\\ [\\d]+$";	// simply signal an edit
	protected static final String regSetPriorityCmd = "^[\\d]\\ " + regPriorityFormat + "$";
	protected static final String regAddListCmd = "^(add)(\\ )+" + regexList+ "$";
	protected static final String regRenameListCmd = "^(rename)\\ " + regexList+ "\\ " + regexList+ "$";
	protected static final String regEditListCmd = "^(edit)\\ " + regexList + "$";
	protected static final String regReminderGeneralCmd = "^(remind)\\ [\\d]+\\ (start|end|deadline|" + regDateTimeOverallFormat +")";
	protected static final String regReminderNumOnly = "^(remind)\\ [\\d]+$";
	protected static final String regRemoveReminder = "^(remind)\\ [\\d]+\\ cancel$";
	
	
	protected boolean parsingCompleted = false;
	protected Commands commandType;
	protected String originalCommand;
	
	/**
	 * The string removed the unnecessary part, for example:
	 * the add task command removed the add task
	 * 	"add meeting tomorrow" => "meeting tomorrow"
	 * the search task command remove the "/", as well as the leading space if has
	 * 	"/ before 10am" => "before 10am"
	 */
	protected String sterilizedCommand;
	protected String matchedStr;
	private List<Pair<String,Commands>> regexCommandsFormatToMatchWithoutMarkingParseComplete;
	private List<Pair<String,Commands>> regexCommandsFormatToMatchAndMarkingParseComplete;
	
	public Commands extractCommand(){
		if(this.commandType == null){
			return Commands.INVALID;
		} else{
			return this.commandType;
		}
	}
	
	public ParseCommandGetType(String command) {
		
		// save the original command string
		this.originalCommand = command;
		
		firstLevelCommandParsing();
	}
	
	protected boolean regexMatchedStrAndSaveMatchedStr(String reg,String matchStr,boolean ignoreCase){
		
		Pattern regPattern = Pattern.compile(
				reg,
				ignoreCase?Pattern.CASE_INSENSITIVE:0);
		Matcher regMatcher = regPattern.matcher(matchStr);
		if(regMatcher.find()){
			this.matchedStr = regMatcher.group();
			return true;
		} else{
			return false;
		}
	}
	
	private void firstLevelCommandParsing(){
		addCommandRegexFormatToCommandTypeMapping();
		
		if(originalCommand == null){
			this.commandType = Commands.INVALID;
		}
		
		for(Pair<String,Commands> regexCommandFormat:regexCommandsFormatToMatchWithoutMarkingParseComplete){
			if(handleMatchedCommandWithRegexFormat(regexCommandFormat)){
				removeMatchedStringFromOriginalCommand();
				return;
			}
		}
		
		for(Pair<String,Commands> regexCommandFormat:regexCommandsFormatToMatchAndMarkingParseComplete){
			if(handleMatchedCommandWithRegexFormat(regexCommandFormat)){
				removeMatchedStringFromOriginalCommand();
				this.parsingCompleted = true;
				return;
			}
		}
	}
	
	private void addCommandRegexFormatToCommandTypeMapping(){
		// command can be done directly
		regexCommandsFormatToMatchAndMarkingParseComplete = new LinkedList<Pair<String,Commands>>();
		regexCommandsFormatToMatchAndMarkingParseComplete.add(
				new Pair<String,Commands>("^undo(\\s)*$", Commands.UNDO));
		regexCommandsFormatToMatchAndMarkingParseComplete.add(
				new Pair<String,Commands>("redo(\\s)*", Commands.REDO));
		regexCommandsFormatToMatchAndMarkingParseComplete.add(
				new Pair<String,Commands>("^exit(\\s)*$", Commands.EXIT));
		
		
		// command needs further dealing with
		regexCommandsFormatToMatchWithoutMarkingParseComplete = new LinkedList<Pair<String,Commands>>();
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regexDeleteTaskCommand, Commands.DELETE_TASK));
		
		// command need to keep the old string -- detect the type only
		// this would be further processed by the child class
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>("^#$", Commands.SWITCH_LIST));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regexSwitchListCommand, Commands.SWITCH_LIST));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regAddListCmd, Commands.ADD_LIST));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regRenameListCmd, Commands.RENAME_LIST));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regEditListCmd, Commands.EDIT_LIST));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regexDeleteListCommand, Commands.DELETE_LIST));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regexDisplayListCommand, Commands.DISPLAY_LISTS));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>("^(display|dis)(\\s)*$", Commands.DISPLAY_LISTS));
		
		
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regexDisplayTaskCommand, Commands.DISPLAY_TASK));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regMoveTaskToListCmd, Commands.MOVE_TASK));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regEditTaskCmd, Commands.EDIT_TASK));
		
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regMarkAsCompleteCmd, Commands.MARK_COMPLETE));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regSetPriorityCmd, Commands.MARK_PRIORITY));
		
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regReminderGeneralCmd, Commands.ADD_REMINDER));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regReminderNumOnly, Commands.ADD_REMINDER));
		regexCommandsFormatToMatchAndMarkingParseComplete.add(
				new Pair<String,Commands>(regRemoveReminder, Commands.REMOVE_REMINDER));
			
			// These are the less restrictive ones; should check in the end
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regexSearchCommand,Commands.SEARCH));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regexAddCommand,Commands.ADD_TASK));
	}

	private boolean handleMatchedCommandWithRegexFormat(Pair<String,Commands> regexCommandPair){
		if(regexMatchedStrAndSaveMatchedStr(regexCommandPair.first,
				originalCommand,IGNORE_CASE)){
			this.commandType = regexCommandPair.second;
			return true;
		} else{
			return false;
		}
		
	}

	private void removeMatchedStringFromOriginalCommand() {
		this.sterilizedCommand = this.originalCommand.replace(this.matchedStr, "");
	}

}
