package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import am.gtest.vortex.models.UserPartnerResourceModel;
import am.gtest.vortex.support.MyJsonParser;

import static am.gtest.vortex.support.MyGlobals.SERVICES_LIST;
import static am.gtest.vortex.support.MyGlobals.USER_PARTNER_RESOURCE_LIST;

public class UserPartnerResourcesData {

    public static void generate(String userPartnerResources) {

        USER_PARTNER_RESOURCE_LIST.clear();

        if (!userPartnerResources.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(userPartnerResources);

                UserPartnerResourceModel userPartnerResourceModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    userPartnerResourceModel = new UserPartnerResourceModel();

                    userPartnerResourceModel.setResourceId(MyJsonParser.getStringValue(oneObject, "ResourceId", ""));
                    userPartnerResourceModel.setResourceName(MyJsonParser.getStringValue(oneObject, "ResourceName", ""));

                    USER_PARTNER_RESOURCE_LIST.add(userPartnerResourceModel);
                }

                //Collections.sort(SERVICES_LIST, (a, b) -> a.getServiceDescription().compareTo(b.getServiceDescription()));

                Collections.sort(USER_PARTNER_RESOURCE_LIST, (a, b) -> a.getResourceName().compareTo(b.getResourceName()));


//                Log.e("myLogs: ServicesData", "===================== SERVICES_LIST_FILTERED:\n" + SERVICES_LIST_FILTERED.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
