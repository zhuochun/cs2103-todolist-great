package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import storage.user.CheatSheet;


/**
 * display cheat sheet and shortcuts defined in storage.user.cheatsheet class
 * 
 * @author Zhuochun
 *
 */
public class CheatSheetDialog extends Dialog {

    protected Shell shell;
    private   Image image;
    
    private static final String IMAGE_PATH = "/cs2103/t14j1/taskmeter/taskMeter.ico";

    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public CheatSheetDialog(Shell parent) {
        super(parent, SWT.NONE);
        image = SWTResourceManager.getImage(CheatSheetDialog.class, IMAGE_PATH);
    }

    /**
     * Open the dialog.
     * @return the result
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
        shell = new Shell(getParent(), SWT.DIALOG_TRIM);
        shell.setSize(450, 404);
        shell.setText(TaskMeter.getResourceString("CheatSheetDialog.title"));
        
        Tree tree = new Tree(shell, SWT.BORDER | SWT.FULL_SELECTION);
        tree.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.NORMAL));
        tree.setSortDirection(SWT.DOWN);
        tree.setBounds(10, 10, 424, 356);
        
        String[] categories = CheatSheet.getCategories();
        
        for (String category : categories) {
            TreeItem cate = new TreeItem(tree, SWT.NONE);
            cate.setImage(image);
            cate.setText(category);
            cate.setExpanded(true);
            
            String[] items = CheatSheet.getItemInCategory(category);
            
            for (String item : items) {
                TreeItem cateItem = new TreeItem(cate, SWT.NONE);
                cateItem.setText(item);
            }
        }
    }

    private void center() {
        Rectangle parent = getParent().getBounds();
        Rectangle rect = shell.getBounds();
        int x = parent.x + (parent.width - rect.width) / 2;
        int y = parent.y + (parent.height - rect.height) / 2;
        shell.setLocation(x, y);
    }
}