package plunit.sqldeveloper;

import javax.swing.JLabel;

import oracle.bali.ewt.dialog.JEWTDialog;
import oracle.ide.dialogs.OnePageWizardDialogFactory;
import oracle.ide.dialogs.WizardLauncher;
import plunit.observers.PopUp;

public class SqlDeveloperNotifier implements PopUp {
	public void show(String message) {
		JLabel label = new JLabel(message);
		JEWTDialog dlg = OnePageWizardDialogFactory.createJEWTDialog(label, null, "PL/Unit Test", 0);
		dlg.setDefaultButton(JEWTDialog.BUTTON_OK);
		dlg.setOKButtonEnabled(true);

		WizardLauncher.runDialog(dlg);
	}

	public void show(Exception e) {
		show("Exception: [" + e.getMessage() + "]");
	}

	public void show(RuntimeException e) {
		show("RuntimeException: [" + e.getStackTrace()[0].toString() + "]");
	}
}