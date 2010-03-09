/*******************************************************************************
 * Copyright (c) 2010 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.tools.emf.ui.internal.common.component;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.e4.tools.emf.ui.common.component.AbstractComponentEditor;
import org.eclipse.e4.ui.model.application.MApplicationFactory;
import org.eclipse.e4.ui.model.application.MApplicationPackage;
import org.eclipse.e4.ui.model.application.MKeyBinding;
import org.eclipse.e4.ui.model.application.MKeySequence;
import org.eclipse.e4.ui.model.application.MParameter;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.databinding.edit.IEMFEditListProperty;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class KeyBindingEditor extends AbstractComponentEditor {

	private Composite composite;
	private Image image;
	private EMFDataBindingContext context;

	public KeyBindingEditor(EditingDomain editingDomain) {
		super(editingDomain);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Image getImage(Object element, Display display) {
		if( image == null ) {
			try {
				image = loadSharedImage(display, new URL("platform:/plugin/org.eclipse.e4.ui.model.workbench.edit/icons/full/obj16/KeyBinding.gif"));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return image;
	}

	@Override
	public String getLabel(Object element) {
		return "Binding";
	}

	@Override
	public String getDescription(Object element) {
		return "Binding bla bla bla";
	}

	@Override
	public Composite getEditor(Composite parent, Object object) {
		if( composite == null ) {
			context = new EMFDataBindingContext();
			composite = createForm(parent,context);
		}
		getMaster().setValue(object);
		return composite;
	}

	private Composite createForm(Composite parent, EMFDataBindingContext context) {
		parent = new Composite(parent,SWT.NONE);
		parent.setLayout(new GridLayout(3, false));

		IWidgetValueProperty textProp = WidgetProperties.text();

		// ------------------------------------------------------------
		{
			Label l = new Label(parent, SWT.NONE);
			l.setText("Id");

			Text t = new Text(parent, SWT.BORDER);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan=2;
			t.setLayoutData(gd);
			context.bindValue(textProp.observeDelayed(200,t), EMFEditProperties.value(getEditingDomain(), MApplicationPackage.Literals.APPLICATION_ELEMENT__ID).observeDetail(getMaster()));
		}

		// ------------------------------------------------------------
		{
			Label l = new Label(parent, SWT.NONE);
			l.setText("Sequence");

			Text t = new Text(parent, SWT.BORDER);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan=2;
			t.setLayoutData(gd);
			context.bindValue(textProp.observeDelayed(200,t), EMFEditProperties.value(getEditingDomain(), MApplicationPackage.Literals.KEY_SEQUENCE__KEY_SEQUENCE).observeDetail(getMaster()));
		}

		// ------------------------------------------------------------
		{
			Label l = new Label(parent, SWT.NONE);
			l.setText("Command");

			Text t = new Text(parent, SWT.BORDER);
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			t.setEnabled(false);
			context.bindValue(textProp.observeDelayed(200,t), EMFEditProperties.value( getEditingDomain(), FeaturePath.fromList(MApplicationPackage.Literals.KEY_BINDING__COMMAND, MApplicationPackage.Literals.APPLICATION_ELEMENT__ID)).observeDetail(getMaster()));

			Button b = new Button(parent, SWT.PUSH|SWT.FLAT);
			b.setText("Find ...");
			b.setImage(getImage(b.getDisplay(), SEARCH_IMAGE));
			b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		}

		Label l = new Label(parent, SWT.NONE);
		l.setText("Parameters");
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		final TableViewer tableviewer = new TableViewer(parent);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 120;
		tableviewer.getTable().setHeaderVisible(true);
		tableviewer.getControl().setLayoutData(gd);
		IEMFEditListProperty prop = EMFEditProperties.list(getEditingDomain(), MApplicationPackage.Literals.KEY_BINDING__PARAMETERS);

		TableViewerColumn column = new TableViewerColumn(tableviewer, SWT.NONE);
		column.getColumn().setText("Tag");
		column.getColumn().setWidth(200);
		column.setEditingSupport(new EditingSupport(tableviewer) {
			private TextCellEditor cellEditor = new TextCellEditor(tableviewer.getTable());

			@Override
			protected void setValue(Object element, Object value) {
				((MParameter)element).setTag((String) value);
			}

			@Override
			protected Object getValue(Object element) {
				String val = ((MParameter)element).getTag();
				return val == null ? "" : val;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return cellEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		column = new TableViewerColumn(tableviewer, SWT.NONE);
		column.getColumn().setText("Value");
		column.getColumn().setWidth(200);
		column.setEditingSupport(new EditingSupport(tableviewer) {
			private TextCellEditor cellEditor = new TextCellEditor(tableviewer.getTable());

			@Override
			protected void setValue(Object element, Object value) {
				((MParameter)element).setValue((String) value);
			}

			@Override
			protected Object getValue(Object element) {
				String val = ((MParameter)element).getValue();
				return val == null ? "" : val;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return cellEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		ColumnViewerEditorActivationStrategy editorActivationStrategy = new ColumnViewerEditorActivationStrategy(tableviewer) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				boolean singleSelect = ((IStructuredSelection)tableviewer.getSelection()).size() == 1;
				boolean isLeftDoubleMouseSelect = event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION && ((MouseEvent)event.sourceEvent).button == 1;

				return singleSelect && (isLeftDoubleMouseSelect
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC
						|| event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL);
			}
		};
		TableViewerEditor.create(tableviewer, editorActivationStrategy, ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR);

		ViewerSupport.bind(tableviewer, prop.observeDetail(getMaster()), new IValueProperty[] {
			EMFEditProperties.value(getEditingDomain(), MApplicationPackage.Literals.PARAMETER__TAG),
			EMFEditProperties.value(getEditingDomain(), MApplicationPackage.Literals.PARAMETER__VALUE)
		});

		Composite buttonComp = new Composite(parent, SWT.NONE);
		buttonComp.setLayoutData(new GridData(GridData.FILL,GridData.END,false,false));
		GridLayout gl = new GridLayout();
		gl.marginLeft=0;
		gl.marginRight=0;
		gl.marginWidth=0;
		gl.marginHeight=0;
		buttonComp.setLayout(gl);

		Button b = new Button(buttonComp, SWT.PUSH | SWT.FLAT);
		b.setText("Up");
		b.setImage(getImage(b.getDisplay(), ARROW_UP));
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		b = new Button(buttonComp, SWT.PUSH | SWT.FLAT);
		b.setText("Down");
		b.setImage(getImage(b.getDisplay(), ARROW_DOWN));
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		b = new Button(buttonComp, SWT.PUSH | SWT.FLAT);
		b.setText("Add ...");
		b.setImage(getImage(b.getDisplay(), TABLE_ADD_IMAGE));
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MKeyBinding item = (MKeyBinding) getMaster().getValue();
				MParameter param = MApplicationFactory.eINSTANCE.createParameter();
				Command cmd = AddCommand.create(getEditingDomain(), item, MApplicationPackage.Literals.KEY_BINDING__PARAMETERS, param);
				if( cmd.canExecute() ) {
					getEditingDomain().getCommandStack().execute(cmd);
				}
				tableviewer.editElement(param, 0);
			}
		});

		b = new Button(buttonComp, SWT.PUSH | SWT.FLAT);
		b.setText("Remove");
		b.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection s = (IStructuredSelection) tableviewer.getSelection();
				if( !s.isEmpty() ) {
					MKeyBinding item = (MKeyBinding) getMaster().getValue();
					Command cmd = RemoveCommand.create(getEditingDomain(), item, MApplicationPackage.Literals.KEY_BINDING__PARAMETERS, s.toList());
					if( cmd.canExecute() ) {
						getEditingDomain().getCommandStack().execute(cmd);
					}
				}
			}

		});
		b.setImage(getImage(b.getDisplay(), TABLE_DELETE_IMAGE));
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		ControlFactory.createTagsWidget(parent, this);

		return parent;
	}

	@Override
	public IObservableList getChildList(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDetailLabel(Object element) {
		MKeySequence seq = (MKeySequence) element;
		if( seq.getKeySequence() != null && seq.getKeySequence().trim().length() > 0 ) {
			return seq.getKeySequence();
		}
		return null;
	}

}
