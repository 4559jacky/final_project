// jsTree
$(document).ready(function() {
	// 부서 트리 데이터를 서버에서 가져오기
	$.ajax({
		url: '/dept/tree',
		method: 'GET',
		success: function(data) {
			// 받은 데이터를 jsTree에 적용
			$('#attendance_jstree').on('loaded.jstree', function () {
				// 모든 노드를 펼침
				$('#attendance_jstree').jstree('open_all');
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
  	$('#attendance_jstree').on("changed.jstree", function (e, data) {
    	// console.log(data.selected);
  	});

  	// 버튼 클릭 시 선택된 노드 출력
  	$('#jstree-btn').on('click', function () {
    	var selectedNodes = $('#attendance_jstree').jstree('get_selected');
    	alert('선택된 노드: ' + selectedNodes);
  	});
	
	// 펼치기 버튼 (+)
	$('#expand_all').on('click', function () {
		$('#attendance_jstree').jstree('open_all');
	});

	// 접기 버튼 (-)
	$('#collapse_all').on('click', function () {
		$('#attendance_jstree').jstree('close_all');
	});
	
	$('.jstree-icon').on('click', function() {
		console.log('test');
	});
	
	// 부서 클릭 시 동작
	$('#attendance_jstree').on("changed.jstree", function (e, data) {
	    if (data && data.selected && data.selected.length > 0) {
	        const selectedDeptId = data.selected[0];
	        console.log("선택된 부서 ID:", selectedDeptId);

	        $("#update_member_div").removeClass("d-none");
	        $("#none_member_div").addClass("d-none");

			fetch('/member/dept/' + selectedDeptId)
			.then(response => response.json())
			.then(data => {
			    const tbody = document.getElementById("member-tbody");
			    tbody.innerHTML = "";

			    if (data.length === 0) {
			        const tr = document.createElement("tr");
			        tr.innerHTML = `<td colspan="7" class="text-center text-muted py-4">사원 정보가 없습니다.</td>`;
			        tbody.appendChild(tr);
			        return;
			    }

			    const today = new Date();
				const annualPolicyList = window.annualPolicyList || [];
				
			    data.forEach(member => {
			        let regDate = new Date(member.reg_date);
			        let yearDiff = today.getFullYear() - regDate.getFullYear();
			        const notYet = today.getMonth() < regDate.getMonth() ||
			            (today.getMonth() === regDate.getMonth() && today.getDate() < regDate.getDate());
			        if (notYet) yearDiff--;
			        const careerYear = Math.max(1, yearDiff + 1);
					const matchedPolicy = annualPolicyList.find(p => p.year === careerYear);
					const leaveDays = matchedPolicy ? matchedPolicy.leaveDays : 'N/A'; // 못 찾으면 N/A

			        let formattedDate = `${regDate.getFullYear()}.${String(regDate.getMonth() + 1).padStart(2, '0')}.${String(regDate.getDate()).padStart(2, '0')}`;

			        const tr = document.createElement("tr");
					tr.setAttribute("data-member-no", member.member_no);
			        tr.innerHTML = `
			            <td>
			                <div class="d-flex align-items-center">
			                    <div class="ms-3">
			                        <h6 class="fs-4 fw-semibold mb-0 member-name">${member.member_name}</h6>
			                        <span class="fw-normal">#${member.member_no}</span>
			                    </div>
			                </div>
			            </td>
			            <td class="text-center">
			                <h6 class="fs-4 fw-semibold mb-0">${formattedDate}</h6>
			            </td>
			            <td class="text-center">
			                <h6 class="fs-4 fw-semibold mb-0">${careerYear}년차</h6>
			            </td>
						<td class="text-center">
						  <h6 class="fs-4 fw-semibold mb-0">${leaveDays}</h6>
						</td>
			            <td class="text-center">
			                <h6 class="fs-4 fw-semibold mb-0">${member.annual_leave}</h6>
			            </td>
			        `;
			        tbody.appendChild(tr);
			    });
			})
			.catch(err => {
			    console.error("사원 정보 불러오기 실패:", err);
			});
	    }
	});
	
});
