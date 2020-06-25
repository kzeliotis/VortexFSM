package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import am.gtest.vortex.models.StatusModel;
import am.gtest.vortex.support.MyJsonParser;

import static am.gtest.vortex.support.MyGlobals.STATUSES_LIST;

public class StatusesData {

    public static void generate(String statuses) {

        STATUSES_LIST.clear();

        if (!statuses.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(statuses);

                StatusModel statusModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    statusModel = new StatusModel();

                    statusModel.setStatusDescription(MyJsonParser.getStringValue(oneObject, "StatusDescription", ""));
                    statusModel.setStatusId(MyJsonParser.getStringValue(oneObject, "StatusId", ""));
                    statusModel.setIsPending(MyJsonParser.getIntValue(oneObject, "IsPending", -1));
                    statusModel.setIsRollback(MyJsonParser.getIntValue(oneObject, "IsRollback", -1));
                    statusModel.setMandatorySteps(MyJsonParser.getIntValue(oneObject, "MandatorySteps", -1));

                    STATUSES_LIST.add(statusModel);
                }

                Collections.sort(STATUSES_LIST, (a, b) -> a.getStatusDescription().compareTo(b.getStatusDescription()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
