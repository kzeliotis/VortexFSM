package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import am.gtest.vortex.models.AllConsumableModel;
import am.gtest.vortex.support.MyJsonParser;
import am.gtest.vortex.support.MyPrefs;

import static am.gtest.vortex.support.MyGlobals.ALL_CONSUMABLES_LIST;
import static am.gtest.vortex.support.MyGlobals.ALL_CONSUMABLES_LIST_FILTERED;
import static am.gtest.vortex.support.MyPrefs.PREF_DATA_ALL_CONSUMABLES;
import static am.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW;

public class AllConsumablesData {

    public static void generate(String AssignmentId) {

        ALL_CONSUMABLES_LIST.clear();
        ALL_CONSUMABLES_LIST_FILTERED.clear();
        String Consumables = MyPrefs.getStringWithFileName(PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW, AssignmentId, "");
        //String allConsumables = MyPrefs.getString(PREF_DATA_ALL_CONSUMABLES, "");
        if(Consumables.isEmpty()){
            Consumables = MyPrefs.getString(PREF_DATA_ALL_CONSUMABLES, "");
        }


        if (!Consumables.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(Consumables);

                AllConsumableModel allConsumableModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    allConsumableModel = new AllConsumableModel();

                    allConsumableModel.setConsumableName(MyJsonParser.getStringValue(oneObject, "ProductDescription", ""));
                    allConsumableModel.setNotes(MyJsonParser.getStringValue(oneObject, "notes", ""));
                    allConsumableModel.setTypeId(MyJsonParser.getIntValue(oneObject, "TypeId", -1));
                    allConsumableModel.setProductId(MyJsonParser.getIntValue(oneObject, "ProductId", 0));

                    ALL_CONSUMABLES_LIST.add(allConsumableModel);
                }

                Collections.sort(ALL_CONSUMABLES_LIST, (a, b) -> a.getConsumableName().compareTo(b.getConsumableName()));

                ALL_CONSUMABLES_LIST_FILTERED.addAll(ALL_CONSUMABLES_LIST);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
