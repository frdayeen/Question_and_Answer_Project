

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import weka.core.ContingencyTables;



public class GeographyKeywords extends KeywordfromParser
{
	ArrayList<String> city_keywords;
	ArrayList<String> country_keywords;
	ArrayList<String> mountain_keywords;
	ArrayList<String> sea_keywords;
	ArrayList<String> continent_keywords;
	
	
	TreeMap<String, String> country_keywords_map;
	TreeMap<String, String> mountain_keywords_map;
	TreeMap<String,String> sea_keywords_map;
	TreeMap<String, String> continent_keywords_map;
	
	final int LENGTH = 28;
	final int CITY_1 = 0;
	final int COUNTRY_1 = 1;
	final int MOUNTAIN_1 = 2;
	final int SEA_1 = 3;
	final int CONTINENT_1 = 4;
	final int MOUNTAIN_HIGH = 5;
	final int SEA_DEEP = 6;
	final int CONTINENT_AREA = 7;
	final int CONTINENT_POPULATION = 8;
	final int CONTINENT_HIGH = 9;
	final int CONTINENT_LOW = 10;
	final int CAPITAL = 11;
	
	final int MOUNTAIN_HIGHEST = 12;
	final int DEEPEST = 13;
	final int CONTINENT_LOWEST = 14;
	final int CONTINENT_HIGHEST = 15;
	final int AREA_LARGEST = 16;
	final int POPULATION_LARGEST = 17;
	final int CITY_2 = 18;
	final int COUNTRY_2 = 19;
	final int MOUNTAIN_2 = 20;
	final int SEA_2 = 21;
	final int CONTINENT_2 = 22;
	final int HIGHER = 23;
	final int LOWER = 24;
	final int DEEPER = 25;
	final int AREA_LARGER = 26;
	final int POPULATION_LARGER = 27;
	
	public GeographyKeywords()
	{
		city_keywords = new ArrayList<String>();
		country_keywords = new ArrayList<String>();
		mountain_keywords = new ArrayList<String>();
		sea_keywords = new ArrayList<String>();
		continent_keywords = new ArrayList<String>();
		
		country_keywords_map = new TreeMap<String, String>();
		mountain_keywords_map = new TreeMap<String, String>();
		sea_keywords_map = new TreeMap<String, String>();
		continent_keywords_map = new TreeMap<String, String>();
		this.qvector = new int[LENGTH];
		this.sems = new String[LENGTH];
		for(int i=0;i<LENGTH;i++)
			this.sems[i] = null;
		
		load_keywords("data/"+"city_keywords.txt", city_keywords, null);
		load_keywords("data/"+"country_keywords.txt", country_keywords, country_keywords_map);
		load_keywords("data/"+"mountain_keywords.txt", mountain_keywords, mountain_keywords_map);
		load_keywords("data/"+"sea_keywords.txt", sea_keywords, sea_keywords_map);
		load_keywords("data/"+"continent_keywords.txt", continent_keywords, continent_keywords_map);
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
	@Override
	public void generateQuestionVector(String question)
	{
		clear();
		String question_low = question.toLowerCase();
		//City name
		boolean tag = false;
		for(String name: city_keywords)
		{
			if(Str.contains(question_low,name))
			{
				tag = true;
				this.qvector[CITY_1] = 1;
				this.sems[CITY_1] = name;
				break;
			}
		}
		if(!tag
			&&(Str.contains(question_low, "which")||Str.contains(question_low, "what")||Str.contains(question_low, "what's"))
			&&(Str.contains(question_low, "city")||Str.contains(question_low, "cities"))
			)
		{
			this.qvector[CITY_1] = 1;
			this.sems[CITY_1] = "?";
		}
		
		//Country
		tag = false;
		for(String name: country_keywords)
		{
			if(Str.contains(question_low,name))
			{
				tag = true;
				this.qvector[COUNTRY_1] = 1;
				this.sems[COUNTRY_1] = country_keywords_map.get(name);
				break;
			}
		}
		if(!tag
			&&(Str.contains(question_low, "which")||Str.contains(question_low, "what")||Str.contains(question_low, "what's"))
			&&(Str.contains(question_low, "country")||Str.contains(question_low, "nation")||Str.contains(question_low, "countries")||Str.contains(question_low, "nations"))
			)
		{
			this.qvector[COUNTRY_1] = 1;
			this.sems[COUNTRY_1] = "?";
		}
		//mountain
		tag = false;
		for(String name: mountain_keywords)
		{
			if(Str.contains(question_low,name))
			{
				tag = true;
				this.qvector[MOUNTAIN_1] = 1;
				this.sems[MOUNTAIN_1] = mountain_keywords_map.get(name);
				break;
			}
		}
		if(!tag
			&&(Str.contains(question_low, "which")||Str.contains(question_low, "what")||Str.contains(question_low, "what's"))
			&&(Str.contains(question_low, "mountain")||Str.contains(question_low, "mountains"))
			)
		{
			this.qvector[MOUNTAIN_1] = 1;
			this.sems[MOUNTAIN_1] = "?";
		}
		//Sea
		tag = false;
		for(String name: sea_keywords)
		{
			if(Str.contains(question_low,name))
			{
				tag = true;
				this.qvector[SEA_1] = 1;
				this.sems[SEA_1] = sea_keywords_map.get(name);
				break;
			}
		}
		if(!tag
			&&(Str.contains(question_low, "which")||Str.contains(question_low, "what")||Str.contains(question_low, "what's"))
			&&(Str.contains(question_low, "ocean")||Str.contains(question_low, "oceans")||Str.contains(question_low, "sea")||Str.contains(question_low, "seas"))
			)
		{
			this.qvector[SEA_1] = 1;
			this.sems[SEA_1] = "?";
		}
		
		//continent
		tag = false;
		for(String name: continent_keywords)
		{
			if(Str.contains(question_low,name))
			{
				tag = true;
				this.qvector[CONTINENT_1] = 1;
				this.sems[CONTINENT_1] = continent_keywords_map.get(name);
				break;
			}
		}
		if(!tag
			&&(Str.contains(question_low, "which")||Str.contains(question_low, "what")||Str.contains(question_low, "what's"))
			&&(Str.contains(question_low, "continent")||Str.contains(question_low, "continents"))
			)
		{
			this.qvector[CONTINENT_1] = 1;
			this.sems[CONTINENT_1] = "?";
		}
		
		//MOUNTAIN_HIGH
		if(this.sems[MOUNTAIN_1]!=null)
		{
			if(Str.contains(question_low, "height") || Str.contains(question_low, "high"))
			{
				String[] tokens = question_low.split(" ");
				for (String token : tokens)
				{
					if(Str.isNumber(token))
					{
						this.qvector[MOUNTAIN_HIGH] = 1;
						this.sems[MOUNTAIN_HIGH] = token;
						break;
					}
				}
				if(this.sems[MOUNTAIN_HIGH]==null && (Str.contains(question_low, "what")||Str.contains(question_low, "what's")|| Str.contains(question_low, "how")))
				{
					this.qvector[MOUNTAIN_HIGH] = 1;
					this.sems[MOUNTAIN_HIGH] = "?";
				}
			}
		}
		//SEA_DEEP
		if(this.sems[SEA_1]!=null)
		{
			if(Str.contains(question_low, "deep") || Str.contains(question_low, "depth"))
			{
				String[] tokens = question_low.split(" ");
				for (String token : tokens)
				{
					if(Str.isNumber(token))
					{
						this.sems[SEA_DEEP] = token;
						break;
					}
				}
				if(this.sems[SEA_DEEP]==null && (Str.contains(question_low, "what") ||Str.contains(question_low, "what's") || Str.contains(question_low, "how")))
				{
					this.sems[SEA_DEEP] = "?";
				}
			}
		}
		
		//CONTINENT_AREA
		if(this.sems[CONTINENT_1]!=null)
		{
			if(Str.contains(question_low, "area") || Str.contains(question_low, "large") ||Str.contains(question_low, "big"))
			{
				String[] tokens = question_low.split(" ");
				for (String token : tokens)
				{
					if(Str.isNumber(token))
					{
						this.sems[CONTINENT_AREA] = token;
						break;
					}
				}
				if(this.sems[CONTINENT_AREA]==null && (Str.contains(question_low, "what") ||Str.contains(question_low, "what's") ||  Str.contains(question_low, "how")))
				{
					this.sems[CONTINENT_AREA] = "?";
				}
			}
		}
		
		//CONTINENT_POPULATION
		if(this.sems[CONTINENT_1]!=null)
		{
			if(Str.contains(question_low, "population") || Str.contains(question_low, "people"))
			{
				String[] tokens = question_low.split(" ");
				for (String token : tokens)
				{
					if(Str.isNumber(token))
					{
						this.sems[CONTINENT_POPULATION] = token;
						break;
					}
				}
				if(this.sems[CONTINENT_POPULATION]==null && (Str.contains(question_low, "what") ||Str.contains(question_low, "what's") || Str.contains(question_low, "how many")))
				{
					this.sems[CONTINENT_POPULATION] = "?";
				}
			}
		}
		
		//CONTINENT_HIGH
		if(this.sems[CONTINENT_1]!=null)
		{
			if(Str.contains(question_low, "hight") || Str.contains(question_low, "high"))
			{
				String[] tokens = question_low.split(" ");
				for (String token : tokens)
				{
					if(Str.isNumber(token))
					{
						this.sems[CONTINENT_HIGH] = token;
						break;
					}
				}
				if(this.sems[CONTINENT_HIGH]==null && (Str.contains(question_low, "what")||Str.contains(question_low, "what's")|| Str.contains(question_low, "how")))
				{
					this.sems[CONTINENT_HIGH] = "?";
				}
			}
		}
		
		//CONTINENT_LOW
		if(this.sems[CONTINENT_1]!=null)
		{
			if(Str.contains(question_low, "low") || Str.contains(question_low, "lowest"))
			{
				String[] tokens = question_low.split(" ");
				for (String token : tokens)
				{
					if(Str.isNumber(token))
					{
						this.sems[CONTINENT_LOW] = token;
						break;
					}
				}
				if(this.sems[CONTINENT_LOW]==null && (Str.contains(question_low, "what")||Str.contains(question_low, "what's")|| Str.contains(question_low, "how")))
				{
					this.sems[CONTINENT_LOW] = "?";
				}
			}
		}
//		final int CAPITAL = 11;
		if(Str.contains(question_low, "capital"))
		{
			this.qvector[CAPITAL] = 1;
			this.sems[CAPITAL] = "#";
			if((Str.contains(question_low, "what's")||Str.contains(question_low, "what")||Str.contains(question_low, "which")) 
					&& this.sems[CITY_1]==null)
			{
				this.qvector[CITY_1] = 1;
				this.sems[CITY_1] = "?";
			}
				
		}
//		final int MOUNTAIN_HIGHEST = 12;
		if(this.sems[MOUNTAIN_1]!=null && Str.contains(question_low, "highest"))
		{
			this.qvector[MOUNTAIN_HIGHEST] = 1;
			this.sems[MOUNTAIN_HIGHEST] = "#";
		}
//		final int DEEPEST = 13;
		if(this.sems[SEA_1]!=null && (Str.contains(question_low, "deepest")))
		{
			this.qvector[DEEPEST] = 1;
			this.sems[DEEPEST] = "#";
		}
//		final int LOWEST = 14;
		if((this.sems[COUNTRY_1]!=null || this.sems[CITY_1]!=null || this.sems[CONTINENT_1]!=null) 
				&& Str.contains(question_low, "lowest"))
		{
			this.qvector[CONTINENT_LOWEST] = 1;
			this.sems[CONTINENT_LOWEST] = "#";
		}
		
//		final int CONTINENT_HIGHEST = 15;
		if((this.sems[COUNTRY_1]!=null || this.sems[CITY_1]!=null || this.sems[CONTINENT_1]!=null) 
				&& Str.contains(question_low, "highest"))
		{
			this.qvector[CONTINENT_HIGHEST] = 1;
			this.sems[CONTINENT_HIGHEST] = "#";
		}
//		final int AREA_LARGEST = 16;
		if((this.sems[COUNTRY_1]!=null || this.sems[CITY_1]!=null || this.sems[CONTINENT_1]!=null) 
				&& (Str.contains(question_low, "largest area") || Str.contains(question_low, "largest")))
		{
			this.qvector[AREA_LARGEST] = 1;
			this.sems[AREA_LARGEST] = "#";
		}
//		final int POPULATION_LARGEST = 17;
		if((this.sems[COUNTRY_1]!=null || this.sems[CITY_1]!=null || this.sems[CONTINENT_1]!=null) 
				&& (Str.contains(question_low, "largest population") || Str.contains(question_low, "most population")))
		{
			this.qvector[POPULATION_LARGEST] = 1;
			this.sems[POPULATION_LARGEST] = "#";
		}
		
		//HIGHER or LOWER
		if((Str.contains(question_low, "higher")||Str.contains(question_low, "taller")||Str.contains(question_low, "lower")) && Str.contains(question_low, "than"))
		{
			if(Str.contains(question_low, "higher")||Str.contains(question_low, "taller"))
			{
				this.qvector[HIGHER] = 1;
				this.sems[HIGHER] = "#";
			}
			else
			{
				this.qvector[LOWER] = 1;
				this.sems[LOWER] = "#";
			}
			if((Str.contains(question_low, "which")||Str.contains(question_low, "what")||Str.contains(question_low, "what's"))
					&&(Str.contains(question_low, "mountain")||Str.contains(question_low, "mountains")))
			{
				this.qvector[MOUNTAIN_1] = 1;
				this.sems[MOUNTAIN_1] = "?";
				
				for(String name: mountain_keywords)
				{
					if(Str.contains(question_low,name))
					{
						tag = true;
						this.qvector[MOUNTAIN_2] = 1;
						this.sems[MOUNTAIN_2] = mountain_keywords_map.get(name);
						break;
					}
				}
			}
			else if(this.sems[MOUNTAIN_1]!=null && this.sems[MOUNTAIN_1].indexOf('?')<0)
			{
				String first = null;
				String second = null;
				for(String name: mountain_keywords)
				{
					if(Str.contains(question_low,name))
					{
						if(first==null)
							first = name;
						else if(!mountain_keywords_map.get(first).equals(mountain_keywords_map.get(name)))
						{
							second = name;
							break;
						}	
					}
				}
				if(question_low.indexOf(first)>question_low.indexOf(second))
				{
					String temp = first;
					first = second;
					second = temp;
				}
				this.qvector[MOUNTAIN_1] = 1;
				this.sems[MOUNTAIN_1] = mountain_keywords_map.get(first);
				this.qvector[MOUNTAIN_2] = 1;
				this.sems[MOUNTAIN_2] = mountain_keywords_map.get(second);
			}
			
			//CONTINENT
			if((Str.contains(question_low, "which")||Str.contains(question_low, "what")||Str.contains(question_low, "what's"))
					&&(Str.contains(question_low, "continent")||Str.contains(question_low, "continents")))
			{
				this.qvector[CONTINENT_1] = 1;
				this.sems[CONTINENT_1] = "?";
				
				for(String name: continent_keywords)
				{
					if(Str.contains(question_low,name))
					{
						tag = true;
						this.qvector[CONTINENT_2] = 1;
						this.sems[CONTINENT_2] = continent_keywords_map.get(name);
						break;
					}
				}
			}
			else if(this.sems[CONTINENT_1]!=null && this.sems[CONTINENT_1].indexOf('?')<0)
			{
				String first = null;
				String second = null;
				for(String name: continent_keywords)
				{
					if(Str.contains(question_low,name))
					{
						if(first==null)
							first = name;
						else if(!continent_keywords_map.get(first).equals(continent_keywords_map.get(name)))
						{
							second = name;
							break;
						}	
					}
				}
				if(question_low.indexOf(first)>question_low.indexOf(second))
				{
					String temp = first;
					first = second;
					second = temp;
				}
				this.qvector[CONTINENT_1] = 1;
				this.sems[CONTINENT_1] = continent_keywords_map.get(first);
				this.qvector[CONTINENT_2] = 1;
				this.sems[CONTINENT_2] = continent_keywords_map.get(second);
			}
		}
		
		//DEEPER
		if((Str.contains(question_low, "deeper")) && Str.contains(question_low, "than"))
		{
			this.qvector[DEEPER] = 1;
			this.sems[DEEPER] = "#";
			if((Str.contains(question_low, "which")||Str.contains(question_low, "what")||Str.contains(question_low, "what's"))
					&&(Str.contains(question_low, "sea")||Str.contains(question_low, "ocean")))
			{
				this.qvector[SEA_1] = 1;
				this.sems[SEA_1] = "?";
				
				for(String name: sea_keywords)
				{
					if(Str.contains(question_low,name))
					{
						tag = true;
						this.qvector[SEA_2] = 1;
						this.sems[SEA_2] = sea_keywords_map.get(name);
						break;
					}
				}
			}
			else if(this.sems[SEA_1]!=null && this.sems[SEA_1].indexOf('?')<0)
			{
				String first = null;
				String second = null;
				for(String name: sea_keywords)
				{
					if(Str.contains(question_low,name))
					{
						if(first==null)
							first = name;
						else if(!sea_keywords_map.get(first).equals(sea_keywords_map.get(name)))
						{
							second = name;
							break;
						}	
					}
				}
				if(question_low.indexOf(first)>question_low.indexOf(second))
				{
					String temp = first;
					first = second;
					second = temp;
				}
				this.qvector[SEA_1] = 1;
				this.sems[SEA_1] = sea_keywords_map.get(first);
				this.qvector[SEA_2] = 1;
				this.sems[SEA_2] = sea_keywords_map.get(second);
			}
		}
		
		//LARGER
		if((Str.contains(question_low, "larger")||(Str.contains(question_low, "more")&&(Str.contains(question_low, "people")||Str.contains(question_low, "population")))) && Str.contains(question_low, "than"))
		{
			if(Str.contains(question_low, "population")||Str.contains(question_low, "people"))
			{
				this.qvector[POPULATION_LARGER] = 1;
				this.sems[POPULATION_LARGER] = "#";
			}
			else
			{
				this.qvector[AREA_LARGER] = 1;
				this.sems[AREA_LARGER] = "#";
			}
			if((Str.contains(question_low, "which")||Str.contains(question_low, "what")||Str.contains(question_low, "what's"))
					&&(Str.contains(question_low, "continent")||Str.contains(question_low, "continents")))
			{
				this.qvector[CONTINENT_1] = 1;
				this.sems[CONTINENT_1] = "?";

				for(String name: continent_keywords)
				{
					if(Str.contains(question_low,name))
					{
						tag = true;
						this.qvector[CONTINENT_2] = 1;
						this.sems[CONTINENT_2] = continent_keywords_map.get(name);
						break;
					}
				}
			}
			else if(this.sems[CONTINENT_1]!=null && this.sems[CONTINENT_1].indexOf('?')<0)
			{
				for(int i= continent_keywords.size()-1;i>=0;i--)
				{
					String name = continent_keywords.get(i);
					if(Str.contains(question_low,name))
					{
						tag = true;
						this.qvector[CONTINENT_2] = 1;
						this.sems[CONTINENT_2] = continent_keywords_map.get(name);
						break;
					}
				}
			}
		}
		
	}
	private String generateSQL_other()
	{
		this.qvector[MOUNTAIN_1] = 0;
		this.qvector[SEA_1]=0;
		this.qvector[MOUNTAIN_HIGH] = 0;
		this.qvector[SEA_DEEP] = 0;
		this.sems[MOUNTAIN_1] = null;
		this.sems[SEA_1]=null;
		this.sems[MOUNTAIN_HIGH] = null;
		this.sems[SEA_DEEP] = null;
		
		final int MOUNTAIN_HIGHEST = 12;
		final int DEEPEST = 13;
		ArrayList<Integer> question_mark = new ArrayList<Integer>();
		ArrayList<Integer> conditions = new ArrayList<Integer>();
		String sql = "select ";
		String table = "continents outer left join (select country_id, country_name, city_id, city_name, continentid as continent_id from (select id as country_id, name as country_name, city_id, city_name from countries outer left join (select id as city_id, name as city_name, countryid as country_id from cities outer left join capitals on cities.id = capitals.cityid)  as t on countries.id = t.country_id ) as t2 outer left join countrycontinents on t2.country_id = countrycontinents.countryid) as t3 on continents.id = t3.continent_id ";
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
		
		if(this.sems[HIGHER]!=null)
		{
			if(question_mark.size()==0)
			{
				sql += "count(*) from "+table+" where continent like '" + this.sems[CONTINENT_1]+"' and highest > (select highest from "+table+" where continent like '"+this.sems[CONTINENT_2]+"' )" ;
				
			}
			else
			{
				sql += "distinct continent from "+table+" where highest > (select highest from "+table+" where continent like '"+this.sems[CONTINENT_2]+"' )";
			}
			return sql;
		}
		if(this.sems[LOWER]!=null)
		{
			if(question_mark.size()==0)
			{
				sql += "count(*) from "+table+" where continent like '" + this.sems[CONTINENT_1]+"' and highest < (select highest from "+table+" where continent like '"+this.sems[CONTINENT_2]+"' )" ;
				
			}
			else
			{
				sql += "distinct continent from "+table+" where highest < (select highest from "+table+" where continent like '"+this.sems[CONTINENT_2]+"' )";
			}
			return sql;
		}
		if(this.sems[POPULATION_LARGER]!=null)
		{
			if(question_mark.size()==0)
			{
				sql += "count(*) from "+table+" where continent like '" + this.sems[CONTINENT_1]+"' and population < (select population from "+table+" where continent like '"+this.sems[CONTINENT_2]+"' )" ;
				
			}
			else
			{
				sql += "distinct continent from "+table+" where population < (select population from "+table+" where continent like '"+this.sems[CONTINENT_2]+"' )";
			}
			return sql;
		}
		if(this.sems[AREA_LARGER]!=null)
		{
			if(question_mark.size()==0)
			{
				sql += "count(*) from "+table+" where continent like '" + this.sems[CONTINENT_1]+"' and area_km2 < (select area_km2 from "+table+" where continent like '"+this.sems[CONTINENT_2]+"' )" ;
				
			}
			else
			{
				sql += "distinct continent from "+table+" where area_km2 < (select area_km2 from "+table+" where continent like '"+this.sems[CONTINENT_2]+"' )";
			}
			return sql;
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
				case CITY_1:
					sql += "city_name ";
					break;
				case COUNTRY_1:
					sql += "country_name ";
					break;
				case CONTINENT_1:
					sql += "continent ";
					break;
				case CONTINENT_AREA:
					sql += "area_km2 ";
					break;
				case CONTINENT_POPULATION:
					sql += "population ";
					break;
				case CONTINENT_HIGH:
					sql +="highest ";
					break;
				case CONTINENT_LOW:
					sql +="lowest ";
					break;
				default:
					break;
				}
				if(i!=question_mark.size()-1)
					sql +=", ";
			}
		}
		
		sql += "from "+ table;
		if(conditions.contains(CAPITAL))
		{
			conditions.remove(conditions.indexOf(CAPITAL));
		}
		if(conditions.size()!=0)
			sql += "where ";
		for(int i=0;i<conditions.size();i++)
		{
			switch (conditions.get(i))
			{
			case CITY_1:
				sql += "city_name like '"+ this.sems[CITY_1]+"' ";
				break;
			case COUNTRY_1:
				sql += "country_name like '"+ this.sems[COUNTRY_1]+"' ";
				break;
			case CONTINENT_1:
				sql += "continent like '"+ this.sems[CONTINENT_1]+"' ";
				break;
			case CONTINENT_LOWEST:
				sql += "lowest = (select max(lowest) from ( "+table+" )) ";
				break;
			case CONTINENT_HIGHEST:
				sql += "highest = (select max(highest) from ( "+table+" )) ";
				break;
			case POPULATION_LARGEST:
				sql += "population = (select max(population) from ( "+table+" )) ";
				break;
			case AREA_LARGEST:
				sql += "area_km2 = (select max(area_km2) from ( "+table+" )) ";
				break;
			case CAPITAL:
				break;
			default:
				break;
			}
			if(i!=conditions.size()-1)
				sql +="and ";
		}
		return sql;
	}
	private String generateSQL_Sea()
	{
		
		ArrayList<Integer> question_mark = new ArrayList<Integer>();
		ArrayList<Integer> conditions = new ArrayList<Integer>();
		String sql = "select ";
		String table = "Seas ";
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
		
		if(this.sems[DEEPER]!=null)
		{
			if(question_mark.size()==0)
			{
				sql += "count(*) from seas where ocean like '" + this.sems[SEA_1]+"' and deepest > (select deepest from seas where ocean like '"+this.sems[SEA_2]+"' )" ;
			}
			else
			{
				sql += "distinct ocean from seas where deepest > (select deepest from seas where ocean like '"+this.sems[SEA_2]+"' )";
			}
			return sql;
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
				case SEA_1:
					sql += "seas.ocean ";
					break;
				case SEA_DEEP:
					sql += "seas.deepest ";
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
			case SEA_1:
				sql += "seas.ocean like '"+ this.sems[SEA_1]+"' ";
				break;
			case SEA_DEEP:
				sql += "seas.deepest like '"+ this.sems[SEA_DEEP]+"' ";
				break;
			case DEEPEST:
				sql += "seas.deepest = (select max(seas.deepest) from seas) ";
				break;
			default:
				break;
			}
			if(i!=conditions.size()-1)
				sql +="and ";
		}
		return sql;
	}
	private String generateSQL_Mountain()
	{
		ArrayList<Integer> question_mark = new ArrayList<Integer>();
		ArrayList<Integer> conditions = new ArrayList<Integer>();
		String sql = "select ";
		String table = "Mountains ";
		for(int i=0;i<LENGTH;i++)
		{
			if(this.sems[i]!=null)
			{
				if(this.sems[i].indexOf('?')>=0)
					question_mark.add(i);
				else //if(this.sems[i].indexOf('#')<0)
					conditions.add(i);
			}	
		}
		
		if(this.sems[HIGHER]!=null || this.sems[LOWER]!=null)
		{
			String op;
			if(this.sems[HIGHER]!=null)
				op = ">";
			else
				op = "<";
			if(question_mark.size()==0)
			{
				sql += "count(*) from mountains where name like '" + this.sems[MOUNTAIN_1]+"' and height "+op+" (select height from mountains where name like '"+this.sems[MOUNTAIN_2]+"' )" ;
			}
			else
			{
				sql += "distinct name from mountains where height "+op+" (select height from mountains where name like '"+this.sems[MOUNTAIN_2]+"' )";
			}
			return sql;
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
				case MOUNTAIN_1:
					sql += "mountains.name ";
					break;
				case MOUNTAIN_HIGH:
					sql += "mountains.height ";
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
			case MOUNTAIN_1:
				sql += "mountains.name like '"+ this.sems[MOUNTAIN_1]+"' ";
				break;
			case MOUNTAIN_HIGH:
				sql += "cr.name like '"+ this.sems[MOUNTAIN_HIGH]+"' ";
				break;
			case MOUNTAIN_HIGHEST:
				sql += "mountains.height = (select max(mountains.height) from mountains) ";
				break;
			default:
				break;
			}
			if(i!=conditions.size()-1)
				sql +="and ";
		}
		return sql;
	}
	@Override
	public String generateSQL()
	{
		if(this.sems[CONTINENT_1]!=null)
		{
			return generateSQL_other();
		}
		if(this.sems[MOUNTAIN_1]!=null)
			return generateSQL_Mountain();
		if(this.sems[SEA_1] != null)
			return generateSQL_Sea();
		else return generateSQL_other();
//		return "TEST";
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
