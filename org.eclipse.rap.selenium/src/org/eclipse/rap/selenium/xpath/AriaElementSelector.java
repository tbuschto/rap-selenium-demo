package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.Predicate.with;

public class AriaElementSelector extends AbstractElementSelector {

  AriaElementSelector( XPath<?> xpath ) {
    super( xpath );
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

  public Object widget( String role, String text ) {
    return element( widgetWith( role ).textContent( text ) );
  }

  public XPath<AriaElementSelector> element( Predicate predicate ) {
    return element( null, predicate );
  }

  public XPath<AriaElementSelector> element( String name, Predicate predicate ) {
    appendElement( name, predicate );
    return getXPath();
  }

  @SuppressWarnings("unchecked")
  private XPath<AriaElementSelector> getXPath() {
    return ( XPath<AriaElementSelector> )xpath;
  }

  private static Predicate widgetWith( String role ) {
    return with().notAttr( "aria-hidden", "true" ).attr( "role", role );
  }

}
