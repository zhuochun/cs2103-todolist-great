package cs2103.t14j1.logic;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cs2103.t14j1.logic.smartbar.ParseCommand;
import cs2103.t14j1.storage.FileHandler;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskLists;

/**
 * It is the centre of logic layer.
 * It also serves as the command line interface for version 0.1
 * Afterwards, it serve as the bridge between user interface and logic
 *
 * @author Zhuochun, Shubham
 * 
 */
class Control {
	
	TaskLists lists;

	/**
	 * command line interface, just for version 0.1
	 * will be replaced by user interface, so I keep it short
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		Control cml  = new Control();
		String  line, result;
		
		while (true) {
			System.out.print("Command: ");
			line = scan.nextLine();
			result = cml.processInput(line); // result is a String that will be display in statusBar in GUI
			System.out.println(result);
		}
	}
	
	/**
	 * constructor
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public Control() throws SAXException, IOException, ParserConfigurationException {
		lists = new TaskLists();
		FileHandler.loadAll(lists); // call storage to load all lists and tasks from file
	}
	
	/**
	 * process input line
	 * 
	 * @param input
	 * @return
	 * @throws Exception 
	 */
	public String processInput(String input) throws Exception {
		ParseCommand parseCommand = new ParseCommand(input);
		Commands command = parseCommand.extractCommand();
		String feedback  = executeCommand(command, input);
		return feedback;
	}

	/**
	 * execute command
	 * 
	 * @param command
	 * @param input
	 * @return feedback
	 * @throws Exception 
	 */
	public String executeCommand(Commands command, String input) throws Exception {
		switch (command) {
		case ADD_TASK:
			return addTask(input);
		case ADD_LIST:
			return addList(input);
		case SEARCH:
			return search(input);
		case DELETE_TASK:
			return deleteTask(input);
		case DELETE_LIST:
			return deleteList(input);
		case EDIT_TASK:
			return editTask(input);
		case EDIT_LIST:
			return editList(input);
		case SORT:
			return sortBy(input);
		// SWITCH_LIST is a GUI action
		default:
			return "invalid command";
		}
	}

	/**
	 * add task into TaskLists
	 * 
	 * @param input
	 * @return
	 * @throws Exception 
	 */
	private String addTask(String input) throws Exception {
		ParseCommand parseCommand = new ParseCommand(input);
		String name        = parseCommand.extractTaskName();
		String list        = parseCommand.extractListName();
		Priority priority  = parseCommand.extractPriority();
		Date startDateTime = parseCommand.extractStartDate();
		Date endDateTime   = parseCommand.extractEndDate();
		boolean status    = Task.NOT_COMPLETED;
		
		Task newTask = new Task(name, list, priority, startDateTime, endDateTime, status);
		String result = lists.addTask(list, newTask);
		
		return result;
	}

	private String addList(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String search(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String deleteTask(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String deleteList(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String editTask(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String editList(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private String sortBy(String input) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
}
