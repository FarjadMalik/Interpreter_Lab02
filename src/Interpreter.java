import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Interpreter {
	ArrayList<Variable> variables; //to hold all the variables

	public Interpreter() {
		//initializes the arraylist
		variables = new ArrayList<Variable>();
	}
	//Reads the file line by line and with response to each line sends it to the respective function
	public void fileRead(String filename) { //File read function
		
			int counter=1;	
		try {
			FileReader r = new FileReader(filename);
			BufferedReader br = new BufferedReader(r);
			String line; //each new line read is stored here
						
		    while((line = br.readLine()) != null){ //reads a line until end of file
		    	String[] tokens = line.split(" "); //splits the line on basis of spaces and stores in token array
		    	
		    	line.trim();
		    	if(line.length()==0){
		    		//empty line
		    	}
		    	else if(line.contains("#")){
		    		//this is the comment
		    	}
		    	else if(line.contains("let") || line.contains("Let")){
		    		CreateVariable(tokens,counter);
		    	}else if(line.contains("print")){
		    		Printing(tokens,counter);
		    	}else if(tokens[1].equalsIgnoreCase("=")){
		    		evaluateOperations(line,counter);
		    	}
		    	else{
	    			syntaxError(counter);
	    		}
		    	counter++;
		    }
		    br.close();
		    
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(NullPointerException e){
			System.out.println("Null Pointer Exception Error: Your file is probably not formatted properly. Read the ProjectDocumentation file. ");
			
		}
		
		
	    
		
	}
// Used for assignment evaluation by creating a string and substituing values for all variables and then passing it to PostFixExpression and then solving the result in the desired variable
	private void evaluateOperations(String line,int lineNumber) {
		int result;
		String[] tokens = line.split(" = ");
		String expressionPFE="(";
		if(tokens.length == 2){
			String variable = tokens[0];
			String expression = tokens[1];
			String [] expressionsVariables = expression.split(" ");
			
			for(int i=0;i<expressionsVariables.length;i++){
				if(expressionsVariables[i].equalsIgnoreCase("+") ||
						expressionsVariables[i].equalsIgnoreCase("-") ||
						expressionsVariables[i].equalsIgnoreCase("*") ||
						expressionsVariables[i].equalsIgnoreCase("/")
						){
					expressionPFE+=expressionsVariables[i];
				}
				else if(Character.isDigit(expressionsVariables[i].charAt(0))){
					expressionPFE+=Integer.parseInt(expressionsVariables[i]);
				}
				else{
					boolean check = false;
					
					for(Variable var:variables){
						if(var.getName().equalsIgnoreCase(expressionsVariables[i])){
							expressionPFE+=var.getValue();
							check=true; // check if that variable is present or not
						}
					}
					if(!check){
						syntaxError(lineNumber);
						return ;
					}
				}
				
			}
			expressionPFE+=")";
			PostFixExpression pfe = new PostFixExpression(expressionPFE);
			result = pfe.postFixValue(pfe.getPostFix());
			boolean check = false;
			
			for(Variable var:variables){
				if(var.getName().equalsIgnoreCase(variable)){
					var.setValue(result);
					check=true; // check if that variable is present or not
				}
			}
			if(!check){
				syntaxError(lineNumber);
				return ;
			}
			
		}else{
			syntaxError(lineNumber);
		}
		
	}
// displays the error
		private void syntaxError(int lineNumber) {
		System.out.println("Error on line number " + lineNumber );
	}
// Only used for print statements. Either prints a variable or a string with double qoutes
	private void Printing(String[] tokens,int lineNumber) {
		boolean check = false;
		//check for more prints as in if print "hello" 
		if(tokens[1].contains("\"")){
			String line = tokens[1];
			line = line.substring(1, line.length()-1);
			System.out.println(line);
		}
		else{
			for(Variable var:variables){
				if(var.getName().equalsIgnoreCase(tokens[1])){
					System.out.println(var.getValue()+"");
					check=true;
				}
			}
			if(!check){
				syntaxError(lineNumber);
			}
		}
	}
// used for let key word. Defines a new variable.
	private void CreateVariable(String[] tokens,int lineNumber) {
		//right per koi variable
		if(Character.isDigit(tokens[1].charAt(0)) || !tokens[2].equalsIgnoreCase("=")){
			syntaxError(lineNumber);
		}
		else{
			Variable temp = new Variable();
			temp.setName(tokens[1]);
			if(Character.isDigit(tokens[3].charAt(0))){
				temp.setValue(Integer.parseInt(tokens[3]));
			}else{
				boolean check = false;
				for(Variable var:variables){
					if(var.getName().equalsIgnoreCase(tokens[3])){
						temp.setValue(var.getValue());
						check=true; // check if that variable is present or not
					}
				}
				if(!check){
					syntaxError(lineNumber);
					return ;
				}
				
			}
			variables.add(temp);
		}
	}
}
