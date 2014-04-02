package org.eclipse.rap.selenium.xpath;


public class Predicate {

  final private StringBuilder stringBuilder;

  public static Predicate with() {
    return new Predicate();
  }

  public static Predicate with( Predicate predicate ) {
    return new Predicate( predicate.toString() );
  }

  public static Predicate with( String initial ) {
    return new Predicate().and( initial );
  }

  public static Predicate not( Predicate predicate ) {
    return new Predicate().andNot( predicate );
  }

  public Predicate() {
    stringBuilder = new StringBuilder();
  }

  public Predicate( String initial ) {
    stringBuilder = new StringBuilder( initial );
  }

  public Predicate and( Predicate predicate ) {
    return predicate != null ? and( predicate.toString() ) : this;
  }

  public Predicate and( String condition ) {
    return andAdd( "(", condition, ")" );
  }

  public Object or( String condition ) {
    return orAdd( "(", condition, ")" );
  }

  public Predicate text( String string ) {
    return andAdd( "text()='", string, "'" );
  }

  public Predicate content( XPath<?> content ) {
    return andAdd( "count(.", content.toString(), ")>0" );
  }

  public Predicate textContent( String elementText ) { // TODO : stringContent
    return andAdd( "count(descendant-or-self::*[text()='", elementText, "'])>0" );
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }

  public Predicate attr( String attribute, String value ) {
    return andAdd( "@", attribute, "='", value, "'" );
  }

  public Predicate notAttr( String attribute, String value ) {
    return andAdd( "@", attribute, "!='", value, "'" );
  }

//public XPath position( int i ) {
//  path.append( "position()=" ).append( i ); // the same as "[i]" would be
//  return this;
//}

  ////////////
  // Internals

  private Predicate andNot( Predicate predicate ) {
    return andAdd( "not(", predicate.toString(), ")" );
  }

  private Predicate andAdd( String... str ) {
    if( stringBuilder.length() > 0 ) {
      stringBuilder.append( " and " );
    }
    return add( str );
  }

  private Predicate orAdd( String... str ) {
    if( stringBuilder.length() > 0 ) {
      stringBuilder.append( " or " );
    }
    return add( str );
  }

  private Predicate add( String... str ) {
    for( String part : str ) {
      stringBuilder.append( part );
    }
    return this;
  }

}
