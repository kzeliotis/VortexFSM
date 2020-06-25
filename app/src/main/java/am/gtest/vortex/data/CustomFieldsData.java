package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.gtest.vortex.models.CustomFieldDefaultValuesModel;
import am.gtest.vortex.models.CustomFieldDetailModel;
import am.gtest.vortex.models.CustomFieldModel;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyGlobals.CUSTOM_FIELDS_LIST;
import static am.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_COMPANY_CUSTOM_FIELDS_LIST;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_INSTALLATION_CUSTOM_FIELDS_LIST;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_COMPANY_CF_DEFAULT_VALUES_DATA_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATIONS_CF_DEFAULT_VALUES_DATA_FOR_SHOW;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_CUSTOM_FIELDS_DATA_FOR_SHOW;

public class CustomFieldsData {

    public static void generate(boolean refresh, String vortexTable, String vortexTableId) {

        CUSTOM_FIELDS_LIST.clear();


        String customFields = "";

        switch (vortexTable){
            case "ProjectInstallations":
                customFields = MyPrefs.getString(PREF_DATA_INSTALLATION_CUSTOM_FIELDS_LIST, "");

                if (customFields.isEmpty()||refresh) {
                    customFields = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_CUSTOM_FIELDS_DATA_FOR_SHOW,  vortexTableId, "");
                }
                break;

            case "Company":
                customFields = MyPrefs.getString(PREF_DATA_COMPANY_CUSTOM_FIELDS_LIST, "");

                if (customFields.isEmpty()||refresh) {
                    customFields = MyPrefs.getStringWithFileName(PREF_FILE_COMPANY_CUSTOM_FIELDS_DATA_FOR_SHOW,  vortexTableId, "");
                }
                break;
        }


        if (!customFields.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(customFields);

                CustomFieldModel customFieldModel;

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

                            defaultValueModel.setCustomFieldId(MyJsonParser.getStringValue(dvObject, "CustomFieldId", ""));
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
                        }

                        continue;
                    }

//                            cfModel = new CustomFieldModel();
//                            cfModel.setCustomFieldId(MyJsonParser.getStringValue(initCustomField, "CustomFieldId", "0"));
//                            cfModel.setCustomFieldDescription(MyJsonParser.getStringValue(initCustomField, "CustomFieldDescription", ""));
//                            cfModel.setCustomFieldValue(MyJsonParser.getStringValue(initCustomField, "CustomFieldValue", ""));
//                            cfModel.setHasValues(MyJsonParser.getBooleanValue(initCustomField, "HasValues", false));
//                            cfModel.setCustomFieldDataType(MyJsonParser.getStringValue(initCustomField, "CustomFieldDataType", ""));
//                            cfModel.setCustomFieldValueId(MyJsonParser.getStringValue(initCustomField, "VortexTableCustomFieldId", "0"));
//                            cfModel.setEditable(MyJsonParser.getBooleanValue(initCustomField, "Editable", true));
//                            cfModel.setObjectTable(MyJsonParser.getStringValue(initCustomField, "VortexTable", ""));
//                            cfModel.setObjectTableIdField(MyJsonParser.getStringValue(initCustomField, "VortexTableIdField", ""));
//                            cfModel.setObjectTableId(MyJsonParser.getStringValue(initCustomField, "VortexTableId", "0"));
//
//                            customFieldsForNewZone.add(cfModel);
//                        }
//
//                        MyPrefs.setStringWithFileName(PREF_FILE_NEW_ZONE_CUSTOM_FIELDS_DATA_FOR_SHOW, "0", customFieldsForNewZone.toString());
//                        continue;
//                        }
//                    }

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
                        JSONArray cfDetails = new JSONArray(CustomFieldDetails);
                        CustomFieldDetailModel cfDetail;
                        List<CustomFieldDetailModel> cfDetailList = new ArrayList<>();
                        for (int d = 0; d < cfDetails.length(); d++) {

                            JSONObject dObject = cfDetails.getJSONObject(d);
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

                            cfDetailList.add(cfDetail);
                        }

                        customFieldModel.setCustomFieldDetails(cfDetailList);

                    }

                    CUSTOM_FIELDS_LIST.add(customFieldModel);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
