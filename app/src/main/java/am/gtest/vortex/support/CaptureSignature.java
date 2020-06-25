package am.gtest.vortex.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import am.gtest.vortex.R;

import static am.gtest.vortex.support.MyGlobals.CONST_EN;
import static am.gtest.vortex.support.MyPrefs.PREF_KEY_SELECTED_LANGUAGE;

public class CaptureSignature extends Activity {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private LinearLayout mContent;
    private signature mSignature;

    private Bitmap mBitmap;
    private View mView;
    private File myPath;

    private Button mCancel;
    private Button mClear;
    private Button mGetSign;
    private TextView textView1;
    private TextView textView3;
    private EditText yourName;
    private EditText etSignatureEmail;

    private String language;
    private String cancel;
    private String clear;
    private String save;
    private String yourNameTitle;
    private String pleaseSignBelow;
    private String enterName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.signature);

        language = MyPrefs.getString(PREF_KEY_SELECTED_LANGUAGE, CONST_EN);

        changeTextLanguage();

        File directory = new File(getExternalFilesDir(null) + "/signatures");
        checkMakeDir(directory);
        cleanDir(directory);

        String imageFileName = getTodayDate() + ".png";
        myPath = new File(directory, imageFileName);

        mContent = findViewById(R.id.llForSignature);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mClear = findViewById(R.id.clear);
        mGetSign = findViewById(R.id.getSign);
        mGetSign.setEnabled(false);
        mCancel = findViewById(R.id.cancel);
        mView = mContent;

        textView1 = findViewById(R.id.textView1);
        textView3 = findViewById(R.id.textView3);
        yourName = findViewById(R.id.yourName);
        etSignatureEmail = findViewById(R.id.etSignatureEmail);

        mClear.setOnClickListener(v -> {
            Log.e(LOG_TAG, "Panel Cleared");
            mSignature.clear();
            mGetSign.setEnabled(false);
        });

        mGetSign.setOnClickListener(v -> {
            MyUtils.hideKeypad(this, v);
            Log.e(LOG_TAG, "Panel Saved");
            boolean error = captureSignature();
            if(!error){
                mView.setDrawingCacheEnabled(true);
                mSignature.save(mView);
                Bundle b = new Bundle();
                b.putString("status", "done");
                b.putString("signatureName", yourName.getText().toString());
                b.putString("signatureEmail", etSignatureEmail.getText().toString());
                b.putString("imagePath", myPath.getAbsolutePath());
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        mCancel.setOnClickListener(v -> {
            MyUtils.hideKeypad(this, v);
            Log.e(LOG_TAG, "Panel Canceled");
            Bundle b = new Bundle();
            b.putString("status", "cancel");
            Intent intent = new Intent();
            intent.putExtras(b);
            setResult(RESULT_OK,intent);
            finish();
        });

        mCancel.setText(cancel);
        mClear.setText(clear);
        mGetSign.setText(save);
        textView1.setText(yourNameTitle);
        textView3.setText(pleaseSignBelow);
    }

    private boolean captureSignature() {

        boolean error = false;
        String errorMessage = "";

        if(yourName.getText().toString().equalsIgnoreCase("")){
            errorMessage = errorMessage + enterName;
            error = true;
        }

        if(error){
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 105, 50);
            toast.show();
        }

        return error;
    }

    private String getTodayDate() {
        Calendar date = Calendar.getInstance();
//        date.setTimeZone(TimeZone.getTimeZone("UTC"));
//        long currentUtcMillis = date.getTimeInMillis() + 3*60*60*1000;
        long currentUtcMillis = date.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(currentUtcMillis);
    }

    private boolean checkMakeDir(File path) {
        return !path.exists() && path.mkdirs();
    }

    private boolean cleanDir(File path) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (!file.delete()) {
                    Log.e(LOG_TAG, "Failed to delete: " + file);
                }
            }
        }
        return (path.isDirectory());
    }

    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) {

            //Log.e(LOG_TAG, "Width: " + v.getWidth());
            //Log.e(LOG_TAG, "Height: " + v.getHeight());

            if(mBitmap == null) {
                mBitmap =  Bitmap.createBitmap (mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }

            Canvas canvas = new Canvas(mBitmap);

            try {
                FileOutputStream mFileOutStream = new FileOutputStream(myPath);

                v.draw(canvas);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);
                Log.e(LOG_TAG, "url: " + url);
                //In case you want to delete the file
                //boolean deleted = myPath.delete();
                //Log.v("log_tag","deleted: " + myPath.toString() + deleted);
                //If you want to convert the image to string use base64 converter

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
//                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

//        private void debug(String string){
//        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    public void onRadioButtonClicked(View view) {
        language = new MySwitchLanguage(this).mySwitchLanguage(view);
        changeTextLanguage();

        mCancel.setText(cancel);
        mClear.setText(clear);
        mGetSign.setText(save);
        textView1.setText(yourNameTitle);
        textView3.setText(pleaseSignBelow);
    }

    private void changeTextLanguage() {
        if (language.equals("gr")) {
            cancel = getString(R.string.cancel_gr);
            clear = getString(R.string.clear_gr);
            save = getString(R.string.save_gr);
            yourNameTitle = getString(R.string.your_name_gr);
            pleaseSignBelow = getString(R.string.please_sign_below_gr);
            enterName = getString(R.string.enter_name_gr);
        } else {
            cancel = getString(R.string.cancel);
            clear = getString(R.string.clear);
            save = getString(R.string.save);
            yourNameTitle = getString(R.string.your_name);
            pleaseSignBelow = getString(R.string.please_sign_below);
            enterName = getString(R.string.enter_name);
        }
    }
}