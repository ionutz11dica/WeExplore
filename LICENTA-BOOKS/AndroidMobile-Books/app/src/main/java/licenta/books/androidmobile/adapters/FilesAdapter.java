package licenta.books.androidmobile.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.interfaces.Constants;

public class FilesAdapter extends ArrayAdapter<File> {
    private final Activity context;
    private final File[] files;

    public FilesAdapter(Activity context, File[] content) {
        super(context, R.layout.row_chapter_lv, content);

        this.context = context;
        this.files = content;

    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View view, ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.row_backup, null, true);
        TextView folderName = rowView.findViewById(R.id.name_file);
        TextView modifiedDate = rowView.findViewById(R.id.created_date);

        folderName.setText(files[position].getName());
        modifiedDate.setText(Constants.sdf.format(files[position].lastModified()));


        return rowView;

    }
}