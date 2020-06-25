package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import am.gtest.vortex.models.ProductTypeModel;
import am.gtest.vortex.support.MyJsonParser;

import static am.gtest.vortex.support.MyGlobals.PRODUCT_TYPES_LIST;

public class ProductTypesData {

    public static void generate(String productTypes) {

        PRODUCT_TYPES_LIST.clear();

        if (!productTypes.isEmpty()) {
            try {
                JSONArray jArrayDataFromApi = new JSONArray(productTypes);

                ProductTypeModel productTypeModel;

                for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                    JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                    productTypeModel = new ProductTypeModel();

                    productTypeModel.setTypeId(MyJsonParser.getStringValue(oneObject, "TypeId", ""));
                    productTypeModel.setTypeColor(MyJsonParser.getStringValue(oneObject, "color", ""));
                    productTypeModel.setTypeDescription(MyJsonParser.getStringValue(oneObject, "TypeDescription", ""));

                    PRODUCT_TYPES_LIST.add(productTypeModel);
                }

                Collections.sort(PRODUCT_TYPES_LIST, (a, b) -> a.getTypeDescription().compareTo(b.getTypeDescription()));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
