package org.selenium.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.rap.rwt.jstest.TestContribution;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.thoughtworks.selenium.Selenium;


public class RAPUtil {


  private static final String TEST_UTIL_JS = "/org/eclipse/rwt/test/fixture/TestUtil.js";
  private final WebDriver driver;
  private final Selenium selenium;
  private final String TEST_UTIL_JS_CONTENT;
  private final String NAMESPACE_JS =   "var namespace = function( value ) {"
                                      + "rwt.qx.Class.createNamespace( value, {} );"
                                      + "};\n";
  private static final String CHARSET = "UTF-8";


  public static String byTestId( String testId ) {
    return "//*[@testId='" + testId + "']";
  }

  public static String byId( String testId ) {
    return "//*[@id='" + testId + "']";
  }

  public static String byContent( String content ) {
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

  public RAPUtil( WebDriver driver, Selenium selenium ) {
    this.driver = driver;
    this.selenium = selenium;
    TEST_UTIL_JS_CONTENT = readTestUtil();
  }

  public void loadApplication( String url ) {
    driver.manage().window().maximize();
    selenium.setSpeed( "500" );
    selenium.open( url );
    selenium.waitForPageToLoad( "10000" );
    waitForAppear( byAria( "application" ) );
    patchRAP();
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
    inputElement.click();
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

  public boolean isElementAvailable( String xpath ) {
    int count = selenium.getXpathCount( xpath ).intValue();
    // NOTE [tb] : I do not know why, but an element can be "not present" and have an count == 1
    return count > 0 && selenium.isElementPresent( xpath ) && selenium.isVisible( xpath );
  }

  // Not optimal: using pageDown also selects items while scrolling
  // Alternatives: Control grid directly or use scrollbar when aria support has been enabled
  public String findGridItem( String gridPath, String cellText ) {
    String itemPath = gridPath + byAria( "row" ) + byContent( cellText );
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

  private static String wrapJS( String js ) {
    return "(function(){\n " + js + "\n }());";
  }

  private static String readTestUtil() {
    try {
      InputStream stream
        = TestContribution.class.getResourceAsStream( TEST_UTIL_JS );
      if( stream == null ) {
        throw new IllegalArgumentException( "Resource not found: " + TEST_UTIL_JS );
      }
      try {
        BufferedReader reader = new BufferedReader( new InputStreamReader( stream, CHARSET ) );
        return readLines( reader );
      } finally {
        stream.close();
      }
    } catch( IOException ex ) {
      throw new RuntimeException( ex );
    }
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
