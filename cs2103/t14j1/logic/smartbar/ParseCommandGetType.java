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
public abstract class ParseCommandGetType extends RegexMatcher{
	protected boolean parsingCompleted = false;
	protected Commands commandType;
	
	/**
	 * The string removed the unnecessary part, for example:
	 * the add task command removed the add task
	 * 	"add meeting tomorrow" => "meeting tomorrow"
	 * the search task command remove the "/", as well as the leading space if has
	 * 	"/ before 10am" => "before 10am"
	 */
	protected String sanitizedCommand;
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
		super(command);
		// save the original command string
		this.originalStr = command;
		
		firstLevelCommandParsing();
	}
	
	private void firstLevelCommandParsing(){
		addCommandRegexFormatToCommandTypeMapping();
		
		if(originalStr == null){
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
		
		this.commandType = Commands.INVALID;	this.parsingCompleted = true;
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
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regRemoveReminder, Commands.REMOVE_REMINDER));
			
			// These are the less restrictive ones; should check in the end
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regexSearchCommand,Commands.SEARCH));
		regexCommandsFormatToMatchWithoutMarkingParseComplete.add(
				new Pair<String,Commands>(regexAddCommand,Commands.ADD_TASK));
	}

	private boolean handleMatchedCommandWithRegexFormat(Pair<String,Commands> regexCommandPair){
		if(regexMatchedStrAndSaveMatchedStr(regexCommandPair.first,
				originalStr,IGNORE_CASE)){
			this.commandType = regexCommandPair.second;
			return true;
		} else{
			return false;
		}
	}

	private void removeMatchedStringFromOriginalCommand() {
		this.sanitizedCommand = this.originalStr.replace(this.matchedStr, "");
	}
}
