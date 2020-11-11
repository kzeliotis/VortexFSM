/*
 * Copyright (c) 2016. Developed by GTest Development
 */

package am.gtest.vortex.unused;

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

import am.gtest.vortex.R;
import am.gtest.vortex.unused.HistoryAttributesRecyclerView;

public class HistoryProductsRecyclerView {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private static List<NewProductsList> ITEMS;

    public void setupRecyclerView(@NonNull final RecyclerView recyclerView, String measurements) {

        ITEMS = new ArrayList<>();
        ITEMS.clear();

        measurements = "{\"measurements\":" + measurements + "}";

        try {
            JSONObject jObjectMeasurements = new JSONObject(measurements);

            Log.e(LOG_TAG, "jObjectMeasurements: \n" + jObjectMeasurements.toString(2));

            if (jObjectMeasurements.optJSONArray("measurements") != null &&
                    jObjectMeasurements.optJSONArray("measurements").length() > 0) {

                JSONArray jArrayMeasurements = jObjectMeasurements.getJSONArray("measurements");

                JSONArray sortedJsonArray = new JSONArray();
                List<JSONObject> jsonValues = new ArrayList<>();

                for (int i = 0; i < jArrayMeasurements.length(); i++) {
                    try {
                        jsonValues.add(jArrayMeasurements.getJSONObject(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Collections.sort(jsonValues, new Comparator<JSONObject>() {
                    //You can change "Name" with "ID" if you want to sort by ID
                    private static final String KEY_NAME = "Product";

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

                for (int i = 0; i < jArrayMeasurements.length(); i++) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_product, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.tvProduct.setText(mValues.get(position).productName);

            HistoryAttributesRecyclerView historyAttributesRecyclerView = new HistoryAttributesRecyclerView();
            historyAttributesRecyclerView.setupRecyclerView(holder.rvAttributes,
                    mValues.get(position).attributes);

            holder.mView.setOnClickListener(v -> {

            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView tvProduct;
            public final RecyclerView rvAttributes;

            public NewProductsList mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvProduct = view.findViewById(R.id.tvProduct);
                rvAttributes = view.findViewById(R.id.rvAttributes);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + tvProduct.getText() + "'";
            }
        }
    }




    private static void addItem(NewProductsList item) {
        ITEMS.add(item);
    }

    private static NewProductsList createDummyItem(JSONObject oneObjectPriceData) {
        String productName = null;
        String attributes = null;

        try {
            productName = oneObjectPriceData.getString("Product");
            attributes = oneObjectPriceData.getString("HMeasurementValues");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NewProductsList(productName, attributes);
    }

    public static class NewProductsList {
        public final String productName;
        public final String attributes;

        public NewProductsList(String productName, String attributes) {
            this.productName = productName;
            this.attributes = attributes;
        }

        @Override
        public String toString() {
            return productName + ", " + attributes;
        }
    }
}