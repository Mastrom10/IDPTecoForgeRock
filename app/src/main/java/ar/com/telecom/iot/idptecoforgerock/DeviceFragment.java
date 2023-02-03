package ar.com.telecom.iot.idptecoforgerock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class DeviceFragment extends Fragment {


    // TODO: Rename and change types of parameters
    private String deviceName;
    private String deviceIconURL;
    private String deviceModel;
    //device_online_text_view boolean online;
    private boolean online;
    //device_uid_text_view String uid
    private String uid;
    //device_uuid_text_view String uuid
    private String uuid;



    private String TuyaCDN = "https://images.tuyacn.com/";

    public DeviceFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DeviceFragment newInstance(Device device) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putString("Device_Name_PARAM", device.getName());
        //Device_Icon_PARAM
        args.putString("Device_Icon_PARAM", device.getIcon());
        //device_model_text_view
        args.putString("Device_Model_PARAM", device.getModel());
        //device_online_text_view
        args.putBoolean("Device_Online_PARAM", device.isOnline());
        fragment.setArguments(args);
        //device_uid_text_view
        args.putString("Device_UID_PARAM", device.getUid());
        //device_uuid_text_view
        args.putString("Device_UUID_PARAM", device.getUuid());
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deviceName = getArguments().getString("Device_Name_PARAM");
            deviceIconURL = getArguments().getString("Device_Icon_PARAM");
            deviceModel = getArguments().getString("Device_Model_PARAM");
            online = getArguments().getBoolean("Device_Online_PARAM");
            uid = getArguments().getString("Device_UID_PARAM");
            uuid = getArguments().getString("Device_UUID_PARAM");



        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device, container, false);
    }


    Button btnSendCommands;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView device_name_text_view = getView().findViewById(R.id.device_name_text_view);
        device_name_text_view.setText(deviceName);
        ImageView imageViewIcon = getView().findViewById(R.id.imageViewIcon);
        new DownloadImageTask(imageViewIcon).execute(TuyaCDN+deviceIconURL);
        TextView device_icon_url_text_view = getView().findViewById(R.id.device_icon_url_text_view);
        device_icon_url_text_view.setText(deviceIconURL);
        TextView device_model_text_view = getView().findViewById(R.id.device_model_text_view);
        device_model_text_view.setText(deviceModel);
        TextView device_online_text_view = getView().findViewById(R.id.device_online_text_view);
        if (online) {
            device_online_text_view.setText("Online");
            //color green online
            device_online_text_view.setTextColor( 0xFF00FF00);

        } else {
            device_online_text_view.setText("Offline");
            //color red offline
            device_online_text_view.setTextColor( 0xFFFF0000);
        }

        TextView device_uid_text_view = getView().findViewById(R.id.device_uid_text_view);
        device_uid_text_view.setText("User ID (UID):" + uid);
        TextView device_uuid_text_view = getView().findViewById(R.id.device_uuid_text_view);
        device_uuid_text_view.setText("Device ID (UUID):" + uuid);

        btnSendCommands = getView().findViewById(R.id.btnSendCommands);
        btnSendCommands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start DeviceSendCommand activity
                Intent intent = new Intent(getActivity(), DeviceSendCommand.class);
                intent.putExtra("Device_Name_PARAM", deviceName);
                intent.putExtra("Device_Icon_PARAM", deviceIconURL);
                intent.putExtra("Device_Model_PARAM", deviceModel);
                intent.putExtra("Device_Online_PARAM", online);
                intent.putExtra("Device_UID_PARAM", uid);
                intent.putExtra("Device_UUID_PARAM", uuid);
                startActivity(intent);

            }
        });
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;

    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String imageURL = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream inputStream = new java.net.URL(imageURL).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }
}
