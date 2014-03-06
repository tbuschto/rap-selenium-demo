package org.selenium.tests;

import static org.junit.Assert.assertEquals;
import static org.selenium.tests.RAPUtil.byAria;
import static org.selenium.tests.RAPUtil.byId;
import static org.selenium.tests.RAPUtil.byTestId;
import static org.selenium.tests.RAPUtil.byText;
import static org.selenium.tests.RAPUtil.containing;
import static org.selenium.tests.RAPUtil.firstResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.thoughtworks.selenium.Selenium;

public class Controls_Demo_Test {

  private final static String baseUrl = "http://127.0.0.1:8383";

  private WebDriver driver;
  private Selenium selenium;
  private RAPUtil rap;

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
    rap.click( byAria( "button" ) + byText( "Push\n Button" ) );
    rap.click( byAria( "button" ) + byText( "Toggle" ) );
    rap.click( byAria( "checkbox" ) + byText( "Check" ) );
    rap.click( byAria( "checkbox" ) + byText( "Check with image" ) );
    rap.click( byAria( "checkbox" ) + byText( "Check" ) );
    rap.click( byAria( "button" ) + byText( "Toggle" ) );
  }

  @Test
  public void testOpenCloseDialog() throws Exception {
    rap.click( byTestId( "defaultButton" ) );
    String okButtonPath = byAria( "dialog", "Information" ) + byText( "OK" );
    rap.waitForAppear( okButtonPath );
    rap.click( okButtonPath );
    rap.waitForDisappear( okButtonPath );
    rap.click( byTestId( "pushButton" ) );
  }

  @Test
  public void testInsertText() throws Exception {
    String navId = rap.getId( firstResult( byAria( "treegrid" ) ) );
    rap.click( rap.findGridItem( byId( navId ), "Text"  ) );
    rap.waitForAppear( byText( "Text:" ) );
    rap.click( byAria( "checkbox" ) + byText( "VerifyListener (numbers only)" ) );
    rap.input( firstResult( byAria( "textbox" ) ), "hello123world" );
    rap.click( byAria( "button" ) + byText( "getText" ) );
    rap.waitForAppear( byText( "123" ) );
    assertEquals( 1, selenium.getXpathCount( byText( "123" ) ).intValue() );
  }

  @Test
  public void testTableWithFlowTo() throws Exception {
    String navId = rap.getId( firstResult( byAria( "treegrid" ) ) );
    rap.click( rap.findGridItem( byId( navId ), "TableViewer" ) );
    rap.waitForAppear( byAria( "checkbox" ) + byText( "VIRTUAL" ) );
    rap.click( byAria( "checkbox" ) + byText( "VIRTUAL" ) );
    rap.waitForServer();
    selenium.click( byText( "Add 100 Items" ) );
    rap.waitForServer();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    String scrollbar = grid + byAria( "scrollbar", "orientation", "vertical" );
    String clientarea = byId( rap.getAttribute( scrollbar, "aria-controls" ) );
//    for (int i = 1; i <= 115; i++) {
//      rap.click(  grid + "/div[3]/div[3]"); // thats the scroll-down button... not a good idea to locate it that way
//    }
    for (int i = 1; i <= 60; i++) {
      rap.scrollWheel( clientarea, -1 );
    }
    String flowto = rap.getAttribute( grid, "aria-flowto" );
    WebElement row = null;
    while( flowto != null ) {
      row = driver.findElement( By.xpath("//div[@id='" + flowto + "']") );
      flowto = row.getAttribute("aria-flowto");
    }
    WebElement column = driver.findElement(By.xpath("//div[@role='columnheader']/div[text()='First Name']"));
    column = column.findElement(By.xpath(".."));
    WebElement entry = driver.findElement(By.xpath("//div[@id='" + row.getAttribute("id")
                                                   + "']/div[@aria-describedby='" + column.getAttribute("id") + "']"));
    //assertThat(entry.getText(), is("new 110"));
    assertEquals( "new 110", entry.getText() );

  }

}
