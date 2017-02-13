package com.github.vitaliikapliuk.ask.persistance;

import com.github.vitaliikapliuk.ask.core.Database;
import com.github.vitaliikapliuk.ask.persistance.model._Question;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Mappings {

    private static final Logger log = LoggerFactory.getLogger(Mappings.class);
    private static final Client client = Database.getESClient();

    public static void initMapping() throws IOException {
        if (!isIndexExist(ESIndex.ASK.index)) {
            createAskIndex();
            log.info("[ask] index created");
        }
    }

    private static XContentBuilder askQuestionsMappingBuilder() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(ESIndex.AskType.QUESTION.type)
                .startObject("properties");

        builder.startObject(_Question.questionText).field("type", "string").endObject();
        builder.startObject(_Question.countryIso).field("type", "string").field("index", "not_analyzed").endObject();

        builder.endObject().endObject().endObject();

        return builder;
    }

    private static void createAskIndex() throws IOException {
        client.admin().indices().prepareCreate(ESIndex.ASK.index)
                .addMapping(ESIndex.AskType.QUESTION.type, askQuestionsMappingBuilder())
                .get();
    }

    private static boolean isIndexExist(String indexName) {
        return client.admin().indices().prepareExists(indexName).get().isExists();
    }

}

