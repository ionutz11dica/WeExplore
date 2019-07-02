package licenta.books.androidmobile.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import info.hoang8f.android.segmented.SegmentedGroup;
import licenta.books.androidmobile.R;
import licenta.books.androidmobile.adapters.ScannedBooksAdapter;
import licenta.books.androidmobile.api.ApiClient;
import licenta.books.androidmobile.api.ApiService;
import licenta.books.androidmobile.classes.BookE;
import licenta.books.androidmobile.interfaces.Constants;
import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.CameraSource;
import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.CameraSourcePreview;
import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.GraphicOverlay;
import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.Graphics.BarcodeGraphic;
import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.Graphics.BarcodeGraphicTracker;
import licenta.books.androidmobile.patterns.BarcodeDetector.CameraInterface.Graphics.BarcodeTrackerFactory;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class ScannerFragment extends Fragment implements BarcodeGraphicTracker.BarcodeUpdateListener, ScannedBooksAdapter.OnCheckedChangedListener {
    private static final String TAG = "Barcode reader";

    //request code pentru camera, trb sa fie mai mic 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int RC_HANDLE_GMS = 9001;
    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private GraphicOverlay<BarcodeGraphic> graphicOverlay;


    //Obiecte suport pentru detectarea atingerilor pe egran si zoom
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private SegmentedGroup segmentedScan;
    private RadioButton rbScan;
    private RadioButton rbScannedBooks;
    private Button selectAll;
    private SwipeMenuListView swipeMenuListView;

    private OnScannerInteractionListener mListener;
    private ApiService apiService;
    static public ArrayList<BookE> scannedBooks = new ArrayList<>();
    public  ScannedBooksAdapter adapter ;
    private LinearLayout listScan ;
    LinearLayout linearLayoutFooter;
    TextView footer;
    private ImageView ivScan;
    View footerView;
    boolean isSelected=true;
    public static boolean flagForFlag = false;
    private static String[] idStrings;

    public ScannerFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        initComp(view);

        //garantarea permisiunilor pentru camera
        if(getContext()!=null) {
            int rc = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
            if(rc == PackageManager.PERMISSION_GRANTED){
                createCameraSource(true,false);
            }else{
                requestCameraPermission(view);
            }
            gestureDetector = new GestureDetector(getContext(), new CaptureGestureListener());
            scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    boolean b = scaleGestureDetector.onTouchEvent(event);

                    boolean c = gestureDetector.onTouchEvent(event);

                    return b || c ;
                }
                return true;
            }
        });
        return view;
    }

    public void initComp(View view){
        preview = view.findViewById(R.id.camera_preview);
        graphicOverlay = view.findViewById(R.id.graphic_overlay);
        segmentedScan = view.findViewById(R.id.segmentedScan);

        rbScan = view.findViewById(R.id.btn_scan);
        rbScan.setOnClickListener(listenerScan);

        rbScannedBooks = view.findViewById(R.id.btn_bookScanned);
        rbScannedBooks.setOnClickListener(listenerScannedBooks);

        segmentedScan.check(rbScan.getId());
        segmentedScan.setTintColor(Color.parseColor(Constants.COLOR_PALLET));

        listScan = view.findViewById(R.id.list_scannedBooks);
        selectAll = view.findViewById(R.id.btn_select_all);
        selectAll.setOnClickListener(listenerSelectAll);

        swipeMenuListView = listScan.findViewById(R.id.lv_scannedbooks);
        createSwipeMenu();
        swipeMenuListView.setOnMenuItemClickListener(menuItemClickListener);

        footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);
        linearLayoutFooter = footerView.findViewById(R.id.footer_layout);
        footer = footerView.findViewById(R.id.footer_1);
        linearLayoutFooter.setOnClickListener(footerListener);
        ivScan = new ImageView(getContext());
        swipeMenuListView.addFooterView(footerView,null,false);
        apiService = ApiClient.getRetrofit().create(ApiService.class);

        getScannedBooks();


    }

    private View.OnClickListener listenerScannedBooks = v -> {
        listScan.setVisibility(View.VISIBLE);
        cameraSource.stop();
        preview.setVisibility(View.INVISIBLE);
        listScan.setClickable(true);
        rbScannedBooks.setClickable(false);
        rbScan.setClickable(true);
        if(scannedBooks.size() == 0 ){
            setFooterViewListener();
        }

    };

    private View.OnClickListener listenerScan = v -> {
        listScan.setVisibility(View.GONE);
        preview.setVisibility(View.VISIBLE);
        linearLayoutFooter.removeView(ivScan);
        startCameraSource();
        rbScannedBooks.setClickable(true);
        rbScan.setClickable(false);
    };

    private View.OnClickListener listenerSelectAll = v -> {
        if(isSelected){
            selectAll.setText("Deselect All");
            removeAllChecks(swipeMenuListView,isSelected);
            isSelected = false;

        }else{
            selectAll.setText("Select All");
            removeAllChecks(swipeMenuListView,isSelected);
            isSelected = true;

        }
    };

    private SwipeMenuListView.OnMenuItemClickListener menuItemClickListener = (position, menu, index) -> {
      switch (index){
          case 0:
              String id = scannedBooks.get(position).get_id();
              String[] arrayList = {id};
              JSONArray books = new JSONArray(Arrays.asList(arrayList));
              try {
                  deleteScannedBooks(books);
              } catch (JSONException e) {
                  e.printStackTrace();
              }
              scannedBooks.remove(position);
              adapter.notifyDataSetChanged();
              swipeMenuListView.setAdapter(adapter);
              if(scannedBooks.size()==0){
                  setFooterViewListener();
              }
              break;
      }
      return false;
    };

    private AdapterView.OnItemClickListener itemClickListener = (parent, view, position, id) -> {

    };

    private View.OnClickListener footerListener = v -> {
        if(scannedBooks.size()>0) {
            if (ScannedBooksAdapter.checks != null && ScannedBooksAdapter.checks.size() > 0) {
                try {
                    deleteScannedBooks(new JSONArray(Arrays.asList(ScannedBooksAdapter.checks.toArray(new String[0]))));
                    scannedBooks.clear();
                    adapter.notifyDataSetChanged();
                    swipeMenuListView.setAdapter(adapter);
                    setFooterViewListener();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(listScan, "Books not selected",
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        }else{
            rbScan.performClick();
        }
    };



    private void removeAllChecks(ViewGroup vg,boolean flag) {
        View v = null;
        for(int i = 0; i < vg.getChildCount(); i++){
            try {
                v = vg.getChildAt(i);
                ((CheckBox)v).setChecked(flag);
            }
            catch(Exception e1){ //if not checkBox, null View, etc
                try {
                    assert v != null;

                    flagForFlag = !flag;
                    removeAllChecks((ViewGroup)v,flag);

                }
                catch(Exception e2){ //v is not a view group
                    continue;
                }
            }
        }

    }



    private void deleteScannedBooks(JSONArray books) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("books",books);
        Call<RequestBody> responseCall = apiService.deleteScannedBooks("nicolae.ionut9711@gmail.com",object.toString());
        responseCall.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                if(response.isSuccessful()){
                    Log.d("Delete code:", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {

            }
        });
    }

    private void getScannedBooks(){
        Call<ArrayList<BookE>> call = apiService.getScannedBooks("nicolae.ionut9711@gmail.com");
        call.enqueue(new Callback<ArrayList<BookE>>() {
            @Override
            public void onResponse(Call<ArrayList<BookE>> call, Response<ArrayList<BookE>> response) {
                long old = System.currentTimeMillis();
//                new Thread(() -> {
                    if(response.isSuccessful()){

                        if(response.body()!=null && response.body().size() > 0){
                            getLengthList(response.body(),"Clear 0 data");

                        }
                    }else{
                        getLengthList(new ArrayList<>(),"Scan books");

                    }
                   long current = System.currentTimeMillis();
                    System.out.println("Time elapsed:" + String.valueOf(current - old));
//                }).run();
            }

            @Override
            public void onFailure(Call<ArrayList<BookE>> call, Throwable t) {
                Toast.makeText(getContext(), (CharSequence) t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private synchronized void getLengthList(ArrayList<BookE> arrayList,String string){

        scannedBooks = arrayList;
        adapter = new ScannedBooksAdapter(getActivity(),scannedBooks);
        adapter.setOnCheckedChangedListener(this);
        footer.setText(string);
        footer.setTextSize(16);
        footer.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"crimsontext.ttf"));
        swipeMenuListView.setAdapter(adapter);

//        Toast.makeText(getContext(),arrayList.size(),Toast.LENGTH_LONG).show();
        Log.d("Length:", String.valueOf(arrayList.size()));

    }

    @SuppressLint("SetTextI18n")
    public void setFooterSize(ArrayList<String> ids){
//        idStrings = ScannedBooksAdapter.checks.toArray(new String[0]);
        footer.setText("Clear "+ ids.size() +" Book");
        footer.setTextSize(16);
        footer.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"crimsontext.ttf"));
        adapter.notifyDataSetChanged();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setFooterViewListener(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1.5f);
        linearLayoutFooter.setLayoutParams(params);
        ivScan.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,0.5f));
        ivScan.setImageResource(R.drawable.ic_qr_code);
        linearLayoutFooter.addView(ivScan);

        footer.setText("Scan books");
        footer.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        footer.setTextSize(30);
        footer.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"crimsontext.ttf"));

    }

    private void createSwipeMenu(){
        SwipeMenuCreator creator = menu -> {
            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getContext());

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.parseColor("#c03546")));
            // set item width
            deleteItem.setWidth(100);
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete_sweep_black_24dp);
            // add to menu
            menu.addMenuItem(deleteItem);
        };

        swipeMenuListView.setMenuCreator(creator);
    }

    private void startCameraSource() throws SecurityException{
        //verificam daca telefonul are play services disponibil
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if(code != ConnectionResult.SUCCESS){
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),code,RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null){
            try{
                preview.start(cameraSource,graphicOverlay);
            }catch (IOException e){
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private void requestCameraPermission(View v){
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (getActivity()!=null && !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final  Activity activity = getActivity();
        View.OnClickListener listener = null;
        if(activity!=null) {
            listener = view -> ActivityCompat.requestPermissions(activity, permissions,
                    RC_HANDLE_CAMERA_PERM);
        }

//        v.findViewById(R.id.topLayout).setOnClickListener(listener);

        Snackbar.make(graphicOverlay,"Access to the camera is needed for detection" ,
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok", listener)
                .show();
    }

    private void createCameraSource(boolean autofocus, boolean useFlash){
        Context context = getContext();

        //Barcode este create pt a detecta codurile de bare, iar o instanta MultiProcesor este
        //setata sa primeasca rezultatele detectarii codului de bare si sa le mentina
        // adica un grafic pentru fiecare cod de bare pe ecran. Fabrica este utilizata de mutiprocesor
        //pt a crea o instanta pentru fiecare cod de bare
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeTrackerFactory = new BarcodeTrackerFactory(graphicOverlay, context);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeTrackerFactory).build());

        if(!barcodeDetector.isOperational()) {
            // !!! In momentul in care aplicatia este instalata pentru prima data
            //GMS va descarca librarii native care ajuta la detectare. De obicei acesta
            // actiune se termina inainte ca aplicatia sa porneasca. Dar in caz ca descarcarea
            // nu s-a terminat, nu se vor detecta coduri de bare

            //isOperational() poate fi folosit pentru a verifica daca sunt disponibile librariile native
            //iar detectorul va fi facut automat operational
            Log.w(TAG, "Detector dependencies are not yet available.");

            //daca memoria este insuficienta, nu se vor descarca librariile, prin urmare nu va fie operational

            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = Objects.requireNonNull(getActivity()).registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(getContext(), "Face detector libraries cannot be downloaded due to low device storage memory", Toast.LENGTH_LONG).show();
            }
        }

            //creare si pornire camera.
            //rezolutie mare pt a detecta coduri mic de la mare disttanta
            CameraSource.Builder builder = new CameraSource.Builder(getContext(),barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1600,1024)
                    .setRequestedFps(15.0f);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                builder = builder.setFocusMode(
                        autofocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
            }

            cameraSource = builder
                    .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                    .build();

    }



    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(preview != null){
            preview.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(preview != null){
            preview.stop();
        }
    }



    private boolean onTap(float rawX, float rawY){
        int[] location = new int[2];
        graphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0])/ graphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1])/ graphicOverlay.getHeightScaleFactor();

        //tap on barcode box
        Barcode closer = null;
        float closerDistance = Float.MAX_VALUE;
        for(BarcodeGraphic graphic : graphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if(barcode.getBoundingBox().contains((int)x, (int)y)) {
                closer = barcode;
                break;
            }
            float dx = x - barcode.getBoundingBox().centerX();
            float dy = y - barcode.getBoundingBox().centerY();
            float distance = (dx * dx)+ (dy * dy); //
            if(distance < closerDistance){
                closer = barcode;
                closerDistance = distance;
            }
        }
        if(closer != null){
            Toast.makeText(getContext(),closer.rawValue,Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    @Override
    public void onBarcodeDetected(Barcode barcode) {
        //pot sa fac ceva cand a fost detectat
        Log.d("Barcode: ", barcode.rawValue);
    }

    @Override
    public void onCheckChanged(ArrayList<String> checks) {
        setFooterSize(checks);
    }

    private class CaptureGestureListener extends  GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            cameraSource.doZoom(detector.getScaleFactor());
        }
    }


    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ScannerFragment.OnScannerInteractionListener) {
            mListener = (ScannerFragment.OnScannerInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnScannerInteractionListener {
        void onFragmentInteraction(String uri);
    }
}
