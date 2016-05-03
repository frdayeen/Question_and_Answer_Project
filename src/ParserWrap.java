

import java.util.ArrayList;


public class ParserWrap {
	static CategoryClassifier classifier = new CategoryClassifier(1.0);
	static ArrayList<String> movieNameList = SQLiteConn.getMovieTitle();
	static ArrayList<String> movieWorkerNameList = SQLiteConn.getActorDirector(); 
	public static String questionToResult(String question){
		 String cat = classifier.classify(question);
		 String dbName = "";
		 if(cat.startsWith("G"))
			 dbName = DataBase.getGeographyDB();
		 else if(cat.startsWith("M"))
			 dbName = DataBase.getMovieDB();
		 else if(cat.startsWith("S"))
			 dbName = DataBase.getMusicDB();
		 else{
			 return "Category classifier fails, unable to determine which db to use!!! :("; 
		 }
		 
		 // append the question mark if not exist
		 if(question.endsWith("?")){
			question = question.substring(0, question.length()-1) + " ?"; 
		 }else{
			question +=" ?"; 
		 }
		 
		
		 // annotate proper noun phrase
		 question = annotateNNP(cat, question);
//		 System.out.println("\n��Annotated question�� :" + question);
		 String sql = SemAttach.questionToSQL(cat, question);
//		 System.out.println("\n��Annotated question SQL��:" + sql);	 
		 // de-annotate proper noun phrase
		 sql = sql.replaceAll("\\^", " "); 
		 sql = sql.replaceAll("\\@", "''");
		 
		 sql = sql.replaceAll("MOVIE-", "");
		 sql = sql.replaceAll("MOVIEWORKER-", "");
//		 System.out.println("\n��De-Annotated question SQL��:" + sql);

		 return SQLiteConn.getSQLResult(dbName, sql);
	}
	
	public static String annotateNNP(String cat, String question){
		 if(cat.equals("G")){
			 
		 }else if(cat.equals("M")){
			 for (String movieName : movieNameList) {
				 int index = question.toLowerCase().indexOf(movieName.toLowerCase());
				 if(index>=0){
					 movieName = movieName.replaceAll(" ", "^");
					 movieName = movieName.replaceAll("'", "@");
					 movieName = movieName;
					 question = question.substring(0,index) + "MOVIE-"+ movieName.toUpperCase()
							    + question.substring(index + movieName.length());
				 }
			 }
			 for (String movieWorkerName : movieWorkerNameList) {
				 int index = question.toLowerCase().indexOf(movieWorkerName.toLowerCase());
				 if(index>=0){
					 movieWorkerName = movieWorkerName.replaceAll(" ", "^");
					 movieWorkerName = movieWorkerName.replaceAll("'", "@");
					 movieWorkerName = movieWorkerName;
					 question = question.substring(0,index) + "MOVIEWORKER-"+ movieWorkerName.toUpperCase()
							    + question.substring(index + movieWorkerName.length());
				 }
			}
		 }else if(cat.equals("S")){
			 
		 }
		 return question;
	}
	public static void main(String[] args) {
		String question = "Did Swank win the oscar in 2000?";
		System.out.println("Question :" + question);
		System.out.println(ParserWrap.questionToResult(question));
	}
	
}
