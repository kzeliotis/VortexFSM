package dc.gtest.vortex.data;

import com.amrdeveloper.treeview.TreeNode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dc.gtest.vortex.models.AttributeModel;
import dc.gtest.vortex.models.MeasurementModel;
import dc.gtest.vortex.models.ProductModel;
import dc.gtest.vortex.support.MyJsonParser;
import dc.gtest.vortex.support.MyPrefs;

import static dc.gtest.vortex.support.MyGlobals.ALL_ATTRIBUTES_LIST;
import static dc.gtest.vortex.support.MyGlobals.MANDATORY_MEASUREMENTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.PRODUCTS_LIST;
import static dc.gtest.vortex.support.MyGlobals.PRODUCTS_TREE_LIST;
import static dc.gtest.vortex.support.MyGlobals.SELECTED_ASSIGNMENT;
import static dc.gtest.vortex.support.MyLocalization.localized_zone;
import static dc.gtest.vortex.support.MyPrefs.PREF_SEND_INSTALLED_PRODUCTS_ON_CHECKOUT;
import static dc.gtest.vortex.support.MyPrefs.PREF_UNSYNCED_INSTALLATION_INSTALLED_PRODUCTS;
import static dc.gtest.vortex.support.MyPrefs.PREF_UNSYNCED_INSTALLED_PRODUCTS;

public class ProductsData {

    public static void generate(String products, String assignmentId, String installationId) {

        if(installationId.equals("0")){installationId = "";}
        if(assignmentId.equals("0")){assignmentId = "";}

        if (!products.equals("")) {

            boolean sendOnCheckOut = MyPrefs.getBoolean(PREF_SEND_INSTALLED_PRODUCTS_ON_CHECKOUT, false);
            if (sendOnCheckOut && (!assignmentId.isEmpty() || !installationId.isEmpty())) {
                List<ProductModel> pl = new ArrayList<ProductModel>();
                if(!assignmentId.isEmpty()){
                    pl = MyPrefs.loadListWithFileName(PREF_UNSYNCED_INSTALLED_PRODUCTS, assignmentId, ProductModel.class);
                }else if (!installationId.isEmpty()){
                    pl = MyPrefs.loadListWithFileName(PREF_UNSYNCED_INSTALLATION_INSTALLED_PRODUCTS, installationId, ProductModel.class);
                }

                for (ProductModel pm : pl) {
                    if (products.length() > 0 && !products.equals("[]")) {
                        products = products.substring(0, products.length() - 1) + "," + pm.toString() + "]";
                    } else {
                        products = "[" + pm.toString() + "]";
                    }
                }
            }


            try {
                JSONArray jArrayProjectProducts = new JSONArray(products);

                ProductModel productModel;
                MeasurementModel measurementModel;

                PRODUCTS_LIST.clear();

                for (int i = 0; i < jArrayProjectProducts.length(); i++) {
                    JSONObject oneObjectProduct = jArrayProjectProducts.getJSONObject(i);

                    productModel = new ProductModel();
                    measurementModel = new MeasurementModel();

                    String ProjectProductId = MyJsonParser.getStringValue(oneObjectProduct, "ProjectProductId", "");

                    productModel.setProductDescription(MyJsonParser.getStringValue(oneObjectProduct, "ProductDescription", ""));
                    productModel.setInstallationDate(MyJsonParser.getStringValue(oneObjectProduct, "InstallationDate", ""));
                    productModel.setProjectProductId(MyJsonParser.getStringValue(oneObjectProduct, "ProjectProductId", ""));
                    productModel.setTypeDescription(MyJsonParser.getStringValue(oneObjectProduct, "TypeDescription", ""));
                    productModel.setIdentityValue(MyJsonParser.getStringValue(oneObjectProduct, "IdentityValue", ""));
                    productModel.setNotSynchronized(MyJsonParser.getBooleanValue(oneObjectProduct, "isNotSynchronized", false)); // not server field
                    String attributeString = MyJsonParser.getStringValue(oneObjectProduct, "ProductAttributesString", "");
                    attributeString = attributeString.replace("{Zone}", localized_zone);
                    productModel.setProductAttributesString(attributeString);
                    productModel.setProjectInstallationId(MyJsonParser.getStringValue(oneObjectProduct, "InstallationId", "0"));
                    productModel.setMasterId(MyJsonParser.getStringValue(oneObjectProduct, "MasterId", "0"));
                    productModel.setNotes(MyJsonParser.getStringValue(oneObjectProduct, "notes", ""));
                    productModel.setProductComponentId(MyJsonParser.getStringValue(oneObjectProduct, "ProductComponentId", "0"));
                    productModel.setProjectZoneDescription(MyJsonParser.getStringValue(oneObjectProduct, "ProjectZoneDescription", ""));
                    productModel.setProjectInstallationDescription(MyJsonParser.getStringValue(oneObjectProduct, "ProjectInstallationDescription", ""));

                    JSONArray jArrayProductAttributes = new JSONArray();
                    JSONArray jArrayMandatoryAttributes = new JSONArray();

                    if (oneObjectProduct.has("ProductAttributeValues")) {
                        jArrayProductAttributes = MyJsonParser.getJsonArrayValue(oneObjectProduct, "ProductAttributeValues");
                    } else if (oneObjectProduct.has("AttributeValues")) {
                        jArrayProductAttributes = MyJsonParser.getJsonArrayValue(oneObjectProduct, "AttributeValues");
                    }

                    if (oneObjectProduct.has("MandatoryForRemoval")) {
                        jArrayMandatoryAttributes = MyJsonParser.getJsonArrayValue(oneObjectProduct, "MandatoryForRemoval");
                    }

                    AttributeModel attributeModel;
                    List<AttributeModel> productAttributes = new ArrayList<>();
                    List<MeasurementModel> MandatoryAttributes = new ArrayList<>();

                    for (int j = 0; j < jArrayProductAttributes.length(); j++) {
                        JSONObject oneObject = jArrayProductAttributes.getJSONObject(j);

                        attributeModel = new AttributeModel();

                        String attributeId = MyJsonParser.getStringValue(oneObject, "AttributeId", "");

                        attributeModel.setAttributeId(attributeId);
                        attributeModel.setAttributeDescription(MyJsonParser.getStringValue(oneObject, "AttributeDescription", ""));
                        attributeModel.setAttributeValue(MyJsonParser.getStringValue(oneObject, "Value", ""));
                        attributeModel.setValueId(MyJsonParser.getStringValue(oneObject,  "ValueId", ""));
                        int IsDateTime = MyJsonParser.getIntValue(oneObject, "IsDateTime", 0);
                        attributeModel.setDateTime(IsDateTime == 1);

                        attributeModel.setProjectProductId(MyJsonParser.getStringValue(oneObject, "ProjectProductId", ""));

                        JSONArray attributeDefaultValues = new JSONArray();

                        AllAttributesData.generate();

                        for (int h = 0; h < ALL_ATTRIBUTES_LIST.size(); h++) {
                            if (ALL_ATTRIBUTES_LIST.get(h).getAttributeId().equalsIgnoreCase(attributeId)) {
                                attributeDefaultValues = ALL_ATTRIBUTES_LIST.get(h).getAttributeDefaultValues();
                            }
                        }

                        attributeModel.setAttributeDefaultValues(attributeDefaultValues);

                        productAttributes.add(attributeModel);
                    }

                    Collections.sort(productAttributes, (a, b) -> a.getAttributeDescription().compareTo(b.getAttributeDescription()));

                    for (int j = 0; j < jArrayMandatoryAttributes.length(); j++) {
                        JSONObject oneObject = jArrayMandatoryAttributes.getJSONObject(j);

                        measurementModel = new MeasurementModel();

                        String MeasurableAttribute = MyJsonParser.getStringValue(oneObject, "MeasurementAttribute", "");

                        measurementModel.setAttributeName(MeasurableAttribute);
                        //measurementModel.setAttributeValue(MyJsonParser.getStringValue(oneObject, "Value", ""));
                        measurementModel.setProjectProductId(MyJsonParser.getStringValue(oneObject, "ProjectProductId", ""));

                        MandatoryAttributes.add(measurementModel);

                        boolean MandatoryMeasurementExists = false;
                        for (int m = 0; m < MANDATORY_MEASUREMENTS_LIST.size(); m++) {
                            if (MANDATORY_MEASUREMENTS_LIST.get(m).getAttributeName().equalsIgnoreCase(MeasurableAttribute) && MANDATORY_MEASUREMENTS_LIST.get(m).getProjectProductId().equalsIgnoreCase(ProjectProductId )) {
                                MandatoryMeasurementExists = true;
                            }
                        }

                        if (!MandatoryMeasurementExists) {MANDATORY_MEASUREMENTS_LIST.add(measurementModel);}

                    }

                    productModel.setProductAttributes(productAttributes);
                    productModel.setRemoveFromProjectMandatory(MandatoryAttributes);

                    PRODUCTS_LIST.add(productModel);
                }

                Collections.sort(PRODUCTS_LIST, (a, b) -> a.getProductDescription().compareTo(b.getProductDescription()));

                try {
                   generateTree(PRODUCTS_LIST);
                } catch (Exception e){
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void generateTree(List<ProductModel> products) {

        PRODUCTS_TREE_LIST.clear();

        List<TreeNode> roots = new ArrayList<>();
        List<TreeNode> allNodes = new ArrayList<>();
        Map<Integer, Integer> parentIndexMap = new HashMap<>();
        List<ProductModel> inserted = new ArrayList<>();

        int cycle = 1;
        while (products.size() != inserted.size()){

            if(cycle > 10000){
                PRODUCTS_TREE_LIST.clear();
                return;
            }

            for (ProductModel pm : products){

                if(inserted.contains(pm)){continue;}

                //int ProjectProductId = Integer.parseInt(pm.getProjectProductId());
                int MasterId = Integer.parseInt(pm.getMasterId());
                if(cycle == 1 && MasterId > 0){continue;}
                List<ProductModel> pmlist = new ArrayList<>();
                pmlist.add(pm);
                addNodesToList(pmlist, allNodes, inserted, roots, parentIndexMap, products);
            }

            cycle += 1;

        }

        for (TreeNode tn : allNodes){
            if(tn.getLevel() > 0){
                tn.setSelected(false);
            }
        }

        PRODUCTS_TREE_LIST.addAll(allNodes);

    }


    public static void addNodesToList(List<ProductModel> pmList, List<TreeNode> allNodes, List<ProductModel> inserted,
                                List<TreeNode> roots, Map<Integer, Integer> parentIndexMap, List<ProductModel> AllProducts){

        for (ProductModel pm : pmList){

            if(inserted.contains(pm)){continue;}
            int ProjectProductId = Integer.parseInt(pm.getProjectProductId());
            int MasterId = Integer.parseInt(pm.getMasterId());

            TreeNode treeNode = new TreeNode(pm, 0);

            treeNode.setSelected(true);

            Integer index = parentIndexMap.get(MasterId);
            if (index == null && MasterId > 0) {
                if(AllProducts.stream().filter(obj -> obj.getProjectProductId().equals(String.valueOf(MasterId))).collect(Collectors.toList()).size() > 0){
                    continue;
                }
            }

            if (index != null) {
                allNodes.get(index).addChild(treeNode);
            } else {
                roots.add(treeNode);
            }
            inserted.add(pm);
            allNodes.add(treeNode);
            parentIndexMap.put(ProjectProductId, allNodes.size() - 1);

            List<ProductModel> childPms = AllProducts.stream().filter(obj -> obj.getMasterId().equals(String.valueOf(ProjectProductId))).collect(Collectors.toList());

            addNodesToList(childPms, allNodes, inserted, roots, parentIndexMap, AllProducts);

        }

    }


}
