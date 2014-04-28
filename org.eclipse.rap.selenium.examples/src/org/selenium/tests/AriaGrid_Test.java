/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

package org.selenium.tests;

import static org.eclipse.rap.selenium.AriaRoles.*;
import static org.eclipse.rap.selenium.xpath.Predicate.with;
import static org.eclipse.rap.selenium.xpath.XPath.any;
import static org.junit.Assert.*;

import org.eclipse.rap.selenium.AriaGridBot;
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
    //driver = new InternetExplorerDriver(); // Not yet tested due to missing IE11 support
    driver = new ChromeDriver();
    selenium = new WebDriverBackedSelenium( driver, URL );
    driver.manage().window().setSize( new Dimension( 1024, 768 ) );
    rap = new RapBot( driver, selenium );
    rap.loadApplication( URL );
  }

  @After
  public void tearDown() throws Exception {
    selenium.stop();
  }

  private void goToVirtualTable() {
    AriaGridBot navGrid = new AriaGridBot( rap, any().widget( TREE_GRID ).firstMatch() );
    rap.click( navGrid.scrollCellIntoView( "TableViewer" ) );
    rap.waitForAppear( any().widget( CHECK_BOX, "VIRTUAL" ) );
    rap.click( any().widget( CHECK_BOX, "VIRTUAL" ) );
    rap.waitForServer(); // best method since there is no obvious UI change
  }

  private void goToSplitTable() { // the controls demo must have been modified to use fixed columns
    AriaGridBot navGrid = new AriaGridBot( rap, any().widget( TREE_GRID ).firstMatch() );
    rap.click( navGrid.scrollCellIntoView( "Table" ) );
    rap.waitForAppear( any().widget( CHECK_BOX, "VIRTUAL" ) );
    XPath composite = any().textElement( "Add" ).parent().parent();
    XPath input = composite.descendants().widget( TEXT_BOX );
    XPath button = composite.descendants().widget( BUTTON );
    rap.clearInput( input );
    rap.input( input, "100" );
    rap.click( button );
    rap.waitForServer();
  }

  @Test
  public void testGetLineOffset() throws Exception {
    goToVirtualTable();
    AriaGridBot grid = gridWithText( "First Name" );
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 0, grid.getLineOffset() );
    rap.click( grid.row( with().offset( -2 ) ) ); // TODO: has to be -1 if last row is not off?
    rap.pressKey( grid.byId(), "Down" );
    rap.pressKey( grid.byId(), "Down" );
    rap.pressKey( grid.byId(), "Down" );
    assertEquals( 3, grid.getLineOffset() );
  }

  @Test
  public void testGetLineOffset_FixedColumns() throws Exception {
    goToSplitTable();
    AriaGridBot grid = gridWithText( "Item0-0" );
    assertEquals( 0, grid.getLineOffset() );
    rap.click( grid.row( with().offset( -2 ) ) );
    rap.pressKey( grid.byId(), "Down" );
    rap.pressKey( grid.byId(), "Down" );
    rap.pressKey( grid.byId(), "Down" );
    assertEquals( 3, grid.getLineOffset() );
  }

  @Test
  public void testGetLineCount() throws Exception {
    goToVirtualTable();
    AriaGridBot grid = gridWithText( "First Name" );
    assertEquals( 12, grid.getLineCount() );
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 112, grid.getLineCount() );
  }

  @Test
  public void testGetLineCount_FixedColumns() throws Exception {
    goToSplitTable();
    AriaGridBot grid = gridWithText( "Item0-0" );
    assertEquals( 115, grid.getLineCount() );
  }

  @Test
  public void testScrollGridByPage() throws Exception {
    goToVirtualTable();
    AriaGridBot grid = gridWithText( "First Name" );
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    int rowcount = grid.getVisibleLineCount();
    grid.getLineCount();
    int offset = grid.getLineOffset();
    assertTrue( offset == rowcount || offset == rowcount -1  );
    grid.getLineCount();
    grid.getLineCount();
    grid.getLineCount();
    offset = grid.getLineOffset();
    assertTrue( offset <= rowcount * 2 && offset >= ( rowcount - 1 ) * 2 );
  }

  @Test
  public void testScrollGridByPage_FixedColumns() throws Exception {
    goToSplitTable();
    AriaGridBot grid = gridWithText( "Item0-0" );
    int rowcount = grid.getVisibleLineCount();
    grid.getLineCount();
    int offset = grid.getLineOffset();
    assertTrue( offset == rowcount || offset == rowcount -1  );
    grid.getLineCount();
    grid.getLineCount();
    grid.getLineCount();
    offset = grid.getLineOffset();
    assertTrue( offset <= rowcount * 2 && offset >= ( rowcount - 1 ) * 2 );
  }

  @Test
  public void testScrollLineIntoView() throws Exception {
    goToVirtualTable();
    AriaGridBot grid = gridWithText( "First Name" );
    assertEquals( 0, grid.getLineOffset() );
    assertEquals( grid.getRowByCell( "von Neumann" ), grid.scrollLineIntoView( 1 ) );
    assertEquals( 0, grid.getLineOffset() ); // did nothing
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    assertNull( grid.scrollLineIntoView( 9000 ) );
    assertTrue( rap.isElementAvailable( grid.byId().descendants().widget( GRID_CELL, "person 110" ) ) );
    grid.getLineCount(); // one more (empty) line that can be scrolled, can be trouble
    XPath ada = grid.scrollLineIntoView( 0 );
    assertEquals( grid.getRowByCell( "Ada" ), ada );
    assertEquals( 0, grid.getLineOffset() );
    XPath person76 = grid.scrollLineIntoView( 77 );
    assertEquals( grid.getRowByCell( "person 76" ), person76 );
    XPath person50 = grid.scrollLineIntoView( 50 );
    assertEquals( grid.getRowByCell( "person 49" ), person50 );
  }

  @Test
  public void testScrollLineIntoView_FixedColumns() throws Exception {
    goToSplitTable();
    AriaGridBot grid = gridWithText( "Item0-0" );
    assertEquals( 0, grid.getLineOffset() );
    assertEquals( grid.getRowByCell( "Item1-0" ), grid.scrollLineIntoView( 1 ) );
    assertEquals( 0, grid.getLineOffset() ); // did nothing
    assertNull( grid.scrollLineIntoView( 9000 ) );
    assertTrue( rap.isElementAvailable( grid.byId().descendants().widget( GRID_CELL, "Item114-0" ) ) );
    grid.getLineCount(); // one more (empty) line that can be scrolled, can be trouble
    XPath ada = grid.scrollLineIntoView( 0 );
    assertEquals( grid.getRowByCell( "Item0-0" ), ada );
    assertEquals( 0, grid.getLineOffset() );
    XPath item77 = grid.scrollLineIntoView( 77 );
    assertEquals( grid.getRowByCell( "Item77-0" ), item77 );
    XPath item50 = grid.scrollLineIntoView( 50 );
    assertEquals( grid.getRowByCell( "Item50-0" ), item50 );
  }

  @Test
  public void testScrollItemIntoView() throws Exception {
    goToVirtualTable();
    AriaGridBot grid = gridWithText( "First Name" );
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    assertEquals( 0, grid.getLineOffset() );
    grid.scrollCellIntoView( "Ada" );
    assertEquals( 0, grid.getLineOffset() ); // did nothing
    rap.click( grid.scrollCellIntoView( "person 110" ) );
    rap.click( grid.scrollCellIntoView( "Ada" ) );
    rap.click( grid.scrollCellIntoView( "person 76" ) );
    rap.click( grid.scrollCellIntoView( "person 49" ) );
    try {
      grid.scrollCellIntoView( "person foo" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testScrollItemIntoView_FixedColumns() throws Exception {
    goToSplitTable();
    AriaGridBot grid = gridWithText( "Item0-0" );
    assertEquals( 0, grid.getLineOffset() );
    grid.scrollCellIntoView( "Item0-0" );
    assertEquals( 0, grid.getLineOffset() ); // did nothing
    rap.click( grid.scrollCellIntoView( "Item114-0" ) );
    rap.click( grid.scrollCellIntoView( "Item0-2" ) );
    rap.click( grid.scrollCellIntoView( "Item77-3" ) );
    rap.click( grid.scrollCellIntoView( "Item49-1" ) );
    try {
      grid.scrollCellIntoView( "Item49-100" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testGetRowByCell() throws Exception {
    goToVirtualTable();
    AriaGridBot grid = gridWithText( "First Name" );
    assertEquals( grid.getRowByCell( "Ada" ), grid.getRowByCell( "Last Name", "Lovelace" ) );
    assertEquals( grid.getRowByCell( "Turing" ), grid.getRowByCell( "Married", "no" ) );
    assertNull( grid.getRowByCell( "First Name", "Lovelace" ) ); // does not exist
    try {
      grid.getRowByCell( "First", "Lovelace" ); // invalid column
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
    try {
      grid.getRowByCell( "First Name", "John" ); // multiple results
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testGetCellContent() throws Exception {
    goToVirtualTable();
    AriaGridBot grid = gridWithText( "First Name" );
    assertEquals( "Ada", grid.getCellContent( grid.getRowByCell( "Lovelace" ), "First Name" ) );
    assertEquals( "1903", grid.getCellContent( grid.getRowAtLine( 1 ), "Born" ) );
    try {
      grid.getCellContent( grid.getRowAtLine( 1 ), "Bornx" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
    try {
      grid.getCellContent( grid.getRowByCell( "foo" ), "Born" );
      fail();
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      grid.getCellContent( grid.getRowByCell( "John" ), "Born" );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  @Test
  public void testGetCellContent_FixedColumns() throws Exception {
    goToSplitTable();
    AriaGridBot grid = gridWithText( "Item0-0" );
    XPath row0 = grid.getRowByCell( "Item0-0" );
    XPath row1 = grid.getRowByCell( "Item1-0" );
    assertEquals( "Item0-0", grid.getCellContent( row0, "Col 0" ) );
    assertEquals( "Item1-4", grid.getCellContent( row1, "Col 4" ) );
  }

  @Test
  public void testTableWithFlowTo() throws Exception {
    goToVirtualTable();
    rap.click( any().widget( BUTTON, "Add 100 Items" ) );
    rap.waitForServer();
    AriaGridBot grid = gridWithText( "First Name" );
    int gridLineCount = grid.getLineCount();
    XPath lastItem = grid.scrollLineIntoView( gridLineCount - 1 );
    assertEquals( "new 110", grid.getCellContent( lastItem, "First Name" ) );
  }

  private AriaGridBot gridWithText( String elementText ) {
    XPath path = any().widget( GRID, with().textContent( elementText ) );
    rap.waitForAppear( path );
    return new AriaGridBot( rap, path );
  }


}
