// Attention pour la genreation de l'operation j'ai fait que le mode shuffle ! 
// Faire le mode, sans shuffle (rajouter un parametre.

class projet extends Program{

	// fonction pour afficher une équation
	// prend en parametre : l'intervalle des nombres qui appraraissent, les operateurs possibles, le nombre d'operateurs, combines ou non. 

	int[][] copyEndTable(int[][] table,int indice,int nbOpe){
	    for(int i=indice; i<nbOpe+1; i++){ // avant derniere case du tableau contenant les nombres.
		table[0][i]=table[0][i+1];
		    }
	    
	    for(int i=indice; i<nbOpe; i++){
		table[1][i]=table[0][i+1];
	    }
	    return table;
	}

	int[][] prepareOperation(int minNumb, int maxNumb, int codeOperators, int nbOperators, int handset){
	    int[][] operation = new int[3][nbOperators + 1];

	    // Selection des nombres figurants dans l'operation
	    for(int i=0; i<nbOperators + 1; i++){ 
		operation[0][i]= (int)(random()*maxNumb + 1);
		while(operation[1][i]<minNumb){
		    operation[0][i] = (int)(random()*maxNumb + 1);
		}
	    }


	    // ATTENTION !! Tout mettre directement dans une chaine.


	    // remplissage d'un tableau contennant les operateurs dispo (1=dispo, 0=interdit) : 
	    // ex : 1101 => +-_/
	    int[] operatorsAvailable=new int[4];
	    int ind=0;
	    int nbOpAvailable=0;
	    while(ind<length(operatorsAvailable) && codeOperators !=0){
		operatorsAvailable[length(operatorsAvailable)-1-ind]=codeOperators%2;
		if(codeOperators%2==1) nbOpAvailable++;
		codeOperators/=2;
		ind++;
	    }

	    // Selection d'un operateur au hasard dans le premier tableau
	    // 1:+ 2:- 4:* 8:/ 16:=       et 10 correspond a / ou -
	    int idOp;
	    int position=0;
	    ind=0;
	    for(int i=0; i<=nbOperators; i++){
		// 11 : / - +   , indice 1,2,3
		idOp=(int)(random()*nbOpAvailable);
		while(ind<idOp){ // verifier le <=
		    if(operatorsAvailable[ind]==1) ind++;
		    position++;
		}		
		operation[1][i]=(int)(Math.pow(2,position));
	    }
	    
	    // calcul du resultat.
	    // Attention à la priorite des operateurs !! surtout x et /
	    
	    int[][] resultBuffer= new int[length(operation,1)][length(operation,2)];
	    
	    // recopie de result dans un buffer qui va nous servir au calcul.
	    for(int i=0; i<length(operation,1)-1; i++){
		for(int j=0; j<length(operation,2); j++){
		    resultBuffer[i][j]=operation[i][j];
		}
	    }

	    /* Etape 1, tant qu'il y a des X ou des /
	         A chaque X ou / trouve : 
	         Etape 1.1, on effectue ces calculs la.
	         Etape 1.2, on recopie le tableau des chiffres, en mettant le nouveau resultat et on decalle la fin du tableau.
	         Etape 1.3, on recopie le tableau des operateurs, en enlevant l'operateurs qui vient d'etre traite..
	       Etape 2, on fait tous les calculs correspondant aux autres operateurs, en decallant a chaque fois les resultats comme pour l'etape 1
	     */
	    boolean isThereFirstOpe=true;
	    while(isThereFirstOpe){
		isThereFirstOpe=false;
		ind=0;
		while(ind<nbOperators && (resultBuffer[1][ind]!=4 || resultBuffer[1][ind]!=8)){
		    ind++;
		}
		
		if(resultBuffer[1][ind]==4){
		    resultBuffer[0][ind]*=resultBuffer[0][ind+1];
		    copyEndTable(resultBuffer,ind+1,nbOperators);// recopie du reste du tableau
		    isThereFirstOpe=true;
		    nbOperators--;
		}

		else if(resultBuffer[1][ind]==8){
		    resultBuffer[0][ind]/=resultBuffer[0][ind+1];
		    copyEndTable(resultBuffer,ind+1,nbOperators);
		    isThereFirstOpe=true;
		    nbOperators--;
		}
	    }

	    ind=0;
	    while(nbOperators>0){
		if(resultBuffer[0][ind]==1){
		    resultBuffer[0][ind]+=resultBuffer[0][ind+1];
		    copyEndTable(resultBuffer,ind+1,nbOperators);
		    nbOperators--;
		}
		else if(resultBuffer[0][ind]==2){
		    resultBuffer[0][ind]-=resultBuffer[0][ind+1];
		    copyEndTable(resultBuffer,ind+1,nbOperators);
		    nbOperators--;
		}
	    }
	    operation[3][0]=resultBuffer[0][0];

	    return operation;
	}
	
    String chaineOperation(int [][] table){
	String opeChaine="";
	for(int i=0; i<length(table,2); i++){
	    opeChaine+=table[0][i];
	    opeChaine+=table[1][i];
	}
	opeChaine+=table[0][length(table,1)-1];
	return opeChaine;
    }

    void algorithm(){
	println(chaineOperation(prepareOperation(1,4,5,3,0)));

    }
}