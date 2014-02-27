package org.selenium.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.rap.rwt.jstest.TestContribution;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.thoughtworks.selenium.Selenium;


public class RAPUtil {


  private static final String TEST_UTIL_JS = "/org/eclipse/rwt/test/fixture/TestUtil.js";
  private static final String RAP_UTIL_JS = "/org/selenium/tests/RAPUtil.js";
  private final WebDriver driver;
  private final Selenium selenium;
  private final String TEST_UTIL_JS_CONTENT;
  private final String RAP_UTIL_JS_CONTENT;
  private final String NAMESPACE_JS =   "var namespace = function( value ) {"
                                      + "rwt.qx.Class.createNamespace( value, {} );"
                                      + "};\n";
  private static final String CHARSET = "UTF-8";


  public RAPUtil( WebDriver driver, Selenium selenium ) {
    this.driver = driver;
    this.selenium = selenium;
    TEST_UTIL_JS_CONTENT = readTestUtil();
    RAP_UTIL_JS_CONTENT = readRAPUtil();
  }

  public static String first( String xpath ) {
    // regarding syntax see
    // http://blog.neustar.biz/web-performance/effectively-using-seleniums-getxpathcount-function/
    // There may be a bug where this fails in some combinations with FF, eg (...)[1]//*[...]
    return "(" + xpath + ")[1]";
  }

  public static String containing( String xpath ) {
    return "[count(" + xpath + ")>0]"; // TODO : exclude self (//* is "descendant-or-self )
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

  public static String byAria( String role ) {
    return "//*[@role='" + role + "']";
  }

  public static String byAria( String role, String label ) {
    return "//*[@role='" + role + "' and @aria-label='" + label + "']";
  }

  public static String byAria( String role, String state, boolean value ) {
    return "//*[@role='" + role + "' and @aria-" + state + "='" + value + "']";
  }

  public static String byAria( String role, String property, String value ) {
    return "//*[@role='" + role + "' and @aria-" + property + "='" + value + "']";
  }

  public void loadApplication( String url ) {
    selenium.open( url );
    selenium.waitForPageToLoad( "10000" );
    waitForAppear( byAria( "application" ) );
    patchRAP();
  }

  public String getId( String xpath ) {
    return getAttribute( xpath, "id" );
  }

  public String getAttribute( String xpath, String attribute ) {
    checkElementCount( xpath );
    return driver.findElement( By.xpath( xpath ) ).getAttribute( attribute );
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

  public void input( String xpath, String text ) {
    checkElementCount( xpath );
    WebElement inputElement = driver.findElement( By.xpath( xpath ) );
    //inputElement.click(); <- Chrome refuses to click here due to the underlying HTML element
    //See http://code.google.com/p/chromedriver/issues/detail?id=149&q=input&colspec=ID%20Status%20Pri%20Owner%20Summary
    // focus or selenium click won't work either (use JS!)
    inputElement.sendKeys( text );
  }

  public void waitForAppear( final String locator ) {
    new WebDriverWait( driver, 10 ).until( new ExpectedCondition<Boolean>() {
      public Boolean apply( WebDriver driverObject ) {
        return isElementAvailable( locator );
      }
    } );
  }

  public void waitForDisappear( final String locator ) {
    new WebDriverWait( driver, 10 ).until( new ExpectedCondition<Boolean>() {
      public Boolean apply( WebDriver driverObject ) {
        return !isElementAvailable( locator );
      }
    } );
  }

  public void waitForServer() {
    // There may be a short delay between the user input and the widget sending the request.
    // In most cases the request is hardcoded to 60ms, though the JS timer isn't very precise.
    // In a few cases like Modify event the delay is much longer and this method won't suffice
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
    // NOTE [tb] : I do not know why, but an element can be "not present" and have an count == 1
    return count > 0 && selenium.isElementPresent( xpath ) && selenium.isVisible( xpath );
  }

  // Not optimal: using pageDown also selects items while scrolling
  // Alternatives: Control grid directly or use scrollbar when aria support has been enabled
   public String findGridItem( String gridPath, String cellText ) {
    String itemPath = gridPath + byAria( "row" ) + byText( cellText );
    if( isElementAvailable( itemPath ) ) {
      return itemPath;
    }
    click( gridPath + byAria( "row", "busy", false ) + "[1]" ); // make sure there is a selection
    press( gridPath, "Home" );
    while(    !isElementAvailable( itemPath )
           && !isLastGridItem( byId( getSelectedGridItemId( gridPath ) ) )
    ) {
      press( gridPath, "PageDown" ); // page down
    }
    return isElementAvailable( itemPath ) ? itemPath : null;
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

  private boolean isLastGridItem( String xpath ) {
    return "".equals( selenium.getAttribute( xpath + "@aria-flowto" ) );
  }

  private String getSelectedGridItemId( String gridPath ) {
    return selenium.getAttribute( gridPath +  byAria( "row", "selected", true ) + "@id" );
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
