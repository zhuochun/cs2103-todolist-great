package cs2103.t14j1.Logic;

import cs2103.t14j1.Storage.Task;
import java.lang.*;

class Control {
	
	/**
	 * @param input - The input string passed by the GUI
	 * returns a boolean value which denotes whether the desired input operation was 
	 * successful or not 
	 */
	public Boolean processInput (String input) {
		Commands command = cs2103.t14j1.Logic.smartBarParse.SmartBarParseCommand.extractCommand();
		executeCommand(command);
	}

	private void executeCommand(Commands command) {
		switch (command) {
		case ADD: smartAdd();
		break;
		case SEARCH: smartSearch();
		break;
		case SWITCHLIST: smartSwitch();
		break;
		}
	}

	private void smartAdd() {
		Task
	}

	private Task createNewTask() {
		
		long startDate = cs2103.t14j1.Logic.smartBarParse.SmartBarParseCommand.extractStartDate();
		long startTime = cs2103.t14j1.Logic.smartBarParse.SmartBarParseCommand.extractStartTime();
		long endDate = cs2103.t14j1.Logic.smartBarParse.SmartBarParseCommand.extractEndDate();
		long endTime = cs2103.t14j1.Logic.smartBarParse.SmartBarParseCommand.extractEndTime();
		long duration = cs2103.t14j1.Logic.smartBarParse.SmartBarParseCommand.extractDuration();
		String place = cs2103.t14j1.Logic.smartBarParse.SmartBarParseCommand.extractPlace();
		String listCategories[] = cs2103.t14j1.Logic.smartBarParse.SmartBarParseCommand.extractListName();
		long deadline = cs2103.t14j1.Logic.smartBarParse.SmartBarParseCommand.extractDeadline();
		int priority = cs2103.t14j1.Logic.smartBarParse.SmartBarParseCommand.extractPriority();
		
		Task newTask = 
	}
	
	smartAdd()
	
	
}
