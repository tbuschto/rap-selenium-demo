package org.eclipse.rap.selenium.snippets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;


public class BasicApplication implements ApplicationConfiguration {

    @Override
    public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        application.addEntryPoint("/snippet1", Snippet1.class, properties );
        application.addEntryPoint("/snippet2", Snippet2.class, properties );
    }

}
