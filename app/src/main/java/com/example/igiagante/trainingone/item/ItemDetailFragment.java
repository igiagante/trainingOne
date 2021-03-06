package com.example.igiagante.trainingone.item;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igiagante.trainingone.DescriptionActivity;
import com.example.igiagante.trainingone.R;

import connections.Connection;
import dao.ItemDao;
import imageloader.ImageLoader;
import model.Item;
import services.ItemService;

/**
 * Created by igiagante on 20/7/15.
 */
public class ItemDetailFragment extends Fragment {

    private Item item;
    private ItemDao itemDao;

    static final String ITEM_PARAM = "ITEM_PARAM";

    private ItemDetailListener itemDetailListener;
    private ImageView imageView;
    private CheckBox checkBoxTracking;
    private TextView textView;
    private ProgressBar pb;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemDao = new ItemDao(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View containerView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        pb = (ProgressBar) containerView.findViewById(R.id.item_progress_bar);
        pb.setVisibility(View.INVISIBLE);
        imageView = (ImageView) containerView.findViewById(R.id.item_image);
        checkBoxTracking = (CheckBox) containerView.findViewById(R.id.checkbox_tracking);
        textView = (TextView) containerView.findViewById(R.id.item_title);
        button = (Button) containerView.findViewById(R.id.item_button);

        if(savedInstanceState != null){
            item = savedInstanceState.getParcelable("item");
        }else{
            hideComponents();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemDetailListener.getItemDescription(item);
            }
        });

        checkBoxTracking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(Connection.checkInternet(getActivity())){

                    if(checkBoxTracking.isChecked()){
                        //start traking item
                        itemDao.createItem(item, true);
                    }else {
                        //stop traking item
                        itemDao.deleteItem(item);
                    }

                }else{
                    Toast.makeText(getActivity(), "Internet is not avialable", Toast.LENGTH_LONG).show();
                }
            }
        });

        return containerView;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, Intent intent) {
            if(intent.getAction().equals(ItemService.NOTIFICATION_ITEM_READY)){

                item = intent.getParcelableExtra(ItemService.ITEM);
                ImageLoader.INSTANCE.displayImage(item.getImageUrl(), imageView, true);
                textView.setText(item.getTitle());

                if(itemDao.exist(item.getItemId())){
                    checkBoxTracking.setChecked(true);
                }else{
                    checkBoxTracking.setChecked(false);
                }

                initComponents();
            }
        }
    };

    private void initComponents(){
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        checkBoxTracking.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        button.setEnabled(true);
        pb.setVisibility(View.INVISIBLE);
    }

    private void hideComponents(){
        textView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        checkBoxTracking.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        button.setEnabled(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("item", item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(ItemService.NOTIFICATION_ITEM_READY));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    public interface ItemDetailListener {
        void getItemDescription(Item item);
    }

    public void setItem(Item item){
        this.item = item;
        pb.setVisibility(View.VISIBLE);
        addItemExtraData(item);
    }

    private void addItemExtraData(Item item){
        Intent intentService = new Intent(getActivity(), ItemService.class);
        intentService.setAction(ItemService.ACTION_ADD_EXTRA_DATA);
        intentService.putExtra(ItemService.ITEM, item);
        getActivity().startService(intentService);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            itemDetailListener = (ItemDetailListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
