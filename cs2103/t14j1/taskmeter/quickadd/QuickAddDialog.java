package cs2103.t14j1.taskmeter.quickadd;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cs2103.t14j1.logic.ControlGUI;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.user.User;
import cs2103.t14j1.taskmeter.TaskMeter;
import cs2103.t14j1.taskmeter.autocomplete.AutoComplete;

public class QuickAddDialog extends Dialog {
    private Shell   shell;
    private Display display;
    
    private Text   quickAddBar;
    private Button btnAdd;
    private Text   txtResult;
    
    private boolean active;
    private boolean isModified;
    
    private String       result;
    private ControlGUI   logic;
    private AutoComplete autoComplete;
    
    private static final String MSG_EXECUTE = ">";
    private static final String MSG_SUCCESS = "Added";
    private static final String MSG_FAIL    = "Failed";
    private static final String MSG_INVALID = "Invalid";

    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public QuickAddDialog(Shell parent, ControlGUI control, AutoComplete autocomp) {
        super(parent, SWT.NONE);
        
        active       = false;
        isModified   = false;
        autoComplete = autocomp;
        logic        = control;
    }

    /**
     * Open the dialog.
     */
    public String open() {
        createContents();
        center();
        
        shell.open();
        shell.layout();
        
        active     = true;
        isModified = false;
        
        display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                result = quickAddBar.getText();
                display.sleep();
            }
        }
        
        active = false;
        
        return result;
    }
    
    private void setSize(boolean mini) {
        if (mini) {
            shell.setSize(330, 68);
        } else {
            shell.setSize(330, 200);
        }
    }
    
    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        setSize(true);
        shell.setText(TaskMeter.getResourceString("QuickAddDialog.title")); 
        
        quickAddBar = new Text(shell, SWT.BORDER);
        quickAddBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.character == ' ' && quickAddBar.getSelectionCount() != 0) {
                    quickAddBar.setSelection(quickAddBar.getText().length());
                    quickAddBar.setFocus();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                // Only perform auto completion when user input characters
                if (User.performAutoComplete && (e.character > 'A' && e.character < 'z')) {
                    String txt = quickAddBar.getText();
                    if (autoComplete.setInput(txt)) {
                        quickAddBar.setText(autoComplete.getCompletedStr());
                        quickAddBar.setSelection(autoComplete.getStartIdx(), autoComplete.getEndIdx());
                    }
                    quickAddBar.setFocus();
                }
            }
        });
        quickAddBar.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.CR) { // Enter to execute Command
                    executeCommand(quickAddBar.getText());
                    focusQuickAdd();
                } else if (e.keyCode == SWT.TAB) { // Tab to complete words
                    e.doit = false;
                    String txt = quickAddBar.getText();
                    if (autoComplete.setInput(txt)) {
                        quickAddBar.setText(autoComplete.getCompletedStr());
                        quickAddBar.setSelection(autoComplete.getStartIdx(), autoComplete.getEndIdx());
                    }
                    quickAddBar.setFocus();
                } else if (e.keyCode == SWT.ESC) { // ESC to confirm complete selection
                    e.doit = false;
                    if (quickAddBar.getSelectionCount() != 0) {
                        quickAddBar.setSelection(quickAddBar.getText().length());
                        quickAddBar.setFocus();
                    }
                }
            }
        });
        quickAddBar.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (quickAddBar.getText().trim().isEmpty()) {
                    setStatus(MSG_EXECUTE);
                    setSize(true);
                }
            }
        });
        quickAddBar.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
        quickAddBar.setBounds(5, 5, 250, 30);
        
        btnAdd = new Button(shell, SWT.NONE);
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (btnAdd.getText().equals(MSG_EXECUTE)) {
                    executeCommand(quickAddBar.getText());
                }
            }
        });
        btnAdd.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        btnAdd.setBounds(260, 5, 60, 30);
        
        txtResult = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        txtResult.setEnabled(false);
        txtResult.setBackground(SWTResourceManager.getColor(255, 255, 240));
        txtResult.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        txtResult.setBounds(5, 40, 315, 125);
        
        setStatus(MSG_EXECUTE);
    }
    
    /**
     * execute input command from user
     * 
     * @param input         user's input string
     */
    private void executeCommand(String input) {
        if (input.toLowerCase().matches("^add\\s.*$")) {
            logic.setUserInput(input.trim());
        } else { // in quick add view, can ignore add command
            logic.setUserInput("add " + input.trim());
        }
        
        try {
            switch (logic.getCommand()) {
                case ADD_TASK:
                    Task newTask = logic.quickAddTask();
                    if (newTask != null) {
                        isModified = true;
                        setStatus(MSG_SUCCESS);
                        setResult(newTask.toString());
                        setSize(false);
                    } else {
                        setStatus(MSG_FAIL);
                        setResult("");
                    }
                    break;
                default:
                    setStatus(MSG_INVALID);
                    break;
            }
        } catch (Exception e) {
            setStatus(MSG_INVALID);
            setResult(e.getMessage());
            setSize(false);
        }
    }
    
    public void close() {
        result = quickAddBar.getText();
        shell.dispose();
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean isModified() {
        return isModified;
    }
    
    public void focusQuickAdd() {
        quickAddBar.setSelection(0, quickAddBar.getText().length());
        quickAddBar.setFocus();
    }
    
    private void setStatus(String str) {
        btnAdd.setText(str);
    }
    
    private void setResult(String str) {
        txtResult.setText(str);
    }
    
    private void center() {
        Rectangle parent = getParent().getBounds();
        Rectangle rect = shell.getBounds();
        int x = parent.x + (parent.width - rect.width) / 2;
        int y = parent.y + (parent.height - rect.height) / 2;
        shell.setLocation(x, y);
    }
}