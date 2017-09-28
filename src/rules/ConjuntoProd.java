/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rules;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author m93492
 */
public class ConjuntoProd {
    List<Producao> conjProd;
    List<String> conjNterm;
    List<String> conjTerm;
    String termIni;
    
    
    
    boolean temVazio = false;
    boolean umNT = false;
    boolean maiorQ2 = false;

    public ConjuntoProd() {
        this.conjProd = new LinkedList<>();
        this.conjNterm = new LinkedList<>();
        this.conjTerm = new LinkedList<>();
    }

    public void setTermIni(String termIni) {
        this.termIni = termIni;
    }
        
    public void setProd(String[] auxText){
        
        for (int i = 0; i < auxText.length; i++) {
            String[] auxProd = auxText[i].split(":");
            
            conjProd.add(new Producao(auxProd[0]));
            
            String[] auxSent = auxProd[1].split(",");
            System.out.println(auxProd[1]);
            for (int j = 0; j < auxSent.length ; j++) {
                conjProd.get(i).addProd(auxSent[j]);
                //System.out.println(auxSent[j]);
            }
        }
    }
    
    public void setConjuntos(String[] nTerm,String[] term){
        for(String x: nTerm){
            conjNterm.add(x);
        }
        
        for(String y: term){
            conjTerm.add(y);
        }
    }

    @Override
    public String toString() {
        String text = "G = ({";
        text += conjNterm.get(0);
        if(conjNterm.size()>1){
            for (int i = 1; i < conjNterm.size(); i++) {
                text += "," + conjNterm.get(i);
            }
        }
        
        text += "}, {" +conjTerm.get(0);
        if(conjTerm.size()>1){
            for (int i = 1; i < conjTerm.size(); i++) {
                text += "," + conjTerm.get(i);
            }
        }
        text += "}, P, "
                + termIni
                + ")\n";
        
        
        text += "P: {";
        for (Producao p :conjProd) {
            text += "" + p.nTerm + "->" + p.prod.get(0);
            
            if(p.prod.size()>1){
                for (int i = 1; i < p.prod.size(); i++) {
                    text += "|" + p.prod.get(i);
                }
            }
            text += "}\n";
        }
        
        return text;
    }
    
    public String reconheceGramatica(){
        String txt = "";
        
        for(Producao p : conjProd){
           if(p.nTerm.length()==1){
               umNT = true;
               
           }
           
            for(String s : p.prod){
                if(s.contains("&")) temVazio = true;
                if(s.length()>2) maiorQ2 = true;
            }
     
        }
        
        if (umNT) txt += "\n* Tem só um não-terminal do lado esquerdo";
        if (temVazio) txt += "\n* Contém vazio do lado direito";
        if (maiorQ2) txt += "\n* Mais que dois simbolos do lado direito";
        
        if (umNT && !maiorQ2)  txt += "\n!!! É uma Gramática Regular";
        else if (umNT && !temVazio) txt += "\n!!! É uma Gramática Livre de Contexto";
        else if (!umNT && !temVazio) txt += "\n!!! É uma Gramática Livre de Contexto";        
        else txt += "\n!!! É uma Gramática Irrestrita";
        
        
        return txt;
    }
  

    
}
