package cs2103.t14j1.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map.Entry;

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
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import cs2103.t14j1.logic.DateFormat;


/**
 * the file handler to load from and save to files
 * 
 * @author Zhuochun, Yangyu
 *
 */
public class FileHandler {
    
	private static final String fileFolder   = "user/";
	private static final String taskFileName = "tasks.xml";
	private static final String listFileName = "lists.xml";
	
    private static final String xmlListsTag  = "lists";
    private static final String xmlListTag   = "list";
    private static final String xmlTasksTag  = "tasks";
    private static final String xmlTaskTag   = "task";
    private static final String xmlUndefined = "undefine";

	private static final String LOAD_SUCCESS = "All Lists and Tasks are Ready!";
	private static final String SAVE_SUCCESS = "All Lists and Tasks are Saved!";
	
	/**
	 * save lists and tasks from application to xml files
	 * 
	 * @param lists
	 */
	public static String saveAll(TaskLists lists) {

    	saveLists(lists);
    	saveTasks(lists);
    	
    	return SAVE_SUCCESS;
    }

    /**
	 * load lists and tasks from xml file to application
	 * 
	 * @param lists
	 */
	public static String loadAll(TaskLists lists) {
	    
	    loadLists(lists);
	    loadTasks(lists);
		
		return LOAD_SUCCESS;
	}
	
    private static boolean saveLists(TaskLists lists) {
        boolean result = false;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // root elements - lists
            Element rootElement = doc.createElement(xmlListsTag);
            doc.appendChild(rootElement);

            // individual element - each list
            for (Entry<String, TaskList> list : lists) {
                Element listElement = doc.createElement(xmlListTag);
                rootElement.appendChild(listElement);
                Text listName = doc.createTextNode(list.getKey());
                listElement.setAttribute(xmlTasksTag, Integer.toString(list.getValue().getSize()));
                listElement.appendChild(listName);
            }

            result = saveXmlDocument(fileFolder, listFileName, doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static void loadLists(TaskLists lists) {
    	Document doc = openXmlDocument(fileFolder, listFileName);
    	
    	if (doc == null) { // if doc failed to load
    	    return ;
    	}
    	
    	NodeList rootElement = doc.getElementsByTagName(xmlListTag);
    	for(int i = 0; i < rootElement.getLength(); i++){
    		Node listElement = rootElement.item(i);
    		
    		if (listElement.getNodeType() == Node.ELEMENT_NODE) {
    		    String listName = listElement.getTextContent();
    		    lists.add(listName);
    		}
    	}
    }

    private static boolean saveTasks(TaskLists lists) {
        boolean result = false;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(xmlTasksTag);
            doc.appendChild(rootElement);

            // a task element
            for (Entry<String, TaskList> list : lists) {
                TaskList thisList = list.getValue();
                for (Task task : thisList) {
                    Element taskElement = doc.createElement(xmlTaskTag);
                    rootElement.appendChild(taskElement);

                    taskElement.setAttribute(xmlListTag, task.getList());
                    taskElement.setAttribute(Task.PRIORITY, task.getPriority().toString());
                    taskElement.appendChild(createElement(Task.NAME, task.getName(), doc));
                    taskElement.appendChild(createElement(Task.PLACE, task.getPlace(), doc));
                    taskElement.appendChild(createElement(Task.START_DATE, task.getStartLong(), doc));
                    taskElement.appendChild(createElement(Task.END_DATE, task.getEndLong(), doc));
                    taskElement.appendChild(createElement(Task.DEADLINE, task.getDeadlineLong(), doc));
                    taskElement.appendChild(createElement(Task.DURATION, task.getDuration() == null ? null : Long.toString(task.getDuration()), doc));
                    taskElement.appendChild(createElement(Task.STATUS, task.getStatusStr(), doc));
                }
            }
            result = saveXmlDocument(fileFolder, taskFileName, doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return result;
    }
    
    private static Node createElement(String tag, String info, Document doc) {
        Element node = doc.createElement(tag);
        Text contain = doc.createTextNode(strToXml(info));
        node.appendChild(contain);
        return node;
    }

    private static void loadTasks(TaskLists lists) {
        Document doc = openXmlDocument(fileFolder, taskFileName);

        if (doc == null) { // if doc failed to load
            return;
        }

        NodeList taskElements = doc.getElementsByTagName(xmlTaskTag);
        for (int i = 0; i < taskElements.getLength(); i++) {
            // load task element
            Node taskNode = taskElements.item(i);
            
            // load task element's attributes
            NamedNodeMap taskAttr = taskNode.getAttributes();
            // load task element's tags
            Element taskElement = (Element) taskNode;
            
            // extract all task informations
            String   name       = getTagValue(Task.NAME, taskElement);
            String   place      = getTagValue(Task.PLACE, taskElement);
            String   list       = taskAttr.getNamedItem(xmlListTag).getNodeValue();
            Priority priority   = getPriorityAttr(taskAttr);
            String startDateStr = getTagValue(Task.START_DATE, taskElement);
            Date   startDate    = DateFormat.strToDateLong(startDateStr);
            String endDateStr   = getTagValue(Task.END_DATE, taskElement);
            Date   endDate      = DateFormat.strToDateLong(endDateStr);
            String deadlineStr  = getTagValue(Task.DEADLINE, taskElement);
            Date   deadline     = DateFormat.strToDateLong(deadlineStr);
            String durationStr  = getTagValue(Task.DURATION, taskElement);
            Long   duration     = durationStr == null ? null : Long.parseLong(durationStr);
            String statusStr    = getTagValue(Task.STATUS, taskElement);
            boolean status     = statusStr.compareToIgnoreCase("completed") == 0;
            
            // add task to correct list
            lists.addTask(list, new Task(name, place, list, priority, startDate, endDate, deadline, duration, status));
        }
	}

    private static String getTagValue(String tag, Element element) {
        NodeList nlList = element.getElementsByTagName(tag);
        
        Node nValue = nlList.item(0);
        String result = nValue.getTextContent();
        
        if (result.compareToIgnoreCase(xmlUndefined) == 0)
            return null;
        
        return result;
    }

    private static Priority getPriorityAttr(NamedNodeMap taskAttr) {
        String priority = taskAttr.getNamedItem(Task.PRIORITY).getNodeValue();
	    return Priority.valueOf(priority.toUpperCase());
    }

    private static boolean saveXmlDocument(String directory, String fileName, Document document) {
    	File fileDirectory = new File(directory);
    	
    	if(!fileDirectory.exists()){
    		fileDirectory.mkdir();
    	}
    	
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(directory + fileName));
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    	
        return true;
    }

    private static Document openXmlDocument(String folder, String fileName) {
    	File directory = new File(folder);
    	
    	if(!directory.exists()){
    		directory.mkdir();
    	}
    	
    	File fXmlFile = new File(folder + fileName);
    	if (!fXmlFile.isFile()) {
    	    createFile(folder + fileName);
    	    return null;
        }
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    
    	DocumentBuilder dBuilder;
    	Document document = null;
            
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(fXmlFile);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    	
    	return document;
    }

    private static String strToXml(String input){
    	return (input == null) ? xmlUndefined : input;
    }
    
    /**
     * create an empty text file under filename
     * 
     * @param filename
     * @return true if successfully created
     */
    public static boolean createFile(String filename) {
        boolean result = false;
        
        try {
            File       file  = new File(filename);
            FileWriter write = new FileWriter(file);
            
            write.close();
            
            result = true;
        } catch (IOException e) {
            result = false;
        }
    
        return result;
    }
    
}