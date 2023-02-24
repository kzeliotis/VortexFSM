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
import static dc.gtest.vortex.support.MyGlobals.CONSUMABLES_TOADD_LIST;
import static dc.gtest.vortex.support.MyGlobals.CONSUMABLES_TOADD_LIST_FILTERED;

public class ConsumablesToAddData {

    public static void generate(String assignmentId) {

        CONSUMABLES_TOADD_LIST.clear();
        CONSUMABLES_TOADD_LIST_FILTERED.clear();

        String consumables = MyPrefs.getStringWithFileName(MyPrefs.PREF_FILE_ADDED_CONSUMABLES_FOR_SYNC, assignmentId, "");

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
                    addedConsumableModel.setProductId(MyJsonParser.getIntValue(oneObject, "ProductId", 0));
                    addedConsumableModel.setStock(MyJsonParser.getStringValue(oneObject, "Stock", "0"));
                    addedConsumableModel.setWarehouseId(MyJsonParser.getStringValue(oneObject, "WarehouseId", "0"));

                    CONSUMABLES_TOADD_LIST.add(addedConsumableModel);
                }

                Collections.sort(CONSUMABLES_TOADD_LIST, (a, b) -> a.getName().compareTo(b.getName()));
                CONSUMABLES_TOADD_LIST_FILTERED.addAll(CONSUMABLES_TOADD_LIST);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e("myLogs: AddedConsData", "ADDED_CONSUMABLES_LIST:\n" + CONSUMABLES_TOADD_LIST);
    }
}
