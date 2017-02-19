<html>
<head>
<title>
	${world}
</title>
</head>
<body>
	<label>学号：</label>${student.id}
	<br>
	<label>姓名：</label>${student.name}
	<br>
	今天的日期：${today?date} &nbsp;&nbsp;&nbsp;&nbsp;现在的时间：${today?time}
	<br>
	今天的日期和时间：${today?datetime}
	<br>
	自定日期格式：${today?string('yyyy/MM/dd HH:mm:ss')}
	<br>
	${hello!'null'}
	
	<br>
	${mouse!"No mouse."}
	<#assign mouse="Jerry">
	${mouse!"No mouse."}
	
	<br>
	学生列表
	<br>
	<table border=1>
	<tr><td>序号</td><td>学号</td><td>姓名</td></tr>
	<#list stulist as stu>
		<#if stu_index % 2 == 0>
			<tr bgcolor="red">
		<#else>
			<tr bgcolor="blue">
		</#if>
		
		<td>${stu_index}</td><td>${stu.id}</td><td>${stu.name}</td></tr>
	</#list>
	</table>
	
	<#include "hello2.ftl">
</body>
</html>
