/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/




package com.cs185.catchphrase;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

/**
 * A simple launcher activity containing a summary sample description
 * and a few action bar buttons.
 */
public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    public static final String FRAGTAG = "ImmersiveModeFragment";
    
    private ImmersiveModeFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        
        setContentView(R.layout.activity_main);

        if (getFragmentManager().findFragmentByTag(FRAGTAG) == null ) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            fragment = new ImmersiveModeFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
        fragment.hideSystemBars();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
