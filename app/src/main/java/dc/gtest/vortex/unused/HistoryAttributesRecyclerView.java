package dc.gtest.vortex.unused;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dc.gtest.vortex.R;

public class HistoryAttributesRecyclerView {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private static List<NewProductsList> ITEMS;

    public void setupRecyclerView(@NonNull final RecyclerView recyclerView, String attributes) {

        ITEMS = new ArrayList<>();
        ITEMS.clear();

        attributes = "{\"attributes\":" + attributes + "}";

        try {
            JSONObject jObjectAttributes = new JSONObject(attributes);

            Log.e(LOG_TAG, "jObjectAttributes: \n" + jObjectAttributes.toString(2));

            if (jObjectAttributes.optJSONArray("attributes") != null &&
                    jObjectAttributes.optJSONArray("attributes").length() > 0) {

                JSONArray jArrayConsumables = jObjectAttributes.getJSONArray("attributes");

                JSONArray sortedJsonArray = new JSONArray();
                List<JSONObject> jsonValues = new ArrayList<>();

                for (int i = 0; i < jArrayConsumables.length(); i++) {
                    try {
                        jsonValues.add(jArrayConsumables.getJSONObject(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Collections.sort(jsonValues, new Comparator<JSONObject>() {
                    //You can change "Name" with "ID" if you want to sort by ID
                    private static final String KEY_NAME = "MeasurableAttribute";

                    @Override
                    public int compare(JSONObject a, JSONObject b) {
                        String valA = "";
                        String valB = "";

                        try {
                            valA = (String) a.get(KEY_NAME);
                            valB = (String) b.get(KEY_NAME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return valA.compareTo(valB);
                        //if you want to change the sort order, simply use the following: return -valA.compareTo(valB);
                    }
                });

                for (int i = 0; i < jArrayConsumables.length(); i++) {
                    sortedJsonArray.put(jsonValues.get(i));
                }

                for (int i = 0; i < sortedJsonArray.length(); i++) {
                    try {
                        JSONObject oneObjectOptionProduct = sortedJsonArray.getJSONObject(i);
                        addItem(createDummyItem(oneObjectOptionProduct));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<NewProductsList> mValues;

        public SimpleItemRecyclerViewAdapter(List<NewProductsList> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_attribute, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            String attributeNameWithColon = mValues.get(position).attributeName + ": ";
            holder.tvAttributeName.setText(attributeNameWithColon);
            holder.tvAttributeValue.setText(mValues.get(position).attributeValue);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView tvAttributeName;
            public final TextView tvAttributeValue;
            public NewProductsList mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvAttributeName = view.findViewById(R.id.tvAttributeName);
                tvAttributeValue = view.findViewById(R.id.tvAttributeValue);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + tvAttributeName.getText() + "'";
            }
        }
    }




    private static void addItem(NewProductsList item) {
        ITEMS.add(item);
    }

    private static NewProductsList createDummyItem(JSONObject oneObjectPriceData) {
        String attributeName = null;
        String attributeValue = null;

        try {
            attributeName = oneObjectPriceData.getString("MeasurableAttribute");
            attributeValue = oneObjectPriceData.getString("MeasurementValue");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NewProductsList(attributeName, attributeValue);
    }

    public static class NewProductsList {
        public final String attributeName;
        public final String attributeValue;

        public NewProductsList(String attributeName, String attributeValue) {
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
        }

        @Override
        public String toString() {
            return attributeName + ", " + attributeValue;
        }
    }
}
