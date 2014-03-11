package org.selenium.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.selenium.tests.RAPUtil.atLastPosition;
import static org.selenium.tests.RAPUtil.button;
import static org.selenium.tests.RAPUtil.byAria;
import static org.selenium.tests.RAPUtil.byId;
import static org.selenium.tests.RAPUtil.byText;
import static org.selenium.tests.RAPUtil.cellWithText;
import static org.selenium.tests.RAPUtil.checkbox;
import static org.selenium.tests.RAPUtil.containing;
import static org.selenium.tests.RAPUtil.firstResult;
import static org.selenium.tests.RAPUtil.label;
import static org.selenium.tests.RAPUtil.nextSibling;
import static org.selenium.tests.RAPUtil.row;
import static org.selenium.tests.RAPUtil.rowWithCellText;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
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
    rap.click( rap.scrollGridItemIntoView( byId( navId ), rowWithCellText( "TableViewer" ) ) );
    rap.waitForAppear( checkbox( "VIRTUAL" ) );
    rap.click( checkbox( "VIRTUAL" ) );
    rap.waitForServer(); // best method since there is obvious UI change
  }

  private void goToSplitTable() { // the controls demo must be modified to render fixed columns
    String navId = rap.getId( firstResult( byAria( "treegrid" ) ) );
    rap.click( rap.scrollGridItemIntoView( byId( navId ), rowWithCellText( "Table" ) ) );
    rap.waitForAppear( checkbox( "VIRTUAL" ) );
    String input = label( "Add" ) + nextSibling() + byAria( "textbox" ); // sibling CONTAINS box
    String button = label( "Add" ) + nextSibling() + nextSibling( button( "Item(s)" ) ); // sibling IS button
    rap.clear( input );
    rap.input( input, "100" );
    rap.click( button );
    rap.waitForServer();
  }

  @Test
  public void testGetLineOffset() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    rap.click( button( "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    rap.click( grid + row() + atLastPosition( -1 ) ); // clicking the very last can be bad
    rap.press( grid, "Down" );
    rap.press( grid, "Down" );
    rap.press( grid, "Down" );
    assertEquals( 3, rap.getGridLineOffset( grid ) );
  }

  @Test
  public void testGetLineOffset_FixedColumns() throws Exception {
    goToSplitTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "Item0-0" ) ) ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    rap.click( rap.getClientArea( grid ) + row() + atLastPosition( -1 ) ); // clicking the very last can be bad
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
    rap.click( button( "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 112, rap.getGridLineCount( grid ) );
  }

  @Test
  public void testGetLineCount_FixedColumns() throws Exception {
    goToSplitTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "Item0-0" ) ) ) );
    assertEquals( 115, rap.getGridLineCount( grid ) );
  }

  @Test
  public void testScrollGridByPage() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    rap.click( button( "Add 100 Items" ) );
    rap.waitForServer();
    int rowcount = rap.getVisibleGridLines( grid );
    rap.scrollGridPageDown( grid );
    int offset = rap.getGridLineOffset( grid );
    assertTrue( offset == rowcount || offset == rowcount -1  );
    rap.scrollGridPageDown( grid );
    rap.scrollGridPageDown( grid );
    rap.scrollGridPageUp( grid );
    offset = rap.getGridLineOffset( grid );
    assertTrue( offset <= rowcount * 2 && offset >= ( rowcount - 1 ) * 2 );
  }

  @Test
  public void testScrollGridByPage_FixedColumns() throws Exception {
    goToSplitTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "Item0-0" ) ) ) );
    int rowcount = rap.getVisibleGridLines( grid );
    rap.scrollGridPageDown( grid );
    int offset = rap.getGridLineOffset( grid );
    assertTrue( offset == rowcount || offset == rowcount -1  );
    rap.scrollGridPageDown( grid );
    rap.scrollGridPageDown( grid );
    rap.scrollGridPageUp( grid );
    offset = rap.getGridLineOffset( grid );
    assertTrue( offset <= rowcount * 2 && offset >= ( rowcount - 1 ) * 2 );
  }

  @Test
  public void testScrollLineIntoView() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    assertEquals( rowId( grid, "von Neumann" ), rap.scrollGridLineIntoView( grid, 1 ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) ); // did nothing
    rap.click( button( "Add 100 Items" ) );
    rap.waitForServer();
    assertNull( rap.scrollGridLineIntoView( grid, 9000 ) );
    assertTrue( rap.isElementAvailable( grid + RAPUtil.cellWithText( "person 110" ) ) );
    rap.scrollGridPageDown( grid ); // one more (empty) line that can be scrolled, can be trouble
    String ada = rap.scrollGridLineIntoView( grid, 0 );
    assertEquals( rowId( grid, "Ada" ), ada );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    String person76 = rap.scrollGridLineIntoView( grid, 77 );
    assertEquals( rowId( grid, "person 76" ), person76 );
    String person50 = rap.scrollGridLineIntoView( grid, 50 );
    assertEquals( rowId( grid, "person 49" ), person50 );
  }

  @Test
  public void testScrollLineIntoView_FixedColumns() throws Exception {
    goToSplitTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "Item0-0" ) ) ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    assertEquals( rowId( grid, "Item1-0" ), rap.scrollGridLineIntoView( grid, 1 ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) ); // did nothing
    assertNull( rap.scrollGridLineIntoView( grid, 9000 ) );
    assertTrue( rap.isElementAvailable( grid + cellWithText( "Item114-0" ) ) );
    rap.scrollGridPageDown( grid ); // one more (empty) line that can be scrolled, can be trouble
    String ada = rap.scrollGridLineIntoView( grid, 0 );
    assertEquals( rowId( grid, "Item0-0" ), ada );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    String item77 = rap.scrollGridLineIntoView( grid, 77 );
    assertEquals( rowId( grid, "Item77-0" ), item77 );
    String item50 = rap.scrollGridLineIntoView( grid, 50 );
    assertEquals( rowId( grid, "Item50-0" ), item50 );
  }

  @Test
  public void testScrollItemIntoView() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    rap.click( button( "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    rap.scrollGridItemIntoView( grid, cellWithText( "Ada" ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) ); // did nothing
    rap.scrollGridItemIntoView( grid, cellWithText( "person 110" ) );
    rap.click( grid + cellWithText( "person 110" ) );
    rap.scrollGridItemIntoView( grid, cellWithText( "Ada" ) );
    rap.click( grid + cellWithText( "Ada" ) );
    rap.scrollGridItemIntoView( grid, cellWithText( "person 76" ) );
    rap.click( grid + cellWithText( "person 76" ) );
    rap.scrollGridItemIntoView( grid, cellWithText( "person 49" ) );
    rap.click( grid + cellWithText( "person 49" ) );
    try {
      rap.scrollGridItemIntoView( grid, cellWithText( "person foo" ) );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testScrollItemIntoView_FixedColumns() throws Exception {
    goToSplitTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "Item0-0" ) ) ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    rap.scrollGridItemIntoView( grid, cellWithText( "Item0-0" ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) ); // did nothing
    rap.scrollGridItemIntoView( grid, cellWithText( "Item114-0" ) );
    rap.click( grid + cellWithText( "Item114-0" ) );
    rap.scrollGridItemIntoView( grid, cellWithText( "Item0-2" ) );
    rap.click( grid + cellWithText( "Item0-2" ) );
    rap.scrollGridItemIntoView( grid, cellWithText( "Item77-3" ) );
    rap.click( grid + cellWithText( "Item77-0" ) );
    rap.scrollGridItemIntoView( grid, cellWithText( "Item49-1" ) );
    rap.click( grid + cellWithText( "Item49-1" ) );
    try {
      rap.scrollGridItemIntoView( grid, cellWithText( "Item49-100" ) );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testGetRowByCell() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    assertEquals( rowId( grid, "Ada" ), rap.getGridRowByCell( grid, "Last Name", "Lovelace" ) );
    assertEquals( rowId( grid, "Turing" ), rap.getGridRowByCell( grid, "Married", "no" ) );
    assertNull( rap.getGridRowByCell( grid, "First Name", "Lovelace" ) ); // does not exist
    try {
      rap.getGridRowByCell( grid, "First", "Lovelace" ); // invalid column
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
    try {
      rap.getGridRowByCell( grid, "First Name", "John" ); // multiple results
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testGetCellContent() throws Exception {
    goToVirtualTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    rap.waitForAppear( grid );
    assertEquals( "Ada", rap.getGridCellContent( grid, rowWithCellText( "Lovelace" ), "First Name" ) );
    assertEquals( "1903", rap.getGridCellContent( grid, rap.getGridRowAtLine( grid, 1 ), "Born" ) );
    try {
      rap.getGridCellContent( grid, rap.getGridRowAtLine( grid, 1 ), "Bornx" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
    try {
      rap.getGridCellContent( grid, rowWithCellText( "foo" ), "Born" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
    try {
      rap.getGridCellContent( grid, rowWithCellText( "John" ), "Born" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testGetCellContent_FixedColumns() throws Exception {
    goToSplitTable();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "Item0-0" ) ) ) );
    assertEquals( "Item0-0", rap.getGridCellContent( grid, rowWithCellText( "Item0-0" ), "Col 0" ) );
    assertEquals( "Item1-4", rap.getGridCellContent( grid, rowWithCellText( "Item1-0" ), "Col 4" ) );
  }

  @Test
  public void testTableWithFlowTo() throws Exception {
    goToVirtualTable();
    rap.click( button( "Add 100 Items" ) );
    rap.waitForServer();
    String grid = byId( rap.getId( byAria( "grid" ) + containing( byText( "First Name" ) ) ) );
    int gridLineCount = rap.getGridLineCount( grid );
    String lastItem = rap.scrollGridLineIntoView( grid, gridLineCount - 1 );
    assertEquals( "new 110", rap.getGridCellContent( grid, lastItem, "First Name" ) );
  }

  private String rowId( String grid, String text ) {
    return byId( rap.getId( grid + RAPUtil.rowWithCellText( text ) ) );
  }

}
