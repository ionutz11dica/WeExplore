package licenta.books.androidmobile.activities.others;

public class Title {
    private String title;
    private String imageLink;

    public Title(String title, String imageLink) {
        this.title = title;
        this.imageLink = imageLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
