package crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class cryptoLab2 {

    public static int[] ReadFileToBinary(String path) throws IOException {
        File file = new File(path);
        byte[] fileData = new byte[(int)file.length()];
        FileInputStream in = new FileInputStream(file);
        in.read(fileData);
        int[] arrayBytes= new int[(int) file.length()];

        for(int i = 0; i < fileData.length; i++) {
            arrayBytes[i] = Byte.toUnsignedInt(fileData[i]);
        }
        in.close();

        return arrayBytes;
    }

    public static byte[] toHH(BigInteger n) {
        byte[] b = new byte[8];
        b[7] = (byte) (n.longValue()  & 0xff);
        b[6] = (byte) (n.longValue() >> 8 & 0xff);
        b[5] = (byte) (n.longValue() >> 16 & 0xff);
        b[4] = (byte) (n.longValue() >> 24 & 0xff);
        b[3] = (byte) (n.longValue() >> 32 & 0xff);
        b[2] = (byte) (n.longValue() >> 40 & 0xff);
        b[1] = (byte) (n.longValue() >> 48 & 0xff);
        b[0] = (byte) (n.longValue() >> 56 & 0xff);
        return b;
    }

    public static void Shamir() throws Exception {
        Random random = new Random();

        var fileData = ReadFileToBinary("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dino.png");

        BigInteger p = BigInteger.probablePrime(31, random);

        BigInteger Ca;
        BigInteger Da;
        BigInteger Cb;
        BigInteger Db;

        BigInteger encrypt;
        BigInteger decrypt;

        List<BigInteger> encryptList = new ArrayList<>();
        byte[] byteArray = new byte[8];
        List<byte[]> byteList = new ArrayList<>();

        do {
            Ca = new BigInteger(31, random);
            Da = new BigInteger(31, random);
        } while((Ca.longValue() * Da.longValue()) % (p.longValue() - 1) != 1
                || Ca.compareTo(p.subtract(BigInteger.ONE)) > 0
                || Da.compareTo(p.subtract(BigInteger.ONE)) > 0);

        System.out.println((Ca.multiply(Da).mod(p.subtract(BigInteger.ONE))));

        do {
            Cb = new BigInteger(31, random);
            Db = new BigInteger(31, random);
        } while((Cb.intValue() * Db.intValue()) % (p.intValue() - 1) != 1
                || Cb.compareTo(p.subtract(BigInteger.ONE)) > 0
                || Db.compareTo(p.subtract(BigInteger.ONE)) > 0);


        FileOutputStream out = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\endinoShamir.png");
        FileOutputStream out2 = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dedinoShamir.png");


        for (int i = 0; i < fileData.length; i++) {
            BigInteger message = BigInteger.valueOf(fileData[i]);

            BigInteger x1 = cryptoLab1.ModPow(message,Ca, p);
            encrypt = cryptoLab1.ModPow(x1, Cb, p);

            encryptList.add(encrypt);
            byteArray = toHH(encrypt);
            byteList.add(byteArray);

            out.write(byteList.get(i));
        }

        var fileData2 = ReadFileToBinary("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\endinoShamir.png");

        for(int i = 0; i < fileData2.length; i+=8) {
            decrypt = BigInteger.valueOf(fileData2[i]);

            for(int j = 0; j < 8; j++) {
                decrypt = BigInteger.valueOf((decrypt.longValue() << 8) | fileData2[i + j]);
            }


            BigInteger x3 = cryptoLab1.ModPow(decrypt, Da, p);
            BigInteger decrypted = cryptoLab1.ModPow(x3, Db, p);

            if(decrypted.intValue() > 127) {
                decrypted = decrypted.subtract(BigInteger.valueOf(256));
                out2.write(decrypted.toByteArray());
            }
            else {
                out2.write(decrypted.toByteArray());
            }
        }
    }

    public static void ElGamal() throws Exception {
        Random random = new Random();

        var fileData = ReadFileToBinary("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dino.png");

        BigInteger p;
        BigInteger q = BigInteger.probablePrime(16, random);
        BigInteger g = BigInteger.probablePrime(16, random);

        do {
            p = BigInteger.probablePrime(16, random);
        } while (p.subtract(BigInteger.ONE).divide(BigInteger.TWO).equals(q));

        q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);

        while(g.compareTo(p.subtract(BigInteger.ONE)) > 0 || (cryptoLab1.ModPow(g,q,p).compareTo(BigInteger.ONE) == 0)) {
            g = BigInteger.probablePrime(16,random);
        }

        BigInteger Cb;

        do {
            Cb = new BigInteger(16, random);
        } while (Cb.compareTo(p.divide(BigInteger.ONE)) > 0);

        BigInteger Db = cryptoLab1.ModPow(g,Cb,p);

        BigInteger k;

        do {
            k = new BigInteger(16, random);
        } while (k.compareTo(p.subtract(BigInteger.TWO)) > 0);

        List<BigInteger> encryptList = new ArrayList<>();
        byte[] byteArray = new byte[8];
        List<byte[]> byteList = new ArrayList<>();
        BigInteger encrypt;
        BigInteger decrypt;

        FileOutputStream out = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\endinoElGamal.png");
        FileOutputStream out2 = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dedinoElGamal.png");

        BigInteger r = cryptoLab1.ModPow(g,k,p);

        for(int i = 0; i < fileData.length; i++) {
            BigInteger message = BigInteger.valueOf(fileData[i]);

            encrypt = message.multiply(cryptoLab1.ModPow(Db, k, p)).mod(p);

            encryptList.add(encrypt);
            byteArray = toHH(encrypt);
            byteList.add(byteArray);

            out.write(byteList.get(i));
        }

        var fileData2 = ReadFileToBinary("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\endinoElGamal.png");

        for(int i = 0; i < fileData2.length; i+=8) {
            BigInteger decrypted;
            decrypt = BigInteger.valueOf(fileData2[i]);

            for(int j = 0; j < 8; j++) {
                decrypt = BigInteger.valueOf((decrypt.longValue() << 8) | fileData2[i + j]);
            }

            decrypted = decrypt.multiply(cryptoLab1.ModPow(r, p.subtract(BigInteger.ONE).subtract(Cb), p)).mod(p);
            if(decrypted.intValue() > 127) {
                decrypted = decrypted.subtract(BigInteger.valueOf(256));
                out2.write(decrypted.toByteArray());
            }
            else {
                out2.write(decrypted.toByteArray());
            }
        }
    }
    public static void Vernam() throws IOException {
        Random random = new Random();

        var fileData = ReadFileToBinary("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dino.png");

        BigInteger[] k = new BigInteger[fileData.length];
        BigInteger[] encrypt = new BigInteger[fileData.length];
        BigInteger[] decrypt = new BigInteger[fileData.length];

        List<BigInteger> encryptList = new ArrayList<>();

        for (int i = 0; i < fileData.length; i++) {
            k[i] = new BigInteger(8, random);
        }

        FileOutputStream out = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\endinoVernam.png");
        FileOutputStream out2 = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dedinoVernam.png");


        for (int i = 0; i < fileData.length; i++) {
            encrypt[i] = BigInteger.valueOf(fileData[i] ^ k[i].intValue());
            if (encrypt[i].intValue() > 127) {
                encrypt[i] = encrypt[i].subtract(BigInteger.valueOf(256));
                out.write(encrypt[i].toByteArray());
            } else {
                out.write(encrypt[i].toByteArray());
            }
            encryptList.add(encrypt[i]);
        }

        var fileData2 = ReadFileToBinary("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\endinoVernam.png");


        for(int i = 0; i < fileData2.length; i++) {
            BigInteger decrypted = BigInteger.valueOf(fileData2[i]);

            decrypt[i] = decrypted.xor(k[i]);
            if (decrypt[i].intValue() > 127) {
                decrypt[i] = decrypt[i].subtract(BigInteger.valueOf(256));
                out2.write(decrypt[i].toByteArray());
            } else {
                out2.write(decrypt[i].toByteArray());
            }
        }
    }
    public static void RSA() throws Exception {
        Random random = new Random();

        var fileData = ReadFileToBinary("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dino.png");

        FileOutputStream out = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\endinoRSA.png");
        FileOutputStream out2 = new FileOutputStream("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\dedinoRSA.png");

        BigInteger P = BigInteger.probablePrime(16, random);
        BigInteger Q = BigInteger.probablePrime(16, random);

        BigInteger N = P.multiply(Q);

        BigInteger f = (P.subtract(BigInteger.ONE)).multiply(Q.subtract(BigInteger.ONE));

        BigInteger d;
        BigInteger c;

        BigInteger encrypt;
        BigInteger decrypt;

        List<BigInteger> encryptList = new ArrayList<>();
        byte[] byteArray = new byte[8];
        List<byte[]> byteList = new ArrayList<>();

        do {
            d = new BigInteger(16, random);
        } while (!(d.gcd(f).equals(BigInteger.ONE)) && d.compareTo(f) < 0);


        c = d.modInverse(f);

        for(int i = 0; i < fileData.length; i++) {
            BigInteger message = BigInteger.valueOf(fileData[i]);

            encrypt = cryptoLab1.ModPow(message, d, N);

            encryptList.add(encrypt);
            byteArray = toHH(encrypt);
            byteList.add(byteArray);

            out.write(byteList.get(i));
        }

        var fileData2 = ReadFileToBinary("C:\\Users\\User\\IdeaProjects\\CryptoLab1\\src\\crypto\\endinoRSA.png");

        for(int i = 0; i < fileData2.length; i+=8) {
            BigInteger decrypted;
            decrypt = BigInteger.valueOf(fileData2[i]);

            for(int j = 0; j < 8; j++) {
                decrypt = BigInteger.valueOf((decrypt.longValue() << 8) | fileData2[i + j]);
            }

            decrypted = cryptoLab1.ModPow(decrypt, c, N);

            if(decrypted.intValue() > 127) {
                decrypted = decrypted.subtract(BigInteger.valueOf(256));
                out2.write(decrypted.toByteArray());
            }
            else {
                out2.write(decrypted.toByteArray());
            }
        }
    }
}
