package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import am.gtest.vortex.models.MeasurableAttributeDefaultModel;
import am.gtest.vortex.models.MeasurableAttributeModel;
import am.gtest.vortex.models.ZoneProductModel;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static am.gtest.vortex.support.MyGlobals.ZONE_PRODUCTS_LIST;
import static am.gtest.vortex.support.MyGlobals.ZONE_PRODUCTS_LIST_FILTERED;
import static am.gtest.vortex.support.MyPrefs.PREF_PROJECT_ID;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW;

public class ZoneProductsData {

    public static void generate(String zoneProducts, String zoneId) {

        ZONE_PRODUCTS_LIST.clear();
        ZONE_PRODUCTS_LIST_FILTERED.clear();

        String AssignmentId = SELECTED_ASSIGNMENT.getAssignmentId();
        String prefKey = MyPrefs.getString(PREF_PROJECT_ID, "") + "_" + zoneId + "_" + AssignmentId;

        if (zoneProducts.isEmpty()) {
            zoneProducts = MyPrefs.getStringWithFileName(PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW, prefKey, "");
        }

        if (!zoneProducts.isEmpty()) {
            MyPrefs.setStringWithFileName(PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW, prefKey, zoneProducts);
            try {
                JSONArray jArrayDataFromApi = new JSONArray(zoneProducts);

                JSONObject oneObject;
                JSONArray jArrayMeasurableAttributes;
                JSONObject jObjectMeasurableAttribute;
                JSONArray jArrayDefaultValues;
                JSONObject jObjectDefaultValue;

                List<MeasurableAttributeModel> ZONE_ATTRIBUTES_LIST;
                List<MeasurableAttributeDefaultModel> ZONE_ATTRIBUTE_DEFAULTS_LIST;

                ZoneProductModel zoneModel;
                MeasurableAttributeModel attributeModel;
                MeasurableAttributeDefaultModel attributeDefaultModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    oneObject = jArrayDataFromApi.getJSONObject(i);

                    ZONE_ATTRIBUTES_LIST = new ArrayList<>();
                    ZONE_ATTRIBUTES_LIST.clear();

                    if (oneObject.has("MeasurableAttributes")) {
                        jArrayMeasurableAttributes = oneObject.getJSONArray("MeasurableAttributes");

                        for (int j = 0; j < jArrayMeasurableAttributes.length(); j++) {
                            jObjectMeasurableAttribute = jArrayMeasurableAttributes.getJSONObject(j);

                            ZONE_ATTRIBUTE_DEFAULTS_LIST = new ArrayList<>();
                            ZONE_ATTRIBUTE_DEFAULTS_LIST.clear();

                            if (jObjectMeasurableAttribute.has("MeasurableAttributeDefaultValues")) {
                                jArrayDefaultValues = jObjectMeasurableAttribute.getJSONArray("MeasurableAttributeDefaultValues");

                                for (int h = 0; h < jArrayDefaultValues.length(); h++) {
                                    jObjectDefaultValue = jArrayDefaultValues.getJSONObject(h);

                                    attributeDefaultModel = new MeasurableAttributeDefaultModel();

                                    attributeDefaultModel.setDefaultValueId(MyJsonParser.getStringValue(jObjectDefaultValue, "MeasurableAttributeDefaultValueId", ""));
                                    attributeDefaultModel.setDefaultValueName(MyJsonParser.getStringValue(jObjectDefaultValue, "DefaultValue", ""));
                                    attributeDefaultModel.setInitial(MyJsonParser.getBooleanValue(jObjectDefaultValue, "Initial", false));
                                    attributeDefaultModel.setIsEdited(MyJsonParser.getBooleanValue(jObjectDefaultValue, "IsEdited", false));

                                    ZONE_ATTRIBUTE_DEFAULTS_LIST.add(attributeDefaultModel);
                                }
                            }

                            attributeModel = new MeasurableAttributeModel();

                            attributeModel.setAttributeId(MyJsonParser.getStringValue(jObjectMeasurableAttribute, "MeasurableAttributeId", ""));
                            attributeModel.setAttributeName(MyJsonParser.getStringValue(jObjectMeasurableAttribute, "MeasurableAttributeDescription", ""));
                            attributeModel.setAttributeDefaultModel(ZONE_ATTRIBUTE_DEFAULTS_LIST);

                            ZONE_ATTRIBUTES_LIST.add(attributeModel);
                        }
                    }

                    zoneModel = new ZoneProductModel();

                    zoneModel.setZoneProductId(MyJsonParser.getStringValue(oneObject, "ZoneProductId", ""));
                    zoneModel.setZoneProductName(MyJsonParser.getStringValue(oneObject, "ZoneProductDescription", ""));
                    zoneModel.setZoneProductIdentity(MyJsonParser.getStringValue(oneObject, "ZoneProductIdentity", ""));
                    zoneModel.setMeasurableAttributeModel(ZONE_ATTRIBUTES_LIST);

                    ZONE_PRODUCTS_LIST.add(zoneModel);
                }

//                Collections.sort(ZONE_PRODUCTS_LIST, (a, b) -> a.getZoneProductIdentity().compareTo(b.getZoneProductIdentity()));

                ZONE_PRODUCTS_LIST_FILTERED.addAll(ZONE_PRODUCTS_LIST);

//                Log.e("myLogs: ZoneProducts", "---------- ZONE_PRODUCTS_LIST:\n" + ZONE_PRODUCTS_LIST);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
