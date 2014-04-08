package org.eclipse.rap.selenium.xpath;




public class XPath<T extends AbstractElementSelector<?>> {

  final StringBuilder stringBuilder;
  T selector;

  public XPath( String initial) {
    stringBuilder = new StringBuilder( initial );
  }

  public T children() {
    stringBuilder.append( "/" );
    return selector;
  }

  public T descendants() {
    stringBuilder.append( "/descendant::*/" );
    return selector;
  }

  public T all() {
    stringBuilder.append( "//" );
    return selector;
  }

  public XPath<T> parent() {
    stringBuilder.append( "/.." );
    return this;
  }

  public XPath<T> child( int position ) {
    if( position <= 0 ) {
      throw new IllegalArgumentException( "position must be > 0" );
    }
    stringBuilder.append( "/*[" )
                 .append( position )
                 .append( "]" );
    return this;
  }

  public XPath<T> firstChild() {
    stringBuilder.append( "/*[1]" );
    return this;
  }

  public XPath<T> lastChild() {
    stringBuilder.append( "/*[last()]" );
    return this;
  }

  public XPath<T> firstMatch() {
    return match( 1 );
  }

  public XPath<T> lastMatch() {
    stringBuilder.insert( 0, "(" ).append( ")[last()]" );
    return this;
  }

  public XPath<T> match( int offset ) {
    if( offset <= 0 ) {
      throw new IllegalArgumentException( "Offset has to be > 0" );
    }
    stringBuilder.insert( 0, "(" ) .append( ")[" ) .append( offset ) .append( "]" );
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public XPath<T> clone() {
    return ( XPath<T> )selector.cloneXPath();
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }


}
