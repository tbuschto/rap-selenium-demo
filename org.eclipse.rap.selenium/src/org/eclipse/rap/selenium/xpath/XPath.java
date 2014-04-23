package org.eclipse.rap.selenium.xpath;

import static java.lang.String.valueOf;
import static org.eclipse.rap.selenium.xpath.Predicate.with;

public class XPath extends AbstractPath<ElementSelector> {

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
    return createXPath( "/" ).children();
  }

  public static XPath createXPath( String initial ) {
    return new XPath( initial );
  }

  private final String path;

  private XPath( String path ) {
    this.path = path;
  }

  /////////////////////
  // relative selection

  public XPath parent() {
    return append( "/.." );
  }

  public XPath child( int position ) {
    if( position <= 0 ) {
      throw new IllegalArgumentException( "position must be > 0" );
    }
    return append( "/*[", valueOf( position ), "]" );
  }

  public XPath firstChild() {
    return append( "/*[1]" );
  }

  public XPath lastChild() {
    return append( "/*[last()]" );
  }

  ///////////////////
  // reduce selection

  public XPath self( Predicate predicate ) {
    StringBuilder stringBuilder = new StringBuilder( toString() );
    ElementSelector.appendPredicate( stringBuilder, predicate );
    return new XPath( stringBuilder.toString() );
  }

  public XPath firstMatch() {
    return match( 1 );
  }

  public XPath lastMatch() {
    return insert( 0, "(" ).append( ")[last()]" );
  }

  public XPath match( int offset ) {
    if( offset <= 0 ) {
      throw new IllegalArgumentException( "Offset has to be > 0" );
    }
    return insert( 0, "(" ).append( ")[", valueOf( offset ), "]" );
  }

  //////////////
  // Overwritten

  @Override
  public XPath clone() {
    return createXPath( this.toString() );
  }

  @Override
  public String toString() {
    return path;
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

  public XPath append( XPath xpath ) {
    return new XPath( toString() + xpath.toString() );
  }

  ////////////
  // Internals

  XPath append( String... strings ) {
    StringBuilder stringBuilder = new StringBuilder( path );
    for( int i = 0; i < strings.length; i++ ) {
      stringBuilder.append( strings[ i ] );
    }
    return new XPath( stringBuilder.toString() );
  }

  XPath insert( int offset, String string ) {
    StringBuilder stringBuilder = new StringBuilder( path );
    stringBuilder.insert( offset, string );
    return new XPath( stringBuilder.toString() );
  }

  @Override
  ElementSelector createSelector( String axis ) {
    return new ElementSelector( this, axis );
  }

}
