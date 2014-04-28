/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.selenium.xpath;


public abstract class AbstractPath<S> {

  /////////////////
  // Axis selection

  public S children() {
    return createSelector( "/" );
  }

  public S descendants() {
    return createSelector( "/descendant::" );
  }

  public S followingSiblings() {
    return createSelector( "/following-sibling::" );
  }

  public S precedingSiblings() {
    return createSelector( "/preceding-sibling::" );
  }

  public S selfAndDescendants() {
    return createSelector( "//" );
  }

  abstract S createSelector( String axis );

}
