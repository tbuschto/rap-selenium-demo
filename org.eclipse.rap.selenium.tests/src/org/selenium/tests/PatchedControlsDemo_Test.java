package org.selenium.tests;

import static org.eclipse.rap.selenium.xpath.Predicate.with;
import static org.eclipse.rap.selenium.xpath.XPath.any;
import static org.eclipse.rap.selenium.xpath.XPath.byTestId;
import static org.junit.Assert.assertEquals;

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

public class PatchedControlsDemo_Test {

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
    rap.click( byTestId( "pushButton" ) );
    rap.click( byTestId( "toggleButton" ) );
    rap.click( byTestId( "checkButton1" ) );
    rap.click( byTestId( "checkButton2" ) );
    rap.click( byTestId( "checkButton1" ) );
    rap.click( byTestId( "toggleButton" ) );
  }

  @Test
  public void testOpenCloseDialog() throws Exception {
    rap.click( byTestId( "defaultButton" ) );
    String label = "The text You entered:";
    rap.waitForAppear( any().textElementContaining( label ) );//label actually ends with &nbsp;
    rap.click( any().element( with().text( "OK" ) ).lastMatch() );
    rap.waitForDisappear( any().textElementContaining( label ) );
    rap.click( byTestId( "pushButton" ) );
  }

  @Test
  public void testInsertText() throws Exception {
    XPath textEl = byTestId( "demoNavigation" ).descendants().textElement( "Text" );
    while( !rap.isElementAvailable( textEl ) ) {
      rap.scrollWheel( byTestId( "demoNavigation" ).firstChild(), -1 );
    }
    rap.click( textEl );
    rap.waitForAppear( byTestId( "textWidget" ) );
    rap.click( byTestId( "btnNumbersOnlyVerifyListener" ) );
    rap.input( byTestId( "textWidget" ).children().element( "input" ), "hello123world" );
    rap.click( byTestId( "btnGetText" ) );
    rap.waitForServer();
    assertEquals( "123", rap.getText( byTestId( "textLabel" ) ) );
  }


}
