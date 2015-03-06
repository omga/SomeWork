/*
 * Copyright (C) 2010 The Android Open Source Project
 * Copyright (C) 2011 Adam Nybäck
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
package se.anyro.nfc_reader;

import android.media.AudioFormat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.otentico.android.nfc.Utils;


public class NotAuthenticatedScreen extends BaseFragmentActivity {

    private Toolbar mToolbar;

    @Override
    protected int getActivityLayout() {
        return R.layout.not_authenticated_screen;
    }

    @Override
    protected int getDrawerLayout() {
        return R.id.drawer_layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.not_authenticated_screen);
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
        //Utils.playMonoSoundWithBrokenFileHeader(this, R.raw.error4, AudioFormat.CHANNEL_OUT_MONO);
        Utils.playAudioTrack(this, R.raw.bt_error,AudioFormat.CHANNEL_OUT_MONO);
        //Utils.playMonoSoundWithBrokenFileHeader(this, R.raw.error_sound, AudioFormat.CHANNEL_OUT_STEREO);

    }

}
