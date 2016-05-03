
import java.util.Scanner;

public class CS421_part2 {
	
	CategoryClassifier classifier = new CategoryClassifier(1.0);
	MoviesKeywords mBuilder = new MoviesKeywords();
	GeographyKeywords  gBuilder = new GeographyKeywords();
	MusicKeywords sBuilder = new MusicKeywords();
	String sqlquery = "";
	
	SQLGenerator test;
	
	public CS421_part2(){
		test = new SQLGenerator();
	}
	
	public String answerQuestion(String question) {
		// TODO Auto-generated method stub
		String results = "";
		try{
			String label = classifier.classify(question);
			test.setSentence(question);
			test.sentence();
			if (test.outputQuery() != null)
			{
				sqlquery = test.outputQuery();
				results = SQLiteConn.getSQLResult(DataBase.getMovieDB(), sqlquery);
				if(Str.isNumber(results)){
					Integer res = Integer.parseInt(results);
					if(res == 0) results= "NO";
					else if(res > 0)results = "YES";
				}
				return results;
			}
			if(label.equals("M")){			
				mBuilder.generateQuestionVector(question);
				sqlquery = mBuilder.generateSQL();
				results = SQLiteConn.getSQLResult(DataBase.getMovieDB(), sqlquery);
			}else if(label.equals("S")){
				sBuilder.generateQuestionVector(question);
				sqlquery = sBuilder.generateSQL();
				results = SQLiteConn.getSQLResult(DataBase.getMusicDB(), sqlquery);
			}else if(label.equals("G")){
				gBuilder.generateQuestionVector(question);
				sqlquery = gBuilder.generateSQL();
				results = SQLiteConn.getSQLResult(DataBase.getGeographyDB(), sqlquery);
			}
			if(KeywordfromParser.questionType(question).equals("YES-NO")){
				if(Str.isNumber(results)){
					Integer res = Integer.parseInt(results);
					if(res == 0) results= "NO";
					else if(res > 0)results = "YES";
				}
			}
		}catch(Exception e){
			return "";
		}
		
		return results;
	}

	public static void main(String[] args) {

		System.out.println("Welcome! This is MiniWatson.");
		System.out.println("Please ask a question. Type 'q' when finished.");
		System.out.println();
		
		
		
		CS421_part2 watson = new CS421_part2();
		
		Scanner keyboard = new Scanner(System.in);
		
		String question = keyboard.nextLine().trim();
		while(!question.equalsIgnoreCase("q")){

			System.out.println("<QUERY>\n" + question);
			
			String answer = watson.answerQuestion(question);
			
			System.out.println("<SQL>\n" + watson.sqlquery);
			
			if (answer.trim().isEmpty())
				answer = "I don't know.";
			
			System.out.println("<Ans:>\n " + answer);
			
			question = keyboard.nextLine().trim();
		}//end of while
		keyboard.close();
		System.out.println("Goodbye.");

	}
}
