/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rules;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author m93492
 */
public class ConjuntoProd {
    List<Producao> conjProd;// Conjunto de produções da gramatica
    List<String> conjNterm; // Conjunto de não terminais
    List<String> conjTerm;  // Conjunto de terminais
    String termIni;         // Termo inicial
    int tipoG;              // Tipo de gramatica
    
    
    
    boolean fTemVazio = false;      // Produção tem sentença vazia
    boolean fUmNT = true;           // Lado Esquerdo só tem um e somente um Não terminal
    boolean fMaiorQ2 = false;       // Lado Direito tem mais de 2 simbolos 
    boolean fLeMaiorQLd = false;    // Lado esquerdo maior que lado direito 
    int menorSentenca = 99;         // Não foi usado

    public ConjuntoProd() {
        this.conjProd = new LinkedList<>();
        this.conjNterm = new LinkedList<>();
        this.conjTerm = new LinkedList<>();
    }

    public void setTermIni(String termIni) {
        this.termIni = termIni;     // Seta o termo inicial do conjunto
    }
        
    public void setProd(String[] auxText){
        
        for (int i = 0; i < auxText.length; i++) {
            String[] auxProd = auxText[i].split(":");       // separa produção em Lado Esq e Dir
                                                            // Ex.:     S : a,b
            conjProd.add(new Producao(auxProd[0]));         // Cria um objeto Produção com a sentença
                                                            // do lado Esquerdo Ex.: "S"
            String[] auxSent = auxProd[1].split(",");       // separa o Lado Direro por virgula 
            //System.out.println(auxProd[1]);
            for (int j = 0; j < auxSent.length ; j++) {     // adiciona cada sentença do lado Dir
                conjProd.get(i).addProd(auxSent[j]);        // na lista de derivação da sentença
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

    public int getTipoG() {
        return tipoG;           //retorna tipo de gramatica
    }
    
    @Override
    public String toString() { // gera o G = ({}...) P->aa|bb
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
                text += "\n";
            }
        }
        text += "}";
        
        return text;
    }
    
    public String reconheceGramatica(){ //faz todos os testes para reconhecer a gramatica
                                        //seta todas a flags(variaveis booleanas)
        String txt = "";
        
        for(Producao p : conjProd){
           if(p.nTerm.length()>1){
               fUmNT = false;
           }
           
            for(String s : p.prod){
                if(s.length()<menorSentenca) menorSentenca = s.length();
                if(s.contains("&")) fTemVazio = true;
                if(s.length()>2) fMaiorQ2 = true;
            }
            
            if(p.nTerm.length()>menorSentenca) ;
     
        }
        
        if (fUmNT) txt += "\n* Tem só um simbolo não-terminal do lado esquerdo";
        if (fTemVazio) txt += "\n* Contém sentença vazia do lado direito";
        if (fMaiorQ2) txt += "\n* Mais que dois simbolos do lado direito";
        
        if (fUmNT && !fMaiorQ2)  {
            txt += "\n!!! É uma Gramática Regular";
            this.tipoG = 3;
        }
        else if (fUmNT && !fTemVazio) {
            txt += "\n!!! É uma Gramática Livre de Contexto";
            this.tipoG = 2;
        }
        else if (!fUmNT && !fTemVazio) {
            txt += "\n!!! É uma Gramática Sensível ao Contexto";
            this.tipoG = 1;
        }        
        else {
            txt += "\n!!! É uma Gramática Irrestrita";
            this.tipoG = 0;
        }
        
        
        return txt;
    }
    
    public boolean validaGramatica(){ //não foi usado
        boolean f1 = false;
        boolean f2 = true;
        
        for(Producao p : this.conjProd){
            for(String s : this.conjNterm){
                if(p.nTerm.contains(s)) f1 = true;
            }
            if(!f1) f2 = false;
            f1 = false;
        }
        
        return f2;    
    }
    
    public String geraPalavra(){ //gera uma palavra aleatoria
        String palavra = this.termIni;
        String txt = "\n" + palavra;
        
        Random rand = new Random();
        // rand.nextInt(10)+1
        
        //for (int i = 0; i < rand.nextInt(5); i++) {
        for (int i = 0; i < 15; i++) {
            for(Producao p : this.conjProd){
                
                if(palavra.contains(p.nTerm)){
                    
                    palavra = palavra.substring(0, palavra.length()-1) + p.prod.get(rand.nextInt(p.prod.size()));
                    
                    txt += " -> " + palavra;
                   
                }                
            }            
        }        
        return txt;
    }  

       
    public void removeInfertilEInutil(){
        int index;
        int repete=1;
        String nTerminal, textAuxB = "";
        String producoesTodas = "",nTermTodas = "";
        
        List<String> termFertil = new LinkedList<>();   // Nao terminais Ferteis
        
        List<String> termNaoFertil = new LinkedList<>();    // Nao terminais Inferteis ou inuteis
        
        List<String> deProdRemove = new LinkedList<>(); //Nao terminais que estao na producao a ser removido
        
        for(Producao p: this.conjProd ){        //Filtra somente os nao terminais Ferteis
            nTerminal = p.nTerm;
            for(Producao aux: this.conjProd ){
                for (String vxx : aux.prod) {
                    if(vxx.contains(nTerminal) || this.termIni.contains(nTerminal) ){
                        if(termFertil.contains(nTerminal)){
                            repete++;
                        }else{
                            termFertil.add(nTerminal);      //adiciona a lista
                        }
                    }
                }
            }
        }
        
        for(String aux:termFertil){
            System.out.println("Terminal fertil: "+aux);
        }
        
        for(Producao p: this.conjProd ){        //Adiciona todos nao terminais em uma string
            nTermTodas = nTermTodas + p.nTerm;
        }
        String esqn = "";
        String dirn = "";
        
        for(int a=0; a <= repete; a++ ){
            for(String aux:termFertil){
                
                index = nTermTodas.indexOf(aux);
                if(index != -1){
                    if (!nTermTodas.isEmpty()) esqn = nTermTodas.substring (0, index);
                    //System.out.println(esqn);
                    if (!nTermTodas.isEmpty()) dirn = nTermTodas.substring (index+1, nTermTodas.length());
                    //System.out.println(dirn);
                    nTermTodas = esqn + dirn;   //string que recebe todos os inferteis
                }
            }
        }
        
        for(int b = 0; b<nTermTodas.length(); b++){     //Remove nao terminais inferteis e suas producoes
            for(Producao p: this.conjProd ){
                if(nTermTodas.substring(b, b+1).equals(p.nTerm)){
                    this.conjProd.remove(p);
                    termNaoFertil.add(nTermTodas.substring(b, b+1));
                    break;
                }
            }
        }
        for(Producao p: this.conjProd ){    //adiciona todas as producoes em uma string
            for(String texto : p.prod){
                producoesTodas = producoesTodas + texto;
            }
        }
        String esq = "";
        String dir = "";
        for(int z=0; z <= repete; z++ ){        //Adiciona todos os terminais e os nao terminais inferteis que estao nas producoes em uma string
            for(String aux:termFertil){
                index = producoesTodas.indexOf(aux);
                if(index != -1){
                    if (!producoesTodas.isEmpty()) esq = producoesTodas.substring (0, index);
                    //System.out.println(esq);
                    if (!producoesTodas.isEmpty()) dir = producoesTodas.substring (index+1, producoesTodas.length());
                    //System.out.println(dir);
                    producoesTodas = esq + dir;   
                }
            }
        }
        String textAux = producoesTodas.toUpperCase();
        
        for(int y = 0; y<producoesTodas.length(); y++){     //Filtra somente o nao terminal infertil
            if(textAux.substring(y, y+1).equals(producoesTodas.substring(y, y+1))){
                if(!textAux.substring(y, y+1).equals("&")){
                    textAuxB = producoesTodas.substring (y, y+1);
                    deProdRemove.add(textAuxB);     //adiciona os nao terminais que estao nas producoes na lista
                }
            }
        }
        
        for(Producao p: this.conjProd ){
            System.out.printf(p.nTerm+" -> ");
            for(String texto : p.prod){
                System.out.printf(" "+texto);
            }
            System.out.println("");
        }
        System.out.println("*********** Removendo inuteis e não ferteis ****************");
        
        for(String pToRem: deProdRemove ){      //Remove nao terminais inferteis das producoes
            for(Producao aux: this.conjProd ){
                for (String vxx : aux.prod) {
                    if(vxx.contains(pToRem)){
                        aux.prod.remove(vxx);
                        break;
                    }
                }
            }
        }
        for(Producao p: this.conjProd ){
            System.out.printf(p.nTerm+" -> ");
            
            for(String texto : p.prod){
                System.out.printf(" "+texto);
            }
            System.out.println("");
        } 
    }
    
}
