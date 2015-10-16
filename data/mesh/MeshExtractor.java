package data.mesh;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author penpen926
 */
import java.io.*;
import java.util.*;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import data.io.*;


public class MeshExtractor {
    
    /**
     * This method extracts the disease synonyms in the mesh file.
     * This is the main method to extract mesh disease syn.
     * @param diseaseMeshAssoc
     * @param diseaseFile
     * @param meshEntryTermMap 
     * @param scEntryTermMap 
     * @param output 
     */
    public void extractSynMap(String diseaseMeshAssoc,
            String diseaseFile,
            String meshEntryTermMap,
            String scEntryTermMap,
            String output){
        DataReader reader = new DataReader();
        HashMap<String, HashSet<String>> meshMap = reader.readMap(meshEntryTermMap);
        HashMap<String, HashSet<String>> scMap = reader.readMap(scEntryTermMap);
        HashMap<String, HashSet<String>> diseaseMeshMap = reader.readMap(diseaseMeshAssoc);
        HashMap<String, HashSet<String>> map = new HashMap<>();
        
        ArrayList<String> diseaseList = reader.readIds2(diseaseFile);
        for(String disease: diseaseList){
            HashSet<String> meshSet = diseaseMeshMap.get(disease);
            HashSet<String> syn= new HashSet<>();
            for(String meshId:meshSet)
                syn.addAll(getEntryTermSet(meshId, meshMap, scMap));
            map.put(disease, syn);
        }
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(map, output);
    }
    
    /**
     * This method 
     * @param id
     * @param meshMap
     * @param scMap
     * @return 
     */
    public HashSet<String> getEntryTermSet(String id, 
            HashMap<String, HashSet<String>> meshMap,
            HashMap<String, HashSet<String>> scMap){
        if(meshMap.containsKey(id))
            return meshMap.get(id);
        else if(scMap.containsKey(id))
            return scMap.get(id);
        else throw new IllegalArgumentException("Wrong id:  "+id);
    }
    
    
    public void extractScMap(String scSetFile,
            String scMeshMapFile,
            String scConceptMapFile,
            String scMeshEntryTermMapFile,
            String output){
        DataReader reader = new DataReader();
        HashSet<String> scSet = new HashSet<>(reader.readIds2(scSetFile));
        HashMap<String, HashSet<String>> scMesh = reader.readMap(scMeshMapFile);
        HashMap<String, HashSet<String>> scConceptMap = reader.readMap(scConceptMapFile);
        HashMap<String, HashSet<String>> scMeshEntryTermMap = reader.readMap(scMeshEntryTermMapFile);
        HashMap<String, HashSet<String>> resMap = new HashMap<>();
        for(String sc: scSet){
            // The concept set mapped to sc.
            HashSet<String> conceptSet = scConceptMap.get(sc);
            // The mesh ids mapped to sc.
            HashSet<String> mappedMeshSet = scMesh.get(sc);
            // The mapped entry terms mapped to the mesh ids mapped to sc.
            HashSet<String> mappedEntryTermSet = new HashSet<>();
            for(String mappedMesh: mappedMeshSet){
                HashSet<String> mappedSet = scMeshEntryTermMap.get(mappedMesh);
                mappedEntryTermSet.addAll(mappedSet);
            }
            // The sum map of concept and entry terms.
            HashSet<String> mappedValue = new HashSet<>();
            mappedValue.addAll(conceptSet);
            mappedValue.addAll(mappedEntryTermSet);
            
            resMap.put(sc, mappedValue);
        }
        DataWriter writer = new DataWriter();
        writer.writeHashMap2(resMap, output);
    }
    
    /**
     * Since supp xml is too large, it is difficult to read the whole 
     * file into SAXBuilder. Thus We try to reduce the size of supp xml.
     * Since only parts of the xml are what we need.
     * The 
     * The parts between <HeadingMappedToList> and </HeadingMappedToList>
     * and the parts between <ConceptList> and </ConceptList>
     * @param suppXml
     * @param output 
     */
    public void reduceSuppXml(String suppXml, String output) throws IOException{
        FileReader fr = new FileReader(suppXml);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter(output);
        BufferedWriter bw = new BufferedWriter(fw);
        String line= null;
        line = br.readLine();
        bw.write(line+"\n");
        line = br.readLine();
        bw.write(line+"\n");
        line = br.readLine();
        bw.write(line+"\n");
        
        boolean suppRecord = false;
        boolean suppUiRecord = false;
        boolean suppRecordName = false;
        boolean mappedToList = false;
        boolean conceptList = false;
        boolean concept = false;
        boolean termList = false;
        while((line = br.readLine())!= null){
            if(line.isEmpty())
                continue;
            if(line.contains("<SupplementalRecord SCRClass")){
                bw.write(line+"\n");
                suppRecord = true;
            }
            if(line.contains("</SupplementalRecord>")){
                bw.write(line+"\n");
                if(!suppRecord)
                    System.out.println("suppRecord not opened but closed:  "+line);
                suppRecord = false;
            }
            
            //Check the supplemental record id.
            if(line.contains("<SupplementalRecordUI")){
                suppUiRecord = true;
                if(line.contains("</SupplementalRecordUI>")){
                    bw.write(line+"\n");
                    if(!suppUiRecord)
                        System.out.println("suppUiRecord not opened but closed:  "+line);
                    suppUiRecord = false;
                }
                else{
                    do{
                        bw.write(line+"\n");
                        line=  br.readLine();
                    }while(!line.contains("</SupplementalRecordUI>"));
                    bw.write(line+"\n");
                    if(!suppUiRecord)
                        System.out.println("suppUiRecord not opended but closed:  "+line);
                    suppUiRecord = false;
                }
            }
            
            //Check the supplemental record name
            if(line.contains("<SupplementalRecordName")){
                suppRecordName = true;
                if(line.contains("</SupplementalRecordName>")){
                    bw.write(line+"\n");
                    if(!suppRecordName)
                        System.out.println("suppRecordName not opened but closed:  "+line);
                    suppRecordName = false;
                }
                else{
                    do{
                        bw.write(line+"\n");
                        line=  br.readLine();
                    }while(!line.contains("</SupplementalRecordName>"));
                    bw.write(line+"\n");
                    if(!suppRecordName)
                        System.out.println("suppRecordName not opened but closed:  "+line);
                    suppRecordName = false;
                }
            }
            //Check the Heading Mapped to List
            if(line.contains("<HeadingMappedToList")){
                mappedToList = true;
                if(line.contains("</HeadingMappedToList>")){
                    bw.write(line+"\n");
                    if(!mappedToList)
                        System.out.println("headingmappedToList not opened but closed:  "+line);
                    mappedToList = false;
                }
                else{
                    do{
                        bw.write(line+"\n");
                        line=  br.readLine();
                    }while(!line.contains("</HeadingMappedToList>"));
                    bw.write(line+"\n");
                    if(!mappedToList)
                        System.out.println("headingMappedToList not opened but closed:  "+line);
                    mappedToList = false;
                }
            }
            //Check the Concept List
            if(line.contains("<ConceptList")){
                bw.write(line+"\n");
                conceptList = true;
            }
            if(line.contains("</ConceptList>")){
                conceptList = false;
                bw.write(line+"\n");
            }
            //Check the Concept element.
            if(line.contains("<Concept ")|| line.contains("<Concept>")){
                bw.write(line+"\n");
                concept = true;
            }
            if(line.contains("</Concept>")){
                concept = false;
                bw.write(line+"\n");
            }
            
            //Check the term list.
            //Check the TermList to List
            if(line.contains("<TermList")){
                termList = true;
                if(line.contains("</TermList>")){
                    bw.write(line+"\n");
                    if(!termList)
                        System.out.println("TermList not opened but closed:  "+line);
                    termList = false;
                }
                else{
                    do{
                        bw.write(line+"\n");
                        line=  br.readLine();
                    }while(!line.contains("</TermList>"));
                    bw.write(line+"\n");
                    if(!termList)
                        System.out.println("TermList not opened but closed:  "+line);
                    termList = false;
                }
            }
            
        }
        
        if(suppRecord)
            System.out.println("suppRecord not closed.");
        if(suppUiRecord)
            System.out.println("suppUiRecord not closed.");
        if(suppRecordName)
            System.out.println("suppRecordName not closed.");
        if(mappedToList)
            System.out.println("mappedList not closed.");
        if(conceptList)
            System.out.println("conceptList not closed.");
        if(concept)
            System.out.println("concept not closed.");
        if(termList)
            System.out.println("termlist not closed.");
        
        //Flush and output the resutls.
        bw.flush();
        bw.close();
        fw.close();
        br.close();
        fr.close();
    }
    /**
     * This method extracts the synonyms for the mesh headings, i.e., the Mesh Headings with term list 
     * that can be found in Mesh Database xml, not the supplemental concept terms.
     * Not tested.
     * @param meshIds
     * @param meshList
     * @param scSet This hashset is modified to store the supplemental concept terms encountered in 
     * the process.
     * @return 
     */
    public HashMap<String,HashSet<String>> extractHeadingSynMap1(HashSet<String> meshIds,
            List<Element> meshList, HashSet<String> scSet){
        HashMap<String, HashSet<String>> map = new HashMap<>();
        for(Element meshElement: meshList){
            String meshId = extractMeshId(meshElement);
            if(!meshIds.contains(meshId))
                continue;
            HashSet<String> syn = extractTermListForMesh(meshElement);
            if(!map.containsKey(meshId))
                map.put(meshId, syn);
            else
                map.get(meshId).addAll(syn);
            
        }
        //Get the scSet
        ArrayList<String> meshSet = new ArrayList<>(map.keySet());
        for(String id:meshIds){
            if(!meshSet.contains(id))
                scSet.add(id);
        }
        return map;
    }
    
    /**
     * This method extracts the 
     * @param scSet
     * @param suppList
     * @param output 
     */
    public void extractScConceptTermMap(HashSet<String> scSet, String suppXml, String output){
        List<Element> suppList = extractSuppElementList(suppXml);
        HashMap<String, HashSet<String>> scSyn1 = extractTermListForSupp(scSet, suppList);
        new DataWriter().writeHashMap2(scSyn1, output);
    }
    
    /**
     * This method extracts all synonyms given a mesh element.
     * @param meshElement
     * @return 
     */
    public HashSet<String> extractTermListForMesh(Element meshElement){
        Element conceptList = meshElement.getChild("ConceptList",meshElement.getNamespace());
        List<Element> conceptElementList = conceptList.getChildren("Concept", conceptList.getNamespace());
        HashSet<String> ans = new HashSet<>();
        for(Element concept: conceptElementList){
            Element termListElement = concept.getChild("TermList", meshElement.getNamespace());
            List<Element> termList = termListElement.getChildren("Term", termListElement.getNamespace());
            for(Element term:termList){
                Element stringElement = term.getChild("String", term.getNamespace());
                String stringContent = stringElement.getContent(0).getValue();
                ans.add(stringContent);
            }
        }
        return ans;
    }
    
    /**
     * This method extracts the term list for the given suppmental concept set.
     * @param scSet
     * @param suppXml
     * @return 
     */
    public HashMap<String, HashSet<String>> extractTermListForSupp(HashSet<String> scSet, 
            List<Element> suppXml){
        HashMap<String, HashSet<String>> map = new HashMap<>();
        for(Element supp: suppXml){
            String suppId = extractSuppId(supp);
            if(!scSet.contains(suppId))
                continue;
            Element conceptListElement = supp.getChild("ConceptList", supp.getNamespace());
            List<Element> conceptList = conceptListElement.getChildren("Concept", supp.getNamespace());
            HashSet<String> termSet = new HashSet<>();
            for(Element concept: conceptList){
                Element termListElement = concept.getChild("TermList", concept.getNamespace());
                List<Element> termList = termListElement.getChildren("Term",termListElement.getNamespace());
                for(Element term: termList){
                    Element stringElement = term.getChild("String",term.getNamespace());
                    String stringContent = stringElement.getContent(0).getValue();
                    termSet.add(stringContent);
                }
            }
            map.put(suppId, termSet);
        }
        return map;
    }
    
    
    /**
     * This method extractst he mesh id from the given mesh element.
     * @param meshElement
     * @return 
     */
    public String extractMeshId(Element meshElement){
        Element descriptorUiElement = meshElement.getChild("DescriptorUI", meshElement.getNamespace());
        if(descriptorUiElement == null)
            throw new IllegalStateException("This element does not contain a descriptor ui element:\t"+meshElement.toString());
        String id = descriptorUiElement.getContent(0).getValue();
        if(id == null)
            throw new IllegalStateException("This element does not contain a non-null descriptor ui element:\t"+meshElement.toString());
        return id;
    }
    
    
    /**
     * This method returns the list of mesh elements inside the 
     * mesh xml.
     * @param meshXml
     * @return 
     */
    public List<Element> extractMeshElementList(String meshXml){
        SAXBuilder builder = new SAXBuilder();
        Document meshDoc = null;
        try {
            meshDoc = builder.build(new File(meshXml));
        } catch (IOException e) {
            System.out.println("(MeshExtractor.extractMeshElementList) Reading file error.");
            return null;
        } catch (JDOMException e) {
            System.out.println("(MeshExtractor.extractMeshElementList) JDOM error");
        }
        if(meshDoc == null)
            throw new IllegalStateException("No element found in the xml.");
        Element root = meshDoc.getRootElement();
        Namespace ns = root.getNamespace();
        if(root == null)
            throw new IllegalStateException("No root elment.");
        return root.getChildren("DescriptorRecord", ns);   
    }
    
    
    /**
     * This method extracts the SupplementalRecord lsit from supp xml.
     * @param suppXml
     * @return 
     */
    public List<Element> extractSuppElementList(String suppXml){
        SAXBuilder builder = new SAXBuilder();
        Document meshDoc = null;
        try {
            meshDoc = builder.build(new File(suppXml));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("(MeshExtractor.extractSuppElementList) Reading file error.");
            return null;
        } catch (JDOMException e) {
            e.printStackTrace();
            System.out.println("(MeshExtractor.extractSuppElementList) JDOM error");
        }
        if(meshDoc == null)
            throw new IllegalStateException("No element found in the xml.");
        Element root = meshDoc.getRootElement();
        Namespace ns = root.getNamespace();
        if(root == null)
            throw new IllegalStateException("No root elment.");
        return root.getChildren("SupplementalRecord", ns); 
    }
    
    /**
     * This method returns the mapping from mesh supplemental concept terms to 
     * mesh headings, outputting the mapping and return the mapping as a HashMap.
     * @param scSet
     * @param suppXml
     * @param outupt
     * @return 
     */
    public HashMap<String, HashSet<String>> extractScMeshMap(HashSet<String> scSet, String suppXml,
            String outupt){
        List<Element> suppElementList = extractSuppElementList(suppXml);
        HashMap<String, HashSet<String>> map = new HashMap<>();
        for(Element suppElement: suppElementList){
            String scId = extractSuppId(suppElement);
            if(!scSet.contains(scId))
                continue;
            HashSet<String> headings = extractMappedHeadingSet(suppElement);
            if(!map.containsKey(scId))
                map.put(scId, headings);
            else
                map.get(scId).addAll(headings);
        }
        new DataWriter().writeHashMap2(map, outupt);
        return map;
    }
    
    /**
     * This method extracts the mapping from supplemental concept terms to mesh ids.
     * @param scSet
     * @param suppElementList
     * @param output
     * @return 
     */
    public HashMap<String, HashSet<String>> extractScMeshMap(HashSet<String> scSet, List<Element> suppElementList,
            String output){
        HashMap<String, HashSet<String>> map = new HashMap<>();
        for(Element suppElement: suppElementList){
            String scId = extractSuppId(suppElement);
            if(!scSet.contains(scId))
                continue;
            HashSet<String> headings = extractMappedHeadingSet(suppElement);
            if(!map.containsKey(scId))
                map.put(scId, headings);
            else
                map.get(scId).addAll(headings);
        }
        new DataWriter().writeHashMap2(map, output);
        return map;
    }
    /**
     * This method extracts the id of a supplemental record.
     * @param suppElement
     * @return 
     */
    public String extractSuppId(Element suppElement){
        Element uiElement = suppElement.getChild("SupplementalRecordUI",suppElement.getNamespace());
        if(uiElement == null)
            throw new IllegalStateException("No supplementalRecordUI:  "+suppElement.toString());
        String ui = uiElement.getContent(0).getValue();
        return ui;
    }
    
    /**
     * This method extracts the heading list mapped to the given supplemental element.
     * @param suppElement
     * @return 
     */
    public HashSet<String> extractMappedHeadingSet(Element suppElement){
        List<Element> mappedHeadingList = 
                suppElement.getChild("HeadingMappedToList",suppElement.getNamespace())
                .getChildren("HeadingMappedTo",suppElement.getNamespace());
        HashSet<String> ans = new HashSet<>();
        for(Element mappedHeading: mappedHeadingList){
            Element descriptorReferredTo = mappedHeading.getChild("DescriptorReferredTo", mappedHeading.getNamespace());
            if(descriptorReferredTo == null)
                continue;
            Element descriptorUI = 
                    descriptorReferredTo.getChild("DescriptorUI",descriptorReferredTo.getNamespace());
            if(descriptorUI == null)
                continue;
            String uiString = descriptorUI.getContent(0).getValue();
            if(uiString.startsWith("*"))
                uiString =uiString.substring(1);
            ans.add(uiString);
        }
        return ans;
    }
    
    /**
     * This method extracts the disease id -- mesh id mapping and outputs the mapping
     * into the given outputfile.
     * @param diseaseIdFile
     * @param meshXml
     * @param suppXml
     * @param outputFile 
     */
    void extractMeshId(String diseaseIdFile, String ctdFile, String outputFile){
        // Check if there is any disease without a mesh id.
        ArrayList<String> diseaseIds = new DataReader().readIds2(diseaseIdFile);
        HashMap<String, HashSet<String>> map = new HashMap<>();
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(ctdFile);
            br = new BufferedReader(fr);
        }catch(IOException e){
            System.err.println("File reading error.");
            e.printStackTrace();
        }
        
        try{
            String line= null;
            while((line = br.readLine())!= null){
                line = line.trim();
                if(line.isEmpty())
                    continue;
                if(line.startsWith("#"))
                    continue;
                String[] splits= line.split("\t");
                String diseaseName = String.copyValueOf(splits[0].toCharArray());
                if(!diseaseIds.contains(diseaseName))
                    continue;
                
                String meshId = String.copyValueOf(splits[1].toCharArray());
                if(!meshId.startsWith("MESH")){
                    ArrayList<String> mappingExtracted = extractMultiMeshMaping(line);
                    HashSet<String> value = new HashSet<>(mappingExtracted);
                    map.put(diseaseName, value);
                }
                else{
                    meshId = meshId.substring(5);
                    map.put(diseaseName, new HashSet<>());
                    map.get(diseaseName).add(meshId);
                }
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        
        new DataWriter().writeHashMap2(map, outputFile);
    }
    
    
    /**
     * This method extracts the OMIM --- MESH mapping inside the ctd file, 
     * in case on OMIM id is given, then we use the MESH id in the "parents ids".
     * @param line
     * @return 
     */
    ArrayList<String> extractMultiMeshMaping(String line){
        
        String[] splits = line.split("\t");
        if(splits.length <5)
            System.out.println("<5\t"+line);
        String parentIds = splits[4];
        String[] parentIdSplits = parentIds.split("\\|");
        ArrayList<String> mapping = new ArrayList<>();
        for(String parent:parentIdSplits){
            if(parent.startsWith("MESH:"))
                mapping.add(String.copyValueOf(parent.substring(5).toCharArray()));
        }
        if(mapping.isEmpty())
            return null;
        return mapping;
    }
    /**
     * This method extract the syns from the list of mesh element in the mesh xml file and output the 
     * syn mapping.
     * The difference from extractHeadingSynMap2 to extractHeadingSynMap1 is extract2 uses the
     * headings extracted from the sc terms.
     * @param meshFromSc
     * @param meshList
     * @return 
     */
    public HashMap<String,HashSet<String>> extractHeadingSynMap2(HashSet<String> meshFromSc,
            List<Element> meshList){
        HashMap<String, HashSet<String>> map = new HashMap<>();
        for(Element meshElement: meshList){
            String meshId = extractMeshId(meshElement);
            if(!meshFromSc.contains(meshId))
                continue;
            HashSet<String> termSet = extractTermListForMesh(meshElement);
            if(!map.containsKey(meshId))
                map.put(meshId, termSet);
            else
                map.get(meshId).addAll(termSet);
        }
        return map;
    }
    
    
    
    
    /**
     * This method merges two HashMaps
     * @param map1
     * @param map2
     * @return 
     */
    public HashMap<String, HashSet<String>> mergeMap(HashMap<String,HashSet<String>> map1,
            HashMap<String, HashSet<String>> map2){
        ArrayList<String> keySet1 = new ArrayList<>(map1.keySet());
        HashMap<String, HashSet<String>> ans = new HashMap<>();
        for(String key1:keySet1){
            ans.put(key1, map1.get(key1));
            if(map2.containsKey(key1)){
                ans.get(key1).addAll(map2.get(key1));
            }
            else{}
        }
        ArrayList<String> keySet2 = new ArrayList<>(map2.keySet());
        for(String key2: keySet2){
            if(keySet1.contains(key2))
                continue;
            ans.put(key2, map2.get(key2));
        }
        return ans;
    }
    
    /**
     * This method reduces the size of supp xml 
     */
    public void runReduceSuppXml(){
        String suppxml = "../../mesh/supp2015.xml";
        String reducedSuppXml= "../../mesh/reduced_supp2015.xml";
        try{
            reduceSuppXml(suppxml, reducedSuppXml);
        }catch(IOException e){e.printStackTrace();}
    }
    /**
     * This methdo runs the extraction from disease names to mesh id.
     */
    public void runExtractDiseaseMeshMapping(){
        String diseaseIdFile = "../../disease/disease_id.txt";
        String ctdFile = "../../ctd/CTD_diseases.tsv";
        String outputFile = "../../mesh/disease_mesh_assoc.txt";
        extractMeshId(diseaseIdFile, ctdFile, outputFile);
    }
    
    
    public void runExtractMeshMapAndScTerms(){
        String diseaseAssocFile = "../../mesh/disease_mesh_assoc.txt";
        HashMap<String, HashSet<String>> diseaseMeshMap = new DataReader().readMap(diseaseAssocFile);
        HashSet<String> meshIds = new HashSet<>();
        for(HashSet<String> value: diseaseMeshMap.values()){
            meshIds.addAll(value);
        }
        HashSet<String> scTerms = new HashSet<>();
        String meshXml = "../../mesh/desc2015.xml";
        String scOut = "../../mesh/sc.txt";
        String meshMapOut = "../../mesh/mesh_map.txt";
        List<Element> meshElementList = extractMeshElementList(meshXml);
        HashMap<String, HashSet<String>> meshMap = extractHeadingSynMap1(meshIds, meshElementList, scTerms);
        new DataWriter().writeHashSet(scTerms, scOut, "\n");
        new DataWriter().writeHashMap2(meshMap, meshMapOut);
    }
    
    
    public void runExtractorScMap(){
        String scSetFile = "../../mesh/sc.txt";
        HashSet<String> scSet = new HashSet<>(new DataReader().readIds2(scSetFile));
        String suppXml = "../../mesh/reduced_supp2015.xml";
        String scMeshMapOut = "../../mesh/sc_mesh_map.txt";
        HashMap<String, HashSet<String>> scMeshMap = extractScMeshMap(scSet,suppXml, scMeshMapOut); 
    }
    
    public void runExtractorScEntryTermMap(){
        String scSetFile = "../../mesh/sc.txt";
        HashSet<String> scSet = new HashSet<>(new DataReader().readIds2(scSetFile));
        String suppXml = "../../mesh/reduced_supp2015.xml";
        String scEntryTermMapOut = "../../mesh/sc_concept_map.txt";
        List<Element> suppElementList = extractSuppElementList(suppXml);
        HashMap<String, HashSet<String>> scEntryMap = extractTermListForSupp(scSet, suppElementList);
        new DataWriter().writeHashMap2(scEntryMap, scEntryTermMapOut);
    }
    
    
    public void runExtractScMappedMeshSyn(){
        String scMeshMap = "../../mesh/sc_mesh_map.txt";
        HashSet<String> meshSet = new HashSet<>();
        HashMap<String, HashSet<String>> map = new DataReader().readMap(scMeshMap);
        for(HashSet<String> value: map.values())
            meshSet.addAll(value);
        String meshXml = "../../mesh/desc2015.xml";
        List<Element> meshList = extractMeshElementList(meshXml);
        HashMap<String, HashSet<String>> resMap = extractHeadingSynMap2(meshSet,meshList);
        String output = "../../mesh/sc_mesh_ids_entry_term_map.txt";
        new DataWriter().writeHashMap2(resMap, output);
    }
    
    public void runExtractScMap(){
        String scSetFile = "../../mesh/sc.txt";
        String scMeshMapFile = "../../mesh/sc_mesh_map.txt";
        String scConceptMapFile = "../../mesh/sc_concept_map.txt";
        String scMeshEntryTermMapFile = "../../mesh/sc_mesh_ids_entry_term_map.txt";
        String output = "../../mesh/sc_map.txt";
        extractScMap(scSetFile,
            scMeshMapFile,
            scConceptMapFile,
            scMeshEntryTermMapFile,
            output);
    }
    
    
    public void runExtractSynMap(){
        String diseaseMeshAssoc = "../../mesh/disease_mesh_assoc.txt";
        String diseaseFile = "../../disease/disease_id.txt";
        String meshEntryTermMap = "../../mesh/mesh_map.txt";
        String scEntryTermMap = "../../mesh/sc_map.txt";
        String output = "../../disease/disease_syn_map.txt";
        extractSynMap(diseaseMeshAssoc,
            diseaseFile,
            meshEntryTermMap,
            scEntryTermMap,
            output);
    }
    
    
    public static void main(String args[]){
        //new MeshExtractor().runExtractDiseaseMeshMapping();
        //new MeshExtractor().runReduceSuppXml();
        //List<Element> suppXml = new MeshExtractor().extractSuppElementList("../../mesh/reduced_supp2015.xml");
        //List<Element> meshXml = new MeshExtractor().extractMeshElementList("../../mesh/desc2015.xml");
        //while(true){}
        //new MeshExtractor().runExtractMeshMapAndScTerms();
        //new MeshExtractor().runExtractorScMap();
        //new MeshExtractor().runExtractScMappedMeshSyn();
        //new MeshExtractor().runExtractScMap();
        new MeshExtractor().runExtractSynMap();
    }
    
}
