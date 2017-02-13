package com.github.vitaliikapliuk.ask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.vitaliikapliuk.ask.core.DataResponse;
import com.github.vitaliikapliuk.ask.dao.QuestionDao;
import com.github.vitaliikapliuk.ask.persistance.QuestionData;
import com.github.vitaliikapliuk.ask.utils.JsonUtil;
import com.github.vitaliikapliuk.ask.utils.StringUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

public class TestHelper {

    private static final Client CLIENT = ClientBuilder.newClient();
    static {
        CLIENT.register(new JacksonJsonProvider(JsonUtil.json()));
    }

    private final Set<String> questionIds = new HashSet<>();

    private final QuestionDao questionDao = new QuestionDao();

    // questions service
    private final String QUESTIONS_GET_ALL = TestServer.SERVER_ADDRESS + "/api/questions?size=%s&from=%s";
    private final String QUESTIONS_GET_BY_ID = TestServer.SERVER_ADDRESS + "/api/questions/%s";
    private final String QUESTIONS_CREATE = TestServer.SERVER_ADDRESS + "/api/questions";

    public Response questionsGetAll(int size, int from) throws JsonProcessingException {
        return questionsGetAll(size, from, null, null);
    }

    public Response questionsGetAll(int size, int from, String countryIso, String search) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(countryIso)) {
            sb.append("&countryIso=").append(countryIso);
        }
        if (StringUtils.isNotBlank(search)) {
            sb.append("&search=").append(search);
        }
        final String requestStr = String.format(QUESTIONS_GET_ALL, size, from) + sb;
        return request(requestStr).get();
    }

    public Response questionsGetById(String id) throws JsonProcessingException {
        return request(String.format(QUESTIONS_GET_BY_ID, id)).get();
    }

    public Response questionsCreate(String questionText) throws JsonProcessingException {
        QuestionData questionData = new QuestionData();
        questionData.setQuestionText(questionText);
        return request(QUESTIONS_CREATE).post(Entity.json(questionData));
    }

    private static Invocation.Builder request(String path) {
        return CLIENT.target(path).request().accept(MediaType.APPLICATION_JSON);
    }

    public String readQuestionId(Response response) {
        final String questionId = response.readEntity(new GenericType<DataResponse<String>>(){}).getData();
        questionIds.add(questionId);
        return questionId;
    }

    public void clean() {
        questionIds.forEach(questionDao::delete);
        questionDao.refreshIndex();
        questionIds.clear();
    }
}
