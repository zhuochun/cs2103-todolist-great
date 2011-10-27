package cs2103.t14j1.taskmeter;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cs2103.t14j1.storage.user.Helps;

public class TipsDialog extends Dialog {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("taskmeter_res");
    protected Shell shell;
    
    private Text txtText;
    private Button btnPrevious;
    private Button btnNext;

    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public TipsDialog(Shell parent) {
        super(parent, SWT.NONE);
        setText(getResourceString("TipsDialog.title"));
    }

    /**
     * Open the dialog.
     */
    public void open() {
        createContents();
        center();
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.MIN | SWT.RESIZE);
        shell.setSize(450, 300);
        shell.setText(getText());
        shell.setLayout(new GridLayout(3, false));
        
        txtText = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        txtText.setBackground(SWTResourceManager.getColor(255, 255, 255));
        txtText.setEditable(false);
        txtText.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
        txtText.setText(Helps.getTip());
        txtText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 8));
        
        Label label = new Label(shell, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        label.setText("");
        
        btnPrevious = new Button(shell, SWT.NONE);
        btnPrevious.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                displayPrevious();
            }
        });
        btnPrevious.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        btnPrevious.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        btnPrevious.setText("Previous");
        
        btnNext = new Button(shell, SWT.NONE);
        btnNext.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                displayNext();
            }
        });
        btnNext.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
        btnNext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        btnNext.setText("Next");
        
        refreshButtons();
    }
    
    private void displayNext() {
        txtText.setText(Helps.getNextTip());
        refreshButtons();
    }
    
    private void displayPrevious() {
        txtText.setText(Helps.getPrevTip());
        refreshButtons();
    }
    
    private void refreshButtons() {
        if (Helps.hasNext()) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
        
        if (Helps.hasPrev()) {
            btnPrevious.setEnabled(true);
        } else {
            btnPrevious.setEnabled(false);
        }
    }

    private void center() {
        Rectangle parent = getParent().getBounds();
        Rectangle rect = shell.getBounds();
        int x = parent.x + (parent.width - rect.width) / 2;
        int y = parent.y + (parent.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

    /**
     * Returns a string from the resource bundle. We don't want to crash because
     * of a missing String. Returns the key if not found.
     */
    public static String getResourceString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        } catch (NullPointerException e) {
            return "!" + key + "!";
        }
    }
}