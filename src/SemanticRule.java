

import java.util.ArrayList;

public class SemanticRule {
	public SemanticRule(){}
	public SemanticRule(SemanticRule2 rule){
		this.rule = rule;
	}
	SemanticRule2 rule = null;
	ArrayList<SemanticRule> children = new ArrayList<SemanticRule>();
	public String toString(){
		return rule.toString();
	}
	public void print(){
		printNode(this);
	}
	private void printNode(SemanticRule root){
		if(root!=null){
			System.out.println(root.rule);
			for (int i = 0; i < root.children.size(); i++) {
				printNode(root.children.get(i));
			}
		}
	}
	public SemanticRule getParentNode(SemanticRule root, SemanticRule target){
		if(root!=null){
			for (SemanticRule child : root.children) {
				if( child == target) return root;
				SemanticRule res = getParentNode(child, target); 
				if(res!=null) return res;
			}
		}
		return null;
	}
}
