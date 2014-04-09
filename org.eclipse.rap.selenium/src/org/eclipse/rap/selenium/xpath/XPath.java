package org.eclipse.rap.selenium.xpath;

import static java.lang.String.valueOf;
import static org.eclipse.rap.selenium.xpath.Predicate.with;




public class XPath {

  public static XPath root() {
    return createXPath( "/*" );
  }

  public static XPath byId( String id ) {
    return any().element( with().id( id ) );
  }

  public static XPath byTestId( String id ) {
    return any().element( with().attr( "test-id", id ) );
  }

  public static ElementSelector any() {
    XPath xpath = createXPath( "//" );
    return xpath.selector;
  }

  public static XPath createXPath( String initial ) {
    XPath result = new XPath( initial );
    result.selector = new ElementSelector( result );
    return result;
  }

  private final StringBuilder stringBuilder;
  ElementSelector selector;

  public XPath( String initial) {
    stringBuilder = new StringBuilder( initial );
  }

  /////////////////
  // Axis selection

  public ElementSelector children() {
    append( "/" );
    return selector;
  }

  public ElementSelector descendants() {
    append( "/descendant::" );
    return selector;
  }

  public ElementSelector followingSiblings() {
    append( "/following-sibling::" );
    return selector;
  }

  public ElementSelector precedingSiblings() {
    append( "/preceding-sibling::" );
    return selector;
  }

  public ElementSelector selfAndDescendants() {
    append( "//" );
    return selector;
  }

  /////////////////////
  // relative selection

  public XPath parent() {
    return clone().append( "/.." );
  }

  public XPath child( int position ) {
    if( position <= 0 ) {
      throw new IllegalArgumentException( "position must be > 0" );
    }
    return clone().append( "/*[", valueOf( position ), "]" );
  }

  public XPath firstChild() {
    return clone().append( "/*[1]" );
  }

  public XPath lastChild() {
    return clone().append( "/*[last()]" );
  }

  ///////////////////
  // reduce selection

  public XPath self( Predicate predicate ) {
    XPath clone = clone();
    clone.selector.appendPredicate( predicate );
    return clone;
  }

  public XPath firstMatch() {
    return match( 1 );
  }

  public XPath lastMatch() {
    return clone().insert( 0, "(" ).append( ")[last()]" );
  }

  public XPath match( int offset ) {
    if( offset <= 0 ) {
      throw new IllegalArgumentException( "Offset has to be > 0" );
    }
    return clone().insert( 0, "(" ).append( ")[", valueOf( offset ), "]" );
  }

  //////////////
  // Overwritten

  @Override
  public XPath clone() {
    return createXPath( this.toString() );
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
    if( obj instanceof XPath ) {
      return toString().equals( ( ( XPath )obj ).toString() );
    }
    return false;
  }

  ////////////
  // Internals

  XPath append( String... strings ) {
    for( int i = 0; i < strings.length; i++ ) {
      stringBuilder.append( strings[ i ] );
    }
    return this;
  }

  XPath insert( int offset, String string ) {
    stringBuilder.insert( offset, string );
    return this;
  }

}
