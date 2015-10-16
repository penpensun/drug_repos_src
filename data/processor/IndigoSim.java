/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.processor;
import com.ggasoftware.indigo.*;
/**
 * This class computes tanimoto coeff by Indigo package.
 * @author penpen926
 */
public class IndigoSim {
    
    public double runSim(String smiles1, String smiles2){
        /* Create the indigo object.*/
        Indigo indigo = new Indigo();
        IndigoObject mol1 = indigo.loadMolecule(smiles1);
        IndigoObject mol2 = indigo.loadMolecule(smiles2);
        mol1.aromatize();
        mol2.aromatize();
        IndigoObject fp1 = mol1.fingerprint("sim");
        IndigoObject fp2 = mol2.fingerprint("sim");
        
        
        double tanimoto = indigo.similarity(fp1, fp2,"tanimoto");
        double tversky = indigo.similarity(fp1, fp2,"tversky");
        return tversky;
    }
    /** 
     * This method is used to test the correctness of yuan zhao's similarities.
     */
    private void testYuanSim(){
        /* Create the indigo object.*/
        Indigo indigo = new Indigo();
        IndigoObject mol1_1 = indigo.loadMolecule("CC(C)C[C@H](NC(=O)[C@@H](COC(C)(C)C)NC(=O)[C@H](CC1=CC=C(O)C=C1)NC(=O)[C@H](CO)NC(=O)[C@H](CC1=CNC2=CC=CC=C12)NC(=O)[C@H](CC1=CN=CN1)NC(=O)[C@@H]1CCC(=O)N1)C(=O)N[C@@H](CCCN=C(N)N)C(=O)N1CCC[C@H]1C(=O)NNC(N)=O");
        IndigoObject mol1_2 = indigo.loadMolecule("NC(=O)CC[C@@H]1NC(=O)[C@H](CC2=CC=CC=C2)NC(=O)[C@H](CC2=CC=C(O)C=C2)NC(=O)CCSSC[C@H](NC(=O)[C@H](CC(N)=O)NC1=O)C(=O)N1CCC[C@H]1C(=O)N[C@@H](CCCNC(N)=N)C(=O)NCC(N)=O");
        mol1_1.aromatize();
        mol1_2.aromatize();
        IndigoObject fp1_1 = mol1_1.fingerprint("sim");
        IndigoObject fp1_2 = mol1_2.fingerprint("sim");
        
        
        double tanimoto_1 = indigo.similarity(fp1_1, fp1_2,"tanimoto");
        double tversky_1 = indigo.similarity(fp1_1, fp1_2,"tversky");
        System.out.println("The tanimoto sim between DB00014 and DB00035 is:  "+tanimoto_1);
        System.out.println("The tversky sim between DB00014 and DB00035 is:  "+tversky_1);
        
        IndigoObject mol2_1 = indigo.loadMolecule("CC(C)C[C@H](NC(=O)[C@@H](COC(C)(C)C)NC(=O)[C@H](CC1=CC=C(O)C=C1)NC(=O)[C@H](CO)NC(=O)[C@H](CC1=CNC2=CC=CC=C12)NC(=O)[C@H](CC1=CN=CN1)NC(=O)[C@@H]1CCC(=O)N1)C(=O)N[C@@H](CCCN=C(N)N)C(=O)N1CCC[C@H]1C(=O)NNC(N)=O");
        IndigoObject mol2_2 = indigo.loadMolecule("NC(=O)[C@H]1C[C@]2(NC(=O)NC2=O)C2=CC(F)=CC=C2O1");
        mol2_1.aromatize();
        mol2_2.aromatize();
        IndigoObject fp2_1 = mol2_1.fingerprint("sim");
        IndigoObject fp2_2 = mol2_2.fingerprint("sim");
        
        double tanimoto_2 = indigo.similarity(fp2_1, fp2_2,"tanimoto");
        double tversky_2 = indigo.similarity(fp2_1, fp2_2,"tversky");
        System.out.println("The tanimoto sim between DB00014 and DB02101 is:  "+tanimoto_2);
        System.out.println("The tversky sim between DB00014 and DB02101 is:  "+tversky_2);
        
    }
    
    public static void main(String[] args){
        IndigoSim sim = new IndigoSim();
        sim.testYuanSim();
    }
}
