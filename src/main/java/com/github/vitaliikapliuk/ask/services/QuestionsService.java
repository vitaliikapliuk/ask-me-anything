package com.github.vitaliikapliuk.ask.services;

import com.github.vitaliikapliuk.ask.core.StatusException;
import com.github.vitaliikapliuk.ask.core.TimeFrame;
import com.github.vitaliikapliuk.ask.dao.QuestionDao;
import com.github.vitaliikapliuk.ask.dto.PaginationDTO;
import com.github.vitaliikapliuk.ask.persistance.Question;
import com.github.vitaliikapliuk.ask.persistance.QuestionData;
import com.github.vitaliikapliuk.ask.utils.IsoPredictor;
import com.github.vitaliikapliuk.ask.utils.SmartClone;
import com.github.vitaliikapliuk.ask.utils.StopWordsDetector;
import com.github.vitaliikapliuk.ask.utils.StringUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/questions")
@Api(value = "QuestionsService")
public class QuestionsService {

    private final QuestionDao questionDao = new QuestionDao();

    @GET
    @ApiOperation(value = "getAll", notes = "get all questions", response = Question.class, responseContainer = "List")
    public PaginationDTO<Question> getAll(
            @ApiParam(required = true, defaultValue = "10") @QueryParam("size") int size,
            @ApiParam(required = true, defaultValue = "0") @QueryParam("from") int from,
            @QueryParam("countyIso") String countryIso,
            @QueryParam("search") String search) throws Exception {
        return questionDao.getAll(size, from, countryIso, search);
    }

    @GET
    @Path("{questionId}")
    @ApiOperation(value = "getById", notes = "get question by id", response = Question.class)
    public Question getById(@PathParam("questionId") String questionId) throws Exception {
        return questionDao.get(questionId);
    }

    @POST
    @ApiOperation(value = "createQuestion", notes = "create new question", response = String.class)
    @ApiResponses({
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 429, message = "Too Many Requests"),
        @ApiResponse(code = 451, message = "Unavailable For Legal Reasons") })
    public String create(@Context org.glassfish.grizzly.http.server.Request request, QuestionData questionData) throws Exception {
        if (questionData == null || StringUtils.isBlank(questionData.getQuestionText())) {
            throw new StatusException(400, "Bad request (questionText field cannot be empty)");
        }
        if (StopWordsDetector.containStopWords(questionData.getQuestionText())) {
            throw new StatusException(451, "Unavailable For Legal Reasons");
        }
        final String iso = IsoPredictor.getIso(request.getRemoteAddr());
        if (!TimeFrame.isAvailable(iso)) {
            throw new StatusException(429, "Too Many Requests");
        }
        Question question = fill(questionData);
        question.setCountryIso(iso);
        return questionDao.create(question, true);
    }

    private Question fill(QuestionData data) throws InvocationTargetException, IllegalAccessException {
        Question question = new Question();
        SmartClone.copy(question, data);
        return question;
    }

}
