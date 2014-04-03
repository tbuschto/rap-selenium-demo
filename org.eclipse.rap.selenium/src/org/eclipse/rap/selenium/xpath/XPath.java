package org.eclipse.rap.selenium.xpath;




public class XPath<T extends AbstractElementSelector> {

  final StringBuilder stringBuilder;

  public XPath( String initial ) {
    stringBuilder = new StringBuilder( initial );
  }

  public T child() {
    stringBuilder.append( "/" );
    return createSelector();
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

  public T descendantOrSelf() {
    stringBuilder.append( "//" );
    return createSelector();
  }

  public T descendant() {
    stringBuilder.append( "/descendant::*/" );
    return createSelector();
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
  public XPath<T> clone() {
    return new XPath<T>( toString() );
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }

  @SuppressWarnings("unchecked")
  T createSelector() {
    return ( T )new XPathElementSelector( this );
  }

}
