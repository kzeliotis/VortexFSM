package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dc.gtest.vortex.models.CustomFieldDefaultValuesModel;
import dc.gtest.vortex.models.CustomFieldModel;
import dc.gtest.vortex.models.ProjectZoneModel;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_ZONES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_INSTALLATION;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_INSTALLATION_ZONES_LIST;
import static dc.gtest.vortex.support.MyGlobals.INSTALLATION_ZONES_LIST;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_INSTALLATION_ZONES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_NEW_ZONE_CUSTOM_FIELDS_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_CF_DEFAULT_VALUES_DATA_FOR_SHOW;

public class InstallationZonesData {

    public static void generate(boolean refresh) {

        INSTALLATION_ZONES_LIST.clear();
        INSTALLATION_ZONES_LIST_FILTERED.clear();

        String zones = MyPrefs.getString(PREF_DATA_INSTALLATION_ZONES_LIST, "");

        if (zones.isEmpty()) {
            zones = MyPrefs.getStringWithFileName(PREF_FILE_INSTALLATION_ZONES_DATA_FOR_SHOW,  SELECTED_INSTALLATION.getProjectInstallationId(), "");
        }

        if (!zones.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(zones);

                ProjectZoneModel zoneModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    if(MyJsonParser.getStringValue(oneObject, "ProjectZoneDescription", "").length() ==0) {
                        continue;
                    }

                    zoneModel = new ProjectZoneModel();
                    CustomFieldModel cfModel;

                    String ZoneId = MyJsonParser.getStringValue(oneObject, "ZoneId", "");

                    if(ZoneId.equals("-1")){
                        List<CustomFieldDefaultValuesModel> defaultValuesList = new ArrayList<>();
                        List<CustomFieldModel> customFieldsForNewZone = new ArrayList<>();
                        CustomFieldDefaultValuesModel defaultValueModel;
                        String customFieldsObject = MyJsonParser.getStringValue(oneObject, "CustomFields", "");
                        JSONArray customFields = new JSONArray(customFieldsObject);

                        for (int n = 0; n < customFields.length(); n++){
                            JSONObject initCustomField = customFields.getJSONObject(n);

                            if(MyJsonParser.getStringValue(initCustomField, "CustomFieldId", "").equals("-1")) {
                                String CustomFieldValuesObject = MyJsonParser.getStringValue(initCustomField, "CustomFieldValues", "");
                                JSONArray customFieldsDefaultValues = new JSONArray(CustomFieldValuesObject);
                                for (int dv = 0; dv < customFieldsDefaultValues.length(); dv++) {

                                    JSONObject dvObject = customFieldsDefaultValues.getJSONObject(dv);
                                    defaultValueModel = new CustomFieldDefaultValuesModel();

                                    defaultValueModel.setBelongsToCustomFieldId(MyJsonParser.getStringValue(dvObject, "BelongsToCustomFieldId", ""));
                                    defaultValueModel.setDefaultValue(MyJsonParser.getStringValue(dvObject, "Value", ""));
                                    defaultValueModel.setInitial(MyJsonParser.getBooleanValue(dvObject, "IsDefault", false));
                                    defaultValueModel.setIsEdited(false);

                                    defaultValuesList.add(defaultValueModel);
                                }

                                MyPrefs.setStringWithFileName(PREF_FILE_ZONE_CF_DEFAULT_VALUES_DATA_FOR_SHOW, "0", defaultValuesList.toString());
                                continue;
                            }

                            cfModel = new CustomFieldModel();
                            cfModel.setCustomFieldId(MyJsonParser.getStringValue(initCustomField, "CustomFieldId", "0"));
                            cfModel.setCustomFieldDescription(MyJsonParser.getStringValue(initCustomField, "CustomFieldDescription", ""));
                            cfModel.setCustomFieldValue(MyJsonParser.getStringValue(initCustomField, "CustomFieldValue", ""));
                            cfModel.setHasValues(MyJsonParser.getBooleanValue(initCustomField, "HasValues", false));
                            cfModel.setCustomFieldDataType(MyJsonParser.getStringValue(initCustomField, "CustomFieldDataType", ""));
                            cfModel.setCustomFieldValueId(MyJsonParser.getStringValue(initCustomField, "VortexTableCustomFieldId", "0"));
                            cfModel.setEditable(MyJsonParser.getBooleanValue(initCustomField, "Editable", true));
                            cfModel.setObjectTable(MyJsonParser.getStringValue(initCustomField, "VortexTable", ""));
                            cfModel.setObjectTableIdField(MyJsonParser.getStringValue(initCustomField, "VortexTableIdField", ""));
                            cfModel.setObjectTableId(MyJsonParser.getStringValue(initCustomField, "VortexTableId", "0"));

                            customFieldsForNewZone.add(cfModel);
                        }

                        MyPrefs.setStringWithFileName(PREF_FILE_NEW_ZONE_CUSTOM_FIELDS_DATA_FOR_SHOW, "0", customFieldsForNewZone.toString());
                        continue;
                    }

                    zoneModel.setZoneId(MyJsonParser.getStringValue(oneObject, "ZoneId", ""));
                    zoneModel.setZoneName(MyJsonParser.getStringValue(oneObject, "ProjectZoneDescription", ""));
                    zoneModel.setZoneNotes(MyJsonParser.getStringValue(oneObject, "ProjectZoneNotes", ""));
                    zoneModel.setZoneCode(MyJsonParser.getStringValue(oneObject, "ProjectZoneCode", ""));
                    zoneModel.setProjectInstallationId(MyJsonParser.getStringValue(oneObject, "ProjectInstallationId", ""));
                    zoneModel.setCustomFieldsString(MyJsonParser.getStringValue(oneObject, "CustomFieldsString", ""));
                    zoneModel.setProjectId(MyJsonParser.getStringValue(oneObject, "ProjectId", ""));

                    String code = zoneModel.getZoneCode();
                    if(code.length() > 0){code += " - ";}
                    String Name = zoneModel.getZoneName();
                    zoneModel.setZoneFullName(code + Name);

                    String customFileds1 = MyJsonParser.getStringValue(oneObject, "CustomFields", "");
                    List<CustomFieldModel> customFieldsList = new ArrayList<>();

                    JSONArray customFields1 = new JSONArray(customFileds1);
                    for (int dv = 0; dv < customFields1.length(); dv++) {
                        cfModel = new CustomFieldModel();
                        JSONObject cfObject = customFields1.getJSONObject(dv);
                        cfModel.setCustomFieldId(MyJsonParser.getStringValue(cfObject, "CustomFieldId", "0"));
                        cfModel.setCustomFieldDescription(MyJsonParser.getStringValue(cfObject, "CustomFieldDescription", ""));
                        cfModel.setCustomFieldValue(MyJsonParser.getStringValue(cfObject, "CustomFieldValue", ""));
                        cfModel.setHasValues(MyJsonParser.getBooleanValue(cfObject, "HasValues", false));
                        cfModel.setCustomFieldDataType(MyJsonParser.getStringValue(cfObject, "CustomFieldDataType", ""));
                        cfModel.setCustomFieldValueId(MyJsonParser.getStringValue(cfObject, "VortexTableCustomFieldId", "0"));
                        cfModel.setEditable(MyJsonParser.getBooleanValue(cfObject, "Editable", true));
                        cfModel.setObjectTable(MyJsonParser.getStringValue(cfObject, "VortexTable", ""));
                        cfModel.setObjectTableIdField(MyJsonParser.getStringValue(cfObject, "VortexTableIdField", ""));
                        cfModel.setObjectTableId(MyJsonParser.getStringValue(cfObject, "VortexTableId", "0"));

                        customFieldsList.add(cfModel);
                    }

                    zoneModel.setZoneCustomFields(customFieldsList);

                    INSTALLATION_ZONES_LIST.add(zoneModel);
                }

                Collections.sort(INSTALLATION_ZONES_LIST, (a, b) -> a.getZoneFullName().compareTo(b.getZoneFullName()));

                INSTALLATION_ZONES_LIST_FILTERED.addAll(INSTALLATION_ZONES_LIST);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
