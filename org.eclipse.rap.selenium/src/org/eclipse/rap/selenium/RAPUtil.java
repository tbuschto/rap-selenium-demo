package org.eclipse.rap.selenium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.rap.rwt.jstest.TestContribution;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;


public class RAPUtil {

  private static final String TEST_UTIL_JS = "/org/eclipse/rwt/test/fixture/TestUtil.js";
  private static final String RAP_UTIL_JS = "/org/eclipse/rap/selenium/RAPUtil.js";
  private final WebDriver driver;
  private final Selenium selenium;
  private final String TEST_UTIL_JS_CONTENT;
  private final String RAP_UTIL_JS_CONTENT;
  private final String NAMESPACE_JS =   "var namespace = function( value ) {"
                                      + "rwt.qx.Class.createNamespace( value, {} );"
                                      + "};\n";
  private static final String CHARSET = "UTF-8";
  private boolean ariaEnabled;


  public RAPUtil( WebDriver driver, Selenium selenium ) {
    this.driver = driver;
    this.selenium = selenium;
    TEST_UTIL_JS_CONTENT = readTestUtil();
    RAP_UTIL_JS_CONTENT = readRAPUtil();
  }

  public static String firstResult( String xpath ) {
    // regarding syntax see
    // http://blog.neustar.biz/web-performance/effectively-using-seleniums-getxpathcount-function/
    // There may be a bug where this fails in some combinations with FF, eg (...)[1]//*[...]
    return "(" + xpath + ")[1]";
  }

  public static String atPosition( int index ) {
    return "[" + index + "]";
  }

  public static String atFirstPosition() {
    return atPosition( 0 );
  }

  public static String atLastPosition( ) {
    return "[last()]";
  }

  public static String atLastPosition( int i ) {
    return "[last()" + i + "]";
  }

  public static String containing( String xpath ) {
    String relativePath = xpath;
    if( relativePath.startsWith( "//*" ) ) {
      relativePath = relativePath.substring( 3 ); // find a (much) better solution
    }
    return "[count(descendant::*" + relativePath + ")>0]";
  }

  public static String nextSibling() {
    return "/following-sibling::*" ;
  }

  public static String nextSibling( String xpath ) {
    String relativePath = xpath;
    if( relativePath.startsWith( "//*" ) ) {
      relativePath = relativePath.substring( 3 ); // find a (much) better solution
    }
    return "/following-sibling::*" + relativePath;
  }

  public static String byTestId( String testId ) {
    return "//*[@testId='" + testId + "']";
  }

  public static String byId( String id ) {
    return "//*[@id='" + id + "']";
  }

  public static String byText( String content ) {
    return "//*[not(@aria-hidden='true') and text()='" + content + "']";
  }

  public static String label( String content ) {
    return byText( content ) + "/parent::*[not(@role)]"; // labels have no role
  }

  public static String button( String content ) {
    return byAria( "button" ) + containing( byText( content ) );
  }

  public static String checkbox( String content ) {
    return byAria( "checkbox" ) + containing( byText( content ) );
  }

  public static String byAria( String role ) {
    return "//*[not(@aria-hidden='true') and @role='" + role + "']";
  }

  public static String byAria( String role, String text ) {
    return byAria( role ) + containing( byText( text ) );
  }

  public static String byAria( String role, String state, boolean value ) {
    return "//*[not(@aria-hidden='true') and @role='" + role + "' and @aria-" + state + "='" + value + "']";
  }

  public static String byAria( String role, String property, String value ) {
    return "//*[not(@aria-hidden='true') and @role='" + role + "' and @aria-" + property + "='" + value + "']";
  }

  public static String row() {
    return byAria( "row" ) + containing( byAria( "gridcell" ) ); // excludes header
  }

  public static String vScrollBar() {
    return byAria( "scrollbar", "orientation", "vertical" );
  }

  public static String rowWithCellText( String cellContent ) {
    return byAria( "row" ) + containing( cellWithText( cellContent ) );
  }

  public static String rowWithCellText( String columnId, String cellContent ) {
    return byAria( "row" ) + containing( cellWithText( columnId, cellContent ) );
  }

  public static String cellWithText( String content ) {
    return byAria( "gridcell" ) + "[text()='" + content + "']";
  }

  public static String cellWithText( String columnId, String content ) {
    return byAria( "gridcell" ) + "[text()='" + content + "' and @aria-describedby='" + columnId + "']";
  }

  public static String cellDescribedBy( String columnId ) {
    return byAria( "gridcell" ) + "[@aria-describedby='" + columnId + "']";
  }

  public void loadApplication( String url ) {
    loadApplication( url, true );
  }

  public void loadApplication( String url, boolean ariaEnabled ) {
    this.ariaEnabled = ariaEnabled;
    selenium.open( url );
    selenium.waitForPageToLoad( "10000" );
    patchRAP();
    if( ariaEnabled ) {
      waitForAppear( byAria( "application" ) );
    } else {
      waitForServer();
    }
  }

  public String getId( String xpath ) {
    checkElementCount( xpath );
    return getAttribute( xpath, "id" );
  }

  public String getAttribute( String xpath, String attribute ) {
    checkElementCount( xpath );
    return driver.findElement( By.xpath( xpath ) ).getAttribute( attribute );
  }

  public int getGridLineOffset( String grid ) {
    checkElementCount( grid );
    String scrollbar = grid + vScrollBar();
    if( selenium.getXpathCount( scrollbar ).intValue() == 0 ) {
      return 0;
    } else {
      // this mirrors the grid implementation (Grid._updateTopItemIndex)
      int offset = Integer.parseInt( getAttribute( scrollbar, "aria-valuenow" ) ) - 1;
      return offset < 0 ? 0 : ( offset / getGridLineHeight( grid ) ) + 1;
    }
  }

  public int getMaxGridLineOffset( String grid ) {
    String scrollbar = grid + vScrollBar();
    if( selenium.getXpathCount( scrollbar ).intValue() == 0 ) {
      return 0;
    } else {
      int max = Integer.parseInt( getAttribute( scrollbar, "aria-valuemax" ) );
      return max / getGridLineHeight( grid );
    }
  }

  public int getGridLineCount( String grid ) {
    checkElementCount( grid );
    String scrollbar = grid + vScrollBar();
    if( selenium.getXpathCount( scrollbar ).intValue() == 0 ) {
      return getVisibleGridLines( grid );
    } else {
      int scrollHeight =   Integer.parseInt( getAttribute( scrollbar, "aria-valuemax" ) )
                         + selenium.getElementHeight( scrollbar ).intValue();
      // NOTE: for nebula grid footer would have to be subtracted
      return ( scrollHeight / getGridLineHeight( grid ) );
    }
  }

  public int getVisibleGridLines( String grid ) {
    return selenium.getXpathCount( getClientArea( grid ) + row() ).intValue();
  }

  /**
   * Returns the absolute path to the client area element of the given widget.
   * If there are multiple (fixed columns), the first is returned;
   *
   * @param grid
   * @return
   */
  public String getClientArea( String grid ) {
    // include invisible scrollbar (as opposed to vScrollBar())
    String scrollbar = grid + "//*[@role='scrollbar' and @aria-orientation='vertical']";
    return getAriaControls( scrollbar );
  }

  /**
   * The first line has index zero.
   * Returns path to row rendering the given line or null if it doesn't exist
   */
  public String scrollGridLineIntoView( String grid, int lineIndex ) {
    checkElementCount( grid );
    String scrollbar = grid + vScrollBar();
    if( selenium.getXpathCount( scrollbar ).intValue() == 0 ) {
      return getGridRowAtPosition( grid, lineIndex );
    } else {
      int offset = getGridLineOffset( grid );
      int rowCount = getVisibleGridLines( grid );
      int maxOffset = getMaxGridLineOffset( grid );
      if( lineIndex >= offset && lineIndex < offset + rowCount ) {
        return getGridRowAtLine( grid, lineIndex );
      }
      // we target the previous line because scrollWheel is not precise (scrolling 2 lines for a
      // grid) AND because the pixel offset has to be exactly 0 for line 0 to show
      int desiredOffset = Math.min( lineIndex - 1, maxOffset );
      int lineDelta = desiredOffset - offset;
      String clientarea = getAriaControls( scrollbar );
      int delta = ( int )Math.floor( -1 * lineDelta / 2F );
      scrollWheel( clientarea, delta );
      return getGridRowAtLine( grid, lineIndex );
    }
  }

  /**
   * Returns the path to the row with the given lineIndex. The line has to be visible
   * on screen. If the grid has fixed columns, the leftside row is returned.
   * @param grid
   * @param rowIndex
   * @return
   */
  public String getGridRowAtLine( String grid, int lineIndex ) {
    return getGridRowAtPosition( grid, lineIndex - getGridLineOffset( grid ) );
  }

  /**
   * Returns the path to the row with the given position relative to the first visible row,
   * or null if the index is out of bounds. If fixed columns are used, the leftside row is returned.
   *
   * @param grid
   * @param position
   * @return
   */
  public String getGridRowAtPosition( String grid, int position ) {
    checkElementCount( grid );
    waitForItems( grid );
    String rowId = getFlowTo( grid );
    for( int i = 0; i < position && rowId != null; i++ ) {
      rowId = getAttribute( byId( rowId ), "aria-flowto" );
      if( rowId != null && rowId.endsWith( "_1" ) ) {
        rowId = getAttribute( byId( rowId ), "aria-flowto" );
      }
    }
    return rowId != null ? byId( rowId ) : null;
  }

  /**
   * Returns the path to the row with the described cell. Returns null if no such cell is visible on
   * screen. Throws {@link IllegalStateException} if the column does not exist or there
   * are multiple rows with that cell.
   */
  public String getGridRowByCell( String grid, String columnName, String cellContent ) {
    String columnId = getColumnId( grid, columnName );
    String row = grid + rowWithCellText( columnId, cellContent );
    int count = selenium.getXpathCount( row ).intValue();
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
   * @param rowPath path to row relative to grid. Expects leftside row for fixed column grids
   * @param columnName
   * @return
   */
  public String getGridCellContent( String grid, String rowPath, String columnName ) {
    checkElementCount( grid + rowPath );
    String columnId = getColumnId( grid, columnName );
    String cell = grid + rowPath + cellDescribedBy( columnId );
    if(    selenium.getXpathCount( cell ).intValue() == 0
        && getFlowTo( rowPath ).endsWith( "_1" ) ) {
      cell = byId( getFlowTo( rowPath ) ) + cellDescribedBy( columnId );
    }
    checkElementCount( cell );
    return selenium.getText( cell );
  }

  public String getFlowTo( String xpath ) {
    return getAttribute( xpath, "aria-flowto" );
  }

  public String getColumnId( String grid, String columnName ) {
    return getId( grid + byAria( "columnheader", columnName ) );
  }


  public void scrollGridItemIntoView( String grid, String columnName, String cellContent ) {
    String columnId = getColumnId( grid, columnName );
    scrollGridItemIntoView( grid, rowWithCellText( columnId, cellContent ) );
  }

  public String scrollGridItemIntoView( String grid, String relativePath ) {
    String needle = grid + relativePath;
    if( isElementAvailable( needle ) ) {
      return needle;
    }
    int startOffset = getGridLineOffset( grid );
    int max = getMaxGridLineOffset( grid );
    while( !isElementAvailable( needle ) && getGridLineOffset( grid ) < max ) {
      scrollGridPageDown( grid );
      waitForItems( grid );
    }
    if( isElementAvailable( needle ) ) {
      return needle;
    }
    scrollGridLineIntoView( grid, 0 );
    waitForItems( grid );
    while( !isElementAvailable( needle ) && getGridLineOffset( grid ) < startOffset ) {
      scrollGridPageDown( grid );
      waitForItems( grid );
    }
    if( !isElementAvailable( needle ) ) {
      throw new IllegalStateException( relativePath + " not found in grid " + grid );
    }
    return needle;
  }

  public void scrollGridPageDown( String grid ) {
    scrollGridByPage( grid, -1 );
  }

  public void scrollGridPageUp( String grid ) {
    scrollGridByPage( grid, 1 );
  }

  private void scrollGridByPage( String grid, int direction ) {
    checkElementCount( grid );
    String scrollbar = grid + vScrollBar();
    if( selenium.getXpathCount( scrollbar ).intValue() == 0 ) {
      return;
    } else {
      String clientarea = getAriaControls( scrollbar );
      int rows = getVisibleGridLines( grid );
      int delta = ( int )Math.floor( rows / 2F ) * direction; // one wheel "click" scrolls by 2 items
      scrollWheel( clientarea, delta );
    }
  }

  private String getAriaControls( String xpath ) {
    String controls = getAttribute( xpath, "aria-controls" );
    return byId( controls.split( "\\s" )[ 0 ] );
  }

  public int getGridLineHeight( String grid ) {
    return selenium.getElementHeight( grid + row() ).intValue();
  }

  // NOTE: even if the element is rendered a click may not be registered if it is not in view
  public void click( String xpath ) {
    checkElementCount( xpath );
    // TODO [tb] : use TestUtil.js also for mouse events?
    selenium.mouseOver( xpath );
    selenium.mouseDown( xpath );
    // The element may disappear on mouse down, e.g. if it is in a pop-up
    try {
      selenium.mouseUp( xpath );
      selenium.click( xpath );
      selenium.mouseOut( xpath );
    } catch( Exception exception ) {
      // ignored
    }
  }

  /**
   * Fires mousewheel events that may be processed by javascript. It does not cause
   * natively scrollable elements to scroll, but effects Tree, Table, Nebula Grid, Scale, Spinner,
   * Slider, DateTime and Combo.
   *
   * @param xpath
   * @param delta
   */
  public void scrollWheel( String xpath, int delta ) {
    checkElementCount( xpath );
    selenium.runScript( "RAPUtil.scrollWheel( \"" + xpath + "\", " + delta + " );" );
  }

// seleniums keyPress method somehow messes up the keycode, therefore this can't be used to
// control most RAP widgets. Also, selenium.focus does not always work on RAP widgets, possibly
// needs to be applied to specific elements.
//public void press( String xpath, String key ) {
//  checkElementCount( xpath );
//  selenium.keyDown( xpath, key );
//  try {
//    selenium.keyPress( xpath, key );
//    selenium.keyUp( xpath, key );
//  } catch( Exception ex ) {
//    // ignored
//  }
//}

  // Requires TestUtil.js to be loaded, either by application or by selenium
  public void press( String xpath, String key ) {
    checkElementCount( xpath );
    String script =    "var el =  " + xpathToJs( xpath )
                     + ";var widget = rwt.event.EventHandlerUtil.getOriginalTargetObject( el );"
                     + "org.eclipse.rwt.test.fixture.TestUtil.press( widget,\""
                     + key
                     + "\" );";
    selenium.runScript( wrapJS( script ) );
  }

  public void clear( String xpath ) {
    checkElementCount( xpath );
    WebElement inputElement = driver.findElement( By.xpath( xpath ) );
    inputElement.clear();
  }

  public void input( String xpath, String text ) {
    checkElementCount( xpath );
    WebElement inputElement = driver.findElement( By.xpath( xpath ) );
    //inputElement.click(); <- Chrome refuses to click here due to the underlying HTML element
    //See http://code.google.com/p/chromedriver/issues/detail?id=149&q=input&colspec=ID%20Status%20Pri%20Owner%20Summary
    // focus or selenium click won't work either (use JS!)
    inputElement.sendKeys( text );
  }

  /**
   * Waits for all visible items of a VIRTUAL Tree, Table or NebulaGrid to cache.
   * @param gridPath
   */
  public void waitForItems( String gridPath ) {
    checkElementCount( gridPath );
    waitForDisappear( gridPath + byAria( "row", "busy", true ) );
    checkElementCount( gridPath );
  }

  public void waitForAppear( final String locator ) {
    try {
      if( !isElementAvailable( locator ) ) {
        new WebDriverWait( driver, 10 ).until( new ExpectedCondition<Boolean>() {
          public Boolean apply( WebDriver driverObject ) {
            return isElementAvailable( locator );
          }
        } );
      }
    } catch( TimeoutException e ) {
      throw new IllegalStateException( "Element \"" + locator + "\" still not present after 10s" );
    }
  }

  public void waitForDisappear( final String locator ) {
    try {
      if( isElementAvailable( locator ) ) {
        new WebDriverWait( driver, 10 ).until( new ExpectedCondition<Boolean>() {
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
   * Waits until a pending request has returned and processed.
   * Will not work reliably for ScrollBar Selection (scroll) events, Modify/Verify events
   * of Text, and Selection events of Spinner. For these an additional ~1000ms should must
   * waited before calling this.
   */
  public void waitForServer() {
    // There may be a short delay between the user input and the widget sending the request.
    // In most cases the request is hardcoded to 60ms, though the JS timer isn't very precise.
    // In a few cases like Modify event the delay is much longer and this method won't suffice.
    try {
      Thread.sleep( 120 );
    } catch( InterruptedException e ) {
      e.printStackTrace();
    }
    new WebDriverWait( driver, 20 ).until( new ExpectedCondition<Boolean>() {
      public Boolean apply( WebDriver driverObject ) {
        return !isRequestPending();
      }
    } );
  }

  public boolean isRequestPending() {
    if( driver instanceof JavascriptExecutor ) {
      String script = "return RAPUtil.busy;";
      JavascriptExecutor executer = ( JavascriptExecutor )driver;
      return ( ( Boolean )executer.executeScript( script, ( Object )null ) ).booleanValue();
    } else {
      throw new IllegalStateException( "This driver does not support JavaScript execution." );
    }
  }

  public boolean isElementAvailable( String xpath ) {
    int count = selenium.getXpathCount( xpath ).intValue();
    // NOTE [tb] : I do not know why, but an element can be "not present" and have an count == 1,
    //             or be "present" but not found by isVisible...
    try {
      return count > 0 && selenium.isElementPresent( xpath ) && selenium.isVisible( xpath );
    } catch( SeleniumException e ) {
      return false;
    }
  }


  private String xpathToJs( String xpath ) {
    return   "document.evaluate( \""
           + xpath
           + "\", document, null, XPathResult.ANY_TYPE, null ).iterateNext()";
  }

  private void checkElementCount( String xpath ) {
    int elementCount = selenium.getXpathCount( xpath ).intValue();
    if( elementCount != 1 ) {
      throw new IllegalStateException( elementCount + " Elements for " + xpath );
    }
  }

  private void patchRAP() {
    // seleniums mouse*** methods sometimes do not set pageX/Y which can crash the server
    // due to reported mouse coordinates [ null, null ]
    String script =   "rwt.qx.Class.__initializeClass( rwt.event.MouseEvent );\n"
                    + "var proto = rwt.event.MouseEvent.prototype;\n"
                    + "var wrap = function( name ) {\n"
                    + "  var org = proto[ name ];\n"
                    + "  proto[ name ] = function() {\n"
                    + "    var result = org.apply( this, arguments ) || 0;\n"
                    + "    return result;\n"
                    + "  };\n"
                    + "};\n"
                    + "wrap( 'getScreenX' );\n"
                    + "wrap( 'getScreenY' );\n"
                    + "wrap( 'getClientX' );\n"
                    + "wrap( 'getClientY' );\n"
                    + "wrap( 'getPageX' );\n"
                    + "wrap( 'getPageY' );\n";
    selenium.runScript( wrapJS( script ) );
    // Required for emulating key events on internal RAP event handler level
    selenium.runScript( wrapJS( NAMESPACE_JS + TEST_UTIL_JS_CONTENT ) );
    selenium.runScript( RAP_UTIL_JS_CONTENT );
    // Force empty grid cells to be renderd in DOM (hack)
    selenium.runScript(   "rwt.qx.Class.__initializeClass( rwt.widgets.GridItem );"
                        + "rwt.widgets.GridItem.prototype.hasText = function(){ return true };" );
  }

  private static String wrapJS( String js ) {
    return "(function(){\n " + js + "\n }());";
  }

  private static String readTestUtil() {
    return readFile( TEST_UTIL_JS );
  }

  private static String readRAPUtil() {
    return readFile( RAP_UTIL_JS );
  }

  private static String readFile( String url ) {
    String result;
    try {
      InputStream stream
        = TestContribution.class.getResourceAsStream( url );
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

}
