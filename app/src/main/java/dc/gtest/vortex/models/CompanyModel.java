package dc.gtest.vortex.models;

public class CompanyModel {

    private String companyAddress = "";
    private String companyBusiness = "";
    private String companyBusinessTitle = "";
    private String companyCity = "";
    private String companyId = "";
    private String companyCode = "";
    private String companyName = "";
    private String companyContact = "";
    private String companyDiscount = "";
    private String companyEmail = "";
    private String companyMobile = "";
    private String companyRevenue = "";
    private String companyTel = "";
    private String companyVatNumber = "";
    private String companyZip = "";
    private String companyProjects = "";

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyBusiness() {
        return companyBusiness;
    }

    public void setCompanyBusiness(String companyBusiness) {
        this.companyBusiness = companyBusiness;
    }

    public String getCompanyBusinessTitle() {
        return companyBusinessTitle;
    }

    public void setCompanyBusinessTitle(String companyBusinessTitle) {
        this.companyBusinessTitle = companyBusinessTitle;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyContact() {
        return companyContact;
    }

    public void setCompanyContact(String companyContact) {
        this.companyContact = companyContact;
    }

    public String getCompanyDiscount() {
        return companyDiscount;
    }

    public void setCompanyDiscount(String companyDiscount) {
        this.companyDiscount = companyDiscount;
    }

    public void clearModel(){
        this.companyAddress = "";
        this.companyBusiness = "";
        this.companyBusinessTitle = "";
        this.companyCity = "";
        this.companyId = "";
        this.companyCode = "";
        this.companyName = "";
        this.companyContact = "";
        this.companyDiscount = "";
        this.companyEmail = "";
        this.companyMobile = "";
        this.companyRevenue = "";
        this.companyTel = "";
        this.companyVatNumber = "";
        this.companyZip = "";
        this.companyProjects = "";
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyMobile() {
        return companyMobile;
    }

    public void setCompanyMobile(String companyMobile) {
        this.companyMobile = companyMobile;
    }

    public String getCompanyRevenue() {
        return companyRevenue;
    }

    public void setCompanyRevenue(String companyRevenue) {
        this.companyRevenue = companyRevenue;
    }

    public String getCompanyTel() {
        return companyTel;
    }

    public void setCompanyTel(String companyTel) {
        this.companyTel = companyTel;
    }

    public String getCompanyVatNumber() {
        return companyVatNumber;
    }

    public void setCompanyVatNumber(String companyVatNumber) {
        this.companyVatNumber = companyVatNumber;
    }

    public String getCompanyZip() {
        return companyZip;
    }

    public void setCompanyZip(String companyZip) {
        this.companyZip = companyZip;
    }

    public String getCompanyProjects() {
        return companyProjects;
    }

    public void setCompanyProjects(String companyProjects) {
        this.companyProjects = companyProjects;
    }

    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"Address\": \"" + companyAddress + "\",\n" +
                        "  \"Business\": \"" + companyBusiness + "\",\n" +
                        "  \"BusinessTitle\": \"" + companyBusinessTitle + "\",\n" +
                        "  \"City\": \"" + companyCity + "\",\n" +
                        "  \"Code\": \"" + companyId + "\",\n" +
                        "  \"Company\": \"" + companyName + "\",\n" +
                        "  \"Contact\": \"" + companyContact + "\",\n" +
                        "  \"Discount\": \"" + companyDiscount + "\",\n" +
                        "  \"Email\": \"" + companyEmail + "\",\n" +
                        "  \"Mobile\": \"" + companyMobile + "\",\n" +
                        "  \"Revenue\": \"" + companyRevenue + "\",\n" +
                        "  \"Tel\": \"" + companyTel + "\",\n" +
                        "  \"VatNumber\": \"" + companyVatNumber + "\",\n" +
                        "  \"Zip\": \"" + companyZip + "\",\n" +
                        "  \"Projects\": \"" + companyProjects + "\"\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }


}
