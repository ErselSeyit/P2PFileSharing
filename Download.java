public class Download {
    private String fileName;
    private long bytesTransferred;
    private long totalBytes;

    public Download(String fileName, long bytesTransferred, long totalBytes) {
        this.fileName = fileName;
        this.bytesTransferred = bytesTransferred;
        this.totalBytes = totalBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public long getBytesTransferred() {
        return bytesTransferred;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public double getPercentage() {
        return (double) bytesTransferred / totalBytes * 100;
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f%%", fileName, getPercentage());
    }
}
