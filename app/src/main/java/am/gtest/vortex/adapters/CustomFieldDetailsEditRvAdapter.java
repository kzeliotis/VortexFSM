package am.gtest.vortex.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import am.gtest.vortex.R;
import am.gtest.vortex.models.CustomFieldDetailColumnModel;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyPrefs;
import am.gtest.vortex.support.MyUtils;

import static am.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD_DETAIL;

public class CustomFieldDetailsEditRvAdapter extends RecyclerView.Adapter<CustomFieldDetailsEditRvAdapter.ViewHolder> {

//    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();

    private final List<CustomFieldDetailColumnModel> mValues;
    private final Context ctx;

    private final String selectMeasurement;
    private String vortexTable;
    //private boolean IsNewZone = false;

    private CustomFieldDetailsEditRvAdapter.ViewHolder mHolder;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private int mHour = 0;
    private int mMinute = 0;
    private Calendar c;


    public CustomFieldDetailsEditRvAdapter(List<CustomFieldDetailColumnModel> items, Context ctx, String selectMeasurement, String vortexTable) {
        mValues = items;
        this.ctx = ctx;
        this.selectMeasurement = selectMeasurement;
        this.vortexTable = vortexTable;

    }

    @NonNull
    @Override
    public CustomFieldDetailsEditRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_field_edit, parent, false);
        return new CustomFieldDetailsEditRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomFieldDetailsEditRvAdapter.ViewHolder holder, final int position) {

        holder.mItem = mValues.get(position);

        String DataType = holder.mItem.getColumnDataType();
        boolean IsDateTime = false;
        boolean IsBoolean = false;
        boolean IsString = false;
        boolean IsInt = false;
        boolean IsDouble = false;
        boolean IsMasterDetail = false;
        switch (DataType){
            case "System.Boolean":
                IsBoolean = true;
                break;
            case "System.DateTime":
                IsDateTime = true;
                break;
            case "System.Date":
                IsDateTime = true;
                break;
            case "System.String":
                IsString = true;
                break;
            case "System.Int32":
                IsInt = true;
                break;
            case "System.Double":
                IsDouble = true;
                break;
            case "MasterDetail":
                IsMasterDetail = true;
                break;
        }

        //holder.tvCustomFieldDescription.setText(mValues.get(position).getCustomFieldDescription());
        holder.tvCustomFieldDescription.setText(holder.mItem.getColumnDescription() + ": ");

        if (holder.mItem.getHasValues()) {
            holder.swCustomFieldBool.setVisibility(View.GONE);
            holder.etCustomFieldValue.setVisibility(View.GONE);
        } else {
            holder.spCustomFieldDV.setVisibility(View.GONE);

            if(IsBoolean){
                holder.etCustomFieldValue.setVisibility(View.GONE);
                holder.spCustomFieldDV.setVisibility(View.GONE);
            }else{
                holder.swCustomFieldBool.setVisibility(View.GONE);
            }

            if(IsDateTime){
                holder.etCustomFieldValue.setFocusable(false);
                holder.etCustomFieldValue.setFocusableInTouchMode(false);
            } else {
                holder.etCustomFieldValue.setFocusable(true);
                holder.etCustomFieldValue.setFocusableInTouchMode(true);
            }

            if(IsString){
                holder.etCustomFieldValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            }else if(IsInt){
                holder.etCustomFieldValue.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if(IsDouble){
                holder.etCustomFieldValue.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        }

        holder.tvToCustomFieldDetails.setVisibility(View.GONE);

        // setup switch
        holder.swCustomFieldBool.setOnCheckedChangeListener(null);
        holder.swCustomFieldBool.setChecked(holder.mItem.getColumnValue().equalsIgnoreCase("true"));
        holder.swCustomFieldBool.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.mItem.setColumnValue("True");
            } else {
                holder.mItem.setColumnValue("False");
            }

            String customFieldDetailsColumnId = holder.mItem.getCustomFieldsDetailColumnId();
            String value = holder.mItem.getColumnValue();
            if(value.length() == 0){value = "false";}
            setValueInCustomFieldDetailModel(customFieldDetailsColumnId, value);

        });

        // setup filling value
        if(IsDateTime){
            holder.etCustomFieldValue.setOnClickListener(v -> {

                //globalMandatoryTaskPosition = holder.getAdapterPosition();
                String DateTimeString = holder.mItem.getColumnValue();

                if(DateTimeString.length() > 0) {
                    String DateString = DateTimeString.split(" ")[0];
                    String TimeString = DateTimeString.split(" ")[1];

                    mDay = Integer.parseInt(DateString.split("/")[0]);
                    mMonth = Integer.parseInt(DateString.split("/")[1]);
                    mYear = Integer.parseInt(DateString.split("/")[2]);
                    mHour  = Integer.parseInt(TimeString.split(":")[0]);
                    mMinute = Integer.parseInt(TimeString.split(":")[1]);
                }

                mHolder = holder;
                show_DatePicker();

            });
        }


        holder.etCustomFieldValue.setText(holder.mItem.getColumnValue());
        holder.etCustomFieldValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.mItem.setColumnValue(s.toString());

                String customFieldsDetailColumnId = holder.mItem.getCustomFieldsDetailColumnId();
                String value = holder.mItem.getColumnValue();
                setValueInCustomFieldDetailModel(customFieldsDetailColumnId, value);

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // setup spinner
        if(holder.spCustomFieldDV.getVisibility() == View.VISIBLE){
            try {

                String InitialValue = "";
                String customFieldsDetailColumnId = holder.mItem.getCustomFieldsDetailColumnId();
                String cfdvValues = "";
                switch(vortexTable){
                    case "ProjectInstallations":
                        cfdvValues = MyPrefs.getStringWithFileName(MyPrefs.PREF_FILE_INSTALLATIONS_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW, "0", "");
                        break;
                    case "Company":
                        cfdvValues = MyPrefs.getStringWithFileName(MyPrefs.PREF_FILE_COMPANY_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW, "0", "");
                        break;
                }

                JSONArray jArrayDefaultValues = new JSONArray(cfdvValues);
                List<String> dValues = new ArrayList<>();
                for (int i = 0; i < jArrayDefaultValues.length(); i++) {
                    JSONObject oneObject = jArrayDefaultValues.getJSONObject(i);
                    if(MyJsonParser.getStringValue(oneObject, "CustomFieldsDetailColumnId", "").equals(customFieldsDetailColumnId)){
                        dValues.add(MyJsonParser.getStringValue(oneObject, "DefaultValue", ""));
                        if (MyJsonParser.getBooleanValue(oneObject, "Initial", false)){
                            InitialValue = MyJsonParser.getStringValue(oneObject, "DefaultValue", "");
                        }
                    }
                }

                String[] newValuesArray = new String[dValues.size() + 1];

                newValuesArray[0] = selectMeasurement;

                for (int i = 0; i < dValues.size(); i++) {
                    newValuesArray[i+1] = dValues.get(i);
                }

                holder.spCustomFieldDV.setAdapter(new MySpinnerAdapter(ctx, newValuesArray));

                holder.spCustomFieldDV.setOnItemSelectedListener(null);

                for (int j = 1; j < newValuesArray.length; j++) {

                    if(holder.mItem.getColumnValue().isEmpty()){ //////
                        if (newValuesArray[j].equals(InitialValue) && SELECTED_CUSTOM_FIELD_DETAIL.getDetailTableId().equals("0")) { /////
                            holder.spCustomFieldDV.setSelection(j); //////
                        }
                    } else {
                        if (newValuesArray[j].equals(holder.mItem.getColumnValue())) {
                            holder.spCustomFieldDV.setSelection(j);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.spCustomFieldDV.setOnTouchListener((parent, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    MyUtils.hideKeypad(holder.etCustomFieldValue);
                    holder.spCustomFieldDV.setFocusable(true);
                    holder.spCustomFieldDV.setFocusableInTouchMode(true);
                    holder.spCustomFieldDV.requestFocus();
                    holder.spCustomFieldDV.performClick();
                }
                return true;
            });



            holder.spCustomFieldDV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        holder.mItem.setColumnValue(holder.spCustomFieldDV.getSelectedItem().toString());
                    } else {
                        holder.mItem.setColumnValue("");
                    }

                    String customFieldsDetailColumnId = holder.mItem.getCustomFieldsDetailColumnId();
                    String value = holder.mItem.getColumnValue();
                    setValueInCustomFieldDetailModel(customFieldsDetailColumnId, value);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }


        // setup enabled / disabled
//        if (holder.mItem.getEditable()) {
//            holder.llCustomFieldContent.setBackgroundColor(ContextCompat.getColor(ctx, R.color.light_blue_50));
//            holder.swCustomFieldBool.setEnabled(true);
//            holder.etCustomFieldValue.setEnabled(true);
//            holder.spCustomFieldDV.setEnabled(true);
//
//        } else {
//            holder.llCustomFieldContent.setBackgroundColor(ContextCompat.getColor(ctx, R.color.grey_300));
//            holder.swCustomFieldBool.setEnabled(false);
//            holder.etCustomFieldValue.setEnabled(false);
//            holder.spCustomFieldDV.setEnabled(false);
//        }
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
                        mHolder.etCustomFieldValue.setText(dateTimeValue);

                        mYear = 0;
                        mMonth = 0;
                        mDay = 0;
                        mHour = 0;
                        mMinute = 0;

                    }
                }, mHour, mMinute, true);

        timePickerDialog.show();
    }


    private void setValueInCustomFieldDetailModel(String customFieldDetailsColumnId, String value){

        for (int i = 0; i < SELECTED_CUSTOM_FIELD_DETAIL.getCustomFieldsDetailColumns().size(); i++){
            if(SELECTED_CUSTOM_FIELD_DETAIL.getCustomFieldsDetailColumns().get(i).getCustomFieldsDetailColumnId().equals(customFieldDetailsColumnId)){
                SELECTED_CUSTOM_FIELD_DETAIL.getCustomFieldsDetailColumns().get(i).setColumnValue(value);
            }
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView tvCustomFieldDescription;
        final SwitchCompat swCustomFieldBool;
        final EditText etCustomFieldValue;
        final Spinner spCustomFieldDV;
        final TextView tvToCustomFieldDetails;
        final LinearLayout llCustomFieldContent;
        final LinearLayout llCustomFieldEdit;
        CustomFieldDetailColumnModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvCustomFieldDescription = view.findViewById(R.id.tvCustomFieldDescription);
            swCustomFieldBool = view.findViewById(R.id.swCustomFieldBool);
            etCustomFieldValue = view.findViewById(R.id.etCustomFieldValue);
            spCustomFieldDV = view.findViewById(R.id.spCustomFieldDV);
            llCustomFieldContent = view.findViewById(R.id.llCustomFieldContent);
            llCustomFieldEdit = view.findViewById(R.id.llCustomFieldEdit);
            tvToCustomFieldDetails = view.findViewById(R.id.tvToCustomFieldDetails);

        }
    }
}
