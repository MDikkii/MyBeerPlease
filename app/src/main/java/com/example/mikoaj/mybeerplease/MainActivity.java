package com.example.mikoaj.mybeerplease;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
        List<Beer> beers;
        Cursor dbCursor;
        ItemListViewAdapter adapter;
        DbControler dbControler;
        AdapterView.AdapterContextMenuInfo infoHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar t = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(t);
        getDataForListView();



        adapter = new ItemListViewAdapter(this, beers);

        ListView itemListView = (ListView)findViewById(R.id.listView);
        registerForContextMenu(itemListView);
        itemListView.setAdapter(adapter);

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long arg3) {

                Beer beer = adapter.getItem(position);

                //Toast.makeText(MainActivity.this, beer.itemName, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, ItemDetailsActivity.class);
                intent.putExtra("id", beer.getId());
                intent.putExtra("name", beer.getName());
                intent.putExtra("desc", beer.getDescription());
                intent.putExtra("image", beer.getImageArray());
                intent.putExtra("price", beer.getPrice());
                intent.putExtra("type", beer.getType());
                startActivity(intent);

            }
        });
        }

    @Override
    protected void onResume(){
        super.onResume();
        if(beers !=null)
        updateListViewData();
        }

    @Override
    protected void onDestroy() {
        if(dbControler != null)
        dbControler.close();
        super.onDestroy();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds beers to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
        return true;
        }

        return super.onOptionsItemSelected(item);
        }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(info == null)
        info = infoHistory;
        else
        infoHistory = info;

        switch (item.getItemId()) {
        case R.id.edit_item:
        editItem(info.position);
        return true;
        case R.id.delete_item:
        deleteItem(info.position);
        return true;
        case R.id.name_item_sort_asc:
        sortItems(info.position, dbControler.KEY_NAME, "ASC");
        return true;
        case R.id.name_item_sort_desc:
        sortItems(info.position, dbControler.KEY_NAME, "DESC");
        return true;
        case R.id.type_item_sort_asc:
        sortItems(info.position, dbControler.KEY_TYPE, "ASC");
        return true;
        case R.id.type_item_sort_desc:
        sortItems(info.position, dbControler.KEY_TYPE, "DESC");
        return true;
        case R.id.price_item_sort_asc:
        sortItems(info.position, dbControler.KEY_PRICE, "ASC");
        return true;
        case R.id.price_item_sort_desc:
        sortItems(info.position, dbControler.KEY_PRICE, "DESC");
        return true;
        default:
        return super.onContextItemSelected(item);
        }
    }

    private void sortItems(int position, String option, String sortWay) {
        dbCursor = getAllEntriesFromDB(option, sortWay);
        updateListViewData();

        }

    private void deleteItem(int position) {
        Beer beer = adapter.getItem(position);
        long id = beer.getId();
        dbControler.deleteBravery(id);
        updateListViewData();
        }

    private void editItem(int position) {
        Beer beer = adapter.getItem(position);

        //Toast.makeText(MainActivity.this, beer.itemName, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
        intent.putExtra("id", beer.getId());
        intent.putExtra("name", beer.getName());
        intent.putExtra("desc", beer.getDescription());
        intent.putExtra("image", beer.getImageArray());
        intent.putExtra("price", beer.getPrice());
        intent.putExtra("type", beer.getType());
        startActivity(intent);
        }

    public void newItem (View v){
        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
        startActivity(intent);
        }

    private void updateListViewData() {
        dbCursor.requery();
        beers.clear();
        updateItemList();
        adapter.notifyDataSetChanged();
        }

    public void getDataForListView()
        {
        dbControler = new DbControler(getApplicationContext());

        dbControler.open();
            //TODO: It should be remove in release build
        if(dbControler.getAllBraveries(null,null).getCount() <= 0) {
            dbControler.insertBravery("American IPA Kormoran", "Pierwszym skojarzeniem, które pojawia się po usłyszeniu AIPA to cała feria aromatów. AIPA oszałamia chmielowymi zapachami przywołującymi cytrusy, kwiaty, sosnowy las, czy owoce tropikalne. Piwo jest koloru miedzi lub bursztynu, czasem z pomarańczowymi odcieniami. Chmielenie na zimno może spowodować lekką opalizację. Piwo pokryte jest trwałą, drobnopęcherzykową pianą. W smaku podobnie jak w aromacie odnajdujemy wszystkie cudowne akcenty używanych chmieli. Zdecydowana goryczka i chmielowość jest dobrze zbalansowana słodową podstawą tego piwa. Mimo słodowości piwo nie jest karmelowe, zatykające. Chmielowy posmak nie powinien być ostry, a zachęcać do kolejnego łyku. Podsumowując można powiedzieć, że to wybitnie pijalne piwo, pełnymi garściami czerpiące z dobrodziejstw nowych odmian chmielu.", null, "American IPA", 3.29);
            dbControler.insertBravery("Ciechan Miodowe", "Ciechan Miodowe jest wiodącym piwem miodowym na polskim rynku. Od pojawienia się w 2004 roku, co roku wybierany jest najlepszym miodowym piwem w konsumenckim Plebiscycie na Piwo Roku wg. Browar.biz. Dzięki stosowaniu dużych ilości wyłącznie naturalnego miodu, Ciechan Miodowe zdobywa uznanie obu płci.",  null, "Smakowe", 4.56);
            dbControler.insertBravery("Tyskie Gronie", "Aromatyczny słód jęczmienny, chmiel pochodzący z polskich plantacji oraz woda. Wszystkie te elementy, przy użyciu drożdży w procesie naturalnej piwnej fermentacji, gwarantują wytrawnym piwoszom pełnię smaku – wyczują oni więc w Tyskim Groniu nuty słodowe, delikatny aromat chmielu, a nawet akcenty jabłkowo-bananowe." , null, "Lager", 2.56);
            dbControler.insertBravery("Wrocławskie", "Wrocławskie to jasne, lekkie piwo dolnej fermentacji. Piwo zawiera 10% wag. ekstraktu oraz 4,2% obj. alkoholu. Jest to piwo o barwie jasnej słomki. Lekka piana towarzyszy przez cały czas degustacji. Przyjemnie wyczuwalny aromat słodu z delikatną nutą chmielu w tle, całość bardzo świeża i orzeźwiająca. W smaku dominuje chmiel z nutką słodowości oraz długą, a zarazem delikatną, orzeźwiającą goryczką.",  null, "Pils", 4.20);
            dbControler.insertBravery("Cornelius Bananowe", "Grasujący po straganach banan w końcu trafił za kapsle. Jego spotkanie z pszenicą i drożdżami w jednej butli być może budziło u niektórych niepokój, ale tylko do pierwszej wspólnej imprezy. Odkąd pszenica kumpluje się z bananem, ma coraz większe wzięcie. Nie dziw się. W końcu i Tobie z bananem do twarzy.",  null, "Smakowe", 3.59);
        }
        getAllItems();
        }

    private void getAllItems() {
        beers = new ArrayList<Beer>();
        dbCursor = getAllEntriesFromDB(null, null);
        updateItemList();
        }

    private Cursor getAllEntriesFromDB(String columnName, String sortWay) {
        dbCursor = dbControler.getAllBraveries(columnName,sortWay);
        if(dbCursor != null) {
        startManagingCursor(dbCursor);
        dbCursor.moveToFirst();
        }
        return dbCursor;
        }

    private void updateItemList() {
        if(dbCursor != null && dbCursor.moveToFirst()) {
            do{
                long id = dbCursor.getLong(dbControler.ID_COLUMN);
                String name =  dbCursor.getString(dbControler.NAME_COLUMN);
                String description = dbCursor.getString(dbControler.DESCRIPTION_COLUMN);
                byte[] photo = dbCursor.getBlob(dbControler.PHOTO_COLUMN);
                double price = dbCursor.getDouble(dbControler.PRICE_COLUMN);
                String type = dbCursor.getString(dbControler.TYPE_COLUMN);
                beers.add( new Beer(id,name, description, photo, type, price));
            } while(dbCursor.moveToNext());
        }
    }


    public class ItemListViewAdapter extends BaseAdapter {
        Activity context;
        List<Beer> beerList;

        public ItemListViewAdapter(Activity context, List<Beer> beers) {
            super();
            this.context = context;
            this.beerList = beers;
        }

        @Override
        public int getCount() {
            return beerList.size();
        }

        @Override
        public Beer getItem(int position) {
            return beerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listitem, parent, false);
            }

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            TextView nameTextView = (TextView)convertView.findViewById(R.id.name_text_view_list_item);
            TextView typeTextView = (TextView)convertView.findViewById(R.id.type_text_view_list_item);
            TextView priceTextView = (TextView)convertView.findViewById(R.id.price_text_view_list_item);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.image_view_list_item);
            imageView.setMinimumHeight(size.y/5);
            imageView.setMinimumWidth(size.x/3);

            Beer beer = beerList.get(position);

            Locale locale = new Locale("pl", "PL");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            nameTextView.setText(beer.getName());
            typeTextView.setText(beer.getType());
            priceTextView.setText(fmt.format(beer.getPrice()));

            byte[] imageArray = beer.getImageArray();
            if(imageArray != null ) {
                Bitmap bm = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
                imageView.setImageBitmap(bm);
            }
            else {
                imageView.setImageResource(R.drawable.beer);
            }


            return convertView;
        }

    }
}