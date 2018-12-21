package ekylibre.zero.inventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ekylibre.database.ZeroContract;
import ekylibre.zero.R;
import ekylibre.zero.inventory.adapters.MainZoneAdapter;


public class NewInventory extends AppCompatActivity implements SelectProductCategoryFragment.OnFragmentInteractionListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SelectProductCategoryFragment selectproductcategoryfragment;
    ArrayList<ItemCategoryInventory> listeCategory=new ArrayList<>();




//todo: Faire une méthode pour enregistrer automatiquement la dernière date d'inventaire dans la zone
   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_new_current_inventory);

       setTitle("Nouvel Inventaire Courant");

//       fillDBtest();
//       Cursor cursorDateZone = queryDateZone();


//       mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//
//       mLayoutManager = new LinearLayoutManager(this);
//       mRecyclerView.setLayoutManager(mLayoutManager);




       TextView modifycategory = findViewById(R.id.choose_category);
       modifycategory.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View productcategorychoice) {
               fillDBtest();
               queryAddCategory();
               selectproductcategoryfragment = SelectProductCategoryFragment.newInstance(listeCategory);
               selectproductcategoryfragment.show(getFragmentTransaction(),"dialog");
           }
       });

   }

    private void queryAddCategory() {
        String[] projectionCategoryID = ZeroContract.Category.PROJECTION_ALL;

        Cursor cursorAddCategory = getContentResolver().query(
                ZeroContract.Category.CONTENT_URI,
                projectionCategoryID,
                null,
                null,
                null);

        while(cursorAddCategory.moveToNext()) {
            int index;

            index = cursorAddCategory.getColumnIndexOrThrow("category_name");
            String categoryName = cursorAddCategory.getString(index);

            Log.i("Query", "" +categoryName);

            listeCategory.add(new ItemCategoryInventory(categoryName,index));

        }
        //cursorZone.close();
    }




    private FragmentTransaction getFragmentTransaction (){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev!= null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        return ft;
    }

    @Override
    public void onFragmentInteraction(List<ItemCategoryInventory> categoryInventory) {
       int length = categoryInventory.size();
       ChipGroup chipGroup = findViewById(R.id.chipCategory);
       for(ItemCategoryInventory category : categoryInventory) {
           if(category.is_selected){
               Chip chip = new Chip(this);
               chip.setText(category.category);
               chip.setCloseIconVisible(true);
               //todo mettre un listener pour fermer
               chipGroup.addView(chip);
           }
       }
        //selectproductcategoryfragment.dismiss();
    }
    /*@Override
    public void onFragmentInteraction(ItemZoneInventory zone) {
        Log.i("MyTag", "click zone"+zone.zone);



        mAdapter.notifyDataSetChanged();
        selectCategoryDialogFragment.dismiss();
    }*/


    public void fillDBtest() {
        ContentValues mNewValues = new ContentValues();
        for (int i=0;i<7;i++){
            if (i<8) {
                mNewValues.clear();
                //int zoneId = i;
                //String zoneName = "zone_" + i;
                //String zoneShape = null;
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
                //SimpleDateFormat sdf = new SimpleDateFormat("E  d MMM yyyy, HH:mm");
                //SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                //String zoneDate = sdf.format(new Date());
                String categoryName = new String("test"+i);
                Log.i("myatg"," test 1 ");
                //String zoneDate = sdf.format(Calendar.getInstance().getTime());
                //Date zoneDate = new Date() ;

                mNewValues.put(ZeroContract.Category.CATEGORY_NAME, categoryName);
                //mNewValues.put(ZeroContract.ZoneStock.NAME, zoneName);
                //mNewValues.put(ZeroContract.ZoneStock.DATEZONE, zoneDate);
                getContentResolver().insert(
                        ZeroContract.Category.CONTENT_URI,   // the user dictionary content URI
                        mNewValues                          // the values to insert
                );
                //this.finish();
            }


        }
    }

}





