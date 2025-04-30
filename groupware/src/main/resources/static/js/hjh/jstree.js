// 기존 selectedParentNo 초기화 유지
if (typeof selectedParentNo === 'undefined') {
  var selectedParentNo = null;
}

$(document).ready(function () {
  console.log("jstree.js 로딩됨");

  // 1. 공유 트리 초기화
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

  $('#shared-tree').on('ready.jstree', function () {
    loadFolderList(null); // 최상위 진입 시 리스트 로딩
  });

  $('#shared-tree').on("changed.jstree", function (e, data) {
    const folderId = data.selected[0];
    loadFolderList(folderId ?? null);
  });

  // 2. 폴더 생성 모달 열릴 때 트리 초기화
  $('#folderModal').on('shown.bs.modal', function (){
    $('#new-folder-name').val('');
    $('#folder-type').val('1');
    selectedParentNo = null;
    $('#folder-type-group').show();

    // 트리 다시 그리기
    $('#modal-folder-tree').jstree("destroy").empty();
    $('#modal-folder-tree').jstree({
      core: {
        multiple: false,
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

    // 폴더 선택 시 상위 folder_type 자동 상속
    $('#modal-folder-tree')
      .off('select_node.jstree')
      .on('select_node.jstree', function (e, data) {
        const nodeId = data.node.id;
        const nodeElement = $('#' + nodeId + '_anchor');

        $('.jstree-anchor').removeClass('selected-button');
        nodeElement.addClass('selected-button');

        selectedParentNo = nodeId;
        console.log("폴더 선택됨 (selectedParentNo):", selectedParentNo);

        // AJAX로 상위 폴더 타입 가져오기
        fetch("/shared/folder/type?folderId=" + nodeId)
          .then(res => res.json())
          .then(data => {
            const folderType = data.folderType;
            document.querySelector(`input[name="folder_type"][value="${folderType}"]`).checked = true;
            $('#folder-type-group').hide();
          })
          .catch(err => {
            console.error("상위 folder_type 가져오기 실패", err);
            $('#folder-type-group').show();
          });
      });

    // 선택 해제 시 라디오 다시 보여줌
    $('#modal-folder-tree')
      .off('deselect_all.jstree')
      .on('deselect_all.jstree', function () {
        console.log("✅ 트리 선택 해제됨 → 라디오 show");
        $('#folder-type-group').show();
        selectedParentNo = null;
      });
  });

  // 빈 공간 클릭 시 전체 선택 해제
  $('#modal-folder-tree').on('click', function (e) {
    const target = $(e.target);
    if (!target.closest('.jstree-anchor').length && !target.closest('.jstree-icon').length) {
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
  const folderType = document.querySelector('input[name="folder_type"]:checked').value;

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
		
		document.querySelector('input[type="file"]').value = ''; // 파일 초기화.
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
			renderFolderTable(data.items, data.parentFolderNo, folderId ?? null); // 테이블 그리기 함수 호출
		})
		.catch(error => {
			console.error("목록 조회 실패 :", error);
			alert("폴더/파일 목록 조회 중 오류가 발생하였습니다.")
		})
}

// 폴더 리스트 출력.
function renderFolderTable(data, parentFolderNo, currentFolderId){
	const tbody = document.querySelector("#folder-table tbody");
	tbody.innerHTML = ""; // 기존 테이블 비우기
	
	if (currentFolderId !== null && currentFolderId !== undefined) {
	  const backRow = document.createElement('tr');
	  backRow.innerHTML = `
	    <td colspan="4" class="text-start folder-name-wrapper" style="cursor:pointer;">
	      <span style="display:flex; align-items:center;">
	        <span>📁..</span>
	      </span>
	    </td>
	  `;
	  backRow.addEventListener('click', () => {
		onFolderClick(parentFolderNo);  // ✅ 부모 폴더로 이동
	  });
	  tbody.appendChild(backRow);
	}
	
	if (data.length === 0) {
	   const emptyRow = document.createElement('tr');
	   emptyRow.innerHTML = `<td colspan='4'>폴더/파일이 없습니다.</td>`;
	   tbody.appendChild(emptyRow);
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