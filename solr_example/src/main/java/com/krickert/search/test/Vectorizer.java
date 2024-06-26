package com.krickert.search.test;

import ai.djl.MalformedModelException;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.google.common.collect.Lists;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Singleton
public class Vectorizer {

    private static final Logger log = LoggerFactory.getLogger(Vectorizer.class);

    Criteria<String, float[]> criteria = Criteria.builder()
            .setTypes(String.class, float[].class)
            .optModelUrls("djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2")
            .optEngine("PyTorch")
            .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
            .build();

    public float[] embeddings(String text) {
        try (ZooModel<String, float[]> model = criteria.loadModel();
             Predictor<String, float[]> predictor = model.newPredictor()) {
            return predictor.predict(text);
        } catch (TranslateException | ModelNotFoundException | MalformedModelException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Float> getEmbeddings(String text) {
        log.debug("getting embeddings for {}", text);
        float[] res = this.embeddings(text);
        final List<Float> response;
        if (res == null) {
            response = Collections.emptyList();
        } else {
            response = Lists.newArrayListWithCapacity(res.length);
            for(float embedding : res) {
                response.add(embedding);
            }
        }
        log.info("Text input [{}] returned embeddings [{}]", text, response);
        return response;
    }
}