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

  public T element( Predicate predicate ) { // TODO : plural? rename/rework firstMatch? Should never END in plural
    return element( null, predicate );
  }

  public T element( String name ) {
    return element( name, null );
  }

  public T element( String name, Predicate predicate ) {
    appendElement( name );
    appendPredicate( predicate );
    return xpath;
  }

  abstract T cloneXPath();

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
