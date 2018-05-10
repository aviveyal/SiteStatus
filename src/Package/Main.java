package Package;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class Main implements Initializable {

	@FXML
	PieChart pieChart;
	@FXML
	BorderPane borderPane;
	@FXML
	Label label;
	
	@FXML
	DatePicker DatePickerFrom;
	@FXML
	ProgressIndicator progress;
	
	@FXML
	DatePicker DatePickerTo;
	@FXML
	ChoiceBox choiceBox;
	
	
	

	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
	@FXML
	Label caption = new Label("");
	
	Double total=0.0;
	String from;
	Double dateDifferance;
	String to;
	String firstDate = "";
	String secondDate = "";
	boolean flag = false;
	Double timing = 0.0;
	Date checkDate ;
	ArrayList<String> Sites = new ArrayList<String>();
	
	
	HashMap<String, Double> map = new HashMap<String, Double>();
	// ArrayList transactionList = new ArrayList<>();

	JSONParser parser = new JSONParser();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		choiceBox.getItems().add("Haifa bay port");
        choiceBox.getItems().add("Intel - FAB 28");
        choiceBox.getItems().add("S32 Mine");
        choiceBox.getItems().add("S32 Refinery");
        choiceBox.getItems().add("BHP Area C");
        choiceBox.getItems().add("BHP San Manuel");
        choiceBox.getItems().add("Minera Centinela");
        choiceBox.getItems().add("Vale NC1");
        choiceBox.getItems().add("Vale NC2");
        
        
        Sites.add("705");
        Sites.add("704");
        Sites.add("706");
        Sites.add("708");
        Sites.add("707");
        Sites.add("723");
        Sites.add("737");
        Sites.add("715");
        Sites.add("714");
        progress.setVisible(true);
		
	}
	public void searchButton(){
		
		String Site="";
		resetVariables();
		switch (choiceBox.getSelectionModel().getSelectedItem().toString()){
		case "Haifa bay port":
			Site="705";
			break;
		case "Intel - FAB 28":
			Site="704";
			break;
		case "S32 Mine":
			Site="706";
			break;
		case "S32 Refinery":
			Site="708";
			break;
		case "BHP Area C":
			Site="707";
			break;
		case "BHP San Manuel":
			Site="723";
			break;
		case "Minera Centinela":
			Site="737";
			break;
		case "Vale NC1":
			Site="715";
			break;
		case "Vale NC2":
			Site="714";
			break;
			
		}
		
		//System.out.println(DatePickerFrom.getValue());
		GraphGenerate(Date.from(DatePickerFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) , 
				Date.from(DatePickerTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),Site );

		loadPieChart();	
	}
	
	public void resetVariables(){
		pieChart.getData().clear();
		pieChartData.clear();
		map.clear();
		total=0.0;
		timing=0.0;
		firstDate = "";
		secondDate = "";
		flag = false;
	}
	

	public void GraphGenerate(Date PickerFrom ,Date PickerTo , String site ) {
		System.out.println(site);
			timing=0.0;	
		try {
			Object obj = parser.parse(new FileReader("./tickets/"+site+".json"));
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject changelog = (JSONObject) jsonObject.get("changelog");
			JSONArray histories = (JSONArray) changelog.get("histories");
			if (!histories.isEmpty()) {

				for (int i = 0; i < histories.size(); i++) {
					JSONObject history = (JSONObject) histories.get(i);
					JSONArray items = (JSONArray) history.get("items");

					for (int j = 0; j < items.size(); j++) {
						JSONObject item = (JSONObject) items.get(j);
						String field = (String) item.get("field");
						if (field.equals("status")) {
							
							checkDate = new SimpleDateFormat("yyyy-MM-dd").parse(((String) history.get("created")).split("T",2)[0]); //change format to date from json
						if((checkDate.after(PickerFrom) || checkDate.equals(PickerFrom)) && ( checkDate.before(PickerTo)|| checkDate.equals(PickerTo))) //check dates range 
						{
							if (!flag) {
								firstDate = (String) history.get("created");
								flag = true;
							} else {
								secondDate = (String) history.get("created");
								from = (String) item.get("fromString");
								
								//System.out.println(from);
								if (map.containsKey(from)) {
									timing = map.get(from);
									map.put(from, timing + calculateDateDifferance(secondDate, firstDate));
								} else {
									map.put(from, calculateDateDifferance(secondDate, firstDate));
								}

								flag = true;
								firstDate = secondDate;
							}
							from = (String) item.get("fromString");
							to = (String) item.get("toString");
						}
						}
					}
				}
			}

					
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public void loadPieChart(){
		total=0.0;		
		for (String key : map.keySet()) {
			pieChartData.add(new PieChart.Data(key, round(map.get(key), 2))); // add data to pie chart
			total= total+ round(map.get(key), 5);
			System.out.println(key + " - " + map.get(key));

		}

		// set pieChart data and settings
		pieChart.setLegendSide(Side.LEFT);
		pieChart.setData(pieChartData);
		pieChart.setTitle("Site Activity");
		pieChartData.forEach(data -> data.nameProperty()
				.bind(Bindings.concat(data.getName(), " for ", data.pieValueProperty(), " Hours")));
		
		
		//set mpuse over functions
		pieChart.getData().stream().forEach(data -> {
			data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
				label.setVisible(true);
				label.setText(round(((data.getPieValue() *100 )/total),2) +"%");	
				label.setTranslateX(e.getSceneX());
				label.setTranslateY(e.getSceneY() + 20);
				data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, (x) -> {
					label.setVisible(false);
					

				});					

			});
		});
		
		pieChart.getData().stream().forEach(data -> {
			data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
				label.setVisible(false);
				

			});
		});
		
		//borderPane.getChildren().add(pieChart);
	}

	public static Double calculateDateDifferance(String Date1, String Date2) throws ParseException {
		Date date1 = new Date();
		Date date2 = new Date();
		String[] DateTime1 = Date1.split("T");
		String[] DateTime2 = Date2.split("T");
		String finalDate1 = "";
		String finalDate2 = "";
		Double result = 0.0;

		DateTime1[1] = DateTime1[1].split("\\.", 2)[0];
		DateTime2[1] = DateTime2[1].split("\\.", 2)[0];

		finalDate1 = DateTime1[0] + " " + DateTime1[1];
		finalDate2 = DateTime2[0] + " " + DateTime2[1];
		//System.out.println(Date1 + "," + Date2);
		date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(finalDate1);
		date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(finalDate2);
		// SimpleDateFormat.parse(String);

		//System.out.println(date1 + "-" + date2);
		Double dif = (double) (date1.getTime() - date2.getTime());
		dif = dif / 1000;

		long days = (long) (dif / (24 * 60 * 60));
		Double hours = dif / (60 * 60) % 24;
		Double minutes = dif / 60 % 60;
		Double seconds = dif % 60;

		//System.out.println(days + "," + hours + "," + minutes + "," + seconds);
		result += (24 * days);
		result += hours;
		result += (minutes / 60);
		result += (seconds / 360);
	//	System.out.println(result);

		return result;

	}
	public void updateProgrss(Double add){
		Double current = progress.getProgress();
		progress.setProgress(0.1+current);
	}
	
	 public Task updateProgress() {
	        return new Task() {
	            @Override
	            protected Object call() throws Exception {
	                for (int i = 0; i < 10; i++) {
	                    Thread.sleep(1000);
	                    updateMessage("1000 milliseconds");
	                    updateProgress(i + 1, 10);
	                }
	                return true;
	            }
	        };
	    }
	
	
	public void updateData() throws IOException{ //download data from jira
		System.out.println("downloading...");
		updateProgress();
		double percent = 100/Sites.size();
		
		for (String site : Sites){
			
			updateProgrss(percent);
		String url = "https://jira.airobotics.co.il:8443/rest/api/2/issue/UCC-"+site+"?expand=changelog";
	     URL obj = new URL(url);
	     String encoding = Base64.getEncoder().encodeToString(("Commandcenter:123456789").getBytes());
	     HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	     // optional default is GET
	     con.setRequestMethod("GET");
	     con.setRequestProperty ("Authorization", "Basic " + encoding);
	     //add request header
	     con.setRequestProperty("User-Agent", "Mozilla/5.0");
	     int responseCode = con.getResponseCode();
	     System.out.println("\nSending 'GET' request to URL : " + url);
	     System.out.println("Response Code : " + responseCode);
	     BufferedReader in = new BufferedReader(
	             new InputStreamReader(con.getInputStream()));
	     String inputLine;
	     StringBuffer response = new StringBuffer();
	     while ((inputLine = in.readLine()) != null) {
	     	response.append(inputLine);
	     }
	     in.close();
	     //print in String
	     System.out.println(response.toString());
	     //Read JSON response and print
	     //JSONObject myResponse = new JSONObject(response.toString());
	     
	     try {      
	         FileOutputStream fos = new FileOutputStream("./tickets/"+site+".json");
	          fos.write(response.toString().getBytes());
	           fos.close();      
	         //Log.d(TAG, "Written to file");  
	       } 
	     catch (Exception e)
	       {    
	              
	           e.printStackTrace(); 
	        } 
	     
		} 
	}

}