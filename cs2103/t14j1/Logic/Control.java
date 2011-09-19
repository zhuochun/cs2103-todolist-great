package cs2103.t14j1.logic;

import java.util.ArrayList;
import java.util.Scanner;

import cs2103.t14j1.storage.*;
import cs2103.t14j1.logic.smartbar.*;

/**
 * It is the centre of logic.
 * It also serves as the command line interface for version 0.1
 * Later on, it serve as the bridge between user interface and logic
 *
 * @author Zhuochun
 * 
 */
class Control {
	
	ArrayList<TaskList> lists;
	
	private static final int TRASH = 0; // in lists, TRASH is lists[1]
	private static final int INBOX = 1; // in lists, INBOX is lists[0]

	/**
	 * command line interface, just for version 0.1
	 * will be replaced by user interface, so I keep it short
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
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
	 */
	public Control() {
		lists = new ArrayList<TaskList>();
		storageLoad(lists); // call storage to load all lists from file
	}
	
	/**
	 * process input line
	 * 
	 * @param input
	 * @return
	 */
	public String processInput(String input) {
		Commands command = extractCommand(input);
		String feedback  = executeCommand(command, input);
		return feedback;
	}

	/**
	 * execute command
	 * 
	 * (public, because in GUI, processInput() will be done in GUI)
	 * 
	 * @param command
	 * @param input
	 * @return
	 */
	public String executeCommand(Commands command, String input) {
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

	private String addTask(String input) {
		// TODO Auto-generated method stub
		
	}

	private String addList(String input) {
		// TODO Auto-generated method stub
		
	}

	private String search(String input) {
		// TODO Auto-generated method stub
		
	}

	private String deleteTask(String input) {
		// TODO Auto-generated method stub
		
	}

	private String deleteList(String input) {
		// TODO Auto-generated method stub
		
	}

	private String editTask(String input) {
		// TODO Auto-generated method stub
		
	}

	private String editList(String input) {
		// TODO Auto-generated method stub
		
	}

	private String sortBy(String input) {
		// TODO Auto-generated method stub
		
	}
}
