package crypto.cryptoLab5;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;

import static crypto.cryptoLab4.genCoPrime;

public class Server {

    private BigInteger P,Q,N,f;
    private final BigInteger C;
    private final BigInteger D;
    private long votesYes, votesNo, notValid;

    private final ArrayList<BigInteger> bulletins = new ArrayList<>();
    private ArrayList<BigInteger> votedClient = new ArrayList<>();

    public Server() {
        Random random = new Random();

        do {
            P = BigInteger.probablePrime(1024, random);
            Q = BigInteger.probablePrime(1024, random);
        } while (P.equals(Q));

        N = P.multiply(Q);
        f = (P.subtract(BigInteger.ONE)).multiply(Q.subtract(BigInteger.ONE));


        D = genCoPrime(f, 1024);
        C = D.modInverse(f);


    }

    public BigInteger getN() {
        return N;
    }

    public BigInteger getD() {
        return D;
    }


    public BigInteger getBulletins(int index) {
        return bulletins.get(index);
    }

    public boolean voteCheck(BigInteger m) {

        if(votedClient.isEmpty()) {
            return true;
        }

        return !votedClient.contains(m);
    }

    public BigInteger createBulletins(BigInteger h1) throws Exception {
        votedClient.add(h1);
        return h1.modPow(C,N);
    }

    public boolean checkBulletins(BigInteger signature, BigInteger message) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA3-256");
        byte[] hash = sha256.digest(message.toByteArray());
        BigInteger buffer = new BigInteger(1, hash);
        if(!buffer.equals(signature.modPow(D,N))) {
            notValid++;
            return false;
        }

        if(message.getLowestSetBit() == 0) {
            votesYes++;
        } else {
            votesNo++;
        }
        bulletins.add(signature);
        return true;
    }

    public void printResult() {
        System.out.println("Голосов за : " + votesYes);
        System.out.println("Голосов против : " + votesNo);
    }

}