rap-selenium-demo
=================

Demonstrating usage of the Selenium UI Testing tool with Eclipse RAP

Requirements:
* Eclipse IDE
* RAP 2.3
* Selenium-WebDriver for Java ( http://docs.seleniumhq.org/docs/03_webdriver.jsp )
* org.eclipse.rap.demo.controls bundle, patched with ControlsDemo.patch (see ./patches)
* com.eclipsesource.rap.aria bundle (for Aria***_Test.java, commercial add-on)

Start:
* Edit launch config for Controls Demo to run on port 8383 and "-Dorg.eclipse.rap.rwt.enableUITests=true" to the arguments.
* Apply patch
* Run controls demo
* Run example tests

Published under Eclipse Public License (EPL) v1.0
http://wiki.eclipse.org/EPL