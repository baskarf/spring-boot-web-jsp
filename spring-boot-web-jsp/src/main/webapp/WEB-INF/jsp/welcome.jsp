<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>

<link rel="stylesheet" type="text/css"
	href="webjars/bootstrap/3.3.7/css/bootstrap.min.css" />

<!-- 
	<spring:url value="/css/main.css" var="springCss" />
	<link href="${springCss}" rel="stylesheet" />
	 -->
<c:url value="/css/main.css" var="jstlCss" />
<link href="${jstlCss}" rel="stylesheet" />
<style>
table, th, td {
  border: 1px solid black;
  border-collapse: collapse;
}
th, td {
  padding: 15px;
  text-align: left;
}
table#t01 {
  width: 100%;    
  background-color: #f1f1c1;
}
</style>
</head>
<body>

	<nav class="navbar navbar-inverse">
		<div class="container">
			<div class="navbar-header">
				<a class="navbar-brand" href="/getNewOrders">New Orders</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li class="active"><a href="#">Delivery Staff</a></li>
					<li><a href="#about">Orders</a></li>
				</ul>
			</div>
		</div>
	</nav>

	<div class="container">

		<div class="starter-template">
			<h2>Message: ${message}</h2>
		</div>

	</div>
	<!-- /.container -->

        <table  id="t01">
         <thead>
            <tr>
             <td>Order ID </td>
               <td>Restaurent Name</td>
               <td>Order Status </td>
               <td>Delivery Staff</td>
               <td>Order Amount</td>
               
         </thead>
         <c:if test="${not empty data}">
         
    <c:forEach items="${data}" var="orderDetail">
       

         <tbody>
            <tr>
               <td>${orderDetail.orderId}</td>
               <td>${orderDetail.orderRestaurentId}</td>
               <td>${orderDetail.orderProcessStatus}</td>
               <td>${orderDetail.deliveryStaffEmail}</td>
               <td>${orderDetail.amount}</td>
               
            </tr>
         </tbody>
         
      
</c:forEach>
</c:if>
</table>

	<script type="text/javascript"
		src="webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</body>

</html>
