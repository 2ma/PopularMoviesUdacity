package hu.am2.popularmovies.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import hu.am2.popularmovies.ui.browser.BrowserViewModel;
import hu.am2.popularmovies.ui.detail.DetailViewModel;
import hu.am2.popularmovies.viewmodel.PopularMoviesViewModelFactory;

/*
This class was based on https://github.com/googlesamples/android-architecture-components project,
 on the GithubBrowserSample/ViewModelModule.java

 License:
    Copyright 2018 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed
with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(BrowserViewModel.class)
    abstract ViewModel bindBrowserViewModel(BrowserViewModel browserViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel.class)
    abstract ViewModel bindDetailViewModel(DetailViewModel detailViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(PopularMoviesViewModelFactory factory);
}
