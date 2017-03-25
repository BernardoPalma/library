package bftsmart.demo.monitoringsystem.util;

import java.nio.file.Files;
import java.io.File;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUtils {

    public static PrivateKey getPrivateKey(String filename) {

        try {
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());

            EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PublicKey getPublicKey(String filename) {

        try {
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());

            EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PublicKey getPublicKey(File file) {

        try {
            byte[] keyBytes = Files.readAllBytes(file.toPath());

            EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] sign(byte[] original, PrivateKey privateKey) {

        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(original);
            return signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean verifySignature(byte[] original, byte[] signature, PublicKey publicKey) {

        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(original);
            return sig.verify(signature);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
