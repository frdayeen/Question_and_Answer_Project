

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;



public class MusicKeywords extends KeywordfromParser
{
//	String[] gender_keywords = {"male", "female", "woman", "man", "women","men"};
//	String[] competition_name_keywords = {"biathlon", "skijumping", "ski jumping", "ski-jumping", 
//			"speedskating", "speed skating", "speed-skating", "shorttrack", "short track", "short-track", 
//			"figureskating", "figure skating", "figure-skating", "giantslalom", "giant slalom", "giant-slalom",
//			"crosscountry", "cross country", "cross-country", "slalom", "superg", "super g", "super-g"};
//	String[] competition_type_keywords = {"competition type", "type", "individual", "nh", 
//			"1000", "one thousand", "one-thousand", "500", "five hundreds", "five-hundred", 
//			"1500", "one thousand and five hundreds", "fifteen hundreds", "fifteen-hundred", "lh", "sprint"};
//	String[] athletes_name_keywords = {"aamodt","ammann","andrea","angerer","arakawa","asada",
//			"bauer","baverel-robert","bjorndalen","buttle","bystol","cheek","chenal","cleski",
//			"cohen","davis","dorfmeister","dorin","dorofeyev","efremova","fak","greis","hanevold","hautamaki",
//			"hedda","hedrick","herbst","ho-suk","hoffmann","hosp","huttary","hyun-soo","jay","jiajun",
//			"kang-seok","kim","kofler","kostelic","kramer","kuzmina","lambiel","lee","ljokelsoy","lysacek",
//			"maier","malysz","mancuso","mcivor","meissnitzer","miller","mo","morgenstern","myhrer","neuner","ohno",
//			"olofsson","ottosson","paerson","parson","plushenko","poutiainen","raich","razzoli","riesch","rochette",
//			"schild","schlierenzauer","schonfelder","skobrev","slutskaya","svendsen","takahashi","veerpalu","wennemars",
//			"zurbriggen"};
//	String[] nationality_keywords = {"austria","canada","canadian","china","chinese",
//			"croatia","czech republic","czech","estonia","estonian","finland","france","french",
//			"germany","german","italy","italian","japan","japanese","south korea","south korean",
//			"korea","korean","netherlands","dutch","norway","poland","polish","russia","russian",
//			"slovakia","sweden","swedish","switzerland","swiss","ukraine","usa","american"};
//	String[] medal_keywords = {"gold medal", "gold", "silver medal", "silver", "bronze medal", "bronze", "first place", "second place", "third place"};
	String[] win = {"get","gets","got", "win","wins", "won"};
	ArrayList<String> gender_keywords = new ArrayList<String>();
	ArrayList<String> competition_name_keywords = new ArrayList<String>();
	ArrayList<String> competition_type_keywords = new ArrayList<String>();
	ArrayList<String> athletes_name_keywords = new ArrayList<String>();
	ArrayList<String> nationality_keywords = new ArrayList<String>();
	ArrayList<String> medal_keywords = new ArrayList<String>();
	ArrayList<String> win_keywords = new ArrayList<String>(Arrays.asList(win));
	
	String table = "athletes outer left join (SELECT * FROM competitions natural join results) as cr on athletes.name = cr.winner ";
	TreeMap<String,String> gender_keywords_map;
	TreeMap<String,String> competition_name_keywords_map;
	TreeMap<String,String> competition_type_keywords_map;
	TreeMap<String,String> nationality_keywords_map;
	TreeMap<String,String> medal_keywords_map;
	final int LENGTH = 8;
	final int ALTHLETE_NAME = 0;
	final int COMPETITION_NAME = 1;
	final int NATIONALITY = 2;
	final int GENDER = 3;
	final int COMPETITION_TYPE = 4;
	final int MEDAL = 5;
	final int MEDAL_YEAR = 6;
	final int WIN = 7;
	public MusicKeywords()
	{
		this.qvector = new int[LENGTH];
		this.sems = new String[LENGTH];
		for(int i=0;i<LENGTH;i++)
			this.sems[i] = null;
		medal_keywords_map = new TreeMap<String, String>();
		gender_keywords_map = new TreeMap<String, String>();
		competition_name_keywords_map = new TreeMap<String, String>();
		competition_type_keywords_map = new TreeMap<String, String>();
		nationality_keywords_map = new TreeMap<String, String>();
		load_keywords("data/"+"gender_keywords.txt", gender_keywords, gender_keywords_map);
		load_keywords("data/"+"competition_name_keywords.txt", competition_name_keywords, competition_name_keywords_map);
		load_keywords("data/"+"competition_type_keywords.txt", competition_type_keywords, competition_type_keywords_map);
		load_keywords("data/"+"athletes_name_keywords.txt", athletes_name_keywords, null);
		load_keywords("data/"+"nationality_keywords.txt", nationality_keywords, nationality_keywords_map);
		load_keywords("data/"+"medal_keywords.txt", medal_keywords, medal_keywords_map);
		
	}
	
	private void load_keywords(String file, ArrayList<String> list, TreeMap<String, String> map)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line = br.readLine();
			while(line!=null)
			{
				if(map!=null)
				{
					String[] words = line.split("\t");
					map.put(words[0], words[1]);
					list.add(words[0]);
				}
				else
				{
					list.add(line);
				}
				line = br.readLine();
			}
			br.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public void generateQuestionVector(String question)
	{
		clear();
		String question_low = question.toLowerCase();
		
		//Athletes name
		boolean tag = false;
		for(String name: athletes_name_keywords)
		{
			if(Str.contains(question_low,name))
			{
				tag = true;
				this.qvector[ALTHLETE_NAME] = 1;
				this.sems[ALTHLETE_NAME] = name;
				break;
			}
		}
		if(!tag&&question_low.startsWith("who"))
		{
			this.qvector[ALTHLETE_NAME] = 1;
			this.sems[ALTHLETE_NAME] = "?";
		}
		
		//competition name
		tag = false;
		for(String competition_name:competition_name_keywords)
		{
			if(Str.contains(question_low,competition_name))
			{
				tag = true;
				this.qvector[COMPETITION_NAME] = 1;
				this.sems[COMPETITION_NAME] = competition_name_keywords_map.get(competition_name);
				break;
			}
		}
		if(!tag && (Str.contains(question_low, "what events") || Str.contains(question_low, "what event") || Str.contains(question_low, "which event") || Str.contains(question_low, "what competition")||Str.contains(question_low, "what competitions")||Str.contains(question_low, "which competition")||Str.contains(question_low, "what sports")||Str.contains(question_low, "what sport")||Str.contains(question_low, "what game")||Str.contains(question_low, "which game")))
		{
			this.qvector[COMPETITION_NAME] = 1;
			this.sems[COMPETITION_NAME] = "?";
		}
		
		//NATIONALITY
		tag = false;
		for(String nationality_name:nationality_keywords)
		{
			if(Str.contains(question_low,nationality_name))
			{
				tag = true;
				this.qvector[NATIONALITY] = 1;
				this.sems[NATIONALITY] = nationality_keywords_map.get(nationality_name);
				break;
			}
		}
		if(!tag && (question_low.startsWith("what country") || question_low.startsWith("which country")||question_low.startsWith("what nation") || question_low.startsWith("which nation") || Str.contains(question_low, "nationality")))
		{
			this.qvector[NATIONALITY] = 1;
			this.sems[NATIONALITY] = "?";
		}
		
		//gender
		tag = false;
		for(String gender:gender_keywords)
		{
			if(Str.contains(question_low,gender))
			{
				tag = true;
				this.qvector[GENDER] = 1;
				this.sems[GENDER] = gender_keywords_map.get(gender);
				break;
			}
		}
		if(!tag && (question_low.startsWith("what gender") || Str.contains(question_low, "gender")))
		{
			this.qvector[GENDER] = 1;
			this.sems[GENDER] = "?";
		}
		
		//competition_type
		tag = false;
		for(String type:competition_type_keywords)
		{
			if(Str.contains(question_low,type))
			{
				tag = true;
				this.qvector[COMPETITION_TYPE] = 1;
				this.sems[COMPETITION_TYPE] = competition_type_keywords_map.get(type);
				break;
			}
		}
		if(!tag && (question_low.startsWith("what type") || Str.contains(question_low, "type")))
		{
			this.qvector[COMPETITION_TYPE] = 1;
			this.sems[COMPETITION_TYPE] = "?";
		}
		
		//medal
		tag = false;
		for(String medal:medal_keywords)
		{
			if(Str.contains(question_low,medal))
			{
				tag = true;
				this.qvector[MEDAL] = 1;
				this.sems[MEDAL] = medal_keywords_map.get(medal);
				break;
			}
		}
		if(!tag && (question_low.startsWith("what medal") || question_low.startsWith("which medal") || Str.contains(question_low, "medal")))
		{
			this.qvector[MEDAL] = 1;
			this.sems[MEDAL] = "?";
		}
		
		//YEAR
		if(Str.contains(question_low, "in 2010") || (Str.contains(question_low, "in")&&Str.contains(question_low, "2010")))
		{
			this.qvector[MEDAL_YEAR] = 1;
			this.sems[MEDAL_YEAR] = "2010";
		}
		else if(Str.contains(question_low, "in 2006")|| (Str.contains(question_low, "in")&&Str.contains(question_low, "2006")))
		{
			this.qvector[MEDAL_YEAR] = 1;
			this.sems[MEDAL_YEAR] = "2006";
		}
		else if(Str.contains(question_low,"year"))
		{
			this.qvector[MEDAL_YEAR] = 1;
			this.sems[MEDAL_YEAR] = "?";
		}
	}
	
	public String generateSQL()
	{
		ArrayList<Integer> question_mark = new ArrayList<Integer>();
		ArrayList<Integer> conditions = new ArrayList<Integer>();
		String sql = "select ";
		for(int i=0;i<LENGTH;i++)
		{
			if(this.sems[i]!=null)
			{
				if(this.sems[i].indexOf('?')>=0)
					question_mark.add(i);
				else
					conditions.add(i);
			}	
		}
		if(question_mark.size()==0)
		{
			sql += "count(*) ";
		}
		else
		{
			sql += "distinct ";
			for(int i=0;i<question_mark.size();i++)
			{
				switch (question_mark.get(i))
				{
				case ALTHLETE_NAME:
					sql += "athletes.name ";
					break;
				case COMPETITION_NAME:
					sql += "cr.name ";
					break;
				case NATIONALITY:
					sql += "nationality ";
					break;
				case GENDER:
					sql += "gender ";
					break;
				case COMPETITION_TYPE:
					sql += "type ";
					break;
				case MEDAL:
					sql += "medal ";
					break;
				case MEDAL_YEAR:
					sql += "year ";
					break;
				default:
					break;
				}
				if(i!=question_mark.size()-1)
					sql +=", ";
			}
			
		}
		sql += "from "+ table;
		if(conditions.size()!=0)
			sql += "where ";
		for(int i=0;i<conditions.size();i++)
		{
			switch (conditions.get(i))
			{
			case ALTHLETE_NAME:
				sql += "athletes.name like '"+ this.sems[ALTHLETE_NAME]+"' ";
				break;
			case COMPETITION_NAME:
				sql += "cr.name like '"+ this.sems[COMPETITION_NAME]+"' ";
				break;
			case NATIONALITY:
				sql += "nationality like '"+ this.sems[NATIONALITY]+"' ";
				break;
			case GENDER:
				sql += "gender like '"+ this.sems[GENDER]+"' ";
				break;
			case COMPETITION_TYPE:
				sql += "type like '"+ this.sems[COMPETITION_TYPE]+"' ";
				break;
			case MEDAL:
				sql += "medal like '" + this.sems[MEDAL]+"' ";
				break;
			case MEDAL_YEAR:
				sql += "year like '"+ this.sems[MEDAL_YEAR]+"' ";
				break;
			default:
				break;
			}
			if(i!=conditions.size()-1)
				sql +="and ";
		}
		return sql;
	}
	public int[] get_qvector()
	{
		return this.qvector;
	}
	public String[] get_stringvalue()
	{
		return this.sems;
	}
	private void clear()
	{
		for(int i=0;i<LENGTH;i++)
		{
			this.qvector[i] = 0;
			this.sems[i] = null;
		}
	}
}
