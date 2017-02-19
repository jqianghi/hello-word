<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="${pageContext.request.contextPath }/layout/inc.jsp"></jsp:include>
<script type="text/javascript">
	function importAll() {
		$.post("/index/importAll", '', function(data) {
			if (data.status == 200) {
				$.messager.alert('提示', '导入成功!');
			}else{
				$.messager.alert('提示', data.msg);
			}
		});
	}
</script>
<div style="padding: 5px">
	<a href="javascript:void(0)" class="easyui-linkbutton"
		onclick="importAll();">商品全量导入索引库</a>
</div>