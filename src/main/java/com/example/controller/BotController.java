package com.example.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Job;
import com.example.repository.CandidateRepository;
import com.example.repository.JobRepository;
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

	@Autowired
	CandidateRepository candidateRepository;

	@Autowired
	JobRepository jobRepository;

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

		if (intentName.equals("search for a job")) {
			String address = customerMessage;
			List<Job> jobs = new ArrayList<>();
			List<Job> jobsToDisplay = new ArrayList<>();

			jobs = jobRepository.findByAreaOrStation(address);

			if (jobs != null) {
				if (jobs.size() <= 5) {
					jobsToDisplay.addAll(jobs);
				} else {
					for (int i = 0; i < 5; i++) {
						jobsToDisplay.add(jobs.get(i));
					}
				}

				carouselForUser(userId, channelToken, jobsToDisplay);
			} else {

				TextMessage textMessage = new TextMessage("No jobs found");

				PushMessage pushMessage = new PushMessage(userId, textMessage);

				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
						.pushMessage(pushMessage).execute();
			}
		}

		return json;
	}

	/**
	 * Method for send carousel template message to use
	 * 
	 * @param userId
	 * @param lChannelAccessToken
	 * @param nameSatff1
	 * @param nameSatff2
	 * @param poster1_url
	 * @param poster2_url
	 * @throws IOException
	 */
	private void carouselForUser(String userId, String lChannelAccessToken, List<Job> jobsToDisplay)
			throws IOException {

		java.util.List<CarouselColumn> columns = new ArrayList<>();

		for (Job job : jobsToDisplay) {
			// Document doc = Jsoup.connect(link).get();
			// String title = doc.getElementsByClass("tit_articleName").get(0).text();
			// String img = doc.getElementsByClass("max-width-260").get(0).attr("abs:src");
			String img = "https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png";
			String title = job.getPositionName();
			String link = "http://www.offerme.com/jobs/" + job.getIdJob();
			CarouselColumn column = new CarouselColumn(img, title, "Click check to apply",
					Arrays.asList(new URIAction("check", link)));
			columns.add(column);
		}

		CarouselTemplate carouselTemplate = new CarouselTemplate(columns);

		TemplateMessage templateMessage = new TemplateMessage("Your search result", carouselTemplate);
		PushMessage pushMessage = new PushMessage(userId, templateMessage);
		try {
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(lChannelAccessToken).build()
					.pushMessage(pushMessage).execute();
			System.out.println(response.code() + " " + response.message());
		} catch (IOException e) {
			System.out.println("Exception is raised ");
			e.printStackTrace();
		}
	}

}
