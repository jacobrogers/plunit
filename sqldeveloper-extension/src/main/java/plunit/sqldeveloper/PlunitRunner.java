package plunit.sqldeveloper;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import oracle.dbtools.raptor.dialogs.actions.AbstractMenuAction;
import oracle.dbtools.raptor.dialogs.actions.XMLBasedObjectAction;
import oracle.ide.Addin;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
import oracle.ide.dialogs.ProgressBar;
import plunit.PlunitTestSuite;
import plunit.observers.PopUp;
import plunit.sqldeveloper.dockable.PlunitWindow;

public class PlunitRunner extends AbstractMenuAction implements Addin, Controller {
	private PlunitWindow dockable = new PlunitWindow();
	private PopUp popup = new SqlDeveloperNotifier(); 

	public PlunitRunner() {}

	public void initialize() {
		popup.show("initializing...");
		dockable.hide();
		try {
			URL url = new URL("file:action.xml");
			XMLBasedObjectAction.registerContextMenus(url);
		} catch (RuntimeException e) {
			popup.show(e);
		} catch (Exception e) {
			popup.show(e);
		}
	}

	@Override
	public void launch() {
		try {
			Thread runner = new Thread() {
			   public void run() {
			      PlunitTestSuite testSuite = loadTestSuite(getPackageName(), getDBObject().getDatabase().getConnection());
			      testSuite.setNotifier(popup);
			      testSuite.addListener(dockable);
			      dockable.setPlunitTestSuite(testSuite);
			      try {
			    	  popup.show("running test: " + testSuite.getName() + " " + testSuite.getTests().size() + " tests");
			    	  testSuite.run();
			    	  popup.show("finished test: " + testSuite.getName());
			      } catch(Exception e) {
			    	  popup.show(e);
			      }
			   }
			};
			runner.start();
		} catch (RuntimeException e) {
			popup.show(e);
		} catch (Exception e) {
			popup.show(e);
		}
	}

	public static PlunitTestSuite loadTestSuite(String suiteName, Connection connection) {
		PlunitTestLoader testLoader = new PlunitTestLoader(suiteName, connection);
		ProgressBar progressBar = new ProgressBar(Ide.getMainWindow(), "Loading Test", testLoader, true);
		testLoader.setProgressBar(progressBar);
		progressBar.start("Loading tests for " + suiteName, null);
		progressBar = null;
		return testLoader.getPlunitTestSuite();
	}

	@Override
	public void setArgs(String arg0) {}

	public boolean handleEvent(IdeAction arg0, Context arg1) {
		return false;
	}

	public boolean update(IdeAction arg0, Context arg1) {
		return false;
	}

	private String getPackageName() {
		return getContext().getElement().getShortLabel();
	}
	
	public static class PlunitTestLoader implements Runnable {
		private PlunitTestSuite testSuite;
		private String testName;
		private Connection connection;
		private ProgressBar progressBar;
		private PopUp popup = new SqlDeveloperNotifier();

		public PlunitTestLoader(String testName, Connection connection) {
			this.testName = testName;
			this.connection = connection;
		}

		public void run() {
			try {
				testSuite = PlunitTestSuite.build(testName, connection);
			} catch (SQLException e) {
				popup.show("Cannot load tests for " + testName + " error: " + e);
			} finally {
				progressBar.setDoneStatus();
			}
		}

		public PlunitTestSuite getPlunitTestSuite() {
			return testSuite;
		}

		public void setProgressBar(ProgressBar progressBar) {
			this.progressBar = progressBar;
		}
	}
}