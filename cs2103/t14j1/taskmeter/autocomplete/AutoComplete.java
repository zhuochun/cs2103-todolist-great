package cs2103.t14j1.taskmeter.autocomplete;

import cs2103.t14j1.storage.TaskLists;

/**
 * Auto Complete in SmartBar, support complete commands, list names
 * 
 * @author Zhuochun
 * 
 */
public class AutoComplete {
    
    private final TaskLists lists;
    
    private String lastInput;
    private String lastInputLowerCase;
    private String completedInput;
    
    private int startIdx;
    private int endIdx;
    
    private int commandIdx;
    private int listIdx;
    
    private final String[] Commands = {
            "add",
            "del",
            "move",
            "edit",
            "done"
    };
    
    public AutoComplete(TaskLists l) {
        lists = l;
    }
    
    public void reset() {
        lastInput          = null;
        lastInputLowerCase = null;
        completedInput     = null;
        startIdx           = 0;
        endIdx             = 0;
        commandIdx         = 0;
        listIdx            = 0;
    }
    
    private void initial(String str) {
        lastInput          = str;
        lastInputLowerCase = str.toLowerCase();
        completedInput     = null;

        commandIdx = listIdx = 0;
        startIdx   = endIdx  = lastInput.length();
    }
    
    public boolean setInput(String str) {
        str = str.trim();
        
        //System.out.println("INPUT : '" + str + "'");
        
        try {
            if (str.equals(completedInput) || str.equals(lastInput)) {
                completedInput = null;
                startIdx = endIdx = lastInput.length();
                
                if (commandIdx >= Commands.length) {
                    commandIdx = 0;
                }
                if (listIdx+1 >= lists.getSize()) {
                    listIdx = 0;
                }
            } else {
                initial(str);
            }
        } catch (NullPointerException e) {
            initial(str);
        }
        
        /*
        System.out.println("Last Input = " + lastInput);
        System.out.println("Last Input LC = " + lastInputLowerCase);
        System.out.println("Last Completed = " + completedInput);
        System.out.println("Command Idx = " + commandIdx);
        */
        
        boolean result = false;
        
        if (result == false) {
            result = completeCommand();
        }
        
        if (result == false) {
            result = completeList();
        }
        
        if (result == true) {
            endIdx = completedInput.length();
        }
        
        return result;
    }
    
    public String getCompletedStr() {
        return completedInput;
    }
    
    public int getStartIdx() {
        return startIdx;
    }
    
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
    
}
