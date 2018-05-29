package Package;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

public class History {
	
	LocalDate DatePickerTo;
	LocalDate DatePickerFrom;
	ArrayList<String> siteList;
	
	
	public History(LocalDate datePickerTo, LocalDate datePickerFrom, ArrayList<String> siteList) {
		super();
		DatePickerTo = datePickerTo;
		DatePickerFrom = datePickerFrom;
		this.siteList = siteList;
				
	}
	
	
	
	
}
