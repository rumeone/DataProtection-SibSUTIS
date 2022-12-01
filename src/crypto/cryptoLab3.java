package crypto;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static crypto.cryptoLab2.toHH;

public class cryptoLab3 {

    public static int[] hashToIntArray(byte[] hash) {
        int[] hashInt = new int[hash.length];
        for (int i = 0; i < hash.length; i++) {
            if (hash[i] < 0) {
                hashInt[i] = hash[i] + 256;
            } else {
                hashInt[i] = hash[i];
            }
        }
        return hashInt;
    }

    public static boolean SignatureRSA() throws Exception {
        Random random = new Random();

        byte[] byteArray = new byte[8];

        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
        byte[] array = Files.readAllBytes(Path.of("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dino.png"));
        byte[] hash = sha512.digest(array);
        var hashInt = hashToIntArray(hash);

        FileOutputStream out = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\DigitalSignatureRSA.png");

        BigInteger P = BigInteger.probablePrime(32, random);
        BigInteger Q = BigInteger.probablePrime(32, random);

        BigInteger N = P.multiply(Q);
        BigInteger f = (P.subtract(BigInteger.ONE)).multiply(Q.subtract(BigInteger.ONE));

        BigInteger d;
        BigInteger c;
        BigInteger signature;
        BigInteger checkSignature;

        do {
            d = new BigInteger(32, random);
        } while (!(d.gcd(f).equals(BigInteger.ONE)) && d.compareTo(f) < 0);


        c = d.modInverse(f);

        List<BigInteger> digitalSignature = new ArrayList<>();
        List<BigInteger> deDigitalSignature = new ArrayList<>();
        List<byte[]> byteList = new ArrayList<>();

        //create a digital signature
        for (int i = 0; i < hash.length; i++) {
            BigInteger y = BigInteger.valueOf(hashInt[i]);

            signature = cryptoLab1.ModPow(y, c, N);
            digitalSignature.add(signature);
            byteArray = toHH(signature); //toHH - representation of a number in 8 byte format
            byteList.add(byteArray);

            out.write(byteList.get(i));
        }



        var fileData = Files.readAllBytes(Path.of("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dino.png"));
        var hashFileData = hashToIntArray(sha512.digest(fileData));

        for (int i = 0; i < hash.length; i++) {

            checkSignature = cryptoLab1.ModPow(digitalSignature.get(i), d, N);
            deDigitalSignature.add(checkSignature);
        }

        for (int i = 0; i < hash.length; i++) {
            if (BigInteger.valueOf(hashFileData[i]).equals(deDigitalSignature.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean SignatureElGamal() throws Exception {
        Random random = new Random();

        byte[] byteArray = new byte[8];

        MessageDigest sha512 = MessageDigest.getInstance("SHA512");
        byte[] array = Files.readAllBytes(Path.of("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dino.png"));
        byte[] hash = sha512.digest(array);
        var hashInt = hashToIntArray(hash);

        FileOutputStream out = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\DigitalSignatureElGamal.png");

        BigInteger p;
        BigInteger q = BigInteger.probablePrime(31, random);
        BigInteger g = BigInteger.probablePrime(31, random);

        do {
            p = BigInteger.probablePrime(31, random);
        } while (p.subtract(BigInteger.ONE).divide(BigInteger.TWO).equals(q));

        q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);

        while (g.compareTo(p.subtract(BigInteger.ONE)) > 0 || (cryptoLab1.ModPow(g, q, p).compareTo(BigInteger.ONE) == 0)) {
            g = BigInteger.probablePrime(31, random);
        }

        BigInteger x;

        do {
            x = new BigInteger(31, random);
        } while (x.compareTo(p.divide(BigInteger.ONE)) > 0);

        BigInteger y = cryptoLab1.ModPow(g, x, p);

        BigInteger k;
        BigInteger invK;
        BigInteger r;
        BigInteger u;
        BigInteger s;

        List<BigInteger> digitalSignature = new ArrayList<>();
        List<byte[]> byteList = new ArrayList<>();

        do {
            k = new BigInteger(31, random);
        } while (k.compareTo(p.subtract(BigInteger.ONE)) > 0 || !(k.gcd(p.subtract(BigInteger.ONE)).equals(BigInteger.ONE)));

        r = cryptoLab1.ModPow(g, k, p);
        invK = k.modInverse(p.subtract(BigInteger.ONE));

        for (int i = 0; i < hashInt.length; i++) {
            u = (BigInteger.valueOf(hashInt[i]).subtract(x.multiply(r))).mod(p.subtract(BigInteger.ONE));
            s = (invK.multiply(u)).mod(p.subtract(BigInteger.ONE));
            digitalSignature.add(s);
            BigInteger a = ((cryptoLab1.ModPow(y, r, p)).multiply(cryptoLab1.ModPow(r, s, p))).mod(p);
            BigInteger b = cryptoLab1.ModPow(g, BigInteger.valueOf(hashInt[i]), p);
            byteArray = toHH(s);
            byteList.add(byteArray);

            out.write(byteList.get(i));
        }



        var fileData = Files.readAllBytes(Path.of("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dino.png"));
        var hashIntSignature = hashToIntArray(sha512.digest(fileData));
        var dataSignatureInt = hashToIntArray(Files.readAllBytes(Path.of("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\DigitalSignatureElGamal.png")));

        for (int i = 0; i < dataSignatureInt.length; i += 8) {
            BigInteger value = BigInteger.valueOf(dataSignatureInt[i]);

            for (int j = 0; j < 8; j++) {
                value = BigInteger.valueOf((value.longValue() << 8) | dataSignatureInt[i + j]);
            }

            BigInteger a = ((cryptoLab1.ModPow(y, r, p)).multiply(cryptoLab1.ModPow(r, value, p))).mod(p);
            BigInteger b = cryptoLab1.ModPow(g, BigInteger.valueOf(hashIntSignature[i / 8]), p);

            if (!a.equals(b)) {
                return false;
            }
        }
        return true;
    }

    public static boolean GOST() throws Exception {
        Random random = new Random();

        BigInteger q = BigInteger.probablePrime(255, random);
        BigInteger b = new BigInteger(768, random);
        BigInteger p = b.multiply(q).subtract(BigInteger.ONE);
        BigInteger a = null;
        BigInteger x;
        BigInteger y;
        BigInteger g = null;

        do {
            g = new BigInteger(15, random);
            a = g.modPow(b, p);
        } while((a.modPow(q, p)).compareTo(BigInteger.ONE) != 0 || g.compareTo(p.subtract(BigInteger.ONE)) > 0);

        do {
            x = BigInteger.probablePrime(255, random);
        } while (x.compareTo(q) > 0);

        y = cryptoLab1.ModPow(a, x, p);
        BigInteger yb = a.modPow(x, p);

        byte[] byteArray = new byte[8];

        MessageDigest sha512 = MessageDigest.getInstance("SHA512");
        byte[] array = Files.readAllBytes(Path.of("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\test.png"));
        byte[] hash = sha512.digest(array);
        var hashInt = hashToIntArray(hash);

        FileOutputStream out = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\DigitalSignatureGOST.png");
        BigInteger u1,u2,v,invH;
        BigInteger k, r, s = null;
        List<BigInteger> digitalSignature = new ArrayList<>();
        List<byte[]> byteList = new ArrayList<>();
        do {
            do {
                k = new BigInteger(255, random);
                r = (a.modPow(k, p)).mod(q);
            } while (k.compareTo(q) > 0 || r.compareTo(BigInteger.ZERO) == 0);
            for(int i = 0; i < hashInt.length; i++) {
                s = (k.multiply(BigInteger.valueOf(hashInt[i])).add(x.multiply(r))).mod(q);
                if(s.equals(BigInteger.ZERO)) {
                    break;
                }
                invH = (BigInteger.valueOf(hashInt[i])).modInverse(q);
                u1 = (s.multiply(invH)).mod(q);
                u2 = (r.negate().multiply(invH)).mod(q);
                v = ((a.modPow(u1,p).multiply(y.modPow(u2, p))).mod(p)).mod(q);

                if(!v.equals(r)) {
                    return false;
                }
            }
        } while (Objects.equals(s, BigInteger.ZERO));
                return true;
            }
}
