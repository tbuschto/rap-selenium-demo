package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.XPath.any;
import static org.eclipse.rap.selenium.xpath.XPath.root;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.rap.selenium.xpath.Predicate;
import org.eclipse.rap.selenium.xpath.XPath;
import org.junit.Test;

public class XPath_Test {

  @Test
  public void testRootElement() {
    XPath path = root();
    assertEquals( "/*", path.toString() );
  }

  @Test
  public void testAllElements() {
    XPath path = any().element();
    assertEquals( "//*", path.toString() );
  }

  @Test
  public void testAllElementsWithName() {
    XPath path = any().element( "a" );
    assertEquals( "//a", path.toString() );
  }

  @Test
  public void testAllRootChildren() {
    XPath path = root().child().element();
    assertEquals( "/*/*", path.toString() );
  }

  @Test
  public void testAllRootDescendantOrSelf() {
    XPath path = root().descendantOrSelf().element( "a" );
    assertEquals( "/*//a", path.toString() );
  }

  @Test
  public void testAllRootDescendant() {
    XPath path = root().descendant().element( "a" );
    assertEquals( "/*/descendant::*/a", path.toString() );
  }

  @Test
  public void testElementParent() {
    XPath path = any().element( "a" ).parent().element();
    assertEquals( "//a/../*", path.toString() );
  }

  @Test
  public void testElementWithPredicate() {
    XPath path = any().element( new Predicate( "id='foo'" ) );
    assertEquals( "//*[id='foo']", path.toString() );
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

  //  @Test
//  public void testElementWithText() {
//    XPath path = elementWith().text( "foobar" );
//    assertEquals( "//*[text()='foobar']", path.toString() );
//  }
//
//  @Test
//  public void testElementWithAttrAndText() {
//    XPath path = elementWith().attr( "x", "foo" ).text( "foobar" );
//    assertEquals( "//*[@x='foo' and text()='foobar']", path.toString() );
//  }
//
//  @Test
//  public void testElementTextAndAttr() {
//    XPath path = elementWith().text( "foobar" ).attr( "x", "foo" );
//    assertEquals( "//*[text()='foobar' and @x='foo']", path.toString() );
//  }
//
//  @Test
//  public void testElementWithTwoAttr() {
//    XPath path = elementWith().attr( "x", "foo" ).attr( "y", "bar" );
//    assertEquals( "//*[@x='foo' and @y='bar']", path.toString() );
//  }
//
//  @Test
//  public void testElementWithTextContains() {
//    XPath path = elementWith().text( "foo" ).contains( elementWith().text( "bar" ) );
//    assertEquals( "//*[text()='foo']//*[text()='bar']", path.toString() );
//  }
//
//  @Test
//  public void testElementWithPosition() {
//    XPath path = elementWith().position( 2 );
//    assertEquals( "//*[position()=]", path.toString() );
//  }

  // @Test
  // public void testElementWithTextHasChild() {
  // XPath path = elementWith().text( "foo" )
  // .hasChild( elementWith().text( "bar" ) );
  // assertEquals( "//*[text()='foo']/*[text()='bar']", path.toString() );
  // }
  //
  // @Test
  // public void testElementWithTextContaining() {
  // XPath path = elementWith().text( "foo" )
  // .containing( elementWith().text( "bar" ) );
  // assertEquals( "//*[text()='foo']...", path.toString() );
  // }
}
