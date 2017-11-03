package com.example.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Candidate;
import com.example.repository.CandidateRepository;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.response.BotApiResponse;

import retrofit2.Response;

@RestController
public class BotController {

	private Candidate candidateToRegister = new Candidate();

	@Autowired
	CandidateRepository candidateRepository;

	@RequestMapping(value = "/webhook", method = RequestMethod.POST)
	private @ResponseBody Map<String, Object> webhook(@RequestBody Map<String, Object> obj)
			throws JSONException, IOException {

		String channelToken = "wvydTwaiKtsG4Z90XPfG6hWB31/TX2tceTz+v1NqSXgOMgUZ55c4GnZZ6rd+i9lJn8d0k17/7A5E0Mq1kKpmAdMKWkmqGaiezxDAZykxJIA8MoDYx+a19t4cQbRd5zLWl3k30y2pSM1zzZQz/JVSjwdB04t89/1O/w1cDnyilFU=";

		System.out.println("*****************WEBHOOK*********************");

		Map<String, Object> json = new HashMap<String, Object>();

		JSONObject jsonResult = new JSONObject(obj);

		JSONObject rsl = jsonResult.getJSONObject("originalRequest");
		JSONObject data = rsl.getJSONObject("data");
		JSONObject source = data.getJSONObject("source");
		JSONObject message = data.getJSONObject("message");
		String userId = source.getString("userId");
		String customerMessage = message.getString("text");
		String timestamp = jsonResult.getString("timestamp");
		JSONObject result = jsonResult.getJSONObject("result");
		JSONObject metadata = result.getJSONObject("metadata");
		String intentName = metadata.getString("intentName");
		JSONObject parameters = result.getJSONObject("parameters");
		JSONObject fulfillment = result.getJSONObject("fulfillment");
		String speech = fulfillment.getString("speech");

		// Not a registered candidate

		if (intentName.equals("name-user")) {
			String userName = customerMessage;
			candidateToRegister.setUserName(userName);
		}

		if (intentName.equals("phone-number")) {
			String phone = parameters.getString("phone-number");
			if (candidateRepository.findByPhone(phone) == null) {
				candidateToRegister.setPhone(phone);

				TextMessage textMessage = new TextMessage("What is your birth date?");

				PushMessage pushMessage = new PushMessage(userId, textMessage);

				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
						.pushMessage(pushMessage).execute();

			} else {
				TextMessage textMessage = new TextMessage(
						"This phone number is already registered. Please enter a different number or type 'hello' to start again");

				PushMessage pushMessage = new PushMessage(userId, textMessage);

				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
						.pushMessage(pushMessage).execute();
				System.out.println(response.code() + " --------- " + response.message());
			}
		}

		if (intentName.equals("birth-date")) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			String date = parameters.getString("date");
			Date birthday;
			try {
				birthday = formatter.parse(date);
				candidateToRegister.setBirthday(birthday);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if (intentName.equals("time-in-japan")) {
			String durationInJapan = parameters.getString("number");
			candidateToRegister.setDurationInJapan(durationInJapan);
		}

		if (intentName.equals("JLPT-level")) {
			String jLPT = parameters.getString("JLPT-level");
			candidateToRegister.setjLPT(jLPT);
		}

		if (candidateToRegister.getjLPT() != null && !candidateToRegister.getjLPT().equals("")
				&& candidateToRegister.getUserName() != null && !candidateToRegister.getUserName().equals("")
				&& candidateToRegister.getPhone() != null && !candidateToRegister.getPhone().equals("")
				&& candidateToRegister.getBirthday() != null && candidateToRegister.getDurationInJapan() != null
				&& !candidateToRegister.getDurationInJapan().equals("")) {

			System.out.println("saving....");
			candidateToRegister.setUserLineId(userId);
			candidateRepository.saveAndFlush(candidateToRegister);
			candidateToRegister = new Candidate();
		}

		// Registered candidate
		if (intentName.equals("phone-number-registered-user")) {
			String phone = parameters.getString("phone-number");
			if (candidateRepository.findByPhone(phone) == null) {
				System.out.println("account not registered...");

				TextMessage textMessage = new TextMessage(
						"This phone number is not registered. Please type 'hello' to start again");

				PushMessage pushMessage = new PushMessage(userId, textMessage);

				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
						.pushMessage(pushMessage).execute();

			} else {
				System.out.println("account registered...");

				TextMessage textMessage = new TextMessage("Do you want to search for a job?");

				PushMessage pushMessage = new PushMessage(userId, textMessage);

				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
						.pushMessage(pushMessage).execute();
				System.out.println(response.code() + " --------- " + response.message());
			}

		}

		return json;

	}

}
