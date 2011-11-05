
package cs2103.t14j1.logic.smartbar;


import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Test;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.storage.Priority;

/**
 * @author SongYY
 *
 */
public class SmartParseTest {
	ParseCommand sp;
	DateTimeProcessor dt;
	Calendar date = Calendar.getInstance();
	int time;
	
	@Test
	public void addBasicTask(){
		sp = new ParseCommand("Add basic task !3");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("basic task",sp.extractTaskName());
		assertEquals(Priority.LOW,sp.extractPriority());
		
		sp = new ParseCommand("Add basic task #home ! 1");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("basic task ! 1",sp.extractTaskName());
		assertEquals("home", sp.extractListName());
		assertEquals(null,sp.extractPriority());
		
		sp = new ParseCommand("Add basic #(life is good) task @home");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("basic task",sp.extractTaskName());
		assertEquals("home",sp.extractPlace());
		assertEquals("life is good",sp.extractListName());
		
		sp = new ParseCommand("Add basic task @(somewhere in the world)");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("basic task",sp.extractTaskName());
		assertEquals("somewhere in the world",sp.extractPlace());
		
		sp = new ParseCommand("Add basic task @(--1984--)");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
		assertEquals("basic task",sp.extractTaskName());
		assertEquals("--1984--",sp.extractPlace());
		
		
	}
	
	@Test
	public void searchTask(){
		sp = new ParseCommand("/songyy");
		assertEquals(Commands.SEARCH, sp.extractCommand());
		assertEquals("songyy", sp.extractTaskName());
	}
	
	@Test
	public void deleteTask(){
		sp = new ParseCommand("del 1");
		assertEquals(Commands.DELETE_TASK, sp.extractCommand());
//		assertEquals(1, (int)sp.extractTaskNum().get(0));
	}
	
	@Test
	public void moveTask(){
		sp = new ParseCommand("mv 1 #lulala");
		assertEquals(Commands.MOVE_TASK, sp.extractCommand());
		
		sp = new ParseCommand("move 12 #lulala");
		assertEquals(Commands.MOVE_TASK, sp.extractCommand());
//		assertEquals(1, (int)sp.extractTaskNum().get(0));
	}

	@Test
	public void editTask(){
		sp = new ParseCommand("edit 1");
		assertEquals(Commands.EDIT_TASK, sp.extractCommand());
	}
	
	@Test
	public void markComplete(){
		sp = new ParseCommand("1 done");
		assertEquals(Commands.MARK_COMPLETE, sp.extractCommand());
		sp = new ParseCommand("done 1");
		assertEquals(Commands.MARK_COMPLETE, sp.extractCommand());
	}
	
	@Test
	public void markPriority(){
		sp = new ParseCommand("1 !3");
		assertEquals(Commands.MARK_PRIORITY, sp.extractCommand());
		sp = new ParseCommand("1 !4");
		assertEquals(Commands.INVALID, sp.extractCommand());
	}
	
	@Test
	public void addReminder(){
		sp = new ParseCommand("remind 1 start");
		assertEquals(Commands.ADD_REMINDER, sp.extractCommand());
		sp = new ParseCommand("remind 2 end");
		assertEquals(Commands.ADD_REMINDER, sp.extractCommand());
		sp = new ParseCommand("remind 2 deadline");
		assertEquals(Commands.ADD_REMINDER, sp.extractCommand());
		sp = new ParseCommand("remind 12 tomorrow");
		assertEquals(Commands.ADD_REMINDER, sp.extractCommand());
		sp = new ParseCommand("remind 12 tomrrow");
		assertEquals(Commands.INVALID, sp.extractCommand());
	}
	
	@Test
	public void removeReminder(){
		sp = new ParseCommand("remind 1 cancel");
		assertEquals(Commands.REMOVE_REMINDER, sp.extractCommand());
		sp = new ParseCommand("remind 1 can");
		assertEquals(Commands.INVALID, sp.extractCommand());
	}
	
	@Test
	public void addList(){
		sp = new ParseCommand("add #abc");
		assertEquals(Commands.ADD_LIST, sp.extractCommand());
		sp = new ParseCommand("add #(flyfy1 songyy)");
		assertEquals(Commands.ADD_LIST, sp.extractCommand());
	}
	
	@Test
	public void editList(){
		sp = new ParseCommand("edit #abc");
		assertEquals(Commands.EDIT_LIST, sp.extractCommand());
		sp = new ParseCommand("edit #(flyfy1 songyy)");
		assertEquals(Commands.EDIT_LIST, sp.extractCommand());
	}
	
	@Test
	public void renameList(){
		sp = new ParseCommand("rename #abc");
		assertEquals(Commands.INVALID, sp.extractCommand());
		sp = new ParseCommand("rename #(flyfy1 songyy) #(luala)");
		assertEquals(Commands.RENAME_LIST, sp.extractCommand());
	}
	
	@Test
	public void deleteList(){
		sp = new ParseCommand("delete #abc");
		assertEquals(Commands.DELETE_LIST, sp.extractCommand());
		sp = new ParseCommand("del #abc");
		assertEquals(Commands.DELETE_LIST, sp.extractCommand());
	}
	
	@Test
	public void switchList(){
		sp = new ParseCommand("#abc");
		assertEquals(Commands.SWITCH_LIST, sp.extractCommand());
		sp = new ParseCommand("#(abc)");
		assertEquals(Commands.SWITCH_LIST, sp.extractCommand());
		sp = new ParseCommand("#");	
		assertEquals(Commands.SWITCH_LIST, sp.extractCommand());
	}
	
	@Test
	public void displayList(){
		sp = new ParseCommand("dis #abc");
		assertEquals(Commands.DISPLAY_LISTS, sp.extractCommand());
		sp = new ParseCommand("display");
		assertEquals(Commands.DISPLAY_LISTS, sp.extractCommand());
		sp = new ParseCommand("dis #(abc)");	
		assertEquals(Commands.DISPLAY_LISTS, sp.extractCommand());
	}
	
	@Test
	public void displayTask(){
		sp = new ParseCommand("dis 1");
		assertEquals(Commands.DISPLAY_TASK, sp.extractCommand());
	}
	
	@Test
	public void testOneStatementCommands(){
		sp = new ParseCommand("undo");
		assertEquals(Commands.UNDO, sp.extractCommand());
		
		sp = new ParseCommand("exit");
		assertEquals(Commands.EXIT, sp.extractCommand());
		
		sp = new ParseCommand("redo");
		assertEquals(Commands.REDO, sp.extractCommand());
		
		sp = new ParseCommand("undo ");
		assertEquals(Commands.UNDO, sp.extractCommand());
		
		sp = new ParseCommand("exit	");
		assertEquals(Commands.EXIT, sp.extractCommand());
		
		sp = new ParseCommand("redo\n");
		assertEquals(Commands.REDO, sp.extractCommand());
	}
	
	@Test
	public void testOneStatementCommandsSpecialCase(){
		
		date = Calendar.getInstance(); DateTime.clearTimeFieldForDate(date);
		dt = new DateTimeProcessor("10:30:20 ,12/Oct/2011");
		setTime(10, 30, 20);date.set(2011, Calendar.OCTOBER, 12, 10, 30, 20);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
		assertTrue(getMessageInTestingDate(),dt.getDateTime().getDate().equals(date.getTime()));
		
		date = Calendar.getInstance();
		dt = new DateTimeProcessor("at 5 pm tomorrow");
		setTime(17, 0, 0);
		date.set(Calendar.HOUR_OF_DAY, 17);	date.add(Calendar.DATE, 1);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
		assertTrue(getMessageInTestingDate(),dt.getDateTime().getDate().equals(date.getTime()));
		
		date = Calendar.getInstance();
		dt = new DateTimeProcessor("at 1am 12th,Dec,2013");
		setTime(1, 0, 0);date.set(2013, Calendar.DECEMBER, 12,1,0,0);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
		assertTrue(getMessageInTestingDate(),dt.getDateTime().getDate().equals(date.getTime()));
		
		date = Calendar.getInstance();DateTime.clearTimeFieldForDate(date);
		dt = new DateTimeProcessor("1:30 pm Dec,12th,2020");
		setTime(13,30,0);date.set(2020, Calendar.DECEMBER, 12, 13, 30, 0);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
		assertTrue(getMessageInTestingDate(),dt.getDateTime().getDate().equals(date.getTime()));
		
		date = Calendar.getInstance();DateTime.clearTimeFieldForDate(date);
		dt = new DateTimeProcessor("1:30am today");
		setTime(1, 30, 0); date.set(Calendar.HOUR_OF_DAY, 1);date.set(Calendar.MINUTE, 30);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
		assertTrue(getMessageInTestingDate(),dt.getDateTime().getDate().equals(date.getTime()));
		
		
		date = Calendar.getInstance(); DateTime.clearTimeFieldForDate(date);
		dt = new DateTimeProcessor("10:30:20pm next Wednesday");
		setTime(22, 30, 20);date.add(Calendar.WEEK_OF_YEAR, 1);
		date.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		DateTime.clearTimeFieldForDate(date);date.set(Calendar.SECOND, time);
		assertTrue(getMessageInTestingTime(),dt.getDateTime().getTime() == time);
	}
	
	private void setTime(int hour, int min, int sec) {
		time = hour * DateTimeProcessor.SEC_PER_HOUR + 
				min*DateTimeProcessor.SEC_PER_MINUTE +
				sec;
		
	}

	/* Test on time processor */
	private String getMessageInTestingDate() {
		return 
			"\nres	= " + dt.getDateTime().getDate() + 
			"\nwant	= " + date.getTime();
	}
	
	private String getMessageInTestingTime(){
		return
			"\nres	= " + dt.getDateTime().getTime() +
			"\nwant	= " + time;
	}
	
	
	@Test
	public void testDateProcessor(){
		
	}
}
