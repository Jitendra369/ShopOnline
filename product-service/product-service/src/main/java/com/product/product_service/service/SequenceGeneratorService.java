package com.product.product_service.service;

import com.product.product_service.entity.DatabaseSequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SequenceGeneratorService {
    private final MongoOperations mongoOperations;

    public Long generateSequence(String sequenceName) {
        Query query = new Query(
                Criteria.where("_id").is(sequenceName)
        );
        Update update = new Update().inc("sequence", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options()
                .returnNew(true)
                .upsert(true);
        DatabaseSequence counter = mongoOperations.findAndModify(
                query,
                update,
                options,
                DatabaseSequence.class
        );

        if (counter == null) {
            throw new IllegalStateException(
                    "Unable to generate sequence: " + sequenceName
            );
        }
        long nextSequence = counter.getSequence();
        log.info("Next generated sequence " + nextSequence);
        return nextSequence;
    }
}
