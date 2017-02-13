package com.github.vitaliikapliuk.ask.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vitaliikapliuk.ask.core.Database;
import com.github.vitaliikapliuk.ask.utils.JsonUtil;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Dao {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final Client client = Database.getESClient();
    protected final ObjectMapper json = JsonUtil.json();
    protected final String index;
    protected final String type;

    public Dao(String index, String type) {
        this.index = index;
        this.type = type;
    }

    public BulkRequestBuilder prepareBulk() {
        return client.prepareBulk();
    }

    public SearchRequestBuilder prepareSearch() {
        return client.prepareSearch(index).setTypes(type);
    }

    public IndexRequestBuilder prepareIndex() {
        return prepareIndex(null);
    }

    public IndexRequestBuilder prepareIndex(String id) {
        return client.prepareIndex(index, type, id);
    }

    public UpdateRequestBuilder prepareUpdate(String id) {
        return client.prepareUpdate(index, type, id);
    }

    public DeleteRequestBuilder prepareDelete(String id) {
        return client.prepareDelete(index, type, id);
    }

    public GetRequestBuilder prepareGet(String id) {
        return client.prepareGet(index, type, id);
    }

    public void refreshIndex() {
        client.admin().indices().prepareRefresh(index).get();
    }

}
