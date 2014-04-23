package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.Predicate.with;
import static org.eclipse.rap.selenium.xpath.XPath.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ElementSelector_Test {

  @Test
  public void testAllElements() {
    assertEquals( "//*", any().element( null, null ).toString() );
  }

  @Test
  public void testRootElement() {
    assertEquals( "/*", root().toString() );
  }

  @Test
  public void testAsXPath() {
    assertEquals( "foobar", createXPath( "foobar" ).toString() );
  }

  @Test
  public void testById() {
    assertEquals( "//*[@id='foo']", byId( "foo" ).toString() );
  }

  @Test
  public void testWidgetWithRole() {
    String expected = "//*[not(@aria-hidden='true') and @role='foo']";
    assertEquals( expected, any().widget( "foo" ).toString() );
  }

  @Test
  public void testWidgetWithPredicate() {
    String expected = "//*[not(@aria-hidden='true') and @role='foo' and (@foo='bar')]";
    assertEquals( expected, any().widget( "foo", with().attr( "foo", "bar" ) ).toString() );
  }

  @Test
  public void testWidgetWithPredicateNull() {
    String expected = "//*[not(@aria-hidden='true') and @role='foo']";
    assertEquals( expected, any().widget( "foo", ( Predicate )null ).toString() );
  }

  @Test
  public void testWidgetWithAttribute() {
    String expected = "//*[not(@aria-hidden='true') and @role='foo' and @aria-bar='bar2']";
    assertEquals( expected, any().widget( "foo", "bar", "bar2" ).toString() );
  }

  @Test
  public void testWidgetWithText() {
    String expected = "//*[not(@aria-hidden='true') and @role='foo' and count(descendant-or-self::*[text()='bar'])>0]";
    assertEquals( expected, any().widget( "foo", "bar" ).toString() );
  }

  @Test
  public void testWidgetWithinWidget() {
    String expected = "//*[not(@aria-hidden='true') and @role='foo']/*[not(@aria-hidden='true') and @role='bar']";
    assertEquals( expected, any().widget( "foo" ).children().widget( "bar" ).toString() );
  }

}
