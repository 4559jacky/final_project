let selectedParentNo = null;

$(document).ready(function () {
  console.log("✅ jstree.js 로딩됨");

  // 1. 공유 트리
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
  
  // 사용자가 처음 메인화면 진입했을 때, 최상위 트리 일단 확인.
  $('#shared-tree').on('ready.jstree', function (e, data) {
      loadFolderList(null);
  });

  $('#shared-tree').on("changed.jstree", function (e, data) {
    const folderId = data.selected[0];
	if (folderId) {
	       loadFolderList(folderId);
	   } else {
	       loadFolderList(null); // 선택 안했으면 최상위
	   }
  });

  // 2. 폴더 모달창
  $('#folderModal').on('shown.bs.modal', function (){
    $('#new-folder-name').val('');    // 폴더 이름 초기화
    $('#folder-type').val('1');        // 폴더 타입 초기화
    selectedParentNo = null;           // 선택 초기화
	
	$('#folder-type-group').show(); // ✅ 기본: 셀렉창 보이기
	 
    // 트리 초기화
    $('#modal-folder-tree').jstree("destroy").empty();
    $('#modal-folder-tree').jstree({
      core: {
        multiple: false,  // 핵심 추가: 하나만 선택
        data: {
          url: '/shared/main/tree',
          dataType: 'json',
        },
        themes: {
          dots: true,
          icons: true
        }
      }
    });
  });

  // 3. 폴더 클릭 (선택/해제)
  $('#modal-folder-tree')
    .on('select_node.jstree', function (e, data) {
	  $('#folder-type-group').hide();
      const nodeId = data.node.id;
      const nodeElement = $('#' + nodeId + '_anchor');

      $('.jstree-anchor').removeClass('selected-button'); // 이전 선택 삭제
      nodeElement.addClass('selected-button');             // 새 선택 표시
      selectedParentNo = nodeId;
      console.log("폴더 선택됨 (selectedParentNo):", selectedParentNo);
    });

  // 4. 빈 공간 클릭하면 전체 선택 해제
  $('#modal-folder-tree').on('click', function (e) {
    const target = $(e.target);

    if (!target.closest('.jstree-anchor').length && !target.closest('.jstree-icon').length) {
      console.log("빈 공간 클릭됨 -> 전체 선택 해제");

      $('#modal-folder-tree').jstree('deselect_all');
      $('.jstree-anchor').removeClass('selected-button');
      selectedParentNo = null;
    }
  });
  
  //  폴더 선택 시 셀렉창 숨기기
  $('#modal-folder-tree').on('select_node.jstree', function (e, data) {
    $('#folder-type-group').hide();
  });

  //  선택 해제 시 셀렉창 다시 보이기
  $('#modal-folder-tree').on('deselect_all.jstree', function () {
    $('#folder-type-group').show();
  });
});

// 5. 새 폴더 생성
function createNewFolder() {
  const folderName = document.getElementById("new-folder-name").value;
  const memberNo = document.getElementById("member-no-hidden").value;
  const folderType = document.getElementById("folder-type").value;

  if (!folderName) {
    alert("폴더 이름을 입력해주세요.");
    return;
  }

  const payload = {
    folder_name: folderName,
    folder_parent_no: selectedParentNo, // 선택된 폴더가 부모 (없으면 최상위)
    member_no: memberNo,
    folder_type: folderType
  };

  fetch("/shared/folder/create", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      [document.querySelector('meta[name="_csrf_header"]').content]:
        document.querySelector('meta[name="_csrf"]').content
    },
    body: JSON.stringify(payload)
  })
    .then(res => res.json())
    .then(data => {
      alert(data.message || "폴더 생성 완료!");
      $('#folderModal').modal('hide');
      $('#shared-tree').jstree(true).refresh();
    })
    .catch(err => {
      console.error("폴더 생성 실패", err);
      alert("폴더 생성 중 오류 발생");
    });
}
// 파일 업로드.
function uploadFiles(){
	const files = document.getElementById("fileUpload").files;
	const folderId = $('#shared-tree').jstree('get_selected')[0]// 선택된 폴더 ID
	
	if(!folderId){
		alert("업로드한 폴더를 선택해주세요.");
		return;
	}
	
	const formData = new FormData();
	for(const file of files){
		formData.append("files", file); // 여러 개 가능.
	}
	formData.append("folderId",folderId);
	
	// CSRF 토큰 가져오기
	const csrfToken = document.querySelector('meta[name="_csrf"]').content;
	const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
	
	
	fetch("/shared/files/upload",{
		method: "POST",
		headers: {
		           [csrfHeader]: csrfToken
		       },
		body: formData
	})
	  .then(res => res.json())
	  .then(data => {
		alert(data.message || "파일 업로드 완료!")
		
		const currentFolderId = $('#shared-tree').jstree('get_selected')[0];
		loadFolderList(currentFolderId); // 리스트 갱신
	  })
	  .catch(err => {
	        console.error("업로드 실패", err);
	        alert("파일 업로드 중 오류 발생");
	      });
}

function loadFolderList(folderId){
	let url = "/shared/files/list";
	if(folderId){
		url +=  "?folderId="+folderId;
	}
	
	fetch(url)
		.then(response => response.json())
		.then(data => {
			renderFolderTable(data); // 테이블 그리기 함수 호출
		})
		.catch(error => {
			console.error("목록 조회 실패 :", error);
			alert("폴더/파일 목록 조회 중 오류가 발생하였습니다.")
		})
}

// 폴더 리스트 출력.
function renderFolderTable(data){
	const tbody = document.querySelector("#folder-table tbody");
	tbody.innerHTML = ""; // 기존 테이블 비우기
	
	if(data.length === 0){
		tbody.innerHTML = "<tr><td colspan='4'>폴더/파일이 없습니다.</td></tr>";
		return;	
	}
	
	data.forEach(item => {
		const tr = document.createElement("tr");
		tr.style.cursor = "pointer";
		
		let icon = item.type === "folder" ? "📁" : "📄";
		let typeLabel = item.type === "folder" ? "폴더" : "파일";
		let size = item.size ? formatFileSize(item.size) : "-";
		let regDate = formatDate(item.regDate);

		
		tr.innerHTML = `
		      <td>${icon} ${item.name}</td>
		      <td>${typeLabel}</td>
		      <td>${size}</td>
		      <td>${regDate}</td>
		    `;
		
		// 클릭 이벤트 분기
		tr.addEventListener("click", function(){
			if(item.type === "folder"){
				onFolderClick(item.id); // 폴더 클릭 시 폴더 이동
			}else{
				onFileClick(item.id); // 파일 클릭 시 다운로드
			}
		});
		
		tbody.appendChild(tr);
	});
}

// 파일 크기 단위 표시
function formatFileSize(bytes){
	if(bytes < 1024) return bytes + "B";
	else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + "KB";
	else return (bytes / 1048576).toFixed(1) + "MB";
}

// 날짜 포멧팅
function formatDate(dateString){
	if(!dateString) return "";
	const date = new Date(dateString);
	return date.toLocaleDateString();
}

// 폴더 클릭시 (탐색)
function onFolderClick(folderId){
	$('#shared-tree').jstree('deselect_all');
	$('#shared-tree').jstree('select_node', folderId); // 트리에서 이동
	loadFolderList(folderId); // 리스트로 이동
}

// 파일 클릭시 (다운로드)
function onFileClick(fileId){
	window.location.href = "/shared/files/download/" + fileId;
}