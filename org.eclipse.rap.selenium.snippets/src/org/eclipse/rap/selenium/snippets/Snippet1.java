package org.eclipse.rap.selenium.snippets;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


public class Snippet1 extends AbstractEntryPoint {

    @Override
    protected void createContents(Composite parent) {
      SeleniumUtil.enableUITests();
      parent.setLayout(new GridLayout(2, false));
      Button checkbox = new Button(parent, SWT.CHECK);
      checkbox.setText("Hello");
      Button button = new Button(parent, SWT.PUSH);
      button.setText("World1");
    }

}
