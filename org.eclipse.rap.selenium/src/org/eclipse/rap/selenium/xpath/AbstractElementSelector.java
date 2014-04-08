package org.eclipse.rap.selenium.xpath;


public abstract class AbstractElementSelector<T extends XPath<?>> {

  T xpath;

  AbstractElementSelector( T xpath ) {
    this.xpath = xpath;
  }

  public T textElement( String string ) {
    return element( Predicate.with().text( string ) );
  }

  public T textElementContaining( String string ) {
    return element( Predicate.with().string( string ) );
  }

  public T any() {
    return element( null, null );
  }

  public T element( Predicate predicate ) {
    return element( null, predicate );
  }

  public T element( String name ) {
    return element( name, null );
  }

  public T element( String name, Predicate predicate ) {
    appendElement( name, predicate );
    return xpath;
  }

  abstract T cloneXPath();

  private void appendElement( String name, Predicate predicate ) {
    xpath.stringBuilder.append( name != null ? name : "*" );
    if( predicate != null ) {
      String predicateString = predicate.toString();
      if( predicateString.isEmpty() ) {
        throw new IllegalArgumentException( "Predicate is empty" );
      }
      xpath.stringBuilder.append( "[" );
      xpath.stringBuilder.append( predicate.toString() );
      xpath.stringBuilder.append( "]" );
    }
  }

}
