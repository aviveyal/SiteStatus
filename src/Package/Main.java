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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;


public class Main implements Initializable {

	@FXML
	PieChart pieChart;
	
	@FXML
	PieChart pieChartOutSite;
	
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
	
//	@FXML
//	MenuButton menuButton;
	
	@FXML
	CheckBox Haifa = new CheckBox("Haifa bay port");
	@FXML
	CheckBox Intel = new CheckBox("Intel - FAB 28");
	@FXML
	CheckBox S32Mine = new CheckBox("S32 Mine");
	@FXML
	CheckBox S32Ref = new CheckBox("S32 Refinery");
	@FXML
	CheckBox AreaC = new CheckBox("BHP Area C");
	@FXML
	CheckBox SanManuel = new CheckBox("BHP San Manuel");
	@FXML
	CheckBox Minera = new CheckBox("Minera Centinela");
	@FXML
	CheckBox ValeNc1 = new CheckBox("Vale NC1");
	@FXML
	CheckBox ValeNc2 = new CheckBox("Vale NC2");
	
	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
	//ObservableList<PieChart.Data> pieChartDataOutSite = FXCollections.observableArrayList();
	
	@FXML
	Label caption = new Label("");
	
//	@FXML
//	ListView listView ;
	
	@FXML
	ListView<String> listView = new ListView<String>(); 
	
	Stage stage;
	boolean running;
	Double total = 0.0;
	String from;
	Double dateDifferance;
	String to;
	String firstDate = "";
	String secondDate = "";
	boolean flag = false;
	Double timing = 0.0;
	Date checkDate;
	int siteChecked=0;
	ArrayList<String> Sites = new ArrayList<String>();
	//ObservableList<String> data = new ObservableList<String>();
	ArrayList<History> historyList = new ArrayList<History>();
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	HashMap<String, Double> map = new HashMap<String, Double>();
	// ArrayList transactionList = new ArrayList<>();

	JSONParser parser = new JSONParser();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		
//		choiceBox.getItems().add("Haifa bay port");
//		choiceBox.getItems().add("Intel - FAB 28");
//		choiceBox.getItems().add("S32 Mine");
//		choiceBox.getItems().add("S32 Refinery");
//		choiceBox.getItems().add("BHP Area C");
//		choiceBox.getItems().add("BHP San Manuel");
//		choiceBox.getItems().add("Minera Centinela");
//		choiceBox.getItems().add("Vale NC1");
//		choiceBox.getItems().add("Vale NC2");
//		
				
		Sites.add("705");
		Sites.add("704");
		Sites.add("706");
		Sites.add("708");
		Sites.add("707");
		Sites.add("723");
		Sites.add("737");
		Sites.add("715");
		Sites.add("714");
		// progress.setVisible(false);

		//init DatePickerFrom
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/y");
        DatePickerFrom.setConverter(new StringConverter<LocalDate>() {

            @Override
            public String toString(LocalDate date) {
                if (date == null) {
                    return "" ;
                }
                return formatter.format(date);
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null ;
                }
                return LocalDate.from(formatter.parse(string));
            }

        });

        DatePickerFrom.valueProperty().addListener((obs, oldDate, newDate) -> 
            System.out.println("Selected "+newDate));

        
      //init DatePickerTo
        DatePickerTo.setConverter(new StringConverter<LocalDate>() {

            @Override
            public String toString(LocalDate date) {
                if (date == null) {
                    return "" ;
                }
                return formatter.format(date);
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null ;
                }
                return LocalDate.from(formatter.parse(string));
            }

        });

        DatePickerTo.valueProperty().addListener((obs, oldDate, newDate) -> 
            System.out.println("Selected "+newDate));
        
		

        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                String item =listView.getSelectionModel().getSelectedItem();
                String []Sites = item.split(", ", 10);
               // DatePickerFrom.setValue(new LocalDate(1, 5, 18));
                unSelectAll();
                for (String site : Sites){
                	getCheckBox(site).setSelected(true);
                }
                searchButton();
            }
        });    
        
        
	}

	public void searchButton() {
		
		
		ArrayList<String> SitesList = new ArrayList<String>();
		resetVariables();

		if(Haifa.isSelected())
			SitesList.add("705");
		if(Intel.isSelected())
			SitesList.add("704");
		if(S32Mine.isSelected())
			SitesList.add("706");
		if(S32Ref.isSelected())
			SitesList.add("708");
		if(AreaC.isSelected())
			SitesList.add("707");
		if(SanManuel.isSelected())
			SitesList.add("723");
		if(Minera.isSelected())
			SitesList.add("737");
		if(ValeNc1.isSelected())
			SitesList.add("715");
		if(ValeNc2.isSelected())
			SitesList.add("714");
		// System.out.println(DatePickerFrom.getValue());
		GraphGenerate(Date.from(DatePickerFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
				Date.from(DatePickerTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()), SitesList);
		siteChecked = SitesList.size(); 
		
		addToHistory(new History(DatePickerTo.getValue(),DatePickerFrom.getValue() ,SitesList));
		loadPieChart();
		
	}
	public void addToHistory(History history){
		historyList.add(history);
		String sb = "";
		for(String site : history.siteList)
		{
			
			sb+=getSiteName(site);
			//if not last site- add comma
			if(site!= history.siteList.get(history.siteList.size()-1))
			{
				sb+=", ";
			}
		}
		if(sb !=""){
			if(!listView.getItems().contains(sb)){
//			sb+=" from ";
//			sb+=history.DatePickerFrom;
//			sb+=" to ";
//			sb+=history.DatePickerTo;
			listView.getItems().add(sb);
		}
		}
	}
	
	public String getSiteName(String ticket){
		
		String siteName = null;
		switch(ticket)
		{
				
		case "705":
			siteName="Haifa bay port";
			break;
		case "704":
			siteName="Intel";
			break;
		case "706":
			siteName="S32 Mine";
			break;
		case "708":
			siteName="S32 Refinery";
			break;
		case "707":
			siteName="BHP Area C";
			break;
		case "723":
			siteName="BHP San Manuel";
			break;
		case "737":
			siteName="Minera Centinela";
			break;
		case "715":
			siteName="Vale NC1";
			break;
		case "714":
			siteName="Vale NC2";
			break;
			
		}
		
		return siteName;
		
		
	}
	
public CheckBox getCheckBox(String SiteName){
		
		String checkBox = null;
		switch(SiteName)
		{
				
		case "Haifa bay port":
			return Haifa;
		case "Intel":
			return Intel;
		case "S32 Mine":
			return S32Mine;
		case "S32 Refinery":
			return S32Ref;
		case "BHP Area C":
			return AreaC;
		case "BHP San Manuel":
			return SanManuel;
		case "Minera Centinela":
			return Minera;
		case "Vale NC1":
			return ValeNc1;
		case "Vale NC2":
			return ValeNc2;
			
		}
		return null;
		
		
		
	}
	
	
	
	
	
	public void resetVariables() {
		pieChart.getData().clear();
		pieChartData.clear();
		map.clear();
		total = 0.0;
		timing = 0.0;
		firstDate = "";
		secondDate = "";
		flag = false;
		siteChecked =0;
	}

	public void GraphGenerate(Date PickerFrom, Date PickerTo, ArrayList<String> SitesList) {
		//System.out.println(site);
		for(int x=0 ; x< SitesList.size(); x++)
		{
		System.out.println(SitesList.get(x));
		flag= false;
		timing = 0.0;
		try {
			Object obj = parser.parse(new FileReader("./tickets/" + SitesList.get(x) + ".json"));
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
							
							// change format to date from json
							checkDate = new SimpleDateFormat("yyyy-MM-dd")
									.parse(((String) history.get("created")).split("T", 2)[0]); 
							// check dates range
							if ((checkDate.after(PickerFrom) || checkDate.equals(PickerFrom))
									&& (checkDate.before(PickerTo) || checkDate.equals(PickerTo))) 
							{
								from = (String) item.get("fromString");
								
								if (!flag) { // for the first date
									firstDate = (String) history.get("created");
									flag = true;
									map.put(from, calculateDateDifferance(firstDate, df.format(PickerFrom) ,true));
									
								} else {
									secondDate = (String) history.get("created");
									
									// System.out.println(from);
									if (map.containsKey(from)) {
										timing = map.get(from);
										map.put(from, timing + calculateDateDifferance(secondDate, firstDate,false));
									} else {
										map.put(from, calculateDateDifferance(secondDate, firstDate,false));
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
		System.out.println(SitesList.get(x)+"-"+ map.get("Not Active-Site Failure"));
		}

	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public void loadPieChart() {
		
		System.out.println("Loading charts...");
		total = 0.0;
		String key2;
		for (String key : map.keySet()) {
			// add data to pie chart
			//System.out.print(key);
			switch (key)
			{
			
			case "Not Active":
				key2 = "Out of client hours";
				break;
			case "Active":
				key2 = "System available";
				break;
			default:
				key2=key;
				break;
			}
			
			pieChartData.add(new PieChart.Data(key2, round(map.get(key)/siteChecked, 2)  )); 
			total = round(total,5)+ (round(map.get(key)/siteChecked, 5));
			System.out.println(key2 + " - " + map.get(key)/siteChecked);
			System.out.println(total);

		}

		// set pieChart data and settings
		pieChart.setLegendSide(Side.TOP);
		pieChart.setData(pieChartData);
		//pieChart.setTitle("Site Activity");
		pieChartData.forEach(data -> data.nameProperty()
				.bind(Bindings.concat(data.getName(), " for ", data.pieValueProperty(), " Hours","(",round(((data.getPieValue() * 100) / total), 2) ,"%)")));

		// set mouse over functions
		pieChart.getData().stream().forEach(data -> {
		data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (event) -> {
	        //System.out.println("DSFSFGSFSGSGSDG" +data.getName());
	        data.getNode().setEffect(new Glow());
	        
	        data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
		       //System.out.println("DSFSFGSFSGSGSDG" +data.getName());
		        data.getNode().setEffect(null);
	        
			//ddDrillDown();	    
			        
	    });
		});
		});
		
		
			
	}
	

	public void selectAll(){
		Haifa.setSelected(true);
		Intel.setSelected(true);
		S32Mine.setSelected(true);
		S32Ref.setSelected(true);
		AreaC.setSelected(true);
		SanManuel.setSelected(true);
		Minera.setSelected(true);
		ValeNc1.setSelected(true);
		ValeNc2.setSelected(true);
		
	}
	public void unSelectAll(){
		Haifa.setSelected(false);
		Intel.setSelected(false);
		S32Mine.setSelected(false);
		S32Ref.setSelected(false);
		AreaC.setSelected(false);
		SanManuel.setSelected(false);
		Minera.setSelected(false);
		ValeNc1.setSelected(false);
		ValeNc2.setSelected(false);
	}
	public void clearList(){
		listView.getItems().clear();
	}
	
	
	public static Double calculateDateDifferance(String Date1, String Date2 , boolean PickerDateFormmated) throws ParseException {
		Date date1 = new Date();
		Date date2 = new Date();
		Double result = 0.0;
		
		String finalDate2 = Date2;
		if(!PickerDateFormmated)
		{
		
		String[] DateTime2 = Date2.split("T");
		DateTime2[1] = DateTime2[1].split("\\.", 2)[0];
		finalDate2 = DateTime2[0] + " " + DateTime2[1];
		}
		
		String finalDate1 = "";
		String[] DateTime1 = Date1.split("T");
		DateTime1[1] = DateTime1[1].split("\\.", 2)[0];
		finalDate1 = DateTime1[0] + " " + DateTime1[1];
		// System.out.println(Date1 + "," + Date2);
		
		date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(finalDate1);
		date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(finalDate2);
		// SimpleDateFormat.parse(String);

		// System.out.println(date1 + "-" + date2);
		Double dif = (double) (date1.getTime() - date2.getTime());
		dif = dif / 1000;

		Double days = dif / (24 * 60 * 60);
	
		result += (24 * days);


		return result;

	}

	public void updateProgrss(Double add) {
		System.out.println(add);

		Double current = progress.getProgress();
		System.out.println(current);
		progress.setProgress(current + (add / 100));

	}

	public void updateData() throws IOException { // download data from jira
		System.out.println("downloading...");
		progress.setVisible(true);
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				
				// TODO Auto-generated method stub
				progress.setProgress(0.0);
				double percent = 100 / Sites.size();

				for (String site : Sites) {
					
					String url = "https://jira.airobotics.co.il:8443/rest/api/2/issue/UCC-" + site + "?expand=changelog";
					
					URL obj = null;
					try {
						obj = new URL(url);
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						showError();
						break;
						
					}
					String encoding = Base64.getEncoder().encodeToString(("Commandcenter:123456789").getBytes());
					HttpURLConnection con = null;
					try {
						con = (HttpURLConnection) obj.openConnection();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						showError();
						break;

					}
					// optional default is GET
					try {
						con.setRequestMethod("GET");
					} catch (ProtocolException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						showError();
						break;

					}
					con.setRequestProperty("Authorization", "Basic " + encoding);
					// add request header
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = 0;
					try {
						responseCode = con.getResponseCode();
						if(responseCode ==200)
						{
							if (site == Sites.get(Sites.size() - 1)) // if last site,
								// progress finished
							{
								updateProgrss(1.0);
							}

							updateProgrss(percent);
						}
						else
						{
							// finish the progress due to error 
							showError();
							break;
			
							
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						// finish the progress due to error 
						showError();
						break;

					}
					System.out.println("\nSending 'GET' request to URL : " + url);
					System.out.println("Response Code : " + responseCode);
					BufferedReader in = null;
					try {
						in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						// finish the progress due to error 
						showError();
						break;

					}
					String inputLine;
					StringBuffer response = new StringBuffer();
					try {
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						// finish the progress due to error 
						showError();
						break;

					}
					try {
						in.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						// finish the progress due to error 
						showError();
						break;

					}
					// print in String
					
					System.out.println(response.toString());
					
					// Read JSON response and print
					// JSONObject myResponse = new
					// JSONObject(response.toString());

					try {
						FileOutputStream fos = new FileOutputStream("./tickets/" + site + ".json");
						fos.write(response.toString().getBytes());
						fos.close();
						// Log.d(TAG, "Written to file");
					} catch (Exception e) {

						e.printStackTrace();
						// finish the progress due to error 
						showError();
						break;

					}

				}
			
			}

		});
		th.start();

		
	}
	
	public void showError(){
		
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
				//init error message
		    	Alert alert = new Alert(AlertType.ERROR);
		    	alert.setTitle("Error");
				alert.setHeaderText("Couldn't download new data");
				alert.setContentText("Please try again");
		    	stage= (Stage) alert.getDialogPane().getScene().getWindow();
				stage.setAlwaysOnTop(true);
				stage.toFront();
		    	stage.show();
		    }
		});
	}
}