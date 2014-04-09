package org.eclipse.rap.selenium.xpath;

import static java.lang.String.valueOf;




public class XPath<T extends AbstractElementSelector<?>> {

  private final StringBuilder stringBuilder;
  T selector;

  public XPath( String initial) {
    stringBuilder = new StringBuilder( initial );
  }

  /////////////////
  // Axis selection

  public T children() {
    append( "/" );
    return selector;
  }

  public T descendants() {
    append( "/descendant::" );
    return selector;
  }

  public T followingSiblings() {
    append( "/following-sibling::" );
    return selector;
  }

  public T precedingSiblings() {
    append( "/preceding-sibling::" );
    return selector;
  }

  public T all() {
    append( "//" );
    return selector;
  }

  /////////////////////
  // relative selection

  public XPath<T> parent() {
    return clone().append( "/.." );
  }

  public XPath<T> child( int position ) {
    if( position <= 0 ) {
      throw new IllegalArgumentException( "position must be > 0" );
    }
    return clone().append( "/*[", valueOf( position ), "]" );
  }

  public XPath<T> firstChild() {
    return clone().append( "/*[1]" );
  }

  public XPath<T> lastChild() {
    return clone().append( "/*[last()]" );
  }

  ///////////////////
  // reduce selection

  public XPath<T> self( Predicate predicate ) {
    XPath<T> clone = clone();
    clone.selector.appendPredicate( predicate );
    return clone;
  }

  public XPath<T> firstMatch() {
    return match( 1 );
  }

  public XPath<T> lastMatch() {
    return clone().insert( 0, "(" ).append( ")[last()]" );
  }

  public XPath<T> match( int offset ) {
    if( offset <= 0 ) {
      throw new IllegalArgumentException( "Offset has to be > 0" );
    }
    return clone().insert( 0, "(" ).append( ")[", valueOf( offset ), "]" );
  }

  //////////////
  // Overwritten

  @Override
  @SuppressWarnings("unchecked")
  public XPath<T> clone() {
    return ( XPath<T> )selector.cloneXPath();
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals( Object obj ) {
    if( obj instanceof XPath<?> ) {
      return toString().equals( ( ( XPath<?> )obj ).toString() );
    }
    return false;
  }

  ////////////
  // Internals

  XPath<T> append( String... strings ) {
    for( int i = 0; i < strings.length; i++ ) {
      stringBuilder.append( strings[ i ] );
    }
    return this;
  }

  XPath<T> insert( int offset, String string ) {
    stringBuilder.insert( offset, string );
    return this;
  }

}
