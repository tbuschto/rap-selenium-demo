package org.eclipse.rap.selenium.xpath;



public class XPathElementSelector extends AbstractElementSelector {

  XPathElementSelector( XPath<?> xpath ) {
    super( xpath );
  }

  public XPath<XPathElementSelector> textElement( String string ) {
    return element( Predicate.with().text( string ) );
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
