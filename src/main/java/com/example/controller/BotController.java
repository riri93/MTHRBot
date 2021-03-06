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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.entity.BotInformation;
import com.example.entity.Candidate;
import com.example.entity.ChatLineAdmin;
import com.example.entity.ChatMessageLine;
import com.example.entity.Job;
import com.example.entity.Shop;
import com.example.entity.ShopCandidateRelation;
import com.example.entity.ShopCandidateRelationPK;
import com.example.repository.BotInformationRepository;
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

@RestController
public class BotController {

	// channel token declaration
	private static final String CHANNEL_ACCESS_TOKEN = "wvydTwaiKtsG4Z90XPfG6hWB31/TX2tceTz+v1NqSXgOMgUZ55c4GnZZ6rd+i9lJn8d0k17/7A5E0Mq1kKpmAdMKWkmqGaiezxDAZykxJIA8MoDYx+a19t4cQbRd5zLWl3k30y2pSM1zzZQz/JVSjwdB04t89/1O/w1cDnyilFU=";

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

	@Autowired
	BotInformationRepository botInformationRepository;

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

		final String uri = "https://hooks.slack.com/services/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";
		RestTemplate restTemplate = new RestTemplate();

		// SlackSession session = SlackSessionFactory.createWebSocketSlackSession(
		// "xoxp-27046751377-127332966816-272009034885-29a9b73ce611a58850e448ea7c4956d4");
		// session.connect();
		// SlackChannel channel = session.findChannelByName("testbot");

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

		// create admin shop if not exists
		if (shopRepository.findByNameShop("admin shop") == null) {
			Shop shopToAdd = new Shop();
			shopToAdd.setNameShop("admin shop");
			shopRepository.saveAndFlush(shopToAdd);
		}

		// create candidate if not registered
		if (candidateRepository.findByUserLineId(userId) == null) {
			Candidate candidateToRegister = new Candidate();
			candidateToRegister.setUserLineId(userId);
			candidateRepository.saveAndFlush(candidateToRegister);
		}

		candidate = candidateRepository.findByUserLineId(userId);

		// create bot information of null
		if (candidate.getBotInformation() == null) {
			BotInformation botInformation = new BotInformation();
			botInformation.setSearchCriteria("address");
			botInformationRepository.saveAndFlush(botInformation);

			candidate.setBotInformation(botInformation);
			candidateRepository.saveAndFlush(candidate);
		}

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

		// this intent treats every user message depending on his/her search criteria
		// if user enters a valid address ,5 jobs carousel is displayed
		// if user enters a non valid address , he is asked to enter a correct one
		// also treats search by salary, working time, location and the answer for
		// "other reason" message
		if (intentName.equals("Default Fallback Intent")) {

			if (candidate.getBotInformation().getSearchCriteria().equals("address")) {

				BotInformation botInformation = new BotInformation();
				botInformation = candidate.getBotInformation();
				botInformation.setAddressToSearch(customerMessage);
				botInformationRepository.saveAndFlush(botInformation);

				List<Job> jobs = new ArrayList<>();
				List<Job> jobsToDisplay = new ArrayList<>();

				jobs = jobRepository.findByAreaOrStation(candidate.getBotInformation().getAddressToSearch());

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

					String input = "{'text':'" + "userID: " + userId + " \n time: " + timestamp + " \n text: "
							+ customerMessage + "'}";

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);

					HttpEntity<String> entity = new HttpEntity<String>(input, headers);

					ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

					TextMessage textMessage = new TextMessage("Please enter a valid area or station address");
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

					saveChatLineMessage(candidate, "Please enter a valid area or station address");
				}

			} else {

				if (candidate.getBotInformation().getSearchCriteria().equals("location")) {
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

						BotInformation botInformation = new BotInformation();
						botInformation = candidate.getBotInformation();
						botInformation.setSearchCriteria("address");
						botInformationRepository.saveAndFlush(botInformation);

						saveChatLineMessage(candidate, "Send jobs carousel");
					} else {

						String input = "{'text':'" + "userID: " + userId + " \n time: " + timestamp + " \n text: "
								+ customerMessage + "'}";

						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);

						HttpEntity<String> entity = new HttpEntity<String>(input, headers);

						ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
								String.class);

						TextMessage textMessage = new TextMessage("No jobs found. Please enter a valid location");
						PushMessage pushMessage = new PushMessage(userId, textMessage);
						LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage)
								.execute();

						saveChatLineMessage(candidate, "No jobs found. Please enter a valid location");
					}
				} else if (candidate.getBotInformation().getSearchCriteria().equals("salary")) {

					String salary = customerMessage;
					List<Job> jobs = new ArrayList<>();
					List<Job> jobsToDisplay = new ArrayList<>();

					double salaryToSearch = 0;

					try {
						salaryToSearch = Double.parseDouble(salary);

						jobs = jobRepository.findByAreaOrStationAndSalary(
								candidate.getBotInformation().getAddressToSearch(), salaryToSearch);

						if (jobs.size() != 0) {
							if (jobs.size() <= 5) {
								jobsToDisplay.addAll(jobs);
							} else {
								for (int i = 0; i < 5; i++) {
									jobsToDisplay.add(jobs.get(i));
								}
							}
							carouselForUser(userId, CHANNEL_ACCESS_TOKEN, jobsToDisplay);
							BotInformation botInformation = new BotInformation();
							botInformation = candidate.getBotInformation();
							botInformation.setSearchCriteria("address");
							botInformationRepository.saveAndFlush(botInformation);

							saveChatLineMessage(candidate, "Send jobs carousel");
						} else {

							TextMessage textMessage = new TextMessage("No jobs found.");
							PushMessage pushMessage = new PushMessage(userId, textMessage);
							LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage)
									.execute();
							saveChatLineMessage(candidate, "No jobs found.");
						}

					} catch (Exception e) {

						String input = "{'text':'" + "userID: " + userId + " \n time: " + timestamp + " \n text: "
								+ customerMessage + "'}";

						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);

						HttpEntity<String> entity = new HttpEntity<String>(input, headers);

						ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity,
								String.class);

						TextMessage textMessage = new TextMessage("No jobs found. Please enter a valid salary");
						PushMessage pushMessage = new PushMessage(userId, textMessage);
						LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage)
								.execute();
						saveChatLineMessage(candidate, "No jobs found. Please enter a valid salary");
						e.printStackTrace();
					}
				} else if (candidate.getBotInformation().getSearchCriteria().equals("others")) {

					TextMessage textMessage = new TextMessage("Okay, thank you!");
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

					saveChatLineMessage(candidate, "Okay, thank you!");

					BotInformation botInformation = new BotInformation();
					botInformation = candidate.getBotInformation();
					botInformation.setSearchCriteria("address");
					botInformationRepository.saveAndFlush(botInformation);

				} else if (candidate.getBotInformation().getSearchCriteria().equals("work time")) {

					String input = "{'text':'" + "userID: " + userId + " \n time: " + timestamp + " \n text: "
							+ customerMessage + "'}";

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);

					HttpEntity<String> entity = new HttpEntity<String>(input, headers);

					ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

					TextMessage textMessage = new TextMessage("Please enter a valid date");
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

					saveChatLineMessage(candidate, "Please enter a valid date");

				} else if (candidate.getBotInformation().getSearchCriteria().equals("interview-time")) {

					TextMessage textMessage = new TextMessage("Please enter a valid date");
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

					saveChatLineMessage(candidate, "Please enter a valid date");

					String input = "{'text':'" + "userID: " + userId + " \n time: " + timestamp + " \n text: "
							+ customerMessage + "'}";

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);

					HttpEntity<String> entity = new HttpEntity<String>(input, headers);

					ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

				} else {

					TextMessage textMessage = new TextMessage(
							"I am sorry, I am having trouble understading your message. You can search for jobs by clicking on 'search for job menu'");
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

					saveChatLineMessage(candidate,
							"I am sorry, I am having trouble understading your message. You can search for jobs by clicking on 'search for job menu'");

					String input = "{'text':'" + "userID: " + userId + " \n time: " + timestamp + " \n text: "
							+ customerMessage + "'}";

					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);

					HttpEntity<String> entity = new HttpEntity<String>(input, headers);

					ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

				}
			}
		}

		// when user clicks the menu
		if (intentName.equals("search job")) {

			TextMessage textMessage = new TextMessage("Please enter an area or station address");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("address");
			botInformationRepository.saveAndFlush(botInformation);

			saveChatLineMessage(candidate, "Please enter an area or station address");
		}

		// when user clicks no for interesting jobs? question (first time)
		if (intentName.equals("not interesting jobs")) {

			List<Job> jobs = new ArrayList<>();
			List<Job> jobsToDisplay = new ArrayList<>();

			jobs = jobRepository.findByAreaOrStation(candidate.getBotInformation().getAddressToSearch());

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

		// when user clicks yes for interesting jobs? question
		if (intentName.equals("interesting jobs")) {

			TextMessage textMessage = new TextMessage("Thank you");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "Thank you");

		}

		// when user clicks no for interesting jobs? question (second time)
		if (intentName.equals("not interesting jobs again")) {

			ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
					"https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png", "Reason",
					"Please choose your reason",
					Arrays.asList(new MessageAction("Location", "Location"), new MessageAction("Salary", "Salary"),
							new MessageAction("Work Time", "Work Time"), new MessageAction("Others", "Others")));
			TemplateMessage templateMessage = new TemplateMessage("Reason", buttonsTemplate);

			PushMessage pushMessage = new PushMessage(userId, templateMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();

			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setAskForReasonDate((new Date()));
			botInformationRepository.saveAndFlush(botInformation);

			saveChatLineMessage(candidate, "Please choose your reason");
		}

		// when user chooses location as a reason
		if (intentName.equals("Location")) {
			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("location");
			botInformationRepository.saveAndFlush(botInformation);

			TextMessage textMessage = new TextMessage("What is your preferred location?");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "What is your location?");
		}

		// when user chooses salary as a reason
		if (intentName.equals("Salary")) {

			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("salary");
			botInformationRepository.saveAndFlush(botInformation);

			TextMessage textMessage = new TextMessage("What is your preferred hourly wage?");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "What is your preferred hourly wage?");
		}

		// when user chooses worktime as a reason
		if (intentName.equals("Work Time")) {
			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("work time");
			botInformationRepository.saveAndFlush(botInformation);

			TextMessage textMessage = new TextMessage("What is your preferred start working time?");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "What is your preferred start working time?");
		}

		// when user enters the start and finish time when asking about his/her
		// preferred working time
		if (intentName.equals("Work Time - start")) {

			if (parameters != null) {
				SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

				BotInformation botInformation = new BotInformation();
				botInformation = candidate.getBotInformation();
				botInformation.setStartWorkingTime(formatter.parse(parameters.getString("start-time")));
				botInformationRepository.saveAndFlush(botInformation);

				BotInformation botInformation2 = new BotInformation();
				botInformation2 = candidate.getBotInformation();
				botInformation2.setFinishWorkingTime(formatter.parse(parameters.getString("finish-time")));
				botInformationRepository.saveAndFlush(botInformation2);

				if (candidate.getBotInformation().getSearchCriteria().equals("work time")) {

					List<Job> jobs = new ArrayList<>();
					List<Job> jobsToDisplay = new ArrayList<>();

					jobs = jobRepository.findByAreaOrStationAndWorkTime(
							candidate.getBotInformation().getAddressToSearch(),
							candidate.getBotInformation().getStartWorkingTime(),
							candidate.getBotInformation().getFinishWorkingTime());

					if (jobs.size() != 0) {
						if (jobs.size() <= 5) {
							jobsToDisplay.addAll(jobs);
						} else {
							for (int i = 0; i < 5; i++) {
								jobsToDisplay.add(jobs.get(i));
							}
						}

						carouselForUser(userId, CHANNEL_ACCESS_TOKEN, jobsToDisplay);

						BotInformation botInformation3 = new BotInformation();
						botInformation3 = candidate.getBotInformation();
						botInformation3.setSearchCriteria("address");
						botInformationRepository.saveAndFlush(botInformation3);

						saveChatLineMessage(candidate, "Send jobs carousel");

					} else {
						TextMessage textMessage = new TextMessage("No jobs found.");
						PushMessage pushMessage = new PushMessage(userId, textMessage);
						LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage)
								.execute();
						saveChatLineMessage(candidate, "No jobs found.");

						BotInformation botInformation4 = new BotInformation();
						botInformation4 = candidate.getBotInformation();
						botInformation4.setSearchCriteria("address");
						botInformationRepository.saveAndFlush(botInformation4);

					}
				}
			}
		}

		// when user chooses others as a reason
		if (intentName.equals("Others")) {

			TextMessage textMessage = new TextMessage("What is the reason?");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "What is the reason?");

			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("others");
			botInformationRepository.saveAndFlush(botInformation);

			Shop shop = new Shop();
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

			String input = "{'text':'" + "userID: " + userId + " \n time: " + timestamp
					+ " \n Potential candidate: True'}";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<String>(input, headers);

			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

		}

		// when user clicks on yes when he is asked whether he got in contact with the
		// shop or not
		if (intentName.equals("Yes I called")) {

			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("confirm interview");
			botInformationRepository.saveAndFlush(botInformation);

			ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
					"https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png",
					"Did you confirm the interview time?", "Did you confirm the interview time?",
					Arrays.asList(new MessageAction("Confirmed", "Interview confirmed"),
							new MessageAction("Not confirmed", "Interview not confirmed"),
							new MessageAction("No interview", "No interview")));
			TemplateMessage templateMessage = new TemplateMessage("Did you confirm the interview time?",
					buttonsTemplate);

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

		// when user clicks on no when he is asked whether he got in contact with the
		// shop or not
		if (intentName.equals("No I did not")) {

			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("confirm interview");
			botInformationRepository.saveAndFlush(botInformation);

			TextMessage textMessage = new TextMessage(
					"Please call the shop: " + botScheduler.getShop().getPhoneNumber());
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "Please call the shop: " + botScheduler.getShop().getPhoneNumber());
		}

		// when user clicks on no interview when he is asked about the interview time
		if (intentName.equals("No interview")) {

			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("confirm interview");
			botInformationRepository.saveAndFlush(botInformation);

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

		// when user clicks on yes when he is asked whether he wants to apply for a job
		// again
		if (intentName.equals("Yes I want to apply for a job again")) {

			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("address");
			botInformationRepository.saveAndFlush(botInformation);

			TextMessage textMessage = new TextMessage("Please enter an area or a station");
			PushMessage pushMessage = new PushMessage(userId, textMessage);
			LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
			saveChatLineMessage(candidate, "Please enter an area or a station");
		}

		// when user clicks on interview not confirmed when he is asked about the
		// interview time
		if (intentName.equals("Interview not confirmed")) {

			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("confirm interview");
			botInformationRepository.saveAndFlush(botInformation);

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

		// when user clicks on interview confirmed when he is asked about the
		// interview time
		if (intentName.equals("Interview confirmed")) {

			BotInformation botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("interview-time");
			botInformationRepository.saveAndFlush(botInformation);

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

		// when user clicks on interview confirmed he is asked about the interview time
		// (enter a valid date and time)
		if (intentName.equals("interview-time")) {

			if (parameters == null) {
				TextMessage textMessage = new TextMessage(
						"Please enter a valid date and time (ex: mm/dd/yyyy, tomorrow 9am, next monday)");
				PushMessage pushMessage = new PushMessage(userId, textMessage);
				LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
				saveChatLineMessage(candidate,
						"Please enter a valid date and time (ex: mm/dd/yyyy, tomorrow 9am, next monday)");
			} else {

				ShopCandidateRelation shopCandidateRelation = new ShopCandidateRelation();
				ShopCandidateRelationPK shopCandidateRelationPK = new ShopCandidateRelationPK();
				shopCandidateRelationPK.setIdCandidate(candidate.getIdUser());
				shopCandidateRelationPK.setIdShop(botScheduler.getShop().getIdShop());

				shopCandidateRelation = shopCandidateRelationRepository.findOne(shopCandidateRelationPK);
				if (shopCandidateRelation != null) {

					if (parameters != null && parameters.getString("date") != null
							&& !parameters.getString("date").equals("")) {

						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						String date = parameters.getString("date");
						Date interviewDate = formatter.parse(date);
						shopCandidateRelation.setInterviewDate(interviewDate);
						shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

						if (parameters.getString("time") != null && !parameters.getString("time").equals("")) {

							SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							String dateTime = parameters.getString("date") + " " + parameters.getString("time");
							Date interviewDate1 = formatter1.parse(dateTime);
							shopCandidateRelation.setInterviewDate(interviewDate1);
							shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

						}

					} else if (parameters.getString("date-time") != null
							&& !parameters.getString("date-time").equals("")) {

						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						String dateTime = parameters.getString("date-time");
						Date interviewDate = formatter.parse(dateTime);
						shopCandidateRelation.setInterviewDate(interviewDate);
						shopCandidateRelationRepository.saveAndFlush(shopCandidateRelation);

					} else {

						TextMessage textMessage = new TextMessage("Please enter a valid date and time (mm/dd/yyyy)");
						PushMessage pushMessage = new PushMessage(userId, textMessage);
						LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage)
								.execute();
						saveChatLineMessage(candidate, "Please enter a valid date and time (mm/dd/yyyy)");
					}

					TextMessage textMessage = new TextMessage("Okay, good luck!");
					PushMessage pushMessage = new PushMessage(userId, textMessage);
					LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build().pushMessage(pushMessage).execute();
					saveChatLineMessage(candidate, "Okay, good luck!");
				}
			}

		}

		// when user did not pass the interview
		if (intentName.equals("No I failed")) {

			BotInformation botInformation = new BotInformation();
			botInformation = candidate.getBotInformation();
			botInformation.setSearchCriteria("address");
			botInformationRepository.saveAndFlush(botInformation);

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
