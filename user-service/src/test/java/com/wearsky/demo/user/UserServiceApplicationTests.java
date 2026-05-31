package com.wearsky.demo.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

//    @Test
//    void getJJwtKey() {
//        KeyPair keyPair = Jwts.SIG.RS256.keyPair().build();
//        String encodedPublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
//        String encodedPrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
//        File publicKeyPath = new File("./keys");
//        File publicKeyFile = new File(publicKeyPath, "public-key.pem");
//        try {
//            publicKeyPath.mkdir();
//            publicKeyFile.createNewFile();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        try (BufferedWriter publicKeyFileWT = new BufferedWriter(new FileWriter(publicKeyFile))) {
//            publicKeyFileWT.write(encodedPublicKey);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        File privateKeyPath = new File("./keys");
//        File privateKeyFile = new File(privateKeyPath, "private-key.pem");
//        try {
//            privateKeyPath.mkdir();
//            privateKeyFile.createNewFile();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        try (BufferedWriter privateKeyFileWT = new BufferedWriter(new FileWriter(privateKeyFile))) {
//            privateKeyFileWT.write(encodedPrivateKey);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
