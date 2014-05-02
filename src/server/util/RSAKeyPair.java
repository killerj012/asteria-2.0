package server.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * A class to generate a simple 1024 bit RSA pair for decoding RSA in the login
 * block.
 * 
 * @author Nikki
 * @author lare96
 */
public final class RSAKeyPair {

    /**
     * Generates the 1024 bit RSA pair.
     * 
     * @param args
     *        an array of the runtime arguments.
     */
    public static void main(String[] args) {
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keypair = keyGen.genKeyPair();
            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();

            RSAPrivateKeySpec privSpec = factory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
            writeKey("./data/security/rsa/rsapriv.txt", privSpec.getModulus(), privSpec.getPrivateExponent());

            RSAPublicKeySpec pubSpec = factory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            writeKey("./data/security/rsa/rsapub.txt", pubSpec.getModulus(), pubSpec.getPublicExponent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the actual key to a file.
     * 
     * @param file
     *        the file to write the key to.
     * @param modulus
     *        the modulus value.
     * @param exponent
     *        the exponent value.
     */
    public static void writeKey(String file, BigInteger modulus, BigInteger exponent) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("private static final BigInteger RSA_MODULUS = new BigInteger(\"" + modulus.toString() + "\");");
            writer.newLine();
            writer.newLine();
            writer.write("private static final BigInteger RSA_EXPONENT = new BigInteger(\"" + exponent.toString() + "\");");
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}