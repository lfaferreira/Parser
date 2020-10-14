/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author Lucas Fernando
 */
public class Scanner {

    private Integer LINE = 1; //Marcador de Linhas do codigo para detecção do erro
    private Integer COLUMN = 0; //Marcador de Colunas do codigo para detecção do erro
    private char LOOKAHEAD = ' ';//Buscador do proximo caracter no arquivo lido. 
    private String BUFFER = "";//Buffer responsavel pela criação dos this.tokens.
    private Deque<String> tokens;//Pilha para guardar os this.tokens criados.
    private BufferedReader arquivoUsuario;

    public Scanner(BufferedReader arquivo) {
        arquivoUsuario = arquivo;
    }

    public Deque<String> scan() throws FileNotFoundException, IOException { //Metodo principal da Classe Scanner. Starta todo o processo de leitura, classificação de this.tokens e print dos this.tokens para o usuario.
        this.tokens = new ArrayDeque<String>();
        return tokenClassification(arquivoUsuario);
    }

    private Deque<String> tokenClassification(BufferedReader arquivoUsuario) {
        while (LOOKAHEAD != 65535) { //"65535" é o codigo referente ao simbolo de EOF                
            if (validCharacter()) {
                while (Character.isWhitespace(LOOKAHEAD)) { //Verificia se LH é space, quebra de linha ou TAB
                    lookAHead(arquivoUsuario);
                }
                if (Character.isDigit(LOOKAHEAD) || LOOKAHEAD == '.') { //1ª seção de criação dos this.tokens. Nesta seção do codigo serão criados os this.tokens numericos, sejam eles int ou float.
                    tokenNumericoInt(arquivoUsuario);
                    return this.tokens;
                }
                if (Character.isLetter(LOOKAHEAD) || LOOKAHEAD == 95) {//2ª seleção de criação dos this.tokens. Nesta seção do codigo serão criados os this.tokens de Identificadores, sejam eles variaveis ou palavras reservadas.                                        
                    tokenID(arquivoUsuario);
                    return this.tokens;
                }
                if (LOOKAHEAD == 39) { //3ª seleção de criação dos this.tokens. Nesta seção do codigo serão criados os this.tokens de Characteres. "39" é o codigo de apostrofo.
                    tokenChar(arquivoUsuario);
                    return this.tokens;
                }
                if (LOOKAHEAD == ';' || LOOKAHEAD == ',' || LOOKAHEAD == '}' || LOOKAHEAD == '{' || LOOKAHEAD == '(' || LOOKAHEAD == ')') {//4ª seleção de criação dos this.tokens.Nesta seção do codigo serão criados os this.tokens especiais.
                    this.tokenSpecial(arquivoUsuario);
                    return this.tokens;
                }
                if (LOOKAHEAD == '+' || LOOKAHEAD == '-' || LOOKAHEAD == '*' || LOOKAHEAD == '=' || LOOKAHEAD == '/') {//5ª Seleção de criação do this.tokens. Nesta seção do codigo serão criados os this.tokens de operações artimeticas.
                    tokenAritmetico(arquivoUsuario);
                    return this.tokens;
                }
                if (LOOKAHEAD == '<' || LOOKAHEAD == '>' || LOOKAHEAD == '!') { //6ª Seleção de criação do this.tokens. Nesta seção do codigo serão criados os this.tokens de operação relacional.
                    tokenRelacional(arquivoUsuario);
                    return this.tokens;
                }
            } else { //Caso o caracter não seja valido na gramatica da linguagem aceita pelo Scanner, ele será invalido.               
                characterInvalid("Character Invalido!");
                System.exit(0);
            }
        }
        this.tokens.clear();
        return this.tokens;
    }

    private void lookAHead(BufferedReader arquivoUsuario) { //Metodo para leitura do arquivo enviado pelo usuario. A leitura é feita caracter por caracter
        try {
            LOOKAHEAD = (char) arquivoUsuario.read();
            localization();
        } catch (IOException ex) {
            System.err.println("Erro na leitura binaria do arquivo.");
        }
    }

    private void localization() { //Metodo para identificar em qual local está ocorrendo a leitura do arquivo. Ocorre atraves de cada linha e coluna.
        
        if (LOOKAHEAD == 13) {
            LINE = LINE + 1;
            COLUMN = 0;
        } else {
            if (LOOKAHEAD == 9) {
                COLUMN = COLUMN + 4;
            } else {
                COLUMN = COLUMN + 1;
            }
        }
    }

    private boolean validCharacter() { //Metodo para validação do caractere lido.
        if (LOOKAHEAD == 34) {//é o codigo de '"'
            return false;
        } else if (LOOKAHEAD == 35) { //é o codigo de '#'
            return false;
        } else if (LOOKAHEAD == 37) {//é o codigo de '%'
            return false;
        } else if (LOOKAHEAD == 38) {//é o codigo de '&'
            return false;
        } else if (LOOKAHEAD == 58) {//é o codigo de ':'
            return false;
        } else if (LOOKAHEAD == 63) {//é o codigo de '?'
            return false;
        } else if (LOOKAHEAD == 64) {//é o codigo de '@'
            return false;
        } else if (LOOKAHEAD == 92) {//é o codigo de '\'
            return false;
        } else if (LOOKAHEAD == 94) {//é o codigo de '^'
            return false;
        } else if (LOOKAHEAD == 96) {//é o codigo de '`'
            return false;
        } else if (LOOKAHEAD == 124) {//é o codigo de '|'
            return false;
        } else if (LOOKAHEAD == 126) {
            return false;
        } else if (LOOKAHEAD == 65533) {//"65533" é o codigo referente ao simbolo de 'ç','Ç','´','ª','º','¨','¬'
            return false;
        }
        return true;
    }

    private void writeToken() { //Metodo para escrita do token no buffer, enquanto ele está sendo classificado.
        BUFFER = BUFFER + LOOKAHEAD;
    }

    private void clearBuffer() {//Metodo para limpeza do buffer.
        BUFFER = "";
    }

    private void tokenNumericoInt(BufferedReader arquivoUsuario) { //Metodo para classificação de Token Numerico do tipo Inteiro.
        while ((LOOKAHEAD >= 48 && LOOKAHEAD <= 57) || LOOKAHEAD == '.') {
            if (LOOKAHEAD == '.') {
                if (BUFFER == "") {
                    BUFFER = 0 + BUFFER;
                }
                tokenNumericoFloat(arquivoUsuario);
                return;
            } else {
                writeToken();
                lookAHead(arquivoUsuario);
            }
        }
        this.tokens.add("60");
        this.tokens.add(BUFFER);
        clearBuffer();
    }

    private void tokenNumericoFloat(BufferedReader arquivoUsuario) { //Metodo para classificação de Token Numerico do tipo Float.
        writeToken();
        lookAHead(arquivoUsuario);
        while (true) {
            if (LOOKAHEAD == 65535 && (BUFFER.charAt(BUFFER.length() - 1) == '.')) {
                characterInvalid("Float Mal Formado");
                System.exit(0);
            } else if (LOOKAHEAD < 48 && LOOKAHEAD > 57) {
                characterInvalid("Float Mal Formado");
                System.exit(0);
            } else if (LOOKAHEAD >= 48 && LOOKAHEAD <= 57) {
                writeToken();
                lookAHead(arquivoUsuario);
            } else {
                break;
            }
        }
        this.tokens.addFirst("70");
        this.tokens.add(BUFFER);
        clearBuffer();
    }

    private void tokenID(BufferedReader arquivoUsuario) { //Metodo para a classificação de this.tokens do tipo Identificadores.
        while (Character.isLetterOrDigit(LOOKAHEAD) || LOOKAHEAD == 95) {
            writeToken();
            lookAHead(arquivoUsuario);
        }
        if (tokenReserved()) {
            BUFFER = "";
        } else {
            this.tokens.addFirst("10");
            this.tokens.add(BUFFER);
            clearBuffer();
        }
    }

    private boolean tokenReserved() { //Metodo para a verificação de this.tokens de identificadores que possam ser palavras reservadas na linguagem defina.
        switch (BUFFER) {
            case "main":
                this.tokens.addFirst("50");
                this.tokens.add(BUFFER);
                return true;
            case "if":
                this.tokens.addFirst("51");
                this.tokens.add(BUFFER);
                return true;
            case "else":
                this.tokens.addFirst("52");
                this.tokens.add(BUFFER);
                return true;
            case "while":
                this.tokens.addFirst("53");
                this.tokens.add(BUFFER);
                return true;
            case "do":
                this.tokens.addFirst("54");
                this.tokens.add(BUFFER);
                return true;
            case "for":
                this.tokens.addFirst("55");
                this.tokens.add(BUFFER);
                return true;
            case "int":
                this.tokens.addFirst("56");
                this.tokens.add(BUFFER);
                return true;
            case "float":
                this.tokens.addFirst("57");
                this.tokens.add(BUFFER);
                return true;
            case "char":
                this.tokens.addFirst("58");
                this.tokens.add(BUFFER);
                return true;
        }
        return false;
    }

    private void tokenChar(BufferedReader arquivoUsuario) { //Metodo para criação de this.tokens do tipo Character.
        writeToken();
        lookAHead(arquivoUsuario);
        if (Character.isLetterOrDigit(LOOKAHEAD)) {//BUFFER = '+ Digito ou Letra
            writeToken();
            lookAHead(arquivoUsuario);
            if (LOOKAHEAD == 39) { //BUFFER = 'Digito ou Letra'
                writeToken();
                this.tokens.addFirst("80");
                this.tokens.add(BUFFER);
                clearBuffer();
                lookAHead(arquivoUsuario);
            }
        } else {
            characterInvalid("Charactere mal formado");
            System.exit(0);
        }
    }

    private void tokenSpecial(BufferedReader arquivoUsuario) {//Metodo para classificação de this.tokens do tipo Especial.
        writeToken();
        lookAHead(arquivoUsuario);
        switch (BUFFER) {
            case ";":
                this.tokens.addFirst("40");
                this.tokens.add(BUFFER);
                break;
            case ",":
                this.tokens.addFirst("41");
                this.tokens.add(BUFFER);
                break;
            case "{":
                this.tokens.addFirst("42");
                this.tokens.add(BUFFER);
                break;
            case "}":
                this.tokens.addFirst("43");
                this.tokens.add(BUFFER);
                break;
            case "(":
                this.tokens.addFirst("44");
                this.tokens.add(BUFFER);
                break;
            case ")":
                this.tokens.addFirst("45");
                this.tokens.add(BUFFER);
                break;
        }
        clearBuffer();
    }

    private void tokenAritmetico(BufferedReader arquivoUsuario) {//Metodo para classificação de this.tokens do tipo Aritmetico .
        writeToken();
        lookAHead(arquivoUsuario);
        switch (BUFFER) {
            case "=":
                if (LOOKAHEAD == '=') {
                    tokenRelacional(arquivoUsuario);
                } else {
                    this.tokens.addFirst("30");
                    this.tokens.add(BUFFER);
                }
                break;
            case "+":
                this.tokens.addFirst("31");
                this.tokens.add(BUFFER);
                break;
            case "-":
                this.tokens.addFirst("32");
                this.tokens.add(BUFFER);
                break;
            case "*":
                this.tokens.addFirst("33");
                this.tokens.add(BUFFER);
                break;
            case "/": //Caso o proximo caracter após o '/' seja outra '/' ou um '*', ele será identificado como um comentario e irá para o respectivo metodo de classificação.
                if (LOOKAHEAD == '/') {
                    comment(arquivoUsuario);
                    return;
                } else if (LOOKAHEAD == '*') {
                    comment(arquivoUsuario);
                    return;
                } else {
                    this.tokens.addFirst("34");
                    this.tokens.add(BUFFER);
                }
                break;
        }
        clearBuffer();
    }

    private void comment(BufferedReader arquivoUsuario) {//Metodo para verificação de comentarios.
        if (LOOKAHEAD == '/') { //Caso seja um comentario de linha unica.
            while (true) {
                if (LOOKAHEAD == 13) {// quebra de linha '\n'
                    clearBuffer();
                    return;
                } else if (LOOKAHEAD == 65535) { //EOF
                    clearBuffer();
                    return;
                } else {
                    lookAHead(arquivoUsuario);
                }
            }
        } else { //Caso seja um comentario de quebra de linha.
            while (true) {
                lookAHead(arquivoUsuario);
                if (LOOKAHEAD == 65535) {
                    characterInvalid("Comentario mal formado");
                    System.exit(0);
                } else if (LOOKAHEAD == '*') {
                    lookAHead(arquivoUsuario);
                    if (LOOKAHEAD == '/') {
                        lookAHead(arquivoUsuario);
                        clearBuffer();
                        return;
                    }
                }
            }
        }
    }

    private void tokenRelacional(BufferedReader arquivoUsuario) { //Metodo de classificação para this.tokens do tipo Relacional
        writeToken();
        lookAHead(arquivoUsuario);
        switch (BUFFER) {
            case "==":
                this.tokens.addFirst("20");
                this.tokens.add(BUFFER);
                break;
            case ">":
                if (LOOKAHEAD == '=') {
                    writeToken();
                    this.tokens.addFirst("21");
                    this.tokens.add(BUFFER);
                } else {
                    this.tokens.addFirst("22");
                    this.tokens.add(BUFFER);
                }
                break;
            case "<":
                if (LOOKAHEAD == '=') {
                    writeToken();
                    this.tokens.addFirst("23");
                    this.tokens.add(BUFFER);
                } else {
                    this.tokens.addFirst("24");
                    this.tokens.add(BUFFER);
                }
                break;
            case "!":
                if (LOOKAHEAD == '=') {
                    writeToken();
                    this.tokens.addFirst("25");
                    this.tokens.add(BUFFER);
                } else {
                    return;
                    //characterInvalid("Token relacional mal formado.");
                }
                break;
        }
        lookAHead(arquivoUsuario);
        clearBuffer();
    }

    private void characterInvalid(String mensagem) { //Metodos informativos caso haja um caracter invalido no Arquivo enviado pelo usuario. O metodo printa a linha, coluna e informa qual foi o ultimo token lido e seu tipo.        
        System.err.println("ERRO na linha " + LINE + ", coluna " + COLUMN + ", ultimo token lido:  [Tipo = " + this.tokens.remove() + "],[Token = '" + this.tokens.remove() + "' ] " + mensagem);
        System.exit(0);
    }

    public int getLine() {
        return LINE;
    }
    
    public int getColumn(){
        return COLUMN;
    }

    public char getHead() { //Metodo para printar todos os this.tokens e seus respectivos tipos.
        while (Character.isWhitespace(LOOKAHEAD)) { //Verificia se LH é space, quebra de linha ou TAB
            lookAHead(arquivoUsuario);            
        }
        return LOOKAHEAD;
    }
}
