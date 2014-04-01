package org.eclipse.rap.selenium.xpath;

import static org.eclipse.rap.selenium.xpath.Predicate.not;
import static org.eclipse.rap.selenium.xpath.Predicate.with;
import static org.junit.Assert.assertEquals;

import org.eclipse.rap.selenium.xpath.Predicate;
import org.junit.Test;




public class Predicate_Test {

  @Test
  public void testDefaultConstructor() {
    assertEquals( "", new Predicate().toString() );
  }

  @Test
  public void testFactory() {
    assertEquals( "", with().toString() );
  }

  @Test
  public void testFactoryWithInitial() {
    assertEquals( "(foo)", with( "foo" ).toString() );
  }

  @Test
  public void testAnd() {
    assertEquals( "(foo) and (bar)", with( "foo" ).and( "bar" ).toString() );
  }

  @Test
  public void testOr() {
    assertEquals( "(foo) or (bar)", with( "foo" ).or( "bar" ).toString() );
  }

  @Test
  public void testWithSingleAttribute() {
    assertEquals( "@foo='bar'", with().attr( "foo", "bar" ).toString() );
  }

  @Test
  public void testWithMultipleAttributes() {
    String expected = "@foo='bar' and @foo2='bar2'";
    assertEquals( expected, with().attr( "foo", "bar" ).attr( "foo2", "bar2" ).toString() );
  }

  @Test
  public void testNotWithAttribute() {
    assertEquals( "not(@foo='bar')", not( with().attr( "foo", "bar" ) ).toString() );
  }

  @Test
  public void testWithAttributesAndNotWithAttribute() {
    Predicate pred = with().attr( "foo", "bar" ).andNot( with().attr( "foo2", "bar2" ) );

    String expected = "@foo='bar' and not(@foo2='bar2')";
    assertEquals( expected, pred.toString() );
  }

  @Test
  public void testWithAttributesAndStaticNotWithAttribute() {
    Predicate pred = with().attr( "foo", "bar" ).and( not( with().attr( "foo2", "bar2" ) ) );

    String expected = "@foo='bar' and (not(@foo2='bar2'))";
    assertEquals( expected, pred.toString() );
  }

}
