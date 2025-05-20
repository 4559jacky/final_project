// jsTree
$(document).ready(function() {
	// 부서 트리 데이터를 서버에서 가져오기
	$.ajax({
		url: '/dept/tree',
		method: 'GET',
		success: function(data) {
			// 받은 데이터를 jsTree에 적용
			$('#ysw_create_jstree').on('loaded.jstree', function () {
				// 모든 노드를 펼침
				$('#ysw_create_jstree').jstree('open_all');
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
  	$('#ysw_create_jstree').on("changed.jstree", function (e, data) {
    	// console.log(data.selected);
  	});

  	// 버튼 클릭 시 선택된 노드 출력
  	$('#jstree-btn').on('click', function () {
    	var selectedNodes = $('#ysw_create_jstree').jstree('get_selected');
    	alert('선택된 노드: ' + selectedNodes);
  	});
	
	// 펼치기 버튼 (+)
	$('#expand_all').on('click', function () {
		$('#ysw_create_jstree').jstree('open_all');
	});

	// 접기 버튼 (-)
	$('#collapse_all').on('click', function () {
		$('#ysw_create_jstree').jstree('close_all');
	});
	
	$('.jstree-icon').on('click', function() {
		console.log('test');
	});
	
	
	$('#ysw_create_jstree').on("changed.jstree", function (e, data) {
	    if (data && data.selected && data.selected.length > 0) {
	        const selectedNodeId = data.selected[0]; // 예: '2'
	        console.log("선택된 노드 ID:", selectedNodeId);

	        // 부서 ID로 사원 목록 요청
	        fetch('/member/dept/'+selectedNodeId)
	            .then(response => response.json())
	            .then(data => {
	                const list = document.getElementById("memberList");
	                list.innerHTML = ""; // 기존 목록 초기화

	                if (data.length === 0) {
	                    list.innerHTML = "<li class='list-group-item'>사원이 없습니다.</li>";
	                    return;
	                }

	                data.forEach(member => {
	                    const li = document.createElement("li");
	                    li.className = "list-group-item";
						li.innerHTML = `
						    <input 
						        type="button" 
						        value="+" 
						        data-name="${member.member_name}" 
						        data-id="${member.member_no}" 
								data-pos="${member.pos_name}" 
						        class="selectBtn btn-sm btn btn-rounded btn-light">
						    ${member.member_name} (${member.pos_name})
						`;

	                    list.appendChild(li);
	                });
	            })
	            .catch(err => {
	                console.error("사원 목록 불러오기 실패:", err);
	            });
	    }
	});
	
});



// jsTree
$(document).ready(function() {
	// 부서 트리 데이터를 서버에서 가져오기
	$.ajax({
		url: '/dept/tree',
		method: 'GET',
		success: function(data) {
			// 받은 데이터를 jsTree에 적용
			$('#ysw_chat_jstree').on('loaded.jstree', function () {
				// 모든 노드를 펼침
				$('#ysw_chat_jstree').jstree('open_all');
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
  	$('#ysw_chat_jstree').on("changed.jstree", function (e, data) {
    	// console.log(data.selected);
  	});

  	// 버튼 클릭 시 선택된 노드 출력
  	$('#jstree-btn').on('click', function () {
    	var selectedNodes = $('#ysw_chat_jstree').jstree('get_selected');
    	alert('선택된 노드: ' + selectedNodes);
  	});
	
	// 펼치기 버튼 (+)
	$('#expand_all').on('click', function () {
		$('#ysw_chat_jstree').jstree('open_all');
	});

	// 접기 버튼 (-)
	$('#collapse_all').on('click', function () {
		$('#ysw_chat_jstree').jstree('close_all');
	});
	
	$('.jstree-icon').on('click', function() {
		console.log('test');
	});
	
	
	$('#ysw_chat_jstree').on("changed.jstree", function (e, data) {
	    if (data && data.selected && data.selected.length > 0) {
	        const selectedNodeId = data.selected[0]; // 예: '2'
	        console.log("선택된 노드 ID:", selectedNodeId);

	        // 부서 ID로 사원 목록 요청
	        fetch('/member/dept/'+selectedNodeId)
	            .then(response => response.json())
	            .then(data => {
	                const list = document.getElementById("memberList");
	                list.innerHTML = ""; // 기존 목록 초기화

	                if (data.length === 0) {
	                    list.innerHTML = "<li class='list-group-item'>사원이 없습니다.</li>";
	                    return;
	                }

					const loginMemberNo = document.getElementById("createMemberNo").value;

					data.forEach(member => {
					    if (String(member.member_no) === loginMemberNo) return; // 🔥 본인 빼!

					    const li = document.createElement("li");
					    li.className = "list-group-item";
					    li.innerHTML = `
					        <input 
					            type="button" 
					            value="+" 
					            data-name="${member.member_name}" 
					            data-id="${member.member_no}" 
					            data-pos="${member.pos_name}" 
					            class="selectBtn btn-sm btn btn-rounded btn-light">
					        ${member.member_name} (${member.pos_name})
					    `;
					    list.appendChild(li);
					});

	            })
	            .catch(err => {
	                console.error("사원 목록 불러오기 실패:", err);
	            });
	    }
	});
	
});




$(document).ready(function() {
	// 부서 트리 데이터를 서버에서 가져오기
	$.ajax({
		url: '/dept/tree',
		method: 'GET',
		success: function(data) {
			// 받은 데이터를 jsTree에 적용
			$('#ysw_add_jstree').on('loaded.jstree', function () {
				// 모든 노드를 펼침
				$('#ysw_add_jstree').jstree('open_all');
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
  	$('#ysw_add_jstree').on("changed.jstree", function (e, data) {
    	// console.log(data.selected);
  	});

  	// 버튼 클릭 시 선택된 노드 출력
  	$('#jstree-btn').on('click', function () {
    	var selectedNodes = $('#ysw_add_jstree').jstree('get_selected');
    	alert('선택된 노드: ' + selectedNodes);
  	});
	
	// 펼치기 버튼 (+)
	$('#expand_all').on('click', function () {
		$('#ysw_add_jstree').jstree('open_all');
	});

	// 접기 버튼 (-)
	$('#collapse_all').on('click', function () {
		$('#ysw_add_jstree').jstree('close_all');
	});
	
	$('.jstree-icon').on('click', function() {
		console.log('test');
	});
	
	
	$('#ysw_add_jstree').on("changed.jstree", function (e, data) {
	    if (data && data.selected && data.selected.length > 0) {
	        const selectedNodeId = data.selected[0]; // 예: '2'
	        console.log("선택된 노드 ID:", selectedNodeId);

	        // 부서 ID로 사원 목록 요청
	        fetch('/member/dept/'+selectedNodeId)
	            .then(response => response.json())
	            .then(data => {
	                const list = document.getElementById("memberLists");
	                list.innerHTML = ""; // 기존 목록 초기화

	                if (data.length === 0) {
	                    list.innerHTML = "<li class='list-group-item'>사원이 없습니다.</li>";
	                    return;
	                }

	                data.forEach(member => {
	                    const li = document.createElement("li");
	                    li.className = "list-group-item";
						li.innerHTML = `
						    <input 
						        type="button" 
						        value="+" 
						        data-name="${member.member_name}" 
						        data-id="${member.member_no}" 
								data-pos="${member.pos_name}" 
						        class="addBtn btn-sm btn btn-rounded btn-light">
						    ${member.member_name} (${member.pos_name})
						`;

	                    list.appendChild(li);
	                });
	            })
	            .catch(err => {
	                console.error("사원 목록 불러오기 실패:", err);
	            });
	    }
	});
	
});