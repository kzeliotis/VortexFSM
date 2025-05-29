package dc.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import dc.gtest.vortex.models.AllConsumableModel;
import dc.gtest.vortex.models.AllProductModel;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyGlobals.ALL_CONSUMABLES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_CONSUMABLES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_CONSUMABLES_LIST;
import static dc.gtest.vortex.support.MyGlobals.ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED;
import static dc.gtest.vortex.support.MyGlobals.PICKING_PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.PICKING_PRODUCTS_LIST_FILTERED;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ALL_CONSUMABLES;
import static dc.gtest.vortex.support.MyPrefs.PREF_DATA_ALL_WAREHOUSE_CONSUMABLES;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_PICKING_LIST_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_CONSUMABLES_FOR_SHOW;
import static dc.gtest.vortex.support.MyPrefs.PREF_FILE_RELATED_WAREHOUSE_CONSUMABLES_FOR_SHOW;

public class AllConsumablesData {

    public static void generate(String AssignmentId, boolean warehouseProducts, boolean pickingList, String _warehouseId) {

        if(warehouseProducts) {
            ALL_WAREHOUSE_CONSUMABLES_LIST.clear();
            ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED.clear();
            String Consumables = MyPrefs.getStringWithFileName(PREF_FILE_RELATED_WAREHOUSE_CONSUMABLES_FOR_SHOW, _warehouseId, "");
            //String allConsumables = MyPrefs.getString(PREF_DATA_ALL_CONSUMABLES, "");
            if (Consumables.isEmpty()) {
                Consumables = MyPrefs.getString(PREF_DATA_ALL_WAREHOUSE_CONSUMABLES, "");
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
                        allConsumableModel.setStock(MyJsonParser.getStringValue(oneObject, "Stock", "0"));

                        ALL_WAREHOUSE_CONSUMABLES_LIST.add(allConsumableModel);
                    }

                    Collections.sort(ALL_WAREHOUSE_CONSUMABLES_LIST, (a, b) -> a.getConsumableName().compareTo(b.getConsumableName()));

                    ALL_WAREHOUSE_CONSUMABLES_LIST_FILTERED.addAll(ALL_WAREHOUSE_CONSUMABLES_LIST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if (pickingList){
            PICKING_PRODUCTS_LIST.clear();
            PICKING_PRODUCTS_LIST_FILTERED.clear();

            String pickingProducts = MyPrefs.getStringWithFileName(PREF_FILE_PICKING_LIST_FOR_SHOW, AssignmentId, "");

            if (!pickingProducts.isEmpty()) {
                try {
                    JSONArray jArrayDataFromApi = new JSONArray(pickingProducts);

                    AllConsumableModel allConsumableModel;

                    for (int i = 0; i < jArrayDataFromApi.length(); i++) {
                        JSONObject oneObject = jArrayDataFromApi.getJSONObject(i);

                        allConsumableModel = new AllConsumableModel();

                        allConsumableModel.setConsumableName(MyJsonParser.getStringValue(oneObject, "ProductDescription", ""));
                        allConsumableModel.setNotes(MyJsonParser.getStringValue(oneObject, "notes", ""));
                        allConsumableModel.setTypeId(MyJsonParser.getIntValue(oneObject, "TypeId", -1));
                        allConsumableModel.setProductId(MyJsonParser.getIntValue(oneObject, "ProductId", 0));
                        allConsumableModel.setStock(MyJsonParser.getStringValue(oneObject, "Stock", "0"));
                        allConsumableModel.setDetPickingId(MyJsonParser.getIntValue(oneObject, "DetPickingId", 0));

                        PICKING_PRODUCTS_LIST.add(allConsumableModel);
                    }

                    Collections.sort(PICKING_PRODUCTS_LIST, (a, b) -> a.getConsumableName().compareTo(b.getConsumableName()));

                    PICKING_PRODUCTS_LIST_FILTERED.addAll(PICKING_PRODUCTS_LIST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
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
                        allConsumableModel.setStock(MyJsonParser.getStringValue(oneObject, "Stock", ""));

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
}
