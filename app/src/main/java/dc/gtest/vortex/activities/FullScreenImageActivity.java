package dc.gtest.vortex.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import dc.gtest.vortex.R;
import dc.gtest.vortex.support.MyImages;

public class FullScreenImageActivity extends Activity {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        String photoPath = getIntent().getStringExtra("photoPath");

        ImageView ivFullscreen = findViewById(R.id.ivFullscreen);
        ivFullscreen.setImageBitmap(MyImages.getNormalOrientedBitmap(photoPath));

        ImageView ivClose = findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> finish());
    }
}
