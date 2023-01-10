package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import dc.gtest.vortex.api.GetZoneProducts;
import dc.gtest.vortex.models.ZoneModel;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyGlobals.ZONES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ZONES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ZONES_LIST;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONES_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_PROJECT_ID;

public class ZonesData {

    public static void generate(boolean refresh) {

        ZONES_LIST.clear();
        ZONES_LIST_FILTERED.clear();
        String AssignmentId = SELECTED_ASSIGNMENT.getAssignmentId();

        String zones = MyPrefs.getString(PREF_DATA_ZONES_LIST, "");

        if (zones.isEmpty()) {
            zones = MyPrefs.getStringWithFileName(PREF_FILE_ZONES_DATA_FOR_SHOW,  AssignmentId, "");
        }

        if (!zones.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(zones);

                ZoneModel zoneModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    zoneModel = new ZoneModel();

                    String ZoneId = MyJsonParser.getStringValue(oneObject, "ZoneId", "");

                    if(ZoneId.equals("-1")){
                       continue;
                    }

                    zoneModel.setZoneId(MyJsonParser.getStringValue(oneObject, "ZoneId", ""));
                    zoneModel.setZoneName(MyJsonParser.getStringValue(oneObject, "ProjectZoneDescription", ""));
                    zoneModel.setZoneNotes(MyJsonParser.getStringValue(oneObject, "ProjectZoneNotes", ""));
                    zoneModel.setZoneCode(MyJsonParser.getStringValue(oneObject, "ProjectZoneCode", ""));
                    zoneModel.setProjectInstallationId(MyJsonParser.getStringValue(oneObject, "ProjectInstallationId", ""));
                    zoneModel.setCustomFieldsString(MyJsonParser.getStringValue(oneObject, "CustomFieldsString", ""));

                    String code = zoneModel.getZoneCode();
                    if(code.length() > 0){code += " - ";}
                    String Name = zoneModel.getZoneName();
                    zoneModel.setZoneFullName(code + Name);

                    ZoneId = zoneModel.getZoneId();
                    String prefKey = MyPrefs.getString(PREF_PROJECT_ID, "") + "_" + ZoneId + "_" + AssignmentId;
                    String zoneProducts = MyPrefs.getStringWithFileName(PREF_FILE_ZONE_PRODUCTS_DATA_FOR_SHOW, prefKey, "");
                    if (zoneProducts.isEmpty() || refresh) {
                        GetZoneProducts getZoneProducts = new GetZoneProducts(null, null);
                        getZoneProducts.execute(ZoneId);
                    }

                    ZONES_LIST.add(zoneModel);
                }

                Collections.sort(ZONES_LIST, (a, b) -> a.getZoneFullName().compareTo(b.getZoneFullName()));

                ZONES_LIST_FILTERED.addAll(ZONES_LIST);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
