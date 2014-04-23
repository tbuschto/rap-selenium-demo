package org.eclipse.rap.selenium;

import static org.eclipse.rap.selenium.AriaRoles.*;
import static org.eclipse.rap.selenium.xpath.Predicate.with;
import static org.eclipse.rap.selenium.xpath.XPath.any;
import static org.eclipse.rap.selenium.xpath.XPath.byId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.rap.rwt.jstest.TestContribution;
import org.eclipse.rap.selenium.xpath.AbstractPath;
import org.eclipse.rap.selenium.xpath.XPath;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.thoughtworks.selenium.Selenium;

public class RapBot {

  private static final String TEST_UTIL_JS = "/org/eclipse/rwt/test/fixture/TestUtil.js";
  private static final String RAP_BOT_JS = "/org/eclipse/rap/selenium/RapBot.js";
  private final WebDriver driver;
  private final Selenium selenium;
  private final String TEST_UTIL_JS_CONTENT;
  private final String RAP_BOT_JS_CONTENT;
  private final String NAMESPACE_JS = "var namespace = function( value ) {"
                                      + "rwt.qx.Class.createNamespace( value, {} );"
                                      + "};\n";
  private static final String CHARSET = "UTF-8";

  public RapBot( WebDriver driver, Selenium selenium ) {
    this.driver = driver;
    this.selenium = selenium;
    TEST_UTIL_JS_CONTENT = readTestUtil();
    RAP_BOT_JS_CONTENT = readRapBotJs();
  }


  /////////////////////
  // Controlling the UI

  public void loadApplication( String url ) {
    loadApplication( url, true );
  }

  public void loadApplication( String url, boolean ariaEnabled ) {
    selenium.open( url );
    selenium.waitForPageToLoad( "10000" );
    patchRAP();
    if( ariaEnabled ) {
      waitForAppear( any().widget( "application" ) );
    } else {
      waitForServer();
    }
  }

  // NOTE: Even if the element is rendered a click may not be registered if it
  // is not in view
  public void click( AbstractPath<?> xpath ) {
    click( xpath.toString() );
  }

  public void click( String xpath ) {
    checkElementCount( xpath );
    // selenium.click is unreliable
//    try {
//      selenium.mouseOver( xpath );
//      try {
//        selenium.mouseDown( xpath );
//        selenium.mouseUp( xpath );
//        selenium.click( xpath );
//        selenium.mouseOut( xpath );
//      } catch( Exception exception ) {
//        // element probably disappeared at some point
//      }
//    } catch( Exception exception ) {
//      // This should never happen, but sometimes selenium does not find elements but driver does??
//      // use JS instead?
//      driver.findElement( By.xpath( xpath ) ).click();
//    }
    driver.findElement( By.xpath( xpath ) ).click();
  }


  public void scrollWheel( AbstractPath<?> xpath, int delta ) {
    scrollWheel( xpath.toString(), delta );
  }

  /**
   * Fires mousewheel events that may be processed by javascript. It does not
   * cause natively scrollable elements to scroll, but effects Tree, Table,
   * Nebula Grid, Scale, Spinner, Slider, DateTime and Combo.
   *
   * @param xpath
   * @param delta
   */
  public void scrollWheel( String xpath, int delta ) {
    checkElementCount( xpath );
    selenium.runScript( "RapBot.scrollWheel( \"" + xpath + "\", " + delta + " );" );
  }

  /**
   * @param xpath
   * @param key a single character, or any value from {@link KeyIdentifier}
   */
  public void press( AbstractPath<?> xpath, String key ) {
    press( xpath.toString(), key );
  }

  // seleniums keyPress method somehow messes up the keycode, therefore this
  // can't be used to
  // control most RAP widgets. Also, selenium.focus does not always work on RAP
  // widgets, possibly
  // needs to be applied to specific elements.
  // public void press( String xpath, String key ) {
  // checkElementCount( xpath );
  // selenium.keyDown( xpath, key );
  // try {
  // selenium.keyPress( xpath, key );
  // selenium.keyUp( xpath, key );
  // } catch( Exception ex ) {
  // // ignored
  // }
  // }
  // Requires TestUtil.js to be loaded, either by application or by selenium
  public void press( String xpath, String key ) {
    checkElementCount( xpath );
    String script = "(function(){var el =  "
                    + xpathToJs( xpath )
                    + ";var widget = rwt.event.EventHandlerUtil.getOriginalTargetObject( el );"
                    + "org.eclipse.rwt.test.fixture.TestUtil.press( widget,\""
                    + key
                    + "\" );"
                    + "}());";
    selenium.runScript( script );
  }

  public void clearInput( AbstractPath<?> xpath ) {
    clearInput( xpath.toString() );
  }

  public void clearInput( String xpath ) {
    checkElementCount( xpath );
    WebElement inputElement = driver.findElement( By.xpath( xpath ) );
    inputElement.clear();
  }

  public void input( AbstractPath<?> xpath, String text ) {
    input( xpath.toString(), text );
  }

  public void input( String xpath, String text ) {
    checkElementCount( xpath );
    WebElement inputElement = driver.findElement( By.xpath( xpath ) );
    // inputElement.click(); <- Chrome refuses to click here due to the
    // underlying HTML element
    // See
    // http://code.google.com/p/chromedriver/issues/detail?id=149&q=input&colspec=ID%20Status%20Pri%20Owner%20Summary
    // focus or selenium click won't work either (use JS!)
    inputElement.sendKeys( text );
  }

  /**
   * The first line has index zero. Returns path to row rendering the given line
   * or null if it doesn't exist
   */
  public XPath scrollGridLineIntoView( AbstractPath<?> grid, int lineIndex ) {
    return asXPath( scrollGridLineIntoView( grid.toString(), lineIndex ) );
  }

  public String scrollGridLineIntoView( String grid, int lineIndex ) {
    checkElementCount( grid );
    String scrollbar = grid + vScrollBar();
    if( getXPathCount( scrollbar ) == 0 ) {
      return getGridRowAtPosition( grid, lineIndex );
    } else {
      int offset = getGridLineOffset( grid );
      int rowCount = getVisibleGridLines( grid );
      int maxOffset = getMaxGridLineOffset( grid );
      if( lineIndex >= offset && lineIndex < offset + rowCount ) {
        return getGridRowAtLine( grid, lineIndex );
      }
      // we target the previous line because scrollWheel is not precise
      // (scrolling 2 lines for a
      // grid) AND because the pixel offset has to be exactly 0 for line 0 to
      // show
      int desiredOffset = Math.min( lineIndex - 1, maxOffset );
      int lineDelta = desiredOffset - offset;
      String clientarea = getAriaControls( scrollbar );
      int delta = ( int )Math.floor( -1 * lineDelta / 2F );
      scrollWheel( clientarea, delta );
      return getGridRowAtLine( grid, lineIndex );
    }
  }

  public String scrollGridItemIntoView( String grid, String cellContent ) {
    return scrollGridItemIntoView( asXPath( grid ), cellContent ).toString();
  }

  public XPath scrollGridItemIntoView( AbstractPath<?> grid, String cellContent ) {
    XPath needle = rowWithCellText( grid, cellContent );
    scrollGridItemIntoView( grid, needle );
    return needle;
  }

  public String scrollGridItemIntoView( String grid, String columnName, String cellContent ) {
    return scrollGridItemIntoView( asXPath( grid ), columnName, cellContent ).toString();
  }

  public XPath scrollGridItemIntoView( AbstractPath<?> grid, String columnName, String cellContent ) {
    String columnId = getColumnId( grid, columnName );
    XPath needle = rowWithCellText( grid, columnId, cellContent );
    scrollGridItemIntoView( grid, needle );
    return needle;
  }

  private void scrollGridItemIntoView( AbstractPath<?> grid, XPath needle ) {
    if( isElementAvailable( needle ) ) {
      return;
    }
    int startOffset = getGridLineOffset( grid );
    int currentOffset = getGridLineOffset( grid );
    int max = getMaxGridLineOffset( grid );
    while( !isElementAvailable( needle ) && currentOffset < max ) {
      scrollGridPageDown( grid );
      currentOffset = getGridLineOffset( grid );
      waitForItems( grid );
      if( currentOffset == startOffset ) {
        break;
      }
    }
    if( isElementAvailable( needle ) ) {
      return;
    }
    scrollGridLineIntoView( grid, 0 );
    waitForItems( grid );
    while( !isElementAvailable( needle ) && getGridLineOffset( grid ) < startOffset ) {
      scrollGridPageDown( grid );
      waitForItems( grid );
    }
    if( !isElementAvailable( needle ) ) {
      throw new IllegalStateException( "Item not found in grid" );
    }
  }

  public void scrollGridPageDown( AbstractPath<?> grid ) {
    scrollGridPageDown( grid.toString() );
  }

  public void scrollGridPageDown( String grid ) {
    scrollGridByPage( grid, -1 );
  }

  public void scrollGridPageUp( AbstractPath<?> grid ) {
    scrollGridPageUp( grid.toString() );
  }

  public void scrollGridPageUp( String grid ) {
    scrollGridByPage( grid, 1 );
  }

  // //////////////
  // Waiting for UI

  /**
   * Waits until a pending request has returned and processed. Will not work
   * reliably for ScrollBar Selection (scroll) events, Modify/Verify events of
   * Text, and Selection events of Spinner. For these an additional ~1000ms
   * should must waited before calling this.
   */
  public void waitForServer() {
    // There may be a short delay between the user input and the widget sending
    // the request.
    // In most cases the request is hard-coded to 60ms, though the JS timer
    // isn't very precise.
    // In a few cases like Modify event the delay is much longer and this method
    // won't suffice.
    try {
      Thread.sleep( 120 );
    } catch( InterruptedException e ) {
      e.printStackTrace();
    }
    new WebDriverWait( driver, 20 ).until( new ExpectedCondition<Boolean>() {

      @Override
      public Boolean apply( WebDriver driverObject ) {
        return !isRequestPending();
      }
    } );
  }

  public void waitForAppear( final XPath xpath ) {
    waitForAppear( xpath.toString() );
  }

  public void waitForAppear( final String locator ) {
    try {
      if( !isElementAvailable( locator ) ) {
        new WebDriverWait( driver, 10 ).until( new ExpectedCondition<Boolean>() {

          @Override
          public Boolean apply( WebDriver driverObject ) {
            return isElementAvailable( locator );
          }
        } );
      }
    } catch( TimeoutException e ) {
      throw new IllegalStateException( "Element \"" + locator + "\" still not present after 10s" );
    }
  }

  public void waitForDisappear( final XPath xpath ) {
    waitForDisappear( xpath.toString() );
  }

  public void waitForDisappear( final String locator ) {
    try {
      if( isElementAvailable( locator ) ) {
        new WebDriverWait( driver, 10 ).until( new ExpectedCondition<Boolean>() {

          @Override
          public Boolean apply( WebDriver driverObject ) {
            return !isElementAvailable( locator );
          }
        } );
      }
    } catch( TimeoutException e ) {
      throw new IllegalStateException( "Element \"" + locator + "\" still present after 10s" );
    }
  }

  /**
   * Waits for all visible items of a VIRTUAL Tree, Table or NebulaGrid to
   * cache.
   *
   * @param gridPath
   */
  public void waitForItems( AbstractPath<?> grid ) {
    waitForItems( grid.toString()  );
  }

  public void waitForItems( String gridPath ) {
    checkElementCount( gridPath );
    waitForDisappear( gridPath + any().widget( "row", "busy", "true" ).toString() );
    checkElementCount( gridPath );
  }

  // ///////////////
  // Reading from UI

  public boolean isRequestPending() {
    if( driver instanceof JavascriptExecutor ) {
      String script = "return RapBot.busy;";
      JavascriptExecutor executer = ( JavascriptExecutor )driver;
      return ( ( Boolean )executer.executeScript( script, ( Object )null ) ).booleanValue();
    } else {
      throw new IllegalStateException( "This driver does not support JavaScript execution." );
    }
  }

  public boolean isElementAvailable( AbstractPath<?> xpath ) {
    return isElementAvailable( xpath.toString() );
  }

  public boolean isElementAvailable( String xpath ) {
//    int count = selenium.getXpathCount( xpath ).intValue();
//    // NOTE [tb] : I do not know why, but an element can be "not present" and
//    // have an count == 1, or be "present" but not found by isVisible...
//    try {
//      return count > 0 && selenium.isElementPresent( xpath ) && selenium.isVisible( xpath );
//    } catch( SeleniumException e ) {
//      return false;
//    }
    try{
      return driver.findElements( By.xpath( xpath ) ).size() > 0;
    } catch( NoSuchElementException e ) {
      return false;
    }
  }

  public int getXPathCount( String xpath ) {
    return driver.findElements( By.xpath( xpath ) ).size();
  }

  public int getXPathCount( AbstractPath<?> xpath ) {
    return getXPathCount( xpath.toString() );
  }

  public String getId( AbstractPath<?> xpath ) {
    return getId( xpath.toString() );
  }

  public String getId( String xpath ) {
    checkElementCount( xpath );
    return getAttribute( xpath, "id" );
  }

  public String getAttribute( AbstractPath<?> xpath, String attribute ) {
    return getAttribute( xpath.toString(), attribute );
  }

  public String getAttribute( String xpath, String attribute ) {
    checkElementCount( xpath );
    return driver.findElement( By.xpath( xpath ) ).getAttribute( attribute );
  }

  public String getText( AbstractPath<?> xpath ) {
    return getText( xpath.toString() );
  }

  public String getText( String xpath ) {
    checkElementCount( xpath );
    return driver.findElement( By.xpath( xpath ) ).getText();
  }

  public int getGridLineOffset( AbstractPath<?> grid ) {
    return getGridLineOffset( grid.toString() );
  }

  public int getGridLineOffset( String grid ) {
    checkElementCount( grid );
    String scrollbar = grid + vScrollBar();
    if( getXPathCount( scrollbar ) == 0 ) {
      return 0;
    } else {
      // this mirrors the grid implementation (Grid._updateTopItemIndex)
      int offset = Integer.parseInt( getAttribute( scrollbar, "aria-valuenow" ) ) - 1;
      return offset < 0
                       ? 0
                       : ( offset / getGridLineHeight( grid ) ) + 1;
    }
  }

  public int getMaxGridLineOffset( AbstractPath<?> grid ) {
    return getMaxGridLineOffset( grid.toString() );
  }

  public int getMaxGridLineOffset( String grid ) {
    String scrollbar = grid + vScrollBar();
    if( getXPathCount( scrollbar ) == 0 ) {
      return 0;
    } else {
      int max = Integer.parseInt( getAttribute( scrollbar, "aria-valuemax" ) );
      return max / getGridLineHeight( grid );
    }
  }

  public int getGridLineCount( AbstractPath<?> grid ) {
    return getGridLineCount( grid.toString() );
  }

  public int getGridLineCount( String grid ) {
    checkElementCount( grid );
    String scrollbar = grid + vScrollBar();
    if( getXPathCount( scrollbar ) == 0 ) {
      return getVisibleGridLines( grid );
    } else {
      int scrollHeight = Integer.parseInt( getAttribute( scrollbar, "aria-valuemax" ) )
                         + selenium.getElementHeight( scrollbar ).intValue();
      // NOTE: for nebula grid footer would have to be subtracted
      return ( scrollHeight / getGridLineHeight( grid ) );
    }
  }

  public int getVisibleGridLines( AbstractPath<?> grid ) {
    return getXPathCount( getClientArea( grid ).children().widget( ROW ) );
  }

  public int getVisibleGridLines( String grid ) {
    return getVisibleGridLines( asXPath( grid ) );
  }

  /**
   * Returns the absolute path to the client area element of the given widget.
   * If there are multiple (fixed columns), the first is returned;
   *
   * @param grid
   * @return
   */
  public XPath getClientArea( AbstractPath<?> grid ) {
    // don't use "widget" to include invisible scrollbar (as opposed to vScrollBar())
    XPath scrollbar = asXPath( grid ).descendants().element(
      with().attr( "role", SCROLL_BAR ).aria( "orientation", "vertical" )
    );
    return getAriaControls( scrollbar );
  }

  public String getClientArea( String grid ) {
    return getClientArea( asXPath( grid ) ).toString();
  }

  /**
   * Returns the path to the row with the given lineIndex. The line has to be
   * visible on screen. If the grid has fixed columns, the leftside row is
   * returned.
   *
   * @param grid
   * @param rowIndex
   * @return
   */
  public XPath getGridRowAtLine( AbstractPath<?> grid, int lineIndex ) {
    return getGridRowAtPosition( grid, lineIndex - getGridLineOffset( grid ) );
  }

  public String getGridRowAtLine( String grid, int lineIndex ) {
    return getGridRowAtPosition( grid, lineIndex - getGridLineOffset( grid ) );
  }

  /**
   * Returns the path to the row with the given position relative to the first
   * visible row, or null if the index is out of bounds. If fixed columns are
   * used, the leftside row is returned.
   *
   * @param grid
   * @param position
   * @return
   */
  public XPath getGridRowAtPosition( AbstractPath<?> grid, int position ) {
    return getGridRowAtPosition( grid, position );
  }

  public String getGridRowAtPosition( String grid, int position ) {
    checkElementCount( grid );
    waitForItems( grid );
    String rowId = getFlowTo( grid );
    for( int i = 0; i < position && rowId != null; i++ ) {
      rowId = getAttribute( byId( rowId ).toString(), "aria-flowto" );
      if( rowId != null && rowId.endsWith( "_1" ) ) {
        rowId = getAttribute( byId( rowId ).toString(), "aria-flowto" );
      }
    }
    return rowId != null ? byId( rowId ).toString() : null;
  }

  public String getGridRowByCell( String grid, String columnName, String cellContent ) {
    String result = getGridRowByCell( grid.toString(), columnName, cellContent );
    return result != null ? result.toString() : null;
  }

  /**
   * Returns the path to the row with the described cell. Returns null if no
   * such cell is visible on screen. Throws {@link IllegalStateException} if the
   * column does not exist or there are multiple visible rows with that cell.
   */
  public XPath getGridRowByCell( AbstractPath<?> grid, String columnName, String cellContent ) {
    String columnId = getColumnId( grid, columnName );
    XPath row = rowWithCellText( grid, columnId, cellContent );
    int count = getXPathCount( row );
    if( count == 1 ) {
      return byId( getId( row ) );
    } else if( count == 0 ) {
      return null;
    } else {
      throw new IllegalStateException( "There are multiple rows matching " + row );
    }
  }

  public String getGridRowByCell( String grid, String cellContent ) {
    String result = getGridRowByCell( grid.toString(), cellContent );
    return result != null ? result.toString() : null;
  }

  /**
   * Returns the path to the row with the described cell. Returns null if no
   * such cell is visible on screen. Throws {@link IllegalStateException} if the
   * column does not exist or there are multiple visible rows with that cell.
   */
  public XPath getGridRowByCell( AbstractPath<?> grid, String cellContent ) {
    XPath row = rowWithCellText( grid, cellContent );
    int count = getXPathCount( row );
    if( count == 1 ) {
      return byId( getId( row ) );
    } else if( count == 0 ) {
      return null;
    } else {
      throw new IllegalStateException( "There are multiple rows matching " + row );
    }
  }

  /**
   * @param grid
   * @param rowPath path to row relative to grid. Expects left-side row for fixed column grids
   * @param columnName
   * @return
   */
  public String getGridCellContent( AbstractPath<?> grid, XPath rowPath, String columnName ) {
    return getGridCellContent( grid.toString(), rowPath.toString(), columnName );
  }

  public String getGridCellContent( String grid, String rowPath, String columnName ) {
    checkElementCount( grid + rowPath );
    String columnId = getColumnId( grid, columnName );
    String cell = grid + rowPath + cellDescribedBy( columnId );
    if( getXPathCount( cell ) == 0 && getFlowTo( rowPath ).endsWith( "_1" ) ) {
      cell = byId( getFlowTo( rowPath ) ).toString() + cellDescribedBy( columnId );
    }
    checkElementCount( cell );
    return selenium.getText( cell );
  }

  public String getFlowTo( AbstractPath<?> xpath ) {
    return getFlowTo( xpath.toString() );
  }

  public String getFlowTo( String xpath ) {
    return getAttribute( xpath, "aria-flowto" );
  }

  public String getColumnId( String grid, String columnName ) {
    return getColumnId( asXPath( grid ), columnName );
  }

  public String getColumnId( AbstractPath<?> grid, String columnName ) {
    return getId( asXPath( grid ).descendants().widget( COLUMN_HEADER, with().textContent( columnName ) ) );
  }

  public int getGridLineHeight( String grid ) {
    return getGridLineHeight( asXPath( grid ) );
  }

  public int getGridLineHeight( AbstractPath<?> grid ) {
    return selenium.getElementHeight( rowOf( grid ).toString() ).intValue();
  }

  /////////
  // Helper

  private void scrollGridByPage( String grid, int direction ) {
    checkElementCount( grid );
    String scrollbar = grid + vScrollBar();
    if( getXPathCount( scrollbar ) == 0 ) {
      return;
    } else {
      String clientarea = getAriaControls( scrollbar );
      int rows = getVisibleGridLines( grid );
      int delta = ( int )Math.floor( rows / 2F ) * direction; // one wheel
                                                              // "click" scrolls
                                                              // by 2 items
      scrollWheel( clientarea, delta );
    }
  }

  private String getAriaControls( String xpath ) {
    return getAriaControls( asXPath( xpath ) ).toString();
  }

  private XPath getAriaControls( AbstractPath<?> xpath ) {
    String controls = getAttribute( xpath, "aria-controls" );
    return byId( controls.split( "\\s" )[ 0 ] );
  }

  private String xpathToJs( String xpath ) {
    return "document.evaluate( \""
           + xpath
           + "\", document, null, XPathResult.ANY_TYPE, null ).iterateNext()";
  }

  private void checkElementCount( String xpath ) {
    int elementCount = getXPathCount( xpath );
    if( elementCount != 1 ) {
      throw new IllegalStateException( elementCount + " Elements for " + xpath );
    }
  }

  private void patchRAP() {
    selenium.runScript( RAP_BOT_JS_CONTENT );
    // Currently required for emulating key events on internal RAP event handler
    // level:
    selenium.runScript( NAMESPACE_JS + TEST_UTIL_JS_CONTENT );
  }

  public static XPath rowOf( AbstractPath<?> grid ) {
    // excludes header:
    return asXPath( grid ).descendants().widget( ROW, with().content( any().widget( "gridcell" )  ) );
  }

  private static String readTestUtil() {
    return readFile( TEST_UTIL_JS );
  }

  private static String readRapBotJs() {
    return readFile( RAP_BOT_JS );
  }

  private static String readFile( String url ) {
    String result;
    try {
      InputStream stream = TestContribution.class.getResourceAsStream( url );
      if( stream == null ) {
        throw new IllegalArgumentException( "Resource not found: " + url );
      }
      try {
        BufferedReader reader = new BufferedReader( new InputStreamReader( stream, CHARSET ) );
        result = readLines( reader );
      } finally {
        stream.close();
      }
    } catch( IOException ex ) {
      throw new RuntimeException( ex );
    }
    return result;
  }

  private static String readLines( BufferedReader reader ) throws IOException {
    StringBuilder builder = new StringBuilder();
    String line = reader.readLine();
    while( line != null ) {
      builder.append( line );
      builder.append( '\n' );
      line = reader.readLine();
    }
    return builder.toString();
  }

  private static String vScrollBar() {
    return any().widget( "scrollbar", "orientation", "vertical" ).toString();
  }

  private static XPath rowWithCellText( AbstractPath<?> grid, String cellContent ) {
    return asXPath( grid ).append( any().widget( ROW, with().content( cellWithText( cellContent ) ) ) );
  }

  private static XPath rowWithCellText( AbstractPath<?> grid, String columnId, String cellContent ) {
    return asXPath( grid ).append( any().widget( ROW, with().content( cellWithText( columnId, cellContent ) ) ) );
  }

  private static XPath cellWithText( String content ) {
    return any().widget( GRID_CELL, content );
  }

  private static XPath cellWithText( String columnId, String content ) {
    return any().widget( GRID_CELL, with().text( content ).attr( "describedby", columnId ) );
  }

  private static String cellDescribedBy( String columnId ) {
    return any().widget( GRID_CELL ).toString() + "[@aria-describedby='" + columnId + "']";
  }

  private static XPath asXPath( AbstractPath<?> path ) {
    return XPath.createXPath( path.toString() );
  }

  private static XPath asXPath( String string ) {
    return XPath.createXPath( string );
  }

}
