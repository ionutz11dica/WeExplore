package licenta.books.androidmobile.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.HomeActivity;
import licenta.books.androidmobile.adapters.FilesAdapter;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.patterns.BackupRestore.LocalBackup;
import licenta.books.androidmobile.patterns.BackupRestore.RemoteBackup;

import static android.graphics.Typeface.BOLD;
import static licenta.books.androidmobile.fragments.SearchFragment.hideKeyboardFrom;
import static licenta.books.androidmobile.interfaces.Constants.DATABASE_NAME;

public class BackupFragment extends Fragment   {
    OnFragmentInteractionListener listener;

    private Toolbar toolbarBackup;
    private ListView backupLv;
    private FilesAdapter adapter;
    private FloatingActionButton fab;
    File[] files = new File[0];
    public BackupFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     View view = inflater.inflate(R.layout.fragment_backup, container, false);
     backupLv = view.findViewById(R.id.backup_lv);
     toolbarBackup = view.findViewById(R.id.backup_toolbar);
     fab = view.findViewById(R.id.fab);

//     localBackup = new LocalBackup((HomeActivity)getActivity());


     File folder = new File(Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME);
     if(folder.exists()){
         files = folder.listFiles();
          adapter = new FilesAdapter(getActivity(),files);
          backupLv.setAdapter(adapter);
     }
        toolbarBackup.setTitle("Backup & Restore  -> "+files.length + " files");
        toolbarBackup.setTitleTextAppearance(getContext(),R.style.CrimsonTextAppearance);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File folder = new File(Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME);
                String outFileName = Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME + File.separator;

                boolean successParent = true;
                AtomicBoolean successChild = new AtomicBoolean(true);
                if(!folder.exists()){
                    successParent = folder.mkdirs();
                }
                if(successParent){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Backup Name");
                    builder.setMessage("This will reset your application.");
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setSingleLine(true);
                    builder.setView(input);
                    builder.setPositiveButton("Save", (dialog, which) -> {
                        if(input.getText().toString().length() >= 1) {
                            String m_Text = input.getText().toString();
                            String out = outFileName + m_Text + ".db";
                            String outCopy = m_Text + ".db";
                            File folderChild = new File(out);
                            if (!folderChild.exists()) {
                                successChild.set(folderChild.mkdirs());
                            }
                            if (successChild.get()) {
                                backupDb(v, outCopy);
                                hideKeyboardFrom(getContext(),v);
                                Thread t = new Thread(() -> System.exit(0));
                                Handler handler = new Handler();
                                handler.postDelayed(t,1000);

                            }
                        }else{
                            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.edit_animation);
                            input.startAnimation(shake);
                        }
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                    builder.show();
                }


            }
        });

        backupLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permission == PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Restore "+ files[position].getName());
                builder.setIcon(R.drawable.ic_restore);
//                TextView test = new TextView(getContext());
//                test.setText("Restore "+files[position].getName());
//                test.setTextSize(25);
//                test.setTextAppearance(BOLD);
//                test.set
//                test.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/"+"crimsontext"+".ttf"));
//                builder.setCustomTitle(test);
                builder.setMessage("This will reset your application.");
                builder.setPositiveButton("Accept", (dialog, which) -> {
                    restoreDb(files[position]);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
                } else {
                     Snackbar.make(view, "Please allow access to your storage", Snackbar.LENGTH_LONG)
                             .setAction("Allow", view -> ActivityCompat.requestPermissions(getActivity(), new String[] {
                                     Manifest.permission.READ_EXTERNAL_STORAGE
                             }, 0)).show();
                 }
            }
        });

        backupLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Backup");
                builder.setPositiveButton("Delete", (dialog, which) -> {
                    String path = "/sdcard/"+DATABASE_NAME+File.separator+ files[position].getName();
                    File file = new File(path);
                    boolean deleted=deleteDirectory(file);
                    if(deleted) {
//                        deleteFilesFromArray(position);
                        if(folder.exists()){
                            files = folder.listFiles();
                            adapter = new FilesAdapter(getActivity(),files);
                            backupLv.setAdapter(adapter);
                        }
                        toolbarBackup.setTitle("Backup & Restore  -> "+files.length + " files");
                    }

                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
                return true;
            }
        });



     return view;
    }

    public void deleteFilesFromArray(int pos){
        for(int i = 0; i < files.length; i++){
            if(i == pos){
                // shifting elements
                for(int j = i; j < files.length - 1; j++){
                    files[j] = files[j+1];
                }
            }
        }
    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }

    private void restoreDb(File file) {
        AppRoomDatabase.getInstance(getContext()).close();

        File db = new File("/sdcard/"+DATABASE_NAME+File.separator+ file.getName(), DATABASE_NAME);
        File dbShm = new File(db.getParent(), DATABASE_NAME + "-shm");
        File dbWal = new File(db.getParent(), DATABASE_NAME + "-wal");

        File db2 = getContext().getDatabasePath(DATABASE_NAME);
        File dbShm2 = new File(db2.getParent(), DATABASE_NAME + "-shm");
        File dbWal2 = new File(db2.getParent(), DATABASE_NAME + "-wal");

        try {
            FileUtils.copyFile(db, db2);
            FileUtils.copyFile(dbShm, dbShm2);
            FileUtils.copyFile(dbWal, dbWal2);
        } catch (Exception e) {
            Log.e("RESTOREDB", e.toString());
        }finally {
            AppRoomDatabase.getInstance(getContext());
        }
    }

    private void backupDb(View v,String out) {
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            AppRoomDatabase.getInstance(getContext()).close();

            File db = getContext().getDatabasePath(DATABASE_NAME);
            File dbShm = new File(db.getParent(), DATABASE_NAME + "-shm");
            File dbWal = new File(db.getParent(), DATABASE_NAME + "-wal");

            File db2 = new File("/sdcard/"+DATABASE_NAME+File.separator+out, DATABASE_NAME);
            File dbShm2 = new File(db2.getParent(), DATABASE_NAME + "-shm");
            File dbWal2 = new File(db2.getParent(), DATABASE_NAME + "-wal");

            try {
                FileUtils.copyFile(db, db2);
                FileUtils.copyFile(dbShm, dbShm2);
                FileUtils.copyFile(dbWal, dbWal2);
            } catch (Exception e) {
                Log.e("SAVEDB", e.toString());
            }
        } else {
            Snackbar.make(v, "Please allow access to your storage", Snackbar.LENGTH_LONG)
                    .setAction("Allow", view -> ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 0)).show();
        }
    }


    public void onButtonPressed(Uri uri) {
        if (listener != null) {
            listener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BackupFragment.OnFragmentInteractionListener) {
            listener = (BackupFragment.OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;

    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
