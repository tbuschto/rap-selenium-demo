package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.Predicate.with;



public class ElementSelector {

  XPath xpath;

  ElementSelector( XPath xpath ) {
    this.xpath = xpath;
  }

  public XPath textElement( String string ) {
    return element( Predicate.with().text( string ) );
  }

  public XPath textElementContaining( String string ) {
    return element( Predicate.with().string( string ) );
  }

  public XPath any() {
    return element( null, null );
  }

  public XPath element( Predicate predicate ) { // TODO : plural? rename/rework firstMatch? Should never END in plural
    return element( null, predicate );
  }

  public XPath element( String name ) {
    return element( name, null );
  }

  public XPath element( String name, Predicate predicate ) {
    appendElement( name );
    appendPredicate( predicate );
    return xpath;
  }

  public XPath widget( String role ) {
    return element( widgetWith( role ) );
  }

  public XPath widget( String role, Predicate predicate ) {
    return element( widgetWith( role ).and( predicate ) );
  }

  public XPath widget( String role, String ariaAttribute, String value ) {
    return element( widgetWith( role ).attr( "aria-" + ariaAttribute, value ) );
  }

  public XPath widget( String role, String text ) {
    return element( widgetWith( role ).textContent( text ) );
  }

  private static Predicate widgetWith( String role ) {
    return with().notAttr( "aria-hidden", "true" ).attr( "role", role );
  }

  private void appendElement( String name) {
    xpath.append( name != null ? name : "*" );
  }

  void appendPredicate( Predicate predicate ) {
    if( predicate != null ) {
      String predicateString = predicate.toString();
      if( predicateString.isEmpty() ) {
        throw new IllegalArgumentException( "Predicate is empty" );
      }
      xpath.append( "[", predicate.toString(), "]" );
    }
  }

}
