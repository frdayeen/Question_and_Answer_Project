

public class Str
{
	public static boolean contains(String question_low, String name)
	{
		int index = question_low.indexOf(name);
		if(index>=0)
		{
			if(index==0 || 
					(!Character.isLetterOrDigit(question_low.charAt(index-1))  &&   question_low.charAt(index-1)!='\''   )  
			  )
			  if(index+name.length()==question_low.length() ||
			        ( !Character.isLetterOrDigit(question_low.charAt(index+name.length())) && question_low.charAt(index+name.length())!='\'')
			    )
					return true;
		}
		return false;
	}
	public static boolean isNumber(String token){
		for (int i = 0; i < token.length(); i++) {
			char c = token.charAt(i);
			if(i==0 && c=='-')
				continue;
			if(!Character.isDigit(c)) return false;
		}
		return true;
	}
	public static String SQLiteStringGetValid(String str){
		return str.replaceAll("'", "''");
	}
	/*
	 *  whether all positions have 1 in str2 are all 1 in str1
	 */
	public static boolean bitCover(String str1, String str2){
		for (int i = 0; i < str2.length(); i++) {
			if(str2.charAt(i)=='1'){
				if(str1.charAt(i)!='1') return false;
			}
		}
		return true;
	}
}
