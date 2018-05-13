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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

	Double total = 0.0;
	String from;
	Double dateDifferance;
	String to;
	String firstDate = "";
	String secondDate = "";
	boolean flag = false;
	Double timing = 0.0;
	Date checkDate;
	ArrayList<String> Sites = new ArrayList<String>();
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		// progress.setVisible(false);

	}

	public void searchButton() {

		String Site = "";
		resetVariables();
		switch (choiceBox.getSelectionModel().getSelectedItem().toString()) {
		case "Haifa bay port":
			Site = "705";
			break;
		case "Intel - FAB 28":
			Site = "704";
			break;
		case "S32 Mine":
			Site = "706";
			break;
		case "S32 Refinery":
			Site = "708";
			break;
		case "BHP Area C":
			Site = "707";
			break;
		case "BHP San Manuel":
			Site = "723";
			break;
		case "Minera Centinela":
			Site = "737";
			break;
		case "Vale NC1":
			Site = "715";
			break;
		case "Vale NC2":
			Site = "714";
			break;

		}

		// System.out.println(DatePickerFrom.getValue());
		GraphGenerate(Date.from(DatePickerFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
				Date.from(DatePickerTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()), Site);

		loadPieChart();
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
	}

	public void GraphGenerate(Date PickerFrom, Date PickerTo, String site) {
		System.out.println(site);
		timing = 0.0;
		try {
			Object obj = parser.parse(new FileReader("./tickets/" + site + ".json"));
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
								
								if (!flag) {
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
		for (String key : map.keySet()) {
			// add data to pie chart
			pieChartData.add(new PieChart.Data(key, round(map.get(key), 2))); 
			total = total + round(map.get(key), 5);
			System.out.println(key + " - " + map.get(key));

		}

		// set pieChart data and settings
		pieChart.setLegendSide(Side.LEFT);
		pieChart.setData(pieChartData);
		pieChart.setTitle("Site Activity");
		pieChartData.forEach(data -> data.nameProperty()
				.bind(Bindings.concat(data.getName(), " for ", data.pieValueProperty(), " Hours")));

		// set mouse over functions
		pieChart.getData().stream().forEach(data -> {
			data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {

				label.setMouseTransparent(true);
				label.setText(round(((data.getPieValue() * 100) / total), 2) + "%");
				label.setTranslateX(e.getSceneX());
				label.setTranslateY(e.getSceneY() + 20);
				label.setVisible(true);
			});

			data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, (x) -> {
				label.setVisible(false);
				
			});

			data.getNode().addEventHandler(MouseEvent.MOUSE_MOVED, (y) -> {
				{
					// Keep Label near the mouse
					label.setTranslateX(y.getSceneX());
					label.setTranslateY(y.getSceneY() + 20);
				}
			});

		});

		;

		// borderPane.getChildren().add(pieChart);
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
					if (site == Sites.get(Sites.size() - 1)) // if last site,
																// progress
																// finished
					{
						updateProgrss(1.0);
					}

					updateProgrss(percent);
					String url = "https://jira.airobotics.co.il:8443/rest/api/2/issue/UCC-" + site
							+ "?expand=changelog";
					URL obj = null;
					try {
						obj = new URL(url);
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String encoding = Base64.getEncoder().encodeToString(("Commandcenter:123456789").getBytes());
					HttpURLConnection con = null;
					try {
						con = (HttpURLConnection) obj.openConnection();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// optional default is GET
					try {
						con.setRequestMethod("GET");
					} catch (ProtocolException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					con.setRequestProperty("Authorization", "Basic " + encoding);
					// add request header
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = 0;
					try {
						responseCode = con.getResponseCode();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println("\nSending 'GET' request to URL : " + url);
					System.out.println("Response Code : " + responseCode);
					BufferedReader in = null;
					try {
						in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
					}
					try {
						in.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
					}

				}
			}

		});
		th.start();

	}
}