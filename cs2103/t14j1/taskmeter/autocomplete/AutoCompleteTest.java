package cs2103.t14j1.taskmeter.autocomplete;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs2103.t14j1.storage.TaskLists;

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
        
        lists.addList("Inhex");
        lists.addList("hello world");
        lists.addList("helworld");
        
        autoComplete = new AutoComplete(lists);
        
        // test empty case
        assertEquals(true, autoComplete.setInput("", true));
        assertEquals("add", autoComplete.getCompletedStr());
        
        System.out.println("add ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        // test continue tab case 0
        assertEquals(true, autoComplete.setInput("", true));
        assertEquals("del", autoComplete.getCompletedStr());
        
        System.out.println("del ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        // test continue tab case 1
        assertEquals(true, autoComplete.setInput("del ", true));
        assertEquals("move", autoComplete.getCompletedStr());
        
        System.out.println("move ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");

        // test case 00
        assertEquals(true, autoComplete.setInput("a", true));
        assertEquals("add", autoComplete.getCompletedStr());
        
        System.out.println("add ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");

        // test case 01
        assertEquals(true, autoComplete.setInput("e", true));
        assertEquals("edit", autoComplete.getCompletedStr());
        
        System.out.println("edit ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        // test list case 00
        assertEquals(true, autoComplete.setInput("#(In", true));
        
        System.out.println("# ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        assertEquals(true, autoComplete.setInput("#(In", true));
        
        System.out.println("# ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        assertEquals(true, autoComplete.setInput("#(Inhex", true));
        
        System.out.println("# ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        assertEquals(true, autoComplete.setInput("#(In", true));
        
        System.out.println("# ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        // test list case 01
        assertEquals(true, autoComplete.setInput("add something #h", true));
        
        System.out.println("# ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        // test list case 01
        assertEquals(true, autoComplete.setInput("add something #he", true));
        
        System.out.println("# ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        // test priority
        assertEquals(true, autoComplete.setInput("add something !", true));
        
        System.out.println("! ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        assertEquals(true, autoComplete.setInput("add something !12", true));
        
        System.out.println("! ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        // test TimeUnit
        assertEquals(true, autoComplete.setInput("add something for 12mi", true));
        
        System.out.println("! ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
        
        assertEquals(true, autoComplete.setInput("add something for 12 h", true));
        
        System.out.println("! ==> " + autoComplete.getCompletedStr());
        System.out.println(autoComplete.getStartIdx());
        System.out.println(autoComplete.getEndIdx());
        
        System.out.println("==========");
    }

}