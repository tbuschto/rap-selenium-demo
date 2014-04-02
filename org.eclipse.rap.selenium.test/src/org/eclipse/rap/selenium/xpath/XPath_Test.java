package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.XPath.any;
import static org.eclipse.rap.selenium.xpath.XPath.root;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class XPath_Test {

  @Test
  public void testRootElement() {
    assertEquals( "/*", root().toString() );
  }

  @Test
  public void testAllElements() {
    assertEquals( "//*", any().element().toString() );
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
  public void testAllRootDescendantOrSelf() {
    assertEquals( "/*//a", root().descendantOrSelf().element( "a" ).toString() );
  }

  @Test
  public void testAllRootDescendant() {
    assertEquals( "/*/descendant::*/a", root().descendant().element( "a" ).toString() );
  }

  @Test
  public void testElementParent() {
    assertEquals( "//a/../*", any().element( "a" ).parent().element().toString() );
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
  public void testElementWithEmptyPredicateFails() {
    try {
      any().element( new Predicate() );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

}
