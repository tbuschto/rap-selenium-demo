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

  public Predicate string( String string ) {
    return andAdd( "contains(text(),'", string, "')" );
  }

  public Predicate stringContent( String string ) {
    return andAdd( "count(descendant-or-self::*[contains(text(),'", string, "')])>0" );
  }

  public Predicate id( String id ) {
    if( "".equals( id ) ) {
      throw new IllegalArgumentException( "id may not be empty string" );
    }
    return attr( "id", id );
  }

  public Predicate attr( String attribute, String value ) {
    checkAttrParameter( attribute, value );
    return andAdd( "@", attribute, "='", value, "'" );
  }

  public Predicate notAttr( String attribute, String value ) {
    checkAttrParameter( attribute, value );
    return andAdd( "@", attribute, "!='", value, "'" );
  }

  public Predicate position( int position ) {
    if( position <= 0 ) {
      throw new IllegalArgumentException( "position must be > 0" );
    }
    return andAdd( "position()=" + position );
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }

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


  private void checkAttrParameter( String attribute, String value ) {
    if( attribute == null || value == null ) {
      throw new NullPointerException( "parameter is null" );
    }
    if( attribute.equals( "" ) ) {
      throw new IllegalArgumentException( "Attribute is empty string" );
    }
  }

}
