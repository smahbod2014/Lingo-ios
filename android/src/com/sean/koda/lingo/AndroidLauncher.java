package com.sean.koda.lingo;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication implements PlatformAdapter {

    Handler handler;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        handler = new Handler();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        Lingo lingo = new Lingo();
        lingo.setPlatformAdapter(this);
		initialize(lingo, config);
	}

    @Override
    public void displayMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AndroidLauncher.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
