package org.selenium.tests;

import static org.eclipse.rap.selenium.AriaRoles.BUTTON;
import static org.eclipse.rap.selenium.AriaRoles.CHECK_BOX;
import static org.eclipse.rap.selenium.AriaRoles.GRID;
import static org.eclipse.rap.selenium.AriaRoles.GRID_CELL;
import static org.eclipse.rap.selenium.AriaRoles.TREE_GRID;
import static org.eclipse.rap.selenium.xpath.Predicate.with;
import static org.eclipse.rap.selenium.xpath.XPath.any;
import static org.eclipse.rap.selenium.xpath.XPath.byId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

public class AriaGrid_Test {

  private final static String URL = "http://127.0.0.1:8383";

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
    driver.manage().window().setSize( new Dimension( 1000, 1000 ) );
    //driver.manage().window().maximize();
    rap = new RapBot( driver, selenium );
    rap.loadApplication( URL );
  }

  @After
  public void tearDown() throws Exception {
    selenium.stop();
  }

  private void goToVirtualTable() {
    XPath navGrid = byId( rap.getId( any().widget( TREE_GRID ).firstMatch() ) );
    rap.click( rap.scrollGridItemIntoView( navGrid, "TableViewer" ) );
    rap.waitForAppear( any().widget( CHECK_BOX, "VIRTUAL" ) );
    rap.click( any().widget( CHECK_BOX, "VIRTUAL" ) );
    rap.waitForServer(); // best method since there is obvious UI change
  }

  private void goToSplitTable() { // the controls demo must be modified to render fixed columns
    XPath navGrid = byId( rap.getId( any().widget( TREE_GRID ).firstMatch() ) );
    rap.click( rap.scrollGridItemIntoView( navGrid, "Table" ) );
    rap.waitForAppear( any().widget( CHECK_BOX, "VIRTUAL" ) );
    XPath composite = any().element( with().text( "Add" ) ).parent().parent();
    XPath input = composite.descendants().widget( "textbox" );
    XPath button = composite.descendants().widget( BUTTON );
    rap.clearInput( input );
    rap.input( input, "100" );
    rap.click( button );
    rap.waitForServer();
  }

  @Test
  public void testGetLineOffset() throws Exception {
    goToVirtualTable();
    XPath grid = gridWithText( "First Name" );
    rap.waitForAppear( grid );
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    rap.click( RapBot.rowOf( grid ).self( with().position( -2 ) ) ); // clicking the very last row can be bad
    rap.press( grid, "Down" );
    rap.press( grid, "Down" );
    rap.press( grid, "Down" );
    assertEquals( 3, rap.getGridLineOffset( grid ) );
  }

  @Test
  public void testGetLineOffset_FixedColumns() throws Exception {
    goToSplitTable();
    XPath grid = gridWithText( "Item0-0" );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    rap.click( RapBot.rowOf( grid ).self( with().position( -2 ) ) ); // clicking the very last row can be bad
    rap.press( grid, "Down" );
    rap.press( grid, "Down" );
    rap.press( grid, "Down" );
    assertEquals( 3, rap.getGridLineOffset( grid ) );
  }

  @Test
  public void testGetLineCount() throws Exception {
    goToVirtualTable();
    XPath grid = gridWithText( "First Name" );
    rap.waitForAppear( grid );
    assertEquals( 12, rap.getGridLineCount( grid ) );
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 112, rap.getGridLineCount( grid ) );
  }

  @Test
  public void testGetLineCount_FixedColumns() throws Exception {
    goToSplitTable();
    XPath grid = gridWithText( "Item0-0" );
    assertEquals( 115, rap.getGridLineCount( grid ) );
  }

  @Test
  public void testScrollGridByPage() throws Exception {
    goToVirtualTable();
    XPath grid = gridWithText( "First Name" );
    rap.waitForAppear( grid );
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
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
    XPath grid = gridWithText( "Item0-0" );
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
    XPath grid = gridWithText( "Item0-0" );
    rap.waitForAppear( grid );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    assertEquals( rowId( grid, "von Neumann" ), rap.scrollGridLineIntoView( grid, 1 ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) ); // did nothing
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    assertNull( rap.scrollGridLineIntoView( grid, 9000 ) );
    assertTrue( rap.isElementAvailable( grid.descendants().widget( GRID_CELL, "person 110" ) ) );
    rap.scrollGridPageDown( grid ); // one more (empty) line that can be scrolled, can be trouble
    XPath ada = rap.scrollGridLineIntoView( grid, 0 );
    assertEquals( rowId( grid, "Ada" ), ada );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    XPath person76 = rap.scrollGridLineIntoView( grid, 77 );
    assertEquals( rowId( grid, "person 76" ), person76 );
    XPath person50 = rap.scrollGridLineIntoView( grid, 50 );
    assertEquals( rowId( grid, "person 49" ), person50 );
  }

  @Test
  public void testScrollLineIntoView_FixedColumns() throws Exception {
    goToSplitTable();
    XPath grid = gridWithText( "Item0-0" );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    assertEquals( rowId( grid, "Item1-0" ), rap.scrollGridLineIntoView( grid, 1 ) );
    assertEquals( 0, rap.getGridLineOffset( grid ) ); // did nothing
    assertNull( rap.scrollGridLineIntoView( grid, 9000 ) );
    assertTrue( rap.isElementAvailable( grid.descendants().widget( GRID_CELL, "Item114-0" ) ) );
    rap.scrollGridPageDown( grid ); // one more (empty) line that can be scrolled, can be trouble
    XPath ada = rap.scrollGridLineIntoView( grid, 0 );
    assertEquals( rowId( grid, "Item0-0" ), ada );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    XPath item77 = rap.scrollGridLineIntoView( grid, 77 );
    assertEquals( rowId( grid, "Item77-0" ), item77 );
    XPath item50 = rap.scrollGridLineIntoView( grid, 50 );
    assertEquals( rowId( grid, "Item50-0" ), item50 );
  }

  @Test
  public void testScrollItemIntoView() throws Exception {
    goToVirtualTable();
    XPath grid = gridWithText( "First Name" );
    rap.waitForAppear( grid );
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    rap.scrollGridItemIntoView( grid, "Ada" );
    assertEquals( 0, rap.getGridLineOffset( grid ) ); // did nothing
    rap.click( rap.scrollGridItemIntoView( grid, "person 110" ) );
    rap.click( rap.scrollGridItemIntoView( grid, "Ada" ) );
    rap.click( rap.scrollGridItemIntoView( grid, "person 76" ) );
    rap.click( rap.scrollGridItemIntoView( grid, "person 49" ) );
    try {
      rap.scrollGridItemIntoView( grid, "person foo" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testScrollItemIntoView_FixedColumns() throws Exception {
    goToSplitTable();
    XPath grid = gridWithText( "Item0-0" );
    assertEquals( 0, rap.getGridLineOffset( grid ) );
    rap.scrollGridItemIntoView( grid, "Item0-0" );
    assertEquals( 0, rap.getGridLineOffset( grid ) ); // did nothing
    rap.click( rap.scrollGridItemIntoView( grid, "Item114-0" ) );
    rap.click( rap.scrollGridItemIntoView( grid, "Item0-2" ) );
    rap.click( rap.scrollGridItemIntoView( grid, "Item77-3" ) );
    rap.click( rap.scrollGridItemIntoView( grid, "Item49-1" ) );
    try {
      rap.scrollGridItemIntoView( grid, "Item49-100" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testGetRowByCell() throws Exception {
    goToVirtualTable();
    XPath grid = gridWithText( "First Name" );
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
    XPath grid = gridWithText( "First Name" );
    rap.waitForAppear( grid );
    assertEquals( "Ada", rap.getGridCellContent( grid, rap.getGridRowByCell( grid, "Lovelace" ), "First Name" ) );
    assertEquals( "1903", rap.getGridCellContent( grid, rap.getGridRowAtLine( grid, 1 ), "Born" ) );
    try {
      rap.getGridCellContent( grid, rap.getGridRowAtLine( grid, 1 ), "Bornx" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
    try {
      rap.getGridCellContent( grid, rap.getGridRowByCell( grid, "foo" ), "Born" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
    try {
      rap.getGridCellContent( grid, rap.getGridRowByCell( grid, "John" ), "Born" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testGetCellContent_FixedColumns() throws Exception {
    goToSplitTable();
    XPath grid = gridWithText( "Item0-0" );
    XPath row0 = rap.getGridRowByCell( grid, "Item0-0" );
    XPath row1 = rap.getGridRowByCell( grid, "Item1-0" );
    assertEquals( "Item0-0", rap.getGridCellContent( grid, row0, "Col 0" ) );
    assertEquals( "Item1-4", rap.getGridCellContent( grid, row1, "Col 4" ) );
  }

  @Test
  public void testTableWithFlowTo() throws Exception {
    goToVirtualTable();
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    XPath grid = gridWithText( "First Name" );
    int gridLineCount = rap.getGridLineCount( grid );
    XPath lastItem = rap.scrollGridLineIntoView( grid, gridLineCount - 1 );
    assertEquals( "new 110", rap.getGridCellContent( grid, lastItem, "First Name" ) );
  }

  private XPath rowId( XPath grid, String text ) {
    return byId( rap.getId( rap.getGridRowByCell( grid, text ) ) );
  }

  private XPath gridWithText( String elementText ) {
    // Use the id since the content can change
    return byId( rap.getId( any().widget( GRID, with().textContent( elementText ) ) ) );
  }


}
