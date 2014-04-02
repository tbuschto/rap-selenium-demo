package org.eclipse.rap.selenium.xpath;


public abstract class AbstractElementSelector {

  XPath<?> xpath;

  AbstractElementSelector( XPath<?> xpath ) {
    this.xpath = xpath;
  }

  void appendElement( String name, Predicate predicate ) {
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
