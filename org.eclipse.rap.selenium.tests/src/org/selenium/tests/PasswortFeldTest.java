package org.selenium.tests;

import java.io.File;

import org.eclipse.rap.selenium.RapBot;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.thoughtworks.selenium.Selenium;

public class PasswortFeldTest {

	@Test
	public void test() {
//		File firefox = new File("D:/Programme/Firefox 10/firefox.exe");
		File firefox = new File("c:/Program Files (x86)/Mozilla Firefox 15/firefox.exe");

		WebDriver driver = new FirefoxDriver(new FirefoxBinary(firefox),
				new FirefoxProfile());

		Selenium selenium = new WebDriverBackedSelenium(driver,
				"http://localhost:9999/passwortFeld");
		RapBot rap = new RapBot(driver, selenium);
		rap.loadApplication("http://localhost:9999/passwortFeld");

		rap.input("//div[2]/div[2]/input", "abc");
		rap.input("//div[2]/div[3]/input", "def");
		rap.input("//div[2]/div[4]/input", "ghi");

		rap.click("//*[contains(text(),'Show Textfield Content')]");

		// 2. Button ist aus irgendwelchen Gr�nden invisible, vorerst per Hand
		// bet�tigen

		// selenium.click("//*[contains(text(),'Show Textbinding Content')]");

		// driver.quit();
	}
}
