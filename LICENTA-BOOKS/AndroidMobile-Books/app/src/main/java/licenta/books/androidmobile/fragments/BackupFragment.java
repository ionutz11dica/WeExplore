package licenta.books.androidmobile.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.HomeActivity;
import licenta.books.androidmobile.database.AppRoomDatabase;
import licenta.books.androidmobile.patterns.BackupRestore.LocalBackup;
import licenta.books.androidmobile.patterns.BackupRestore.RemoteBackup;

import static licenta.books.androidmobile.interfaces.Constants.DATABASE_NAME;

public class BackupFragment extends Fragment   {
    OnFragmentInteractionListener listener;
    private RemoteBackup remoteBackup;
    private LocalBackup localBackup;

    private boolean isBackup = true;
    private Button backupbtn;
    private Button restore;
    com.google.android.gms.common.api.GoogleApiClient GAC;

    public BackupFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     View view = inflater.inflate(R.layout.fragment_backup, container, false);
     backupbtn = view.findViewById(R.id.backup_button);
     restore = view.findViewById(R.id.restore_button);
     remoteBackup = new RemoteBackup((HomeActivity)getActivity());
     localBackup = new LocalBackup((HomeActivity)getActivity());





     backupbtn.setOnClickListener(new View.OnClickListener() {
         @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
         @Override
         public void onClick(View v) {
//                AppRoomDatabase.getInstance(getContext()).close();
//                String outFileName = Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME + File.separator;
//                localBackup.performBackup(AppRoomDatabase.getInstance(getContext()),outFileName);

             int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
             if (permission == PackageManager.PERMISSION_GRANTED) {
                 AppRoomDatabase.getInstance(getContext()).close();

                 File db = getContext().getDatabasePath(DATABASE_NAME);
                 File dbShm = new File(db.getParent(), DATABASE_NAME + "-shm");
                 File dbWal = new File(db.getParent(), DATABASE_NAME + "-wal");

                 File db2 = new File("/sdcard/"+DATABASE_NAME, DATABASE_NAME);
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
     });

     restore.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
//                AppRoomDatabase.getInstance(getContext()).close();
//                localBackup.performRestore(AppRoomDatabase.getInstance(getContext()));
                 int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                 if(permission == PackageManager.PERMISSION_GRANTED) {
                     AppRoomDatabase.getInstance(getContext()).close();

                     File db = new File("/sdcard/"+DATABASE_NAME, DATABASE_NAME);
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
                 } else {
                     Snackbar.make(view, "Please allow access to your storage", Snackbar.LENGTH_LONG)
                             .setAction("Allow", view -> ActivityCompat.requestPermissions(getActivity(), new String[] {
                                     Manifest.permission.READ_EXTERNAL_STORAGE
                             }, 0)).show();
                 }
            }
        });

     return view;
    }









    static boolean file2Os(OutputStream os, File file) {
        boolean bOK = false;
        InputStream is = null;
        if (file != null && os != null) try {
            byte[] buf = new byte[4096];
            is = new FileInputStream(file);
            int c;
            while ((c = is.read(buf, 0, buf.length)) > 0)
                os.write(buf, 0, c);
            bOK = true;
        } catch (Exception e) {e.printStackTrace();}
        finally {
            try {
                os.flush(); os.close();
                if (is != null )is.close();
            } catch (Exception e) {e.printStackTrace();}
        }
        return  bOK;
    }

    void upload(final String titl, final File file, final String mime) {
        if (GAC != null && GAC.isConnected() && titl != null  && file != null) try {
            Drive.DriveApi.newDriveContents(GAC).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult contRslt) {
                    if (contRslt.getStatus().isSuccess()){
                        DriveContents cont = contRslt.getDriveContents();
                        if (cont != null && file2Os(cont.getOutputStream(), file)) {
                            MetadataChangeSet meta = new MetadataChangeSet.Builder().setTitle(titl).setMimeType(mime).build();
                            Drive.DriveApi.getRootFolder(GAC).createFile(GAC, meta, cont).setResultCallback(
                                    new ResultCallback<DriveFolder.DriveFileResult>() {
                                        @Override
                                        public void onResult(@NonNull DriveFolder.DriveFileResult fileRslt) {
                                            if (fileRslt.getStatus().isSuccess()) {
                                                Toast.makeText(getContext(),"Merge frt?",Toast.LENGTH_LONG).show();
                                                 fileRslt.getDriveFile();
                                            }
                                        }
                                    }
                            );
                        }
                    }
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

//    private void setBackupFolderTitle(DriveId id) {
//        id.asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
//                new ResultCallback<DriveResource.MetadataResult>() {
//                    @Override
//                    public void onResult(@NonNull DriveResource.MetadataResult result) {
//                        if (!result.getStatus().isSuccess()) {
//                            showErrorDialog();
//                            return;
//                        }
//                        Metadata metadata = result.getMetadata();
//                        folderTextView.setText(metadata.getTitle());
//                    }
//                }
//        );
//    }
//
//    private void openFolderPicker() {
//        try {
//            connectClient();
//            if (mGoogleApiClient!=null){
//                if (intentPicker==null)
//                    intentPicker = buildIntent();
//
//                // Start the intent to choose a folder
//                getActivity().startIntentSenderForResult(
//                        intentPicker, REQUEST_CODE_PICKER, null, 0, 0, 0);}
//        } catch (IntentSender.SendIntentException e) {
//            Log.e(TAG, "Unable to send intent", e);
//            showErrorDialog();
//        }
//    }
//    private IntentSender buildIntent(){
//        return Drive.DriveApi
//                .newOpenFileActivityBuilder()
//                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
//                .build(mGoogleApiClient);
//    }
//
//
//    private void openFilePicker() {
//        connectClient();
//        IntentSender intentSender = Drive.DriveApi
//                .newOpenFileActivityBuilder()
//                // these mimetypes enable these folders/files
//                // types to be selected
//                // In this case we only need to open plain text
//                .setMimeType(new String[] { DriveFolder.MIME_TYPE, "text/plain"})
//                .build(mGoogleApiClient);
//        try {
//            getActivity().startIntentSenderForResult(
//                    intentSender, REQUEST_CODE_SELECT, null, 0, 0, 0);
//        } catch (IntentSender.SendIntentException e) {
//            Log.e(TAG, "Unable to send intent", e);
//            showErrorDialog();
//        }
//    }
//
//
//    private void uploadToDrive(DriveId mFolderDriveId) {
//        if (mFolderDriveId != null) {
//            //Create the file on GDrive
//            final DriveFolder folder = mFolderDriveId.asDriveFolder();
//            Drive.DriveApi.newDriveContents(mGoogleApiClient)
//                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
//                        @Override
//                        public void onResult(DriveApi.DriveContentsResult result) {
//                            if (!result.getStatus().isSuccess()) {
//                                Log.e(TAG, "Error while trying to create new file contents");
//                                showErrorDialog();
//                                return;
//                            }
//                            final DriveContents driveContents = result.getDriveContents();
//
//                            // Perform I/O off the UI thread.
//                            new Thread() {
//                                @Override
//                                public void run() {
//                                    // write content to DriveContents
//                                    OutputStream outputStream = driveContents.getOutputStream();
//
//                                    FileInputStream inputStream = null;
//                                    try {
//                                        inputStream = new FileInputStream(getContext().getDatabasePath(DATABASE_NAME));
//                                    } catch (FileNotFoundException e) {
//                                        showErrorDialog();
//                                        e.printStackTrace();
//                                    }
//
//                                    byte[] buf = new byte[1024];
//                                    int bytesRead;
//                                    try {
//                                        if (inputStream != null) {
//                                            while ((bytesRead = inputStream.read(buf)) > 0) {
//                                                outputStream.write(buf, 0, bytesRead);
//                                            }
//                                        }
//                                    } catch (IOException e) {
//                                        showErrorDialog();
//                                        e.printStackTrace();
//                                    }
//
//
//                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                                            .setTitle("database.room")
//                                            .setMimeType("text/plain")
//                                            .build();
//
//                                    // create a file in selected folder
//                                    folder.createFile(mGoogleApiClient, changeSet, driveContents)
//                                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
//                                                @Override
//                                                public void onResult(DriveFolder.DriveFileResult result) {
//                                                    if (!result.getStatus().isSuccess()) {
//                                                        Log.d(TAG, "Error while trying to create the file");
//                                                        showErrorDialog();
//                                                        getActivity().finish();
//                                                        return;
//                                                    }
//                                                    showSuccessDialog();
//                                                    getActivity().finish();
//                                                }
//                                            });
//                                }
//                            }.start();
//                        }
//                    });
//        }
//    }
//
//
//
//    private void downloadFromDrive(DriveFile file) {
//        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
//                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
//                    @Override
//                    public void onResult(DriveApi.DriveContentsResult result) {
//                        if (!result.getStatus().isSuccess()) {
//                            showErrorDialog();
//                            return;
//                        }
//
//                        // DriveContents object contains pointers
//                        // to the actual byte stream
//                        DriveContents contents = result.getDriveContents();
//                        InputStream input = contents.getInputStream();
//
//                        try {
//                            File file = getContext().getDatabasePath(DATABASE_NAME);
//                            OutputStream output = new FileOutputStream(file);
//                            try {
//                                try {
//                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
//                                    int read;
//
//                                    while ((read = input.read(buffer)) != -1) {
//                                        output.write(buffer, 0, read);
//                                    }
//                                    output.flush();
//                                } finally {
//                                    output.close();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } finally {
//                            try {
//                                input.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//    }
//
//
//    @Override
//    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        switch (requestCode) {
//            case REQUEST_CODE_PICKER:
//                intentPicker = null;
//
//                if (resultCode == RESULT_OK) {
//                    //Get the folder drive id
//                    DriveId mFolderDriveId = (DriveId) data.getParcelableExtra(
//                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
//
//                    uploadToDrive(mFolderDriveId);
//                }
//                break;
//
//            case REQUEST_CODE_SELECT:
//                if (resultCode == RESULT_OK) {
//                    // get the selected item's ID
//                    DriveId driveId = (DriveId) data.getParcelableExtra(
//                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
//
//                    DriveFile file = driveId.asDriveFile();
//                    downloadFromDrive(file);
//
//                } else {
//                    showErrorDialog();
//                }
//                getActivity().finish();
//                break;
//
//        }
//    }
//
//    public void connectClient() {
//        backup.start();
//    }
//
//    public void disconnectClient() {
//        backup.stop();
//    }
//
//
//    private void showSuccessDialog() {
//        Toast.makeText(getContext(), "Succes", Toast.LENGTH_SHORT).show();
//    }
//
//    private void showErrorDialog() {
//        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
//    }

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
