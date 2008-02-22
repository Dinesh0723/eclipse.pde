/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.internal.provisional.search;

import org.eclipse.pde.api.tools.internal.provisional.IApiAnnotations;


/**
 * Describes a Java code reference from one component to another. An unresolved 
 * reference cannot guarantee the completeness of the information contained in 
 * its target location.
 * <p>
 * For example we can describe the reference from some type T to some type T1, where 
 * T resides in component C within package P and T1 resides in component C1 within package P1.
 * </p>
 * 
 * @noimplement This interface is not to be implemented by clients.
 * @since 1.0.0
 */
public interface IReference {

    /**
	 * Returns the originating location of this reference.
	 * For example if T references T1 the {@link ILocation} for T
	 * would be the source location.
	 * 
	 * @return the originating location of this reference.
	 */
	public ILocation getSourceLocation();
	
	/**
	 * Returns the unresolved location that has been referenced
	 * from the source location. 
	 * 
	 * @return the referenced location
	 */
	public ILocation getReferencedLocation();
	
	/**
	 * Returns the resolved referenced location or <code>null</code> if
	 * unresolved.
	 * 
	 * @return resolved referenced location or <code>null</code>
	 */
	public ILocation getResolvedLocation();
	
	/**
	 * Returns the kind of this reference. See constants defined 
	 * in {@link ReferenceModifiers}.
	 * 
	 * @return the kind of this reference.
	 */
	public int getReferenceKind();
	
	/**
	 * Returns the {@link IApiAnnotations} for the target location of this
	 * reference. If the reference is unresolved <code>null</code> is returned.
	 * @return the {@link IApiAnnotations} for the target location or <code>null</code> if unresolved.
	 */
	public IApiAnnotations getResolvedAnnotations();
	
}
