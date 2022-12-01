package crypto;
import java.math.BigInteger;
import java.util.*;
import static java.util.Map.entry;

public class cryptoLab4 {

    public static BigInteger genCoPrime(BigInteger value, int numBits) {
        BigInteger coPrime;

        do{
            coPrime = new BigInteger(numBits, new Random());
        } while (value.gcd(coPrime).longValue() != 1);

        return coPrime;
    }

    public static void printCardsPerPlayer(int players, ArrayList<ArrayList<BigInteger>> cards, int ccp) {
        for(int i = 0; i < players; i++) {
            System.out.print("Игрок " + i + " имеет карты: ");
            for(int j = 0; j < ccp; j++) {
                System.out.print(" " + deck.get(cards.get(i).get(j).intValue()));
            }
            System.out.println();
        }
    }

    static Map<Integer, String> deck = Map.<Integer, String>ofEntries(
            entry(2, "2S"), entry(3, "2H"), entry(4, "2D"), entry(5, "2C"),
            entry(6, "3S"), entry(7, "3H"), entry(8, "3D"), entry(9, "3C"),
            entry(10, "4S"), entry(11, "4H"), entry(12, "4D"), entry(13, "4C"),
            entry(14, "5S"), entry(15, "5H"), entry(16, "5D"), entry(17, "5C"),
            entry(18, "6S"), entry(19, "6H"), entry(20, "6D"), entry(21, "6C"),
            entry(22, "7S"), entry(23, "7H"), entry(24, "7D"), entry(25, "7C"),
            entry(26, "8S"), entry(27, "8H"), entry(28, "8D"), entry(29, "8C"),
            entry(30, "9S"), entry(31, "9H"), entry(32, "9D"), entry(33, "9C"),
            entry(34, "10S"), entry(35, "10H"), entry(36, "10D"), entry(37, "10C"),
            entry(38, "JS"), entry(39, "JH"), entry(40, "JD"), entry(41, "JC"),
            entry(42, "QS"), entry(43, "QH"), entry(44, "QD"), entry(45, "QC"),
            entry(46, "KS"), entry(47, "KH"), entry(48, "KD"), entry(49, "KC"),
            entry(50, "AS"), entry(51, "AH"), entry(52, "AD"), entry(53, "AC")
    );

    public static void MentalPoker() throws Exception {

        int players = 3;
        int cardsPerPlayer = 3;
        Random random = new Random();
        BigInteger P = BigInteger.probablePrime(31,random);
        ArrayList<ArrayList<BigInteger>> cards = new ArrayList<>(players);

        ArrayList<BigInteger> C = new ArrayList<>(players);
        ArrayList<BigInteger> D = new ArrayList<>(players);

        for(int i = 0; i < players; i++) {
            cards.add(new ArrayList<>());
            BigInteger value1;
            BigInteger value2;
            do {
                value1 = genCoPrime(P.subtract(BigInteger.ONE), 32);
                value2 = value1.modInverse(P.subtract(BigInteger.ONE));
            } while (value1.compareTo(P) > 0 || (value1.multiply(value2)).mod(P.subtract(BigInteger.ONE)).compareTo(BigInteger.ONE) != 0);
            C.add(i, value1);
            D.add(i, value2);
        }

        ArrayList<ArrayList<BigInteger>> list = new ArrayList<>(players + 1);
        for(int i = 0; i <= players; i++) {
            list.add(new ArrayList<>());
        }

        for (var entry : deck.entrySet()) {
            BigInteger buffer = BigInteger.valueOf(entry.getKey());
            list.get(0).add(buffer);
        }

        for(int i = 1; i <= players; i++) {
            for (int j = 0; j < 52; j++) {
                list.get(i).add(cryptoLab1.ModPow(list.get(i-1).get(j), C.get(i - 1), P));
            }
            Collections.shuffle(list.get(i));
        }

        for (int i = 0, j = 0; i < players; i++, j+=cardsPerPlayer) {
            for(int k = j, l = 0; l < cardsPerPlayer; k++, l++) {
                cards.get(i).add(list.get(players).get(k));
            }
        }

        for(int i = 0; i < players; i++) {
            for(int k = 0; k < cardsPerPlayer; k++) {
                for(int j = 0; j < players; j++) {
                    if(j != i) {
                        BigInteger buffer;
                        buffer = cryptoLab1.ModPow(cards.get(i).get(k), D.get(j), P);
                        cards.get(i).set(k, buffer);
                    }
                }
                BigInteger buffer = cryptoLab1.ModPow(cards.get(i).get(k), D.get(i), P);
                cards.get(i).set(k, buffer);
            }
        }

        printCardsPerPlayer(players, cards, cardsPerPlayer);
    }

}