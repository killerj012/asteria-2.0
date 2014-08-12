package com.asteria.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.asteria.world.map.Position;

/**
 * A collection of miscellaneous utility methods and constants.
 * 
 * @author blakeman8192
 * @author lare96
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class Utility {

    /**
     * Thread local random instance, used to generate pseudo-random primitive
     * types. Thread local random is faster than your traditional random
     * implementation as there is no unnecessary wait on the backing
     * <code>AtomicLong</code> within {@link Random}.
     */
    public static final Random RANDOM = ThreadLocalRandom.current();

    /** The login response codes. */
    public static final int LOGIN_RESPONSE_OK = 2,
            LOGIN_RESPONSE_INVALID_CREDENTIALS = 3,
            LOGIN_RESPONSE_ACCOUNT_DISABLED = 4,
            LOGIN_RESPONSE_ACCOUNT_ONLINE = 5, LOGIN_RESPONSE_UPDATED = 6,
            LOGIN_RESPONSE_WORLD_FULL = 7,
            LOGIN_RESPONSE_LOGIN_SERVER_OFFLINE = 8,
            LOGIN_RESPONSE_LOGIN_LIMIT_EXCEEDED = 9,
            LOGIN_RESPONSE_BAD_SESSION_ID = 10,
            LOGIN_RESPONSE_PLEASE_TRY_AGAIN = 11,
            LOGIN_RESPONSE_NEED_MEMBERS = 12,
            LOGIN_RESPONSE_COULD_NOT_COMPLETE_LOGIN = 13,
            LOGIN_RESPONSE_SERVER_BEING_UPDATED = 14,
            LOGIN_RESPONSE_LOGIN_ATTEMPTS_EXCEEDED = 16,
            LOGIN_RESPONSE_MEMBERS_ONLY_AREA = 17;

    /** The equipment slots. */
    public static final int EQUIPMENT_SLOT_HEAD = 0, EQUIPMENT_SLOT_CAPE = 1,
            EQUIPMENT_SLOT_AMULET = 2, EQUIPMENT_SLOT_WEAPON = 3,
            EQUIPMENT_SLOT_CHEST = 4, EQUIPMENT_SLOT_SHIELD = 5,
            EQUIPMENT_SLOT_LEGS = 7, EQUIPMENT_SLOT_HANDS = 9,
            EQUIPMENT_SLOT_FEET = 10, EQUIPMENT_SLOT_RING = 12,
            EQUIPMENT_SLOT_ARROWS = 13;

    /** The appearance slots. */
    public static final int APPEARANCE_SLOT_CHEST = 0,
            APPEARANCE_SLOT_ARMS = 1, APPEARANCE_SLOT_LEGS = 2,
            APPEARANCE_SLOT_HEAD = 3, APPEARANCE_SLOT_HANDS = 4,
            APPEARANCE_SLOT_FEET = 5, APPEARANCE_SLOT_BEARD = 6;

    /** The bonus id's. */
    public static final int ATTACK_STAB = 0, ATTACK_SLASH = 1,
            ATTACK_CRUSH = 2, ATTACK_MAGIC = 3, ATTACK_RANGE = 4,
            DEFENCE_STAB = 5, DEFENCE_SLASH = 6, DEFENCE_CRUSH = 7,
            DEFENCE_MAGIC = 8, DEFENCE_RANGE = 9, BONUS_STRENGTH = 10,
            BONUS_PRAYER = 11;

    /** The gender id's. */
    public static final int GENDER_MALE = 0, GENDER_FEMALE = 1;

    /** Difference in X coordinates for directions array. */
    public static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1,
            1, -1, 0, 1 };

    /** Difference in Y coordinates for directions array. */
    public static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0,
            -1, -1, -1 };

    /** Lengths for the various packets. */
    public static final int PACKET_LENGTHS[] = { //
    0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
            0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
            0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
            0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
            2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
            0, 0, 0, 12, 0, 0, 0, 0, 8, 0, // 50
            0, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
            6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
            0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
            0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
            0, 13, 0, -1, 0, 0, 0, 0, 0, 0,// 100
            0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
            1, 0, 6, 0, 0, 0, -1, 0, 2, 6, // 120
            0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
            0, 0, 0, 0, 0, 6, 0, 0, 0, 0, // 140
            0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
            0, 0, 0, 0, -1, -1, 0, 0, 0, 0,// 160
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
            0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
            0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
            2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
            4, 0, 0, 0, 7, 8, 0, 0, 10, 0, // 210
            0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
            1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
            0, 4, 0, 0, 0, 0, -1, 0, -1, 4,// 240
            0, 0, 6, 6, 0, 0, 0 // 250
    };

    /** The character table. */
    public static final char XLATE_TABLE[] = { ' ', 'e', 't', 'a', 'o', 'i',
            'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g',
            'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(',
            ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$',
            '%', '"', '[', ']' };

    /** The decode buffer. */
    public static final char DECODE_BUFFER[] = new char[4096];

    /** A table of valid characters. */
    public static final char VALID_CHARS[] = { '_', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*',
            '(', ')', '-', '+', '=', ':', ';', '.', '>', '<', ',', '"', '[',
            ']', '|', '?', '/', '`' };

    /** The bonus names. */
    public static final String[] BONUS_NAMES = { "Stab", "Slash", "Crush",
            "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range",
            "Strength", "Prayer" };

    /**
     * Returns a pseudo-random {@code int} value between inclusive
     * <code>min</code> and exclusive <code>max</code>.
     * 
     * <br>
     * <br>
     * This method is thread-safe. </br>
     * 
     * @param min
     *            The minimum inclusive number.
     * @param max
     *            The maximum exclusive number.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If the specified range is less <tt>0</tt>
     * 
     *             <p>
     *             We use {@link ThreadLocalRandom#current()} to produce this
     *             random {@code int}, it is faster than a standard
     *             {@link Random} instance as we do not have to wait on
     *             {@code AtomicLong}.
     *             </p>
     */
    public static int exclusiveRandom(int min, int max) {
        if (max <= min) {
            max = min + 1;
        }
        return RANDOM.nextInt((max - min)) + min;
    }

    /**
     * Returns a pseudo-random {@code int} value between inclusive <tt>0</tt>
     * and exclusive <code>range</code>.
     * 
     * <br>
     * <br>
     * This method is thread-safe. </br>
     * 
     * @param range
     *            The exclusive range.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If the specified range is less <tt>0</tt>
     * 
     *             <p>
     *             We use {@link ThreadLocalRandom#current()} to produce this
     *             random {@code int}, it is faster than a standard
     *             {@link Random} instance as we do not have to wait on
     *             {@code AtomicLong}.
     *             </p>
     */
    public static int exclusiveRandom(int range) {
        return exclusiveRandom(0, range);
    }

    /**
     * Returns a pseudo-random {@code int} value between inclusive
     * <code>min</code> and inclusive <code>max</code>.
     * 
     * @param min
     *            The minimum inclusive number.
     * @param max
     *            The maximum inclusive number.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If {@code max - min + 1} is less than <tt>0</tt>.
     * @see {@link #exclusiveRandom(int)}.
     */
    public static int inclusiveRandom(int min, int max) {
        if (max < min) {
            max = min + 1;
        }
        return exclusiveRandom((max - min) + 1) + min;
    }

    /**
     * Returns a pseudo-random {@code int} value between inclusive <tt>0</tt>
     * and inclusive <code>range</code>.
     * 
     * @param range
     *            The maximum inclusive number.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If {@code max - min + 1} is less than <tt>0</tt>.
     * @see {@link #exclusiveRandom(int)}.
     */
    public static int inclusiveRandom(int range) {
        return inclusiveRandom(0, range);
    }

    /**
     * Returns a pseudo-random {@code int} value between inclusive
     * <code>min</code> and inclusive <code>max</code> excluding the specified
     * numbers within the {@code excludes} array.
     * 
     * @param min
     *            The minimum inclusive number.
     * @param max
     *            The maximum inclusive number.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If {@code max - min + 1} is less than <tt>0</tt>.
     * @see {@link #inclusiveRandom(int, int)}.
     */
    public static int inclusiveRandomExcludes(int min, int max, int... exclude) {
        Arrays.sort(exclude);

        int result = inclusiveRandom(min, max);
        while (Arrays.binarySearch(exclude, result) >= 0) {
            result = inclusiveRandom(min, max);
        }

        return result;
    }

    /**
     * Returns a pseudo-random {@code float} between inclusive <tt>0</tt> and
     * exclusive <code>range</code>
     * 
     * <br>
     * <br>
     * This method is thread-safe. </br>
     * 
     * @param range
     *            The exclusive range.
     * @return The pseudo-random {@code float}.
     * @throws IllegalArgumentException
     *             If the specified range is less than <tt>0</tt>
     * 
     *             <p>
     *             We use {@link ThreadLocalRandom#current()} to produce this
     *             random {@code float}, it is faster than a standard
     *             {@link Random} instance as we do not have to wait on
     *             {@code AtomicLong}.
     *             </p>
     */
    public static float random(float range) {
        if (range < 0F) {
            throw new IllegalArgumentException("range <= 0");
        }

        return RANDOM.nextFloat() * range;
    }

    /**
     * Determines the indefinite article of a 'thing'.
     * 
     * @param thing
     *            the thing.
     * @return the indefinite article.
     */
    public static String determineIndefiniteArticle(String thing) {
        char first = thing.toLowerCase().charAt(0);
        boolean vowel = first == 'a' || first == 'e' || first == 'i' || first == 'o' || first == 'u';
        return vowel ? "an" : "a";
    }

    /**
     * Appends the determined indefinite article to a 'thing'.
     * 
     * @param thing
     *            the thing.
     * @return the indefinite article.
     */
    public static String appendIndefiniteArticle(String thing) {
        return determineIndefiniteArticle(thing).concat(" " + thing);
    }

    /**
     * Picks a random element out of any array type.
     * 
     * @param array
     *            the array to pick the element from.
     * @return the element chosen.
     */
    public static <T> T randomElement(T[] array) {
        return array[(int) (RANDOM.nextDouble() * array.length)];
    }

    /**
     * Picks a random element out of any list type.
     * 
     * @param list
     *            the list to pick the element from.
     * @return the element chosen.
     */
    public static <T> T randomElement(List<T> list) {
        return list.get((int) (RANDOM.nextDouble() * list.size()));
    }

    /**
     * Converts a long hash to a string.
     * 
     * @param l
     *            the long to convert.
     * @return the converted string.
     */
    public static String hashToName(long l) {
        int i = 0;
        char ac[] = new char[12];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            ac[11 - i++] = VALID_CHARS[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }

    /**
     * Converts an array of bytes to an integer.
     * 
     * @param data
     *            the array of bytes.
     * @return the newly constructed integer.
     */
    public static int hexToInt(byte[] data) {
        int value = 0;
        int n = 1000;
        for (int i = 0; i < data.length; i++) {
            int num = (data[i] & 0xFF) * n;
            value += num;
            if (n > 1) {
                n = n / 1000;
            }
        }
        return value;
    }

    /**
     * Unpacks text from an array of bytes.
     * 
     * @param packedData
     *            the array of bytes.
     * @param size
     *            the size of the array of bytes.
     * @return the unpacked string.
     */
    public static String textUnpack(byte packedData[], int size) {
        int idx = 0, highNibble = -1;
        for (int i = 0; i < size * 2; i++) {
            int val = packedData[i / 2] >> (4 - 4 * (i % 2)) & 0xf;
            if (highNibble == -1) {
                if (val < 13)
                    DECODE_BUFFER[idx++] = XLATE_TABLE[val];
                else
                    highNibble = val;
            } else {
                DECODE_BUFFER[idx++] = XLATE_TABLE[((highNibble << 4) + val) - 195];
                highNibble = -1;
            }
        }

        return new String(DECODE_BUFFER, 0, idx);
    }

    /**
     * Capitalizes the first character of the argued string. Any leading or
     * trailing whitespace in the argued string should be trimmed before using
     * this method.
     * 
     * @param s
     *            the string to capitalize.
     * @return the capitalized string.
     */
    public static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase()
                .concat(s.substring(1, s.length()));
    }

    /**
     * Converts a string to a long hash value.
     * 
     * @param s
     *            the string to convert.
     * @return the long hash value.
     */
    public static long nameToHash(String s) {
        long l = 0L;
        for (int i = 0; i < s.length() && i < 12; i++) {
            char c = s.charAt(i);
            l *= 37L;
            if (c >= 'A' && c <= 'Z')
                l += (1 + c) - 65;
            else if (c >= 'a' && c <= 'z')
                l += (1 + c) - 97;
            else if (c >= '0' && c <= '9')
                l += (27 + c) - 48;
        }
        while (l % 37L == 0L && l != 0L)
            l /= 37L;
        return l;
    }

    /**
     * Returns the delta coordinates. Note that the returned Position is not an
     * actual position, instead it's values represent the delta values between
     * the two arguments.
     * 
     * @param a
     *            the first position.
     * @param b
     *            the second position.
     * @return the delta coordinates contained within a position.
     */
    public static Position delta(Position a, Position b) {
        return new Position(b.getX() - a.getX(), b.getY() - a.getY());
    }

    /**
     * Calculates the direction between the two coordinates.
     * 
     * @param dx
     *            the first coordinate.
     * @param dy
     *            the second coordinate.
     * @return the direction.
     */
    public static int direction(int dx, int dy) {
        if (dx < 0) {
            if (dy < 0) {
                return 5;
            } else if (dy > 0) {
                return 0;
            } else {
                return 3;
            }
        } else if (dx > 0) {
            if (dy < 0) {
                return 7;
            } else if (dy > 0) {
                return 2;
            } else {
                return 4;
            }
        } else {
            if (dy < 0) {
                return 6;
            } else if (dy > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * Deletes character files out of the specified directory.
     * 
     * @author lare96
     */
    @SuppressWarnings("unused")
    private static final class DeleteCharacterFiles {

        /** The directory to delete files from. */
        public static final String DIRECTORY = "./data/players/";

        /** The files with names starting with this string will be deleted. */
        public static final String STARTING_WITH = "stressbot";

        /** The main method which starts the deletion process. */
        public static void main(String[] args) {
            int count = 0;
            Logger l = Logger.getLogger(DeleteCharacterFiles.class
                    .getSimpleName());
            l.info("Starting with data " + DIRECTORY + ":" + STARTING_WITH);

            try {
                // List the files in the given directory.
                File[] files = new File(DIRECTORY).listFiles();

                // Loop through all of the files and delete them.
                for (File child : files) {
                    if (child == null || !child.isFile() || child.isHidden() || !child
                            .getName().toLowerCase()
                            .startsWith(STARTING_WITH.toLowerCase())) {
                        continue;
                    }
                    child.delete();
                    count++;
                }
            } catch (Exception e) {
                l.log(Level.SEVERE, "Error deleting files!", e);
            } finally {
                l.info("Deleted " + count + " files!");
            }
        }
    }

    /**
     * A class to generate RSA keys for the login block.
     * 
     * @author Nikki
     */
    @SuppressWarnings("unused")
    private static class RSAKeyGen {

        /** Generates the 1024 bit RSA pair. */
        public static void main(String[] args) {
            try {
                KeyFactory factory = KeyFactory.getInstance("RSA");
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(1024);
                KeyPair keypair = keyGen.genKeyPair();
                PrivateKey privateKey = keypair.getPrivate();
                PublicKey publicKey = keypair.getPublic();

                RSAPrivateKeySpec privSpec = factory.getKeySpec(privateKey,
                        RSAPrivateKeySpec.class);
                writeKey("./data/rsa/rsapriv.txt",
                        privSpec.getModulus(), privSpec.getPrivateExponent());

                RSAPublicKeySpec pubSpec = factory.getKeySpec(publicKey,
                        RSAPublicKeySpec.class);
                writeKey("./data/rsa/rsapub.txt",
                        pubSpec.getModulus(), pubSpec.getPublicExponent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /** Writes the actual key to a file. */
        private static void writeKey(String file, BigInteger modulus,
                BigInteger exponent) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("private static final BigInteger RSA_MODULUS = new BigInteger(\"" + modulus
                        .toString() + "\");");
                writer.newLine();
                writer.newLine();
                writer.write("private static final BigInteger RSA_EXPONENT = new BigInteger(\"" + exponent
                        .toString() + "\");");
                writer.newLine();
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Utility() {}
}
