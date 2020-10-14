/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Lucas Fernando
 */
public class Compilador {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        // abertura do arquivo
        BufferedReader myBuffer = new BufferedReader (new InputStreamReader(
                new FileInputStream("C:\\Users\\Lucas Fernando\\Desktop\\teste.txt"), "UTF-8"));
        Scanner x = new Scanner(myBuffer);
        Parser y = new Parser(x); 
        y.runningParser();
    }
}

