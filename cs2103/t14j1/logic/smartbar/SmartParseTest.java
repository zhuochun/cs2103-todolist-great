
package cs2103.t14j1.logic.smartbar;


import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.t14j1.logic.Commands;

/**
 * @author SongYY
 *
 */
public class SmartParseTest {
	ParseCommand sp;
	
	@Test
	public void addBasicTask(){
		sp = new ParseCommand("Add basic task");
		assertEquals(Commands.ADD_TASK, sp.extractCommand());
//		assertEquals("basic task",sp.extractTaskName());
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
//		sp = new ParseCommand("remind 12 tomrrow");	TODO: this would fail
//		assertEquals(Commands.INVALID, sp.extractCommand());
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
		
	}
}
