rap-selenium-demo
=================

Demonstrating usage of the Selenium UI Testing tool with Eclipse RAP

Requirements:
* Eclipse IDE with RAP 2.1 tools installed
* RAP 2.1.1
* org.eclipse.rap.demo.controls bundle, patched with ControlsDemo.patch (see ./patches)
* Selenium-WebDriver for Java (http://docs.seleniumhq.org/docs/03_webdriver.jsp(
* com.eclipsesource.rap.aria bundle (commercial add-on)
* Mozilla Firefox (path of binary may have to be adjusted in Controls_Demo_Test.java)

Start:
* Start patched controls demo with included launch config from the Eclispe IDE
* Run Controls_Demo_Test as a JUnit Test
