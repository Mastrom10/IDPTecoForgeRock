package ar.com.telecom.iot.idptecoforgerock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class userDevices extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_devices);

        //get extra "devices"
        String devicesJson = getIntent().getStringExtra("devices");
        Response response = new Gson().fromJson(devicesJson, Response.class);
        List<Device> devices = response.getDevices();

        // Crea una instancia del DevicesPagerAdapter
        DevicesPagerAdapter adapter = new DevicesPagerAdapter(getSupportFragmentManager());


        if (devices != null) {
            for (Device device : devices) {
                // Crea una instancia del fragmento y le pasa el objeto del dispositivo como argumento
                DeviceFragment fragment = DeviceFragment.newInstance(device);

                // Agrega el fragmento al DevicesPagerAdapter junto con su título
                adapter.addFragment(fragment, device.getName());
            }
        }

        // Asigna el DevicesPagerAdapter al ViewPager
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        // Asigna el ViewPager al TabLayout para que se muestren las pestañas
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);



    }
}