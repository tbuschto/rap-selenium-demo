package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.XPath.*;
import static org.junit.Assert.*;

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
    assertEquals( "//*", any().element( null, null ).toString() );
  }

  @Test
  public void testReturnClone() {
    XPath xpath1 = any().element();
    xpath1.child( 1 );
    assertEquals( "//*", xpath1.toString() );
  }

  @Test
  public void testDoNotChangeWhenSelectorReturns() {
    XPath xpath1 = any().element();
    xpath1.children();
    assertEquals( "//*", xpath1.toString() );
  }

  @Test
  public void testSameSelectorReturnsMultipleClones() {
    ElementSelector children = any().element().children();

    XPath xpath1 = children.element( "foo" );
    XPath xpath2 = children.element( "bar" );

    assertEquals( "//*/foo", xpath1.toString() );
    assertEquals( "//*/bar", xpath2.toString() );
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
    assertEquals( "/*/*", root().children().element().toString() );
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
    assertEquals( "/*//a", root().selfAndDescendants().element( "a" ).toString() );
  }

  @Test
  public void testAllRootDescendantElementA() {
    assertEquals( "/*/descendant::a", root().descendants().element( "a" ).toString() );
  }

  @Test
  public void testElementParent() {
    assertEquals( "//a/..", any().element( "a" ).parent().toString() );
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
    assertEquals( "//*[bar='foo']", any().element( new Predicate( "bar='foo'" ) ).toString() );
  }

  @Test
  public void testElementSelfWithPredicate() {
    assertEquals( "//a[bar='foo']", any().element( "a" ).self( new Predicate( "bar='foo'" ) ).toString() );
  }

  @Test
  public void testFirstMatch() {
    assertEquals( "//a[1]", any().element( "a" ).firstMatch().toString() );
  }

  @Test
  public void testMatchNo() {
    assertEquals( "//a[2]", any().element( "a" ).match( 2 ).toString() );
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
  public void testLastMatch() {
    assertEquals( "//a[last()]", any().element( "a" ).lastMatch().toString() );
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
  public void testEquals() {
    XPath element1 = any().element( "a" ).children().element( "b" );
    XPath element2 = any().element( "a" ).children().element( "b" );
    assertTrue( element1.equals( element2 ) );
    assertTrue( element2.equals( element1 ) );
    assertFalse( element1.equals( null ) );
    assertFalse( element2.equals( null ) );
  }

  @Test
  public void testToHash() {
    XPath element1 = any().element( "a" ).children().element( "b" );
    XPath element2 = any().element( "a" ).children().element( "b" );
    assertEquals( element1.hashCode(), element2.hashCode() );
    assertEquals( element2.hashCode(), element1.hashCode() );
  }

}
