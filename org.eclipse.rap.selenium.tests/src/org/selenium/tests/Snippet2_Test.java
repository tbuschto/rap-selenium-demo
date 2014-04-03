package org.selenium.tests;

import static org.eclipse.rap.selenium.RapBot.byAria;
import static org.eclipse.rap.selenium.RapBot.byText;
import static org.junit.Assert.assertEquals;

import org.eclipse.rap.selenium.RapBot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.Selenium;

public class Snippet2_Test {

  private final static String baseUrl = "http://127.0.0.1:9999";

  private WebDriver driver;
  private Selenium selenium;
  private RapBot rap;

  static {
    System.setProperty( "webdriver.firefox.bin", "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe" );
  }

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    selenium = new WebDriverBackedSelenium( driver, baseUrl );
    rap = new RapBot( driver, selenium );
    rap.loadApplication( baseUrl + "/snippet2" );
  }

  @After
  public void tearDown() throws Exception {
    selenium.stop();
  }

  @Test
  public void testClickButtons_byContent() throws Exception {
    rap.click( byAria( "checkbox" ) + byText( "Hello" ) );
    assertEquals( 1, selenium.getXpathCount( byAria( "button" ) + byText( "World2" ) ) );
  }


}
