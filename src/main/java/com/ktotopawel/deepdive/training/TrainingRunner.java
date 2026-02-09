package com.ktotopawel.deepdive.training;

import com.ktotopawel.deepdive.training.adapter.devto.DevToArticleDTO;
import com.ktotopawel.deepdive.training.adapter.devto.DevToClient;
import com.ktotopawel.deepdive.training.adapter.persistence.TrainingArticleEntity;
import com.ktotopawel.deepdive.training.adapter.persistence.TrainingArticleRepository;
import com.ktotopawel.deepdive.training.domain.model.Label;
import com.ktotopawel.deepdive.training.domain.model.LabelSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("training")
@RequiredArgsConstructor
public class TrainingRunner implements CommandLineRunner {

    private final TrainingArticleRepository repository;
    private final DevToClient devToClient;

    @Override
    public void run(String... args) {
        System.out.println("-------------------------------------------");
        System.out.println("Fetching dev.to articles");

        List<DevToArticleDTO> articles = this.devToClient.fetchAll();
        for (DevToArticleDTO article : articles) {
            TrainingArticleEntity entity = new TrainingArticleEntity();
            entity.setUrl(article.url());
            entity.setTitle(article.title());
            entity.setBody(article.body());
            // todo change test label
            entity.setLabel(Label.PROGRAMMING);
            entity.setLabelSource(LabelSource.DEV_TO);
            repository.save(entity);
        }

        System.out.println("Success! Saved " + articles.size() + " articles" );
    }
}
