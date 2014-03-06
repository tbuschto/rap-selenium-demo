package org.selenium.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.selenium.tests.RAPUtil.atLastPosition;
import static org.selenium.tests.RAPUtil.byAria;
import static org.selenium.tests.RAPUtil.byId;
import static org.selenium.tests.RAPUtil.byText;
import static org.selenium.tests.RAPUtil.cell;
import static org.selenium.tests.RAPUtil.containing;
import static org.selenium.tests.RAPUtil.firstResult;
import static org.selenium.tests.RAPUtil.row;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.thoughtworks.selenium.Selenium;

public class Grid_Test {

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
    driver.manage().window().setSize( new Dimension( 1000, 1000 ) );
    //driver.manage().window().maximize();
    rap = new RAPUtil( driver, selenium );
    rap.loadApplication( baseUrl + "/controls" );
  }

  @After
  public void tearDown() throws Exception {
    selenium.stop();
  }

  private void goToVirtualTable() {
    String navId = rap.getId( firstResult( byAria( "treegrid" ) ) );
    rap.click( rap.findGridItem( byId( navId ), "TableViewer" ) );
    rap.waitForAppear( byAria( "checkbox" ) + byText( "VIRTUAL" ) );
    rap.click( byAria( "checkbox" ) + byText( "VIRTUAL" ) );
    rap.waitForServer(); // best method since there is obvious UI change
  }

  @Test
  public void testGetLineOffset() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    selenium.click( byText( "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    selenium.click( grid + row() + atLastPosition( -1 ) ); // clicking the very last can be bad
    rap.press( grid, "Down" );
    rap.press( grid, "Down" );
    rap.press( grid, "Down" );
    assertEquals( 3, rap.getGridLineOffset( grid ) );
  }

  @Test
  public void testGetLineCount() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    assertEquals( 12, rap.getGridLineCount( grid ) );
    selenium.click( byText( "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 112, rap.getGridLineCount( grid ) );
  }

  @Test
  public void testScrollGridByPage() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    selenium.click( byText( "Add 100 Items" ) );
    rap.waitForServer();
    int rowcount = rap.getVisibleGridLines( grid );
    rap.scrollGridPageDown( grid );
    int offset = rap.getGridLineOffset( grid );
    assertTrue( offset == rowcount || offset == rowcount -1  );
    rap.scrollGridPageDown( grid );
    rap.scrollGridPageDown( grid );
    rap.scrollGridPageUp( grid );
    offset = rap.getGridLineOffset( grid );
    assertTrue( offset <= rowcount * 2 && offset > ( rowcount - 1 ) * 2 );
  }

  @Test
  public void testScrollLineIntoView() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    assertEquals( rowById( grid, "von Neumann" ), rap.scrollGridLineIntoView( grid, 1 ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) ); // did nothing
    selenium.click( byText( "Add 100 Items" ) );
    rap.waitForServer();
    assertNull( rap.scrollGridLineIntoView( grid, 9000 ) );
    assertTrue( rap.isElementAvailable( grid + RAPUtil.cell( "person 110" ) ) );
    rap.scrollGridPageDown( grid ); // one more (empty) line that can be scrolled, can be trouble
    String ada = rap.scrollGridLineIntoView( grid, 0 );
    assertEquals( rowById( grid, "Ada" ), ada );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    String person76 = rap.scrollGridLineIntoView( grid, 77 );
    assertEquals( rowById( grid, "person 76" ), person76 );
    String person50 = rap.scrollGridLineIntoView( grid, 50 );
    assertEquals( rowById( grid, "person 49" ), person50 );
  }

  private String rowById( String grid, String text ) {
    return byId( rap.getId( grid + RAPUtil.row( text ) ) );
  }

  @Test
  public void testScrollItemIntoView() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    selenium.click( byText( "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    rap.scrollGridItemIntoView( grid, cell( "Ada" ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) ); // did nothing
    rap.scrollGridItemIntoView( grid, cell( "person 110" ) );
    rap.click( grid + cell( "person 110" ) );
    rap.scrollGridItemIntoView( grid, cell( "Ada" ) );
    rap.click( grid + cell( "Ada" ) );
    rap.scrollGridItemIntoView( grid, cell( "person 76" ) );
    rap.click( grid + cell( "person 76" ) );
    rap.scrollGridItemIntoView( grid, cell( "person 49" ) );
    rap.click( grid + cell( "person 49" ) );
    try {
      rap.scrollGridItemIntoView( grid, cell( "person foo" ) );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testTableWithFlowTo() throws Exception {
    goToVirtualTable();
    selenium.click( byText( "Add 100 Items" ) );
    rap.waitForServer();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    String scrollbar = grid + byAria( "scrollbar", "orientation", "vertical" );
    String clientarea = byId( rap.getAttribute( scrollbar, "aria-controls" ) );
    for (int i = 1; i <= 60; i++) {
      rap.scrollWheel( clientarea, -1 );
    }
    rap.waitForItems( grid );
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
    assertEquals( "new 110", entry.getText() );

  }



}
