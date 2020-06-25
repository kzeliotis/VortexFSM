package am.gtest.vortex.models;

import android.support.annotation.NonNull;

public class ManualModel {

    private String manualName = "";
    private String manualUrl = "";
    private String manualKeywords = "";

    @NonNull
    @Override
    public String toString() {
        String modelToString =
                "{\n" +
                        "  \"manualName\": \"" + manualName + "\",\n" +
                        "  \"manualUrl\": \"" + manualUrl + "\",\n" +
                        "  \"manualKeywords\": \"" + manualKeywords + "\"\n" +
                        "}";

        modelToString = modelToString.replace("}]", "}\n  ]");

        return modelToString;
    }

    public String getManualName() {
        return manualName;
    }

    public void setManualName(String manualName) {
        this.manualName = manualName;
    }

    public String getManualUrl() {
        return manualUrl;
    }

    public void setManualUrl(String manualUrl) {
        this.manualUrl = manualUrl;
    }

    public String getManualKeywords() {
        return manualKeywords;
    }

    public void setManualKeywords(String manualKeywords) {
        this.manualKeywords = manualKeywords;
    }
}
