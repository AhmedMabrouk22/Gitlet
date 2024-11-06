package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.join;
import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 *  @author Ahmed Mabrouk
 */
public class Commit implements Serializable {

    /** commit hash */
    private String commitId;
    private String parent;
    private Date timestamp;
    private String message;
    private Map<String,String> trackedBlobs;
    public Commit(String message, Date timestamp, String parent, Map<String,String> trackedBlobs) {
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : new Date();
        this.parent = parent;
        this.trackedBlobs = trackedBlobs != null ? trackedBlobs :new HashMap<>();
        this.commitId = generateHash();
    }

    private String generateHash() {
        List<Object> items = new ArrayList<>();
        items.add(message);
        items.add(timestamp.toString());
        items.add(parent);
        for (Map.Entry<String,String> blob : trackedBlobs.entrySet())
            items.add(blob.toString());
        return sha1(items);
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getTrackedBlobs() {
        return trackedBlobs;
    }

    public void setTrackedBlobs(Map<String, String> trackedBlobs) {
        this.trackedBlobs = trackedBlobs;
    }
}
