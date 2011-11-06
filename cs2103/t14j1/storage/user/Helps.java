package cs2103.t14j1.storage.user;

public class Helps {
    
    private static int index = 0;
    
    private static final String[] tips = {
        "Welcome to TaskMeter!\n------------------------------------------------------------\nClick next to see all the tips.",
        "Getting Started: How to add a TASK?\n------------------------------------------------------------\nTo add a TASK via TaskMeter smartbar:\n\"add + space + TASK\"\n\nadd go jogging",
        "TASK Categorization: Introducing LIST\n------------------------------------------------------------\nA TASK always belongs to a LIST.\nThis allows you to categorize different types of TASKs",
        "User-Defined LIST\n------------------------------------------------------------\nSpecifying LIST using '#': \"add + TASK + # + LIST\"\n\nadd dinner with my parents #family\n\nRemarks:\n- If the LIST specified doesn't exist, it will be created.\n- Use () if there is space like #(high school friends)",
        "If no LIST is specified, the TASK entered will be stored in default \"Inbox LIST\".\nAnother default LIST is \"Trash LIST\" which stores all deleted TASKs.",
        "TASK Details Specification\n------------------------------------------------------------\nIn addition to TASK, many details can be specified.\nThis includes PLACE, DATE, TIME, DEADLINE, DURATION and PRIORITY.\n\nRemarks:\n- There's no fixed order for the details; they can come in any order.",
        "TaskMeter awares it's important to Knowing the PLACE of your appointment; An easy way to do so is provided.",
        "PLACE Specification: Knowing Where\n------------------------------------------------------------\nSpecifying PLACE using '@':\n\"add + TASK + @ + PLACE\"\n\nadd jogging @home\n\nRemarks:\n- Use () if there is space like @(my home)",
        "Knowinging which day your appointments are on is very important, hence, TaskMeter makes it simple to specify DATE.",
        "DATE Specification: Knowing When\n------------------------------------------------------------\nSpecifying DATE:\n\"add + TASK + DATE\"\n\nadd my father's birthday 20 December\n\nRemarks:\n- Day comes before Month before Year (optional)\n- When no Year is indicated, TaskMeter takes the soonest Year with the Day/Month specified as task's Year",
        "Specifying DAY IN WEEK:\n\"add + TASK + DAY IN WEEK\"\n\nadd GUI workshop on Thursday\n\nRemarks:\n- Keywords you might consider when specifying DAY IN WEEK are \"this\" and \"next\".\n- These keywords are interpreted on weekly basis",
        "To make it even more comprehensive, specifying TIME can also be done easily.",
        "TIME Specification: Knowing More Precisely When\n------------------------------------------------------------\nSpecifying TIME: \"add + TASK + TIME\"\n\nadd Skype interview with Microsoft 10pm.\nRemarks: Both AM/PM and 24:00 formats are recognized",
        "START/END TIME Specification: Knowing Exactly When\n------------------------------------------------------------\nSpecifying START TIME/END TIME using '~':\n\"add + TASK + START TIME ~ END TIME\"\n\nadd study marathon 10:00 ~ 22:00\nOR\nadd study marathon 10am ~ 10pm",
        "Nevertheless, it happens from time to time that a TASK doesn't have a specific START TIME/END TIME.\n\nFor instance, 2 hours is required for finishing an assignment at any time before next Monday.",
        "DURATION Specification: Knowing How Long\n------------------------------------------------------------\nSpecifying DURATION using \"for\":\n\"add + TASK + for + DURATION\"\n\nadd hangout with friends for 4 hours",
        "In daily life, there're many things we have to do. Sometimes we can't do them all and want to skip some. Go on with PRIORITY feature.",
        "PRIORITY Specification: Knowing What Is Important\n------------------------------------------------------------\nSpecifying PRIORITY using '!':\"add + TASK + ! + PRIORITY\"\n\nadd dating with girl Z !1\n\n- 1 for Important PRIORITY\n- 2 for Normal PRIORITY\n- 3 for Low PRIORITY\n\nRemarks:\n-When PRIORITY is not indicated, TaskMeter takes it as Normal PRIORITY by default.",
        "Beside adding a TASK, deleting, re-categorizing and editing could also be done very easily.",
        "Take note that TaskMeter indexes all TASKs it has. This is useful when deleting and editing TASKs",
        "TASK Deletion\n------------------------------------------------------------\nDeleting TASK using \"del\":\n\"del + TASK's index\"\n\ndel 3\n\nThe 3rd TASK shown is deleted.",
        "TASK Re-Categorization\n------------------------------------------------------------\nMoving TASK using \"move\":\n\"move + TASK's index + # + LIST\"\n\nmove 4 #School\n\nThe 4th TASK shown is moved to \"School LIST\".",
        "TASK Details Edition\n------------------------------------------------------------\nEditing TASK using \"edit\":\n\"edit + TASK's index\"\n\nedit 5\n\nThe details of 5th TASK shown will be displayed on a separate windows for you to edit.",
        "TASK Quick Modification\n------------------------------------------------------------\nMarking COMPLETE using \"done\":\n\"done + TASK's index\n\n done 2\n\nThe 2nd TASK shown is marked as Completed\"\n------------------------------------------------------------\nMarking PRIORITY:\n\"TASK's index + space + ! + PRIORITY\"\n\n7 !1\n\nThe 7th TASK shown is marked as Important",
        "Still keep missing appointment after appointment even after using TaskMeter?\nPerhaps, you need to use our Reminder feature.",
        "TASK Reminder: Reminding Me!\n------------------------------------------------------------\nTASK Reminding using \"remind\":\n\"remind + space + TASK's index\"\n\nremind 1",
        "TASK Highlighting\n------------------------------------------------------------\nTaskMeter adopts color-coding to make it easier for users to distinguish different type of tasks as follows:\nRED = IMPORTANT\nPURPLE = OVERDUE\nGREY = COMPLETED\nBLUE = with REMINDER",
        "There is no point storing things yet without being able to find it.\nTherefore, TaskMeter provides you with extrodinary search function.",
        "TASK Searching: Finding What You Stored\n------------------------------------------------------------\nSearching TASK using '/':\n\"/+TASK Details\"\n\n/homework #Physics\n\nRemarks:\n- You can search by any combination of TASK Details you entered.",
        "We're all busy with life. And, from time to time, we make mistakes.\nTaskMeter realizes this, hence, allowing Undo/Redo",
        "TASK Undo/Redo-ing\n------------------------------------------------------------\nUndo using \"Ctrl + Z\" or typing \"undo\" onto TaskMeter smartbar.\nRedo using \"Ctrl + Y\" or typing \"redo\" onto TaskMeter smartbar.",
        "Now you're a TaskMeter expert! Please read TaskMeter User Guide for more details."
    };
    
    /**
     * get a random tip from all the tips
     * 
     * @return a tip
     */
    public static String getRandomTip() {
        return tips[(int) Math.random() % tips.length];
    }
    
    /**
     * get current tip
     * 
     * @return the tip under current index
     */
    public static String getTip() {
        return tips[index];
    }
    
    /**
     * get a tip from in sequence from the tips
     * 
     * @return a tip
     */
    public static String getNextTip() {
        if (hasNext()) {
            return tips[++index];
        } else {
            return null;
        }
    }
    
    /**
     * get a tip from in backward sequence from the tips
     * 
     * @return a tip
     */
    public static String getPrevTip() {
        if (hasPrev()) {
            return tips[--index];
        } else {
            return null;
        }
    }
    
    /**
     * get the help formed based on all the tips
     * 
     * @return help document
     */
    public static String getHelp() {
        StringBuffer paragraph = new StringBuffer();
        
        for (String tip : tips) {
            paragraph.append(tip);
            paragraph.append("\n");
        }
        
        return paragraph.toString();
    }
    
    /**
     * check the tip index is in range
     * 
     * @return true if yes
     */
    public static boolean hasNext() {
        return index < tips.length - 1;
    }
    
    /**
     * check the tip index is in range
     * 
     * @return true if yes
     */
    public static boolean hasPrev() {
        return index > 0;
    }
    
}