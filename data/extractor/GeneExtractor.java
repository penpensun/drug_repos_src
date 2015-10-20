/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data.extractor;

import data.io.DataReader;
import data.io.DataWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.jdom2.Element;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.*;
import java.util.*;

/**
 * This class contains the extractors for gene names (ligands).
 * @author Peng Sun
 */
public class GeneExtractor {
    /**
     * This method extracts the connected gene names and the drug-gene name relations 
     * and output them into the given files.
     * The returned gene names will be:
     * (1) Filtered off non-fasta genes.
     * (2) Filtered off non-drug associated genes
     * (3) Filtered off "HIV-1 protease" 
     * (4) Change AAC(6')-IY to AAC6IY, AAC(6')-II to AAC6II and BCR/ABL FUSION to BCR_ABL_FUSION
     * 
     * Pre-cond: drug ids and drug bank xml
     * Post-cond: Gene ids are written in the given file and drug-gene associations are written.
     * @param drugIdFile
     * @param drugbankXml
     * @param geneOutput
     * @param associationOutput 
     */
    public void extractGene(String drugIdFile, String drugbankXml ,String geneOutput, 
            String associationOutput){
        /* Read the drug list. */
        DrugExtractor drugEx = new DrugExtractor();
        DataReader reader = new DataReader();
        ArrayList<String> drugList = reader.readIds(drugIdFile);
        List<Element> drugElementList = drugEx.extractDrugList(drugbankXml);
        HashSet<String> geneSet = new HashSet<>();
        HashMap<String, ArrayList<String>> drugGenenameMap = new HashMap<>();
        int i=0;
        for(Element drugElement: drugElementList){
            i++;
            if(i%5000==0)
                System.out.println(i/(float)drugElementList.size()*100+"% is finished.");
            /* Extract all gene names. */
            String drugId = drugEx.extractPrimaryId(drugElement);
            if(!drugList.contains(drugId))
                continue;
            
            ArrayList<Element> polypeptideElementList = extractPolypepElementInDrug(drugElement);
            if(polypeptideElementList == null || polypeptideElementList.isEmpty())
                continue;
            ArrayList<String> genesInDrug= new ArrayList<>();
            for(Element polypeptide: polypeptideElementList){
                String fasta = extractFastaSeq(polypeptide);
                if(fasta == null || fasta.isEmpty())
                    continue;
                else{
                    String gene= extractGeneName(polypeptide);
                    if(gene == null || gene.isEmpty())
                        continue;
                    
                    // Remove HIV-1 protease
                    if(gene.equals("HIV-1 protease"))
                        continue;
                    
                    // Change AAC(6')-IY, AAC(6')-II and BCR/ABL Fusion
                    if(gene.equalsIgnoreCase("BCR/ABL FUSION"))
                        gene = "BCR_ABL_FUSION";
                    if(gene.equalsIgnoreCase("aac(6')-Iy"))
                        gene = "AAC6IY";
                    if(gene.equalsIgnoreCase("aac(6')-Ii"))
                        gene = "AAC6II";
                    
                    // Turn the gene name into upper case.
                    gene = gene.toUpperCase();
                    geneSet.add(gene);
                    genesInDrug.add(gene);
                }
            }
            drugGenenameMap.put(drugId, genesInDrug);
        }
        /* Output the geneNames set and the hashmap. */
        new DataWriter().writeHashSet(geneSet, geneOutput, "\n");
        new DataWriter().writeHashMap(drugGenenameMap, associationOutput);
    }

    
    /**
     * This method extracts the amino sequence in fasta format for all gene in the given file.
     * Pre-cond:
     * Post-cond:
     * @param geneFile
     * @param bankXml 
     * @param outputFolder 
     */
    public void extractGeneSeq(String geneFile, String bankXml, String outputFolder){
        ArrayList<String> geneNameList =null;
        DataReader reader = new DataReader();
        geneNameList = reader.readIds(geneFile);
        
        List<Element> drugElementList = new DrugExtractor().extractDrugList(bankXml);
        for(Element drugElement: drugElementList){
            //Extract all polypeptide element list.
            List<Element> polypeptideElementList = extractPolypepElementInDrug(drugElement);
            if(polypeptideElementList == null)
                continue;
            //For each polypeptide element, we extract the amino acid sequence.
            for(Element polypeptideElement: polypeptideElementList){
                List<Element> geneChildren = polypeptideElement.getChildren("gene-name",polypeptideElement.getNamespace());
                if(geneChildren == null || geneChildren.isEmpty()){
                    System.err.println("(GeneNameExtractor.extractGeneNameSeq) polypeptide without gene-name.");
                    return;
                }
                if(geneChildren.size() >1){
                    System.err.println("(GeneNameExtractor.extractGeneNameSeq) More than one gene-name: "+polypeptideElement);
                    return;
                }
                Element geneElement = geneChildren.get(0);
                if(geneElement.getContent().isEmpty()){
                    //System.err.println("(GeneNameExtractor.extractGeneNameSeq) No content for the gene name element.");
                }
                else{
                    String gene = geneElement.getContent(0).getValue().trim().toUpperCase();
                    if(gene.equalsIgnoreCase("AAC(6')-IY"))
                        gene = "AAC6IY";
                    if(gene.equalsIgnoreCase("AAC(6')-II"))
                        gene = "AAC6II";
                    if(gene.equalsIgnoreCase("BCR/ABL FUSION"))
                        gene = "BCR_ABL_FUSION";
                    
                    if(!geneNameList.contains(gene))
                        continue;
                    if(gene.equals("\"")) // Do not add a quote into the gene name list.
                        continue;
                    else {
                        String fastaSeq = extractFastaSeq(polypeptideElement);
                        if(fastaSeq == null){
                            System.err.println("Gene name without fasta:  "+gene);
                            continue;
                        }
                       
                        String filePath = new StringBuilder(outputFolder).append(gene).append(".fasta").toString();
                        new DataWriter().writeGeneFasta(filePath, gene, fastaSeq);
                    }
                }
                
            }
        }
        
    }
    
    /**
     * This method extracts the polypeptide elements in a given drug.
     * Pre-cond:
     * Post-cond:
     * @param drugElement
     * @return 
     */
    public ArrayList<Element> extractPolypepElementInDrug(Element drugElement){
        List<Element> ligandElementList = extractLigand(drugElement);
        if(ligandElementList == null)
            return null;
        ArrayList<Element> polypeptideElementList = new ArrayList<>();
        for(Element ligandElement: ligandElementList){
            ArrayList<Element> list = extractPolypepElementInLigand(ligandElement);
            if(list == null){}
            else polypeptideElementList.addAll(list);
        }
        return polypeptideElementList;
    }
    
    /**
     * This method extracts the polypeptide element in the given ligand element.
     * Pre-cond:
     * Post-cond:
     * @param ligandElement
     * @return 
     */
    private ArrayList<Element> extractPolypepElementInLigand(Element ligandElement){
        ArrayList<Element> polypeptideList = new ArrayList<>();
        if(ligandElement == null)
            throw new IllegalArgumentException("(GeneNameExtractor.extractPolypepElementInLigand) The given ligand element is null.");
        List<Element> polypepChildren = ligandElement.getChildren("polypeptide",ligandElement.getNamespace());
        if(polypepChildren == null || polypepChildren.isEmpty())
            return null;
        GeneExtractor geneNameEx = new GeneExtractor();
        /* For all polypeptide element. */
        for(Element polypeptideElement: polypepChildren){
            List<Element> geneNameChildren = polypeptideElement.getChildren("gene-name",ligandElement.getNamespace());
            if(geneNameChildren == null || geneNameChildren.isEmpty()){
                System.err.println("(InfoExtractor.extractPolypepElementInLigand) polypeptide without gene-name.");
                return null;
            }
            if(geneNameChildren.size() >1)
                System.err.println("(InfoExtractor.extractPolypepElementInLigand) More than one gene-name: "+geneNameEx.extractLigandId(ligandElement));
            polypeptideList.add(polypeptideElement);
                
        }
        return polypeptideList;
    }
    
 
    
    
    
    /**
     * This method extracts the sequence of the given polypeptide element.
     * @param polypeptideElement
     * @return 
     */
    public String extractFastaSeq(Element polypeptideElement){
        if(polypeptideElement == null)
            throw new IllegalArgumentException("(GeneNameExtractor.extractFastaSeq) The given polypeptide element is null.");
        Element aminoAcidSeq = polypeptideElement.getChild("amino-acid-sequence",polypeptideElement.getNamespace());
        if(aminoAcidSeq == null)
            return null;
        /* Since it's possible that we have such case: <amino-acid-sequence format="fasts" />, we have to 
        check the length of the getContent().*/
        if(aminoAcidSeq.getContent().isEmpty()) 
            return null;
        String fastaSeq = aminoAcidSeq.getContent(0).getValue().trim();
        if(fastaSeq == null || fastaSeq.isEmpty())
            return null;
        return fastaSeq;
    }
    
    
    
    /**
     * This method extracts the gene name given a polypeptide element.
     * @param polypeptide
     * @return 
     */
    public String extractGeneName(Element polypeptide){
        
        List<Element> geneNameChildren = polypeptide.getChildren("gene-name",polypeptide.getNamespace());
            if(geneNameChildren == null || geneNameChildren.isEmpty()){
                System.err.println("(InfoExtractor.extractGenename) polypeptide without gene-name.");
                return null;
            }
            if(geneNameChildren.size() >1){
                System.err.println("(InfoExtractor.extractGenename) More than one gene-name: "+polypeptide.toString());
            }
              
            Element geneNameElement = geneNameChildren.get(0);
            if(geneNameElement.getContent().isEmpty())
                    return null;
            else{
                String geneName = geneNameElement.getContent(0).getValue();
                if(geneName.trim().equals("\"")) // Do not add a quote into the gene name list.
                        return null;
                else 
                    return geneName;
            }
    }
    
    
    
    /**
     * With given ligandType, this method extracts the list of element with fasta 
     * sequence of all types of ligands (including targets, enzymes,carriers and transporters). 
     * @param drug
     * @return 
     */
    @Deprecated
    private List<Element> extractLigand(Element drug){
        
        if(drug == null)
            throw new IllegalArgumentException("(extractLigandWithFasta_drug) Drug element cannot be null.");
        List<Element> ligandElementList = new ArrayList<>();
        
        List<Element> targetElementList = this.extractLigand(drug,"target");/* Extract the target list. */
        List<Element> enzymeElementList = this.extractLigand(drug,"enzyme");/* Extract the enzyme list. */
        List<Element> carrierElementList = this.extractLigand(drug,"carrier");/* Extract the carrier list. */
        List<Element> transporterElementList = this.extractLigand(drug,"transporter"); /* Extract the transporter list. */
        
        if(targetElementList != null && !targetElementList.isEmpty())
            ligandElementList.addAll(targetElementList);
        if(enzymeElementList != null && !enzymeElementList.isEmpty())
            ligandElementList.addAll(enzymeElementList);
        if(carrierElementList!= null && !carrierElementList.isEmpty())
            ligandElementList.addAll(carrierElementList);
        if(transporterElementList!= null && !transporterElementList.isEmpty())
            ligandElementList.addAll(transporterElementList);
        
        if(!ligandElementList.isEmpty())
            return ligandElementList;
        return null;
    }
    
    /**
     * This method extracts the ligandId given a ligandElement.
     * @param ligandElement
     * @return 
     */
    @Deprecated
    private String extractLigandId(Element ligandElement){
        Element ligandId = ligandElement.getChild("id",ligandElement.getNamespace());
        if(ligandId == null){
            System.out.println("(extractLigandIds) There exists ligand without id:  "+ligandElement.toString());
            return null;
        }
        String id = ligandId.getContent(0).getValue();
        if(id == null){
            System.out.println("(extractLigandIds) There exists ligand without id:  "+ligandElement.toString());
            return null;
        }
        return id;
    }
    
    
    
    
    /**
     * This method extracts the ligand id from the given gene name. 
     * @param geneName 
     * @param relationFile 
     * @return
     */
    @Deprecated
    public String extractLigandIdFromGeneName(String geneName, String relationFile){
        try{
        FileReader fr= new FileReader(relationFile);
        BufferedReader br =new BufferedReader(fr);
        String line = null;
        while((line = br.readLine())!= null){
            line = line.trim();
            String[] splits = line.split("\t");
            String ligandId = String.copyValueOf(splits[0].toCharArray()).trim();
            // Try to find the gene name. 
            for(int i=1;i<splits.length;i++){
                String geneNameInLine = String.copyValueOf(splits[i].toCharArray()).trim();
                if(geneName.equals(geneNameInLine)){
                    return ligandId;
                }       
            }
        }
        br.close();
        fr.close();
        }catch(IOException e){
            System.err.println("(InfoExtractor.extractLigandIdFromGeneName) ");
        }
        return null;
    }
   
    public void runExtractGeneDiseaseMatrix(float posEw, float negEw){
        String geneFile = "../../gene/gene_id.txt";
        String diseaseFile = "../../disease/cui.txt";
        String assocFile = "../../assoc/gene_cui_assoc.txt";
        String outputFile = "../../matrix/gene_cui_matrix.txt";
        new MatrixExtractor().extractGeneDiseaseMatrix(geneFile, diseaseFile, assocFile, outputFile,posEw, negEw);
    }
  
    /**
     * This method extracts the genes.
     */
    public void runExtractGene(){
        String drugIdFile = "../../drug/drug_id.txt";
        String drugbankXml = "../../drug/drugbank.xml";
        String geneOutput = "../../gene/gene_id.txt";
        String drugGeneAssocOutput = "../../drug/drug_gene_association.txt";
        extractGene(drugIdFile, drugbankXml, geneOutput, drugGeneAssocOutput);
    }
    
    
    /**
     * This method extracts the fasta sequences.
     */
    public void runExtractFasta(){
        String geneFile = "../../gene/gene_id.txt";
        String outputFolder = "../../gene/seq/";
        String bankXml = "../../drug/drugbank.xml";
        extractGeneSeq(geneFile, bankXml,outputFolder);
    }
    
    public static void main(String args[]){
        //new GeneExtractor().runExtractGene();
        //new GeneExtractor().runExtractGeneNameFasta();
        
        //new GeneExtractor().check();
        //new GeneNameExtractor().runExtractGeneNameDiseaseAssociation();
        new GeneExtractor().runExtractGeneDiseaseMatrix(1, 0);
        //new GeneExtractor().test();
        //new GeneExtractor().checkMatrixSymmetry();
        //new GeneExtractor().testMatrix();
    }
    
    
    
    /**
     * This method extracts the list of elements of different "ligands" (including targets,
     * enzymes, carriers and transporters) with fastsa, given the drug element and the ligand type.
     * @param drug
     * @param ligandType
     * @return 
     */
    @Deprecated
    private List<Element> extractLigand(Element drug, String ligandType){
        if(drug == null)
            throw new IllegalArgumentException("(extractLigandWithFasta_drug_ligandType) Drug element cannot be null.");
        if(ligandType == null)
            throw new IllegalArgumentException("(InfoExtractor.extractLigandWithFasta) Must give a ligand type. ");
        /* Check if the ligandType is legal. */
        if(!ligandType.equalsIgnoreCase("target") && !ligandType.equalsIgnoreCase("enzyme")
                && !ligandType.equalsIgnoreCase("carrier") && !ligandType.equalsIgnoreCase("transporter"))
            throw new IllegalArgumentException("(dataprocessing.TargetsInforamtionExtractor.countLigandWithFasta) "+
                    "Illegal ligand type:  "+ligandType);
        String ligandsName = null;
        if(ligandType.equalsIgnoreCase("target"))
            ligandsName = "target";
        else if(ligandType.equalsIgnoreCase("enzyme"))
            ligandsName= "enzyme";
        else if(ligandType.equalsIgnoreCase("transporter"))
            ligandsName= "transporter";
        else if(ligandType.equalsIgnoreCase("carrier"))
            ligandsName= "carrier";
        
        /* Get the ligand element from the given drug element. */
        Element ligandsElement = null;
        /* Here we have to user "ligandName"+s. */
        ligandsElement = 
                drug.getChild(new StringBuilder(ligandsName).append("s").toString(),drug.getNamespace());
        /* Create the arraylist of ligand to return. */
        ArrayList<Element> ligands = new ArrayList<>();
        
        if(ligandsElement == null)
            return null;
        List<Element> ligandElementList = 
                ligandsElement.getChildren(ligandsName,drug.getNamespace());
        if(ligandElementList == null)
            throw new IllegalStateException("(InfoExtractor.extractLigandWithFasta) Element \"ligands\" must have \"ligand\" element.");
        
        if(ligandElementList.isEmpty())
            return null;
        else return ligandElementList;
    }
    
}
