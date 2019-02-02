package com.bitemii.delivery;

import com.bitemii.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class WelcomeController {

    // inject via application.properties
    @Value("${welcome.message:test}")
    private String message = "Hello World";


    @Autowired
    private DeliveryService serviceObj;

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("message", "All Order Details");
        model.put("data", serviceObj.getAllNewOrder());

        return "welcome";
    }


    @RequestMapping("/getNewOrders")
    public String getNewOrders(Map<String, Object> model) {
        model.put("message", serviceObj.getNewOrders());

        return "welcome";
    }

    
    @RequestMapping("/getDeliveryStaffDetails")
    public String getDeliveryStaffDetails(Map<String, Object> model) {
        model.put("message", serviceObj.getAllDeliveryStaff());

        return "welcome";
    }

    @RequestMapping("/getAssignedDeliveryStaff")
    public String getAssignedDeliveryStaff(Map<String, Object> model) {
        model.put("message",serviceObj.assignedDeliveryStaff());
        //serviceObj.assignOrderToDeliveryStaff("YYM1548049324", 305, 207);
        return "welcome";
    }
}