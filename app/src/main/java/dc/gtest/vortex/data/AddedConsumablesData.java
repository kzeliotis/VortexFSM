package dc.gtest.vortex.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import dc.gtest.vortex.models.AddedConsumableModel;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ADDED_CONSUMABLES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_ADDED_CONSUMABLES_FOR_SHOW;

public class AddedConsumablesData {

    public static void generate(String assignmentId) {

        ADDED_CONSUMABLES_LIST.clear();
        ADDED_CONSUMABLES_LIST_FILTERED.clear();

        String consumables = MyPrefs.getStringWithFileName(PREF_FILE_ADDED_CONSUMABLES_FOR_SHOW, assignmentId, "");

        if (!consumables.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(consumables);

                AddedConsumableModel addedConsumableModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    addedConsumableModel = new AddedConsumableModel();

                    addedConsumableModel.setName(MyJsonParser.getStringValue(oneObject, "name", ""));
                    addedConsumableModel.setNotes(MyJsonParser.getStringValue(oneObject, "notes", ""));
                    addedConsumableModel.setSuggested(MyJsonParser.getStringValue(oneObject, "suggested", ""));
                    addedConsumableModel.setUsed(MyJsonParser.getStringValue(oneObject, "used", ""));

                    ADDED_CONSUMABLES_LIST.add(addedConsumableModel);
                }

                Collections.sort(ADDED_CONSUMABLES_LIST, (a, b) -> a.getName().compareTo(b.getName()));

                ADDED_CONSUMABLES_LIST_FILTERED.addAll(ADDED_CONSUMABLES_LIST);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e("myLogs: AddedConsData", "ADDED_CONSUMABLES_LIST:\n" + ADDED_CONSUMABLES_LIST);
    }
}
