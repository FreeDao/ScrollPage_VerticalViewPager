/*
 * Copyright (C) 2014 Johnny Shieh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.johnnyshieh.test;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;

import com.view.FragmentStatePagerAdapter;

/**
 * @ClassName: 	TestFragmentStateAdapter
 * @Description:TODO
 * @author 	Johnny Shieh
 * @date	Mar 4, 2014
 */
public class TestFragmentStateAdapter extends FragmentStatePagerAdapter {
    
    private List < String > mList ;

    public TestFragmentStateAdapter ( FragmentManager fm, List < String > list ) {
        super ( fm );
        mList = list ;
    }

    @Override
    public Fragment getItem ( int position ) {
        return TestFragment.newInstance ( mList.get ( position ) );
    }

    @Override
    public int getCount () {
        return mList == null ? 0 : mList.size () ;
    }

}
