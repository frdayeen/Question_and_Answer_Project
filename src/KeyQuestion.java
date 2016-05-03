
public class KeyQuestion {
	
	public KeyQuestion(String text){
		this.text = text;
	}
	public KeyQuestion(String id, String text){
		this.id = id;
		this.text = text;
	}
	public KeyQuestion(String id, String text, char cat){
		this.id = id;
		this.text = text;
		this.category = cat;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public char getCategory() {
		return category;
	}
	public void setCategory(char category) {
		this.category = category;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Question [id=" + id + ", text=" + text + ", category="
				+ category + "]";
	}
	private String id = "no-id";
	
	private String text;
	private char category;  
}
