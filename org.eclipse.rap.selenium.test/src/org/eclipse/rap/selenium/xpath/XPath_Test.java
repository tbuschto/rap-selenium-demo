package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.XPathElementSelector.any;
import static org.eclipse.rap.selenium.xpath.XPathElementSelector.asXPath;
import static org.eclipse.rap.selenium.xpath.XPathElementSelector.byId;
import static org.eclipse.rap.selenium.xpath.XPathElementSelector.root;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class XPath_Test {

  @Test
  public void testRootElement() {
    assertEquals( "/*", root().toString() );
  }

  @Test
  public void testAsXPath() {
    assertEquals( "foobar", asXPath( "foobar" ).toString() );
  }

  @Test
  public void testAllElements() {
    assertEquals( "//*", any().element().toString() );
  }

  @Test
  public void testById() {
    assertEquals( "//*[@id='foo']", byId( "foo" ).toString() );
  }

  @Test
  public void testAllElementsWithName() {
    assertEquals( "//a", any().element( "a" ).toString() );
  }

  @Test
  public void testAllRootChildren() {
    assertEquals( "/*/*", root().child().element().toString() );
  }

  @Test
  public void testChildNo() {
    assertEquals( "/*/*[12]", root().child( 12 ).toString() );
  }

  @Test
  public void testChildNoZero() {
    try {
      root().child( 0 );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testFirstChild() {
    assertEquals( "/*/*[1]", root().firstChild().toString() );
  }

  @Test
  public void testLastChild() {
    assertEquals( "/*/*[last()]", root().lastChild().toString() );
  }

  @Test
  public void testAllRootDescendantOrSelf() {
    assertEquals( "/*//a", root().descendantOrSelf().element( "a" ).toString() );
  }

  @Test
  public void testAllRootDescendant() {
    assertEquals( "/*/descendant::*/a", root().descendant().element( "a" ).toString() );
  }

  @Test
  public void testElementParent() {
    assertEquals( "//a/..", any().element( "a" ).parent().toString() );
  }

  @Test
  public void testElementWithPredicate() {
    assertEquals( "//*[id='foo']", any().element( new Predicate( "id='foo'" ) ).toString() );
  }

  @Test
  public void testTextElement() {
    assertEquals( "//*[text()='foo']", any().textElement( "foo" ).toString() );
  }

  @Test
  public void testMatchNo() {
    assertEquals( "(//a)[2]", any().element( "a" ).match( 2 ).toString() );
  }

  @Test
  public void testInvalidMatchNo() {
    try {
      any().element( "a" ).match( 0 );
      fail();
    } catch( IllegalArgumentException ex ) {
      // expected
    }
  }

  @Test
  public void testFirstMatch() {
    assertEquals( "(//a)[1]", any().element( "a" ).firstMatch().toString() );
  }

  @Test
  public void testLastMatch() {
    assertEquals( "(//a)[last()]", any().element( "a" ).lastMatch().toString() );
  }

  @Test
  public void testElementWithEmptyPredicateFails() {
    try {
      any().element( new Predicate() );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testClone() {
    XPath<XPathElementSelector> elementA = any().element( "a" );

    XPath<XPathElementSelector> clone = elementA.clone();
    clone.child().element( "b" );

    assertEquals( "//a", elementA.toString() );
    assertEquals( "//a/b", clone.toString() );
  }

}
