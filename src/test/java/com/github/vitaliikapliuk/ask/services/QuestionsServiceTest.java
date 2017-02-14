package com.github.vitaliikapliuk.ask.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vitaliikapliuk.ask.TestHelper;
import com.github.vitaliikapliuk.ask.core.DataResponse;
import com.github.vitaliikapliuk.ask.dao.QuestionDao;
import com.github.vitaliikapliuk.ask.dto.PaginationDTO;
import com.github.vitaliikapliuk.ask.persistance.Question;
import com.github.vitaliikapliuk.ask.utils.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.UUID;

public class QuestionsServiceTest {

    private static TestHelper testHelper;

    @BeforeClass
    public static void setUp() {
        testHelper = new TestHelper();
    }

    @After
    public void tearDown() {
        testHelper.clean();
    }

    @Test
    public void testCreateQuestion() throws IOException {
        final QuestionDao questionDao = new QuestionDao();
        Response resp = testHelper.questionsCreate("My Question");
        Assert.assertEquals("must be ok", 200, resp.getStatus());

        final String questionId = testHelper.readQuestionId(resp);
        Assert.assertTrue("Returned Id cannot be empty", StringUtils.isNotBlank(questionId));

        Assert.assertNotNull("Question must be exist in DB", questionDao.get(questionId));
    }

    @Test
    public void testCreateEmptyQuestion() throws JsonProcessingException {
        Response resp = testHelper.questionsCreate("");
        Assert.assertEquals("Bad request (question cannot be empty)", 400, resp.getStatus());
    }

    @Test
    public void testQuestionsGetAll() throws JsonProcessingException {
        // lets create two questions
        Response resp = testHelper.questionsCreate("My Question 1");
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        testHelper.readQuestionId(resp);

        resp = testHelper.questionsCreate("My Question 2");
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        testHelper.readQuestionId(resp);

        // size = 10, from = 0
        resp = testHelper.questionsGetAll(2, 0);
        Assert.assertEquals("must be ok", 200, resp.getStatus());

        PaginationDTO<Question> questions = resp.readEntity(new GenericType<DataResponse<PaginationDTO<Question>>>(){})
                .getData();
        Assert.assertNotNull("cannot be null", questions);
        Assert.assertTrue("total can be more than 2", questions.getTotal() >= 2);
        Assert.assertEquals("size must be equals", 2, questions.getSize());
        Assert.assertEquals("from must be equals", 0, questions.getFrom());
        Assert.assertEquals("data must be equals", 2, questions.getData().size());

    }

    @Test
    public void testQuestionCreateWithIso() throws JsonProcessingException {
        Response resp = testHelper.questionsCreate("My Question With Iso");
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        final String questionId = testHelper.readQuestionId(resp);

        resp = testHelper.questionsGetById(questionId);
        Assert.assertEquals("must be ok", 200, resp.getStatus());

        Question question = resp.readEntity(new GenericType<DataResponse<Question>>(){}).getData();
        Assert.assertNotNull("cannot be null", question);
        Assert.assertTrue("iso cannot be blank", StringUtils.isNotBlank(question.getCountryIso()));

    }

    @Test
    public void testQuestionSearchByIso() throws JsonProcessingException {
        final String questionText = "My Question With Iso";
        Response resp = testHelper.questionsCreate(questionText);
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        final String questionId = testHelper.readQuestionId(resp);

        resp = testHelper.questionsGetAll(10, 0, "LV", null); // LV by default is we cannot detect correct country
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        PaginationDTO<Question> questions = resp.readEntity(new GenericType<DataResponse<PaginationDTO<Question>>>(){})
                .getData();
        Assert.assertNotNull("cannot be null", questions);
        for (Question question : questions.getData().values()) {
            Assert.assertEquals("total must be equals", "LV", question.getCountryIso());
        }
    }

    @Test
    public void testQuestionSearch() throws JsonProcessingException {
        Response resp = testHelper.questionsCreate("Hello question");
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        final String questionId1 = testHelper.readQuestionId(resp);

        resp = testHelper.questionsCreate("World question");
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        final String questionId2 = testHelper.readQuestionId(resp);

        // lets search by word 'hello'
        resp = testHelper.questionsGetAll(10, 0, null, "hello");
        Assert.assertEquals("must be ok", 200, resp.getStatus());

        PaginationDTO<Question> questions = resp.readEntity(new GenericType<DataResponse<PaginationDTO<Question>>>(){})
                .getData();

        Assert.assertTrue("must be more than 0", questions.getTotal() > 0);
        for (Question question : questions.getData().values()) {
            Assert.assertTrue("must contain", question.getQuestionText().toLowerCase().contains("hello"));
        }

        // lets search by word 'world'
        resp = testHelper.questionsGetAll(10, 0, null, "world");
        Assert.assertEquals("must be ok", 200, resp.getStatus());

        questions = resp.readEntity(new GenericType<DataResponse<PaginationDTO<Question>>>(){}).getData();

        Assert.assertTrue("must be more than 0", questions.getTotal() > 0);
        for (Question question : questions.getData().values()) {
            Assert.assertTrue("must contain", question.getQuestionText().toLowerCase().contains("world"));
        }
        // lets search by word 'question'
        resp = testHelper.questionsGetAll(10, 0, null, "question");
        Assert.assertEquals("must be ok", 200, resp.getStatus());

        questions = resp.readEntity(new GenericType<DataResponse<PaginationDTO<Question>>>(){}).getData();

        Assert.assertTrue("must be more than 0", questions.getTotal() > 0);
        for (Question question : questions.getData().values()) {
            Assert.assertTrue("must contain", question.getQuestionText().toLowerCase().contains("question"));
        }
        // lets search by word 'fake(random sequence)'
        final String randomString = UUID.randomUUID().toString().replaceAll("-", "");
        resp = testHelper.questionsGetAll(10, 0, null, randomString);
        Assert.assertEquals("must be ok", 200, resp.getStatus());

        questions = resp.readEntity(new GenericType<DataResponse<PaginationDTO<Question>>>(){}).getData();

        Assert.assertEquals("must be 0", 0, questions.getTotal());
    }

    @Test
    public void testQuestionsSearchByLowerCaseAndUpperCase() throws JsonProcessingException {
        Response resp = testHelper.questionsCreate("Hello World");
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        final String questionId = testHelper.readQuestionId(resp);

        // 'hello'
        resp = testHelper.questionsGetAll(10, 0, null, "hello");
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        PaginationDTO<Question> questions = resp.readEntity(new GenericType<DataResponse<PaginationDTO<Question>>>(){})
                .getData();
        Assert.assertEquals("must be 1 result", 1, questions.getTotal());
        Assert.assertTrue("must contain questionId", questions.getData().containsKey(questionId));

        // 'Hello'
        resp = testHelper.questionsGetAll(10, 0, null, "Hello");
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        questions = resp.readEntity(new GenericType<DataResponse<PaginationDTO<Question>>>(){}).getData();
        Assert.assertEquals("must be 1 result", 1, questions.getTotal());
        Assert.assertTrue("must contain questionId", questions.getData().containsKey(questionId));

        // 'HelLO'
        resp = testHelper.questionsGetAll(10, 0, null, "HelLO");
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        questions = resp.readEntity(new GenericType<DataResponse<PaginationDTO<Question>>>(){}).getData();
        Assert.assertEquals("must be 1 result", 1, questions.getTotal());
        Assert.assertTrue("must contain questionId", questions.getData().containsKey(questionId));

        // 'HELLO'
        resp = testHelper.questionsGetAll(10, 0, null, "HELLO");
        Assert.assertEquals("must be ok", 200, resp.getStatus());
        questions = resp.readEntity(new GenericType<DataResponse<PaginationDTO<Question>>>(){}).getData();
        Assert.assertEquals("must be 1 result", 1, questions.getTotal());
        Assert.assertTrue("must contain questionId", questions.getData().containsKey(questionId));

    }

    @Test
    public void testUnavailableForLegalReasons() throws JsonProcessingException {
        Response resp = testHelper.questionsCreate("Hello CornHub!");
        Assert.assertEquals("451 Unavailable For Legal Reasons", 451, resp.getStatus());
    }
}
