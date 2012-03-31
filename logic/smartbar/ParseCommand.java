package logic.smartbar;

import gui.reminder.Reminder;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import storage.Priority;
import storage.TaskLists;



public class ParseCommand extends ParseCommandGetType
implements
	IParseAddTaskCommand, IParseListRelatedCommand,IParseTaskNumRelatedCommand,
	IParseMarkPriorityCommand,IParseReminderCommand,IParseSearchCommand
{
	
	private DateTime startTime = new DateTime();
	private DateTime endTime = new DateTime();
	
	private Long duration = null;
	private Priority priority = null;
	private String place = null;
	private String list;
	
	private DateTime deadlineTime = new DateTime();
	
	// for rename list
	private String newListName = null;
	
	private DateTime searchBeforeTime;
	private DateTime searchAfterTime;
	
	private Reminder reminderType;
	private Calendar reminderTime;
	
	private Set<Integer> taskNum = null;
	
	public ParseCommand(String command) {
		super(command);
		parseKnownCommand();
	}

	private void parseKnownCommand() {
		if(this.parsingCompleted)	return;
		
		switch(this.commandType){
		case ADD_TASK:		parseAddTask();break;
		case SEARCH:		parseSearchTask();break;
		
		case DELETE_TASK:	
		case MOVE_TASK:		
	    case EDIT_TASK:		
		case MARK_COMPLETE:
		case MARK_PRIORITY:
		case ADD_LIST:
		case EDIT_LIST:
		case RENAME_LIST:
		case DISPLAY_TASK:
		case REMOVE_REMINDER:
		case DELETE_LIST:	parseTaskNumAndListRelatedCommand();break;
		
		case DISPLAY_LISTS:
		case SWITCH_LIST:	parseListAndReturnInboxOnNoListNameGiven();break;
			
		case ADD_REMINDER:	parseAddReminderCommand();break;
	    
	    default:// do nothing
		}
	}

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
	private void parseAddReminderCommand() {
		saveOriginalStringToSanitizedCommandAndRemoveExtraSpace();
		parseNumList();
		replaceMatchedStringFromSanitizedCommandWithOneSpace();
		
		int lastPosOfSpace = sanitizedCommand.lastIndexOf(' ');
		
		String reminderParamStr = sanitizedCommand.substring(lastPosOfSpace+1);
		
		if(parseAfterCurrentTimeFormat()){
			
		} else if(parseDateTimeAndRemoveMatchedStr()){
			if(startTime.time == null){
				this.reminderType = Reminder.INVALID;
			} else if(startTime.date == null){
				startTime.date = Calendar.getInstance();
				this.reminderTime = this.startTime.date;
				this.reminderType = Reminder.CUSTOM;
			}
		} else if(reminderParamStr.equalsIgnoreCase("end")){
			this.reminderType = Reminder.END;
		} else if(reminderParamStr.equalsIgnoreCase("deadline")){
			this.reminderType = Reminder.DEADLINE;
		} else{
			this.reminderType = storage.user.User.getDefaultReminder();
		}
	}

	private boolean parseAfterCurrentTimeFormat() {
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase(regReminderAfterTimeFormat)){
			String matchedStr = removeLeadingAndTailingNonDigitLetterDefinedSymbol(this.matchedStr);
			String pureDurationStr = matchedStr.substring(matchedStr.indexOf(' '));
			Long res = regDurationPartsProess(pureDurationStr);
			if(res == null){
				this.reminderType = Reminder.INVALID;
				return false;
			}
			this.reminderTime = Calendar.getInstance();
			reminderTime.add(Calendar.SECOND, (int)(long) res);
			this.reminderType = Reminder.CUSTOM;
			replaceMatchedStringFromSanitizedCommandWithOneSpace();
			return true;
		}else return false;
	}

	private void parseListAndReturnInboxOnNoListNameGiven() {
		saveOriginalStringToSanitizedCommand();
		parseListNameAndRemoveParsedStr();
		if(this.list == null)	this.list = TaskLists.INBOX;
	}
	
	private void saveOriginalStringToSanitizedCommandAndRemoveExtraSpace() {
		this.sanitizedCommand =
			removeLeadingAndTailingNonDigitLetterDefinedSymbol(this.originalStr);
	}

	private void parseTaskNumAndListRelatedCommand() {
		saveOriginalStringToSanitizedCommand();
		parseNumList();
		parseListNameAndRemoveParsedStr();
		parseNewListName();
		parsePriorityParam();
	}

	private void saveOriginalStringToSanitizedCommand() {
		this.sanitizedCommand = this.originalStr;
	}

	private void parseNewListName() {
		// the new list name is the the second list name which comes later
		this.newListName = parseParamWithBracketTypeAndRemoveParsedStr(regexList);
		
	}

	private void parseNumList() {
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase(regexNumList)){
			String taskNString = removeLeadingAndTailingNonDigitLetterDefinedSymbol(this.matchedStr);
			this.taskNum = getListOfTaskNum(taskNString);
		}
	}

	/**
	 * Format supported:
	 *	delete 1 2-3 3 4 5
	 *	delete 2,4-5,1-9,5
	 *	delete 1~5
	 *	delete 1-5
	 * There would be no repeated number 
	 * 		-- each number would be taken once only
	 * @param taskNumList
	 * @return
	 */
	private Set<Integer> getListOfTaskNum(String taskNumList) {
		Set<Integer> res = new TreeSet<Integer>();
		String numParts[] = removeLeadingAndTailingNonDigitLetterDefinedSymbol(taskNumList).split("[\\ ,]");
		
		for(String numPart:numParts){
			if(numPart.matches("^(\\d)+[-~](\\d)+$")){
				res.addAll(getRangeOfTaskNumFromStr(numPart));
			} else if(numPart.matches("^(\\d)+$")){
				res.add(Integer.parseInt(numPart));
			} else{
				outputErr("Error in parsing list string");
			}
		}
		return res;
	}

	private List<Integer> getRangeOfTaskNumFromStr(String inStr) {
		String numBounds[] = inStr.split("[-~]");
		List<Integer> res = new LinkedList<Integer>();
		int lowerBound =Integer.parseInt(numBounds[0]);
		int upperBound =Integer.parseInt(numBounds[1]);
		for(int i=lowerBound;i<=upperBound;i++){
			res.add(i);
		}
		return res;
	}

	private void parseSearchTask() {
		parseSearchBeforeTime();
		parseSearchAfterTime();
		parseSpecialSearchStr();
		parseParamPlaceAndRemoveParsedStr();
		parseListNameAndRemoveParsedStr();
		parsePriorityParam();
	}

	private void parseSpecialSearchStr() {
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase("today|tomorrow|" + regTimeUnitForSearch)){
			this.searchAfterTime	= DateTime.getInstance();
			String str = removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr);
			
			if(str.equalsIgnoreCase("today")){
				this.searchBeforeTime	= searchAfterTime.clone();
				searchBeforeTime.setTimeToLastSec();
			} 
			
			else if(str.equalsIgnoreCase("tomorrow")){
				this.searchAfterTime.date.add(Calendar.DATE, 1);
				this.searchBeforeTime = this.searchAfterTime.clone();
				this.searchAfterTime.setTimeToFirstSec();
				this.searchBeforeTime.setTimeToLastSec();
			}
			
			else{
				String timeArgs[] = removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr).split("\\ ");
				boolean isNext = (timeArgs[0].equalsIgnoreCase("next"));
				int param = 0;
				
				String unit = timeArgs[1];	// compare the time unit
				if(unit.length() >= 3 && unit.substring(0, 3).equalsIgnoreCase("sec")){
					_searchClearSetFieldHelper(isNext,Calendar.SECOND);
				} else if(unit.length() >= 3 && unit.substring(0, 3).equalsIgnoreCase("min")){
					_searchClearSetFieldHelper(isNext,Calendar.MINUTE);
				} else if(unit.length() >= 1 && unit.substring(0, 1).equalsIgnoreCase("h")){
					_searchClearSetFieldHelper(isNext,Calendar.HOUR_OF_DAY);
				} else if(unit.length() >= 3 && unit.substring(0, 3).equalsIgnoreCase("day")){
					_searchClearSetFieldHelper(isNext, Calendar.DAY_OF_YEAR);
				} else if(unit.equalsIgnoreCase("week")){
					_searchClearSetFieldHelper(isNext, Calendar.WEEK_OF_YEAR);
				} else if(unit.equalsIgnoreCase("month")){
					_searchClearSetFieldHelper(isNext, Calendar.MONTH);
				} else if(unit.equalsIgnoreCase("year")){
					_searchClearSetFieldHelper(isNext, Calendar.YEAR);
				} else if((param = DateTimeProcessor.dateParseGetMonthFromText(unit)) != -1){
					setSearchParamBasedOnFields(param,isNext,Calendar.MONTH,Calendar.YEAR);
				} else if((param = DateTimeProcessor.dateParseGetDayOfWeekFromText(unit)) != -1){
					setSearchParamBasedOnFields(param,isNext,Calendar.DAY_OF_WEEK,Calendar.WEEK_OF_YEAR);
				} else{
					outputErr("Problem with parsing search time period");
				}
			}
			replaceMatchedStringFromSanitizedCommandWithOneSpace();
		}
	}

	private void setSearchParamBasedOnFields(int param, boolean next, int fieldToCompare,int higherOrderYear){
		clearCalendarFields(this.searchAfterTime, fieldToCompare);
		if(next)	this.searchAfterTime.date.add(higherOrderYear, 1);
		this.searchBeforeTime = this.searchAfterTime.clone();
		this.searchBeforeTime.date.add(fieldToCompare, 1);
		this.searchBeforeTime.date.add(Calendar.MILLISECOND, -1);
	}
	
	/**
	 * Clear all the fields lower than given field, exclusive
	 * Say input field is Calendar.MINUTE, then this would clear the 
	 * second and millisecond fields
	 * @param date
	 * @param field
	 */
	private void clearCalendarFields(DateTime date, int field) {
		for(int i=field+1;i<=Calendar.MILLISECOND; i++){
			date.date.clear(i);
		}
		
		if(field >= Calendar.AM_PM){
			date.time = (date.date.getTimeInMillis()/1000)%(DateTime.HOUR_PER_DAY * DateTime.SEC_PER_HOUR);
		}
	}

	private void _searchClearSetFieldHelper(boolean next, int currentField) {
		int lowerField = Calendar.MILLISECOND;
		if(next)	this.searchAfterTime.date.add(currentField, 1);
		this.searchBeforeTime = this.searchAfterTime.clone();
		clearCalendarFields(this.searchBeforeTime,currentField);
		if(next)	clearCalendarFields(this.searchAfterTime,currentField);
		
		this.searchBeforeTime.date.add(currentField, 1);this.searchBeforeTime.time = null;
		this.searchBeforeTime.date.add(lowerField, -1);this.searchAfterTime.time = null;
	}

	private void parseSearchAfterTime() {
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase(
				"after\\ " + regDateTimeOverallFormat)){
			String strToProcess = removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr).substring(6);
			this.searchAfterTime = new DateTimeProcessor(strToProcess).getDateTime();
			
			replaceMatchedStringFromSanitizedCommandWithOneSpace();
		}
	}

	private void parseSearchBeforeTime() {
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase(
				"before\\ " + regDateTimeOverallFormat)){
			String strToProcess = removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr).substring(6);
			this.searchBeforeTime = new DateTimeProcessor(strToProcess).getDateTime();
			
			replaceMatchedStringFromSanitizedCommandWithOneSpace();
		}
	}

	private void parseAddTask() {
		parseParamPlaceAndRemoveParsedStr();
		parsePriorityParam();
		parseListNameAndRemoveParsedStr();
		parseDeadlineAndRemoveParsedStr();
		
		if(!parsedTimePeriod()){
			parseDuration();
			parseDateTimeAndRemoveMatchedStr();
		}
	}
	
	
	private void parseDuration() {
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase(regDurationFormat)){
			String pureDurationStr = removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr).substring(4);
			Long res = regDurationPartsProess(pureDurationStr);
			
			if(res == null)	outputErr("Unsuccessful Duration processing");
			duration = res;
			
			// Note: we do not remove the duration string for user because sometimes
			// 	the duration information would lost in the logic part, due to the 
			// 	current improper dealing with duration
		}
	}
	
	private static Long regDurationPartsProess(String durationStr){
		String durationParts[] = durationStr.trim().split("\\ ");
		long res = 0;
		
		// then try to tell the duration information
		for(int i=0; i<durationParts.length; i+=2){
			int base = Integer.parseInt(durationParts[i]);
			String unit = durationParts[i+1];
			if(unit == null || unit.compareTo("")==0){
				continue;
			}
			if(unit.length() >= 3 && unit.substring(0, 3).compareToIgnoreCase("sec") == 0){
				res += base;
			} else if(unit.length() >= 3 && unit.substring(0, 3).compareToIgnoreCase("min") == 0){
				res += base * DateTime.SEC_PER_MINUTE;
			} else if(unit.length() >= 1 && unit.substring(0, 1).compareToIgnoreCase("h") == 0){
				res += base * DateTime.SEC_PER_HOUR;
			} else if(unit.length() >= 3 && unit.substring(0, 3).compareToIgnoreCase("day") == 0){
				res += base * DateTime.SEC_PER_HOUR * DateTime.HOUR_PER_DAY;
			} else {
				return null;	// when failed to process duration
			}
		}
		return res;
	}
	
	private boolean parseDateTimeAndRemoveMatchedStr(){
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase(regDateTimeOverallFormat)){
			this.startTime = new DateTimeProcessor(matchedStr).getDateTime();
			replaceMatchedStringFromSanitizedCommandWithOneSpace();
			return true;
		}else return false;
	}
	
	private boolean parsedTimePeriod() {
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase(
				regDateTimeOverallFormat + "(\\ )?[\\-\\~](\\ )?" + regDateTimeOverallFormat)){
			String[] timePoint = matchedStr.split("[\\~\\-]");

			DateTime startTime = new DateTimeProcessor(timePoint[0]).getDateTime();
			DateTime endTime = new DateTimeProcessor(timePoint[1]).getDateTime();
			
			regulateStartAndEndDateTime(startTime,endTime);
			
			this.startTime	= startTime;
			this.endTime	= endTime;
			this.duration	= endTime.diff(startTime);
			
			replaceMatchedStringFromSanitizedCommandWithOneSpace();
			return true;
		} else return false;
	}
	
	
	private static void regulateStartAndEndDateTime(DateTime startTime, DateTime endTime) {
		
		boolean startEndTimeHasOneDayDifference = checkStartEndTimeHasOneDayDifference(startTime,endTime);
		
		startTime.setTimeToFirstSecOnNull();
		endTime.setTimeToLastSecOnNull();
		
		if(startTime.date == null && endTime.date == null){	// then default to today
			startTime.date = Calendar.getInstance();
			startTime.optionallyAddOneDayBasedOnCurrentTime();
			endTime.date = (Calendar) startTime.date.clone();
		} else if(startTime.date == null){	// the end date is specified
			startTime.date = (Calendar) endTime.date.clone();
			if(startEndTimeHasOneDayDifference)	startTime.date.add(Calendar.DATE, -1);
		} else if(endTime.date == null){	// the start date is specified
			endTime.date = (Calendar) startTime.date.clone();
			if(startEndTimeHasOneDayDifference)	endTime.date.add(Calendar.DATE, 1);
		} else{								// both start and end date are specified
			// do nothing
		}
	}

	private static boolean checkStartEndTimeHasOneDayDifference(
			DateTime startTime, DateTime endTime) {
		
		// both start and end date are clear
		if(startTime.date != null && endTime.date != null)	return false;
		
		// either start time or end time is unclear --> then no need to set the one day difference
		if(startTime.time == null || endTime == null)	return false;
		
		if(startTime.time > endTime.time)	return true;
		else return false;
	}

	private void parseDeadlineAndRemoveParsedStr() {
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase("by\\ " + regDateTimeOverallFormat)){
			String deadlineStr = removeLeadingAndTailingNonDigitLetterDefinedSymbol(this.matchedStr).substring(3);	// by pass the "by"
			
			deadlineTime = new DateTimeProcessor(deadlineStr).getDateTime();
			replaceMatchedStringFromSanitizedCommandWithOneSpace();
		}
	}

	private void parseParamPlaceAndRemoveParsedStr() {
		this.place = parseParamWithBracketTypeAndRemoveParsedStr(regPlaceFormat);
	}
	
	private void parseListNameAndRemoveParsedStr() {
		this.list = parseParamWithBracketTypeAndRemoveParsedStr(regexList);
	}

	private String parseParamWithBracketTypeAndRemoveParsedStr(String regex) {
		String res = null;
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase(regex)){
			res = removeLeadingAndTailingCharUntilBracketOrLetterOrDigit(matchedStr);
			res = removeLeadingAndTailingBracket(res);
			replaceMatchedStringFromSanitizedCommandWithOneSpace();
		}
		return res;
	}

	private void parsePriorityParam() {
		if(regexMatchWithSanitizedCommandAddWordSpacerCareCase(regPriorityFormat)){
			String priority = removeLeadingAndTailingNonDigitLetterDefinedSymbol(matchedStr).substring(1);
			this.priority = translatePriority(priority);
			replaceMatchedStringFromSanitizedCommandWithOneSpace();
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

	private String removeLeadingAndTailingCharUntilBracketOrLetterOrDigit(
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
				!Character.isLetter(inStr.charAt(length-1))&&
				!Character.isDigit(inStr.charAt(length-1))
				){
			inStr = inStr.substring(0,length-1);
		}
		
		return inStr;
	}

	private void replaceMatchedStringFromSanitizedCommandWithOneSpace() {
		this.sanitizedCommand = this.sanitizedCommand.replace(this.matchedStr, " ");
		
	}

	private boolean regexMatchWithSanitizedCommandAddWordSpacerCareCase(String regex) {
		return regexMatchedStrAndSaveMatchedStr(regWordSpacer + regex + regWordSpacer, sanitizedCommand, false);
	}
	
	@Override
	public Date extractSearchBeforeDate(){
		if(this.searchBeforeTime == null)	return null;
		this.searchBeforeTime.setTimeToLastSecOnNull();
		return this.searchBeforeTime.getDateWithTime();
	}

	@Override
	public Date extractSearchAfterDate() {
		if(this.searchAfterTime == null)	return null;
		this.searchAfterTime.setTimeToFirstSecOnNull();
		return this.searchAfterTime.getDateWithTime();
	}

	@Override
	public Reminder getRemindParamter() {
		return this.reminderType;
	}

	@Override
	public Date getRemindTime() {
		if(this.reminderTime == null)	return null;
		return this.reminderTime.getTime();
	}

	@Override
	public Set<Integer> extractTaskNum() {
		return this.taskNum;
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
	public String extractListName(){
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
		if(this.startTime.date == null)	this.startTime.setDateToBeCurrentTime();
		startTime.optionallyAddOneDayBasedOnCurrentTime();
		return this.startTime.getDateWithTime();
	}

	@Override
	public Long extractStartTime() {
		return this.startTime.time;
	}

	@Override
	public Long extractDuration() {
		if(this.duration != null)	return this.duration;
		if(this.startTime.time != null)	return 0L;
		return null;
	}

	@Override
	public Date extractEndDate(){
		if(this.endTime.date != null)	return this.endTime.getDateWithTime();
		else if(this.duration != null && this.startTime.time != null){
			this.endTime = this.startTime.clone();
			this.endTime.addSecToTime(this.duration);
			return this.endTime.getDateWithTime();
		} else return null;
	}

	@Override
	public Long extractEndTime() {
		if(this.endTime.time != null){
			return this.endTime.time;
		} else if(this.startTime.time !=null && this.duration!= null){
			this.endTime.time = this.startTime.time + this.duration;
			return this.endTime.time;
		} else{
			return this.endTime.time;
		}
	}

	@Override
	public Date extractDeadlineDate(){
		if(this.deadlineTime.time == null && this.deadlineTime.date == null){
			return null;	// no deadline specified
		} else if(this.deadlineTime.date == null){	// means time is not null
			if(this.startTime.date == null && this.startTime.time == null){
				this.deadlineTime.onDateNullDefaultTodayOrNextDayBasedOnTime();
				this.deadlineTime.optionallyAddOneDayBasedOnCurrentTime();
				this.deadlineTime.setTimeOfDateToLastSecOnTimeNull();
			} else if(this.startTime.date != null){
				this.deadlineTime.date = (Calendar) this.startTime.date.clone();
				this.deadlineTime.setTimeOfDateToTime();
			} 
		}
		
		if(this.deadlineTime.date == null)	return null;
		deadlineTime.setTimeOfDateToLastSecOnTimeNull();
		return this.deadlineTime.getDateWithTime();
	}

	@Override
	public Long extractDeadlineTime() {
		if(this.deadlineTime.time == null && this.deadlineTime.date != null)	this.deadlineTime.setTimeToLastSec();
		return this.deadlineTime.getTime();
	}
}