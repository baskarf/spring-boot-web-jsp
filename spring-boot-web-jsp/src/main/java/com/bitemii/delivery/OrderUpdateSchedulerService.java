package com.bitemii.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bitemii.delivery.service.DeliveryService;

@Service
public class OrderUpdateSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(OrderUpdateSchedulerService.class);
    
    @Autowired
    private DeliveryService serviceObj;
    
    @Scheduled(fixedRateString = "${delivery.fixedRate}", initialDelayString = "${delivery.fixedDelay}")
    public void assignDeliveryForNewOrders() {
    	
    	//serviceObj.assignOrderToDeliveryStaff();
        logger.debug("Assign Delivery boy for new Orders");
    }
}
