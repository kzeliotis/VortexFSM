package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import dc.gtest.vortex.models.ServiceModel;
import dc.gtest.vortex.support.MyJsonParser;

import static dc.gtest.vortex.support.MyGlobals.RELATED_SERVICES_LIST;
import static dc.gtest.vortex.support.MyGlobals.RELATED_SERVICES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_FOR_NEW_ASSIGNMENT_LIST;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_LIST;
import static dc.gtest.vortex.support.MyGlobals.SERVICES_LIST_FILTERED;

public class ServicesData {

    public static void generate(String services, String AssignmentId, boolean isForNewAssignment) {

        if (AssignmentId == "0" && !isForNewAssignment) {
            SERVICES_LIST.clear();
        }else if (isForNewAssignment){
            SERVICES_FOR_NEW_ASSIGNMENT_LIST.clear();
        }else{
            RELATED_SERVICES_LIST.clear();
        }

        if (!services.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(services);

                ServiceModel serviceModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    serviceModel = new ServiceModel();

                    serviceModel.setServiceId(MyJsonParser.getStringValue(oneObject, "ServiceId", ""));
                    serviceModel.setServiceCode(MyJsonParser.getStringValue(oneObject, "ServiceCode", ""));
                    serviceModel.setServiceDescription(MyJsonParser.getStringValue(oneObject, "ServiceDescription", ""));
                    serviceModel.setServiceDuration(MyJsonParser.getStringValue(oneObject, "ServiceDuration", ""));

                    if (AssignmentId == "0" && !isForNewAssignment) {
                        SERVICES_LIST.add(serviceModel);
                    }else if (isForNewAssignment){
                        SERVICES_FOR_NEW_ASSIGNMENT_LIST.add(serviceModel);
                    } else {
                        RELATED_SERVICES_LIST.add(serviceModel);
                    }
                }

                if (AssignmentId == "0" && !isForNewAssignment) {
                    Collections.sort(SERVICES_LIST, (a, b) -> a.getServiceDescription().compareTo(b.getServiceDescription()));
                }else if (isForNewAssignment){
                    Collections.sort(SERVICES_FOR_NEW_ASSIGNMENT_LIST, (a, b) -> a.getServiceDescription().compareTo(b.getServiceDescription()));
                }else{
                    Collections.sort(RELATED_SERVICES_LIST, (a, b) -> a.getServiceDescription().compareTo(b.getServiceDescription()));
                }

                if (AssignmentId == "0" && !isForNewAssignment) {
                    SERVICES_LIST_FILTERED.clear();
                    SERVICES_LIST_FILTERED.addAll(SERVICES_LIST);
                }else if (isForNewAssignment){
                    SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED.clear();
                    SERVICES_FOR_NEW_ASSIGNMENT_LIST_FILTERED.addAll(SERVICES_FOR_NEW_ASSIGNMENT_LIST);
                }else{
                    RELATED_SERVICES_LIST_FILTERED.clear();
                    RELATED_SERVICES_LIST_FILTERED.addAll(SERVICES_LIST);
                }

//                Log.e("myLogs: ServicesData", "===================== SERVICES_LIST_FILTERED:\n" + SERVICES_LIST_FILTERED.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
