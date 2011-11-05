package cs2103.t14j1.logic.smartbar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.taskmeter.reminder.Reminder;


public class ParseCommand extends ParseCommandGetType
implements
	ParseAddTaskCommand, ParseListRelatedCommand,ParseTaskNumRelatedCommand,
	ParseMarkPriorityCommand,ParseReminderCommand,ParseSearchCommand
{
	
	// some magic string used in defining the property
	private static final int EARLIEST_TIME	= 0;
	private static final int LATEST_TIME	= 1;
	private static final int CURRENT_TIME	= 2;
	private static final int NO_CHANGE = 3;
	
	private DateTime startTime = new DateTime();
	private DateTime endTime = new DateTime();
	
	private Integer duration = null;
	private Priority priority = null;
	private String place = null;
	private String list;
	
	private DateTime deadlineTime = new DateTime();
	
	// for rename list
	private String newListName = null;
	
	private DateTime searchBeforeTime	= new DateTime();
	private DateTime searchAfterTime	= new DateTime();
	
	private Reminder reminderType;
	private Calendar reminderTime;
	
	private List<Integer> taskNum = null;
	
	
	// for internal use only
	private String tempMatchedStr;
	private String tempStrInOp;
	
	public ParseCommand(String command) {
		super(command);
		parseKnownCommand();
	}

	private void parseKnownCommand() {
		if(this.parsingCompleted)	return;
		switch(this.commandType){
	    /**
	     * Syntax: add + task str
	     * @see {@link ParseAddTaskCommand}
	     */
		case ADD_TASK:parseAddTask();break;

	    /**
	     * Syntax: del + [num]
	     * @see {@link ParseTaskNumRelatedCommand}
	     */
		case DELETE_TASK:

	    /**
	     * Syntax: [move|mv] [num] [#list]
	     * @see {@link ParseTaskNumRelatedCommand}, {@link ParseListRelatedCommand}
	     */
		case MOVE_TASK:
	    
	    /**
	     * Syntax: edit + [num]
	     * @see {@link ParseTaskNumRelatedCommand}
	     */
		case EDIT_TASK:
	    
	    /**
	     * Syntax: [num] + done
	     * Syntax: done + [num]
	     * @see {@link ParseTaskNumRelatedCommand}
	     */
		case MARK_COMPLETE:
	    
	    /**
	     * Syntax: [num] + [!1~3]
	     * @see {@link ParseMarkPriorityCommand}
	     */
		case MARK_PRIORITY:
	    
	    /**
	     * Syntax: remind [id] [start|end|deadline|custom]
	     * 
	     * start    -> on StartDateTime
	     * end      -> on EndDateTime
	     * deadline -> on Deadline
	     * custom   -> supported custom type:
	     * 	-> in + time period: e.g.: in 2 hours, in 2 second, in 5 minutes
	     *  -> date/time format: a specific date/time point
	     *  
	     *  @see {@link ParseReminderCommand}
	     */
		case ADD_REMINDER:
	    
	    /**
	     * Syntax: remind [id] cancel
	     * @see {@link ParseReminderCommand}
	     */
		case REMOVE_REMINDER:

	    /**
	     * Syntax: add [#list]
	     * @see {@link ParseListRelatedCommand}
	     */
		case ADD_LIST:

	    /**
	     * Syntax: edit [#list]
	     * @see {@link ParseListRelatedCommand}
	     */
		case EDIT_LIST:
	    
	    /**
	     * Syntax: rename [#oldListName] [#newListName]
	     * @see {@link ParseListRelatedCommand}
	     */
		case RENAME_LIST:

	    /**
	     * Syntax: (del|delete) + [#list]
	     * @see {@link ParseListRelatedCommand}
	     */
		case DELETE_LIST:

	    /**
	     * Syntax: #(list name)
	     * @see {@link ParseListRelatedCommand}
	     */
		case SWITCH_LIST:

	    /**
	     * Syntax: / + string to search
	     * @see {@link ParseSearchCommand},{@link ParseListRelatedCommand}
	     */
		case SEARCH:

	    /**
	     * Syntax: dis/display + [#list]
	     * @see {@link ParseListRelatedCommand}
	     */
		case DISPLAY_LISTS:

	    /**
	     * Syntax: dis/display + [num]
	     * @see {@link ParseTaskNumRelatedCommand}
	     */
		case DISPLAY_TASK:break;
	    
		}
	}

	private void parseAddTask() {
		parseParamPlace();
		parsePriorityParam();
		parseListName();
		parseDeadline();
	}
	
	private void parseDeadline() {
		if(regexMatchWithSanitizedCommandIgnoreCase(regexWordSpacer + "by\\ " + regDateTimeOverallFormat + regexWordSpacer)){
			String deadlineStr = removeLeadingAndTailingNonDigitLetterDefinedSymbol(this.matchedStr).substring(3);	// by pass the "by"
//			deadlineDate = dateTimeProcess(deadlineStr,deadlineTime,null);
		}
		
	}

	
	private boolean regexMatchAndSaveToTempStr(String regex) {
		this.tempMatchedStr = regexMatchedStr(regex,this.tempStrInOp,true);
		return this.tempMatchedStr != null;
	}

	private void parseParamPlace() {
		this.place = parseParamWithBracketType(regPlaceFormat);
	}
	
	private void parseListName() {
		this.list = parseParamWithBracketType(regexList);
	}

	private String parseParamWithBracketType(String regex) {
		String res = null;
		if(regexMatchWithSanitizedCommandIgnoreCase(regexWordSpacer + regex + regexWordSpacer)){
			res = removeLeadingAndTailingCharUntilBracketOrLetter(matchedStr);
			res = removeLeadingAndTailingBracket(res);
			removeMatchedStringFromSanitizedCommand();
		}
		return res;
	}

	private void parsePriorityParam() {
		if(regexMatchWithSanitizedCommandIgnoreCase(regexWordSpacer + regPriorityFormat + regexWordSpacer)){
			String priority = removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr).substring(1);
			this.priority = translatePriority(priority);
			removeMatchedStringFromSanitizedCommand();
		}
	}

	private Priority translatePriority(String priorityStr) {
		int priorityVal = Integer.parseInt(priorityStr);
		switch(priorityVal){
		case 1: return Priority.IMPORTANT;
		case 2: return Priority.NORMAL;
		case 3: return Priority.LOW;
		default: return null;
		}
	}

	

	private String removeLeadingAndTailingBracket(String inStr) {
		if(inStr == null || inStr.length() < 2){
			return inStr;
		}
		if(inStr.charAt(0) == '('){
			inStr = inStr.substring(1);
		}
		
		if(inStr.charAt(inStr.length() - 1) == ')'){
			inStr = inStr.substring(0,inStr.length()-1);
		}
		return inStr;
	}

	private String removeLeadingAndTailingCharUntilBracketOrLetter(
			String inStr) {
		if(inStr == null)	return inStr;
		
		while(inStr.length() > 0 &&
				inStr.charAt(0)!='(' && 
				!Character.isLetter(inStr.charAt(0))){
			inStr = inStr.substring(1);
		}
		
		int length = 0;
		while( (length = inStr.length()) > 0 &&
				inStr.charAt(length-1)!=')'&&
				!Character.isLetter(inStr.charAt(length-1))
				){
			inStr = inStr.substring(0,length-1);
		}
		
		return inStr;
	}

	private void removeMatchedStringFromSanitizedCommand() {
		this.sanitizedCommand = this.sanitizedCommand.replace(this.matchedStr, " ");
		
	}

	private boolean regexMatchWithSanitizedCommandIgnoreCase(String regex) {
		return regexMatchedStrAndSaveMatchedStr(regex, sanitizedCommand, true);
	}
	
	@Override
	public Date extractSearchBeforeDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date extractSearchAfterDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reminder getRemindParamter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getRemindTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> extractTaskNum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractNewListName() {
		return this.newListName;
	}

	@Override
	public String extractTaskName() {
		return removeLeadingAndTailingNonDigitLetterDefinedSymbol(this.sanitizedCommand);
	}

	@Override
	public String extractListName() {
		return this.list;
	}

	@Override
	public Priority extractPriority() {
		return this.priority;
	}

	@Override
	public String extractPlace() {
		return this.place;
	}

	@Override
	public Date extractStartDate() {
		return this.startTime.getDate();
	}

	@Override
	public Integer extractStartTime() {
		return this.startTime.time;
	}

	@Override
	public Integer extractDuration() {
		return this.duration;
	}

	@Override
	public Date extractEndDate() {
		return this.endTime.getDate();
	}

	@Override
	public Integer extractEndTime() {
		return this.endTime.getTime();
	}

	@Override
	public Date extractDeadlineDate() {
		return this.deadlineTime.getDate();
	}

	@Override
	public Integer extractDeadlineTime() {
		return this.deadlineTime.getTime();
	}
}