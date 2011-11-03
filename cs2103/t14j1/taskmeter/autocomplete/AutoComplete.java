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
    private String[] listNames;         // a copy of the list names
    
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
            "add", "del", "move", "edit", "done", "remind", "rename"
    };
    
    private final String[] Dictionary = {
            "this", "next", "today", "tomorrow", "done",
            "before", "after", "start", "end", "deadline",
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
        lists     = ls;
        listNames = lists.getListNames();
    }
    
    /**
     * reset all the AutoComplete Indexes to default
     */
    public void reset() {
        listNames          = lists.getListNames();
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
        commandIdx         = 0;
        dictionaryIdx      = 0;
        listIdx            = 0;
        timeUnitIdx        = 0;
        priorityIdx        = 0;
        startIdx           = lastInput.length();
        endIdx             = startIdx;
    }
    
    /**
     * set the input for completion
     * 
     * @param str           the user input
     * @return true         if a valid completion is generated
     */
    public boolean setInput(String str, boolean selected) {
        if (str == null) { // in case str is null
            str = "";
        } else {
            str = str.trim();
        }
        
        try {
            if (str.equals(lastInput) || (str.equals(completedInput) && selected)) {
                startIdx = endIdx = lastInput.length();
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
     * get the last user input String
     * 
     * @return String
     */
    public String getInputStr() {
        return lastInput;
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
    
    private String formCompleteWord(String complete, String input) {
        StringBuilder result = new StringBuilder();
        
        result.append(input);
        result.append(complete.substring(input.length()));
        
        return result.toString();
    }
    
    private boolean completeCommand() {
        if (commandIdx >= Commands.length) {
            commandIdx = 0;
        }
        
        if (lastInputLowerCase.matches("^([a-z])*$")) {
            int oldIdx = commandIdx;
            
            do {
                if (Commands[commandIdx].startsWith(lastInputLowerCase)) {
                    completedInput = formCompleteWord(Commands[commandIdx], lastInput);
                    
                    commandIdx++;
                    
                    return true;
                }
                
                commandIdx++;
                
                if (commandIdx >= Commands.length) {
                    commandIdx = 0;
                }
            } while (commandIdx != oldIdx);
        }
        
        return false;
    }
    
    private boolean completeList() {
        if (listIdx >= lists.getSize()) {
            listIdx = 0;
        }
        
        if (lastInputLowerCase.matches("^.*#[^\\)]*$")) {
            int hashIdx = lastInput.lastIndexOf("#");
            
            boolean hasBracket = false;
            if (hashIdx != lastInput.length()-1) {
                hasBracket = lastInput.charAt(hashIdx+1) == '(';
            }
            
            if (hasBracket) {
                hashIdx++;
            }
            
            String keyword = lastInputLowerCase.substring(hashIdx+1);
            
            int oldIdx = listIdx;
            
            do {
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
                
                listIdx++;
                
                if (listIdx >= lists.getSize()) {
                    listIdx = 0;
                }
            } while (listIdx != oldIdx);
        }
        
        return false;
    }
    
    private boolean completePriority() {
        if (priorityIdx >= PriorityNums.length) {
            priorityIdx = 0;
        }
        
        if (lastInputLowerCase.matches("^.*!(\\d)*$")) {
            int expIdx = lastInput.lastIndexOf("!");
            
            startIdx = expIdx+1; // update startIdx
            
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
            result.append(PriorityNums[priorityIdx]);
            
            completedInput = result.toString();
            
            priorityIdx++;
            
            return true;
        }
        
        return false;
    }

    private boolean completeTimeUnit() {
        if (timeUnitIdx >= TimeUnits.length) {
            timeUnitIdx = 0;
        }
                
        if (lastInputLowerCase.matches("^.*\\sfor\\s(\\d+\\s?[a-z]+\\s?)*\\d+$")) {
            startIdx++;
            
            StringBuilder result = new StringBuilder();

            result.append(lastInput);
            result.append(" ");
            result.append(TimeUnits[timeUnitIdx]);

            completedInput = result.toString();

            timeUnitIdx++;

            return true;
        } else if (lastInputLowerCase.matches("^.*\\sfor\\s(\\d+\\s?[a-z]+\\s?)*\\d+\\s?[a-z]+$")) {
            String[] tokens = lastInput.split("\\d+\\s?");
            
            String keyword = tokens[tokens.length-1];
            
            int oldIdx = timeUnitIdx;
            
            do {
                if (TimeUnits[timeUnitIdx].startsWith(keyword.toLowerCase())) {
                    StringBuilder result = new StringBuilder();
                    
                    result.append(lastInput.substring(0, lastInput.length() - keyword.length()));
                    result.append(formCompleteWord(TimeUnits[timeUnitIdx], keyword));
                    
                    completedInput = result.toString();
                    
                    timeUnitIdx++;
                    return true;
                }
                
                timeUnitIdx++;
                
                if (timeUnitIdx >= TimeUnits.length) {
                    timeUnitIdx = 0;
                }
            } while (timeUnitIdx != oldIdx);
        }
        
        return false;
    }
    
    private boolean completeDictionary() {
        if (dictionaryIdx >= Dictionary.length) {
            dictionaryIdx = 0;
        }
        
        if (lastInputLowerCase.matches("^.*\\s[a-z]+$")) {
            String[] tokens = lastInput.split("\\s");
            
            String keyword = tokens[tokens.length-1];
            
            int oldIdx = dictionaryIdx;
            
            do {
                if (Dictionary[dictionaryIdx].toLowerCase().startsWith(keyword.toLowerCase())) {
                    StringBuilder result = new StringBuilder();
                    
                    result.append(lastInput.substring(0, lastInput.length() - keyword.length()));
                    result.append(formCompleteWord(Dictionary[dictionaryIdx], keyword));
                    
                    completedInput = result.toString();
                    
                    dictionaryIdx++;
                    return true;
                }
                
                dictionaryIdx++;
                
                if (dictionaryIdx >= Dictionary.length) {
                    dictionaryIdx = 0;
                }
            } while (dictionaryIdx != oldIdx);
        }
        
        return false;
    }
    
}