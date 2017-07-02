package com.nedelu.juntada;

import android.app.Application;

import com.codeslap.persistence.DatabaseSpec;
import com.codeslap.persistence.PersistenceConfig;
import com.nedelu.juntada.model.*;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseSpec database = PersistenceConfig.registerSpec(1);
        database.match(Group.class, User.class);
    }
}
