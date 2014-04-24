package org.eclipse.rap.selenium;

import static org.eclipse.rap.selenium.AriaRoles.APPLICATION;
import static org.eclipse.rap.selenium.xpath.XPath.any;

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
  final WebDriver driver;
  final Selenium selenium;
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
    loadApplication( url, false );
  }

  public void loadApplication( String url, boolean ariaEnabled ) {
    selenium.open( url );
    selenium.waitForPageToLoad( "10000" );
    patchRAP();
    if( ariaEnabled ) {
      waitForAppear( any().widget( APPLICATION ) );
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

  public String getFlowTo( AbstractPath<?> xpath ) {
    return getFlowTo( xpath.toString() );
  }

  public String getFlowTo( String xpath ) {
    return getAttribute( xpath, "aria-flowto" );
  }

  /////////
  // Helper

  private String xpathToJs( String xpath ) {
    return "document.evaluate( \""
           + xpath
           + "\", document, null, XPathResult.ANY_TYPE, null ).iterateNext()";
  }

  void checkElementCount( String xpath ) {
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

    static XPath asXPath( AbstractPath<?> path ) {
    return path != null ? XPath.createXPath( path.toString() ) : null;
  }

  static XPath asXPath( String string ) {
    return string != null ? XPath.createXPath( string ) : null;
  }

}
