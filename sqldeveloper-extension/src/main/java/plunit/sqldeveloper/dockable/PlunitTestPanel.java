package plunit.sqldeveloper.dockable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import oracle.ide.controls.CustomJSplitPane;
import oracle.ide.controls.JAutoScrollPane;
import oracle.ide.controls.tree.CustomJTree;
import oracle.ide.controls.tree.JMutableTreeNode;
import plunit.InvalidPlunitTestException;
import plunit.PlunitTest;
import plunit.PlunitTestSuite;
import plunit.observers.PopUp;
import plunit.observers.TestSuiteListener;
import plunit.sqldeveloper.PlunitRunner;
import plunit.sqldeveloper.SqlDeveloperNotifier;
import plunit.Testable;
import plunit.TestState;

public class PlunitTestPanel extends JPanel implements TreeSelectionListener, TestSuiteListener {
	private static final long serialVersionUID = 1L;
	private final Color PASSING_COLOR = new Color(95, 191, 95);
	private final Color FAILING_COLOR = new Color(159, 63, 63);
	private final Color DEFAULT_BACKGROUND = new Color(173,212,241);
	private CustomJSplitPane splitPane = new CustomJSplitPane(CustomJSplitPane.VERTICAL_SPLIT);
	private JTextArea detailsPane = new JTextArea();
	private CustomJTree tree;
	private JMutableTreeNode parentTree;
	private JProgressBar progressBar;
	private JButton runAgainButton = new JButton();
	private JAutoScrollPane testView;
	private boolean reloadTestList = true;
	private PlunitTestSuite plunitTestSuite;
	private PopUp popup = new SqlDeveloperNotifier();
	private AbstractButton refreshButton = new JButton();
	private PlunitTestPanel thisPanel;
	private JLabel runsLabel = new JLabel();
	private JLabel failuresLabel = new JLabel();

	public PlunitTestPanel(PlunitTestSuite plunitTestSuite) {
		this.plunitTestSuite = plunitTestSuite;
		thisPanel = this;
		try {
			init();
		} catch (RuntimeException e) {
			popup.show(e);
		} catch (Exception e) {
			popup.show(e);
		}
	}

	public void valueChanged(TreeSelectionEvent e) {
		Object selectedTest = getSelectedTest();
		if(null != selectedTest && null != selectedTest.toString()) {
			detailsPane.setText(getSelectedTest().toString());
			detailsPane.setCaretPosition(0);
		} else {
			detailsPane.setText("");
		}
	}

	public void run() {
		Thread runner = new Thread() {
			public void run() {
				try {
					plunitTestSuite.runSuite();
				} catch (InvalidPlunitTestException e) {
					popup.show(plunitTestSuite.getName() + " is not valid Pl/Unit Test");
				} catch(SQLException e) {
					popup.show(e);
				}
			}
		};
		runner.start();
	}

	public void run(final PlunitTest test) {
		Runnable testRunner = new Runnable() {
			public void run() {
				try {
					plunitTestSuite.run(test);
				} catch (InvalidPlunitTestException e) {
					popup.show(plunitTestSuite.getName() + " is not valid Pl/Unit Test");
				} catch(SQLException e) {
					popup.show(e);
				}
			}
		};
		new Thread(testRunner, "Pl/Unit Test Runner").start();
	}

	public void notifyStart(int numberOfTests) {
		if (reloadTestList) {
			setupTree();
			setupPanels();
		}
		runAgainButton.setEnabled(false);
		refreshButton.setEnabled(false);
		progressBar.setValue(0);
		tree.setSelectionRow(0);
		progressBar.setMaximum(numberOfTests);
		progressBar.setForeground(PASSING_COLOR);
		refreshTestTree();
		repaint();
	}

	public void notify(PlunitTestSuite testSuite, int testNumber) {
		plunitTestSuite = testSuite;
		updateRunsLabel(testNumber);
		updateProgressBar(testSuite, testNumber);
		refreshTestTree();
		repaint();
	}

	private void updateRunsLabel(int testNumber) {
		runsLabel.setText(("    Runs:   "+testNumber+"/" + progressBar.getMaximum()));
	}

	public void notifyFinish() {
		runAgainButton.setEnabled(true);
		refreshButton.setEnabled(true);
		if(plunitTestSuite.getTests().size() == 1) {
			selectOnlyTest();
		}
	}

	private void selectOnlyTest() {
		tree.setSelectionPath( new TreePath(parentTree.getFirstLeaf().getPath()));
	}

	private void init() throws Exception {
		this.setBackground(DEFAULT_BACKGROUND);
		parentTree = new JMutableTreeNode(plunitTestSuite.getName());
		detailsPane.setEditable(false);
		detailsPane.setLineWrap(false);
		splitPane.setBottomComponent(new JAutoScrollPane(detailsPane));
		splitPane.setBounds(new Rectangle(new Dimension(400, 450)));
		buildRunAgainButton();
		buildRefreshButton();
		setupLayout();
		setupProgressBar();
	}
	
	private void setupLayout() {
		this.setLayout(new BorderLayout());
		this.setSize(new Dimension(500, 200));
	}

	private void setupProgressBar() {
		JPanel progressBarPanel = new JPanel(new BorderLayout());
		progressBarPanel.setBounds(new Rectangle(new Dimension(200, 25)));
		progressBar = new JProgressBar(0, plunitTestSuite.getTests().size());
		
		JPanel metricsPanel = new JPanel(new BorderLayout());
		metricsPanel.setBackground(DEFAULT_BACKGROUND);
		metricsPanel.add(runsLabel, BorderLayout.WEST);
		metricsPanel.add(failuresLabel, BorderLayout.EAST);
		progressBarPanel.add(metricsPanel, BorderLayout.NORTH);
		progressBarPanel.add(buildButtonPanel(), BorderLayout.WEST);
		progressBarPanel.add(progressBar, BorderLayout.CENTER);
		this.add(progressBarPanel, BorderLayout.NORTH);
	}

	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBackground(Color.white);
		buttonPanel.add(runAgainButton, BorderLayout.WEST);
		buttonPanel.add(refreshButton, BorderLayout.EAST);
		progressBar.setStringPainted(true);
		progressBar.setForeground(PASSING_COLOR);
		return buttonPanel;
	}

	private void buildRunAgainButton() {
		ImageIcon runAgainIcon = TestRenderer.createImageIcon("runAgain.png");
		runAgainButton.addActionListener(new RunAgainButtonListener());
		runAgainButton.setToolTipText("Rerun Test");
		runAgainButton.setIcon(runAgainIcon);
		runAgainButton.setMargin(new Insets(1,1,1,1));
		runAgainButton.setBackground(Color.white);
	}
	
	private void buildRefreshButton() {
		ImageIcon refreshIcon = TestRenderer.createImageIcon("refresh.png");
		refreshButton.addActionListener(new RefreshButtonListener());
		refreshButton.setToolTipText("Refresh Test List");
		refreshButton.setIcon(refreshIcon);
		refreshButton.setMargin(new Insets(1,2,1,2));
		refreshButton.setBackground(Color.white);
	}
	
	private void setupTree() {
		parentTree = new JMutableTreeNode(plunitTestSuite.getName());
		createNodes();
		tree = new CustomJTree(parentTree);
		tree.addMouseListener(new RunAgainMouseAdapter());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new TestRenderer());
		tree.addTreeSelectionListener(this);
	}

	private void setupPanels() {
		testView = new JAutoScrollPane(tree);
		splitPane.setTopComponent(testView);
		splitPane.setDividerLocation(.65);
		this.add(splitPane, BorderLayout.CENTER);
	}

	private void refreshTestTree() {
		parentTree.removeAllChildren();
		createNodes();
	}

	private void updateProgressBar(PlunitTestSuite testSuite, int testNumber) {
		int failureCount = 0;
		progressBar.setValue(testNumber);
		for (Testable test : testSuite.getTests()) {
			if (TestState.FAILING.equals(test.getState())) {
				progressBar.setForeground(FAILING_COLOR);
				failureCount++;
			}
		}
		failuresLabel.setText("Failures:  " + failureCount + "    ");
	}

	private void createNodes() {
		for (Testable test : plunitTestSuite.getTests()) {
			if(test instanceof PlunitTestSuite) {
				DefaultMutableTreeNode suiteNode = new DefaultMutableTreeNode( test);
				for(Testable suiteTest : ((PlunitTestSuite)test).getTests()) {
					suiteNode.add( new DefaultMutableTreeNode(suiteTest));
				}
				parentTree.add(suiteNode);
			} else {
				parentTree.add(new DefaultMutableTreeNode(test));
			}
		}
	}

	private Object getSelectedTest() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node == null) {
			return null;
		} else {
			return node.getUserObject();
		}
	}

	class RunAgainButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			reloadTestList = false;
			run();
		}
	}

	class RefreshButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			plunitTestSuite = PlunitRunner.loadTestSuite(plunitTestSuite.getName(), plunitTestSuite.getConnection());
			plunitTestSuite.setNotifier(popup);
			plunitTestSuite.addListener(thisPanel);
			reloadTestList = true;
			run();
			splitPane.setDividerLocation(.65);
		}
	}
	
	class RunAgainMouseAdapter extends MouseAdapter implements ActionListener {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger() && e.getClickCount() == 1) {
				doPopup(e.getX(), e.getY());
			}

			if (2 == e.getClickCount() && !e.isPopupTrigger()) {
				String testName = ((PlunitTest) getSelectedItem()).getName();
				PlunitCodeEditor editor = new PlunitEditorManager().getEditor(plunitTestSuite.getName());

				if (null != editor) {
					editor.highlightTest(testName);
				} else {
					// TODO: open editor for test double clicked on
//					try {
//						OpenEditorOptions editorOptions = new OpenEditorOptions();
//						editorOptions.setEditorClass(CodeEditor.class);
//						editorOptions.setFlags(OpenEditorOptions.FOCUS);
//						editorOptions.setFlags(OpenEditorOptions.RAISE);
//						EditorManager.getEditorManager().openEditor(editorOptions);
//					} catch (RuntimeException re) {
////						popup.show("Error opening editor!");
//					}
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && e.getClickCount() == 1) {
				tree.setSelectionRow(tree.getClosestRowForLocation(e.getX(), e.getY()));
				doPopup(e.getX(), e.getY());
			}
		}

		public void actionPerformed(ActionEvent event) {
			Object selectedItem = getSelectedItem();
			reloadTestList = true;
			if (selectedItem instanceof PlunitTest) {
				run((PlunitTest) selectedItem);
			} else {
				run();
			}
		}

		private Object getSelectedItem() {
			Object value = tree.getSelectionPath().getLastPathComponent();
			Object selectedItem = ((DefaultMutableTreeNode) value).getUserObject();
			return selectedItem;
		}

		private void doPopup(int x, int y) {
			TreePath clickedElement = tree.getPathForLocation(x, y);

			if (null != clickedElement) {
				JPopupMenu contextMenu = retrieveContextMenu(clickedElement);
				contextMenu.show(tree, x, y);
			}
		}

		private JPopupMenu retrieveContextMenu(TreePath clickedElement) {
			JPopupMenu contextMenu = new JPopupMenu();
			JMenuItem runMenuItem = new JMenuItem("Run");
			runMenuItem.addActionListener(this);

			contextMenu.add(runMenuItem);
			contextMenu.invalidate();
			contextMenu.pack();
			return contextMenu;
		}
	} // end RunAgainMouseAdapter
}
