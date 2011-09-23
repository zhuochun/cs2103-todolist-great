package cs2103.t14j1.storage;

import java.io.File;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cs2103.t14j1.logic.DateFormat;


/**
 * the file handler to load from and save to files
 * 
 * TODO: Worapol, I have wrote the basic functions and demo, you only need to add
 *       code that open XML files, read it and save it.
 * 
 * @author Zhuochun, Worapol
 *
 */
public class FileHandler {
	
	/** Songyy's notes at 2011-9-23 9:17:08
	 *  initially this is not final; I don't understand why... Changed it to 
	 *  final; also separated the folder from the filename 
	 */
	// using on Windows, \\ is needed.
	private static final String fileFolder = "user\\";
	private static final String taskFileName = "tasks.txt";
	private static final String listFileName = "lists.txt";
	
	private static final String LOAD_SUCCESS = "All Lists and Tasks are Ready!";
	private static final String SAVE_SUCCESS = "All Lists and Tasks are Saved!";
	
	
	
	public static String loadAll(TaskLists lists) {
		loadLists(lists);
		loadTasks(lists);
		
		return LOAD_SUCCESS;
	}
	
	private static void loadLists(TaskLists lists) {
		// this is a demo of how to add a list
		lists.add("test list");
		
		// TODO: open the listFile and load it lists
	}

	
	private static void loadTasks(TaskLists lists) {
		
		// these are demos of how to add a task into list
		// delete these demos only after you finished the loading
		// because they are used for other layer for testing
		
		// add one task
		String name = "new task";
		String list = "inbox";
		Priority priority = Priority.IMPORTANT;
		Date startDateTime = null;
		Date endDateTime = null;
		boolean status = Task.NOT_COMPLETED;
		Task newTask = new Task(name, list, priority, startDateTime, endDateTime, status);
		lists.addTask(list, newTask);
		
		// add another task
		name = "new task 2";
		list = "inbox";
		priority = Priority.NORMAL;
		startDateTime = DateFormat.strToDateLong("2011-9-20 14:20:20");
		endDateTime = DateFormat.strToDateLong("2011-9-20 15:20:30");
		status = Task.NOT_COMPLETED;
		newTask = new Task(name, list, priority, startDateTime, endDateTime, status);
		lists.addTask(list, newTask);
		
		// TODO: open the taskFile and load all the tasks into lists
		
	}
	
	public static String saveAll(TaskLists lists) throws ParserConfigurationException, TransformerException {
		String[] listNames = lists.getListNames(); // it will give you all the lists name
		
		saveLists(listNames);
		saveTasks(lists);
		
		return SAVE_SUCCESS;
	}
	
	private static boolean saveXmlDocument(String directory, String fileName,Document document) throws TransformerException{
		// if directory doesn't exist, create it
		File fileDirectory = new File(directory);
		if(!fileDirectory.exists()){
			fileDirectory.mkdir();
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(directory + fileName));
		
		transformer.transform(source, result);
		return true;
	}
	
	/**
	 * 
	 * 
	 * @param lists
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	private static void saveLists(String[] names) throws ParserConfigurationException, TransformerException {
		String listFilePath = fileFolder + listFileName;
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("list");
		doc.appendChild(rootElement);
		
		for(String name: names) {
			rootElement.appendChild(doc.createElement(name));
		}
		
		if(saveXmlDocument(fileFolder,listFilePath,doc)){
			System.out.println("List Saved");
		} else{
			System.out.println("List Saving Failed");
		}
		
	}
	
	/**
	 * Zhuochun's note:
	 * TODO: this is slow to extract list one by one using name,
	 *       there is better solution which is to implement iteration for
	 *       taskLists class. (left this to next version)
	 ******************
	 * Songyy's note:
	 *  Initially there're two parameters: String names[], and TaskLists list
	 *   (I assume the names[] is an array of the list's names)
	 *  It's unnecessary to put the names[] here, because we can get this by 
	 *   calling the method getListNames 
	 ******************
	 * @param lists
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	private static void saveTasks(TaskLists lists) throws ParserConfigurationException, TransformerException {
		
		String taskFilePath = fileFolder + taskFileName;
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("list");
		doc.appendChild(rootElement);
		
		String listNames[] = lists.getListNames();
		
		for (String listName : listNames) {
			TaskList list = lists.getList(listName);
			
			Element listElement = doc.createElement(listName);
			rootElement.appendChild(listElement);
			
			int numTasks = list.getSize();
			for (int i = 0; i < numTasks; i++) {
				Task task = list.getTask(i);
				
				Element taskElement = doc.createElement("task");
				listElement.appendChild(taskElement);
				
				listElement.setAttribute(Task.NAME, task.getName());
				listElement.setAttribute(Task.LIST, task.getList());
				listElement.setAttribute(Task.PRIORITY,translatePriorityToString(task.getPriority()));
				listElement.setAttribute(Task.START_DATE, task.getStartLong());
				listElement.setAttribute(Task.END_DATE, task.getEndLong());
				listElement.setAttribute(Task.START_TIME, task.getStartTime());
				listElement.setAttribute(Task.END_TIME, task.getEndTime());
				listElement.setAttribute(Task.STATUS, Boolean.toString(task.getStatus()));
			}
		}
		
		if(saveXmlDocument(fileFolder, taskFilePath, doc)){
			System.out.println("Task Saved.");
		} else{
			System.out.println("Task Saving Failed.");
		}
	}
	
	
	private static String translatePriorityToString(Priority priority) {
		String priorityStr = null;
		switch (priority) {
		case IMPORTANT:
			priorityStr = "important";break;
		case NORMAL:
			priorityStr = "nurmal";	break;
		case LOW:
			priorityStr = "low";	break;
		default:
			priorityStr = "undefined";
		}
		return priorityStr;
	}

	public static void main(String args[]) throws ParserConfigurationException, TransformerException{
		String names[] = {"a", "b", "c"};
		saveLists(names);
		System.out.println("Something here.");
	}
}