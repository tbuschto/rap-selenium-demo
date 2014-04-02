package org.eclipse.rap.selenium.xpath;




public class XPath<T extends AbstractElementSelector> {

  final StringBuilder stringBuilder;

  public static XPath<XPathElementSelector> root() {
    return new XPath<XPathElementSelector>( "/*" );
  }

  public static XPathElementSelector any() {
    return new XPathElementSelector( new XPath<XPathElementSelector>( "//" ) );
  }

  public static AriaElementSelector aria() {
    return new AriaElementSelector( new XPath<AriaElementSelector>( "//" ) );
  }

  public XPath( String initial ) {
    stringBuilder = new StringBuilder( initial );
  }

  public T child() {
    stringBuilder.append( "/" );
    return createSelector();
  }

  public T parent() {
    stringBuilder.append( "/../" );
    return createSelector();
  }

  public T descendantOrSelf() {
    stringBuilder.append( "//" );
    return createSelector();
  }

  public T descendant() {
    stringBuilder.append( "/descendant::*/" );
    return createSelector();
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
