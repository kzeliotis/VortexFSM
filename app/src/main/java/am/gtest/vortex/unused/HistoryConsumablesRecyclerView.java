/*
 * Copyright (c) 2016. Developed by GTest Development
 */

package am.gtest.vortex.unused;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

public class HistoryConsumablesRecyclerView {

    private final String LOG_TAG = "myLogs: " + this.getClass().getSimpleName();
    private static List<NewProductsList> ITEMS;

    public void setupRecyclerView(@NonNull final RecyclerView recyclerView, String consumables) {

        ITEMS = new ArrayList<>();
        ITEMS.clear();

        consumables = "{\"consumables\":" + consumables + "}";

        try {
            JSONObject jObjectConsumables = new JSONObject(consumables);

            Log.e(LOG_TAG, "jObjectConsumables: \n" + jObjectConsumables.toString(2));

            if (jObjectConsumables.optJSONArray("consumables") != null &&
                    jObjectConsumables.optJSONArray("consumables").length() > 0) {

                JSONArray jArrayConsumables = jObjectConsumables.getJSONArray("consumables");

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_consumable, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.tvConsumableName.setText(mValues.get(position).productName);
            holder.tvConsumableQuantity.setText(mValues.get(position).quantity);


            holder.mView.setOnClickListener(v -> {

            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView tvConsumableName;
            public final TextView tvConsumableQuantity;
            public NewProductsList mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvConsumableName = view.findViewById(R.id.tvConsumableName);
                tvConsumableQuantity = view.findViewById(R.id.tvConsumableQuantity);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + tvConsumableName.getText() + "'";
            }
        }
    }




    private static void addItem(NewProductsList item) {
        ITEMS.add(item);
    }

    private static NewProductsList createDummyItem(JSONObject oneObjectPriceData) {
        String productName = null;
        String quantity = null;

        try {
            productName = oneObjectPriceData.getString("Product");
            quantity = oneObjectPriceData.getString("Quantity");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NewProductsList(productName, quantity);
    }

    public static class NewProductsList {
        public final String productName;
        public final String quantity;

        public NewProductsList(String productName, String quantity) {
            this.productName = productName;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return productName + ", " + quantity;
        }
    }
}