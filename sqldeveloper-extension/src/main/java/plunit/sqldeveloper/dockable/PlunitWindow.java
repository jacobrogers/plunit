package plunit.sqldeveloper.dockable;

import java.awt.Component;

import javax.swing.JComponent;

import oracle.ide.Ide;
import oracle.ide.docking.DockStation;
import oracle.ide.docking.DockableWindow;
import oracle.ide.layout.ViewId;
import plunit.PlunitTestSuite;
import plunit.observers.TestSuiteListener;

public class PlunitWindow extends DockableWindow implements TestSuiteListener {
   public static final ViewId VIEW_ID = new ViewId("MyDockable", "MyDockable");
   private JComponent userInterface;
   private PlunitTestSuite plunitTestSuite;

   public PlunitWindow() {
      super(Ide.getMainWindow(), VIEW_ID.getId());
   }

   @Override
   public Component getGUI() {
      return userInterface;
   }

   public String getTabName() {
      return "PL/Unit Test Results";
   }

   public String getTitleName() {
      return plunitTestSuite.getName() + " Results";
   }

   public void show() {
      DockStation.getDockStation().setDockableVisible(this, true);
   }

   public void hide() {
      DockStation.getDockStation().setDockableVisible(this, false);
   }

   public void notifyStart(int numberOfTests) {
      if (userInterface == null) {
         userInterface = new PlunitTestPanel(plunitTestSuite);
      }
      ((TestSuiteListener) userInterface).notifyStart(numberOfTests);
      show();
   }

   public void notify(PlunitTestSuite testSuite, int testNumber) {
      ((TestSuiteListener) userInterface).notify(testSuite, testNumber);
   }
   public void notifyFinish() {
      ((TestSuiteListener) userInterface).notifyFinish();
   }

   public void setPlunitTestSuite(PlunitTestSuite plunitTest) {
      this.plunitTestSuite = plunitTest;
   }

}
