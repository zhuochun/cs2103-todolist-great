package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * A dialog window to take in the new list name
 * 
 * @author Zhuochun
 * 
 */
public class AddListDialog extends Dialog {

    protected String  result;       // new list name
    protected Shell   shell;
    private Text      text;         // list text box
    private boolean   button;       // whether to take new list name

    /**
     * Create the dialog.
     * 
     * @param parent
     */
    public AddListDialog(Shell parent) {
        super(parent, SWT.NONE);
        setText(TaskMeter.getResourceString("addListDialog.title"));
        
        createContents();
        
        button = false;
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public String open() {
        center();
        
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }
    
    public void setListName(String name) {
        text.setText(name);
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                e.doit = closeAddTask();
            }
        });
        shell.setSize(245, 120);
        shell.setText(getText());

        createText();
        createButtons();
    }

    /**
	 * 
	 */
    private void createText() {
        Label lblEnterNewList = new Label(shell, SWT.NONE);
        lblEnterNewList.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        lblEnterNewList.setBounds(5, 5, 230, 20);
        lblEnterNewList.setText(TaskMeter.getResourceString("addListDialog.msg"));

        text = new Text(shell, SWT.BORDER);
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                result = text.getText().trim();
            }
        });
        text.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
        text.setBounds(5, 30, 230, 25);
    }

    /**
	 * 
	 */
    private void createButtons() {
        Button btnAdd = new Button(shell, SWT.NONE);
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                button = true;
                shell.close();
            }
        });
        btnAdd.setBounds(5, 60, 75, 28);
        btnAdd.setText(TaskMeter.getResourceString("button.add"));

        Button btnCancel = new Button(shell, SWT.NONE);
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                button = true;
                result = null;
                shell.close();
            }
        });
        btnCancel.setBounds(85, 60, 75, 28);
        btnCancel.setText(TaskMeter.getResourceString("button.cancel"));

        shell.setDefaultButton(btnAdd);
    }

    private boolean closeAddTask() {
        if (button == false && text.getText().trim().length() > 1) {
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
            box.setText(this.getText());
            box.setMessage(TaskMeter.getResourceString("addListDialog.close.msg"));

            int choice = box.open();
            if (choice == SWT.CANCEL) {
                return false;
            } else if (choice == SWT.YES) {
                result = text.getText().trim();
            } else {
                result = null;
            }
        }

        return true;
    }

    private void center() {
        Rectangle parent = getParent().getBounds();
        Rectangle rect = shell.getBounds();
        int x = parent.x + (parent.width - rect.width) / 2;
        int y = parent.y + (parent.height - rect.height) / 2;
        shell.setLocation(x, y);
    }
}
