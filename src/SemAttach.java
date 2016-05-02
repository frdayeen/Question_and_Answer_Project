

import java.io.StringReader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;



import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.StringUtils;


public class SemAttach {
	static LexicalizedParser lp = ProjectParser.getModel();
	/*
	 * Input: a question as string
	 * Output: a RuleTree as a root node
	 */
	public static void getRulesAndRelation(String question, SemanticRule rTree ){
		//rTree
		String text = question;
	    TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	    List<CoreLabel> rawWords = tokenizerFactory.getTokenizer(new StringReader(text)).tokenize();
	    Tree root = lp.apply(rawWords);
	    getRuleAndRelationFromNode(root, rTree);
	}
	/*
	 *  recursive function called by getRulesAndRelation, build tree in a top down manner
	 */
	private static void getRuleAndRelationFromNode(Tree root, SemanticRule rTree){
		if(root == null) return ;
		List<Tree> children = root.getChildrenAsList();
		ArrayList<String> right = new ArrayList<String>();
		SemanticRule2 rule = null;
		for (Tree tree : children) {
			right.add(tree.label().value());
		}
		if(right.size()!=0) {
			rule = new SemanticRule2(root.label().value(),right);
			rTree.rule = rule;
		}
		for (Tree child : children) {
			SemanticRule childRule = new SemanticRule();
			getRuleAndRelationFromNode(child,childRule);
			if(childRule.rule!=null)rTree.children.add(childRule);
		}
	}
	/*
	 *   Input: the RuleTree root
	 *   Output: assign the semantic attachment to each node of the tree
	 */
	public static void setSemantic(String cat, SemanticRule root, String annotatedQuestion){
		setSemanticForNode(cat, root, root , annotatedQuestion);
	}
	/*
	 * 	  recursive function called by setSemantic, set semantic attachment in a bottom up manner
	 */
	public static void setSemanticForNode(String cat, SemanticRule root, SemanticRule node, String annotatedQuestion){
		if(node == null || root ==null) return ;
		for (SemanticRule child : node.children) {
			setSemanticForNode(cat, root, child, annotatedQuestion);
		}
		SemanticRule2 r = node.rule;
		//  (0) ROOT => *
		if (r.left.equals("ROOT")){
			r.sem = node.children.get(0).rule.sem;
		}// (1) VB => direct
		else if(r.left.equals("VB") && r.right.size() ==1 && r.right.get(0).equalsIgnoreCase("direct")){
			r.sem = "(lambda :y :x)" +
					"{[FROM] += FROM Person P INNER JOIN Director D ON P.id = D.director_id INNER JOIN Movie M ON D.movie_id = M.id ," +
					"[WHERE] += P.Name LIKE '%:x' ," +
					"[WHERE] += M.name LIKE '%:y%' ," +
					"}";
		}// (1.2) VB => star
		else if(r.left.equals("VB") && r.right.size() ==1 && r.right.get(0).equalsIgnoreCase("star")){
			r.sem = "(lambda :y :x)" +
					"{[FROM] += FROM Person P INNER JOIN Actor A ON P.id = A.actor_id INNER JOIN Movie M ON A.movie_id = M.id ," +
					"[WHERE] += P.Name LIKE '%:x' ," +
					"[WHERE] += M.name LIKE '%:y%' ," +
					"}";
		}// (1.3) VB => act
		else if(r.left.equals("VB") && r.right.size() ==1 && r.right.get(0).equalsIgnoreCase("act")){
			r.sem = "(lambda :y :x)" +
					"{[FROM] += FROM Person P INNER JOIN Actor A ON P.id = A.actor_id INNER JOIN Movie M ON A.movie_id = M.id ," +
					"[WHERE] += P.Name LIKE '%:x' ," +
					"[WHERE] += M.name LIKE '%:y%' ," +
					"}";
		}// (1.4) VB => win
		else if(r.left.equals("VB") && r.right.size() ==1 && r.right.get(0).equalsIgnoreCase("win")){
			if(cat.equals("M")){
				// Movie win  or Person win 
				if(annotatedQuestion.contains("MOVIE-")){
					r.sem = "(lambda :x)" +
							"{[FROM] += FROM Person P INNER JOIN Oscar O ON P.id = O.person_id INNER JOIN Movie M ON O.movie_id = M.id ," +
							"[WHERE] += M.Name LIKE '%:x' " +
							"}";
				}else if(annotatedQuestion.contains("MOVIEWORKER-")){
					r.sem = "(lambda :x)" +
							"{[FROM] += FROM Person P INNER JOIN Oscar O ON P.id = O.person_id INNER JOIN Movie M ON O.movie_id = M.id ," +
							"[WHERE] += P.Name LIKE '%:x'" +
							"}";
				}
					
			}else if(cat.equals("S")){
				
			}else if(cat.equals("G")){
				
			}
		}
		// (2) NNP => Romeo 
		else if(r.left.equals("NNP") && r.right.size() == 1){
			r.sem = new String(r.right.get(0));
		}// (3) NP => NNP
		else if(r.left.equals("NP") && r.right.size() == 1 && r.right.get(0).equals("NNP")){
		//	r.sem = new String(r.)
			r.sem = node.children.get(0).rule.sem;
		}// (4) VP => VB NP   {VP.sem(VB.sem)}
		else if(r.left.equals("VP") && r.right.size() == 2 && r.right.get(0).equals("VB") && r.right.get(1).equals("NP") ){
			r.sem = node.children.get(0).rule.sem+="<"+ node.children.get(1).rule.sem +">";
		}// (4.2) VP => VB PP {VP.sem(VB.sem)}
		else if(r.left.equals("VP") && r.right.size() == 2 && r.right.get(0).equals("VB") && r.right.get(1).equals("PP") ){
			r.sem = node.children.get(0).rule.sem+="<"+ node.children.get(1).rule.sem +">";
		}// (5) VBD  => did
		else if(r.left.equals("VBD") && r.right.size() ==1 && r.right.get(0).equalsIgnoreCase("did")){
			r.sem = null;
		}// (6) PP => IN NP {PP.sem(NP.sem)}
		else if(r.left.equals("PP") && r.right.size() ==2 && r.right.get(0).equalsIgnoreCase("IN") && r.right.get(1).equalsIgnoreCase("NP")){
			r.sem = node.children.get(1).rule.sem;
		}
		// (7) . = > ?
		else if(r.left.equals(".")){
			r.sem = null;
		}// (8) SQ => VBD NP VP .
		else if(r.left.equals("SQ") && r.right.size() == 4 && r.right.get(0).equals("VBD")
				&& r.right.get(1).equals("NP") && r.right.get(2).equals("VP")){
			r.sem = "[SELECT] += SELECT count(*)," +
				     node.children.get(2).rule.sem + "<" + node.children.get(1).rule.sem +">";
		}// (8.2) SQ => NNP NP VP .
		else if(r.left.equals("SQ") && r.right.size() == 4 && r.right.get(0).equals("NNP")
				&& r.right.get(1).equals("NP") && r.right.get(2).equals("VP")){
			r.sem = "[SELECT] += SELECT count(*)," +
				     node.children.get(2).rule.sem + "<" + node.children.get(1).rule.sem +">";
		}// (8.3) SQ => VBP NP VP . {SQ.sem(NP.sem)}
		else if(r.left.equals("SQ") && r.right.size() == 4 && r.right.get(0).equals("VBP")
				&& r.right.get(1).equals("NP") && r.right.get(2).equals("VP")){
			r.sem = "[SELECT] += SELECT count(*)," +
				     node.children.get(2).rule.sem + "<" + node.children.get(1).rule.sem +">";
		}
	}
	

	/*
	 *   now only supports one level reduction
	 *   assuming lamda variables only one letter  e.g. :x :y  
	 */
	public static String lamdaReduction(String sqlSem){
		
		int begin = sqlSem.indexOf("{");
		int end = sqlSem.lastIndexOf("}");
		int end2 = sqlSem.lastIndexOf(")", begin);
		int begin2 = sqlSem.lastIndexOf("(", begin);
		
		String prefix = sqlSem.substring(0,begin2);
		
		String str1 = sqlSem.substring(begin+1,end);
		String str2 = sqlSem.substring(begin2+1,end2);
		System.out.println(str1);
		System.out.println(str2);
		
		int nVariable = str1.replaceAll("[^:]", "").length();
		System.out.println(nVariable);
		
		String[] vars = new String[nVariable];
		int start_of_variable = end;
		for (int i = 0; i < nVariable; i++) {
			// find the Variable
			int begin3 = sqlSem.indexOf("<", start_of_variable+1);
			int end3 = sqlSem.indexOf(">", start_of_variable+1);
			vars[i] = sqlSem.substring(begin3+1,end3);
			System.out.println(vars[i]);
			start_of_variable = end3;
		}
		
		String postfix = sqlSem.substring(start_of_variable+1);
				
		System.out.println(prefix);
		System.out.println(postfix);
		int index_of_variable = 0;
		for (int i = 0; i < str2.length(); i++) {
			if(str2.charAt(i)==':'){
				str1 = str1.replaceAll(":"+str2.charAt(++i), vars[index_of_variable++]);
			}
		}
		
		System.out.println(prefix+"\n"+ str1+ "\n" + postfix);
		
		return prefix + str1 + postfix;			
	}
	/*
	 * after all possible lamda reduction, translate the sqlSem string to SQL codes
	 */
	public static String translateToSQL(String sqlSem){
		String pieces[] = sqlSem.split(",");
		String SELECT = "", FROM ="", WHERE = "WHERE";
		int cnt_of_where = 0;
		for (int i = 0; i < pieces.length; i++) {
			String pieces2[] = pieces[i].split("\\+=");
			if(pieces2[0].trim().equals("[SELECT]")){
				SELECT += pieces2[1];
			}else if(pieces2[0].trim().equals("[FROM]")){
				FROM += pieces2[1];
			}else if(pieces2[0].trim().equals("[WHERE]")){
				if(cnt_of_where == 0) { WHERE += pieces2[1]; cnt_of_where ++; }
				else {WHERE += " and " + pieces2[1];}
			}
				
		}
		System.out.println("SELECT="+SELECT);
		System.out.println("FROM="+FROM);
		System.out.println("WHERE="+WHERE);
		return SELECT +" " + FROM +" " + WHERE;
	}
	/*
	 *  question to SQL code
	 */
	public static String questionToSQL(String cat, String question){
		 SemanticRule rTree = new SemanticRule(null);
		 SemAttach.getRulesAndRelation(question, rTree);
		 SemAttach.setSemantic(cat, rTree, question);
		 String sqlSem = rTree.rule.sem;
		 System.out.println("�� sqlsem WITHOUT reduction��:" + sqlSem);
	     sqlSem = SemAttach.lamdaReduction(sqlSem);
	     System.out.println("�� sqlsem WITH reduction��:" + sqlSem);
		 String sql = SemAttach.translateToSQL(sqlSem);
		 
		 return sql;
	}
}
