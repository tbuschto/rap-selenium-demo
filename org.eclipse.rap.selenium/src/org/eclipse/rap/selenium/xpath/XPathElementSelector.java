package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.Predicate.with;



public class XPathElementSelector extends AbstractElementSelector<XPath<XPathElementSelector>> {

  // TODO : merge again with AriaElementSelector
  public static XPath<XPathElementSelector> root() {
    return createXPath( "/*" );
  }

  public static XPath<XPathElementSelector> byId( String id ) {
    return all().element( with().id( id ) );
  }

  public static XPath<XPathElementSelector> byTestId( String id ) {
    return all().element( with().attr( "test-id", id ) );
  }

  public static XPathElementSelector all() {
    XPath<XPathElementSelector> xpath = createXPath( "//" );
    return xpath.selector;
  }

  public static XPath<XPathElementSelector> createXPath( String initial ) {
    XPath<XPathElementSelector> result = new XPath<XPathElementSelector>( initial );
    result.selector = new XPathElementSelector( result );
    return result;
  }

  XPathElementSelector( XPath<XPathElementSelector> xpath ) {
    super( xpath );
  }

  @Override
  XPath<XPathElementSelector> cloneXPath() {
    return createXPath( xpath.toString() );
  }

}
