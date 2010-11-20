package plunit.sqldeveloper.dockable;

import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import plunit.Testable;
import plunit.sqldeveloper.SqlDeveloperNotifier;

public class TestRenderer extends DefaultTreeCellRenderer {
   private static final long serialVersionUID = 1L;

   public static final ImageIcon passIcon = createImageIcon("pass.jpg");
   public static final ImageIcon failIcon = createImageIcon("fail.gif"); 
   public static final ImageIcon notRanIcon = createImageIcon("notRan.gif");
   public static final ImageIcon runningIcon = createImageIcon("running.jpg");
   
   public TestRenderer() {}

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
         boolean expanded, boolean leaf, int row, boolean hasFocus) {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    	 try {
	    	 Testable test = getPlunitTest(value);
	    	 switch(test.getState()) {
	    	 	case PASSING:
	    	 		setIcon(passIcon);
	    	 		break;
	    	 	case FAILING:
	    	 		setIcon(failIcon);
	    	 		break;
	    	 	case RUNNING:
	    	 		setIcon(runningIcon);
	    	 		break;
	    	 	case NOT_RAN:
	    	 		setIcon(notRanIcon);
	    	 		break;
	    	 }
	    	 setText((null != test.getDescription()) ? test.getDescription() : test.getName());
    	 } catch(RuntimeException e) {}
      return this;
   }

   private Testable getPlunitTest(Object value) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      Testable nodeInfo = (Testable) node.getUserObject();
      return nodeInfo;
   }
   
   public static ImageIcon createImageIcon(String path) {
      URL imgURL = PlunitTestPanel.class.getResource(path);
      if (imgURL != null) {
         return new ImageIcon(imgURL);
      } else {
         new SqlDeveloperNotifier().show("Couldn't find file: " + path);
         return null;
      }
   }
} // end TestRenderer
