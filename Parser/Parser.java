/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author Lucas Fernando
 */
public class Parser {

    private Deque<String> TOKEN = new ArrayDeque<String>();//Pilha para guardar os tokens criados.
    private final Scanner SCANNER = new Scanner();

    public Parser() {

    }

    public void runningParser(BufferedReader arquivoUsuario) throws IOException {
        programSyntax(arquivoUsuario);
        arithmeticExpression(arquivoUsuario);
        assignment(arquivoUsuario);
    }

    private void variableDeclaration(BufferedReader arquivoUsuario) throws IOException {
        getToken(arquivoUsuario); //1ª Chamada do Scanner          
        if (isVariableTypeValidation(TOKEN.element())) {
            getToken(arquivoUsuario); //2ª Chamada do Scanner          
            if (isIdValidation(TOKEN.element())) {
                do {
                    getToken(arquivoUsuario); //3ª Chamada do Scanner	
                    if (TOKEN.element().equalsIgnoreCase("41")) {
                        getToken(arquivoUsuario); //4ª chamada do Scanner          
                        if (!isIdValidation(TOKEN.element())) {
                            System.out.println("erro de validação do token, tipo ID");
                            System.exit(0);
                        }
                    } else if (TOKEN.element().equalsIgnoreCase("40")) {
                        break;
                    } else {
                        System.out.println("erro de validação do token, tipo ;");
                        System.exit(0);
                    }
                } while (!TOKEN.element().equalsIgnoreCase("40"));
            } else {
                System.out.println("erro de validação do token, tipo ID");
                System.exit(0);
            }
        } else {
            System.out.println("erro de validação do token, tipo primitivo: int, float ou char");
            System.exit(0);
        }
    }

    private void getToken(BufferedReader arquivoUsuario) throws IOException {//Pegar o proximo token e identificador do arquivo com Scanner;
        TOKEN = SCANNER.scan(arquivoUsuario);
    }

    private boolean isIdValidation(String type) {//Metodo para validação de identificadores.
        if (type.equalsIgnoreCase("10")) { //identificadores(variaveis) = 10;
            return true;
        }
        return false;
    }

    private boolean isVariableTypeValidation(String type) {//Metodo para validação dos possiveis tipos para variaveis nesta linguagem. 
        if (type.equalsIgnoreCase("56") || type.equalsIgnoreCase("57") || type.equalsIgnoreCase("58")) {//int = 56; float = 57; char = 58;
            return true;
        } else {
            return false;
        }
    }

    private void programSyntax(BufferedReader arquivoUsuario) throws IOException {
        getToken(arquivoUsuario); //1ª Chamada do Scanner 
        if (TOKEN.element().equalsIgnoreCase("56")) {// 56 é o codigo do int
            getToken(arquivoUsuario); //2ª Chamada do Scanner 
            if (TOKEN.element().equalsIgnoreCase("50")) {//50 é o codigo do main
                getToken(arquivoUsuario); //3ª Chamada do Scanner 
                if (isOpeningParentheses(TOKEN.element())) {
                    getToken(arquivoUsuario); //4ª Chamada do Scanner 
                    if (isClosingParentheses(TOKEN.element())) {
                        return;
                    } else {
                        System.out.println("erro de validação do token, tipo )");
                        System.exit(0);
                    }
                } else {
                    System.out.println("erro de validação do token, tipo (");
                    System.exit(0);
                }
            } else {
                System.out.println("erro de validação do token, tipo main");
                System.exit(0);
            }
        } else {
            System.out.println("erro de validação do token, tipo int");
            System.exit(0);
        }
    }

    private boolean isOpeningParentheses(String value) { //Metodo para verificação do codigo do parenteses de abertura
        if (value.equalsIgnoreCase("44")) { // 44 = (
            return true;
        }
        return false;
    }

    private boolean isClosingParentheses(String value) { //Metodo para verificação do codigo do parenteses de fechamento
        if (value.equalsIgnoreCase("45")) { // 45 = )
            return true;
        }
        return false;
    }

 
    private void assignment(BufferedReader arquivoUsuario) throws IOException {
        getToken(arquivoUsuario); //1ª chamada Scanner
        if (isVariableTypeValidation(TOKEN.element())) {
            getToken(arquivoUsuario); //2ª chamada Scanner
            if (TOKEN.element().equalsIgnoreCase("30")) { //Verificação do token para saber se ele é o igual. O codigo 30 corresponde ao simoblo de=
                arithmeticExpression(arquivoUsuario);
                getToken(arquivoUsuario);//3ª chamada Scanner
                if (TOKEN.element().equalsIgnoreCase("40")) {
                    return;
                } else {
                    System.out.println("erro de validação do token, tipo ;");
                    System.exit(0);
                }
            } else {
                System.out.println("erro de validação do token, tipo =");
                System.exit(0);
            }
        } else {
            System.out.println("erro de validação do token, tipo ID");
            System.exit(0);
        }
    }

    private void arithmeticExpression(BufferedReader arquivoUsuario) throws IOException {
        while (true) {
            termToken(arquivoUsuario);
            getToken(arquivoUsuario);
            if (TOKEN.element().equalsIgnoreCase("31")) {
                arithmeticExpression(arquivoUsuario);
            } else if (TOKEN.element().equalsIgnoreCase("32")) {
                arithmeticExpression(arquivoUsuario);
            } else {
                return;
            }
        }

    }

    private void termToken(BufferedReader arquivoUsuario) throws IOException {
        factorToken(arquivoUsuario);
        do {
            getToken(arquivoUsuario);
            if (TOKEN.isEmpty()) {
                if (isMultiplicationOrDivision(TOKEN.element())) {
                    factorToken(arquivoUsuario);
                } else {
                    return;
                }
            }else{
                return;
            }
        } while (true);
    }

    private boolean isMultiplicationOrDivision(String value) {//Metodo de validação para tokens com simbolo de multiplicação '*' e divisão '/'
        if (value.equalsIgnoreCase("33")) {
            return true;
        } else if (value.equalsIgnoreCase("34")) {
            return true;
        }
        return false;
    }

    private void factorToken(BufferedReader arquivoUsuario) throws IOException {
        getToken(arquivoUsuario);
        if (isOpeningParentheses(TOKEN.element())) {
            arithmeticExpression(arquivoUsuario);
            getToken(arquivoUsuario);
            if (isClosingParentheses(TOKEN.element())) {
                return;
            } else {
                System.out.println("erro de validação do token, tipo )");
                System.exit(0);
            }
        } else if (isIdValidation(TOKEN.element())) {
            System.out.println(TOKEN.element());
            return;
        } else if (isFactorTypeValidation(TOKEN.element())) {
            return;
        } else {
            System.out.println("Erro no tipo do token. O token não é uma expressão aritmetica, um ID, um float, um inteiro ou um char.");
            System.exit(0);
        }
    }

    private boolean isFactorTypeValidation(String value) {
        if (value.equalsIgnoreCase("60")) {
            return true;
        } else if (value.equalsIgnoreCase("70")) {
            return true;
        } else if (value.equalsIgnoreCase("80")) {
            return true;
        } else {
            return false;
        }
    }

    private void printToken() {//Metodo para printar todos os tokens e seus respectivos tipos.
        while (TOKEN.element() != null) {
            System.out.println(TOKEN.removeFirst());
        }
        System.exit(0);
    }
}
