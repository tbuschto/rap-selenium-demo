package org.eclipse.rap.selenium.xpath;


public class XPath {


  final StringBuilder stringBuilder;

  public static XPath root() {
    return new XPath( "/*" );
  }

  public static XPathElementSelector any() {
    return new XPathElementSelector( new XPath( "//" ) );
  }

  public XPath( String initial ) {
    stringBuilder = new StringBuilder( initial );
  }

  public XPathElementSelector child() {
    stringBuilder.append( "/" );
    return new XPathElementSelector( this );
  }

  public XPathElementSelector parent() {
    stringBuilder.append( "/../" );
    return new XPathElementSelector( this );
  }

  public XPathElementSelector descendantOrSelf() {
    stringBuilder.append( "//" );
    return new XPathElementSelector( this );
  }

  public XPathElementSelector descendant() {
    stringBuilder.append( "/descendant::*/" );
    return new XPathElementSelector( this );
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }

}
