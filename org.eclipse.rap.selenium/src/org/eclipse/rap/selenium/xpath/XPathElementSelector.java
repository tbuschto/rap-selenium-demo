package org.eclipse.rap.selenium.xpath;


public class XPathElementSelector {


  XPath xpath;

  XPathElementSelector( XPath xpath ) {
    this.xpath = xpath;
  }

  public XPath element() {
    return element( null, null );
  }

  public XPath element( String name ) {
    return element( name, null );
  }

  public XPath element( Predicate predicate ) {
    return element( null, predicate );
  }

  public XPath element( String name, Predicate predicate ) {
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
    return xpath;
  }

}
