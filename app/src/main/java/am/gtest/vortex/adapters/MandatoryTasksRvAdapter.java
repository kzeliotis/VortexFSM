package am.gtest.vortex.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.activities.AssignmentActionsActivity;
import am.gtest.vortex.models.MandatoryTaskModel;
import am.gtest.vortex.support.MyImages;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MyUtils;
import am.gtest.vortex.support.TakeUploadPhoto;

import static am.gtest.vortex.support.MyGlobals.CONST_ASSIGNMENT_PHOTOS_FOLDER;
import static am.gtest.vortex.support.MyGlobals.CONST_MANDATORY_TASKS_PHOTOS_FOLDER;
import static am.gtest.vortex.support.MyGlobals.MANDATORY_TASKS_LIST;
import static am.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_TAKE_ASSIGNMENT_PHOTO;
import static am.gtest.vortex.support.MyGlobals.OTHER_APP_RESULT_TAKE_MANDATORY_TASK_PHOTO;
import static am.gtest.vortex.support.MyGlobals.PERMISSIONS_STORAGE;
import static am.gtest.vortex.support.MyGlobals.REQUEST_CAMERA_FOR_MANDATORY_PHOTO;
import static am.gtest.vortex.support.MyGlobals.REQUEST_EXTERNAL_STORAGE_FOR_MANDATORY_PHOTO;
import static am.gtest.vortex.support.MyGlobals.globalCurrentPhotoPath;
import static am.gtest.vortex.support.MyGlobals.globalMandatoryTaskPosition;
import static am.gtest.vortex.support.MyGlobals.mandatoryStepPhoto;
import static am.gtest.vortex.support.MyLocalization.localized_step_comments;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_IN;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_IS_CHECKED_OUT;
import static am.gtest.vortex.support.MyPrefs.PREF_SHOW_MANDATORY_TASKS_COMMENTS;

public class MandatoryTasksRvAdapter extends RecyclerView.Adapter<MandatoryTasksRvAdapter.ViewHolder> {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final List<MandatoryTaskModel> mValues;
    private final Context ctx;
    private final String assignmentId;
    private final String selectMeasurement;



    private ViewHolder mHolder;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private int mHour = 0;
    private int mMinute = 0;
    private Calendar c;


    public MandatoryTasksRvAdapter(List<MandatoryTaskModel> items, Context ctx, String assignmentId, String selectMeasurement) {
        mValues = items;
        this.ctx = ctx;
        this.assignmentId = assignmentId;
        this.selectMeasurement = selectMeasurement;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mandatory_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        holder.mItem = mValues.get(position);

        String IsDateTime = holder.mItem.getIsDateTime();

        boolean ShowComments = MyPrefs.getBoolean(PREF_SHOW_MANDATORY_TASKS_COMMENTS, false);
        if (ShowComments){
            holder.llMandatoryComments.setVisibility(View.VISIBLE);
        } else {
            holder.llMandatoryComments.setVisibility(View.GONE);
        }


        if (!holder.mItem.getStepSequence().equals("")) {
            String taskNumberWithDot = mValues.get(position).getStepSequence() + ". ";
            holder.tvTaskNumber.setText(taskNumberWithDot);
        }

        holder.tvTaskName.setText(mValues.get(position).getStepDescription());

        String attributeNameWithColon = holder.mItem.getMeasurableAttribute() + ": ";
        holder.tvMeasurableName.setText(attributeNameWithColon);

        if (holder.mItem.getHasMeasurement().equals("0")) {
            holder.swMandatoryTasks.setVisibility(View.VISIBLE);
            holder.llMandatoryMeasurable.setVisibility(View.GONE);
        } else {
            holder.swMandatoryTasks.setVisibility(View.GONE);
            holder.llMandatoryMeasurable.setVisibility(View.VISIBLE);

            if (holder.mItem.getServiceStepDefaultValues().length() == 0) {
                holder.etMandatoryTasks.setVisibility(View.VISIBLE);
                holder.spMandatoryTasks.setVisibility(View.GONE);
                if(IsDateTime.equals("1")){
                    holder.etMandatoryTasks.setFocusable(false);
                    holder.etMandatoryTasks.setFocusableInTouchMode(false);
                } else {
                    holder.etMandatoryTasks.setFocusable(true);
                    holder.etMandatoryTasks.setFocusableInTouchMode(true);
                }
            } else {
                holder.etMandatoryTasks.setVisibility(View.GONE);
                holder.spMandatoryTasks.setVisibility(View.VISIBLE);
            }
        }

        // setup switch
        holder.swMandatoryTasks.setOnCheckedChangeListener(null);
        holder.swMandatoryTasks.setChecked(holder.mItem.getMeasurementValue().equalsIgnoreCase("done"));
        holder.swMandatoryTasks.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.mItem.setMeasurementValue("done");
            } else {
                holder.mItem.setMeasurementValue("");
            }
        });

        // setup filling value
        if(IsDateTime.equals("1")){
            holder.etMandatoryTasks.setOnClickListener(v -> {

                //globalMandatoryTaskPosition = holder.getAdapterPosition();
                String DateTimeString = holder.mItem.getMeasurementValue();

                if(DateTimeString.length() > 0) {
                    String DateString = DateTimeString.split(" ")[0];
                    String TimeString = DateTimeString.split(" ")[1];

                    mDay = Integer.valueOf(DateString.split("/")[0]);
                    mMonth = Integer.valueOf(DateString.split("/")[1]);
                    mYear = Integer.valueOf(DateString.split("/")[2]);
                    mHour  = Integer.valueOf(TimeString.split(":")[0]);
                    mMinute = Integer.valueOf(TimeString.split(":")[1]);
                }

                mHolder = holder;
                show_DatePicker();

            });
        }

        holder.etMandatoryTasks.setText(holder.mItem.getMeasurementValue());
        holder.etMandatoryTasks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.mItem.setMeasurementValue(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        holder.tvComments.setText(localized_step_comments);
        holder.etMandatoryComments.setText(holder.mItem.getStepComments());

        holder.etMandatoryComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.mItem.setStepComments(s.toString()) ;
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // setup spinner
        try {
            JSONArray jArrayDefaultValues = holder.mItem.getServiceStepDefaultValues();
            String[] newAttributesArray = new String[jArrayDefaultValues.length() + 1];

            newAttributesArray[0] = selectMeasurement;
            String InitialValue = "";

            for (int i = 0; i < jArrayDefaultValues.length(); i++) {
                JSONObject oneObject = jArrayDefaultValues.getJSONObject(i);
                newAttributesArray[i+1] = oneObject.getString("MeasurableAttributeDefaultValue");
                if (oneObject.getBoolean("Initial")){
                    InitialValue = oneObject.getString("MeasurableAttributeDefaultValue");
                }
            }

            holder.spMandatoryTasks.setAdapter(new MySpinnerAdapter(ctx, newAttributesArray));

            holder.spMandatoryTasks.setOnItemSelectedListener(null);

            for (int j = 1; j < newAttributesArray.length; j++) {

                if(holder.mItem.getMeasurementValue().isEmpty()){ //////
                    if (newAttributesArray[j].equals(InitialValue)) { /////
                        holder.spMandatoryTasks.setSelection(j); //////
                    }
                } else {
                    if (newAttributesArray[j].equals(holder.mItem.getMeasurementValue())) {
                        holder.spMandatoryTasks.setSelection(j);
                    }
                }
            }

            holder.spMandatoryTasks.setOnTouchListener((parent, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    MyUtils.hideKeypad(holder.etMandatoryTasks);
                    holder.spMandatoryTasks.setFocusable(true);
                    holder.spMandatoryTasks.setFocusableInTouchMode(true);
                    holder.spMandatoryTasks.requestFocus();
                    holder.spMandatoryTasks.performClick();
                }
                return true;
            });

            holder.spMandatoryTasks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        holder.mItem.setMeasurementValue(holder.spMandatoryTasks.getSelectedItem().toString());
                    } else {
                        holder.mItem.setMeasurementValue("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // setup take photo
        holder.ivTakePhoto.setOnClickListener(v -> {
            globalMandatoryTaskPosition = holder.getAdapterPosition();
            Activity activity = (Activity) ctx;
            new AlertDialog.Builder(ctx)
                    .setNeutralButton("Gallery", (dialog, which) -> {
                        int permission = ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permission != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE_FOR_MANDATORY_PHOTO);
                        } else {
                            AssignmentActionsActivity.pickMandatoryPhotoFromStorage(activity);
                        }
                    })
                    .setPositiveButton("Camera", (dialog, which) -> {
                        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED) {
                            mandatoryStepPhoto = true;
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_FOR_MANDATORY_PHOTO);
                        } else {
                            AssignmentActionsActivity.takeMandatoryPhotoWithCamera(ctx);
                        }
                    })
                    .show();
        });

        // setup remove image
        holder.ivRemove.setOnClickListener(v -> {
            MyUtils.deleteFile(holder.mItem.getStepPhotoPath());
            holder.mItem.setStepPhotoPath("");
            holder.mItem.setStepPhoto("");
            globalCurrentPhotoPath = "";

            holder.ivTaskPhoto.setImageDrawable(null);
            holder.ivRemove.setVisibility(View.GONE);
        });

        if (holder.mItem.getRequiresPhoto().equals("1")) {
            holder.llMandatoryPhoto.setVisibility(View.VISIBLE);
        } else {
            holder.llMandatoryPhoto.setVisibility(View.GONE);
        }

        if (!holder.mItem.getStepPhoto().isEmpty()) {
            new MyImages.SetImageFromPath(holder.ivTaskPhoto).execute(holder.mItem.getStepPhotoPath(), "64", "64");
            holder.ivRemove.setVisibility(View.VISIBLE);

        } else {
            holder.ivTaskPhoto.setImageDrawable(null);
            holder.ivRemove.setVisibility(View.GONE);
        }

        // setup enabled / disabled
        if (MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_IN, assignmentId, false) &&
                !MyPrefs.getBooleanWithFileName(PREF_FILE_IS_CHECKED_OUT, assignmentId, false)) {
            holder.llMandatoryTasksContent.setBackgroundColor(ContextCompat.getColor(ctx, R.color.light_blue_50));
            holder.swMandatoryTasks.setEnabled(true);
            holder.etMandatoryTasks.setEnabled(true);
            holder.spMandatoryTasks.setEnabled(true);
            holder.ivTakePhoto.setEnabled(true);
            holder.ivTakePhoto.setImageResource(R.drawable.ic_add_a_photo_blue_24dp);
            holder.etMandatoryComments.setEnabled(true);
        } else {
            holder.llMandatoryTasksContent.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_300));
            holder.swMandatoryTasks.setEnabled(false);
            holder.etMandatoryTasks.setEnabled(false);
            holder.spMandatoryTasks.setEnabled(false);
            holder.ivRemove.setVisibility(View.GONE);
            holder.ivTakePhoto.setEnabled(false);
            holder.ivTakePhoto.setImageResource(R.drawable.ic_add_a_photo_grey_24dp);
            holder.etMandatoryComments.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void show_DatePicker() {
        c = Calendar.getInstance();

        if(mYear == 0) {
            mYear = Calendar.getInstance().get(Calendar.YEAR);
            mMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
            mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) ;
        }


        int mYearParam = mYear;
        int mMonthParam = mMonth-1;
        int mDayParam = mDay;

        DatePickerDialog datePickerDialog = new DatePickerDialog(ctx,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mMonth = monthOfYear + 1;
                        mYear=year;
                        mDay=dayOfMonth;

                        show_TimePicker();

                    }
                }, mYearParam, mMonthParam, mDayParam);

        datePickerDialog.show();
    }


    private void show_TimePicker() {

        if (mHour == 0) {
            mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) ;
            mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        }


        TimePickerDialog timePickerDialog = new TimePickerDialog(ctx,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int pHour,
                                          int pMinute) {

                        mHour = pHour;
                        mMinute = pMinute;

                        String sYear = Integer.toString(mYear);
                        String sMonth = Integer.toString(mMonth);
                        String sDay = Integer.toString(mDay);
                        String sHour = Integer.toString(mHour);
                        String sMinute = Integer.toString(mMinute);

                        if(sMonth.length() == 1){sMonth = "0" + sMonth;}
                        if(sDay.length() == 1){sDay = "0" + sDay;}
                        if(sHour.length() == 1){sHour = "0" + sHour;}
                        if(sMinute.length() == 1){sMinute = "0" + sMinute;}

                        String dateTimeValue = sDay + "/" + sMonth + "/" + sYear + " " + sHour + ":" + sMinute;

                        //mValues.get(globalMandatoryTaskPosition).setMeasurementValue(dateTimeValue);
                        mHolder.etMandatoryTasks.setText(dateTimeValue);

                        mYear = 0;
                        mMonth = 0;
                        mDay = 0;
                        mHour = 0;
                        mMinute = 0;

                    }
                }, mHour, mMinute, true);

        timePickerDialog.show();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView tvTaskNumber;
        final TextView tvTaskName;
        final TextView tvMeasurableName;
        final SwitchCompat swMandatoryTasks;
        final EditText etMandatoryTasks;
        final Spinner spMandatoryTasks;
        final LinearLayout llMandatoryMeasurable;
        final LinearLayout llMandatoryTasksContent;
        final LinearLayout llMandatoryPhoto;
        final ImageView ivTaskPhoto;
        final ImageView ivRemove;
        final ImageView ivTakePhoto;
        final LinearLayout llMandatoryComments;
        final TextView tvComments;
        final EditText etMandatoryComments;
        MandatoryTaskModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvTaskNumber = view.findViewById(R.id.tvTaskNumber);
            tvTaskName = view.findViewById(R.id.tvTaskName);
            tvMeasurableName = view.findViewById(R.id.tvMeasurableName);
            swMandatoryTasks = view.findViewById(R.id.swMandatoryTasks);
            etMandatoryTasks = view.findViewById(R.id.etMandatoryTasks);
            spMandatoryTasks = view.findViewById(R.id.spMandatoryTasks);
            llMandatoryMeasurable = view.findViewById(R.id.llMandatoryMeasurable);
            llMandatoryTasksContent = view.findViewById(R.id.llMandatoryTasksContent);
            llMandatoryPhoto = view.findViewById(R.id.llMandatoryPhoto);
            ivTaskPhoto = view.findViewById(R.id.ivTaskPhoto);
            ivRemove = view.findViewById(R.id.ivRemove);
            ivTakePhoto = view.findViewById(R.id.ivTakePhoto);
            llMandatoryComments = view.findViewById(R.id.llMandatoryComments);
            tvComments = view.findViewById(R.id.tvComments);
            etMandatoryComments = view.findViewById(R.id.etMandatoryComments);
        }
    }


}
