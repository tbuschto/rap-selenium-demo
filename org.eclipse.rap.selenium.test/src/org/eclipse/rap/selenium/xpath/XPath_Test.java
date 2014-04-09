package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.XPathElementSelector.all;
import static org.eclipse.rap.selenium.xpath.XPathElementSelector.byId;
import static org.eclipse.rap.selenium.xpath.XPathElementSelector.createXPath;
import static org.eclipse.rap.selenium.xpath.XPathElementSelector.root;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class XPath_Test {

  @Test
  public void testRootElement() {
    assertEquals( "/*", root().toString() );
  }

  @Test
  public void testCrateXPath() {
    assertEquals( "foobar", createXPath( "foobar" ).toString() );
  }

  @Test
  public void testAllElements() {
    assertEquals( "//*", all().any().toString() );
  }

  @Test
  public void testReturnClone() {
    XPath<XPathElementSelector> xpath1 = all().any();
    xpath1.child( 1 );
    assertEquals( "//*", xpath1.toString() );
  }

  @Test
  public void testById() {
    assertEquals( "//*[@id='foo']", byId( "foo" ).toString() );
  }

  @Test
  public void testAllElementsWithName() {
    assertEquals( "//a", all().element( "a" ).toString() );
  }

  @Test
  public void testAllRootChildren() {
    assertEquals( "/*/*", root().children().any().toString() );
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
    assertEquals( "/*//a", root().all().element( "a" ).toString() );
  }

  @Test
  public void testAllRootDescendantElementA() {
    assertEquals( "/*/descendant::a", root().descendants().element( "a" ).toString() );
  }

  @Test
  public void testElementParent() {
    assertEquals( "//a/..", all().element( "a" ).parent().toString() );
  }

  @Test
  public void testFollowingSiblings() {
    assertEquals( "/*/following-sibling::a", root().followingSiblings().element( "a" ).toString() );
  }

  @Test
  public void testPrecedingSiblings() {
    assertEquals( "/*/preceding-sibling::a", root().precedingSiblings().element( "a" ).toString() );
  }

  @Test
  public void testElementWithPredicate() {
    assertEquals( "//*[bar='foo']", all().element( new Predicate( "bar='foo'" ) ).toString() );
  }

  @Test
  public void testElementSelfWithPredicate() {
    assertEquals( "//a[bar='foo']", all().element( "a" ).self( new Predicate( "bar='foo'" ) ).toString() );
  }

  @Test
  public void testFirstMatch() {
    assertEquals( "(//a)[1]", all().element( "a" ).firstMatch().toString() );
  }

  @Test
  public void testMatchNo() {
    assertEquals( "(//a)[2]", all().element( "a" ).match( 2 ).toString() );
  }

  @Test
  public void testInvalidMatchNo() {
    try {
      all().element( "a" ).match( 0 );
      fail();
    } catch( IllegalArgumentException ex ) {
      // expected
    }
  }


  @Test
  public void testLastMatch() {
    assertEquals( "(//a)[last()]", all().element( "a" ).lastMatch().toString() );
  }

  @Test
  public void testElementWithEmptyPredicateFails() {
    try {
      all().element( new Predicate() );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testClone() {
    XPath<XPathElementSelector> elementA = all().element( "a" );

    XPath<XPathElementSelector> clone = elementA.clone();
    clone.children().element( "b" );

    assertEquals( "//a", elementA.toString() );
    assertEquals( "//a/b", clone.toString() );
  }

  @Test
  public void testEquals() {
    XPath<XPathElementSelector> element1 = all().element( "a" ).children().element( "b" );
    XPath<XPathElementSelector> element2 = all().element( "a" ).children().element( "b" );
    assertTrue( element1.equals( element2 ) );
    assertTrue( element2.equals( element1 ) );
    assertFalse( element1.equals( null ) );
    assertFalse( element2.equals( null ) );
  }

  @Test
  public void testToHash() {
    XPath<XPathElementSelector> element1 = all().element( "a" ).children().element( "b" );
    XPath<XPathElementSelector> element2 = all().element( "a" ).children().element( "b" );
    assertEquals( element1.hashCode(), element2.hashCode() );
    assertEquals( element2.hashCode(), element1.hashCode() );
  }

}
