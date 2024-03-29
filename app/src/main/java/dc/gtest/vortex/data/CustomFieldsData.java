package dc.gtest.vortex.data;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dc.gtest.vortex.models.CustomFieldColumnDefaultValueModel;
import dc.gtest.vortex.models.CustomFieldDefaultValuesModel;
import dc.gtest.vortex.models.CustomFieldDetailColumnModel;
import dc.gtest.vortex.models.CustomFieldDetailModel;
import dc.gtest.vortex.models.CustomFieldModel;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyGlobals.CUSTOM_FIELDS_LIST;
import static dc.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_DETAILS_LIST;
import static dc.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_DETAILS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.CUSTOM_FIELD_EMPTY_DETAILS_MAP;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_CUSTOM_FIELD_DETAIL;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_COMPANY_CF_DEFAULT_VALUES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_COMPANY_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_CUSTOM_FIELD_EMPTY_DETAILS;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_DET_CF_DEFAULT_VALUES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_DET_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_DET_CUSTOM_FIELDS_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATIONS_CF_DEFAULT_VALUES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATIONS_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_CUSTOM_FIELDS_DATA_FOR_SHOW;

public class CustomFieldsData {

    public static void generate(boolean refresh, String vortexTable, String vortexTableId) {

        CUSTOM_FIELDS_LIST.clear();


        String customFields = "";

        switch (vortexTable){
            case "ProjectInstallations":
                customFields = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_CUSTOM_FIELDS_DATA_FOR_SHOW,  vortexTableId, ""); //MyPrefs.getString(PREF_DATA_INSTALLATION_CUSTOM_FIELDS_LIST, "");

//                if (customFields.isEmpty()||refresh) {
//                    customFields = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_CUSTOM_FIELDS_DATA_FOR_SHOW,  vortexTableId, "");
//                }
                break;

            case "Company":
                customFields = MyPrefs.getStringWithFileName(PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW,  vortexTableId, ""); //MyPrefs.getString(PREF_DATA_COMPANY_CUSTOM_FIELDS_LIST, "");

//                if (customFields.isEmpty()||refresh) {
//                    customFields = MyPrefs.getStringWithFileName(PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW,  vortexTableId, "");
//                }
                break;

            case "Det":
                customFields = MyPrefs.getStringWithFileName(PREF_FILE_DET_CUSTOM_FIELDS_DATA_FOR_SHOW,  vortexTableId, ""); //MyPrefs.getString(PREF_DATA_COMPANY_CUSTOM_FIELDS_LIST, "");

//                if (customFields.isEmpty()||refresh) {
//                    customFields = MyPrefs.getStringWithFileName(PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW,  vortexTableId, "");
//                }
                break;
        }


        if (!customFields.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(customFields);

                CustomFieldModel customFieldModel;
                List<CustomFieldDetailModel> empty_detail_list = new ArrayList<>();
                List<CustomFieldDetailColumnModel> emptyDetailColumnsList = new ArrayList<>();

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    customFieldModel = new CustomFieldModel();

                    String CustomFieldId = MyJsonParser.getStringValue(oneObject, "CustomFieldId", "");

                    if(CustomFieldId.equals("-1")){
                        List<CustomFieldDefaultValuesModel> defaultValuesList = new ArrayList<>();
                        //List<CustomFieldModel> customFieldsForNewZone = new ArrayList<>();
                        CustomFieldDefaultValuesModel defaultValueModel;
                        String customFieldsDVObject = MyJsonParser.getStringValue(oneObject, "CustomFieldValues", "");
                        JSONArray cFieldsDV = new JSONArray(customFieldsDVObject);

//                        for (int n = 0; n < customFields.length(); n++){
//                            JSONObject initCustomField = cFields.getJSONObject(n);
//
//                            if(MyJsonParser.getStringValue(initCustomField, "CustomFieldId", "").equals("-1")) {
//                                String CustomFieldValuesObject = MyJsonParser.getStringValue(initCustomField, "CustomFieldValues", "");
//                                JSONArray customFieldsDefaultValues = new JSONArray(CustomFieldValuesObject);
                        for (int dv = 0; dv < cFieldsDV.length(); dv++) {

                            JSONObject dvObject = cFieldsDV.getJSONObject(dv);
                            defaultValueModel = new CustomFieldDefaultValuesModel();

                            defaultValueModel.setBelongsToCustomFieldId(MyJsonParser.getStringValue(dvObject, "BelongsToCustomFieldId", ""));
                            defaultValueModel.setDefaultValue(MyJsonParser.getStringValue(dvObject, "Value", ""));
                            defaultValueModel.setInitial(MyJsonParser.getBooleanValue(dvObject, "IsDefault", false));
                            defaultValueModel.setIsEdited(false);

                            defaultValuesList.add(defaultValueModel);
                        }

                        switch(vortexTable){
                            case "ProjectInstallations":
                                MyPrefs.setStringWithFileName(PREF_FILE_INSTALLATIONS_CF_DEFAULT_VALUES_DATA_FOR_SHOW, "0", defaultValuesList.toString());
                                break;
                            case "Company":
                                MyPrefs.setStringWithFileName(PREF_FILE_COMPANY_CF_DEFAULT_VALUES_DATA_FOR_SHOW, "0", defaultValuesList.toString());
                                break;
                            case "Det":
                                MyPrefs.setStringWithFileName(PREF_FILE_DET_CF_DEFAULT_VALUES_DATA_FOR_SHOW, "0", defaultValuesList.toString());
                                break;
                        }

                        continue;
                    }


                    customFieldModel.setCustomFieldId(MyJsonParser.getStringValue(oneObject, "CustomFieldId", "0"));
                    customFieldModel.setCustomFieldDescription(MyJsonParser.getStringValue(oneObject, "CustomFieldDescription", ""));
                    customFieldModel.setCustomFieldValue(MyJsonParser.getStringValue(oneObject, "CustomFieldValue", ""));
                    customFieldModel.setHasValues(MyJsonParser.getBooleanValue(oneObject, "HasValues", false));
                    customFieldModel.setCustomFieldDataType(MyJsonParser.getStringValue(oneObject, "CustomFieldDataType", ""));
                    customFieldModel.setCustomFieldValueId(MyJsonParser.getStringValue(oneObject, "VortexTableCustomFieldId", "0"));
                    customFieldModel.setEditable(MyJsonParser.getBooleanValue(oneObject, "Editable", true));
                    customFieldModel.setObjectTable(MyJsonParser.getStringValue(oneObject, "VortexTable", ""));
                    customFieldModel.setObjectTableIdField(MyJsonParser.getStringValue(oneObject, "VortexTableIdField", ""));
                    customFieldModel.setObjectTableId(MyJsonParser.getStringValue(oneObject, "VortexTableId", "0"));

                    if(customFieldModel.getCustomFieldDataType().equals("MasterDetail")){
                        String CustomFieldDetails = MyJsonParser.getStringValue(oneObject, "CustomFieldDetails", "");
                        if(CustomFieldDetails.length() == 0){continue;}
                        JSONArray cfDetails = new JSONArray(CustomFieldDetails);
                        CustomFieldDetailModel cfDetail;
                        List<CustomFieldDetailModel> cfDetailList = new ArrayList<>();
                        for (int d = 0; d < cfDetails.length(); d++) {

                            JSONObject dObject = cfDetails.getJSONObject(d);

                            if(MyJsonParser.getStringValue(dObject, "CustomFieldId", "0").contains("-")){


                                List<CustomFieldColumnDefaultValueModel> columnDefaultValues = new ArrayList<>();
                                CustomFieldColumnDefaultValueModel cfCDV; // = new CustomFieldColumnDefaultValueModel();
                                CustomFieldDetailColumnModel emptyDetailColumn; //= new CustomFieldDetailColumnModel();
                                String Cf_det_col = MyJsonParser.getStringValue(dObject,"CustomFieldsDetailColumns", "");
                                JSONArray ColumnsDV = new JSONArray(Cf_det_col);

                                CustomFieldDetailModel cfEmpty_detail = new CustomFieldDetailModel();

                                cfEmpty_detail.setCustomFieldId(MyJsonParser.getStringValue(dObject, "CustomFieldId", "0").replace("-", ""));
                                cfEmpty_detail.setDetailTable(MyJsonParser.getStringValue(dObject, "DetailTable", "")) ;
                                cfEmpty_detail.setDetailTableId("0");
                                cfEmpty_detail.setDetailTableId_Field(MyJsonParser.getStringValue(dObject, "DetailTableId_Field", ""));
                                cfEmpty_detail.setVortexTable(MyJsonParser.getStringValue(dObject, "VortexTable", ""));
                                cfEmpty_detail.setVortexTableIdField(MyJsonParser.getStringValue(dObject, "VortexTableIdField", ""));
                                cfEmpty_detail.setVortexTableId("0");
                                cfEmpty_detail.setCustomFieldDescription(MyJsonParser.getStringValue(dObject, "CustomFieldDescription", ""));
                                cfEmpty_detail.setCustomFieldDetailsString("");
                                cfEmpty_detail.setIsEdited(false);

                                empty_detail_list.add(cfEmpty_detail);

                                for (int n = 0; n < ColumnsDV.length(); n++){
                                    JSONObject columnsdvObj = ColumnsDV.getJSONObject(n);

                                    if(MyJsonParser.getStringValue(columnsdvObj, "CustomFieldId", "0").equals("-1")){
                                        String c_dv = MyJsonParser.getStringValue(columnsdvObj, "CustomFieldColumnDefaultValues", "");
                                        JSONArray columnDefaultValuesArray = new JSONArray(c_dv);
                                        for (int vl = 0; vl < columnDefaultValuesArray.length(); vl++){

                                            JSONObject col_DV = columnDefaultValuesArray.getJSONObject(vl);

                                            cfCDV = new CustomFieldColumnDefaultValueModel();

                                            cfCDV.setBelongsToCustomFieldId(MyJsonParser.getStringValue(col_DV,"BelongsToCustomFieldId", "0"));
                                            cfCDV.setCustomFieldsDetailColumnId(MyJsonParser.getStringValue(col_DV, "CustomFieldsDetailColumnId", "0"));
                                            cfCDV.setInitial(MyJsonParser.getBooleanValue(col_DV, "Initial", false));
                                            cfCDV.setDefaultValue(MyJsonParser.getStringValue(col_DV, "Value", ""));

                                            columnDefaultValues.add(cfCDV);
                                        }

                                        switch(vortexTable){
                                            case "ProjectInstallations":
                                                MyPrefs.setStringWithFileName(PREF_FILE_INSTALLATIONS_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW, "0", columnDefaultValues.toString());
                                                break;
                                            case "Company":
                                                MyPrefs.setStringWithFileName(PREF_FILE_COMPANY_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW, "0", columnDefaultValues.toString());
                                                break;
                                            case "Det":
                                                MyPrefs.setStringWithFileName(PREF_FILE_DET_CF_DETAILS_DEFAULT_VALUES_DATA_FOR_SHOW, "0", columnDefaultValues.toString());
                                                break;
                                        }

                                    }else{

                                        emptyDetailColumn = new CustomFieldDetailColumnModel();

                                        emptyDetailColumn.setColumnDataType(MyJsonParser.getStringValue(columnsdvObj, "ColumnDataType", ""));
                                        emptyDetailColumn.setColumnDescription(MyJsonParser.getStringValue(columnsdvObj, "ColumnDescription", ""));
                                        emptyDetailColumn.setColumnName(MyJsonParser.getStringValue(columnsdvObj, "ColumnName", ""));
                                        emptyDetailColumn.setColumnValue(MyJsonParser.getStringValue(columnsdvObj, "ColumnValue", ""));
                                        emptyDetailColumn.setCustomFieldId(MyJsonParser.getStringValue(columnsdvObj, "CustomFieldId", "0"));
                                        emptyDetailColumn.setCustomFieldsDetailColumnId(MyJsonParser.getStringValue(columnsdvObj, "CustomFieldsDetailColumnId", "0"));
                                        emptyDetailColumn.setHasValues(MyJsonParser.getBooleanValue(columnsdvObj, "HasValues", false));

                                        emptyDetailColumnsList.add(emptyDetailColumn);
                                    }
                                }

                                continue;
                            }


                            cfDetail = new CustomFieldDetailModel();

                            cfDetail.setCustomFieldId(MyJsonParser.getStringValue(dObject, "CustomFieldId", "0"));
                            cfDetail.setDetailTable(MyJsonParser.getStringValue(dObject, "DetailTable", "")) ;
                            cfDetail.setDetailTableId(MyJsonParser.getStringValue(dObject, "DetailTableId", "0"));
                            cfDetail.setDetailTableId_Field(MyJsonParser.getStringValue(dObject, "DetailTableId_Field", ""));
                            cfDetail.setVortexTable(MyJsonParser.getStringValue(dObject, "VortexTable", ""));
                            cfDetail.setVortexTableIdField(MyJsonParser.getStringValue(dObject, "VortexTableIdField", ""));
                            cfDetail.setVortexTableId(MyJsonParser.getStringValue(dObject, "VortexTableId", "0"));
                            cfDetail.setCustomFieldDescription(MyJsonParser.getStringValue(dObject, "CustomFieldDescription", ""));
                            cfDetail.setCustomFieldDetailsString(MyJsonParser.getStringValue(dObject, "CustomFieldDetailsString", ""));
                            cfDetail.setIsEdited(false);

                            String dt_columns_string = MyJsonParser.getStringValue(dObject, "CustomFieldsDetailColumns", "");
                            JSONArray dt_columns_array = new JSONArray(dt_columns_string);
                            List<CustomFieldDetailColumnModel> dt_column_list = new ArrayList<>();
                            CustomFieldDetailColumnModel dt_column = new CustomFieldDetailColumnModel();
                            for(int cl = 0; cl < dt_columns_array.length(); cl++){

                                JSONObject dt_column_object = dt_columns_array.getJSONObject(cl);

                                dt_column = new CustomFieldDetailColumnModel();

                                dt_column.setColumnDataType(MyJsonParser.getStringValue(dt_column_object, "ColumnDataType", ""));
                                dt_column.setColumnDescription(MyJsonParser.getStringValue(dt_column_object, "ColumnDescription", ""));
                                dt_column.setColumnName(MyJsonParser.getStringValue(dt_column_object, "ColumnName", ""));
                                dt_column.setColumnValue(MyJsonParser.getStringValue(dt_column_object, "ColumnValue", ""));
                                dt_column.setCustomFieldId(MyJsonParser.getStringValue(dt_column_object, "CustomFieldId", "0"));
                                dt_column.setCustomFieldsDetailColumnId(MyJsonParser.getStringValue(dt_column_object, "CustomFieldsDetailColumnId", "0"));
                                dt_column.setHasValues(MyJsonParser.getBooleanValue(dt_column_object, "HasValues", false));

                                dt_column_list.add(dt_column);

                            }

                            cfDetail.setCustomFieldsDetailColumns(dt_column_list);

                            cfDetailList.add(cfDetail);
                        }

                        customFieldModel.setCustomFieldDetails(cfDetailList);

                    }

                    CUSTOM_FIELDS_LIST.add(customFieldModel);

                }


                CUSTOM_FIELD_EMPTY_DETAILS_MAP.clear();

                for(CustomFieldDetailModel cfdm : empty_detail_list){
                    String CustomFieldId = cfdm.getCustomFieldId();

                    for(CustomFieldDetailColumnModel cfdcm : emptyDetailColumnsList){
                        if(cfdcm.getCustomFieldId().equals(CustomFieldId)){
                            List<CustomFieldDetailColumnModel> col = cfdm.getCustomFieldsDetailColumns();
                            if(col == null){col = new ArrayList<>();}
                            col.add(cfdcm);
                            cfdm.setCustomFieldsDetailColumns(col);
                        }
                    }

                    CUSTOM_FIELD_EMPTY_DETAILS_MAP.put(CustomFieldId, cfdm);
                }

                MyPrefs.setStringWithFileName(PREF_FILE_CUSTOM_FIELD_EMPTY_DETAILS, vortexTable, new Gson().toJson(CUSTOM_FIELD_EMPTY_DETAILS_MAP));

//                for (int u = 0; u < emptyDetailColumnsList.size(); ++u){
//                    if(CUSTOM_FIELD_EMPTY_COLUMNS_MAP.containsKey(emptyDetailColumnsList.get(u).getCustomFieldId())){
//                        CUSTOM_FIELD_EMPTY_COLUMNS_MAP.get(emptyDetailColumnsList.get(u).getCustomFieldId()).add(emptyDetailColumnsList.get(u));
//                    }else{
//                        List<CustomFieldDetailColumnModel> columns_by_customfieldId = new ArrayList<>();
//                        columns_by_customfieldId.add(emptyDetailColumnsList.get(u));
//                        CUSTOM_FIELD_EMPTY_COLUMNS_MAP.put(emptyDetailColumnsList.get(u).getCustomFieldId(), columns_by_customfieldId);
//                    }
//                }


                for (CustomFieldModel cfm : CUSTOM_FIELDS_LIST){
                    if(cfm.getCustomFieldId().equals(SELECTED_CUSTOM_FIELD.getCustomFieldId())){
                        SELECTED_CUSTOM_FIELD = cfm;
                        CUSTOM_FIELD_DETAILS_LIST = SELECTED_CUSTOM_FIELD.getCustomFieldDetails();
                        CUSTOM_FIELD_DETAILS_LIST_FILTERED.clear();
                        CUSTOM_FIELD_DETAILS_LIST_FILTERED.addAll(CUSTOM_FIELD_DETAILS_LIST);
                        for(CustomFieldDetailModel cfdm : SELECTED_CUSTOM_FIELD.getCustomFieldDetails()){
                            if(cfdm.getDetailTableId().equals(SELECTED_CUSTOM_FIELD_DETAIL.getDetailTableId())){
                                SELECTED_CUSTOM_FIELD_DETAIL = cfdm;
                            }
                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
