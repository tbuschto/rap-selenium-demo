package org.selenium.tests;


import static org.eclipse.rap.selenium.AriaRoles.*;
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


/**
 * This test required RAP to be started with the RAP ARIA Add-On.
 * In the entryPoint (i.e. ControlsDemo.java), add "Aria.activate();"
 */
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
    driver.manage().window().setSize( new Dimension( 1024, 768 ) );
    //driver.manage().window().maximize();
    rap = new RapBot( driver, selenium );
    rap.loadApplication( baseUrl + "/", false );
  }

  @After
  public void tearDown() throws Exception {
    selenium.stop();
  }

  @Test
  public void testClickButtons_byContent() throws Exception {
    rap.click( any().widget( BUTTON, "Push\n Button" ) );
    rap.click( any().widget( BUTTON, "Toggle" ) );
    rap.click( any().widget( CHECK_BOX, "Check" ) );
    rap.click( any().widget( CHECK_BOX, "Check with image" ) );
    rap.click( any().widget( CHECK_BOX, "Check" ) );
    rap.click( any().widget( BUTTON, "Toggle" ) );
  }

  @Test
  public void testOpenCloseDialog() throws Exception {
    rap.click( any().widget( BUTTON, "Default Button" ) );
    XPath OK = any().widget( DIALOG, "label", "Information" ).descendants().widget( BUTTON, "OK" );
    rap.waitForAppear( OK );
    rap.click( OK );
    rap.waitForDisappear( OK );
    rap.click( any().widget( BUTTON, "Push\n Button" ) );
  }

  @Test
  public void testInsertText() throws Exception {
    XPath navGrid = byId( rap.getId( any().widget( TREE_GRID ).firstMatch() ) );
    rap.click( rap.scrollGridItemIntoView( navGrid, "Text" ) );
    rap.waitForAppear( any().textElement( "Text:" ) );
    rap.click( any().widget( CHECK_BOX, "VerifyListener (numbers only)" ) );
    rap.input( any().widget( TEXT_BOX ).firstMatch(), "hello123world" );
    rap.click( any().widget( BUTTON, "getText" ) );
    rap.waitForAppear( any().textElement( "123" ) );
  }

}
