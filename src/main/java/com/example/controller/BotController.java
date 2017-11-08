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
import com.example.entity.Shop;
import com.example.repository.CandidateRepository;
import com.example.repository.JobRepository;
import com.example.repository.ShopRepository;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;

import retrofit2.Response;

@RestController
public class BotController {

	@Autowired
	CandidateRepository candidateRepository;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	ShopRepository shopRepository;

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
		System.out.println("customerMessage : " + customerMessage);

		if (intentName.equals("add job")) {

			Shop shop = new Shop();

			// shop.setAddressShop("tokyo hizusikio");
			// shop.setCategory("category");
			// shop.setChannelToken(channelToken);
			// shop.setDescriptionShop("description shop 1");
			// shop.setNameShop("shop1");
			// shop.setNearestStation("Ōsaka Abenobashi Station");
			// shop.setOpenTime("every day");
			//
			// shopRepository.saveAndFlush(shop);
			// shopRepository.flush();
			// System.out.println("*************shop ID ********** : " + shop.getIdShop());
			//
			// shop = shopRepository.getOne(11);
			//
			// Job job1 = new Job();
			// Job job2 = new Job();
			// Job job3 = new Job();
			// Job job4 = new Job();
			// Job job5 = new Job();
			//
			// job1.setJobDetails("jobbbbbbbbbb1");
			// job1.setNumberStaffNeeded(44);
			// job1.setPositionCategory("pos category 1");
			// job1.setPositionName("position 1");
			// job1.setSalary(4541);
			// job1.setSalaryDetail("salary1");
			// job1.setShop(shop);
			//
			// job2.setJobDetails("jobbbbbbbbbb2");
			// job2.setNumberStaffNeeded(25);
			// job2.setPositionCategory("pos category 2");
			// job2.setPositionName("position 2");
			// job2.setSalary(4541);
			// job2.setSalaryDetail("salary2");
			// job2.setShop(shop);
			//
			// job3.setJobDetails("jobbbbbbbbbb3");
			// job3.setNumberStaffNeeded(47);
			// job3.setPositionCategory("pos category 3");
			// job3.setPositionName("position 3");
			// job3.setSalary(4541);
			// job3.setSalaryDetail("salary3");
			// job3.setShop(shop);
			//
			// job4.setJobDetails("jobbbbbbbbbb4");
			// job4.setNumberStaffNeeded(36);
			// job4.setPositionCategory("pos category 4");
			// job4.setPositionName("position 4");
			// job4.setSalary(4541);
			// job4.setSalaryDetail("salary4");
			// job4.setShop(shop);
			//
			// job5.setJobDetails("jobbbbbbbbbb5");
			// job5.setNumberStaffNeeded(85);
			// job5.setPositionCategory("pos category 5");
			// job5.setPositionName("position 5");
			// job5.setSalary(4541);
			// job5.setSalaryDetail("salary5");
			// job5.setShop(shop);
			//
			// jobRepository.saveAndFlush(job1);
			// jobRepository.saveAndFlush(job2);
			// jobRepository.saveAndFlush(job3);
			// jobRepository.saveAndFlush(job4);
			// jobRepository.saveAndFlush(job5);
			// jobRepository.flush();

		}

		if (intentName.equals("Default Fallback Intent")) {

			String address = customerMessage;
			List<Job> jobs = new ArrayList<>();
			List<Job> jobsToDisplay = new ArrayList<>();

			jobs = jobRepository.findByAreaOrStation(address);

			if (jobs.size() != 0) {
				if (jobs.size() <= 5) {
					jobsToDisplay.addAll(jobs);
				} else {
					for (int i = 0; i < 5; i++) {
						jobsToDisplay.add(jobs.get(i));
					}
				}

				carouselForUser(userId, channelToken, jobsToDisplay);

			} else {

				TextMessage textMessage = new TextMessage("No jobs found. Please enter a valid area name or station");
				PushMessage pushMessage = new PushMessage(userId, textMessage);
				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
						.pushMessage(pushMessage).execute();
			}
		}

		/**
		 * code to send two confirm buttons template for "Push have you called shop name
		 */
		if (intentName.equals("called")) {
			ConfirmTemplate confirmTemplate = new ConfirmTemplate("Have you called the shop?",
					new MessageAction("Yes", "Yes I called"), new MessageAction("No", "No I did not"));
			TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
			PushMessage pushMessage = new PushMessage(userId, templateMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
					.pushMessage(pushMessage).execute();

			System.out.println(response.code() + " " + response.message());

		}

		if (intentName.equals("Yes I called")) {
			ConfirmTemplate confirmTemplate = new ConfirmTemplate("Did you confirm the interview time?",
					new MessageAction("Confirmed", "Interview confirmed"),
					new MessageAction("Not confirmed", "Interview not confirmed"));
			TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
			PushMessage pushMessage = new PushMessage(userId, templateMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
					.pushMessage(pushMessage).execute();

			System.out.println(response.code() + " " + response.message());
		}

		if (intentName.equals("interview-time")) {
			if (parameters == null) {
				TextMessage textMessage = new TextMessage("Please enter a valid date and time");
				PushMessage pushMessage = new PushMessage(userId, textMessage);
				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
						.pushMessage(pushMessage).execute();
			} else {
				System.out.println("parameters : " + parameters.getString("date"));
				System.out.println("parameters : " + parameters.getString("time"));

				if (parameters != null && parameters.getString("date") != null && parameters.getString("time") != null
						&& !parameters.getString("date").equals("") && !parameters.getString("time").equals("")) {
					TextMessage textMessage = new TextMessage("Okay, good luck!");
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
							.pushMessage(pushMessage).execute();
				}
			}
		}

		/**
		 * code to send a reminder for the interview
		 */
		if (intentName.equals("reminder")) {
			TextMessage textMessage = new TextMessage("Tomorrow is the interview!");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
					.pushMessage(pushMessage).execute();
		}

		/**
		 * code to send two confirm buttons template for "Push have you passed?
		 */
		if (intentName.equals("passed")) {
			ConfirmTemplate confirmTemplate = new ConfirmTemplate("Have you passed the interview?",
					new MessageAction("Yes", "Yes I passed"), new MessageAction("No", "" + ""));
			TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
			PushMessage pushMessage = new PushMessage(userId, templateMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(channelToken).build()
					.pushMessage(pushMessage).execute();

			System.out.println(response.code() + " " + response.message());

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
