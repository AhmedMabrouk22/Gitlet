package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class BlobService {
    private final File BLOB_DIR;

    public BlobService(File blobDir) {
        BLOB_DIR = blobDir;
    }

    public String saveBlob(File file) {
        String blobContent = readContentsAsString(file);
        String blobId = sha1(blobContent);
        File blob = join(BLOB_DIR,blobId);
        writeContents(blob,blobContent);
        return blobId;
    }

    public File getBlob(String blobName) {
        return join(BLOB_DIR,blobName);
    }

}
