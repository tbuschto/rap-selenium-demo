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
