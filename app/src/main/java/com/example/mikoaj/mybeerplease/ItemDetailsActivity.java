package com.example.mikoaj.mybeerplease;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;


public class ItemDetailsActivity extends AppCompatActivity {
    long id;
    String name;
    String desc;
    byte[] imageUrl;
    String type;
    double price;

    @Override
    protected void onResume() {
        Intent intent = getIntent();
        id = intent.getLongExtra("id", 1);
        name = intent.getStringExtra("name");
        desc = intent.getStringExtra("desc");
        imageUrl = intent.getByteArrayExtra("image");
        type = intent.getStringExtra("type");
        price = intent.getDoubleExtra("price", 3.50);

        DbControler dbControler = new DbControler(getApplicationContext());

        dbControler.open();
        Beer beer =  dbControler.getBravery(id);
        dbControler.close();

        TextView nameTV = (TextView)findViewById(R.id.nameTextView);
        TextView descTV = (TextView)findViewById(R.id.descTextView);
        TextView typeTV = (TextView)findViewById(R.id.type_text_view);
        TextView priceTV = (TextView)findViewById(R.id.price_text_view);
        ImageView imv = (ImageView) findViewById(R.id.imageUrlImageView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        imv.setMinimumHeight(size.y/5);
        imv.setMinimumWidth(size.x/3);


        Locale locale = new Locale("pl", "PL");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        typeTV.setText(beer.getType());
        priceTV.setText(fmt.format(beer.getPrice()));
        nameTV.setText(beer.getName());
        descTV.setText(beer.getDescription());
        descTV.setMovementMethod(new ScrollingMovementMethod());
        imageUrl = beer.getImageArray();
        if(imageUrl != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(imageUrl, 0, imageUrl.length);
            imv.setImageBitmap(bm);
        }
        else {
            imv.setImageResource(R.drawable.beer);
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Toolbar t = (Toolbar) findViewById(R.id.tool_bar_detail_activity);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    public void goBackBtn(View view) {
        this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds beers to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_details, menu);
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

    public void editItem(View v) {
        Intent intent = new Intent(getBaseContext(), EditItemActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("desc", desc);
        intent.putExtra("image", imageUrl);
        intent.putExtra("price", price);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}
