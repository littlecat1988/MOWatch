package care.adapter;

public class DateText {
	private String date;
	private String text;
	private String text_title;
	private String text_time;

	public DateText() {

	}

	public DateText(String date, String text, String text_title, String text_time) {
		super();
		this.date = date;
		this.text = text;
		this.text_title = text_title;
		this.text_time = text_time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	
	public String getTextTitle() {
		return text_title;
	}

	public void setTextTitle(String text_title) {
		this.text_title = text_title;
	}

	public String getTime() {
		return text_time;
	}

	public void setTime(String text_time) {
		this.text_time = text_time;
	}

}
