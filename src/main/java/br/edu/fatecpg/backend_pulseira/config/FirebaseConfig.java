package br.edu.fatecpg.backend_pulseira.config;

import tools.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FirebaseConfig {
    @Bean
    FirebaseApp firebaseApp(@Value("${firebase.project-id:}") String projectId) throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) return FirebaseApp.getInstance();
        FirebaseOptions.Builder options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault());
        if (!projectId.isBlank()) options.setProjectId(projectId);
        return FirebaseApp.initializeApp(options.build());
    }

    @Bean
    Firestore firestore(FirebaseApp app) { return com.google.firebase.cloud.FirestoreClient.getFirestore(app); }

    @Bean
    FirebaseAuth firebaseAuth(FirebaseApp app) { return FirebaseAuth.getInstance(app); }

    @Bean
    FirebaseDocumentStore firebaseDocumentStore(Firestore firestore, ObjectMapper mapper) {
        return new FirebaseDocumentStore(firestore, mapper);
    }
}
