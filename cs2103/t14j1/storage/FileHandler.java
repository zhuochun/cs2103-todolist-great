package cs2103.t14j1.storage;

import java.io.File;
import java.io.IOException;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	
	// add in magic string for the list tag
	private static final String xmlListTag = "list";
	private static final String xmlTaskTag = "task";
	
	// magic string for priority tag
	private static final String xmlPriorityLow = "low";
	private static final String xmlPriorityNormal = "normal";
	private static final String xmlPriorityHigh = "high";
	
	// this should not appaer; create this just in case
	private static final String xmlUndefined = "undefine";	
	
	private static final String LOAD_SUCCESS = "All Lists and Tasks are Ready!";
	private static final String SAVE_SUCCESS = "All Lists and Tasks are Saved!";
	
	
	
	public static String loadAll(TaskLists lists) throws SAXException, IOException, ParserConfigurationException {
		loadLists(lists);
		loadTasks(lists);
		
		return LOAD_SUCCESS;
	}
	
	private static boolean readXmlDocument(String folder, String fileName, Document document) throws SAXException, IOException, ParserConfigurationException{
		File directory = new File(folder);
		if(!directory.exists()){
			directory.mkdir();
		}
		
		File fXmlFile = new File(folder + fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		document = dBuilder.parse(fXmlFile);
		
		return true;
	}
	
	private static void loadLists(TaskLists lists) throws SAXException, IOException, ParserConfigurationException {
		Document doc = null;
		readXmlDocument(fileFolder,taskFileName,doc);
		
		NodeList listElements = doc.getElementsByTagName(xmlListTag);
		for(int i = 0; i < listElements.getLength(); i++){
			Node listElement = listElements.item(i);
			
			if(listElement.getNodeType() == Node.ELEMENT_NODE){
				Element eListElement = (Element) listElement;
				lists.add(eListElement.getNodeValue());
			} else{
				throw new SAXException("Wrong Element Type when parsing list file");
			}
		}
	}

	
	private static void loadTasks(TaskLists lists) throws SAXException, IOException, ParserConfigurationException {
		Document doc = null;
		readXmlDocument(fileFolder,taskFileName,doc);
		
		NodeList taskElements = doc.getElementsByTagName(xmlTaskTag);
		for(int i=0; i<taskElements.getLength(); i++){
			// Node here is a task list
			Node taskElement = taskElements.item(i);
			NamedNodeMap taskAttr = taskElement.getAttributes();
			String name = (taskAttr.getNamedItem(Task.NAME)).getNodeValue();
			String list = (taskAttr.getNamedItem(Task.LIST)).getNodeValue();
			Priority priority = translateStringToPriority((taskAttr.getNamedItem(Task.PRIORITY)).getNodeValue());
			boolean status = Boolean.parseBoolean((taskAttr.getNamedItem(Task.STATUS)).getNodeValue());
			
			String startDateStr = (taskAttr.getNamedItem(Task.START_DATE)).getNodeValue();
			Date startDate = null;
			if(startDateStr.compareTo(xmlUndefined)!=0){
				startDate = DateFormat.strToDateLong(startDateStr);
			}
			String endDateStr = (taskAttr.getNamedItem(Task.END_DATE)).getNodeValue();
			Date endDate = null;
			if(endDateStr.compareTo(xmlUndefined)!=0){
				endDate = DateFormat.strToDateLong(endDateStr);
			}
			
			// TODO: the time also need that kind of handling
//			Long start_time = Long.parseLong((taskAttr.getNamedItem(Task.START_TIME)).getNodeValue());
//			Long end_time = Long.parseLong((taskAttr.getNamedItem(Task.END_TIME)).getNodeValue());
			
			lists.addTask(list, new Task(name,list,priority,startDate,endDate,status));
		}
	}
	
	private static Priority translateStringToPriority(String priorityStr) throws IOException {
		if(priorityStr.compareTo(xmlPriorityLow) == 0){
			return Priority.LOW;
		} else if(priorityStr.compareTo(xmlPriorityNormal) == 0){
			return Priority.NORMAL;
		} else if(priorityStr.compareTo(xmlPriorityHigh) == 0){
			return Priority.IMPORTANT;
		} else{
			throw new IOException("Priority parsing problem when reading the Task xml File.");
		}
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
		Element rootElement = doc.createElement(xmlListTag);
		doc.appendChild(rootElement);
		
		String listNames[] = lists.getListNames();
		
		for (String listName : listNames) {
			TaskList list = lists.getList(listName);
			
			Element listElement = doc.createElement(listName);
			rootElement.appendChild(listElement);
			
			int numTasks = list.getSize();
			for (int i = 0; i < numTasks; i++) {
				Task task = list.getTask(i);
				
				Element taskElement = doc.createElement(xmlTaskTag);
				listElement.appendChild(taskElement);
				
				listElement.setAttribute(Task.NAME, task.getName());
				listElement.setAttribute(Task.LIST, task.getList());
				listElement.setAttribute(Task.PRIORITY,translatePriorityToString(task.getPriority()));
				listElement.setAttribute(Task.START_DATE, translateStrToXML(task.getStartLong()));
				listElement.setAttribute(Task.END_DATE, translateStrToXML(task.getEndLong()));
				listElement.setAttribute(Task.START_TIME, translateStrToXML(task.getStartTime()));
				listElement.setAttribute(Task.END_TIME, translateStrToXML(task.getEndTime()));
				listElement.setAttribute(Task.STATUS, Boolean.toString(task.getStatus()));
				// would implement this later cuz currently not defined in the task
				// listElement.setAttribute(Task.PLACE, task.getList());
				
			}
		}
		
		if(saveXmlDocument(fileFolder, taskFilePath, doc)){
			System.out.println("Task Saved.");
		} else{
			System.out.println("Task Saving Failed.");
		}
	}
	
	private static String translateStrToXML(String input){
		return (input == null)?xmlUndefined:input;
	}
	
	private static String translatePriorityToString(Priority priority) {
		String priorityStr = null;
		switch (priority) {
		case IMPORTANT:
			priorityStr = xmlPriorityHigh;break;
		case NORMAL:
			priorityStr = xmlPriorityNormal;break;
		case LOW:
			priorityStr = xmlPriorityLow;	break;
		default:
			priorityStr = xmlUndefined;
		}
		return priorityStr;
	}

	public static void main(String args[]) throws ParserConfigurationException, TransformerException{
		String names[] = {"a", "b", "c"};
		saveLists(names);
		System.out.println("Something here.");
	}
}