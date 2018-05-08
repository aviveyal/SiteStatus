
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {

	public static void main(String[] args) {
		int i = 0;
		int j=0;
		String from;
		Double dateDifferance;
		String to;
		String firstDate="";
		String secondDate="";
		boolean flag=false;
		Double timing=0.0;
		HashMap<String, Double> map = new HashMap<String, Double>();
		//ArrayList transactionList = new ArrayList<>();
				
		JSONParser parser = new JSONParser();
		
        try
        {
        	Object obj = parser.parse(new FileReader("./tickets/707.json"));
        	JSONObject jsonObject = (JSONObject) obj;
        	JSONObject changelog = (JSONObject)jsonObject.get("changelog");
           	JSONArray histories = (JSONArray)changelog.get("histories");
          if (!histories.isEmpty() ) {
            
        	  for (i = 0; i < histories.size(); i++) {
        		  JSONObject history = (JSONObject) histories.get(i);
        		  JSONArray items = (JSONArray)history.get("items");
        		  
        		  for (j=0; j < items.size(); j++)
        		  {
        			  JSONObject item = (JSONObject) items.get(j);
        			  String field =(String) item.get("field");
        			  if (field.equals("status"))
        			  {
        				  if(!flag){
        					firstDate = (String) history.get("created");
        					flag = true;
        				  }
        				  else
        				  {
        					  secondDate= (String) history.get("created");
         					  from = (String) item.get("fromString");
         					   //calculateDateDifferance (secondDate,firstDate);
        					 // System.out.println("status: "+ from +" to: "+ secondDate+ " - " + firstDate );
         					  System.out.println(from);
        					  if(map.containsKey(from))
        					  {
        						  timing= map.get(from);
        						  map.put(from, timing+calculateDateDifferance (secondDate,firstDate));
        					  }
        					  else
        					  {
        						  map.put(from, calculateDateDifferance (secondDate,firstDate));
        					  }
        					  
        					  flag= true;
        					  firstDate = secondDate;
        				  }
        				  from = (String) item.get("fromString");
        				  to = (String) item.get("toString");
        				  //System.out.println("changed from: "+ from +" to: "+ to+ " on: " +firstDate );
        				  
        			  }
        		  }
        		}
        	}

          
          for (String key : map.keySet())
          {
        	  System.out.println(key +" - "+map.get(key));
          }
          
         }
        catch(FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
	}


public static Double calculateDateDifferance(String Date1, String Date2) throws ParseException{
	Date date1 = new Date();
	Date date2 = new Date();
	String[] DateTime1 = Date1.split("T");
	String[] DateTime2 = Date2.split("T");
	String finalDate1="";
	String finalDate2="";
	Double result = 0.0;
	
	DateTime1[1] = DateTime1[1].split("\\.",2)[0];
	DateTime2[1] = DateTime2[1].split("\\.",2)[0];


	finalDate1= DateTime1[0] +" "+ DateTime1[1];
	finalDate2 =DateTime2[0] +" "+ DateTime2[1];
	System.out.println(Date1+","+Date2);
	date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(finalDate1);
	date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(finalDate2);
	//SimpleDateFormat.parse(String);
	
	System.out.println(date1+"-"+date2);
	Double dif = (double) (date1.getTime() - date2.getTime());
	dif = dif / 1000;

    long days = (long) (dif / (24 * 60 * 60));
    Double hours = dif / (60 * 60) % 24;
    Double minutes = dif / 60 % 60;
    Double seconds = dif % 60;
    
    System.out.println(days+","+hours+","+minutes+","+seconds);
    result += (24*days);
    result += hours;
    result += (minutes/60);
    result += (seconds /360);
    System.out.println(result);
				
	return result;
	
}

}