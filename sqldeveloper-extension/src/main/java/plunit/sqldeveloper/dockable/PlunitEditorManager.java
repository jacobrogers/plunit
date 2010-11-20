package plunit.sqldeveloper.dockable;

import java.util.List;

import javax.swing.text.BadLocationException;

import oracle.ide.ceditor.CodeEditor;
import oracle.ide.editor.Editor;
import oracle.ide.editor.EditorManager;
import oracle.ide.editor.TextEditor;
import oracle.ideimpl.editor.EditorManagerImpl;

public class PlunitEditorManager extends EditorManagerImpl {
   EditorManager editorManager;
   
   public PlunitEditorManager() {
      super();
      editorManager = EditorManager.getEditorManager();
   }
   
   public PlunitCodeEditor getEditor(String suiteName) {
      PlunitCodeEditor plunitCodeEditor = null;
      List<Editor> editors = editorManager.getAllEditors();
      for( Editor editor : editors) {
         if(editor.getDisplayName().equalsIgnoreCase("CodeEditor") && editor.getTabLabel().contains(suiteName)) {
            plunitCodeEditor = new PlunitCodeEditor((CodeEditor) editor);
         } 
      }
      return plunitCodeEditor;
   }
}

class PlunitCodeEditor implements TextEditor {
   private CodeEditor editor;

   public PlunitCodeEditor(CodeEditor codeEditor) {
      this.editor = codeEditor;
   }
   
   public int getLineCount() {
      return editor.getLineCount();
   }
   
   public boolean gotoLine(int i, int j, boolean flag) {
      return editor.gotoLine(i, j, flag);
   }
   
   public void highlightTest(String testName) {
      testName = testName.toUpperCase();
      for(int i = 1; i < editor.getLineCount(); i++) {
         try {
            String line = editor.getText( editor.getLineStartOffset(i), editor.getLineEndOffset(i) - editor.getLineStartOffset(i));
            if(line.toUpperCase().contains(testName)) {
               editor.gotoLine(i, 0, true);
               break;
            }
         } catch (BadLocationException e1) {}
      }
   }
}
