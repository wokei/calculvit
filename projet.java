// TODO :

//      Attention pour la generation de l'operation j'ai fait que le mode shuffle !  
// [--] Faire le mode, sans shuffle (rajouter un parametre).
// [OK] si presence de /, désactiver le Zero
// [--] mettre des nb negatifs a la place des -, et si on a un + avant un nb negatif, à l'affichage, on n'affiche pas le +.
// [OK] PAs plus d'un / par operation : Mais ATTENTION ! si on a un 5-5 dans l'opération, y a toujours un risque d'avoir un /0 !! 
// [--] Il nous faut la gestion des erreur pour pouvoir refaire une opération si elle est impossible !
//      ET ATTENTION : là on a des divisions qui fournissent des résultats entiers donc, 8/3=2 ! 
//      Faudrait peut être désactiver les / dans les opérations à operateurs mulitples ... 
// Solution imparable à mettre en place : on laisse le 0, mais on s'assure (lors de la sélection des nb) que le resultat des operations qui suivent ne donnent pas 0
// Necessite de faire une fonction de calcul pure et durt (deja faite mais pas en fonction).
// PB car 2/4*3 = 0*3 ou 2/12 ! Il faut faire en sorte que les divisions divisent des trucs qui donnent des résultats entiers non tronqués ! 
// sinon l'ordre des operations prend de l'importance ! (mais normalement y en a pas : cf 5*6/2 = 30/2 = 5*3 = 15
// Le denominateur doit etre un quotient du numerateur ! 
// Pour ça on peut cheater un peu et mettre la / que en fin.
// pb : 3*1/1*4 = 0


class projet extends Program{

        // fonction pour afficher une équation
	// prend en parametre : l'intervalle des nombres qui appraraissent, les operateurs possibles, le nombre d'operateurs, combines ou non. 

	int[][] copyEndTable(int[][] table,int indice,int nbOpe){

	    for(int i=indice; i<nbOpe; i++){ // avant derniere case du tableau contenant les nombres.
		table[0][i]=table[0][i+1];
	    }
	   
	    for(int i=indice; i<nbOpe-1; i++){
		table[1][i]=table[1][i+1];
	    }

	    //debuggage :
	    print("new table ");
	    for(int i=0; i<4; i++){
		print(table[0][i]+"|");
	    }
	    println();
	    print("les signes ");
	    for(int i=0; i<4; i++){
		print(table[1][i]+"|");
	    }
	    println();

            //fin du debuggage
	    return table;
	}
	int[][] prepareOperation(int minNumb, int maxNumb, int codeOperators, int nbOperators, int handset){
	    final int NB_TYPES_OF_OPERATORS = 3; 
	    int[][] operation = new int[3][nbOperators + 1];
	    
	    // remplissage d'un tableau contennant les operateurs dispo (1=dispo, 0=interdit) : 
	    // ex : 1011 => /   - +   
	    boolean[] operatorsAvailable=new boolean[NB_TYPES_OF_OPERATORS];
	    int ind=0;
	    int nbOpAvailable=0;

	    while(ind<length(operatorsAvailable) && codeOperators !=0){
		operatorsAvailable[length(operatorsAvailable)-1-ind]=(codeOperators%2==1);
		if(codeOperators%2==1) nbOpAvailable++; //nbOpavailable correspond au nombre d'operateurs pouvant etre utilises dans l'operation  ! ATTENTION :mettre des booleens
		codeOperators/=2;
		ind++;
	    }

	    // Attention stocké en Big Endian => lecture doit etre en Big aussi
		    
	    // Selection d'un operateur au hasard dans le premier tableau (avec 1101) (si on pioche 3, on prend le 3eme non nul)
	    // 1:+ 2:* 4:/       et 5 correspond a / ou +.  ATTENTION : le - n'existe pas en tant qu'operateurs, ce sont les nombres qui sont negatifs.
	    int idOp;
	    int position;
	    boolean isThereDivision=false;
	    for(int i=0; i<=nbOperators; i++){
		operation[1][i]=0; // initialisation du tableau a zero, car il y a une case en plus qui ne contiendra pas de nombre.
	    }
	    println(nbOpAvailable);
	    for(int i=0; i<nbOperators; i++){
		idOp=(int)(random()*nbOpAvailable+1);
		println("id op= "+idOp);
		position=0;
		ind=length(operatorsAvailable)-1;
		while(ind>=0 && position<idOp){ // Verifier >= .Parcours de la table des opes disponibles (a partir de la fin), jusqu'a trouver le idOp ieme terme.
		    if(operatorsAvailable[ind]==true) position++;
		    ind--;
	        } 
		if(idOp==length(operatorsAvailable)-1){ // Cette condition fait en sorte que l'on ait qu'une seule division.
		    isThereDivision=true;
		    nbOpAvailable--;
		    operatorsAvailable[0]=false;
		}
		println("ind = "+ind);
		println("position = "+position);
		println();
		
		operation[1][i]=(int)(Math.pow(2,length(operatorsAvailable)-2-ind)); // pk -2 ? 
	    }
	    println(nbOpAvailable);

	    // Selection des nombres figurants dans l'operation
	    for(int i=0; i<nbOperators + 1; i++){ 
		operation[0][i]= (int)(random()*(maxNumb-minNumb+1) + minNumb);
		if(isThereDivision==true){ // si y a l'operateur /
		    while(operation[0][i]==0){
			operation[0][i] = (int)(random()*maxNumb + 1);
		    }
		}
		/*		while(operation[0][i]<minNumb){
		    operation[0][i] = (int)(random()*maxNumb + 1);
		}
		*/
	    }

	     // CALCUL DU RESULTAT.
	    
	    int[][] resultBuffer= new int[length(operation,1)][length(operation,2)];
	    
	    // recopie de result dans un buffer qui va nous servir au calcul.
	    for(int i=0; i<length(operation,1)-1; i++){
		for(int j=0; j<length(operation,2); j++){
		    resultBuffer[i][j]=operation[i][j];
		}
	    }

	    // ETAPES DE CALCUL
	    
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
		
		for(int i=nbOperators; i>=0; i--){
		    if(resultBuffer[1][i]==4){
			resultBuffer[0][i]/=resultBuffer[0][i+1];
			resultBuffer[1][i]=resultBuffer[1][i+1];
			copyEndTable(resultBuffer,i+1,nbOperators);// recopie du reste du tableau
			isThereFirstOpe=true;
		 	nbOperators--;
		    }
		    else if(resultBuffer[1][i]==2){
			resultBuffer[0][i]*=resultBuffer[0][i+1];
			resultBuffer[1][i]=resultBuffer[1][i+1];
			copyEndTable(resultBuffer,i+1,nbOperators);// recopie du reste du tableau
			isThereFirstOpe=true;
			nbOperators--;
		    }
		}
	
		println("nbops : "+nbOperators);	    
		for(int i=nbOperators-1; i>=0; i--){
		    println(nbOperators);
		    if(resultBuffer[1][i]==1){
			resultBuffer[0][i]-=resultBuffer[0][i+1];
			resultBuffer[1][i]=resultBuffer[1][i+1];
			copyEndTable(resultBuffer,i+1,nbOperators);// recopie du reste du tableau
		 	nbOperators--;
		    }
		}
    nbOperators--;

	    print("new table ");

	    //debuggage :
	    for(int i=0; i<4; i++){
		print(resultBuffer[0][i]+"|");
	    }
	    println();
	    print("les signes ");
	    for(int i=0; i<4; i++){
		print(resultBuffer[1][i]+"|");
	    }
	    println();

	    }
	    
	    ind=0;
	    while(ind<length(resultBuffer,1) && nbOperators>0){
		if(resultBuffer[0][ind]==1){
		    resultBuffer[0][ind]+=resultBuffer[0][ind+1];
		}
		else if(resultBuffer[0][ind]==2){
		    resultBuffer[0][ind]-=resultBuffer[0][ind+1];
		}
		nbOperators--;
		copyEndTable(resultBuffer,ind+1,nbOperators);
		ind++;
	    }
	    operation[2][0]=resultBuffer[0][0];
	    
	    	    
	    return operation;
	}
    
    String chaineOperation(int [][] table){
	String opeChaine="";
	for(int i=0; i<length(table,2); i++){
	    opeChaine+=table[0][i];
	    if(table[1][i]==1){
		opeChaine+="+"; // remplacer les 1 2 4 par des + * /
	    }
	    else if(table[1][i]==2){
		opeChaine+="*";
	    }
	    else if(table[1][i]==4){
		opeChaine+="/";
	    }
	}
	opeChaine+=" = "+table[2][0];
	
	return opeChaine;
    }

    void algorithm(){
        int[][] operationGenerated=prepareOperation(0,4,6,3,0);
	for(int i=0; i<length(operationGenerated,1); i++){
	    for(int j=0; j<length(operationGenerated,2); j++){
		print(operationGenerated[i][j]);
	    }

	}
	println();
	    println(chaineOperation(operationGenerated));
	print("salut");
    }
}