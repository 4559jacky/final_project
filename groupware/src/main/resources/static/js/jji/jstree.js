// jsTree
$(document).ready(function() {
	// 부서 트리 데이터를 서버에서 가져오기
	$.ajax({
		url: '/dept/tree',
		method: 'GET',
		success: function(data) {
			// 받은 데이터를 jsTree에 적용
			$('#appr_jstree').on('loaded.jstree', function () {
				// 모든 노드를 펼침
				$('#appr_jstree').jstree('open_all');
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
  	$('#appr_jstree').on("changed.jstree", function (e, data) {
    	// console.log(data.selected);
  	});

  	// 버튼 클릭 시 선택된 노드 출력
  	$('#jstree-btn').on('click', function () {
    	var selectedNodes = $('#appr_jstree').jstree('get_selected');
    	alert('선택된 노드: ' + selectedNodes);
  	});
	
	// 펼치기 버튼 (+)
	$('#expand_all').on('click', function () {
		$('#appr_jstree').jstree('open_all');
	});

	// 접기 버튼 (-)
	$('#collapse_all').on('click', function () {
		$('#appr_jstree').jstree('close_all');
	});
	
	$('.jstree-icon').on('click', function() {
		console.log('test');
	});
	
	// 부서 클릭 시 동작
	$('#appr_jstree').on("changed.jstree", function (e, data) {
	    if (data && data.selected && data.selected.length > 0) {
	        const selectedNodeId = data.selected[0]; // 예: '2'
	        console.log("선택된 노드 ID:", selectedNodeId);

	        // 현재 등록된 결재자, 합의자, 참조자 번호들 가져오기
	        const currentApproverNos = Array.from(document.querySelectorAll('.selected_approver')).map(btn => btn.value);
	        const currentAgreementerNos = Array.from(document.querySelectorAll('.selected_agreementer')).map(btn => btn.value);
	        const currentReferencerNos = Array.from(document.querySelectorAll('.selected_referencer')).map(btn => btn.value);
	        
	        fetch('/member/dept/' + selectedNodeId)
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
	                    
	                    // 이미 등록된 사람이면 disabled 처리
	                    let isDisabled = currentApproverNos.includes(String(member.member_no)) ||
	                                     currentAgreementerNos.includes(String(member.member_no)) ||
	                                     currentReferencerNos.includes(String(member.member_no));

	                    li.innerHTML = `<label><input type="checkbox" value="${member.member_no}" class="member-checkbox" ${isDisabled ? 'disabled' : ''}> ${member.member_name} ${member.pos_name}&nbsp</label>`;
	                    
	                    list.appendChild(li);
	                });
	            })
	            .catch(err => {
	                console.error("사원 목록 불러오기 실패:", err);
	            });
	    }
	});
	
});
