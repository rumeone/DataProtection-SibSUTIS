package crypto.cryptoLab5;

import crypto.cryptoLab1;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import static crypto.cryptoLab4.genCoPrime;

public class Client {

    private BigInteger rnd;
    private BigInteger vote; // client vote
    private BigInteger m; // rnd | vote
    private BigInteger r; // coPrime(N);
    private BigInteger inv_r; // inv r;
    private BigInteger h,h1; //хеш
    private BigInteger signature; // voter's signature

    public Client(BigInteger N, BigInteger D) throws Exception {
        Random random = new Random();
        MessageDigest sha512 = MessageDigest.getInstance("SHA3-256");

        rnd = new BigInteger(512, random);
        vote = new BigInteger(1, random);
        m = (rnd.shiftLeft(512)).or(vote);
        r = genCoPrime(N, 1024);

        byte[] hash = sha512.digest(m.toByteArray());
        h = new BigInteger(1, hash);
        h1 = (h.multiply(r.modPow(D,N)).mod(N));


    }

    public BigInteger getH1() {
        return h1;
    }

    public void createSignature(BigInteger bulletins, BigInteger N) {
        inv_r = r.modInverse(N);

        signature = (bulletins.multiply(inv_r)).mod(N);
    }

    public BigInteger getSignature() {
        return signature;
    }

    public BigInteger getMessage() {
        return m;
    }
}

