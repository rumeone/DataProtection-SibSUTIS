package crypto;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class cryptoLab1 {

    //y = a^x mod p
    public static BigInteger ModPow(BigInteger a, BigInteger x, BigInteger p) throws Exception {
        if(x.compareTo(BigInteger.ZERO) == 0) {
            return BigInteger.valueOf(1);
        }

        var t = Integer.toString(x.intValue(), 2);
        var result = BigInteger.valueOf(1);

        for (int i = t.length() - 1; i >= 0; i--) {
            if(t.charAt(i) == '1') {
                result = (result.multiply(a)).mod(p);
            }
            a = (a.multiply(a)).mod(p);
        }

        return result;
    }

    public static ArrayList<BigInteger> Euclid(BigInteger a, BigInteger b) {
        ArrayList<BigInteger> value1 = null;
        ArrayList<BigInteger> value2;

        if(a.compareTo(b) > 0) {
            value1 = new ArrayList<>(Arrays.asList(
                    a,
                    BigInteger.valueOf(1),
                    BigInteger.valueOf(0)
            ));

            value2 = new ArrayList<>(Arrays.asList(
                    b,
                    BigInteger.valueOf(0),
                    BigInteger.valueOf(1)
            ));

            while(value2.get(0).compareTo(BigInteger.valueOf(0)) > 0) {
                BigInteger q = value1.get(0).divide(value2.get(0));
                ArrayList<BigInteger> temp = new ArrayList<>(Arrays.asList(
                        value1.get(0).mod(value2.get(0)),
                        value1.get(1).subtract(q.multiply(value2.get(1))),
                        value1.get(2).subtract(q.multiply(value2.get(2)))
                ));
                value1 = value2;
                value2 = temp;
            }
        }
        return value1;
    }

    public static boolean DiffieHellman() throws Exception {
        Random random = new Random();

        BigInteger p;
        BigInteger q = BigInteger.probablePrime(10, random);
        BigInteger g = BigInteger.probablePrime(10, random);

        do {
            p = BigInteger.probablePrime(10, random);
        } while (p.subtract(BigInteger.ONE).divide(BigInteger.TWO).equals(q));

        q = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);

        while(g.compareTo(p.subtract(BigInteger.ONE)) > 0 || (ModPow(g,q,p).compareTo(BigInteger.ONE) == 0)) {
            g = BigInteger.probablePrime(10,random);
        }


        BigInteger Xa = new BigInteger(10, random);
        BigInteger Xb = new BigInteger(10, random);

        BigInteger Ya = ModPow(g, Xa, p);
        BigInteger Yb = ModPow(g, Xb, p);

        BigInteger Zab = ModPow(Yb, Xa, p);
        BigInteger Zba = ModPow(Ya,Xb, p);

        if(Zab.equals(Zba)) {
            System.out.println("It works, Zab = " + Zab + " Zba = " + Zba);
            System.out.println("p = " + p + " q = " + q + " g = " + g);
            return true;
        }


        return false;
    }

    public static BigInteger Shanks() {

        Random random = new Random();
        // m and k = sqrt(p) + 1

        BigInteger p = BigInteger.probablePrime(10, random);
        //BigInteger p = new BigInteger("17");

        BigInteger m = p.sqrt().add(BigInteger.ONE);
        BigInteger k = p.sqrt().add(BigInteger.ONE);


        BigInteger a = BigInteger.probablePrime(10, random);
        BigInteger y = BigInteger.probablePrime(10, random);
        /*BigInteger a = new BigInteger("5");
        BigInteger y = new BigInteger("12");*/

        while(a.compareTo(p) > 0 || y.compareTo(p) > 0) {
            a = BigInteger.probablePrime(5, random);
            y = BigInteger.probablePrime(5, random);
        }

        System.out.println("a = " + a + " p = " + p + " y = " + y);

        ArrayList<BigInteger> array1 = new ArrayList<>();

        for(BigInteger i = BigInteger.ZERO; i.compareTo(m) < 0; i = i.add(BigInteger.ONE)) {
            BigInteger temp = y.multiply(a.pow(i.intValue())).mod(p);
            array1.add(temp);
        }

        for(BigInteger j = BigInteger.ONE; j.compareTo(k) < 0; j = j.add(BigInteger.ONE)) {
            BigInteger temp = a.pow(j.multiply(m).intValue()).mod(p);
            for(int h = 0; h < array1.size(); h++) {
                if(Objects.equals(array1.get(h), temp)) {
                    return m.multiply(j).subtract(BigInteger.valueOf(h));
                }
            }
        }

        return new BigInteger("-1");
    }

}
