package org.eclipse.pde.internal.editor.feature;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.*;
import org.eclipse.pde.internal.*;
import org.eclipse.pde.internal.base.model.feature.*;
import org.eclipse.core.runtime.*;
import org.eclipse.debug.internal.ui.actions.ResumeActionDelegate;
import org.eclipse.pde.model.plugin.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.*;
import org.eclipse.core.resources.*;
import org.eclipse.swt.events.*;
import org.eclipse.pde.internal.base.model.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.update.ui.forms.internal.*;
import org.eclipse.pde.internal.editor.*;
import org.eclipse.swt.*;
import org.eclipse.ui.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.pde.internal.elements.*;
import org.eclipse.pde.internal.util.*;
import org.eclipse.pde.internal.wizards.*;
import org.eclipse.pde.model.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.pde.internal.editor.PropertiesAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.pde.internal.model.*;
import org.eclipse.pde.internal.model.feature.FeaturePlugin;
import java.util.*;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.pde.internal.parts.TablePart;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;

public class DataSection
	extends TableSection
	implements IModelProviderListener {
	private static final String SECTION_TITLE = "FeatureEditor.DataSection.title";
	private static final String SECTION_DESC = "FeatureEditor.DataSection.desc";
	private static final String KEY_NEW = "FeatureEditor.DataSection.new";
	private static final String POPUP_NEW = "Menus.new.label";
	private static final String POPUP_DELETE = "Actions.delete.label";
	private boolean updateNeeded;
	private PropertiesAction propertiesAction;
	private TableViewer dataViewer;
	private Action newAction;
	private Action openAction;
	private Action deleteAction;

	class PluginContentProvider
		extends DefaultContentProvider
		implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			if (parent instanceof IFeature) {
				return ((IFeature) parent).getData();
			}
			return new Object[0];
		}
	}

	public DataSection(FeatureReferencePage page) {
		super(page, new String[] { PDEPlugin.getResourceString(KEY_NEW)});
		setHeaderText(PDEPlugin.getResourceString(SECTION_TITLE));
		setDescription(PDEPlugin.getResourceString(SECTION_DESC));
		getTablePart().setEditable(false);
		setCollapsable(true);
		IFeatureModel model = (IFeatureModel)page.getModel();
		IFeature feature = model.getFeature();
		setCollapsed(feature.getData().length==0);
	}

	public void commitChanges(boolean onSave) {
	}

	public Composite createClient(Composite parent, FormWidgetFactory factory) {
		Composite container = createClientContainer(parent, 2, factory);
		GridLayout layout = (GridLayout) container.getLayout();
		layout.verticalSpacing = 9;

		createViewerPartControl(container, SWT.MULTI, 2, factory);
		TablePart tablePart = getTablePart();
		dataViewer = tablePart.getTableViewer();
		dataViewer.setContentProvider(new PluginContentProvider());
		dataViewer.setLabelProvider(PDEPlugin.getDefault().getLabelProvider());
		factory.paintBordersFor(container);
		makeActions();
		return container;
	}

	protected void handleDoubleClick(IStructuredSelection selection) {
		openAction.run();
	}

	protected void buttonSelected(int index) {
		if (index == 0)
			handleNew();
	}

	public void dispose() {
		IFeatureModel model = (IFeatureModel) getFormPage().getModel();
		model.removeModelChangedListener(this);
		WorkspaceModelManager mng = PDEPlugin.getDefault().getWorkspaceModelManager();
		mng.removeModelProviderListener(this);
		super.dispose();
	}
	public void expandTo(Object object) {
		if (object instanceof IFeatureData) {
			dataViewer.setSelection(new StructuredSelection(object), true);
		}
	}
	protected void fillContextMenu(IMenuManager manager) {
		manager.add(openAction);
		manager.add(new Separator());
		manager.add(newAction);
		manager.add(deleteAction);
		manager.add(new Separator());
		getFormPage().getEditor().getContributor().contextMenuAboutToShow(manager);
		manager.add(new Separator());
		manager.add(propertiesAction);
	}

	private void handleNew() {
		final IFeatureModel model = (IFeatureModel) getFormPage().getModel();
		IResource resource = model.getUnderlyingResource();
		final IContainer folder = resource.getParent();

		BusyIndicator.showWhile(dataViewer.getTable().getDisplay(), new Runnable() {
			public void run() {
				ResourceSelectionDialog dialog =
					new ResourceSelectionDialog(dataViewer.getTable().getShell(), folder, null);
				dialog.open();
				Object[] result = dialog.getResult();
				processNewResult(model, folder, result);
			}
		});
	}
	private void processNewResult(
		IFeatureModel model,
		IContainer folder,
		Object[] result) {
		if (result==null || result.length==0) return;
		IPath folderPath = folder.getProjectRelativePath();
		ArrayList entries = new ArrayList();
		for (int i = 0; i < result.length; i++) {
			Object item = result[i];
			if (item instanceof IFile) {
				IFile file = (IFile) item;
				IPath filePath = file.getProjectRelativePath();
				int matching = filePath.matchingFirstSegments(folderPath);
				IPath relativePath = filePath.removeFirstSegments(matching);
				entries.add(relativePath);
			}
		}
		if (entries.size() > 0) {
			try {
				IFeatureData[] array = new IFeatureData[entries.size()];
				for (int i = 0; i < array.length; i++) {
					IFeatureData data = model.getFactory().createData();
					IPath path = (IPath) entries.get(i);
					data.setId(path.toString());
					array[i] = data;
				}
				model.getFeature().addData(array);
			} catch (CoreException e) {
				PDEPlugin.logException(e);
			}
		}
	}
	private void handleSelectAll() {
		IStructuredContentProvider provider =
			(IStructuredContentProvider) dataViewer.getContentProvider();
		Object[] elements = provider.getElements(dataViewer.getInput());
		StructuredSelection ssel = new StructuredSelection(elements);
		dataViewer.setSelection(ssel);
	}
	private void handleDelete() {
		IStructuredSelection ssel = (IStructuredSelection) dataViewer.getSelection();

		if (ssel.isEmpty())
			return;
		IFeatureModel model = (IFeatureModel) getFormPage().getModel();
		IFeature feature = model.getFeature();

		try {
			IFeatureData[] removed = new IFeatureData[ssel.size()];
			int i = 0;
			for (Iterator iter = ssel.iterator(); iter.hasNext();) {
				IFeatureData iobj = (IFeatureData) iter.next();
				removed[i++] = iobj;
			}
			feature.removeData(removed);
		} catch (CoreException e) {
			PDEPlugin.logException(e);
		}
	}
	public boolean doGlobalAction(String actionId) {
		if (actionId.equals(org.eclipse.ui.IWorkbenchActionConstants.DELETE)) {
			BusyIndicator.showWhile(dataViewer.getTable().getDisplay(), new Runnable() {
				public void run() {
					handleDelete();
				}
			});
			return true;
		}
		if (actionId.equals(IWorkbenchActionConstants.SELECT_ALL)) {
			BusyIndicator.showWhile(dataViewer.getTable().getDisplay(), new Runnable() {
				public void run() {
					handleSelectAll();
				}
			});
			return true;
		}
		return false;
	}
	protected void selectionChanged(IStructuredSelection selection) {
		getFormPage().setSelection(selection);
	}
	public void initialize(Object input) {
		IFeatureModel model = (IFeatureModel) input;
		update(input);
		if (model.isEditable() == false) {
			dataViewer.getTable().setEnabled(false);
		}
		model.addModelChangedListener(this);
		WorkspaceModelManager mng = PDEPlugin.getDefault().getWorkspaceModelManager();
		mng.addModelProviderListener(this);
	}

	public void modelChanged(IModelChangedEvent e) {
		if (e.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
			updateNeeded = true;
			if (getFormPage().isVisible()) {
				update();
			}
		} else {
			Object obj = e.getChangedObjects()[0];
			if (obj instanceof IFeatureData) {
				if (e.getChangeType() == IModelChangedEvent.CHANGE) {
					dataViewer.update(obj, null);
				} else if (e.getChangeType() == IModelChangedEvent.INSERT) {
					dataViewer.add(e.getChangedObjects());
				} else if (e.getChangeType() == IModelChangedEvent.REMOVE) {
					dataViewer.remove(e.getChangedObjects());
				}
			}
		}
	}
	private void makeActions() {
		newAction = new Action() {
			public void run() {
				handleNew();
			}
		};
		newAction.setText(PDEPlugin.getResourceString(POPUP_NEW));

		deleteAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(dataViewer.getTable().getDisplay(), new Runnable() {
					public void run() {
						handleDelete();
					}
				});
			}
		};
		deleteAction.setText(PDEPlugin.getResourceString(POPUP_DELETE));
		openAction = new OpenReferenceAction(dataViewer);
		propertiesAction = new PropertiesAction(getFormPage().getEditor());
	}

	public void modelsChanged(IModelProviderEvent event) {
		updateNeeded = true;
		update();
	}

	public void setFocus() {
		if (dataViewer != null)
			dataViewer.getTable().setFocus();
	}

	public void update() {
		if (updateNeeded) {
			this.update(getFormPage().getModel());
		}
	}

	public void update(Object input) {
		IFeatureModel model = (IFeatureModel) input;
		IFeature feature = model.getFeature();
		dataViewer.setInput(feature);
		updateNeeded = false;
	}
}