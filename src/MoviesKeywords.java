

import java.util.ArrayList;


import edu.stanford.nlp.parser.lexparser.LexicalizedParser;



public class MoviesKeywords extends KeywordfromParser {
	
	//-------------Entities--------------
	final int MOVIEWORKERNAME = 0;
	final int MOVIENAME = 1;
	final int MOVIEWORKERNAME2 = 2;
	final int MOVIENAME2 = 3;
	//-------------Entitiy Aspects---------------
	final int DOB = 4;
	final int POB = 5;
	final int MOVIEYEAR = 6;
	final int RATING = 7;
	final int RUNTIME = 8;
	final int GENRE = 9;
	final int EARNINGS_RANK = 10;
	//-------------Relation Aspects------------
	final int AWARDTYPE = 11;
	final int AWARDYEAR = 12;
	//-------------Events-----------------------
	final int ACT = 13; 	 		//including act play star
	final int DIRECT = 14;      	//direct
	final int WIN = 15;
	final int CMP = 16;
	final int SIZE = 17;

	ArrayList<String> actor_director = SQLiteConn.getActorDirector();
	ArrayList<String> movie_title = SQLiteConn.getMovieTitle();
	ArrayList<String> actor_director_pob = SQLiteConn.getPOB();
	
	final String oscar_actor    = "11000000000101000";
	final String oscar_director = "11000000000100100";
	final String oscar_winner   = "11000000000100000";
	
	final String oscar2	     	 = "10000000000101000";
	final String directoroscar2	     = "10000000000100100";
	final String personoscar2			 = "10000000000100000";
	final String movieoscar2			 = "01000000000100000";
	
	final String ActorMovie              = "11000000000001000";
	final String DirectorMovie           = "11000000000000100";
	
	final String DOBofPerson             = "10001000000000000";
	final String POBofPerson             = "10000100000000000";
	
	final String MYearofMovie            = "01000010000000000";
	final String RatingofMovie			 = "01000001000000000";
	final String RuntimeOfMovie          = "01000000100000000";
	final String GenreOfMovie            = "01000000010000000";
	final String EarningsrankOfMovie     = "01000000001000000";
	
	final String CMPofPerson             = "10100000000000001";
	final String CMPofMovie	             = "01010000000000001";
	
	@Override
	public void generateQuestionVector(String question) {
		// TODO Auto-generated method stub
		qvector = new int[SIZE];
		sems = new String[SIZE];
		generateEntities(question);		
		generateEvent(question);
		generateEntityAspects(question);
		generateRelationAspects(question);
	}
	@Override
	public String generateSQL() {
		String SELECT = "SELECT distinct";
		String FROM = "";
		String WHERE = "";
		String pattern = "";
		for (int bit : qvector) {
			pattern += bit;
		}
		//System.out.println(pattern);
		
		//----------------- begin build SELECT WHERE ------------------------------
		if(sems[MOVIEWORKERNAME] != null){
			if(sems[MOVIEWORKERNAME].equals("?")) SELECT+=" Person.name,";
			else {
				WHERE += " Person.name like '%"+sems[MOVIEWORKERNAME]+"' and";
			}
		}
		if(sems[MOVIENAME] != null){
			if(sems[MOVIENAME].equals("?")) SELECT+=" Movie.name,";
			else {
				WHERE += " Movie.name like '%"+Str.SQLiteStringGetValid(sems[MOVIENAME])+"%' and";
			}
		}
		if(sems[DOB] != null){
			if(sems[DOB].equals("?")) SELECT+=" Person.dob,";
			else {
				WHERE += " Person.dob='"+sems[DOB]+"' and";
			}
		}
		if(sems[POB] != null){
			if(sems[POB].equals("?")) SELECT+=" Person.pob,";
			else {
				WHERE += " Person.pob='"+sems[POB]+"' and";
			}
		}
		if(sems[MOVIEYEAR] != null){
			if(sems[MOVIEYEAR].equals("?")) SELECT+=" Movie.year,";
			else {
				WHERE += " Movie.year="+sems[MOVIEYEAR]+" and";
			}
		}
		if(sems[RATING] != null){
			if(sems[RATING].equals("?")) SELECT+=" Movie.rating,";
			else {
				WHERE += " Movie.rating='"+sems[RATING]+"' and";
			}
		}
		if(sems[RUNTIME] != null){
			if(sems[RUNTIME].equals("?")) SELECT+=" Movie.runtime,";
			else {
				WHERE += " Movie.runtime="+sems[RUNTIME]+" and";
			}
		}
		if(sems[GENRE] != null){
			if(sems[GENRE].equals("?")) SELECT+=" Movie.genre,";
			else {
				WHERE += " Movie.genre='"+sems[GENRE]+"' and";
			}
		}
		if(sems[EARNINGS_RANK] != null){
			if(sems[EARNINGS_RANK].equals("?")) SELECT+=" Movie.earnings_rank,";
			else {
				WHERE += " Movie.earnings_rank="+sems[EARNINGS_RANK]+" and";
			}
		}
		if(sems[AWARDTYPE]!=null){
			if(sems[AWARDTYPE].equals("?")) SELECT+=" Oscar.type,";
			else if(!sems[AWARDTYPE].equals("*")){
				WHERE += " Oscar.type='"+sems[AWARDTYPE]+"' and";
			}
		}
		if(sems[AWARDYEAR]!=null){
			if(sems[AWARDYEAR].equals("?")) SELECT+=" Oscar.year,";
			else {
				WHERE += " Oscar.year='"+sems[AWARDYEAR]+"' and";
			}
		}
		//----------------- end Build SELECT WHERE --------------------------------------
		
		//----------------- begin pattern matching CMP ------------------------------
		if(Str.bitCover(pattern, CMPofPerson) || Str.bitCover(pattern, CMPofMovie)  ){
			String sql1= "", sql2="";
			if(Str.bitCover(pattern, CMPofPerson)) {
				FROM = "FROM Person";
			}else {
				FROM = "FROM Movie";
			}
			if(SELECT.endsWith(",")) SELECT = SELECT.substring(0,SELECT.length()-1);
			
			if(WHERE.length()>0) WHERE = " WHERE "+ WHERE.substring(0, WHERE.length()-"and".length());
			sql1= SELECT + " " + FROM + " " + WHERE + " limit 0, 1 ";
			
			WHERE = "";
			
			if(sems[MOVIEWORKERNAME2] != null){
				if(sems[MOVIEWORKERNAME2].equals("?")) SELECT+=" Person.name,";
				else {
					WHERE += " Person.name like '%"+sems[MOVIEWORKERNAME2]+"' and";
				}
			}
			if(sems[MOVIENAME2] != null){
				if(sems[MOVIENAME2].equals("?")) SELECT+=" Movie.name,";
				else {
					WHERE += " Movie.name like '%"+Str.SQLiteStringGetValid(sems[MOVIENAME2])+"%' and";
				}
			}
			if(WHERE.length()>0) WHERE = " WHERE "+ WHERE.substring(0, WHERE.length()-"and".length());
			sql2 = SELECT + " " + FROM + " " + WHERE + " limit 0, 1 ";
			System.out.println("sql1="+sql1);
			System.out.println("sql2="+sql2);
			
			return "SELECT ("+"("+sql1+")"+sems[CMP]+"("+sql2+"))";
		}
		
		//----------------- begin pattern matching NON-CMP ------------------------------
		if(Str.bitCover(pattern, oscar_actor)){
			FROM += "FROM Person join Actor on Person.id = Actor.actor_id join Oscar on Actor.movie_id = Oscar.movie_id " +
					"join Movie on Movie.id = Oscar.movie_id";
		}else if(Str.bitCover(pattern, oscar_director)){
			FROM += "FROM Movie join Oscar on Movie.id = Oscar.movie_id join Director on Director.movie_id = Movie.id " +
					"join Person on Person.id = Director.director_id";
		}
		else if(Str.bitCover(pattern, oscar_winner)){
			FROM += "FROM Movie join Oscar on Movie.id = Oscar.movie_id join Person on Person.id = Oscar.person_id";
		}
		else if(Str.bitCover(pattern, oscar2) ||
			    Str.bitCover(pattern, directoroscar2) || 
			    Str.bitCover(pattern, personoscar2) 
				)
		{
			FROM += "FROM Person join Oscar on Person.id = Oscar.person_id";
		}else if(Str.bitCover(pattern, movieoscar2)){
			FROM += "FROM Movie join Oscar on Movie.id = Oscar.movie_id";
		}else if(Str.bitCover(pattern, ActorMovie)){
			FROM += "FROM Person join Actor on Person.id = Actor.actor_id join Movie on Actor.movie_id = Movie.id";
		}else if(Str.bitCover(pattern, DirectorMovie)){
			FROM += "FROM Person join Director on Person.id = Director.director_id join Movie on Director.movie_id = Movie.id";
		}else if(Str.bitCover(pattern, DOBofPerson) ||
				 Str.bitCover(pattern, POBofPerson) 
				){
			FROM += "FROM Person";
		}else if(Str.bitCover(pattern, MYearofMovie) ||
				 Str.bitCover(pattern, RatingofMovie) ||
				 Str.bitCover(pattern, RuntimeOfMovie) ||
				 Str.bitCover(pattern, GenreOfMovie) ||
				 Str.bitCover(pattern, EarningsrankOfMovie) 
				){
			FROM += "FROM MOVIE";
		}
		//------------------end pattern matching ----------------------------
		if(SELECT.equals("SELECT distinct")) SELECT = "SELECT COUNT(*) ";
		if(SELECT.endsWith(",")) SELECT = SELECT.substring(0,SELECT.length()-1);
		if(WHERE.length()>0) WHERE = " WHERE "+ WHERE.substring(0, WHERE.length()-"and".length());
		return SELECT + " " + FROM + " " + WHERE;
	}
	
	
	//-------------------------------------------------------------------------------------------------------
	private void generateEntityAspects(String question){
		question = question.toLowerCase();
		if( (Str.contains(question,"when") && (Str.contains(question,"born"))) || 
			((Str.contains(question,"what") && (Str.contains(question,"birth")) || Str.contains(question,"birthday"))) ||
			(Str.contains(question,"older")) ||
			(Str.contains(question,"old")) ||
			(Str.contains(question,"younger")) ||
			(Str.contains(question,"young")) ||
			(Str.contains(question,"age")) 
		  ) {
			qvector[DOB] = 1;
			sems[DOB] = "?";
		}else if(Str.contains(question,"where") && Str.contains(question,"born")){
			qvector[POB] = 1;
			sems[POB] = "?";
		}
		
		if(
			Str.contains(question,"show") || Str.contains(question,"showed") || Str.contains(question,"shown")
			|| Str.contains(question,"release") || Str.contains(question,"released")
		  ) 
		{
		    if( Str.contains(question,"which year") || Str.contains(question,"what year") || Str.contains(question,"when")){
		    	qvector[MOVIEYEAR] = 1;
		    	sems[MOVIEYEAR] = "?";
		    }else{ 
	    		String tokens[] = question.split("[ ?]");
	    			for (String token : tokens) {
	    				if(Str.isNumber(token)){
	    					qvector[MOVIEYEAR] = 1;
	    					sems[MOVIEYEAR] = token;
	    					if(token.equals("2012")){
	    						qvector[MOVIENAME] = 0;
	    						sems[MOVIENAME] = null;
	    					}
	    					break;
	    			}
	    	    }
		    }
		}
		if(
			Str.contains(question,"runtime") || Str.contains(question,"run-time") 
		  )
		{
			qvector[RUNTIME] = 1;
			sems[RUNTIME] = "?";
		}
		if(Str.contains(question,"earnings") || Str.contains(question,"earningsrank") || Str.contains(question,"earnings-rank")
			|| Str.contains(question,"earning") || Str.contains(question,"earningrank") || Str.contains(question,"earning-rank")	
		  ){
//			if( StringAlgo.contains(question,"what's") || StringAlgo.contains(question,"what") ){
				qvector[EARNINGS_RANK] = 1;
				sems[EARNINGS_RANK] = "?";	
//			}
		}
		if(Str.contains(question,"rating") || Str.contains(question,"ratings")){
			if( Str.contains(question,"what's") || Str.contains(question,"what") ){
				qvector[RATING] = 1;
				sems[RATING] = "?";	
			}
		}
		if(Str.contains(question,"genre") || Str.contains(question,"genres")){
			if( Str.contains(question,"what's") || Str.contains(question,"what") ){
				qvector[GENRE] = 1;
				sems[GENRE] = "?";	
			}
		}
//      a French actor
//		else{
//			for (String movieWorkerPOB : movieWorkerPOBs) {
//				String pieces[] = movieWorkerPOB.split(",");
//				String countryName = 
//			}
//		}
	}
	private void generateRelationAspects(String question){
		question = question.toLowerCase();
		//--------------AWARDTYPE-----------------------------
		if(Str.contains(question,"best picture") || Str.contains(question,"best movie") ){
			qvector[AWARDTYPE] = 1;
			sems[AWARDTYPE] = "BEST-PICTURE";
		}
		else if(Str.contains(question,"best director")){
			qvector[AWARDTYPE] = 1;
			sems[AWARDTYPE] = "BEST-DIRECTOR";
		}
		else if(Str.contains(question,"best actor") || Str.contains(question,"best leading actor")
				|| (Str.contains(question,"actor") && Str.contains(question,"oscar"))){
			qvector[AWARDTYPE] = 1;
			sems[AWARDTYPE] = "BEST-ACTOR";
		}
		else if(Str.contains(question,"best supporting actor")){
			qvector[AWARDTYPE] = 1;
			sems[AWARDTYPE] = "BEST-SUPPORTING-ACTOR";
		}
		else if(Str.contains(question,"best actress") || Str.contains(question,"best leading actress")
				|| (Str.contains(question,"actress") && Str.contains(question,"oscar")) ){
			qvector[AWARDTYPE] = 1;
			sems[AWARDTYPE] = "BEST-ACTRESS";
		}
		else if(Str.contains(question,"best supporting actress")){
			qvector[AWARDTYPE] = 1;
			sems[AWARDTYPE] = "BEST-SUPPORTING-ACTRESS";
		}
		else if(Str.contains(question,"oscar")){
			qvector[AWARDTYPE] = 1;
			sems[AWARDTYPE] = "*";
		}
		if(qvector[AWARDTYPE] == 1) {
		//--------------AWARDYEAR-----------------------------
			if(Str.contains(question, "when") || Str.contains(question, "which year") || Str.contains(question, "what year")){
				qvector[AWARDYEAR] = 1;
				sems[AWARDYEAR] = "?";
			}else{
				String tokens[] = question.split("[ ?]");
				for (String token : tokens) {
					if(Str.isNumber(token)){
						qvector[AWARDYEAR] = 1;
						sems[AWARDYEAR] = token;
						if(token.equals("2012")){
							qvector[MOVIENAME] = 0;
							sems[MOVIENAME] = null;
						}
						break;
					}
				}
			}
		}
		
	}

	private void generateEvent(String question){
		question = question.toLowerCase();
		if(Str.contains(question,"direct") || Str.contains(question,"director") 
				|| Str.contains(question,"directed") || Str.contains(question,"directs")){
			qvector[DIRECT] = 1;
			sems[DIRECT] = "DIRECT";
		}
		if(
			Str.contains(question,"act") || Str.contains(question,"acts") || Str.contains(question,"acted") 
			|| Str.contains(question,"actor") || Str.contains(question,"actors")
			|| Str.contains(question,"actress") || Str.contains(question,"actresses")
			|| Str.contains(question,"play") || Str.contains(question,"plays") || Str.contains(question,"played")
			|| Str.contains(question,"star") || Str.contains(question,"stars") || Str.contains(question,"stared")
		        ) 
		{
			qvector[ACT] = 1;
			sems[ACT] = "ACT";
		}
		if(Str.contains(question,"win") || Str.contains(question,"wins") || Str.contains(question,"won")){
			qvector[WIN] = 1;
			sems[WIN] = "WIN";	
		}
		if(Str.contains(question,"younger") || Str.contains(question,"shorter") || Str.contains(question,"lower") || Str.contains(question,"less")){
			qvector[CMP] = 1;
			sems[CMP] = "<";
		}
		if(Str.contains(question,"older") || Str.contains(question,"longer") || Str.contains(question,"higher") || Str.contains(question,"more")){
			qvector[CMP] = 1;
			sems[CMP] = ">";
		}
		if(Str.contains(question,"equal") || Str.contains(question,"equals") || Str.contains(question,"same") ||
				Str.contains(question,"as old as") || Str.contains(question,"as young as") ||
				Str.contains(question,"as high as") || Str.contains(question,"as long as")
		  ){
			qvector[CMP] = 1;
			sems[CMP] = "=";
		}
	}
	
	private void generateEntities(String question){
		question = question.toLowerCase();
		if(Str.contains(question,"who") || Str.contains(question,"which actor") || Str.contains(question,"which actress") 
		   || Str.contains(question,"which director") ){
			qvector[MOVIEWORKERNAME] = 1;
			sems[MOVIEWORKERNAME] = "?";
		}else if(Str.contains(question,"which movie")){
			qvector[MOVIENAME] = 1;
			sems[MOVIENAME] = "?";
		}
		int index_of_movieWorker1 = -1;
		for (String movieWorker : actor_director) {
			if(Str.contains(question,movieWorker.toLowerCase())){
				qvector[MOVIEWORKERNAME] = 1;
				sems[MOVIEWORKERNAME] = movieWorker;
				index_of_movieWorker1 = question.indexOf(movieWorker.toLowerCase());
				break;
			}
		}
		if(index_of_movieWorker1 >=0 ){
			String subQuestion = question.substring(index_of_movieWorker1 + sems[MOVIEWORKERNAME].length());
			for (String movieWorker : actor_director) {
				if(Str.contains(subQuestion,movieWorker.toLowerCase())){
					qvector[MOVIEWORKERNAME2] = 1;
					sems[MOVIEWORKERNAME2] = movieWorker;
					break;
				}
			}
		}
		int index_of_movie1 = -1;
		for (String movie : movie_title) {
			if(Str.contains(question,movie.toLowerCase())){
				qvector[MOVIENAME] = 1;
				sems[MOVIENAME] = movie;
				index_of_movie1 = question.indexOf(movie.toLowerCase());
				break;
			}
		}
		if(index_of_movie1 >=0){
			String subQuestion = question.substring(index_of_movie1 + sems[MOVIENAME].length());
			for (String movie : movie_title) {
				if(Str.contains(subQuestion,movie.toLowerCase())){
					qvector[MOVIENAME2] = 1;
					sems[MOVIENAME2] = movie;
					break;
				}
			}
		}
	}
}
