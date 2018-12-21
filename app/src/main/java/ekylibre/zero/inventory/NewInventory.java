package ekylibre.zero.inventory;

import android.content.ContentValues;
import android.content.ContentProvider;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.net.Uri;
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

import java.text.SimpleDateFormat;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ekylibre.database.DatabaseHelper;
import ekylibre.database.ZeroContract;
import ekylibre.database.ZeroProvider;
import ekylibre.zero.R;
import ekylibre.zero.home.Zero;
import ekylibre.zero.inventory.adapters.MainZoneAdapter;
import ekylibre.zero.inventory.adapters.UiSelectProductAdapter;

import static java.security.AccessController.getContext;
import static java.text.SimpleDateFormat.*;


public class NewInventory extends AppCompatActivity implements SelectProductCategoryFragment.OnFragmentInteractionListener,
        SelectProductTypeFragment.OnTypeFragmentInteractionListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SelectProductCategoryFragment selectproductcategoryfragment;
    private SelectProductTypeFragment selectproducttypefragment;
    ArrayList<ItemCategoryInventory> listeCategory=new ArrayList<>();
    ArrayList<ItemTypeInventory> listeType=new ArrayList<>();

    ArrayList<ItemProductInventory> listProduct = new ArrayList<>();
    Bundle extras;
    String zoneId = "1";
    int inventoryType;



//todo: Faire une méthode pour enregistrer automatiquement la dernière date d'inventaire dans la zone
   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_new_current_inventory);
       Intent intent = getIntent();
       extras = intent.getExtras();
       inventoryType = extras.getInt("inventoryType");
       zoneId = extras.getString("itemId");

  /*     class MyButtonClickListener implements View.OnClickListener {
           @Override
           public void onClick(View _buttonView) {
               if (_buttonView.getId() == R.id.addproduct) {
                   Log.i("mytag", "categorychoice");
                   Intent intent=new Intent(this,thirdactivity.class);
                   startActivity(intent);
       setTitle("Nouvel Inventaire Courant");*/

//       fillDBtest();
//       Cursor cursorDateZone = queryDateZone();


//       mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//
//       mLayoutManager = new LinearLayoutManager(this);
//       mRecyclerView.setLayoutManager(mLayoutManager);

       SQLiteDatabase db = (new DatabaseHelper(getApplicationContext())).getReadableDatabase();
       String rawQuery = "SELECT * FROM "+ZeroContract.InventoryProductColumns.TABLE_NAME+" JOIN " +
               ""+ZeroContract.ProductColumns.TABLE_NAME+" ON " +
               ""+ZeroContract.ProductColumns.PRODUCT_ID+" = "+ZeroContract.InventoryProductColumns.FK_PRODUCT_ID+" " +
               "WHERE "+ZeroContract.ProductColumns.FK_ZONE_STOCK_ID+" = "+zoneId;
       Cursor cursor = db.rawQuery(rawQuery,null);

       while (cursor.moveToNext()){
           String productName =  cursor.getString(1);
           String productVariant = cursor.getString(2);
           Date productDate = null;
           SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
           try {
               productDate = dateFormat.parse(cursor.getString(3));
           }
           catch (java.text.ParseException e){
               Log.e("MyTag","Exception on date converter");
           }
           Float productQuantity = null;
           try {
               productQuantity = Float.parseFloat(cursor.getString(4));
           }catch (Exception e){
               Log.e("MyTag", "exception on quantity converter");
           }
           listProduct.add(new ItemProductInventory(productName,
                   productVariant,
                   productDate,
                   productQuantity,
                   cursor.getString(5),
                   null));

       }
       Log.i("MyTag","QueryListProduct"+listProduct.toString());



       class MyButtonClickListener implements View.OnClickListener {
           @Override
           public void onClick(View _buttonView) {
//               if (_buttonView.getId() == R.id.choose_category) {
//                   Log.i("mytag", "categorychoice");
//                   Intent intent = new Intent(this, thirdactivity.class);
//                   startActivity(intent);
//               }
               if (_buttonView.getId() == R.id.validate_button) {

               }
           }


    }/*
       TextView productcategorychoicebutton = findViewById(R.id.productcategorychoicebutton);
       productcategorychoicebutton.setOnClickListener(new View.OnClickListener() {
       }*/
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

       TextView modifytype = findViewById(R.id.choose_type);
       modifytype.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View producttypechoice) {
               fillDBtest();
               queryAddtype();
               selectproducttypefragment = SelectProductTypeFragment.newInstance(listeType);
               selectproducttypefragment.show(getFragmentTransaction(),"dialog");
           }
       });

/*
       for (int i=0;i<4;i++){
           listProduct.add(new ItemProductInventory("Name_"+i, "var_"+i,
                   new Date(), 10+i, "comm_"+i, image));
       }
       Log.i("MyTag", ""+listProduct.toString());
*/
       mRecyclerView = (RecyclerView) findViewById(R.id.ProductsRecycler);

       mLayoutManager = new LinearLayoutManager(this);
       mRecyclerView.setLayoutManager(mLayoutManager);

       mAdapter = new UiSelectProductAdapter(listProduct);
       mRecyclerView.setAdapter(mAdapter);

       mRecyclerView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               switch (view.getId()){
                   case R.id.product_qtt_edit:
                       Log.i("MyTag", "Edit Quantity");
                       break;
                   case R.id.product_minus_qtt:
                       Log.i("MyTag", "Edit Quantity Minus");
                       break;
                   case R.id.product_plus_qtt:
                       Log.i("MyTag", "Edit Quantity Plus");
                       break;
                   case R.id.product_checkbox:
                       Log.i("MyTag", "Check");
                       break;
                   case R.id.add_image:
                       // Should open popup
                       Log.i("MyTag", "Edit Image");
                       break;
                   case R.id.add_comment:
                       // Should open popup
                       Log.i("MyTag", "Edit Comment");
                       break;
                   default:
                       Log.i("MyTag", "Default");
                       break;
               }
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

    private void queryAddtype() {
        String[] projectionTypeID = ZeroContract.Type.PROJECTION_ALL;
        List<String> listeCategoryId=new ArrayList<>();

        for (ItemCategoryInventory category : listeCategory){
            listeCategoryId.add(String.valueOf(category.Id));
        }
        String[] args = new String[listeCategoryId.size()];
        for(int i = 0; i < listeCategoryId.size(); i++) {
            args[i] = listeCategoryId.get(i);
        }


        Cursor cursorAddType = getContentResolver().query(
                ZeroContract.Type.CONTENT_URI,
                projectionTypeID,
                 ZeroContract.Type.FK_CATEGORY_ID+" in ?", args,
                null);

        while(cursorAddType.moveToNext()) {
            int index;

            index = cursorAddType.getColumnIndexOrThrow("type_name");
            String typeName = cursorAddType.getString(index);

            Log.i("Query", "" +typeName);

            listeType.add(new ItemTypeInventory(typeName,index));

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

    @Override
    public void onFragmentInteraction(ItemTypeInventory zone) {

    }
}





