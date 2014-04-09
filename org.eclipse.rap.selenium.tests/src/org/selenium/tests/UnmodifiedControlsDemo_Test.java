package org.selenium.tests;

import static org.eclipse.rap.selenium.xpath.Predicate.with;
import static org.eclipse.rap.selenium.xpath.XPathElementSelector.all;
import static org.eclipse.rap.selenium.xpath.XPathElementSelector.byId;

import org.eclipse.rap.selenium.RapBot;
import org.eclipse.rap.selenium.xpath.XPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.chrome.ChromeDriver;

import com.thoughtworks.selenium.Selenium;

public class UnmodifiedControlsDemo_Test {

  private final static String URL = "http://127.0.0.1:8383/";

  private WebDriver driver;
  private Selenium selenium;
  private RapBot rap;

  static {
    System.setProperty( "webdriver.firefox.bin", "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe" );
    // see http://code.google.com/p/selenium/wiki/ChromeDriver
    System.setProperty( "webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe" );
    // See http://code.google.com/p/selenium/wiki/InternetExplorerDriver
    System.setProperty( "webdriver.ie.driver", "C:\\Program Files\\Internet Explorer\\IEDriverServer.exe" );
  }

  @Before
  public void setUp() throws Exception {
    //driver = new FirefoxDriver();
    driver = new ChromeDriver();
    //driver = new InternetExplorerDriver(); // Not yet tested due to missing IE11 support
    selenium = new WebDriverBackedSelenium( driver, URL );
    driver.manage().window().setSize( new Dimension( 1024, 768 ) );
    rap = new RapBot( driver, selenium );
    rap.loadApplication( URL, false );
  }

  @After
  public void tearDown() throws Exception {
    selenium.stop();
  }

  @Test
  public void testClickButtons() throws Exception {
    rap.click( all().element( with().text( "Push\n Button" ) ) );
    rap.click( all().element( with().text( "Toggle" ) ) );
    rap.click( all().element( with().text( "Check" ) ) );
    rap.click( all().element( with().text( "Check with image" ) ) );
    rap.click( all().element( with().text( "Check" ) ) );
    rap.click( all().element( with().text( "Toggle" ) ) );
  }

  @Test
  public void testOpenCloseDialog() throws Exception {
    // This tests sometimes files with Chrome webdriver for no reason?
    rap.click( all().element( with().text( "Default Button" ) ).lastMatch() );
    String label = "The text You entered:";
    rap.waitForAppear( all().textElementContaining( label ) );//label actually may end with &nbsp;
    rap.click( all().element( with().text( "OK" ) ).lastMatch() );
    rap.waitForDisappear( all().textElementContaining( label ) );
    rap.click( all().element( with().text( "Push\n Button" ) ) );
  }

  @Test
  public void testInsertText() throws Exception {
    XPath<?> button = all().element( with().text( "Button" ) ).firstMatch();
    // This requires the widget IDs to be rendered using org.eclipse.rap.rwt.enableUITests=true
    String navId = rap.getId( button.parent().parent().parent() );
    XPath<?> textEl = byId( navId ).descendants().textElement( "Text" );
    //rap.click( button ); // make sure grid is focused to fire key events
    while( !rap.isElementAvailable( textEl ) ) {
      //rap.press( byId( navId ), PAGE_DOWN ); // <- would also work
      rap.scrollWheel( byId( navId ).firstChild(), -1 ); // firstChild is the container of rows
    }
    rap.click( textEl );
    rap.waitForAppear( all().element( with().text( "Text:" ) ) );
    rap.click( all().element( with().text( "VerifyListener (numbers only)" ) ) );
    rap.input( all().element( "input" ).firstMatch(), "hello123world" );
    rap.click( all().element( with().text( "getText" ) ) );
    rap.waitForAppear( all().element( with().text( "123" ) ) ); // non-numbers have been deleted by the verify listener
  }


}
