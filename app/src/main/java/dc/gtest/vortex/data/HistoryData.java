package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dc.gtest.vortex.models.AttachmentModel;
import dc.gtest.vortex.models.HAssignmentModel;
import dc.gtest.vortex.models.HConsumableModel;
import dc.gtest.vortex.models.HMeasurementModel;
import dc.gtest.vortex.models.HMeasurementValueModel;
import dc.gtest.vortex.support.MyJsonParser;

import static dc.gtest.vortex.support.MyGlobals.HISTORY_LIST;

public class HistoryData {

    public static void generate(String history) {

        try {
            JSONArray jArrayDataFromApi = new JSONArray(history);

            HAssignmentModel hAssignmentModel;

            HISTORY_LIST.clear();

            for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                hAssignmentModel = new HAssignmentModel();

                hAssignmentModel.setDateStart(MyJsonParser.getStringValue(oneObject, "DateStart", ""));
                hAssignmentModel.setHAssignmentId(MyJsonParser.getStringValue(oneObject, "HAssignmentId", ""));
                hAssignmentModel.setResourceName(MyJsonParser.getStringValue(oneObject, "ResourceName", ""));
                hAssignmentModel.setProduct(MyJsonParser.getStringValue(oneObject, "Product", ""));
                hAssignmentModel.setService(MyJsonParser.getStringValue(oneObject, "Service", ""));
                hAssignmentModel.setProblem(MyJsonParser.getStringValue(oneObject, "Problem", ""));
                hAssignmentModel.setSolution(MyJsonParser.getStringValue(oneObject, "Solution", ""));
                hAssignmentModel.setProjectHistory(MyJsonParser.getBooleanValue(oneObject, "ProjectHistory", true));




                JSONArray jArrayMeasurements = MyJsonParser.getJsonArrayValue(oneObject, "HMeasurements");

                List<HMeasurementModel> hMeasurementModelList = new ArrayList<>();

                for (int j = 0; j < jArrayMeasurements.length(); j++) {
                    JSONObject jObjectOneMeasurement = jArrayMeasurements.getJSONObject(j);

                    HMeasurementModel hMeasurementModel = new HMeasurementModel();

                    hMeasurementModel.setProduct(MyJsonParser.getStringValue(jObjectOneMeasurement, "Product", ""));

                    JSONArray jArrayMeasurementValue = MyJsonParser.getJsonArrayValue(jObjectOneMeasurement, "HMeasurementValues");

                    List<HMeasurementValueModel> hMeasurementValueModelList = new ArrayList<>();

                    for (int h = 0; h < jArrayMeasurementValue.length(); h++) {
                        JSONObject jObjectMeasurementValue = jArrayMeasurementValue.getJSONObject(h);

                        HMeasurementValueModel hMeasurementValueModel = new HMeasurementValueModel();

                        hMeasurementValueModel.setMeasurableAttribute(MyJsonParser.getStringValue(jObjectMeasurementValue, "MeasurableAttribute", ""));
                        hMeasurementValueModel.setMeasurementValue(MyJsonParser.getStringValue(jObjectMeasurementValue, "MeasurementValue", ""));

                        hMeasurementValueModelList.add(hMeasurementValueModel);
                    }

                    hMeasurementModel.setHMeasurementValues(hMeasurementValueModelList);

                    hMeasurementModelList.add(hMeasurementModel);
                }

                hAssignmentModel.setHMeasurements(hMeasurementModelList);




                JSONArray jArrayConsumables = MyJsonParser.getJsonArrayValue(oneObject, "HConsumables");

                List<HConsumableModel> hConsumableModelList = new ArrayList<>();

                for (int h = 0; h < jArrayConsumables.length(); h++) {
                    JSONObject jObjectOneConsumable = jArrayConsumables.getJSONObject(h);

                    HConsumableModel hConsumableModel = new HConsumableModel();

                    hConsumableModel.setProduct(MyJsonParser.getStringValue(jObjectOneConsumable, "Product", ""));
                    hConsumableModel.setQuantity(MyJsonParser.getStringValue(jObjectOneConsumable, "Quantity", ""));

                    hConsumableModelList.add(hConsumableModel);
                }

                hAssignmentModel.setHConsumables(hConsumableModelList);


                JSONArray jArrayAttachments = MyJsonParser.getJsonArrayValue(oneObject, "Attachments");
                List<AttachmentModel> hAttachmentsList = new ArrayList<>();
                for (int a = 0; a < jArrayAttachments.length(); a++){
                    JSONObject jObjectOneAttachment = jArrayAttachments.getJSONObject(a);

                    AttachmentModel hattachmentModel = new AttachmentModel();

                    hattachmentModel.setObjectId(MyJsonParser.getStringValue(jObjectOneAttachment, "ObjectId", "0"));
                    hattachmentModel.setObjectType(MyJsonParser.getStringValue(jObjectOneAttachment, "ObjectType", ""));
                    hattachmentModel.setFilename(MyJsonParser.getStringValue(jObjectOneAttachment, "Filename", ""));
                    hattachmentModel.setCloudFileURL(MyJsonParser.getStringValue(jObjectOneAttachment, "CloudFileURL", ""));
                    hattachmentModel.setAttachmentId(MyJsonParser.getStringValue(jObjectOneAttachment, "AttachmentId", "0"));
                    hattachmentModel.setBlobAttachmentId(MyJsonParser.getStringValue(jObjectOneAttachment, "BlobAttachmentId", "0"));

                    hAttachmentsList.add(hattachmentModel);
                }

                hAssignmentModel.setHAttachments(hAttachmentsList);


                HISTORY_LIST.add(hAssignmentModel);
            }

//            Collections.sort(HISTORY_LIST, (a, b) -> a.getHAssignmentId().compareTo(b.getHAssignmentId()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
