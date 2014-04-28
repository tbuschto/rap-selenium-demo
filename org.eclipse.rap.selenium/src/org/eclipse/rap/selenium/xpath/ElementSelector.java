package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.Predicate.with;



public class ElementSelector {

  XPath xpath;
  private final String axis;

  ElementSelector( XPath xpath, String axis ) {
    this.xpath = xpath;
    this.axis = axis;
  }

  public XPath textElement( String string ) {
    return element( Predicate.with().text( string ) );
  }

  public XPath textElementContaining( String string ) {
    return element( Predicate.with().string( string ) );
  }

  public XPath element() {
    return element( null, null );
  }

  public XPath element( Predicate predicate ) {
    return element( null, predicate );
  }

  public XPath element( String name ) {
    return element( name, null );
  }

  public XPath element( String name, Predicate predicate ) {
    StringBuilder selection = new StringBuilder( axis );
    appendElement( selection, name );
    appendPredicate( selection, predicate );
    return xpath.append( selection.toString() );
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
    // TODO: use for text also, include in ARIA addon
    return with().notAria( "hidden", "true" ).attr( "role", role );
  }

  private static void appendElement( StringBuilder selection, String name ) {
    selection.append( name != null ? name : "*" );
  }

  static void appendPredicate( StringBuilder selection, Predicate predicate ) {
    if( predicate != null ) {
      String predicateString = predicate.toString();
      if( predicateString.isEmpty() ) {
        throw new IllegalArgumentException( "Predicate is empty" );
      }
      selection.append( "[" ).append( predicate.toString() ).append( "]" );
    }
  }

}
