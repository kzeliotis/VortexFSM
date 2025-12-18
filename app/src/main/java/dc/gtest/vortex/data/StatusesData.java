package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import dc.gtest.vortex.models.StatusModel;
import dc.gtest.vortex.support.MyJsonParser;

import static dc.gtest.vortex.support.MyGlobals.ALL_STATUSES_LIST;
import static dc.gtest.vortex.support.MyGlobals.STATUSES_LIST;

public class StatusesData {

    public static void generate(String statuses, boolean all) {

        if(all){
            ALL_STATUSES_LIST.clear();
        }else{
            STATUSES_LIST.clear();
        }

        if (statuses != null && !statuses.isEmpty()) {
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
                    statusModel.setMandatoryMinimumPayment(MyJsonParser.getIntValue(oneObject, "MandatoryMinimumPayment", -1));

                    if(all){
                        ALL_STATUSES_LIST.add(statusModel);
                    }else{
                        STATUSES_LIST.add(statusModel);
                    }

                }

                if(all){
                    Collections.sort(ALL_STATUSES_LIST, (a, b) -> a.getStatusDescription().compareTo(b.getStatusDescription()));
                }else{
                    Collections.sort(STATUSES_LIST, (a, b) -> a.getStatusDescription().compareTo(b.getStatusDescription()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
