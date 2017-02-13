package com.github.vitaliikapliuk.ask.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vitaliikapliuk.ask.dto.PaginationDTO;
import com.github.vitaliikapliuk.ask.persistance.Model;
import com.github.vitaliikapliuk.ask.utils.JsonUtil;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;

public abstract class ModelDao<T extends Model> extends Dao {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final Class<T> clazz;

    public ModelDao(String index, String type, Class<T> clazz) {
        super(index, type);
        this.clazz = clazz;
    }

    public boolean isExist(String id) {
        return prepareGet(id).setFetchSource(false).get().isExists();
    }

    public String create(T obj) throws JsonProcessingException {
        return create(null, obj, false);
    }

    public String create(T obj, boolean refresh) throws JsonProcessingException {
        return create(null, obj, refresh);
    }

    public String create(String id, T obj) throws JsonProcessingException {
        return create(id, obj, false);
    }

    public String create(String id, T obj, boolean refresh) throws JsonProcessingException {
        IndexResponse resp = prepareIndex(id).setSource(json.writeValueAsBytes(obj)).get();
        if (refresh) {
            refreshIndex();
        }
        return resp.getId();
    }

    public T get(String id) throws IOException {
        return get(id, clazz);
    }

    public <R extends T> R get(String id, Class<R> type) throws IOException {
        if (id == null) {
            return null;
        }
        GetResponse resp = prepareGet(id).get();
        if (resp.isExists()) {
            R obj = json.readValue(resp.getSourceAsBytes(), type);
            obj.setId(resp.getId());
            return obj;
        } else {
            return null;
        }
    }

    public PaginationDTO<T> getAll(int size, int from) throws IOException {
        return getAllRestricted(null, size, from);
    }

    public PaginationDTO<T> getAllRestricted(QueryBuilder queryBuilder, int size, int from, SortBuilder... sortBuilders) throws IOException {
        if (size < 0) {
            size = 0;
        }
        if (from < 0) {
            from = 0;
        }
        SearchRequestBuilder builder = prepareSearch().setSize(size).setFrom(from);
        if (queryBuilder != null) {
            builder.setQuery(queryBuilder);
        }
        if (sortBuilders != null) {
            for (SortBuilder sortBuilder : sortBuilders) {
                builder.addSort(sortBuilder);
            }
        }
        SearchResponse resp = builder.get();
        LinkedHashMap<String, T> map = new LinkedHashMap<>(size);
        for (SearchHit hit : resp.getHits().getHits()) {
            T obj = JsonUtil.json().readValue(hit.getSourceAsString(), clazz);
            obj.setId(hit.getId());
            map.put(hit.getId(), obj);
        }
        PaginationDTO<T> retval = new PaginationDTO<>();
        retval.setSize(size);
        retval.setFrom(from);
        retval.setTotal((int)resp.getHits().totalHits());
        retval.setData(map);
        return retval;
    }

    public String update(String id, T obj) throws JsonProcessingException {
        return update(id, obj, false);
    }

    public String update(String id, T obj, boolean refresh) throws JsonProcessingException {
         String updateId = prepareUpdate(id).setDoc(json.writeValueAsBytes(obj)).get().getId();
        if (refresh) {
            refreshIndex();
        }
        return updateId;
    }

    public void delete(String id) {
        delete(id, false);
    }

    public void delete(String id, boolean refresh) {
        prepareDelete(id).get();
        if (refresh) {
            refreshIndex();
        }
    }

}