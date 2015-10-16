/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.lucene;
import java.util.*;
import java.io.*;
/**
 * This class is a structure of Medline, containing all information needed for the indexing.
 * 
 * @author penpen926
 */
public class Medline implements Serializable{
    public String pmid;
    public String title;
    public String abstractText;
    public ArrayList<String> meshHeadings;
    public ArrayList<String> keywords;
    public Medline(String pmid, String title, String abstractText, ArrayList<String> meshHeadings, 
            ArrayList<String> keywords){
        this.pmid= pmid;
        this.title = title;
        this.abstractText = abstractText;
        this.meshHeadings = meshHeadings;
        this.keywords = keywords;
    }
    public Medline(){
        pmid = null;
        title= null;
        abstractText = null;
        meshHeadings = null;
        keywords = null;
    }
    
    
}
