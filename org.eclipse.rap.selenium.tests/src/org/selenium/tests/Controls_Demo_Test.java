package org.selenium.tests;

import static org.junit.Assert.assertEquals;
import static org.selenium.tests.RAPUtil.byAria;
import static org.selenium.tests.RAPUtil.byContent;
import static org.selenium.tests.RAPUtil.byTestId;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.Selenium;

public class Controls_Demo_Test {

  private final static String baseUrl = "http://127.0.0.1:8383";

  private WebDriver driver;
  private Selenium selenium;
  private RAPUtil rap;

  static {
    System.setProperty( "webdriver.firefox.bin", "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe" );
  }

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    selenium = new WebDriverBackedSelenium( driver, baseUrl );
    rap = new RAPUtil( driver, selenium );
    rap.loadApplication( baseUrl + "/controls" );
  }

  @After
  public void tearDown() throws Exception {
    selenium.stop();
  }

  @Test
  public void testClickButtons_byTestId() throws Exception {
    rap.click( byTestId( "pushButton" ) );
    rap.click( byTestId( "toggleButton" ) );
    rap.click( byTestId( "checkButton1" ) );
    rap.click( byTestId( "checkButton2" ) );
    rap.click( byTestId( "checkButton1" ) );
    rap.click( byTestId( "toggleButton" ) );
  }

  @Test
  public void testClickButtons_byContent() throws Exception {
    rap.click( byAria( "button" ) + byContent( "Push\n Button" ) );
    rap.click( byAria( "button" ) + byContent( "Toggle" ) );
    rap.click( byAria( "checkbox" ) + byContent( "Check" ) );
    rap.click( byAria( "checkbox" ) + byContent( "Check with image" ) );
    rap.click( byAria( "checkbox" ) + byContent( "Check" ) );
    rap.click( byAria( "button" ) + byContent( "Toggle" ) );
  }

  @Test
  public void testOpenCloseDialog() throws Exception {
    rap.click( byTestId( "defaultButton" ) );
    String okButtonPath = byAria( "dialog", "Information" ) + byContent( "OK" );
    rap.waitForAppear( okButtonPath );
    rap.click( okButtonPath );
    rap.waitForDisappear( okButtonPath );
    rap.click( byTestId( "pushButton" ) );
  }

  @Test
  public void testNavigateTree() throws Exception {
    rap.click( rap.findGridItem( byTestId( "navtree" ), "Combo"  ) );
    rap.click( rap.findGridItem( byTestId( "navtree" ), "NLS" ) );
    rap.click( byAria( "radio" ) + byContent( "English" ) );
    rap.click( byAria( "radio" ) + byContent( "German" ) );
    rap.click( byAria( "radio" ) + byContent( "Spanish" ) );
    rap.click( rap.findGridItem( byTestId( "navtree" ), "Table" ) );
    rap.click( byAria( "button" ) + byContent( "Foreground" ) );
    rap.click( byAria( "button" ) + byContent( "Foreground" ) );
    rap.click( byAria( "button" ) + byContent( "Foreground" ) );
    rap.click( rap.findGridItem( byTestId( "navtree" ), "Button"  ) );
    //rap.click( rap.findGridItem( byTestId( "navtree" ), "doesnotexist" ) );
  }

  @Test
  public void testInsertText() throws Exception {
    rap.click( rap.findGridItem( byTestId( "navtree" ), "Text"  ) );
    rap.waitForAppear( byContent( "Text:" ) );
    rap.click( byAria( "checkbox" ) + byContent( "VerifyListener (numbers only)" ) );
    selenium.typeKeys( byAria( "textbox" ) + "[1]", "hello123world" );
    rap.click( byAria( "button" ) + byContent( "getText" ) );
    rap.waitForAppear( byContent( "123" ) );
    assertEquals( 1, selenium.getXpathCount( byContent( "123" ) ).intValue() );
  }

}
