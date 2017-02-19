<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="/js/jquery-1.6.4.js"></script>
<script type="text/javascript">
	function f123123123123(data){
		success(data)
	}
	
	function click1(){
		$.ajax({
			url : "http://localhost:8088/sso/test/1?callback=?",
			type : "GET",
			dataType:'jsonp',
			success : function(data) {
				alert(data.username)
			}
		});
	}
	
	function test(data){
		alert(data.username);
	}
</script>
<script type="text/javascript" src="http://localhost:8088/sso/test/1?callback=test"></script>
</head>
<body>
	<input type="button" onclick="click1();" value="测试js跨域">
</body>
</html>