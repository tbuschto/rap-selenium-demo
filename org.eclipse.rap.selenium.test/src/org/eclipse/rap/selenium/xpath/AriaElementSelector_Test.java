package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.Predicate.with;
import static org.eclipse.rap.selenium.xpath.XPath.aria;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AriaElementSelector_Test {

  @Test
  public void testAllElements() {
    assertEquals( "//*", aria().element( null, null ).toString() );
  }

  @Test
  public void testWidgetWithRole() {
    String expected = "//*[@aria-hidden!='true' and @role='foo']";
    assertEquals( expected, aria().widget( "foo" ).toString() );
  }

  @Test
  public void testWidgetWithPredicate() {
    String expected = "//*[@aria-hidden!='true' and @role='foo' and (@foo='bar')]";
    assertEquals( expected, aria().widget( "foo", with().attr( "foo", "bar" ) ).toString() );
  }

  @Test
  public void testWidgetWithPredicateNull() {
    String expected = "//*[@aria-hidden!='true' and @role='foo']";
    assertEquals( expected, aria().widget( "foo", ( Predicate )null ).toString() );
  }

  @Test
  public void testWidgetWithAttribute() {
    String expected = "//*[@aria-hidden!='true' and @role='foo' and @aria-bar='bar2']";
    assertEquals( expected, aria().widget( "foo", "bar", "bar2" ).toString() );
  }

  @Test
  public void testWidgetWithText() {
    String expected = "//*[@aria-hidden!='true' and @role='foo' and count(descendant-or-self::*[text()='bar'])>0]";
    assertEquals( expected, aria().widget( "foo", "bar" ).toString() );
  }

}
