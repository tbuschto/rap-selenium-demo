package org.eclipse.rap.selenium.snippets;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class PasswortFeld extends AbstractEntryPoint {

  public class TestRealm extends Realm {
    public TestRealm() {
      setDefault(this);
    }


    @Override
    public boolean isCurrent() {
      return true;
    }
  }

  private Text text1;
  private Text text2;
  private Text text3;

  public String string1;
  public String string2;
  public String string3;


  @Override
  protected void createContents(Composite parent) {
    SeleniumUtil.enableUITests();
    parent.setLayout(new GridLayout(1, false));

    this.text1 = new Text(parent, SWT.PASSWORD | SWT.BORDER);
    this.text2 = new Text(parent, SWT.PASSWORD | SWT.BORDER);
    this.text3 = new Text(parent, SWT.PASSWORD | SWT.BORDER);
    this.string1 = new String("");
    this.string2 = new String("");
    this.string3 = new String("");

    new TestRealm();

    DataBindingContext dataBindingContext = new DataBindingContext();
    dataBindingContext.bindValue(SWTObservables.observeText(this.text1, SWT.FocusOut),
                                 PojoObservables.observeValue(this, "string1"));
    dataBindingContext.bindValue(SWTObservables.observeText(this.text2, SWT.FocusOut),
                                 PojoObservables.observeValue(this, "string2"));
    dataBindingContext.bindValue(SWTObservables.observeText(this.text3, SWT.FocusOut),
                                 PojoObservables.observeValue(this, "string3"));

    Button button = new Button(parent, SWT.PUSH);
    button.setText("Show Textfield Content");
    button.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        printTextFields();
      }
    });

    Button buttonBinding = new Button(parent, SWT.PUSH);
    buttonBinding.setText("Show Textbinding Content");
    buttonBinding.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        printTextBindings();
      }

    });
  }


  void printTextFields() {
    System.out.println("Text1: " + this.text1.getText());
    System.out.println("Text2: " + this.text2.getText());
    System.out.println("Text3: " + this.text3.getText());
  }


  void printTextBindings() {
    System.out.println("String1: " + this.string1);
    System.out.println("String2: " + this.string2);
    System.out.println("string3: " + this.string3);
  }


  public String getString1() {
    return this.string1;
  }


  public void setString1(String string1) {
    this.string1 = string1;
  }


  public String getString2() {
    return this.string2;
  }


  public void setString2(String string2) {
    this.string2 = string2;
  }


  public String getString3() {
    return this.string3;
  }


  public void setString3(String string3) {
    this.string3 = string3;
  }

}
