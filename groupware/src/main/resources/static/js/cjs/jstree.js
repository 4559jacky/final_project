// jsTree
$(document).ready(function() {
	// 부서 트리 데이터를 서버에서 가져오기
	$.ajax({
		url: '/admin/dept/tree',
		method: 'GET',
		success: function(data) {
			// 받은 데이터를 jsTree에 적용
			$('#jstree').on('loaded.jstree', function () {
				// 모든 노드를 펼침
				$('#jstree').jstree('open_all');
			})
			.jstree({
				'core': {
					'data': data,
					'themes': { 
						'icons': false }
				}
			});
		},
    	error: function(error) {
      		console.log("Error fetching data:", error);
    	}
  	});

  	// jsTree에서 선택된 노드 정보 출력
  	$('#jstree').on("changed.jstree", function (e, data) {
    	// console.log(data.selected);
  	});

  	// 버튼 클릭 시 선택된 노드 출력
  	$('#jstree-btn').on('click', function () {
    	var selectedNodes = $('#jstree').jstree('get_selected');
    	alert('선택된 노드: ' + selectedNodes);
  	});
});