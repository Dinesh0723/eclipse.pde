package org.eclipse.pde.internal.ui.editor.site;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.*;
import org.eclipse.jface.action.*;
import org.eclipse.pde.internal.ui.editor.*;


public class SiteEditorContributor extends PDEEditorContributor {
	private Action buildAction;
//	private SynchronizeVersionsAction synchronizeAction;
//	private PreviewAction previewAction;

public SiteEditorContributor() {
	super("Site");
}
public void contextMenuAboutToShow(IMenuManager mng) {
	super.contextMenuAboutToShow(mng);
	mng.add(new Separator());
	mng.add(buildAction);
}
/*
public Action getBuildAction() {
	return buildAction;
}
public Action getSynchronizeAction() {
	return synchronizeAction;
}
*/
protected void makeActions() {
	super.makeActions();
	buildAction = new Action() {
		public void run() {
			PDEMultiPageEditor editor = getEditor();
			BuildControlSection.handleBuild(editor);
		}
	};
	buildAction.setText("&Rebuild All");
}

public void setActiveEditor(IEditorPart targetEditor) {
	super.setActiveEditor(targetEditor);
/*
	buildAction.setActiveEditor((MultiPageSiteEditor) targetEditor);
	synchronizeAction.setActiveEditor((MultiPageSiteEditor) targetEditor);
	previewAction.setActiveEditor((MultiPageSiteEditor)targetEditor);
*/
}

	protected boolean hasKnownTypes(Clipboard clipboard) {
		return true;
	}
}
