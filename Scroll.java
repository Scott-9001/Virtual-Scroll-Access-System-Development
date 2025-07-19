import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Scroll {
    private String name;
    private String scrollId;
    private String originalType;
    private String uploaderId;
    private LocalDate uploadDate;
    private int downloadCount;

    // Constructor for a scroll creation
    public Scroll(String name, String scrollId, String originalType, String uploaderId) {
        this.name = name;
        this.scrollId = scrollId;
        this.originalType = originalType;
        this.uploaderId = uploaderId;
        this.uploadDate = LocalDate.now();
        this.downloadCount = 0;
    }

    // Constructor for a scroll load
    public Scroll(String name, String scrollId, String originalType, String uploaderId, String uploadDate, String downloadCount) {
        this.name = name;
        this.scrollId = scrollId;
        this.originalType = originalType;
        this.uploaderId = uploaderId;
        this.uploadDate = loadDate(uploadDate);
        this.downloadCount = Integer.parseInt(downloadCount);
    }

    public LocalDate loadDate(String uploadDate) {
        try {
            return LocalDate.parse(uploadDate);
        } catch (DateTimeParseException e) {
            System.out.println("Issue loading date. Setting it to today.");
            return LocalDate.now();
        }
    }
    // Getters + Setters
    // Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // scroll Id
    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    // scroll data type
    public String getOriginalType() {
        return originalType;
    }

    public void setOriginalType(String scrollId) {
        this.scrollId = scrollId;
    }

    // scroll uploader Id
    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    // scroll upload Date
    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate() {
        this.uploadDate = LocalDate.now();
    }

    // scroll download
    public int getDownloadCount() {
        return downloadCount;
    }

    public void downloadScroll() {
        downloadCount++;
    }


    @Override
    public String toString() {
        return name + "," + scrollId + "," + originalType + "," + uploaderId + "," + uploadDate.toString() + "," + downloadCount;
    }

    public String viewInfo() {
        return name + " (ID: " + scrollId + ", Uploaded by: " + uploaderId + ", Upload Date: " + uploadDate.toString() + ")";
    }
}
