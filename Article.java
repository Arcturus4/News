package news2;

public class Article {
    private String title;
    private String url;
    private String description;
    private String publishedAt;
    private String content;

    public Article(String title, String url, String description, String publishedAt, String content) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.publishedAt = publishedAt;
        this.content = content;
    }

	protected String getTitle() {
		return title;
	}

	protected void setTitle(String title) {
		this.title = title;
	}

	protected String getUrl() {
		return url;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	protected String getDescription() {
		return description;
	}

	protected void setDescription(String description) {
		this.description = description;
	}

	protected String getPublishedAt() {
		return publishedAt;
	}

	protected void setPublishedAt(String publishedAt) {
		this.publishedAt = publishedAt;
	}

	protected String getContent() {
		return content;
	}

	protected void setContent(String content) {
		this.content = content;
	}
}