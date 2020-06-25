package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.gtest.vortex.models.AttributeModel;
import am.gtest.vortex.models.MeasurementModel;
import am.gtest.vortex.models.ProductModel;
import am.gtest.vortex.support.MyJsonParser;

import static am.gtest.vortex.support.MyGlobals.ALL_ATTRIBUTES_LIST;
import static am.gtest.vortex.support.MyGlobals.MANDATORY_MEASUREMENTS_LIST;
import static am.gtest.vortex.support.MyGlobals.PRODUCTS_LIST;

public class ProductsData {

    public static void generate(String products) {

        if (!products.equals("")) {
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
                    productModel.setProductAttributesString(MyJsonParser.getStringValue(oneObjectProduct, "ProductAttributesString", ""));

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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
