import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.Math;
import java.util.Scanner;

public class WordleSolver{
    /** Data that consists of every possible words */
    private ArrayList<String> data = new ArrayList<>();
    /** This ArrayList will change after every guess and remove words that isn't right guess for sure */
    private ArrayList<String> changeableData = new ArrayList<>();
    // Scanner for inputs
    private Scanner myObj = new Scanner(System.in); 
    /**
     * Imports the data from specific .txt file and save every word in data ArrayList
     */
    private void importingData(String name){
        try {
            BufferedReader rd = new BufferedReader(new FileReader(name));
            while(true){
                String line = rd.readLine();
                if(line==null)
                    break;
                line = line.trim();
                if(line.length()>0){
                    data.add(line);
                    changeableData.add(line);
                }
            }
            rd.close();
        } catch (Exception e) {
           System.out.println("Couldn't find the file");
        }
    }

    /**
     * Main logic that calculates steps to reach the answer
     * @return steps
     */
    private int logic(){
        String w = "";
        int counter = 0;
        
        while(true){
            double maxEntropy = 0;
            for(String word : changeableData){
                double entropy =  entropy(word);
                if(entropy>maxEntropy || maxEntropy==0){
                    maxEntropy=entropy;
                    w=word;
                }
            } 
            System.out.println("Enter " + w + " for the next guess");
            if(isRight())
                break;
            updateWord(w);
            counter++; 
        }
        return counter;
    }

    /**
     * Asks user if choice is right
     * @return true if it's true and false if it isn't
     */
    private boolean isRight(){
         // Create a Scanner object
        int res;
        while(true){
            try {
                System.out.println("If guess is right, enter 1, otherwise 0");
                res = myObj.nextInt();
                if(res == 1 || res == 0)
                    break;
                else System.out.println("Enter 0 or 1");
            } catch (Exception e) {
                System.out.println("Enter valid number");
            }
        }
        return (res == 1) ? true : false;
    }

    /**
     * Main logic that calculates steps to reach the answer
     * Is used to check the logic and calculate average
     * @return steps
     */
    private int logic(String correctWord){
        String w = "";
        int counter = 0;
        
        while(w.equals(correctWord)==false){
            double maxEntropy = 0;
            for(String word : changeableData){
                double entropy =  entropy(word);
                if(entropy>maxEntropy || maxEntropy==0){
                    maxEntropy=entropy;
                    w=word;
                }
                
            } 
            updateWord(w, correctWord);
            counter++; 
        }
        return counter;
    }

    /**
     * Asks user to enter data after inputing the choosen word in the website
     * @param word choosen word
     */
    private void updateWord(String word){
        HashMap<Integer, String> contains = new HashMap<>();
        ArrayList<String> notContains = new ArrayList<>();
        HashMap<Integer, String> isOnFixedPos = new HashMap<>();
        while(true){
            System.out.println("Write which letter is green from 1-5 and if there's no other green enter -1");
            
            int ind = myObj.nextInt();
            if(ind==-1)
                break;
                
            if(ind>0 && ind<6){
                isOnFixedPos.put(ind-1, ""+word.charAt(ind-1));
            }
        }
        
        while(true){
            System.out.println("Write which letter is yellow from 1-5 and if there's no other yellow enter -1");

            int ind = myObj.nextInt();
            if(ind==-1)
                break;
                
            if(ind>0 && ind<6){
                contains.put(ind-1, ""+word.charAt(ind-1));
            }
        }

        while(true){
            System.out.println("Write which letter is grey from 1-5 and if there's no other grey enter -1");
            
            int ind = myObj.nextInt();
            if(ind==-1)
                break;
            
            if(ind>0 && ind<6){
                notContains.add(""+word.charAt(ind-1));
            }
        }

        changeableData.remove(word);
        Iterator<String> it = changeableData.iterator();

        while(it.hasNext()){
            String next = it.next();
            if(checker(contains,notContains,isOnFixedPos,next)==false){
                it.remove();
            }
        }
    }

    /**
     * Updates data after entering the word
     * It is used for test cases and calculation of average
     * @param word choosen word
     * @param correctWord target word
     */
    private void updateWord(String word, String correctWord){
        HashMap<Integer, String> contains = new HashMap<>();
        ArrayList<String> notContains = new ArrayList<>();
        HashMap<Integer, String> isOnFixedPos = new HashMap<>();

        for(int i = 0; i<word.length(); i++){
            if(correctWord.contains(""+word.charAt(i))==false){
                notContains.add(""+word.charAt(i));
            }else if(correctWord.contains(""+word.charAt(i)) && correctWord.charAt(i)!=word.charAt(i)){
                contains.put(i,""+word.charAt(i));
            }else if(correctWord.contains(""+word.charAt(i)) && correctWord.charAt(i)==word.charAt(i)){
                isOnFixedPos.put(i,""+word.charAt(i));
            }
        }
        changeableData.remove(word);
        Iterator<String> it = changeableData.iterator();

        while(it.hasNext()){
            String next = it.next();
            if(checker(contains,notContains,isOnFixedPos,next)==false){
                it.remove();
            }
        }
    }

    /**
     * Calculates entropy of the word
     * With higher entropy, we get higher expected value of the information
     * @param word
     * @return entropy
     */
    private double entropy(String word){
        /** 
         * Main logic here is that we have the Array which is filled with 0's
         * 0 means grey
         * 1 means yellow
         * 2 means green
         * 
         * Whole array is based 3 variant of integer and after every iteration it is incremented by one
         * Therefore program will check every possible variant
         */
        int[] arr= new int[5];
        double sum = 0;
        while(true){
            double pos = possibility(arr, word);
            if(pos!=0)
                sum+=-pos*(Math.log(pos))/(Math.log(2));
            
            if(arrayIsFull(arr, 3)==false){
                arr = increment(arr, 3);
            }else{
                break;
            }
        }
        
        return sum;
    }

    /**
     * Checks if Array is consists num
     * @param arr
     * @param num
     * @return
     */
    private boolean arrayIsFull(int[]arr, int num){
        for(int i :arr){
            if(i!=num){
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates words and it's colors(green, yellow, grey) possibility
     * @param arr
     * @param word
     * @return
     */
    private double possibility(int[] arr, String word){
        HashMap<Integer, String> contains = new HashMap<>();
        ArrayList<String> notContains = new ArrayList<>();
        HashMap<Integer, String> isOnFixedPos = new HashMap<>();

        for(int i = 0; i<arr.length; i++){
            if(arr[i]==0){
                notContains.add(""+word.charAt(i));
            }else if(arr[i]==1){
                contains.put(i,""+word.charAt(i));
            }else{
                isOnFixedPos.put(i,""+word.charAt(i));
            }
        }    
        double good = 0;
        double len = changeableData.size();
        for(String i :changeableData){
            if(checker(contains,notContains,isOnFixedPos,i)){
                good++;
            }
        }
        return good/len;
    }

    /**
     * Checks if there is any words with custom green, yellow, grey letters
     * @param contains yellow
     * @param notContains grey
     * @param isOnFixedPos green
     * @param word choosen word
     * @return
     */
    private boolean checker(HashMap<Integer, String> contains,ArrayList<String> notContains,HashMap<Integer, String> isOnFixedPos, String word){
        for(int i : contains.keySet()){
            if(contains.get(i).equals(""+word.charAt(i)) || word.contains(contains.get(i))==false){
                return false;
            }
        }

        for(int i : isOnFixedPos.keySet()){
            if(isOnFixedPos.get(i).equals(""+word.charAt(i))==false){
                return false;
            }
        }

        for(String i : notContains){
            ArrayList<Integer> indexes = new ArrayList<>();

            for(int j : contains.keySet()){
                if(i.equals(contains.get(j))){
                    indexes.add(j);
                }
            }
            for(int j : isOnFixedPos.keySet()){
                if(i.equals(isOnFixedPos.get(j))){
                    indexes.add(j);
                }
            }
            for(int j = 0; j<word.length(); j++){
                if(word.charAt(j) == i.charAt(0) && indexes.size()==0){
                    if(word.equals("theer"))
                        System.out.println(j+" "+i);
                    return false;
                }else if(word.charAt(j) == i.charAt(0)){
                    indexes.remove(0);
                }
            }
        }
        return true;
    }


    /**
     * Increments array of based max by one
     * @param arr
     * @param max
     * @return int[] arr
     */
    private int[] increment(int[] arr, int max){
        for(int i=arr.length-1; i>=0; i--){
            if(arr[i]<3){
                arr[i] = arr[i]+1;
                break;   
            }else if(arr[i]==3){
                arr[i]=0;
            }
        }
        return arr;
    }
       

    /**
     * Used for test cases and finding average
     * @param word target
     * @return
     */
    private int wordle(String word){
        if(word.equals("slate"))
            return 1;
        updateWord("slate", word);
        
        int result = logic(word)+1;

        return result;
    }
    
    /**
     * Calculating average points which is 3.8
     * @return
     */
    private double averagePoint(){
        double average = 0;
        int sum = 0;
        double length = data.size();
        for (String word:data){
            System.out.println(word);
            changeableData.clear();
            for(String i : data){
                changeableData.add(i);
            }
            sum+=wordle(word);
        }
        average=sum/length;
        return average;
    }

    /**
     * Method which is user friendly
     * @return steps
     */
    private int wordle(){
        /**
         * First the best word is slate but it needs time to calculate that, that's why I manually enter that word
         */
        String firstGuess = "slate";
        System.out.println("Enter " + firstGuess + " for the next guess");
        if(isRight() == true)
            return 1;
        
        updateWord("slate");
        
        int result = logic()+2;

        return result;
    }

    public void run(){
        importingData("wordle.txt");
        System.out.println(wordle());
        myObj.close();
    }
	public static void main(String[] args){
		new WordleSolver().run();
	}	
}