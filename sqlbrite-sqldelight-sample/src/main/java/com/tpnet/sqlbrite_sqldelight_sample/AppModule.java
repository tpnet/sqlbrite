package com.tpnet.sqlbrite_sqldelight_sample;

import com.tpnet.sqlbrite_sqldelight_sample.db.DbModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by litp on 2017/3/15.
 */
@Module(
        includes = {
                DbModule.class,
        }
)
public class AppModule {

    private final BaseApplication application;

    AppModule(BaseApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    BaseApplication provideApplication() {
        return application;
    }
}
