package com.tpnet.sqlbrite_sqldelight_sample;

import com.tpnet.sqlbrite_sqldelight_sample.ui.ItemsFragment;
import com.tpnet.sqlbrite_sqldelight_sample.ui.ListsFragment;
import com.tpnet.sqlbrite_sqldelight_sample.ui.NewItemFragment;
import com.tpnet.sqlbrite_sqldelight_sample.ui.NewListFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by litp on 2017/3/15.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {


    void inject(ListsFragment fragment);

    void inject(ItemsFragment fragment);

    void inject(NewItemFragment fragment);

    void inject(NewListFragment fragment);

}
