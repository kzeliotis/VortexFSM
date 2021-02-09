package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import am.gtest.vortex.models.ResourceLeaveModel;
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

                    if(oneObject.has("Leaves")){
                        String resourceLeaves_str = MyJsonParser.getStringValue(oneObject, "Leaves", "");
                        JSONArray rLeaves = new JSONArray(resourceLeaves_str);
                        ResourceLeaveModel rl_model;
                        List<ResourceLeaveModel> resourceleaveList = new ArrayList<>();
                        for(int n = 0; n < rLeaves.length(); n++){
                            JSONObject leave = rLeaves.getJSONObject(n);
                            rl_model = new ResourceLeaveModel();
                            rl_model.setLeaveStart(MyJsonParser.getStringValue(leave, "LeaveStart", ""));
                            rl_model.setLeaveEnd(MyJsonParser.getStringValue(leave, "LeaveEnd", ""));

                            resourceleaveList.add(rl_model);
                        }

                        userPartnerResourceModel.setLeaves(resourceleaveList);
                    }


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
