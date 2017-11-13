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
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.richmenu.RichMenu;
import com.linecorp.bot.model.richmenu.RichMenuArea;
import com.linecorp.bot.model.richmenu.RichMenuBounds;
import com.linecorp.bot.model.richmenu.RichMenuSize;

@RestController
public class BotController {

	// class declaration
	private Candidate candidateToRegister = new Candidate();

	// channel token declaration
	private static final String CHANNEL_ACCESS_TOKEN = "wvydTwaiKtsG4Z90XPfG6hWB31/TX2tceTz+v1NqSXgOMgUZ55c4GnZZ6rd+i9lJn8d0k17/7A5E0Mq1kKpmAdMKWkmqGaiezxDAZykxJIA8MoDYx+a19t4cQbRd5zLWl3k30y2pSM1zzZQz/JVSjwdB04t89/1O/w1cDnyilFU=";

	// String declaration
	private String searchCriteria = "address";
	private String addressToSearch;

	// Repositories and services injection
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

	// scheduler declaration
	@Autowired
	BotScheduler botScheduler;

	/**
	 * @author Rihab Kallel
	 * 
	 *         Dialog flow api webhook service for line bot, gets and sends message
	 *         to line bot
	 * @param obj
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 * @throws Exception
	 */
	@RequestMapping(value = "/webhook", method = RequestMethod.POST)
	private @ResponseBody Map<String, Object> webhook(@RequestBody Map<String, Object> obj)
			throws JSONException, IOException, Exception {

		System.out.println("*****************WEBHOOK*********************");

		Candidate candidate = new Candidate();

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

		// create candidate if not registered
		if (candidateRepository.findByUserLineId(userId) == null) {
			candidateToRegister = new Candidate();
			candidateToRegister.setUserLineId(userId);
			candidateRepository.saveAndFlush(candidateToRegister);
		}

		candidate = candidateRepository.findByUserLineId(userId);

		// create shop candidate relations if not exist
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

		// create chatLineAdmin if not exists
		if (candidate != null) {
			if (candidate.getChatLineAdmin() == null) {
				chatLineAdminRepository.saveAndFlush(chatLineAdmin);
				candidate.setChatLineAdmin(chatLineAdmin);
				candidateRepository.saveAndFlush(candidate);
			}
		}

		// save every user message from bot
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

			RichMenuArea richMenuArea = new RichMenuArea(new RichMenuBounds(0, 0, 2500, 1686),
					new PostbackAction(null, "action=buy&itemid=123"));
			RichMenu richMenu = RichMenu.builder().size(RichMenuSize.FULL).selected(false).name("Nice richmenu")
					.chatBarText("Tap here").build();

			System.out.println("searchCriteria : " + searchCriteria);

			if (searchCriteria.equals("address")) {
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
							new MessageAction("yes", "interesting jobs"),
							new MessageAction("No", "not interesting jobs"));
					TemplateMessage templateMessage = new TemplateMessage("Any interesting jobs?", confirmTemplate);
					PushMessage pushMessage = new PushMessage(userId, templateMessage);
					LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

					saveChatLineMessage(candidate, "Send jobs carousel");
					saveChatLineMessage(candidate, "Any interesting jobs?");

				} else {

					TextMessage textMessage = new TextMessage(
							"No jobs found. Please enter a valid area name or station");
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

					saveChatLineMessage(candidate, "No jobs found. Please enter a valid area name or station");

				}
			} else {

				if (searchCriteria.equals("location")) {
					String location = customerMessage;
					List<Job> jobs = new ArrayList<>();
					List<Job> jobsToDisplay = new ArrayList<>();
					jobs = jobRepository.findByAreaOrStation(location);

					if (jobs.size() != 0) {
						if (jobs.size() <= 5) {
							jobsToDisplay.addAll(jobs);
						} else {
							for (int i = 0; i < 5; i++) {
								jobsToDisplay.add(jobs.get(i));
							}
						}
						carouselForUser(userId, CHANNEL_ACCESS_TOKEN, jobsToDisplay);
						saveChatLineMessage(candidate, "Send jobs carousel");
					} else {
						TextMessage textMessage = new TextMessage("No jobs found. Please enter a valid location");
						PushMessage pushMessage = new PushMessage(userId, textMessage);
						LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage)
								.execute();

						saveChatLineMessage(candidate, "No jobs found. Please enter a valid location");
					}
				}

				if (searchCriteria.equals("salary")) {

					String salary = customerMessage;
					List<Job> jobs = new ArrayList<>();
					List<Job> jobsToDisplay = new ArrayList<>();

					double salaryToSearch = 0;

					try {
						salaryToSearch = Double.parseDouble(salary);

						jobs = jobRepository.findByAreaOrStationAndSalary(addressToSearch, salaryToSearch);

						if (jobs.size() != 0) {
							if (jobs.size() <= 5) {
								jobsToDisplay.addAll(jobs);
							} else {
								for (int i = 0; i < 5; i++) {
									jobsToDisplay.add(jobs.get(i));
								}
							}
							carouselForUser(userId, CHANNEL_ACCESS_TOKEN, jobsToDisplay);
							saveChatLineMessage(candidate, "Send jobs carousel");
						}

					} catch (Exception e) {

						TextMessage textMessage = new TextMessage("No jobs found. Please enter a valid salary");
						PushMessage pushMessage = new PushMessage(userId, textMessage);
						LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage)
								.execute();
						saveChatLineMessage(candidate, "No jobs found. Please enter a valid salary");
						e.printStackTrace();
					}
				}

				if (searchCriteria.equals("work time")) {
					String workTime = customerMessage;
					List<Job> jobs = new ArrayList<>();
					List<Job> jobsToDisplay = new ArrayList<>();
					jobs = jobRepository.findByAreaOrStation(workTime);

					if (jobs.size() != 0) {
						if (jobs.size() <= 5) {
							jobsToDisplay.addAll(jobs);
						} else {
							for (int i = 0; i < 5; i++) {
								jobsToDisplay.add(jobs.get(i));
							}
						}
						carouselForUser(userId, CHANNEL_ACCESS_TOKEN, jobsToDisplay);
						saveChatLineMessage(candidate, "Send jobs carousel");
					} else {
						TextMessage textMessage = new TextMessage("No jobs found. Please enter a valid work time");
						PushMessage pushMessage = new PushMessage(userId, textMessage);
						LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage)
								.execute();
						saveChatLineMessage(candidate, "No jobs found. Please enter a valid work time");
					}
				}

				if (searchCriteria.equals("others")) {
					TextMessage textMessage = new TextMessage("Okay, thank you!");
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
					saveChatLineMessage(candidate, "Okay, thank you!");
					saveChatLineMessage(candidate, "Okay, thank you!");
					searchCriteria = "address";
				}
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
				LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

				saveChatLineMessage(candidate, "Send jobs carousel");
				saveChatLineMessage(candidate, "Any interesting jobs?");
			}
		}

		if (intentName.equals("not interesting jobs again")) {

			ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
					"https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png", "Reason",
					"Please choose your reason",
					Arrays.asList(new MessageAction("Location", "Location"), new MessageAction("Salary", "Salary"),
							new MessageAction("Work Time", "Work Time"), new MessageAction("Others", "Others")));
			TemplateMessage templateMessage = new TemplateMessage("Reason", buttonsTemplate);

			PushMessage pushMessage = new PushMessage(userId, templateMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "Please choose your reason");
		}

		if (intentName.equals("Location")) {
			searchCriteria = "location";
			TextMessage textMessage = new TextMessage("What is your preferred location?");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "What is your location?");
		}

		if (intentName.equals("Salary")) {
			searchCriteria = "salary";
			TextMessage textMessage = new TextMessage("What is your preferred hourly wage?");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "What is your preferred hourly wage?");
		}

		if (intentName.equals("Work Time")) {
			searchCriteria = "Work Time";
			TextMessage textMessage = new TextMessage("What is your preferred work time?");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "What is your work time?");
		}

		if (intentName.equals("Others")) {

			searchCriteria = "others";

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
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "What is the reason?");
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
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "Did you confirm the interview time?");

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
			TextMessage textMessage = new TextMessage(
					"Please call the shop: " + botScheduler.getShop().getPhoneNumber());
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "Please call the shop: " + botScheduler.getShop().getPhoneNumber());
		}

		if (intentName.equals("No interview")) {
			ConfirmTemplate confirmTemplate = new ConfirmTemplate("Do you want to apply for a job again?",
					new MessageAction("yes", "Yes I want to apply for a job again"),
					new MessageAction("No", "No I do not want to apply for a job again"));
			TemplateMessage templateMessage = new TemplateMessage("Do you want to apply for a job again?",
					confirmTemplate);
			PushMessage pushMessage = new PushMessage(userId, templateMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

			ShopCandidateRelation shopCandidateRelation = new ShopCandidateRelation();
			ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
			shopCandidateRelationPK.setIdCandidate(candidate.getIdUser());
			shopCandidateRelationPK.setIdShop(botScheduler.getShop().getIdShop());

			shopCandidateRelation = shopCandidateRelationRepository.findOne(shopCandidateRelationPK);

			if (shopCandidateRelation != null) {
				shopCandidateRelation.setConfirmedInterview(false);
				shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);
			}
			saveChatLineMessage(candidate, "Do you want to apply for a job again?");
		}

		if (intentName.equals("Yes I want to apply for a job again")) {
			TextMessage textMessage = new TextMessage("Please enter an area or a station");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "Please enter an area or a station");
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
				LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
				saveChatLineMessage(candidate, "Please enter a valid date and time");
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
					LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
					saveChatLineMessage(candidate, "Okay, good luck!");
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
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "Do you want to apply for a job again?");
		}

		return json;
	}

	/**
	 * @author Rihab Kallel
	 * 
	 *         Method for send carousel template message to user
	 * @param userId
	 *            userlineID
	 * @param lChannelAccessToken
	 *            channel access token
	 * @param jobsToDisplay
	 *            list of jobs for the carousel
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

		searchCriteria = "address";
		try {
			LineMessagingServiceBuilder.create(lChannelAccessToken).build().pushMessage(pushMessage).execute();
		} catch (IOException e) {
			System.out.println("Exception is raised ");
			e.printStackTrace();
		}
	}

	/**
	 * @author Rihab Kallel
	 * 
	 *         method to save every user and bot message to database
	 * @param candidate
	 * @param text
	 */
	private void saveChatLineMessage(Candidate candidate, String text) {

		ChatMessageLine chatMessageLineToAdd = new ChatMessageLine();
		chatMessageLineToAdd.setChatLineAdmin(candidate.getChatLineAdmin());
		chatMessageLineToAdd.setMessageDirection(candidate.getIdUser());
		chatMessageLineToAdd.setMessageText(text);
		chatMessageLineToAdd.setReadState(false);
		chatMessageLineToAdd.setMessageDate((new Date()));
		chatMessageLineRepository.saveAndFlush(chatMessageLineToAdd);

	}

}
