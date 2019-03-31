package licenta.books.androidmobile.downloadProgress;

public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
