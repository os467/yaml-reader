package com.os467;

public class SingletonFactory {

    private static ClassScanner classScanner = new ClassScanner();

    //First Scan produce all @Singleton object's half product

    //half product register a list of the Objects that it needs but haven't create and put it to the map
    //Product -> List<half Product> event in the map

    //if create the next Product ,we need check the list,give this product's instance reference to the needed Product on the list
    //remove the Product -> List<half Product> event in the map




}
