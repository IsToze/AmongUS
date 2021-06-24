package fr.Toze.amongus.utils;

import java.util.ArrayList;

public class ArgsValue {

	private ArrayList<Object> valuesList;
	
	public ArgsValue() {
		this.valuesList = new ArrayList<>();
	}
	
	public void add(Object object){
		valuesList.add(object);
	}
	
	public Object[] getValues(){
		return valuesList.toArray();
	}
	
}
