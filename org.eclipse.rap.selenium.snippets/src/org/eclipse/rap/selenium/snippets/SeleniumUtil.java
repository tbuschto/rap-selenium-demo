package org.eclipse.rap.selenium.snippets;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.Widget;

import com.eclipsesource.rap.aria.Aria;


public class SeleniumUtil {

  public static void enableUITests() {
    Aria.activate();
  }

  public static void setTestId( Widget widget, String testId ) {
    JavaScriptExecutor executor = RWT.getClient().getService( JavaScriptExecutor.class );
    StringBuilder builder = new StringBuilder();
    builder.append( "rwt.remote.HandlerUtil.callWithTarget( \"" );
    builder.append( WidgetUtil.getId( widget ) );
    builder.append( "\", function( widget ) { widget.setHtmlAttribute( \"testId\", \"" );
    builder.append( testId );
    builder.append( "\" ); } );" );
    executor.execute( builder.toString() );
  }

}

