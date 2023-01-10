package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import dc.gtest.vortex.models.AllProductModel;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyGlobals.ALL_PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_PRODUCTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ALL_PRODUCTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_WAREHOUSE_PRODUCTS;

public class AllProductsData {

    public static void generate(boolean warehouseProducts) {


        if (warehouseProducts) {

            ALL_WAREHOUSE_PRODUCTS_LIST.clear();
            ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED.clear();

            String allProducts = MyPrefs.getString(PREF_DATA_WAREHOUSE_PRODUCTS, "");

            if (!allProducts.isEmpty()) {
                try {
                    JSONArray jArrayDataFromApi = new JSONArray(allProducts);

                    AllProductModel allProductModel;

                    for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                        JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                        allProductModel = new AllProductModel();

                        allProductModel.setProductId(MyJsonParser.getStringValue(oneObject, "ProductId", ""));
                        allProductModel.setProductDescription(MyJsonParser.getStringValue(oneObject, "ProductDescription", ""));
                        allProductModel.setNotes(MyJsonParser.getStringValue(oneObject, "notes", ""));
                        allProductModel.setTypeId(MyJsonParser.getIntValue(oneObject, "TypeId", -1));
                        allProductModel.setStock(MyJsonParser.getStringValue(oneObject, "Stock", ""));
                        allProductModel.setBasicValue(MyJsonParser.getStringValue(oneObject, "BasicValue", ""));
                        allProductModel.setProjectProductId(MyJsonParser.getStringValue(oneObject, "ProjectProductId", "0"));
                        allProductModel.setAttributeId(MyJsonParser.getStringValue(oneObject, "AttributeId", "0"));
                        allProductModel.setAttributeDescription(MyJsonParser.getStringValue(oneObject, "AttributeDescription", ""));

                        ALL_WAREHOUSE_PRODUCTS_LIST.add(allProductModel);
                    }

                    Collections.sort(ALL_WAREHOUSE_PRODUCTS_LIST, (a, b) -> a.getProductDescription().compareTo(b.getProductDescription()));

                    ALL_WAREHOUSE_PRODUCTS_LIST_FILTERED.addAll(ALL_WAREHOUSE_PRODUCTS_LIST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else {
            ALL_PRODUCTS_LIST.clear();
            ALL_PRODUCTS_LIST_FILTERED.clear();

            String allProducts = MyPrefs.getString(PREF_DATA_ALL_PRODUCTS, "");

            if (!allProducts.isEmpty()) {
                try {
                    JSONArray jArrayDataFromApi = new JSONArray(allProducts);

                    AllProductModel allProductModel;

                    for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                        JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                        allProductModel = new AllProductModel();

                        allProductModel.setProductId(MyJsonParser.getStringValue(oneObject, "ProductId", ""));
                        allProductModel.setProductDescription(MyJsonParser.getStringValue(oneObject, "ProductDescription", ""));
                        allProductModel.setNotes(MyJsonParser.getStringValue(oneObject, "notes", ""));
                        allProductModel.setTypeId(MyJsonParser.getIntValue(oneObject, "TypeId", -1));

                        ALL_PRODUCTS_LIST.add(allProductModel);
                    }

                    Collections.sort(ALL_PRODUCTS_LIST, (a, b) -> a.getProductDescription().compareTo(b.getProductDescription()));

                    ALL_PRODUCTS_LIST_FILTERED.addAll(ALL_PRODUCTS_LIST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
