package org.eclipse.pde.internal.base.model.feature;

public interface IFeatureData extends IFeatureEntry {
	/**
	 * Tests if the library that this object points to
	 * exists in the project.
	 */
	public boolean exists();
}
