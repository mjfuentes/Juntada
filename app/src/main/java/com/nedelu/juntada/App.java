package com.nedelu.juntada;

import android.app.Application;
import android.os.StrictMode;

import com.codeslap.persistence.DatabaseSpec;
import com.codeslap.persistence.PersistenceConfig;
import com.nedelu.juntada.model.*;
import com.nedelu.juntada.model.aux.GroupMember;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseSpec database = PersistenceConfig.registerSpec(10);
        database.matchNotAutoIncrement(Group.class, User.class, GroupMember.class, Event.class);
        database.match(PollRequest.class);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
