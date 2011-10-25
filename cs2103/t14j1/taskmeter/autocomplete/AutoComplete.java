package cs2103.t14j1.taskmeter.autocomplete;

import cs2103.t14j1.storage.TaskLists;

/**
 * Auto Complete in SmartBar, support complete commands, list names, time units, special words
 * 
 * @author Zhuochun
 * 
 */
public class AutoComplete {
    
    private final TaskLists lists;      // a copy of the TaskLists
    
    private String lastInput;           // stores the last input from user
    private String lastInputLowerCase;  // stores the last input in lower case
    private String completedInput;      // stores the last input with completed terms
    
    private int startIdx;               // stores the last index of initial string user passed in
    private int endIdx;                 // stores the new last index of completed input string
    
    private int commandIdx;
    private int dictionaryIdx;
    private int timeUnitIdx;
    private int priorityIdx;
    private int listIdx;
    
    private final String[] Commands = {
            "add", "del", "move", "edit", "done", "rename"
    };
    
    private final String[] Dictionary = {
            "this", "next", "today", "tomorrow", "done", "before", "after",
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday",
            "January", "Feburary", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"
    };
    
    private final String[] TimeUnits = {
             "seconds", "minutes", "hours", "days", "weeks", "months"
    };
    
    private final int[] PriorityNums = { 1, 2, 3 };
    
    /**
     * constructor, need to pass a copy of the TaskLists
     * 
     * @param ls        pass a copy of the taskLists
     */
    public AutoComplete(TaskLists ls) {
        lists = ls;
    }
    
    /**
     * reset all the AutoComplete Indexes to default
     */
    public void reset() {
        lastInput          = null;
        lastInputLowerCase = null;
        completedInput     = null;
        startIdx           = 0;
        endIdx             = 0;
        commandIdx         = 0;
        dictionaryIdx      = 0;
        listIdx            = 0;
        timeUnitIdx        = 0;
        priorityIdx        = 0;
    }
    
    /**
     * initial all the indexes according to passed string
     * 
     * @param str       user input string
     */
    private void initial(String str) {
        lastInput          = str;
        lastInputLowerCase = str.toLowerCase();
        completedInput     = null;

        commandIdx = listIdx = priorityIdx = 0;
        timeUnitIdx = dictionaryIdx = 0;
        startIdx   = endIdx  = lastInput.length();
    }
    
    /**
     * set the input for completion
     * 
     * @param str           the user input
     * @return true         if a valid completion is generated
     */
    public boolean setInput(String str) {
        if (str == null) { // in case str is null
            str = "";
        } else {
            str = str.trim();
        }
        
        try {
            if (str.equals(completedInput) || str.equals(lastInput)) {
                completedInput = null;
                startIdx = endIdx = lastInput.length();
                
                // clear indexes if they are out of range already
                if (commandIdx >= Commands.length) {
                    commandIdx = 0;
                }
                if (listIdx+1 >= lists.getSize()) {
                    listIdx = 0;
                }
                if (priorityIdx >= PriorityNums.length) {
                    priorityIdx = 0;
                }
                if (timeUnitIdx >= TimeUnits.length) {
                    timeUnitIdx = 0;
                }
                if (dictionaryIdx >= Dictionary.length) {
                    dictionaryIdx = 0;
                }
            } else {
                initial(str);
            }
        } catch (NullPointerException e) {
            initial(str);
        }
        
        boolean result = false;
        
        if (result == false) { // Complete Commands
            result = completeCommand();
        }
        
        if (result == false) { // Complete List Names
            result = completeList();
        }
        
        if (result == false) { // Complete Priority
            result = completePriority();
        }
        
        if (result == false) { // Complete Time Units
            result = completeTimeUnit();
        }
        
        if (result == false) { // Complete Dictionary
            result = completeDictionary();
        }
        
        if (result == true && completedInput != null) { // update endIdx
            endIdx = completedInput.length();
        }
        
        return result;
    }
    
    /**
     * get the completed new input String
     * 
     * @return String
     */
    public String getCompletedStr() {
        return completedInput;
    }
    
    /**
     * get the last index of the input string before completion
     * 
     * @return int
     */
    public int getStartIdx() {
        return startIdx;
    }
    
    /**
     * get the last index of the completed input string
     * 
     * @return int
     */
    public int getEndIdx() {
        return endIdx;
    }
    
    private boolean completeCommand() {
        if (lastInputLowerCase.matches("^([a-z]){0,3}$")) {
            for (; commandIdx < Commands.length; commandIdx++) {
                if (Commands[commandIdx].startsWith(lastInputLowerCase)) {
                    completedInput = Commands[commandIdx++];
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean completeList() {
        if (lastInputLowerCase.matches("^.*#[^\\)]*$")) {
            int     hashIdx    = lastInput.lastIndexOf("#");
            
            boolean hasBracket = false;
            if (hashIdx != lastInput.length()-1) {
                hasBracket = lastInput.charAt(hashIdx+1) == '(';
            }
            
            if (hasBracket) {
                hashIdx++;
            }
            
            String   keyword   = lastInputLowerCase.substring(hashIdx+1);
            String[] listNames = lists.getListNames();
            
            for (; listIdx < listNames.length; listIdx++) {
                //System.out.println(listNames[listIdx]);
                
                if (listNames[listIdx].toLowerCase().startsWith(keyword)) {
                    StringBuilder result = new StringBuilder();
                    
                    // append initial string
                    result.append(lastInput.substring(0, hashIdx+1));
                    // add starting bracket if necessary
                    if (listNames[listIdx].contains(" ") && !hasBracket) {
                        result.append("(");
                        hasBracket = true;
                        startIdx++;
                    }
                    // append list name
                    result.append(listNames[listIdx]);
                    // close the bracket if necessary
                    if (hasBracket) {
                        result.append(")");
                    }
                    
                    completedInput = result.toString();
                    
                    listIdx++;
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean completePriority() {
        if (lastInputLowerCase.matches("^.*!(\\d)*$")) {
            int expIdx = lastInput.lastIndexOf("!");
            
            startIdx = expIdx; // update startIdx
            
            // check if there is priority set already
            if (expIdx != lastInput.length()-1) {
                int pr = Integer.parseInt(lastInput.substring(expIdx+1));
                if (pr <= PriorityNums[0] || pr >= PriorityNums[PriorityNums.length-1]) {
                    priorityIdx = 0;
                } else {
                    priorityIdx = pr;
                }
                
                lastInput = lastInput.substring(0, expIdx+1);
            }
            
            StringBuilder result = new StringBuilder();
            
            result.append(lastInput.substring(0, expIdx+1));
            result.append(PriorityNums[priorityIdx++]);
            
            completedInput = result.toString();
            
            return true;
        }
        
        return false;
    }

    private boolean completeTimeUnit() {
        if (lastInputLowerCase.matches("^.*\\sfor\\s(\\d+\\s?[a-z]+\\s?)*\\d+$")) {
            startIdx++;
            
            StringBuilder result = new StringBuilder();

            result.append(lastInput);
            result.append(" ");
            result.append(TimeUnits[timeUnitIdx]);

            completedInput = result.toString();

            timeUnitIdx++;

            return true;
        } else if (lastInputLowerCase.matches("^.*\\sfor\\s(\\d+\\s?[a-z]+\\s?)*\\d+\\s?[a-z]{1,3}$")) {
            String[] tokens = lastInputLowerCase.split("\\d+\\s?");
            
            String keyword = tokens[tokens.length-1];
            
            for (; timeUnitIdx < TimeUnits.length; timeUnitIdx++) {
                if (TimeUnits[timeUnitIdx].startsWith(keyword)) {
                    StringBuilder result = new StringBuilder();
                    
                    result.append(lastInput.substring(0, lastInput.length() - keyword.length()));
                    result.append(TimeUnits[timeUnitIdx++]);
                    
                    completedInput = result.toString();
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean completeDictionary() {
        if (lastInputLowerCase.matches("^.*\\s[a-z]+$")) {
            String[] tokens = lastInputLowerCase.split("\\s");
            
            String keyword = tokens[tokens.length-1];
            
            for (; dictionaryIdx < Dictionary.length; dictionaryIdx++) {
                if (Dictionary[dictionaryIdx].toLowerCase().startsWith(keyword)) {
                    StringBuilder result = new StringBuilder();
                    
                    result.append(lastInput.substring(0, lastInput.length() - keyword.length()));
                    result.append(Dictionary[dictionaryIdx++]);
                    
                    completedInput = result.toString();
                    return true;
                }
            }
        }
        
        return false;
    }
    
}