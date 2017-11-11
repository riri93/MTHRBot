package com.example.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Candidate;
import com.example.entity.ChatLineAdmin;
import com.example.entity.ChatMessageLine;
import com.example.entity.Job;
import com.example.entity.Shop;
import com.example.entity.ShopCandidateRelation;
import com.example.entity.ShopCandidateRelationPK;
import com.example.repository.CandidateRepository;
import com.example.repository.ChatLineAdminRepository;
import com.example.repository.ChatMessageLineRepository;
import com.example.repository.JobRepository;
import com.example.repository.ShopCandidateRelationRepository;
import com.example.repository.ShopRepository;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;

import retrofit2.Response;

@RestController
public class BotController {

	private BotScheduler botScheduler = new BotScheduler();
	private Candidate candidateToRegister = new Candidate();

	private static final String CHANNEL_ACCESS_TOKEN = "wvydTwaiKtsG4Z90XPfG6hWB31/TX2tceTz+v1NqSXgOMgUZ55c4GnZZ6rd+i9lJn8d0k17/7A5E0Mq1kKpmAdMKWkmqGaiezxDAZykxJIA8MoDYx+a19t4cQbRd5zLWl3k30y2pSM1zzZQz/JVSjwdB04t89/1O/w1cDnyilFU=";

	@Autowired
	CandidateRepository candidateRepository;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	ShopRepository shopRepository;

	@Autowired
	ShopCandidateRelationRepository shopCandidateRelationRepository;

	@Autowired
	ChatLineAdminRepository chatLineAdminRepository;

	@Autowired
	ChatMessageLineRepository chatMessageLineRepository;

	@RequestMapping(value = "/webhook", method = RequestMethod.POST)
	private @ResponseBody Map<String, Object> webhook(@RequestBody Map<String, Object> obj)
			throws JSONException, IOException, Exception {

		System.out.println("*****************WEBHOOK*********************");

		Candidate candidate = new Candidate();
		String addressToSearch = "";
		ChatLineAdmin chatLineAdmin = new ChatLineAdmin();

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

		if (candidateRepository.findByUserLineId(userId) == null) {
			candidateToRegister = new Candidate();
			candidateToRegister.setUserLineId(userId);
			candidateRepository.saveAndFlush(candidateToRegister);
		}

		candidate = candidateRepository.findByUserLineId(userId);

		if (shopRepository.findAll() != null) {

			List<Shop> shops = new ArrayList<>();
			shops = shopRepository.findAll();

			for (Shop shop1 : shops) {
				ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
				shopCandidateRelationPK.setIdCandidate(candidate.getIdUser());
				shopCandidateRelationPK.setIdShop(shop1.getIdShop());

				if (shopCandidateRelationRepository.findOne(shopCandidateRelationPK) == null) {
					ShopCandidateRelation shopCandidateRelationToAdd = new ShopCandidateRelation();
					shopCandidateRelationToAdd.setShopCandidateRelationPK(shopCandidateRelationPK);
					shopCandidateRelationToAdd.setCandidate(candidate);
					shopCandidateRelationToAdd.setConfirmedInterview(false);
					shopCandidateRelationToAdd.setShop(shop1);
					shopCandidateRelationRepository.saveAndFlush(shopCandidateRelationToAdd);
				}
			}
		}

		if (candidate != null) {
			if (candidate.getChatLineAdmin() == null) {
				chatLineAdminRepository.saveAndFlush(chatLineAdmin);
				candidate.setChatLineAdmin(chatLineAdmin);
				candidateRepository.saveAndFlush(candidate);
			}
		}

		if (customerMessage != null && !customerMessage.equals("")) {
			if (candidate != null) {
				ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
				chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
				chatMessageLineToAdd.setMessageDate((new Date()));
				chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
				chatMessageLineToAdd.setMessageText(customerMessage);
				chatMessageLineToAdd.setReadState(false);
				chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
			}
		}

		System.out.println("userId : " + userId);
		System.out.println("speech : " + speech);
		System.out.println("timestamp : " + timestamp);
		System.out.println("intentName : " + intentName);
		System.out.println("customerMessage : " + customerMessage);

		if (intentName.equals("Default Fallback Intent")) {
			addressToSearch = customerMessage;
			List<Job> jobs = new ArrayList<>();
			List<Job> jobsToDisplay = new ArrayList<>();

			jobs = jobRepository.findByAreaOrStation(addressToSearch);

			if (jobs.size() != 0) {
				if (jobs.size() <= 5) {
					jobsToDisplay.addAll(jobs);
				} else {
					for (int i = 0; i < 5; i++) {
						jobsToDisplay.add(jobs.get(i));
					}
				}

				carouselForUser(userId, CHANNEL_ACCESS_TOKEN, jobsToDisplay);

				ConfirmTemplate confirmTemplate = new ConfirmTemplate("Any interesting jobs?",
						new MessageAction("yes", "interesting jobs"), new MessageAction("No", "not interesting jobs"));
				TemplateMessage templateMessage = new TemplateMessage("Any interesting jobs?", confirmTemplate);
				PushMessage pushMessage = new PushMessage(userId, templateMessage);
				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
						.pushMessage(pushMessage).execute();

				ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
				chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
				chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
				chatMessageLineToAdd.setMessageText("Send jobs carousel");
				chatMessageLineToAdd.setReadState(false);
				chatMessageLineToAdd.setMessageDate((new Date()));
				chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);

				ChatMessageLine chatMessageLineToAdd2 = new ChatMessageLine();
				chatMessageLineToAdd2.setChatLineAdmin(candidate.getChatLineAdmin());
				chatMessageLineToAdd2.setMessageDate((new Date()));
				chatMessageLineToAdd2.setMessageText("Any interesting jobs?");
				chatMessageLineToAdd2.setReadState(false);
				chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd2);

			} else {

				TextMessage textMessage = new TextMessage("No jobs found. Please enter a valid area name or station");
				PushMessage pushMessage = new PushMessage(userId, textMessage);
				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
						.pushMessage(pushMessage).execute();

				ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
				chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
				chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
				chatMessageLineToAdd.setMessageText("No jobs found. Please enter a valid area name or station");
				chatMessageLineToAdd.setReadState(false);
				chatMessageLineToAdd.setMessageDate((new Date()));
				chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
			}
		}

		if (intentName.equals("not interesting jobs")) {
			List<Job> jobs = new ArrayList<>();
			List<Job> jobsToDisplay = new ArrayList<>();

			jobs = jobRepository.findByAreaOrStation(addressToSearch);

			if (jobs.size() != 0) {
				if (jobs.size() <= 5) {
					jobsToDisplay.addAll(jobs);
				} else {
					for (int i = 0; i < 5; i++) {
						jobsToDisplay.add(jobs.get(i));
					}
				}

				carouselForUser(userId, CHANNEL_ACCESS_TOKEN, jobsToDisplay);

				ConfirmTemplate confirmTemplate = new ConfirmTemplate("Any interesting jobs?",
						new MessageAction("yes", "interesting jobs"),
						new MessageAction("No", "not interesting jobs again"));
				TemplateMessage templateMessage = new TemplateMessage("Any interesting jobs?", confirmTemplate);
				PushMessage pushMessage = new PushMessage(userId, templateMessage);
				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
						.pushMessage(pushMessage).execute();

				ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
				chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
				chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
				chatMessageLineToAdd.setMessageText("Send jobs carousel");
				chatMessageLineToAdd.setReadState(false);
				chatMessageLineToAdd.setMessageDate((new Date()));
				chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);

				ChatMessageLine chatMessageLineToAdd2 = new ChatMessageLine();
				chatMessageLineToAdd2.setChatLineAdmin(candidate.getChatLineAdmin());
				chatMessageLineToAdd2.setMessageDirection(candidate.getIdUser());
				chatMessageLineToAdd2.setMessageText("Any interesting jobs?");
				chatMessageLineToAdd2.setReadState(false);
				chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd2);
			}
		}

		if (intentName.equals("not interesting jobs again")) {

			Shop shop = new Shop();

			if (shopRepository.findByNameShop("admin shop") == null) {
				Shop shopToAdd = new Shop();
				shopToAdd.setNameShop("admin shop");
				shopRepository.saveAndFlush(shopToAdd);
			}

			shop = shopRepository.findByNameShop("admin shop");

			ShopCandidateRelation shopCandidateRelation = new ShopCandidateRelation();
			ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
			shopCandidateRelationPK.setIdCandidate(candidate.getIdUser());
			shopCandidateRelationPK.setIdShop(shop.getIdShop());

			shopCandidateRelation = shopCandidateRelationRepository.findOne(shopCandidateRelationPK);

			if (shopCandidateRelation != null) {
				shopCandidateRelation.setProgress("Potential Candidate");
				shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);
			}

			ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
					"https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png", "Reason",
					"Please choose your reason",
					Arrays.asList(new MessageAction("Location", "Location"), new MessageAction("Salary", "Salary"),
							new MessageAction("Work Time", "Work Time"), new MessageAction("Others", "Others")));
			TemplateMessage templateMessage = new TemplateMessage("Reason", buttonsTemplate);

			PushMessage pushMessage = new PushMessage(userId, templateMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("Please choose your reason");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
		}

		if (intentName.equals("Location")) {
			TextMessage textMessage = new TextMessage("Thank you! We will contact you again!");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("Thank you! We will contact you again!");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
		}

		if (intentName.equals("Salary")) {
			TextMessage textMessage = new TextMessage("Thank you! We will contact you again!");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("Thank you! We will contact you again!");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
		}

		if (intentName.equals("Job position")) {
			TextMessage textMessage = new TextMessage("Thank you! We will contact you again!");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("Thank you! We will contact you again!");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
		}

		if (intentName.equals("Work Time")) {
			TextMessage textMessage = new TextMessage("Thank you! We will contact you again!");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("Thank you! We will contact you again!");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
		}

		if (intentName.equals("Others")) {

			Shop shop = new Shop();

			if (shopRepository.findByNameShop("admin shop") == null) {
				Shop shopToAdd = new Shop();
				shopToAdd.setNameShop("admin shop");
				shopRepository.saveAndFlush(shopToAdd);
			}

			shop = shopRepository.findByNameShop("admin shop");

			ShopCandidateRelation shopCandidateRelation = new ShopCandidateRelation();
			ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
			shopCandidateRelationPK.setIdCandidate(candidate.getIdUser());
			shopCandidateRelationPK.setIdShop(shop.getIdShop());

			shopCandidateRelation = shopCandidateRelationRepository.findOne(shopCandidateRelationPK);

			if (shopCandidateRelation != null) {
				shopCandidateRelation.setProgress("Potential Candidate");
				shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);
			}

			TextMessage textMessage = new TextMessage("What is the reason?");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("What is the reason?");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
		}

		if (intentName.equals("Yes I called")) {
			ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
					"https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png",
					"Did you confirm the interview time?", "Did you confirm the interview time?",
					Arrays.asList(new MessageAction("Confirmed", "Interview confirmed"),
							new MessageAction("Not confirmed", "Interview not confirmed"),
							new MessageAction("No interview", "No interview")));
			TemplateMessage templateMessage = new TemplateMessage("Reason", buttonsTemplate);

			PushMessage pushMessage = new PushMessage(userId, templateMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("Did you confirm the interview time?");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);

			System.out.println("botScheduler.getShop() : " + botScheduler.getShop().getIdShop());

			ShopCandidateRelation shopCandidateRelation = new ShopCandidateRelation();
			ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
			shopCandidateRelationPK.setIdCandidate(candidate.getIdUser());
			shopCandidateRelationPK.setIdShop(botScheduler.getShop().getIdShop());

			shopCandidateRelation = shopCandidateRelationRepository.findOne(shopCandidateRelationPK);

			if (shopCandidateRelation != null) {
				shopCandidateRelation.setAskInterviewDate((new Date()));
				shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);
			}
		}

		if (intentName.equals("No I did not")) {

			TextMessage textMessage = new TextMessage("Please call the shop");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("Please call the shop");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
		}

		if (intentName.equals("No interview")) {
			ConfirmTemplate confirmTemplate = new ConfirmTemplate("Do you want to apply for a job again?",
					new MessageAction("yes", "Yes I want to apply for a job again"),
					new MessageAction("No", "No I do not want to apply for a job again"));
			TemplateMessage templateMessage = new TemplateMessage("Do you want to apply for a job again?",
					confirmTemplate);
			PushMessage pushMessage = new PushMessage(userId, templateMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ShopCandidateRelation shopCandidateRelation = new ShopCandidateRelation();
			ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
			shopCandidateRelationPK.setIdCandidate(candidate.getIdUser());
			shopCandidateRelationPK.setIdShop(botScheduler.getShop().getIdShop());

			shopCandidateRelation = shopCandidateRelationRepository.findOne(shopCandidateRelationPK);

			if (shopCandidateRelation != null) {
				shopCandidateRelation.setConfirmedInterview(false);
				shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);
			}

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("Do you want to apply for a job again?");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
		}

		if (intentName.equals("Yes I want to apply for a job again")) {
			TextMessage textMessage = new TextMessage("Please enter an area or a station");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("Please enter an area or a station");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
		}

		if (intentName.equals("Interview not confirmed")) {
			ShopCandidateRelation shopCandidateRelation = new ShopCandidateRelation();
			ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
			shopCandidateRelationPK.setIdCandidate(candidate.getIdUser());
			shopCandidateRelationPK.setIdShop(botScheduler.getShop().getIdShop());

			shopCandidateRelation = shopCandidateRelationRepository.findOne(shopCandidateRelationPK);

			if (shopCandidateRelation != null) {
				shopCandidateRelation.setConfirmedInterview(false);
				shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);
			}
		}

		if (intentName.equals("Interview confirmed")) {

			// DatetimePickerAction date = new DatetimePickerAction("rihab", "rihab",
			// "datetime", "2017-06-18T06:15",
			// "2100-12-31T23:59", "1900-01-01T00:00");
			// CarouselTemplate carouselTemplate = new CarouselTemplate(Arrays.asList(new
			// CarouselColumn(
			// "https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png",
			// "Datetime Picker", "Please select a date, time or datetime",
			// Arrays.asList(date))));
			//
			// TemplateMessage templateMessage1 = new TemplateMessage("date time picker",
			// carouselTemplate);
			// PushMessage pushMessage1 = new PushMessage(userId, templateMessage1);
			// Response<BotApiResponse> response1 =
			// LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
			// .pushMessage(pushMessage1).execute();
			ShopCandidateRelation shopCandidateRelation = new ShopCandidateRelation();
			ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
			shopCandidateRelationPK.setIdCandidate(candidate.getIdUser());
			shopCandidateRelationPK.setIdShop(botScheduler.getShop().getIdShop());

			shopCandidateRelation = shopCandidateRelationRepository.findOne(shopCandidateRelationPK);

			if (shopCandidateRelation != null) {
				shopCandidateRelation.setConfirmedInterview(true);
				shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);
			}
		}

		if (intentName.equals("interview-time")) {
			if (parameters == null) {
				TextMessage textMessage = new TextMessage("Please enter a valid date and time");
				PushMessage pushMessage = new PushMessage(userId, textMessage);
				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
						.pushMessage(pushMessage).execute();

				ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
				chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
				chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
				chatMessageLineToAdd.setMessageText("Please enter a valid date and time");
				chatMessageLineToAdd.setReadState(false);
				chatMessageLineToAdd.setMessageDate((new Date()));
				chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);

			} else {

				if (parameters != null && parameters.getString("date") != null
						&& !parameters.getString("date").equals("")) {

					ShopCandidateRelation shopCandidateRelation = new ShopCandidateRelation();
					ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
					shopCandidateRelationPK.setIdCandidate(candidate.getIdUser());
					shopCandidateRelationPK.setIdShop(botScheduler.getShop().getIdShop());

					shopCandidateRelation = shopCandidateRelationRepository.findOne(shopCandidateRelationPK);

					if (parameters.getString("time") != null && !parameters.getString("time").equals("")) {
						if (shopCandidateRelation != null) {
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							String dateTime = parameters.getString("date") + " " + parameters.getString("time");
							Date interviewDate = formatter.parse(dateTime);
							shopCandidateRelation.setInterviewDate(interviewDate);
							shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);
						}
					} else {
						if (shopCandidateRelation != null) {
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							String dateTime = parameters.getString("date");
							Date interviewDate = formatter.parse(dateTime);
							shopCandidateRelation.setInterviewDate(interviewDate);
							shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);
						}
					}

					TextMessage textMessage = new TextMessage("Okay, good luck!");
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
							.pushMessage(pushMessage).execute();

					ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
					chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
					chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
					chatMessageLineToAdd.setMessageText("Okay, good luck!");
					chatMessageLineToAdd.setReadState(false);
					chatMessageLineToAdd.setMessageDate((new Date()));
					chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);

				}
			}
		}

		if (intentName.equals("No I failed")) {
			ConfirmTemplate confirmTemplate = new ConfirmTemplate("Do you want to apply for a job again?",
					new MessageAction("yes", "Yes I want to apply for a job again"),
					new MessageAction("No", "No I do not want to apply for a job again"));
			TemplateMessage templateMessage = new TemplateMessage("Do you want to apply for a job again?",
					confirmTemplate);
			PushMessage pushMessage = new PushMessage(userId, templateMessage);
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()
					.pushMessage(pushMessage).execute();

			ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
			chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
			chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
			chatMessageLineToAdd.setMessageText("Do you want to apply for a job again?");
			chatMessageLineToAdd.setReadState(false);
			chatMessageLineToAdd.setMessageDate((new Date()));
			chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);
		}

		return json;
	}

	/**
	 * 
	 * @author Rihab Kallel
	 * 
	 *         Method for send carousel template message to use
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
