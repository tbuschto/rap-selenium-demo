package org.selenium.tests;

import static org.eclipse.rap.selenium.xpath.XPath.any;
import static org.eclipse.rap.selenium.xpath.XPath.byId;

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
    rap.click( any().textElement( "Push\n Button" ) );
    rap.click( any().textElement( "Toggle" ) );
    rap.click( any().textElement( "Check" ) );
    rap.click( any().textElement( "Check with image" ) );
    rap.click( any().textElement( "Check" ) );
    rap.click( any().textElement( "Toggle" ) );
  }

  @Test
  public void testOpenCloseDialog() throws Exception {
    // This tests sometimes files with Chrome webdriver for no reason?
    rap.click( any().textElement( "Default Button" ).lastMatch() ); // exits as label and button
    String label = "The text You entered:";
    rap.waitForAppear( any().textElementContaining( label ) ); //label actually may end with &nbsp;
    rap.click( any().textElement( "OK" ).lastMatch() );
    rap.waitForDisappear( any().textElementContaining( label ) ); // sometimes fails in chrome?
    rap.click( any().textElement( "Push\n Button" ) );
  }

  @Test
  public void testInsertText() throws Exception {
    XPath someNavItem = any().textElement( "Button" ).firstMatch();
    // This requires the widget IDs to be rendered using org.eclipse.rap.rwt.enableUITests=true
    XPath navigation = byId( rap.getId( someNavItem.parent().parent().parent() ) );
    XPath navItemText =  navigation.descendants().textElement( "Text" );
    while( !rap.isElementAvailable( navItemText ) ) {
      //rap.press( navigation.firstChild(), PAGE_DOWN ); // <- would also work, but cause navigation
      rap.scrollWheel( navigation.firstChild(), -1 ); // firstChild is the container of rows
    }
    rap.click( navItemText );
    rap.waitForAppear( any().textElement( "Text:" ) );
    rap.click( any().textElement( "VerifyListener (numbers only)" ) );
    rap.input( any().element( "input" ).firstMatch(), "hello123world" );
    rap.click( any().textElement( "getText" ) );
    rap.waitForAppear( any().textElement( "123" ) ); // non-numbers rejected be verify listener
  }

}
