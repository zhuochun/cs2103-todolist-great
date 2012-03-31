package gui.autocomplete;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import storage.TaskLists;


/**
 * test AutoComplete
 * 
 * @author Zhuochun
 *
 */
public class AutoCompleteTest {

    private AutoComplete autoComplete;

    @Before
    public void setUp() throws Exception {
        
    }

    @After
    public void tearDown() throws Exception {
        
    }

    @Test
    public void testGetCompletedStr() {
        // test complete Commands
        TaskLists lists = new TaskLists();
        // has Inbox, Trash
        lists.addList("Inhex");
        lists.addList("hello world");
        lists.addList("helworld");
        
        autoComplete = new AutoComplete(lists);
        
        // test empty case
        assertEquals(true, autoComplete.setInput("", true));
        assertEquals("add", autoComplete.getCompletedStr());
        
        // test continue tab case 0
        assertEquals(true, autoComplete.setInput("", true));
        assertEquals("del", autoComplete.getCompletedStr());
        
        // test continue tab case 1
        assertEquals(true, autoComplete.setInput("del ", true));
        assertEquals("move", autoComplete.getCompletedStr());

        // test case 00
        assertEquals(true, autoComplete.setInput("a", true));
        assertEquals("add", autoComplete.getCompletedStr());
        
        // test case 01
        assertEquals(true, autoComplete.setInput("e", true));
        assertEquals("edit", autoComplete.getCompletedStr());
        
        // test list case 00
        assertEquals(true, autoComplete.setInput("#(In", true));
        assertEquals("#(Inbox)", autoComplete.getCompletedStr());
        
        // test list case 00 tab
        assertEquals(true, autoComplete.setInput("#(In", true));
        assertEquals("#(Inhex)", autoComplete.getCompletedStr());
        
        // continue tab to go back
        assertEquals(true, autoComplete.setInput("#(Inhex)", true));
        assertEquals("#(Inbox)", autoComplete.getCompletedStr());
        
        // no completion
        assertEquals(false, autoComplete.setInput("#(Inhex)", true));
        
        // complete ( by it self
        assertEquals(true, autoComplete.setInput("#He", true));
        assertEquals("#(hello world)", autoComplete.getCompletedStr());
        
        assertEquals(true, autoComplete.setInput("#He", true));
        assertEquals("#helworld", autoComplete.getCompletedStr());
        
        // test list case 01
        assertEquals(true, autoComplete.setInput("add something #h", true));
        assertEquals("add something #(hello world)", autoComplete.getCompletedStr());
        
        // test priority 1
        assertEquals(true, autoComplete.setInput("add something !", true));
        assertEquals("add something !1", autoComplete.getCompletedStr());
        
        // test priority 2
        assertEquals(true, autoComplete.setInput("add something !12", true));
        assertEquals("add something !1", autoComplete.getCompletedStr());
        
        // test priority 3
        assertEquals(true, autoComplete.setInput("add something !2", true));
        assertEquals("add something !3", autoComplete.getCompletedStr());
        
        // test TimeUnit 1
        assertEquals(true, autoComplete.setInput("add something for 12mi", true));
        assertEquals("add something for 12minutes", autoComplete.getCompletedStr());
        
        // test TimeUnit 2
        assertEquals(true, autoComplete.setInput("add something for 12 h", true));
        assertEquals("add something for 12 hours", autoComplete.getCompletedStr());
        
        // test dictionary
        assertEquals(true, autoComplete.setInput("add sth, n", true));
        assertEquals("add sth, next", autoComplete.getCompletedStr());
        
        // tab next word
        assertEquals(true, autoComplete.setInput("add sth, next", true));
        assertEquals("add sth, november", autoComplete.getCompletedStr());
        
        // tab go back the first one
        assertEquals(true, autoComplete.setInput("add sth, n", true));
        assertEquals("add sth, next", autoComplete.getCompletedStr());
    }
}