

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import edu.stanford.nlp.ling.TaggedWord;

public class CategoryClassifier {
	Connection connection = null;  
    Statement statement = null;  
    
    NaiveBayes nb = new NaiveBayes();
    double[][] features;
    double[] idf_features;
    int[] test_list;
    Instances testDataSet;
    double train_percent = 1.0;
    private TreeMap<String,Integer> idf_count = new TreeMap<String, Integer>();
	public TreeMap<String,Double> idf = new TreeMap<String, Double>();
	private TreeSet<String> voc = new TreeSet<String>();
	private HashMap<String, Integer> term_index = new HashMap<String, Integer>();
   
	private TreeSet<String> keywords_M = new TreeSet<String>();

	private TreeSet<String> keywords_G = new TreeSet<String>();
	private TreeSet<String> movie_personName = new TreeSet<String>();
	private TreeSet<String> movie_movieName = new TreeSet<String>();

	private TreeSet<String> moviePN = new TreeSet<String>(); 
 
	private TreeSet<String> geographyPN = new TreeSet<String>(); 
	
	String classLabel[] = {"M","G"};
	public CategoryClassifier(double train){
		train_percent = train;
		loadKeyWord();
		loadKeyWordSetsForMovie();

		loadKeyWordSetsForGeography();
		build_feature();
		build_classifier();
	}
	public CategoryClassifier(){
		loadKeyWord();
		loadKeyWordSetsForMovie();

		loadKeyWordSetsForGeography();
		build_feature();
		build_classifier();
	}
	public int[] get_permutation(int[] list)
	{
		Random r =new Random();
		int[] result = new int[list.length];
		for(int i = 0; i < list.length;i++)
		{
			int t = r.nextInt(list.length-i);
			int temp = list[t];
			list[t] = list[list.length-1-i];
			list[list.length-i-1] = temp;
			result[list.length-i-1] = list[list.length-i-1];
		}
		return result;
	}
	public void build_classifier()
	{
		ArrayList<String> labels = new ArrayList<String>();
		for (String label : classLabel) {
			labels.add(label);
		}
		Attribute cls = new Attribute("class", labels);
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		Iterator<String> it = voc.iterator();
		while(it.hasNext())
		{
			Attribute att = new Attribute(it.next());
			attributes.add(att);
		}
		attributes.add(cls);
		Instances dataset = new Instances("Training-dataset", attributes, 0);
		
				int[] temp_array = new int[features.length];
		for(int i=0;i<temp_array.length;i++)
			temp_array[i] = i;
		test_list = new int[(int)Math.ceil(temp_array.length*(1-train_percent))];
		int[] perm = get_permutation(temp_array);
		for(int i=0;i<test_list.length;i++)
		{
			test_list[i] = perm[temp_array.length-test_list.length+i];
		}
		dataset.setClassIndex(dataset.numAttributes() - 1);
		testDataSet = new Instances(dataset);
		for(int i = 0; i<temp_array.length-test_list.length;i++)
		{
			Instance inst = new DenseInstance(1.0, features[perm[i]]);
			dataset.add(inst);
		}

		try
		{
			nb.buildClassifier(dataset);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void build_feature()
	{
		LinkedList<KeyQuestion> question_list = loadTrainingData();
		
		voc = get_voc(question_list);
		features = new double[question_list.size()][voc.size()+1];
		idf_features = new double[voc.size()];
		get_idf_count(voc, question_list);
		get_idf(voc, question_list.size());
		output_words_index("term_index.txt", voc);
		Iterator<String> it = voc.iterator();
		int index = 0;
		while(it.hasNext())
		{
			idf_features[index] = idf.get(it.next());
			index++;
		}
		for(int i=0;i<features.length;i++)
			for(int j=0;j<features[0].length;j++)
				features[i][j] = 0;
		int i=0;
		for(KeyQuestion q : question_list)
		{
			String q_text = q.getText().toLowerCase();
			String[] words = q_text.split("[ ?,.;:!()]");
			String label = ""+q.getCategory();
			for(String w : words)
			{
				w = w.trim();
				if(w.length()>0)
				{
					int ind = term_index.get(w);
					int frequency = 0;
			
					for(int j=0;j<words.length;j++)
					{
						if(words[j].equals(w))
							frequency++;
					}
					features[i][ind] = frequency * idf_features[ind];
				}
			}
			int ind = 0;
			for(int i1=0;i1<classLabel.length;i1++)
				if(label.equalsIgnoreCase(classLabel[i1]))
					ind = i1;
			features[i][features[0].length-1] = ind;
			i++;
		}
	}
	private void output_words_index(String file, TreeSet<String> voc)
	{
		Iterator<String> it = voc.iterator();
		int index = 0;
			while(it.hasNext())
			{
				String term = it.next();
				term_index.put(term, index);

				index++;
			}
	}
	private void get_idf(TreeSet<String> voc, double size)
	{
		Iterator<String> it = voc.iterator();
		while(it.hasNext())
		{
			String term = it.next();
			int idf_c = idf_count.get(term);
			if(idf_c==0)
				idf_c = 1;
			idf.put(term, Math.log(size/idf_c));
		}
	}
	private void get_idf_count(TreeSet<String> voc, LinkedList<KeyQuestion> question_list)
	{
		Iterator<String> it = voc.iterator();
		while(it.hasNext())
			idf_count.put(it.next(), 0);
		Iterator<KeyQuestion> it_q = question_list.iterator();
		while(it_q.hasNext())
		{
			String q = it_q.next().getText().toLowerCase();
			String[] words = q.split("[ ?,.;:!()]");
			for(String w : words)
			{
				w = w.trim();
				if(w.length()>0)
				{
					int temp = idf_count.get(w);
					idf_count.put(w, temp+1);
				}
			}
		}
	}
	private TreeSet<String> get_voc(LinkedList<KeyQuestion> question_list)
	{
		TreeSet<String> result = new TreeSet<String>();
		Iterator<String> it = moviePN.iterator();
		while(it.hasNext())
		{
			String pn = it.next().toLowerCase();
			String[] words = pn.split(" ");
			for(String word:words)
				result.add(word);
		}
		it = geographyPN.iterator();
		while(it.hasNext())
		{
			String pn = it.next().toLowerCase();
			String[] words = pn.split(" ");
			for(String word:words)
				result.add(word);
		}
		Iterator<KeyQuestion> it_q = question_list.iterator();
		while(it_q.hasNext())
		{
			String q = it_q.next().getText().toLowerCase();
			String[] words = q.split("[ ?,.;:!()]");
			for(String w : words)
			{
				w = w.trim();
				if(w.length()>0)
					result.add(w);
			}
		}
		return result;
	}
	public String classify(String text){
		text = text.toLowerCase();
		ArrayList<TaggedWord> tagWords = ParseTreeGen.getTaggedText(text);
		String tokens[] = text.split("[ ?,.;:!()]");
		for (int i = 0; i < tokens.length; i++) {
			if(keywords_M.contains(tokens[i])) {
	
				return "M";
			}
			if(keywords_G.contains(tokens[i])) {
			
				return "G";
			}
		}
		
		for (String str : movie_movieName) {
			int index = text.toLowerCase().indexOf(str);
			boolean tag_front = false;
			boolean tag_after = false;
			if(index < 0) continue;
			if(index==0 || !Character.isLetterOrDigit(text.charAt(index-1)))
				tag_front = true;
			if(index + str.length()==text.length() || !Character.isLetterOrDigit(text.charAt(index + str.length()))){
				tag_after = true;
			}
			if(tag_front && tag_after){
		
					return "M";
			}
		}
	
			
			double[] feature = new double[features[0].length];
			double[] IDF = new double[features[0].length];
			Iterator<String> it = voc.iterator();
			int index = 0;
			while(it.hasNext())
			{
				IDF[index] = idf.get(it.next());
				index++;
			}
				
			String[] terms = text.toLowerCase().split("[ ?,.;:!()]");
			for(String t:terms)
			{
				t = t.trim();
				if(t.length()>0 && voc.contains(t))
				{
					int ind = term_index.get(t);
					int frequency = 0;
			
					for(int j=0;j<terms.length;j++)
					{
						if(terms[j].equals(t))
							frequency++;
					}
					feature[ind] = frequency * IDF[ind];
				}
			}
	
			try
			{
				testDataSet.clear();
				testDataSet.add(new DenseInstance(1.0, feature));
				double dist[] = nb.distributionForInstance(testDataSet.get(0));
				for (double d : dist) {
				
				}
				
				double result = nb.classifyInstance(testDataSet.firstInstance());
				
				return classLabel[(int)result];
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "None";
		
	}
	private void connectToDB(String dbName){
		try {  
	      Class.forName("org.sqlite.JDBC");  
	      connection = DriverManager.getConnection("jdbc:sqlite:data/db/"+dbName);
	      statement = connection.createStatement();  
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void closeDB(){
        try {
			statement.close();  
		    connection.close();  
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	private void loadKeyWord(){
		try{
			Scanner scan = new Scanner(new File("data/keywords_M.txt"));
			while(scan.hasNextLine()){
				keywords_M.add(scan.nextLine());
			}
			scan.close();
			
			scan = new Scanner(new File("data/keywords_S.txt"));
			
			scan = new Scanner(new File("data/keywords_G.txt"));
			while(scan.hasNextLine()){
				keywords_G.add(scan.nextLine());
			}
			scan.close();
			
		}catch(Exception e){
			
		}
		
	}
	
	private void loadKeyWordSetsForMovie(){
		connectToDB(DataBase.getMovieDB());
		try {
			ResultSet rs = statement.executeQuery("select * from Person");
			while(rs.next()){
				String personName = rs.getString("name").toLowerCase();
				int indexSpace = personName.lastIndexOf(" ");
				String personLastName = personName.substring(indexSpace+1);
				moviePN.add(personLastName);
				movie_personName.add(personLastName);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		try {
			ResultSet rs = statement.executeQuery("SELECT * FROM Movie");
			while(rs.next()){
				moviePN.add(rs.getString("name").toLowerCase());
				movie_movieName.add(rs.getString("name").toLowerCase());
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		closeDB();
	}
	
	private void loadKeyWordSetsForGeography(){
		connectToDB(DataBase.getGeographyDB());
		try {
			ResultSet rs = statement.executeQuery("SELECT * FROM Cities");
			while(rs.next()){
				geographyPN.add(rs.getString("Name").toLowerCase());
			}
			rs.close();
			
			rs = statement.executeQuery("SELECT * FROM Continents");
			while(rs.next()){
				geographyPN.add(rs.getString("Continent").toLowerCase());
			}
			rs.close();
			
			rs = statement.executeQuery("SELECT * FROM Countries");
			while(rs.next()){
				geographyPN.add(rs.getString("Name").toLowerCase());
			}
			rs.close();
			
			rs = statement.executeQuery("SELECT * FROM Mountains");
			while(rs.next()){
				geographyPN.add(rs.getString("Name").toLowerCase());
			}
			rs.close();
			
			rs = statement.executeQuery("SELECT * FROM Seas");
			while(rs.next()){
				geographyPN.add(rs.getString("Ocean").toLowerCase());
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		closeDB();
	}

	public  LinkedList<KeyQuestion> loadTrainingData(){
		LinkedList<KeyQuestion> qList = new LinkedList<KeyQuestion>();
		Scanner scan = null;
		try {
			scan = new Scanner(new File("data/training_data.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(scan.hasNextLine()){
			String line = scan.nextLine();
			if(line.startsWith("#") || line.length() == 0) continue;
			if(scan.hasNextLine()){
				String classLabel = scan.nextLine();
				qList.add(new KeyQuestion("no-id",line,classLabel.charAt(0)));
			}
		}
		return qList;
	}
	
	public TreeSet<String> getMoviePN() {
		return moviePN;
	}
	public TreeSet<String> getGeographyPN() {
		return geographyPN;
	}
	 

	public TreeSet<String> getMovie_movieName() {
		return movie_movieName;
	}
	public TreeSet<String> getMovie_personName() {
		return movie_personName;
	}
	public TreeSet<String> getKeywords_M() {
		return keywords_M;
	}
	public TreeSet<String> getKeywords_G() {
		return keywords_G;
	}
}
