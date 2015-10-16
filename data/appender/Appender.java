/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.appender;
import data.io.*;
import data.init.*;
import data.extractor.*;
import java.io.*;
/**
 * This class appends drugs and diseases to the original drugs and diseases.
 * @author penpen926
 */
public class Appender {
    public void appendDrugs(String appendDrugNames, DrugReposConfig conf){
        DrugExtractor drugEx = new DrugExtractor();
        //Extract the drug ids and the smiles of the new drugs.
        drugEx.extractSmilesById(conf, conf.drug_id, conf.drug_smiles);
        drugEx.extractSmilesByName(conf,appendDrugNames, conf.compare2_append_smiles);
        combines(conf.drug_smiles,conf.compare2_append_smiles, conf.compare2_new_smiles);
        //Create new drug-gene association.
        
        //Create new drug-disease association.
        
    }
    
    public void createNewDrugMatrix(DrugReposConfig conf){
        
    }
    
    
    public void appendDisease(String)
    
    public void combines(String file1, String file2, String output){
        try{
            FileReader fr = new FileReader(file1);
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter(output);
            BufferedWriter bw = new BufferedWriter(fw);
            String line = null;
            while((line = br.readLine())!= null){
                bw.write(line+"\n");
            }
            br.close();
            fr.close();
            fr = new FileReader(file2);
            br = new BufferedReader(fr);
            while((line = br.readLine())!= null){
                bw.write(line+"\n");
            }
            
            br.close();
            fr.close();
            bw.flush();
            bw.close();
            fw.close();
        }catch(IOException e){
            System.err.println();
        }
    }
    
}
