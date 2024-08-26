package music.app.my.music.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import java.io.File


class PlaylistFilemaker() {
    companion object {
    }
        fun newPlaylist(ctx: Context, fname: String): Intent {
            var pfname : String = Environment.getExternalStorageDirectory().getPath() + "/music/playlists/"
            var f: File = File(pfname)
            val path: Uri = Uri.fromFile(f)
            return createFile(path, fname, ctx)
        }

        // Request code for creating a PDF document.
        val CREATE_FILE = 1

        private fun createFile(pickerInitialUri: Uri, name: String, ctx: Context): Intent {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/m3u"
                putExtra(Intent.EXTRA_TITLE, name + ".m3u")

                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker before your app creates the document.
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)

            }
            return intent;
//            ctx.startActivity(intent)
        }


    fun exportPlaylist( name: String, id :String){ //}, items: ArrayList<Song>){

        var pfname : String = Environment.getExternalStorageDirectory().getPath() + "/Music/playlists/" + name + ".m3u"
        var f: File = File(pfname)
//                f.createNewFile()
//        val path: Uri = Uri.fromFile(f)
//          o
        var data: String = "test ........"
//        for(a :Song in items) data += a.getTitle() + " " + a.getFilePath()  + "/n"
        f.writeText(data)

    }

}