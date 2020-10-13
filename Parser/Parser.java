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

    private Deque<String> token;
    private Scanner scanner;
    private char currentChar;

    public Parser(Scanner arquivo) {
        this.token = new ArrayDeque<String>();
        this.scanner = arquivo;
        this.currentChar = ' ';
    }

    public void runningParser() throws IOException {
        //variableDeclaration();
        programSyntax();
    }

    private void getHead() {
        this.currentChar = this.scanner.getHead();
    }

    private void getToken() throws IOException {
        this.token = this.scanner.scan();
    }

    private void programSyntax() throws IOException {
        getHead();
        //1ª Chamada do Scanner 
        if (Character.isLetter(this.currentChar)) {
            getToken();
            if (this.token.element().equalsIgnoreCase("56")) {// 56 é o codigo do int
                getHead();
                if (Character.isLetter(this.currentChar)) {
                    getToken();
                    if (this.token.element().equalsIgnoreCase("50")) {//50 é o codigo do main
                        getHead();
                        if (isOpeningParentheses(this.currentChar)) {
                            getToken();
                            getHead();
                            if (isClosingParentheses(this.currentChar)) {
                                getToken();
                                getHead();
                                if (!codeBlock()) {
                                    characterInvalid("Tipo do Token esperado: '{'");
                                }
                            } else {
                                characterInvalid("Tipo do Token esperado: ')'");
                            }
                        } else {
                            characterInvalid("Tipo do Token esperado: '('");
                        }
                    } else {
                        characterInvalid("token esperado: 'main'");
                    }
                } else {
                    characterInvalid("token esparado: main");
                }
            } else {
                characterInvalid("token esparado: int");
            }
        } else {
            characterInvalid("token esparado: int");
        }
    }

    private boolean codeBlock() throws IOException {
        if (this.currentChar == '{') {
            getToken();
            boolean aux;
            do {
                aux = variableDeclaration();
            } while (aux);
            do {
                aux = codeCommand();
            } while (aux);
            getToken();
            if (this.currentChar == '}') {
                return true;
            } else {
                characterInvalid("Tipo do token esperado: '}'");
            }
        }else {
            return false;
        }
        return false;
    }

    private boolean variableDeclaration() throws IOException {
        getHead();
        if (isCharacterOrDigit()) {
            getToken();
            if (isVariableTypeValidation(this.token.element())) {
                getHead();
                if (isCharacterOrDigit() || this.currentChar == 95) {
                    getToken();
                    if (isIdValidation(this.token.element())) {
                        while (true) {
                            getHead();
                            if (this.currentChar == ',') {
                                getToken();
                                getHead();
                                if (isCharacterOrDigit() || this.currentChar == 95) {
                                    getToken();
                                } else {
                                    characterInvalid("Token esperado: 'ID'");
                                }
                            } else {
                                break;
                            }
                        }
                        if (this.currentChar == ';') {
                            getToken();
                            return true;
                        } else {
                            characterInvalid("Token esperado: ';'");
                        }
                    } else {
                        characterInvalid("Token esperado: 'ID'");
                    }
                } else {
                    characterInvalid("Token esperado: 'ID'");
                }
            } else if (isTokenReserved()) {
                return false;
            } else if (isIdValidation(this.token.element())) {
                return false;
            } else {
                characterInvalid("Token esperado: int, float ou char");
            }
        }
        return false;
    }

    private boolean isCharacterOrDigit() {
        if (Character.isLetterOrDigit(this.currentChar)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isTokenReserved() {
        switch (this.token.element()) {
            case "50":
            case "51":
            case "52":
            case "53":
            case "54":
            case "55":
            case "56":
            case "57":
            case "58":
                return true;
            default:
                return false;

        }
    }

    private boolean codeCommand() throws IOException {
        getHead();
        if (basicCodeCommand()) {
            return true;
        } else if (iterationCode()) {
            return true;
        } else if (this.token.element().equalsIgnoreCase("51")) { // Entra neste caso, caso o token seja um "if"
            getHead();
            if (isOpeningParentheses(this.currentChar)) {  // O proximo caracterer, obrigatoriamente, tem que ser um '('
                getToken();
                relationalExpression();
                getHead();
                if (isClosingParentheses(this.currentChar)) { //Ao sair da expressão relacional, proximo caracter tem que ser ')'
                    getToken();
                    codeCommand();
                    if (this.token.element().equalsIgnoreCase("52")) {
                        codeCommand();
                    }
                    return true;
                } else {
                    characterInvalid("Tipo do token no final da expressão condigional: ')'");
                }
            } else {
                characterInvalid("Tipo do token no inicio da expressão condigional: '('");
            }
        }
        return false;
    }

    private boolean basicCodeCommand() throws IOException {
        if (assignment()) {
            return true;
        } else if (codeBlock()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean iterationCode() throws IOException {
        if (this.token.element().equalsIgnoreCase("53")) {
            getHead();
            if (isOpeningParentheses(this.currentChar)) {
                getToken();
                relationalExpression();
                getHead();
                if (isClosingParentheses(this.currentChar)) {
                    getToken();
                    codeCommand();
                    return true;
                } else {
                    characterInvalid("Tipo do token no final da expressão condigional: ')'");
                }
            } else {
                characterInvalid("Tipo do token no inicio da expressão condigional: '('");
            }
        } else if (this.token.element().equalsIgnoreCase("54")) {
            codeCommand();
            if (this.token.element().equalsIgnoreCase("53")) {
                getHead();
                if (isOpeningParentheses(this.currentChar)) {
                    getToken();
                    relationalExpression();
                    getHead();
                    if (isClosingParentheses(this.currentChar)) {
                        getToken();
                        getHead();
                        if (this.currentChar == ';') {
                            return true;
                        } else {
                            characterInvalid("Tipo do token no final da expressão condigional: ';'");
                        }
                    } else {
                        characterInvalid("Tipo do token no final da expressão condigional: ')'");
                    }
                } else {
                    characterInvalid("Tipo do token no inicio da expressão condigional: '('");
                }
            }
        }
        return false;
    }

    private boolean assignment() throws IOException {
        if (Character.isLetter(this.currentChar) || this.currentChar == 95) {
            getToken();
            if (isIdValidation(token.element())) {
                getHead();
                if (this.currentChar == '=') { //Verificação do token para saber se ele é o igual. O codigo 30 corresponde ao simoblo de=
                    getToken();
                    if (token.element().equalsIgnoreCase("30")) {
                        arithmeticExpression();
                        getHead();
                        if (this.currentChar == ';') {
                            getToken();
                            return true;
                        } else {
                            characterInvalid("token esperado: ';'");
                        }
                    } else {
                        characterInvalid("token esperado: '='");
                    }
                } else {
                    characterInvalid("token esperado: '='");
                }
            } else if (isTokenReserved()) {
                return false;
            } else {
                characterInvalid("token esperado: 'ID'");
            }
        }
        return false;
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

    private boolean isOpeningParentheses(char value) { //Metodo para verificação do codigo do parenteses de abertura
        if (value == '(') { // 44 = (
            return true;
        }
        return false;
    }

    private boolean isClosingParentheses(char value) { //Metodo para verificação do codigo do parenteses de fechamento
        if (value == ')') {
            return true;
        }
        return false;
    }

    private void relationalExpression() throws IOException {
        arithmeticExpression();
        if (isFirstPartRelationalExpression()) {
            getToken();
            if (isRelationalExpression()) {
                arithmeticExpression();
            } else {
                characterInvalid("token esparado: '==' ou '>=' ou '>' ou '<' ou '<=' ou '!='");
            }
        } else {
            characterInvalid("token esparado: '=' ou '>' ou '<' ou '!' ");
        }
    }

    private boolean isFirstPartRelationalExpression() {
        if (!this.token.isEmpty()) {
            switch (this.currentChar) {
                case '=':
                case '>':
                case '<':
                case '!':
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private boolean isRelationalExpression() {
        if (!this.token.isEmpty()) {
            switch (this.token.element()) {
                case "20":
                case "21":
                case "22":
                case "23":
                case "24":
                case "25":
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private void arithmeticExpression() throws IOException {
        getHead();
        do {
            if (this.currentChar == '+') {
                getToken();
                arithmeticExpression();
            } else if (this.currentChar == '-') {
                getToken();
                arithmeticExpression();
            } else if (this.currentChar == 65535) {
                return;
            } else {
                termToken();
            }
        } while (this.currentChar == '+' || this.currentChar == '-');
    }

    private void termToken() throws IOException {
        factorToken();
        while (this.currentChar == '*' || this.currentChar == '/') {
            getToken();
            getHead();
            factorToken();
        }
    }

    private void factorToken() throws IOException {
        if (Character.isLetterOrDigit(this.currentChar) || this.currentChar == 39 || this.currentChar == '.') {
            getToken();
        } else if (this.currentChar == '(') {
            getToken();
            arithmeticExpression();
            if (this.currentChar == ')') {
                getToken();
            } else {
                characterInvalid("esperado: ')'");
            }
        } else {
            characterInvalid("não é ID, FLOAT, INTEIRO, CHAR, '(' OU ')'");
        }
        getHead();
    }

    private void characterInvalid(String mensagem) {
        System.err.println("ERRO na linha " + this.scanner.getLine() + ", coluna " + this.scanner.getColumn() + "\nUltimo token lido:  [Tipo = " + this.token.remove() + "],[Token = '" + this.token.remove() + "' ] \n" + mensagem);
        System.exit(0);
    }
}
