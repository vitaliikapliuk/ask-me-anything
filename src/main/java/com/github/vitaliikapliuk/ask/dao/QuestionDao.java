package com.github.vitaliikapliuk.ask.dao;

import com.github.vitaliikapliuk.ask.dto.PaginationDTO;
import com.github.vitaliikapliuk.ask.persistance.model._Question;
import com.github.vitaliikapliuk.ask.persistance.ESIndex;
import com.github.vitaliikapliuk.ask.persistance.Question;
import com.github.vitaliikapliuk.ask.utils.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.*;

public class QuestionDao extends ModelDao<Question> {

    public QuestionDao() {
        super(ESIndex.ASK.index, ESIndex.AskType.QUESTION.type, Question.class);
    }

    public PaginationDTO<Question> getAll(int size, int from, String countryIso, String search) throws IOException {
        BoolQueryBuilder boolQuery = boolQuery();
        if (StringUtils.isNotBlank(countryIso)) {
            boolQuery.must(termQuery(_Question.countryIso, countryIso.toUpperCase()));
        }
        if (StringUtils.isNotBlank(search)) {
            boolQuery.must(termQuery(_Question.questionText, search.toLowerCase()));
        }
        return super.getAllRestricted(boolQuery, size, from);
    }
}
