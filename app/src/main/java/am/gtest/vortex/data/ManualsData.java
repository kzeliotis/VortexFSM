package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import am.gtest.vortex.models.ManualModel;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyGlobals.MANUALS_LIST;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_MANUALS;

public class ManualsData {

    public static void generate() {

        String manuals = MyPrefs.getString(PREF_DATA_MANUALS, "");

        if (!manuals.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(manuals);

                MANUALS_LIST.clear();
                ManualModel manualModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    manualModel = new ManualModel();

                    manualModel.setManualName(MyJsonParser.getStringValue(oneObject, "ManualName", ""));
                    manualModel.setManualUrl(MyJsonParser.getStringValue(oneObject, "ManualUrl", ""));
                    manualModel.setManualKeywords(MyJsonParser.getStringValue(oneObject, "Keywords", ""));
                    manualModel.setblobAttachmentId(MyJsonParser.getStringValue(oneObject, "BlobAttachmentId", ""));
                    manualModel.setfileName(MyJsonParser.getStringValue(oneObject, "FileName", ""));
                    MANUALS_LIST.add(manualModel);
                }

                Collections.sort(MANUALS_LIST, (a, b) -> a.getManualName().compareTo(b.getManualName()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
