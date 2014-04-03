package org.eclipse.rap.selenium.xpath;



public class XPathElementSelector extends AbstractElementSelector {


  public static XPath<XPathElementSelector> root() {
    return new XPath<XPathElementSelector>( "/*" );
  }

  public static XPathElementSelector any() {
    return new XPathElementSelector( new XPath<XPathElementSelector>( "//" ) );
  }

  public static XPath<XPathElementSelector> byId( String id ) {
    return any().element( Predicate.with().id( id ) );
  }

  public static XPath<XPathElementSelector> byText( String text ) {
    return any().element( Predicate.with().text( text ) );
  }

  public static XPath<XPathElementSelector> asXPath( String string ) {
    return new XPath<XPathElementSelector>( string );
  }

  XPathElementSelector( XPath<?> xpath ) {
    super( xpath );
  }

  public XPath<XPathElementSelector> textElement( String string ) {
    return element( Predicate.with().text( string ) );
  }

  public XPath<XPathElementSelector> textElementContaining( String string ) {
    return element( Predicate.with().string( string ) );
  }

  public XPath<XPathElementSelector> element() {
    return element( null, null );
  }

  public XPath<XPathElementSelector> element( String name ) {
    return element( name, null );
  }

  public XPath<XPathElementSelector> element( Predicate predicate ) {
    return element( null, predicate );
  }

  public XPath<XPathElementSelector> element( String name, Predicate predicate ) {
    appendElement( name, predicate );
    return getXPath();
  }

  @SuppressWarnings("unchecked")
  private XPath<XPathElementSelector> getXPath() {
    return ( XPath<XPathElementSelector> )xpath;
  }

}
