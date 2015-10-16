/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processor;
import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.*;

/**
 * This class contains several methods acting like filters, to remove data unsatisfying certain
 * conditions, e.g. to remove redundant data. 
 * @author penpen926
 */
public class DataFilter {
    /**
     * This method removes the redundancy in the arrayList.
     * @param toFilter
     * @return 
     */
    public ArrayList<String> redunFilter(List<String> toFilter){
        HashSet<String> nonRedunSet = new HashSet<>();
        toFilter.stream().forEach((id) -> {
            nonRedunSet.add(id);
        });
        ArrayList<String> nonRedunList = new ArrayList<>(nonRedunSet);
        return nonRedunList;
    }
    
    /**
     * This method returns if the drug is in the filter.
     * @param drug
     * @param filterList
     * @return 
     */
    public boolean drugIdFilter(String drug, List<String> filterList){
        drug = drug.trim();
        return filterList.contains(drug);
    }
    
}
