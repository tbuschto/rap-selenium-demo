package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.Predicate.with;

public class AriaElementSelector extends AbstractElementSelector<XPath<AriaElementSelector>> {

  AriaElementSelector( XPath<AriaElementSelector> xpath ) {
    super( xpath );
  }

  public static XPath<AriaElementSelector> root() {
    return createXPath( "/*" );
  }

  public static XPath<AriaElementSelector> byId( String id ) {
    return all().element( with().id( "foo" ) );
  }

  public static XPath<AriaElementSelector> byTestId( String id ) {
    return all().element( with().attr( "test-id", id ) );
  }

  public static AriaElementSelector all() {
    XPath<AriaElementSelector> xpath = createXPath( "//" );
    return xpath.selector;
  }

  public static XPath<AriaElementSelector> createXPath( String initial ) {
    XPath<AriaElementSelector> result = new XPath<AriaElementSelector>( initial );
    result.selector = new AriaElementSelector( result );
    return result;
  }

  public XPath<AriaElementSelector> widget( String role ) {
    return element( widgetWith( role ) );
  }

  public XPath<AriaElementSelector> widget( String role, Predicate predicate ) {
    return element( widgetWith( role ).and( predicate ) );
  }

  public XPath<AriaElementSelector> widget( String role, String ariaAttribute, String value ) {
    return element( widgetWith( role ).attr( "aria-" + ariaAttribute, value ) );
  }

  public XPath<AriaElementSelector> widget( String role, String text ) {
    return element( widgetWith( role ).textContent( text ) );
  }

  private static Predicate widgetWith( String role ) {
    return with().notAttr( "aria-hidden", "true" ).attr( "role", role );
  }

  @Override
  XPath<AriaElementSelector> cloneXPath() {
    return createXPath( xpath.toString() );
  }

}
