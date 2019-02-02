package com.bitemii.delivery.service;

import com.bitemii.delivery.dao.MysqlConnect;
import com.bitemii.delivery.dao.RestaurantDao;
import com.bitemii.delivery.model.DeliveryStaff;
import com.bitemii.delivery.model.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryService {

    @Autowired
    private MysqlConnect daoObject;
    @Autowired
    private RestaurantDao restaurantDao;
    
    
    public List<OrderDetail> getNewOrders() {
        return daoObject.getNewOrder();
    }

    public List<DeliveryStaff> getAllDeliveryStaff() {
        return daoObject.getAllDeliveryStaff();
    }
    
    public List<DeliveryStaff> getDeliveryStaff() {
        return daoObject.getDeliveryStaff();
    }

    public List<DeliveryStaff> assignedDeliveryStaff() {
        return daoObject.getAssignedDeliveryStaff();
    }
    
    public void assignOrderToDeliveryStaff() {
         daoObject.assignOrderToDeliveryStaff();
    }

    public void assignOrderToDeliveryStaff(final String orderId, final int userId, final int assignedBy) {
        daoObject.assignOrderToDeliveryStaff(orderId, userId, assignedBy);
    }

	public Object getAllNewOrder() {
		return restaurantDao.getAllNewOrder();
	}
}
