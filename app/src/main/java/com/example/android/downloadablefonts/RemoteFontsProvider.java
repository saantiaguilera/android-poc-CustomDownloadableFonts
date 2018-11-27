package com.example.android.downloadablefonts;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.FontsContractCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * File content provider that provides my own remote fonts. This way I can download them from somewhere
 * and provide my own fonts
 */
public class RemoteFontsProvider extends FileProvider {

    private final int id = 107;

    public RemoteFontsProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(projection);

        // TODO Read the selection arguments / selection / etc, and build the row accordingly
        // TODO Validate authority, create a uri matcher, retrieve sorting order and stuff

        MatrixCursor.RowBuilder row = cursor.newRow();

        for (String projectionCol : projection) {
            Object value;
            switch (projectionCol) {
                case FontsContractCompat.Columns._ID:
                    value = 0;
                    break;
                case FontsContractCompat.Columns._COUNT:
                    value = 1;
                    break;
                case FontsContractCompat.Columns.FILE_ID:
                    try {
                        value = writeFile();
                    } catch (IOException e) {
                        value = -1;
                    }
                    break;
                case FontsContractCompat.Columns.ITALIC:
                    value = 0;
                    break;
                case FontsContractCompat.Columns.WEIGHT:
                    value = 400;
                    break;
                case FontsContractCompat.Columns.RESULT_CODE:
                    value = FontsContractCompat.Columns.RESULT_CODE_OK;
                    break;
                case FontsContractCompat.Columns.TTC_INDEX:
                    value = 0;
                    break;
                case FontsContractCompat.Columns.VARIATION_SETTINGS:
                    value = null;
                    break;
                default:
                    value = null;
            }

            if (value != null) {
                row.add(projectionCol, value);
            }
        }

        return cursor;
    }

    /**
     * We just expose a font we have in an asset to the developers. In a real world scenario, you
     * should instead download it from some source you've got and expose it just as I do.
     *
     * @return id of the font
     * @throws IOException exception thrown
     */
    private Object writeFile() throws IOException {
        // This is the file we will write (hence, the file that we "download" and write would be this one)
        File targetFile = new File(getContext().getFilesDir(), "fonts" + File.separator + String.valueOf(id));

        // TODO, This is just for the sake of working fast, but you should have a more robust logic,
        // or even a cache if you plan to download a lot of fonts
        if (targetFile.exists()) {
            expose(targetFile);
            return id;
        }

        // Write our "downloaded font" (the assets/font) to the targetFile
        targetFile.createNewFile();

        AssetManager am = getContext().getAssets();
        InputStream is = am.open("fonts/Roboto.ttf");

        byte[] buffer = new byte[is.available()];
        is.read(buffer);

        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);

        expose(targetFile);

        // Return the id, we have to provide their FontsContract an id.
        return id;
    }

    private void expose(File targetFile) {
        // Create a uri exposed by this provider that maps the targetFile. Provide read access!
        Uri uri = getUriForFile(getContext(), "com.saantiaguilera.testing.authority", targetFile);
        // TODO Grant if >19 also Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        getContext().grantUriPermission(getContext().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
