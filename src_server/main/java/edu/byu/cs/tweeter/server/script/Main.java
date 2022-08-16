package edu.byu.cs.tweeter.server.script;

import edu.byu.cs.tweeter.server.dao.DynamoDbDAO;

/*
 In computer programming, a script is a program or sequence of
 instructions that is interpreted or carried out by another
 program rather than by the computer processor (as a compiled program is).
*/
public class Main {
    public static void main(String[] args) {
        //If you have your AWS credentials set up on your computer, you can run the code from your IDE.
        Filler filler = new Filler(new DynamoDbDAO());
        //filler.fillDatabase();
    }
}
