package sep.project.services;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.MerchantPreferences;
import com.paypal.api.payments.Patch;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentDefinition;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.Plan;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import sep.project.dto.BillingAgreementDTO;
import sep.project.dto.BillingPlanDTO;
import sep.project.dto.PaymentDTO;
import sep.project.model.BillingPlan;
import sep.project.model.BillingType;
import sep.project.model.Client;
import sep.project.model.Subscription;
import sep.project.model.SubscriptionStatus;
import sep.project.model.TransactionStatus;


@Service
public class PayPalService {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private BillingPlanService billingPlanService;
	
	@Autowired
	private SubscriptionService subscriptionService;
		
	@Value("${success_url_payment}")
	private String successPaymentURL;
	
	@Value("${success_url_agreement}")
	private String successAgreementURL;

	@Value("${cancel_url}")
	private String cancelURL;
	
	private String executionMode = "sandbox";
	
	public String createPayment(PaymentDTO paymentDTO, Client client) throws PayPalRESTException {
		
	    Payer payer = new Payer();
	    payer.setPaymentMethod("paypal");
	    
	    RedirectUrls redirectUrls = new RedirectUrls();
	    redirectUrls.setCancelUrl(cancelURL);
	    redirectUrls.setReturnUrl(successPaymentURL + paymentDTO.getEmail());

		Amount amount = new Amount();
		amount.setCurrency(paymentDTO.getPaymentCurrency());
		amount.setTotal(paymentDTO.getPaymentAmount().toString());
	    
	    Transaction transaction = new Transaction();
	    transaction.setAmount(amount);
	    transaction.setDescription(client.getEmail() + " payment");
	    
	    List<Transaction> transactions = new ArrayList<Transaction>();
	    transactions.add(transaction);
	    
	    //create the payment object
	    Payment payment = new Payment("sale", payer);
	    payment.setTransactions(transactions);
	    payment.setRedirectUrls(redirectUrls);
	    	    	    
	    //save transaction with transaction status INITIATED
	    sep.project.model.Transaction paypalTransaction = new sep.project.model.Transaction(client, new Date(), TransactionStatus.INITIATED, paymentDTO.getPaymentAmount(), paymentDTO.getPaymentCurrency(), paymentDTO.getSuccessUrl(), paymentDTO.getErrorUrl(), paymentDTO.getFailedUrl());
	    sep.project.model.Transaction savedTransaction = transactionService.save(paypalTransaction);
	    
	    APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);

    	String redirectUrl = "";
    	
	    try {	    	
	    	//create the payment
	    	Payment newPayment = payment.create(context);
	    		    
	    	if(newPayment != null) {
				//get the approval url from the response 
				Iterator links = newPayment.getLinks().iterator();		
				
				while(links.hasNext()) {
					Links link = (Links) links.next();
					
					if(link.getRel().equalsIgnoreCase("approval_url")) {
						redirectUrl = link.getHref();                  
	    				break;
					}
				}
								
				//save payment id and set status to CREATED
				savedTransaction.setPaymentId(newPayment.getId());
				savedTransaction.setStatus(TransactionStatus.CREATED);
				transactionService.save(savedTransaction);
	    	}    	
		} 
	    catch (PayPalRESTException e) {						
			//set transaction status to CANCELED
			savedTransaction.setStatus(TransactionStatus.CANCELED);
			transactionService.save(savedTransaction);
			
    	    throw e;    
		}
	 
		//to redirect the customer to the paypal site	    
	    return redirectUrl;    
 	}
	
	public void executePayment(String paymentId, String token, String PayerID, Client client) throws PayPalRESTException {
		
		Payment payment = new Payment();
		payment.setId(paymentId);
		
	    PaymentExecution paymentExecution = new PaymentExecution();
	    paymentExecution.setPayerId(PayerID);
	    
	    sep.project.model.Transaction transaction = transactionService.findByPaymentId(paymentId);
	    
        APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);

	    try {
	        //execute the payment
	        Payment createdPayment = payment.execute(context, paymentExecution);  
	        
	        //set transaction status to COMPLETED
			transaction.setStatus(TransactionStatus.COMPLETED);
			transactionService.save(transaction);
	    } 
	    catch (PayPalRESTException e) {    
	    	//set transaction status to CANCELED
			transaction.setStatus(TransactionStatus.CANCELED);
			transactionService.save(transaction);
			
			throw e;
	    }    
	}
	
	public void createBillingPlan(BillingPlanDTO billingPlanDTO, Client client) throws PayPalRESTException {
						
		//set currency and value
		Currency currency = new Currency(billingPlanDTO.getPaymentCurrency(), billingPlanDTO.getPaymentAmount().toString());
						
		PaymentDefinition paymentDefinition = new PaymentDefinition();
		paymentDefinition.setName(client.getEmail() + " subscription");
		paymentDefinition.setType("REGULAR");
		paymentDefinition.setFrequency(billingPlanDTO.getFrequency().toString());
		paymentDefinition.setFrequencyInterval("1");
		paymentDefinition.setCycles(billingPlanDTO.getCyclesNumber().toString());
		
		paymentDefinition.setAmount(currency);
		
		List<PaymentDefinition> paymentDefinitionList = new ArrayList<PaymentDefinition>();
		paymentDefinitionList.add(paymentDefinition);
				
		MerchantPreferences merchantPreferences = new MerchantPreferences(cancelURL, successAgreementURL+client.getEmail());
		merchantPreferences.setAutoBillAmount("YES");
		merchantPreferences.setInitialFailAmountAction("CONTINUE");
				
		//create a plan with infinite number of payment cycles
		Plan plan = new Plan();
		plan.setType("FIXED");
		plan.setName(client.getEmail() + " subscription");
		plan.setDescription(billingPlanDTO.getPaymentAmount() + " " + billingPlanDTO.getPaymentCurrency() + " a " + billingPlanDTO.getFrequency().toString().toLowerCase());
		
		plan.setPaymentDefinitions(paymentDefinitionList);
		plan.setMerchantPreferences(merchantPreferences);
		
		APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);
		
		try {
			  //create the plan
			  Plan createdPlan = plan.create(context);
			  
			  //update plan state to ACTIVE
			  List<Patch> patchRequestList = new ArrayList<Patch>();
			  Map<String, String> value = new HashMap<String, String>();
			  value.put("state", "ACTIVE");

			  Patch patch = new Patch();
			  patch.setPath("/");
			  patch.setValue(value);
			  patch.setOp("replace");
			  patchRequestList.add(patch);

			  //activate the plan
			  createdPlan.update(context, patchRequestList);
			  
			  //save the billing plan
			  BillingPlan billingPlan = new BillingPlan(billingPlanDTO.getPaymentAmount(), billingPlanDTO.getPaymentCurrency(), billingPlanDTO.getFrequency(), BillingType.FIXED, createdPlan.getId(), billingPlanDTO.getCyclesNumber());
			  BillingPlan savedBillingPlan = billingPlanService.save(billingPlan);
			  
			  //add billing plan to the client
			  client.getBillingPlans().add(savedBillingPlan);
			  clientService.save(client);
		} 
		catch (PayPalRESTException e) {			
			throw e;
		}	
	}
	
	public String createBillingAgreement(BillingAgreementDTO billingAgreementDTO, Client client, BillingPlan billingPlan) throws PayPalRESTException, MalformedURLException, UnsupportedEncodingException {
		//get date for the agreement				
		Date date = new Date();		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, 1);
		
		//format defined in ISO8601
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String formattedDate = sdf.format(c.getTime());
		
		//set billing plan id
		Plan plan = new Plan();
		plan.setId(billingPlan.getBillingPlanId());
		
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");
				
		//create the agreement object
		Agreement agreement = new Agreement();
		agreement.setName(client.getEmail() + " subscription");
		agreement.setDescription(client.getEmail() + " subscription");
		agreement.setStartDate(formattedDate);
		
		agreement.setPlan(plan);	
		agreement.setPayer(payer);
		
		
		//save subscription with status INITIATED
		Subscription subscription = new Subscription(billingPlan, client, SubscriptionStatus.INITIATED, billingAgreementDTO.getSuccessUrl(), billingAgreementDTO.getErrorUrl(), billingAgreementDTO.getFailedUrl());
		Subscription savedSubscription = subscriptionService.save(subscription);
		
		APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);
		
		String redirectUrl = "";
		
		try {
			//create the agreement
			Agreement newAgreement = agreement.create(context);
						
			if(newAgreement != null) {
				//get the approval url from the response 
				Iterator links = newAgreement.getLinks().iterator();		
				
				while(links.hasNext()) {
					Links link = (Links) links.next();
					
					if(link.getRel().equalsIgnoreCase("approval_url")) {
						redirectUrl = link.getHref();                  
	    				break;
					}
				}
			}
			
			//save agreement id and set status to CREATED
			savedSubscription.setToken(newAgreement.getToken());
			savedSubscription.setStatus(SubscriptionStatus.CREATED);
			subscriptionService.save(savedSubscription);
		} 
		catch (PayPalRESTException e) {
			throw e;
		} 
		catch (MalformedURLException e) {
			throw e;
		} 
		catch (UnsupportedEncodingException e) {
			throw e;
		}
		
		//to redirect the customer to the paypal site	    
	    return redirectUrl;  
	}
	
	public void executeBillingAgreement(Client client, String token) throws PayPalRESTException {
		
		Agreement agreement =  new Agreement();
		agreement.setToken(token);
		
		APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);

		try {
			//execute the agreement and sign up the user for the subscription
			Agreement createdAgreement = agreement.execute(context, agreement.getToken());
			
			//set status to COMPLETED
			Subscription subscription = subscriptionService.findByToken(token);
			subscription.setStatus(SubscriptionStatus.COMPLETED);
			subscriptionService.save(subscription);
		} 
		catch (PayPalRESTException e) {
			throw e;
		}
	}
	
	/**
	 * Compare transaction statues with PayPal every hour
	 */
	@Scheduled(initialDelay = 10000, fixedRate = 3600000)
	public void synchronizeTransactions() {
		//find all PayPal clients
		List<Client> clientsList = clientService.findAll();
		
		for(Client client : clientsList) {
			//find all transaction with status INITIATED
			List<sep.project.model.Transaction> transactions = transactionService.findAllCreatedTransactions(client);
			
			if(transactions.size() > 0) {				
				APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);
				
				for(sep.project.model.Transaction transaction : transactions) {
					try {
						//get payment details
						Payment payment = Payment.get(context, transaction.getPaymentId());						
						
						if(payment.getState().equalsIgnoreCase("APPROVED")){
							//update transaction status to COMPLETED
							transaction.setStatus(TransactionStatus.COMPLETED);
							transactionService.save(transaction);
						}
						else if(payment.getState().equalsIgnoreCase("FAILED")){
							//update transaction status to CANCELED
							transaction.setStatus(TransactionStatus.CANCELED);
							transactionService.save(transaction);
						}
					}
					catch(PayPalRESTException e) {
						//if transaction doesn't exist
						if(e.getResponsecode() == 404) {
							transaction.setStatus(TransactionStatus.CANCELED);
							transactionService.save(transaction);
						}
					}
				}
			}	
		}
	}
	
}
		
