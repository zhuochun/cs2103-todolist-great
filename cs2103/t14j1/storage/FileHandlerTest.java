package cs2103.t14j1.storage;

import java.util.Date;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs2103.t14j1.logic.DateFormat;

/**
 * this jUnit class tests the fileHandler on load (save) lists and tasks
 * 
 * @author Zhuochun
 * 
 */
public class FileHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSaveAll() {
        TaskLists lists = new TaskLists();

        // add one task
        String name = "new task";
        String place = null;
        String list = "Inbox";
        Priority priority = Priority.IMPORTANT;
        Date startDateTime = null;
        Date endDateTime = null;
        Date deadline = null;
        Long duration = new Long(20);
        boolean status = Task.INCOMPLETE;
        
        Task newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
        lists.addTask(list, newTask);
        
        // add another task
        name = "new task 2";
        place = "i dont have a place";
        list = "List 1";
        priority = Priority.NORMAL;
        startDateTime = DateFormat.strToDateLong("2011-9-20 14:20:20");
        endDateTime = DateFormat.strToDateLong("2011-9-20 15:20:30");
        deadline = DateFormat.strToDateLong("2011-10-6 20:20:20");
        status = Task.INCOMPLETE;
        
        newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
        lists.addTask(list, newTask);
        
        // add an empty task and set properties one by one
        newTask = new Task();
        
        newTask.setName("task 3");
        newTask.setPlace("hell");
        newTask.setPriority(Priority.LOW);
        newTask.setStartDateTime(null);
        newTask.setEndDateTime(null);
        newTask.setDeadline(DateFormat.strToDate("2011-10-10"));
        newTask.setDuration(new Long(20));
        newTask.setStatus(Task.COMPLETED);

        lists.addTask("Inbox", newTask);
        FileHandler.saveAll(lists);
    }

    @Test
    public void testLoadAll() {
        TaskLists newLists = new TaskLists();
        
        TaskList temp = new TaskList("name");
        FileHandler.loadAll(newLists, temp);
        
        for (Entry<String, TaskList> list : newLists) {
            System.out.println(list.getKey());
            
            for (Task task : list.getValue()) {
                System.out.println("\t" + task.getName());
                System.out.println("\t" + task.getList());
                System.out.println("\t" + task.getStartLong());
                System.out.println("\t" + task.getEndLong());
                System.out.println("\t" + task.getDeadlineLong());
                System.out.println("\t" + task.getStatusStr());
                System.out.println("\t=====");
            }
            
            System.out.println("--------------");
        }
    }

}
