package am.gtest.vortex.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

import am.gtest.vortex.models.CompanyModel;
import am.gtest.vortex.support.MyJsonParser;

import static am.gtest.vortex.support.MyGlobals.COMPANIES_LIST;
import static am.gtest.vortex.support.MyGlobals.COMPANIES_LIST_FILTERED;
import static am.gtest.vortex.support.MyGlobals.SELECTED_COMPANY;

public class CompaniesData {

    public static void generate(String responseBody, String SingleSelection) {

        COMPANIES_LIST.clear();

        if (responseBody != null && !responseBody.isEmpty()) {
            try {
                JSONArray jArrayCompanies = new JSONArray(responseBody);

                CompanyModel companyModel;

                for (int i = 0; i < jArrayCompanies.length(); i++) {
                    JSONObject oneObject = jArrayCompanies.getJSONObject(i);

                    companyModel = new CompanyModel();

                    companyModel.setCompanyAddress(MyJsonParser.getStringValue(oneObject, "Address", ""));
                    companyModel.setCompanyBusiness(MyJsonParser.getStringValue(oneObject, "Business", ""));
                    companyModel.setCompanyBusinessTitle(MyJsonParser.getStringValue(oneObject, "BusinessTitle", ""));
                    companyModel.setCompanyCity(MyJsonParser.getStringValue(oneObject, "City", ""));
                    companyModel.setCompanyId(MyJsonParser.getStringValue(oneObject, "CustomerId", ""));
                    companyModel.setCompanyCode(MyJsonParser.getStringValue(oneObject, "Code", ""));
                    companyModel.setCompanyName(MyJsonParser.getStringValue(oneObject, "Company", ""));
                    companyModel.setCompanyContact(MyJsonParser.getStringValue(oneObject, "Contact", ""));
                    companyModel.setCompanyDiscount(MyJsonParser.getStringValue(oneObject, "Discount", ""));
                    companyModel.setCompanyEmail(MyJsonParser.getStringValue(oneObject, "Email", ""));
                    companyModel.setCompanyMobile(MyJsonParser.getStringValue(oneObject, "Mobile", ""));
                    companyModel.setCompanyRevenue(MyJsonParser.getStringValue(oneObject, "Revenue", ""));
                    companyModel.setCompanyTel(MyJsonParser.getStringValue(oneObject, "Tel", ""));
                    companyModel.setCompanyVatNumber(MyJsonParser.getStringValue(oneObject, "VatNumber", ""));
                    companyModel.setCompanyZip(MyJsonParser.getStringValue(oneObject, "Zip", ""));
                    companyModel.setCompanyProjects(MyJsonParser.getStringValue(oneObject, "Projects", ""));


                    if (SingleSelection.length() > 0){
                        SELECTED_COMPANY.clearModel();
                        SELECTED_COMPANY = companyModel;
                    } else {
                    COMPANIES_LIST.add(companyModel);
                    Collections.sort(COMPANIES_LIST, (a, b) -> a.getCompanyName().compareTo(b.getCompanyName()));
                    }

                }

                if(SingleSelection.length() == 0){
                    COMPANIES_LIST_FILTERED.clear();
                    COMPANIES_LIST_FILTERED.addAll(COMPANIES_LIST);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
