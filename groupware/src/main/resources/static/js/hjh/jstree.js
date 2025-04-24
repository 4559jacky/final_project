let selectedParentNo = null;
$(document).ready(function () {
  console.log("✅ jstree.js 로딩됨");
  $('#shared-tree').jstree({
    core: {
      data: {
        url: '/shared/main/tree',
        dataType: 'json',
        error: function(xhr, status, error) {
          console.error('jsTree 데이터 로드 실패:', status, error);
        }
      },
      themes: {
        dots: true,
        icons: true
      }
    }
  });

  $('#shared-tree').on("changed.jstree", function (e, data) {
    const folderId = data.selected[0];
    if (folderId) {
      window.location.href = '/shared?folderNo=' + folderId; // 나중에 에디터 영역 만들때 쓰자.
    }
  });

  $('#expand_all').click(() => $('#shared-tree').jstree('open_all'));
  $('#collapse_all').click(() => $('#shared-tree').jstree('close_all'));


// 폴더 모달창 js 
$('#folderModal').on('shown.bs.modal', function (){
	// 트리 초기화
	$('#modal-folder-tree').jstree("destroy").empty(); // 기존 것 초기화.
	$('#modal-folder-tree').jstree({
		core: {
			data: {
				url: '/shared/main/tree',
				dataType: 'json',			
			},
			themes: {
				dots: true,
				icons: true
			}
		}
	})
});

// 폴더 선택 이벤트
$('#modal-folder-tree').on("changed.jstree", function (e, data){
	selectedParentNo = data.selected[0]; // 폴더 선택 시 ID 저장
	console.log("선택된 부모 폴더 번호:", selectedParentNo);
})

});

// 새 폴더(최상위 폴더 밑에 하위 폴더 생성)
function createNewFolder(){
	const folderName = document.getElementById("new-folder-name").value;
	const memberNo = document.getElementById("member-no-hidden").value;
	if(!folderName){
		alert("폴더 이름을 입력해주세요.");
		return;
	}
	
	if(!selectedParentNo){
		alert("생성하실 폴더의 위치를 선택해주세요.")
		return;
	}
	
	const payload = {
		folder_name : folderName,
		folder_parent_no : selectedParentNo,
		member_no : memberNo 
	};
	
	fetch("/shared/folder/create", {
		method : "POST",
		headers : {
			"Content-Type" : "application/json",
			   [document.querySelector('meta[name="_csrf_header"]').content]:
			   document.querySelector('meta[name="_csrf"]').content
		},
		body: JSON.stringify(payload)
	})	
		.then(res => res.json())
		.then(data => {
			alert(data.message || "폴더 생성 완료!")
			$('#folderModal').modal('hide');
			
			// 트리 전체 새로고침 (모든 데이터 다시 불러옴.)
			$('#shared-tree').jstree(true).refresh();
			
		})
		.catch(err => {
			console.error("폴더 생성 실패", err);
			alert("폴더 생성 중 오류 발생");
		})
}

// 최상위 폴더 생성.
function createTopFolder(){
	const folderName = document.getElementById("top-folder-name").value;
	const memberNo = document.getElementById("admin-member-no").value;
	
	if(!folderName){
		alert("폴더 이름을 입력해주세요.");
		return;
	}
	
	const payload = {
		folder_name : folderName,	
		folder_parent_no : null,
		member_no : memberNo
	};
	
	fetch("/shared/folder/create",{
		method: "POST",
		headers:{
			"Content-Type" : "application/json",
			[document.querySelector('meta[name="_csrf_header"]').content]:
			 document.querySelector('meta[name="_csrf"]').content
		},
		body: JSON.stringify(payload)
	})
	.then(res => res.json)
	.then(data => {
		alert(data.message || "최상위 폴더 생성 완료!");
		$('#topFolderModal').modal('hide');
		$('#shared-tree').jstree(true).refresh();
	})
	.catch(err => {
	    console.error("최상위 폴더 생성 실패", err);
	    alert("오류 발생");
	  });
}

// tree 클릭 시 오른쪽 테이블 동적으로 생성
function createFolderContents(folderNo){
	fetch(`/shared/folder/${folderNo}/contents`)
	.then(res => res.json())
	.then(data => {
		const tbody = document.querySelector('tbody');
		tbody.innerHTML = ""; // 기존 목록 비움.
		
		data.forEach(item => {
			const icon = item.type === 'folder' ? '📁' : '📄';
			const shared = item.shared ? '공유' : '공유안함';
			const size = item.size || '-';
			tbody.innerHTML += `
			<tr data-folder-id="${item.id}">
				<td>${icon} ${item.name}</td>
			    <td>${shared}</td>
			    <td>${size}</td>
			    <td>${item.regDate}</td>
			</tr>
			`;	
		});
	});
}
