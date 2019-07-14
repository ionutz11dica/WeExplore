package licenta.books.androidmobile.patterns.BackupRestore;

import android.arch.persistence.room.RoomDatabase;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.activities.HomeActivity;
import licenta.books.androidmobile.database.AppRoomDatabase;

import static licenta.books.androidmobile.interfaces.Constants.DATABASE_NAME;

public class LocalBackup {

    private HomeActivity activity;

    public LocalBackup(HomeActivity activity) {
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void performBackup(final AppRoomDatabase roomDatabase, final String outFileName) {
        Permissions.verifyStoragePermissions(activity);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator +DATABASE_NAME);

        boolean success = true;
        if(!folder.exists())
            success = folder.mkdirs();
        if(success){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Backup Name");
            final EditText input = new EditText(activity);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Save", (dialog, which) -> {
                String m_Text = input.getText().toString();
                String out = outFileName + m_Text + ".db";
                roomDatabase.backup(out,activity.getApplicationContext());
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        }else
            Toast.makeText(activity, "Unable to create directory. Retry", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void performRestore(final AppRoomDatabase roomDatabase) {

        Permissions.verifyStoragePermissions(activity);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME);
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item);
            for (File file : files)
                arrayAdapter.add(file.getName());

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
            builderSingle.setTitle("Restore:");
            builderSingle.setNegativeButton(
                    "cancel",
                    (dialog, which) -> dialog.dismiss());
            builderSingle.setAdapter(
                    arrayAdapter,
                    (dialog, which) -> {
                        try {
                            roomDatabase.importDB(files[which].getPath(),activity.getApplicationContext());
                        } catch (Exception e) {
                            Toast.makeText(activity, "Unable to restore. Retry", Toast.LENGTH_SHORT).show();
                        }
                    });
            builderSingle.show();
        } else
            Toast.makeText(activity, "Backup folder not present.\nDo a backup before a restore!", Toast.LENGTH_SHORT).show();
    }
}
