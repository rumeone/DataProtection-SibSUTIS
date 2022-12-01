package com.company;

import crypto.cryptoLab5.Client;
import crypto.cryptoLab5.Server;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args) throws Exception {
        crypto.cryptoLab5.Server sever = new Server();
        crypto.cryptoLab5.Client client = new Client(sever.getN(), sever.getD());
        crypto.cryptoLab5.Client client1 = new Client(sever.getN(), sever.getD());

        BigInteger h1 = client.getH1();
        BigInteger h2 = client1.getH1();
        if(sever.voteCheck(client.getMessage()) && sever.voteCheck(client.getMessage())) {
            client.createSignature(sever.createBulletins(h1), sever.getN());
            client1.createSignature(sever.createBulletins(h2), sever.getN());
        }
        if(sever.checkBulletins(client.getSignature(), client.getMessage()) && sever.checkBulletins(client1.getSignature(), client1.getMessage())) {
            System.out.println("Бюллетень действительна!");
        }
        sever.printResult();
    }
}

