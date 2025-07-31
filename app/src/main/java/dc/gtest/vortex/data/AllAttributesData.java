package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import dc.gtest.vortex.models.AllAttributeModel;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyGlobals.ALL_ATTRIBUTES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_ATTRIBUTES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ALL_ATTRIBUTES;

public class AllAttributesData {

    public static void generate() {

        String allAttributes = MyPrefs.getString(PREF_DATA_ALL_ATTRIBUTES, "");

        if (!allAttributes.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(allAttributes);

                AllAttributeModel allAttributeModel;

                ALL_ATTRIBUTES_LIST.clear();
                ALL_ATTRIBUTES_LIST_FILTERED.clear();

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    allAttributeModel = new AllAttributeModel();

                    allAttributeModel.setAttributeDescription(MyJsonParser.getStringValue(oneObject, "AttributeDescription", ""));
                    allAttributeModel.setAttributeId(MyJsonParser.getStringValue(oneObject, "AttributeId", ""));
                    int isDateTime = MyJsonParser.getIntValue(oneObject, "IsDateTime", 0);
                    allAttributeModel.setDateTime(isDateTime == 1);
                    allAttributeModel.setAttributeDefaultValues(MyJsonParser.getJsonArrayValue(oneObject, "AttributeDefaultValues"));

                    ALL_ATTRIBUTES_LIST.add(allAttributeModel);
                }

                Collections.sort(ALL_ATTRIBUTES_LIST, (a, b) -> a.getAttributeDescription().compareTo(b.getAttributeDescription()));

                ALL_ATTRIBUTES_LIST_FILTERED.addAll(ALL_ATTRIBUTES_LIST);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
