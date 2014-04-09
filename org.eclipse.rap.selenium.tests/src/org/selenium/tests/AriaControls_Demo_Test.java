package org.selenium.tests;


import static org.eclipse.rap.selenium.AriaRoles.TREE_GRID;
import static org.eclipse.rap.selenium.xpath.Predicate.with;
import static org.eclipse.rap.selenium.xpath.XPath.any;
import static org.eclipse.rap.selenium.xpath.XPath.byId;
import static org.junit.Assert.assertEquals;

import org.eclipse.rap.selenium.RapBot;
import org.eclipse.rap.selenium.xpath.XPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.chrome.ChromeDriver;

import com.thoughtworks.selenium.Selenium;

public class AriaControls_Demo_Test {

  private final static String baseUrl = "http://127.0.0.1:8383";

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
    selenium = new WebDriverBackedSelenium( driver, baseUrl );
    //driver.manage().window().setSize( new Dimension( 1000, 1000 ) );
    driver.manage().window().maximize();
    rap = new RapBot( driver, selenium );
    rap.loadApplication( baseUrl + "/", false );
  }

  @After
  public void tearDown() throws Exception {
    selenium.stop();
  }

//  @Test
//  public void testClickButtons_byTestId() throws Exception {
//    rap.click( byTestId( "pushButton" ) );
//    rap.click( byTestId( "toggleButton" ) );
//    rap.click( byTestId( "checkButton1" ) );
//    rap.click( byTestId( "checkButton2" ) );
//    rap.click( byTestId( "checkButton1" ) );
//    rap.click( byTestId( "toggleButton" ) );
//  }

  @Test
  public void testClickElements_byContent() throws Exception {
    rap.click( any().element( with().text( "Push\n Button" ) ).toString() );
    rap.click( any().element( with().text( "Toggle" ) ).toString() );
    rap.click( any().element( with().text( "Check" ) ).toString() );
    rap.click( any().element( with().text( "Check with image" ) ).toString() );
    rap.click( any().element( with().text( "Check" ) ).toString() );
    rap.click( any().element( with().text( "Toggle" ) ).toString() );
  }

  @Test
  public void testClickButtons_byContent() throws Exception {
    rap.click( any().widget( "button" ).toString() + any().element( with().text( "Push\n Button" ) ).toString() );
    rap.click( any().widget( "button" ).toString() + any().element( with().text( "Toggle" ) ).toString() );
    rap.click( any().widget( "checkbox" ).toString() + any().element( with().text( "Check" ) ).toString() );
    rap.click( any().widget( "checkbox" ).toString() + any().element( with().text( "Check with image" ) ).toString() );
    rap.click( any().widget( "checkbox" ).toString() + any().element( with().text( "Check" ) ).toString() );
    rap.click( any().widget( "button" ).toString() + any().element( with().text( "Toggle" ) ).toString() );
  }

//  @Test
//  public void testOpenCloseDialog() throws Exception {
//    rap.click( byTestId( "defaultButton" ) );
//    String okButtonPath = byAria( "dialog", "label", "Information" ) + byText( "OK" );
//    rap.waitForAppear( okButtonPath );
//    rap.click( okButtonPath );
//    rap.waitForDisappear( okButtonPath );
//    rap.click( byTestId( "pushButton" ) );
//  }

  @Test
  public void testInsertText() throws Exception {
    XPath navGrid = byId( rap.getId( any().widget( TREE_GRID ).firstMatch() ) );
    rap.click( rap.scrollGridItemIntoView( navGrid, "Text" ) );
    rap.waitForAppear( any().element( with().text( "Text:" ) ).toString() );
    rap.click( any().widget( "checkbox" ).toString() + any().element( with().text( "VerifyListener (numbers only)" ) ).toString() );
    rap.input( any().widget( "textbox" ).firstMatch().toString(), "hello123world" );
    rap.click( any().widget( "button" ).toString() + any().element( with().text( "getText" ) ).toString() );
    rap.waitForAppear( any().element( with().text( "123" ) ).toString() );
    assertEquals( 1, selenium.getXpathCount( any().element( with().text( "123" ) ).toString() ).intValue() );
  }

}
