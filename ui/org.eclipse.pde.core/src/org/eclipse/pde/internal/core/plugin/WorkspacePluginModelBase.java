/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.core.plugin;

import java.io.*;
import java.net.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.pde.core.*;
import org.eclipse.pde.core.build.*;
import org.eclipse.pde.internal.core.*;

public abstract class WorkspacePluginModelBase
	extends AbstractPluginModelBase
	implements IEditableModel {
	private IFile file;
	private boolean dirty;
	private boolean editable = true;
	private IBuildModel buildModel;

	protected NLResourceHelper createNLResourceHelper() {
		String name = isFragmentModel() ? "fragment" : "plugin"; //$NON-NLS-1$ //$NON-NLS-2$
		NLResourceHelper helper =
			new NLResourceHelper(name, getNLLookupLocations());
		//helper.setFile(file);
		return helper;
	}
	
	public URL getNLLookupLocation() {
		IPath path = file.getLocation().removeLastSegments(1);
		String installLocation = path.toOSString();
		if (installLocation.startsWith("file:") == false) //$NON-NLS-1$
			installLocation = "file:" + installLocation; //$NON-NLS-1$
		try {
			URL url = new URL(installLocation + "/"); //$NON-NLS-1$
			return url;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public WorkspacePluginModelBase(IFile file) {
		setFile(file);
		setEnabled(true);
	}
	public void fireModelChanged(IModelChangedEvent event) {
		dirty = true;
		super.fireModelChanged(event);
	}
	public IBuildModel getBuildModel() {
		return buildModel;
	}
	public String getContents() {
		StringWriter swriter = new StringWriter();
		PrintWriter writer = new PrintWriter(swriter);
		save(writer);
		writer.flush();
		try {
			swriter.close();
		} catch (IOException e) {
		}
		return swriter.toString();
	}
	public IFile getFile() {
		return file;
	}
	public String getInstallLocation() {
		return file.getParent().getLocation().toOSString();
	}

	public IResource getUnderlyingResource() {
		return file;
	}

	public boolean isInSync() {
		if (file == null)
			return true;
		IPath path = file.getLocation();
		if (path == null)
			return false;
		return super.isInSync(path.toFile());
	}

	public boolean isDirty() {
		return dirty;
	}
	public boolean isEditable() {
		return editable;
	}

	public void dispose() {
		super.dispose();
	}

	public void load() {
		if (file == null)
			return;
		if (file.exists()) {
			InputStream stream = null;

			boolean outOfSync = false;

			try {
				stream = file.getContents(false);
			} catch (CoreException e) {
				outOfSync = true;
			}
			if (outOfSync) {
				try {
					stream = file.getContents(true);
				} catch (CoreException e) {
					return;
				}
			}
			try {
				load(stream, outOfSync);
				stream.close();

			} catch (CoreException e) {
			} catch (IOException e) {
				PDECore.logException(e);
			}
		} else {
			pluginBase = (PluginBase) createPluginBase();
			pluginBase.setModel(this);
			loaded = true;
		}
	}

	protected void updateTimeStamp() {
		updateTimeStamp(file.getLocation().toFile());
	}

	public void save() {
		if (file == null)
			return;
		try {
			String contents = getContents();
			ByteArrayInputStream stream =
				new ByteArrayInputStream(contents.getBytes("UTF8")); //$NON-NLS-1$
			if (file.exists()) {
				file.setContents(stream, false, false, null);
			} else {
				file.create(stream, false, null);
			}
			stream.close();
		} catch (CoreException e) {
			PDECore.logException(e);
		} catch (IOException e) {
		}
	}
	public void save(PrintWriter writer) {
		if (isLoaded()) {
			pluginBase.write("", writer); //$NON-NLS-1$
		}
		dirty = false;
	}
	public void setBuildModel(IBuildModel newBuildModel) {
		buildModel = newBuildModel;
	}
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	public void setEditable(boolean newEditable) {
		editable = newEditable;
	}
	public void setFile(IFile newFile) {
		file = newFile;
		//setEditable(newFile.isReadOnly()==false);
	}
}
