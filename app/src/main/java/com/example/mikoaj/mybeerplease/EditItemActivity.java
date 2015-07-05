package com.example.mikoaj.mybeerplease;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class EditItemActivity extends AppCompatActivity {

    long id;
    EditText nameET,descET,typeET,priceET;
    ImageButton imageButton;
    int PICK_IMAGE_REQUEST = 1;
    byte[] imageUrl;
    byte [] tempByte;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Toolbar t = (Toolbar) findViewById(R.id.tool_bar_edit_activity);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        String name;
        String desc;

        String type;
        double price;
        
        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);
        name = intent.getStringExtra("name");
        desc = intent.getStringExtra("desc");
        imageUrl = intent.getByteArrayExtra("image");
        type = intent.getStringExtra("type");
        price = intent.getDoubleExtra("price", 0);

        nameET = (EditText)findViewById(R.id.name_editText_edit);
        descET = (EditText)findViewById(R.id.desc_editText_edit);
        typeET = (EditText)findViewById(R.id.type_text_view_edit);
        priceET = (EditText)findViewById(R.id.price_text_view_edit);
        imageButton = (ImageButton)findViewById(R.id.image_button_edit);

        imageButton.setMinimumWidth(size.x/3);
        imageButton.setMinimumHeight(size.y / 5);

        typeET.setText(type);
        priceET.setText(price+"");
        nameET.setText(name);
        descET.setText(desc);
        descET.setMovementMethod(new ScrollingMovementMethod());
        if(imageUrl != null ) {
                Bitmap bm = BitmapFactory.decodeByteArray(imageUrl, 0, imageUrl.length);
                imageButton.setImageBitmap(bm);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds beers to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so priceg
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void saveClick(View view) {


        DbControler dbControler = new DbControler(getApplicationContext());
        dbControler.open();
        if(id == -1)
        {
            dbControler.insertBravery(nameET.getText().toString(),descET.getText().toString(),tempByte != null?tempByte:imageUrl, typeET.getText().toString(), Double.parseDouble(priceET.getText().toString()));
        }
        else
        {
            dbControler.updateBravery(id,nameET.getText().toString(),descET.getText().toString(),tempByte != null?tempByte:imageUrl, typeET.getText().toString(), Double.parseDouble(priceET.getText().toString()));
        }
        dbControler.close();

        this.finish();
    }

    public void pickImage(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();


            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Bitmap destinationBitmap;



                destinationBitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        size.x/3,
                        size.y/5,
                        false
                );

                imageButton.setImageBitmap(destinationBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                destinationBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                tempByte = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
