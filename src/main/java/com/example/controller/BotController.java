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
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.response.BotApiResponse;

import retrofit2.Response;

@RestController
public class BotController {

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

		System.out.println("intentName : " + intentName);

		// Not a registered candidate
		Candidate candidateToRegister = new Candidate();

		if (intentName.equals("name-user")) {
			System.out.println("user name : " + customerMessage);
			String userName = customerMessage;
			candidateToRegister.setUserName(userName);
		}

		if (intentName.equals("phone-number")) {
			System.out.println("phone number : " + parameters.getString("phone-number"));
			String phone = parameters.getString("phone-number");
			candidateToRegister.setPhone(phone);
		}

		if (intentName.equals("birth-date")) {
			System.out.println("birth date : " + parameters.getString("date"));

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
			System.out.println("time in japan : " + parameters.getString("number"));

			String durationInJapan = parameters.getString("number");
			candidateToRegister.setDurationInJapan(durationInJapan);
		}

		if (intentName.equals("JLPT-level")) {
			System.out.println("JLPT-level : " + parameters.getString("JLPT-level"));
			String jLPT = parameters.getString("JLPT-level");
			candidateToRegister.setjLPT(jLPT);
		}

		if (candidateToRegister.getjLPT() != null && !candidateToRegister.getjLPT().equals("")
				&& candidateToRegister.getUserName() != null && !candidateToRegister.getUserName().equals("")
				&& candidateToRegister.getPhone() != null && !candidateToRegister.getPhone().equals("")
				&& candidateToRegister.getBirthday() != null && candidateToRegister.getDurationInJapan() != null
				&& !candidateToRegister.getDurationInJapan().equals("")) {

			candidateRepository.saveAndFlush(candidateToRegister);
		}

		// Registered candidate
		if (intentName.equals("phone-number-registered-user")) {
			System.out.println("phone number registered user : " + parameters.getString("phone-number"));
		}

		return json;

	}

}
