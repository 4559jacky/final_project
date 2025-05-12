//  공유문서함 JS 전체 리팩토링

window.currentType = 'personal';
window.initialFolderId = null;

let selectedParentNo = null;

const folderTypeMap = {
  personal: 1,
  department: 2,
  public: 3
};

document.addEventListener("DOMContentLoaded", function () {
  console.log("공유문서함 스크립트 로딩됨");

  $('#folderModal').on('shown.bs.modal', function () {
    initModalTree();
	document.getElementById("new-folder-name").value = ""; 
	 selectedParentNo = null; //  선택된 부모 초기화
	 lastSelectedId = null; // 중복 클릭 방지 초기화.
  });
  
  document.querySelectorAll('#shared-type-tab button[data-type]').forEach(btn => {
    btn.addEventListener('click', function () {
      const selectedType = this.dataset.type;
      currentType = selectedType;
      window.currentType = selectedType;
      console.log("현재 선택된 문서함 타입:", selectedType);

	  loadTree(selectedType, () => {
	    loadFolderList(null); // ✅ 트리 로딩 완료 후에 호출
	  });
	  loadTrashBin();
	  loadUsageChart();
    });
  });

  function loadTree(type,callback) {
  
	$('#shared-tree')
	   .off('ready.jstree') // 이전 이벤트 제거
	   .on('ready.jstree', function () {
	     console.log("✅ 트리 로드 완료됨");
	     if (typeof callback === 'function') callback();
	   });
	
	
	  $('#shared-tree').jstree(true).settings.core.data.url = function () {
      return `/shared/main/tree?type=${type}`;
    };
    $('#shared-tree').jstree(true).settings.core.data.dataFilter = function (data) {
      const parsed = JSON.parse(data);
      const targetType = folderTypeMap[currentType] ?? 1;
      const filtered = parsed.filter(item => item.folder_type === targetType);
      window.rootNodeList = filtered
        .filter(item => !item.id.toString().startsWith("file-") && item.parent === "#")
        .map(item => item.id);
      return JSON.stringify(filtered);
    };
    $('#shared-tree').jstree(true).refresh();
  }

  $('#shared-tree').jstree({
    core: {
      check_callback: function (operation, node, parent, position, more) {
        if (operation === "move_node") {
          if (node.id.startsWith("file-") && parent.id === "#") return false;
          if (parent.id.startsWith("file-")) return false;
        }
        return true;
      },
      data: {
        url: function () {
          return `/shared/main/tree?type=${currentType}`;
        },
        dataType: 'json',
        dataFilter: function (data) {
          const parsed = JSON.parse(data);
          const targetType = folderTypeMap[currentType] ?? 1;
          const filtered = parsed.filter(item => item.folder_type === targetType);
          window.rootNodeList = filtered
            .filter(item => !item.id.toString().startsWith("file-") && item.parent === "#")
            .map(item => item.id);
          return JSON.stringify(filtered);
        },
        error: function (xhr, status, error) {
          console.error('jsTree 데이터 로드 실패:', status, error);
        }
      },
      themes: { dots: true, icons: true }
    },
    plugins: ['dnd', 'contextmenu'],
    contextmenu: {
      items: function ($node) {
        return {
          deleteItem: {
            label: "삭제",
            action: function () {
              const isFile = $node.id.startsWith("file-");
              const numericId = $node.id.replace("file-", "");
              if (confirm(`${isFile ? "파일" : "폴더"}을 삭제하시겠습니까?`)) {
                fetch(`/shared/${isFile ? "file" : "folder"}/delete`, {
                  method: 'POST',
                  headers: {
                    'Content-Type': 'application/json',
                    [document.querySelector('meta[name="_csrf_header"]').content]:
                      document.querySelector('meta[name="_csrf"]').content
                  },
                  body: JSON.stringify({ id: numericId })
                })
                  .then(res => res.json())
                  .then(data => {
                    alert(data.message);
                    $('#shared-tree').jstree(true).refresh();
                    const currentFolderId = $('#shared-tree').jstree('get_selected')[0] ?? null;
                    loadFolderList(currentFolderId);
                    loadTrashBin();
                    loadUsageChart();
                  })
                  .catch(err => {
                    alert("삭제 실패");
                    console.error(err);
                  });
              }
            }
          }
        };
      }
    },
    dnd: {
      is_draggable: function (node) {
        return true;
      }
    }
  });

  $('#shared-tree').on('ready.jstree', function () {
    console.log("✅ 트리 로드 완료됨, 최상위 폴더 리스트 출력");
    loadFolderList(null); // → 루트 기준으로 리스트 출력
  });

  $('#shared-tree').on("changed.jstree", function (e, data) {
    const folderId = data.selected[0];
    if (typeof folderId === 'string' && folderId.startsWith("file-")) {
      $('#shared-tree').jstree('deselect_node', folderId);
      return;
    }
    loadFolderList(folderId ?? null);
  });

  const trashTab = document.getElementById("trash-tab");
  if (trashTab) {
    trashTab.addEventListener("click", function () {
      console.log("🗑️ 휴지통 탭 클릭됨");
      loadTrashBin();
    });
  }
});


// 5. 새 폴더 생성
function createNewFolder() {
  const folderName = document.getElementById("new-folder-name").value;
  const memberNo = document.getElementById("member-no-hidden").value;
  const folderType = folderTypeMap[currentType];

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

let lastSelectedId = null;

function initModalTree() {
  $('#modal-folder-tree').jstree('destroy').empty();

  $('#modal-folder-tree').jstree({
    core: {
      check_callback: true,
      data: {
        url: function () {
          return `/shared/main/tree?type=${currentType}`;
        },
        dataType: 'json',
        dataFilter: function (data) {
          const parsed = JSON.parse(data);
          const targetType = folderTypeMap[currentType] ?? 1;
          const filtered = parsed.filter(item =>
            item.folder_type === targetType && !item.id.toString().startsWith("file-")
          );
          return JSON.stringify(filtered);
        }
      }
    },
    plugins: ['radio']
  });

  //  클릭 -> 선택, 다시 클릭 -> 선택 해제
  $('#modal-folder-tree').on('changed.jstree', function (e, data) {
    if (data.selected.length === 0) {
      selectedParentNo = null;
      lastSelectedId = null;
      return;
    }

    const clickedId = data.selected[0];

    if (lastSelectedId === clickedId) {
      $(this).jstree(true).deselect_node(clickedId);
      selectedParentNo = null;
      lastSelectedId = null;
    } else {
      selectedParentNo = clickedId;
      lastSelectedId = clickedId;
    }
  });
}



// 파일 업로드.
async function uploadFiles() {
  const files = document.getElementById("fileUpload").files;
  const folderId = $('#shared-tree').jstree('get_selected')[0];

  if (!folderId) {
    alert("업로드할 폴더를 선택해주세요.");
    return;
  }

  // 🔥 [추가] 전체 업로드 용량 계산
  let totalUploadSize = 0;
  for (const file of files) {
    totalUploadSize += file.size;
  }

  // 🔥 [추가] 남은 용량 체크
  const usage = await fetch("/shared/trash/usage").then(res => res.json());
  const remain = usage.remain;

  if (totalUploadSize > remain) {
    alert("📦 저장 용량이 부족합니다. 업로드할 수 없습니다.");
    return;
  }

  // [기존 업로드 로직]
  const formData = new FormData();
  for (const file of files) {
    formData.append("files", file);
  }
  formData.append("folderId", folderId);

  const csrfToken = document.querySelector('meta[name="_csrf"]').content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

  fetch("/shared/files/upload", {
    method: "POST",
    headers: { [csrfHeader]: csrfToken },
    body: formData
  })
    .then(async res => {
      if (!res.ok) {
        const errorMsg = await res.text();
        throw new Error(errorMsg || "업로드 실패");
      }

      let data;
      try {
        data = await res.json();  // 또는 .text(), 백엔드 반환 형식에 따라
      } catch (e) {
        console.warn("응답 파싱 중 오류 발생 (무시 가능):", e);
      }

      alert(data?.message || "파일 업로드 완료!");
      const currentFolderId = $('#shared-tree').jstree('get_selected')[0];
      loadFolderList(currentFolderId);
      $('#shared-tree').jstree(true).refresh();
      document.querySelector('input[type="file"]').value = '';
	  loadUsageChart();
    })
    .catch(err => {
      console.error("파일 업로드 중 오류 발생:", err);
      alert("⚠ 파일 업로드 중 오류 발생\n" + err.message);
    });
}

function loadFolderList(folderId) {
  let url = "/shared/files/list";
  const params = new URLSearchParams();

  // ✅ folderId가 없으면: 트리 최상위 노드들의 ID를 리스트로 보여줘야 함
  if (folderId == null) {
    const treeInstance = $('#shared-tree').jstree(true);
    const nodes = treeInstance.get_json('#', { flat: true });
	const rootFolderIds = nodes
	  .filter(n => !n.id.toString().startsWith('file-') && n.parent === '#')
	  .map(n => n.id);

	// 서버에서 List<Long> folderIds로 받기.
	rootFolderIds.forEach(id => params.append("folderIds", id));
    console.log("📂 루트 폴더 리스트 로드:", rootFolderIds);
  } else {
    params.append("folderId", folderId);
    console.log("📂 특정 폴더 리스트 로드:", folderId);
  }

  params.append("type", currentType);
  url += "?" + params.toString();

  fetch(url)
    .then(response => {
      if (!response.ok) throw new Error("응답 실패");
      return response.json();
    })
    .then(data => {
      console.log("📄 폴더/파일 데이터 로드 완료:", data.items?.length ?? 0, "개");
      renderFolderTable(data.items, data.parentFolderNo, folderId ?? null);
    })
    .catch(error => {
      console.error("❌ 목록 조회 실패:", error);
      alert("📂 폴더/파일 목록 조회 중 오류가 발생하였습니다.");
    });
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
			  <td title="${item.name}">${icon} ${item.name}</td>
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

document.addEventListener("DOMContentLoaded", function () {
  const trashTab = document.getElementById("trash-tab");
  if (trashTab) {
    trashTab.addEventListener("click", function () {
      console.log("🗑️ 휴지통 탭 클릭됨");
      loadTrashBin();
    });
  } else {
    console.error("❗ 'trash-tab' 요소를 찾을 수 없습니다.");
  }
});

// 🗑️ 휴지통 불러오기
function loadTrashBin() {
	fetch(`/shared/trash/list?type=${currentType}`)
	    .then(res => res.json())
	    .then(data => {
	      renderTrashTable(data.items); // ✅ 수정 포인트
	    })
	    .catch(err => {
	      console.error("휴지통 목록 조회 실패", err);
	      alert("휴지통을 불러오지 못했습니다.");
	    });
}

// 🗑️ 휴지통 테이블 렌더링
function renderTrashTable(items) {
  const tbody = document.querySelector("#trash-table tbody");  // ✅ 수정됨
  tbody.innerHTML = ""; // 기존 비우기

  if (items.length === 0) {
    const row = document.createElement("tr");
    row.innerHTML = `<td colspan='5'>휴지된 폴더/파일이 없습니다.</td>`; // ✅ colspan도 5로 수정
    tbody.appendChild(row);
    return;
  }

  items.forEach(item => {
    const tr = document.createElement("tr");
    const icon = item.type === "folder" ? "📁" : "📄";
    const typeLabel = item.type === "folder" ? "폴더" : "파일";
    const size = item.size ? formatFileSize(item.size) : "-";
    const regDate = formatDate(item.deletedAt); // 삭제일 기준

    tr.innerHTML = `
      <td><input type="checkbox" class="trash-checkbox" data-id="${item.id}" data-type="${item.type}"></td>
      <td>${icon} ${item.name}</td>
      <td>${typeLabel}</td>
      <td>${size}</td>
      <td>${regDate}</td>
    `;

    tbody.appendChild(tr);
  });
}

// 휴지통 폴더/파일 복구
function restoreSelected() {
  const checkboxes = document.querySelectorAll(".trash-checkbox:checked");
  if (checkboxes.length === 0) {
    alert("복구할 항목을 선택해주세요.");
    return;
  }

  const folderIds = [];
  const fileIds = [];

  checkboxes.forEach(cb => {
    const id = parseInt(cb.dataset.id);
    const type = cb.dataset.type;
    if (type === "folder") folderIds.push(id);
    else fileIds.push(id);
  });

  fetch("/shared/restore", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      [document.querySelector('meta[name="_csrf_header"]').content]:
        document.querySelector('meta[name="_csrf"]').content
    },
	body: JSON.stringify({ folderIds, fileIds, type: currentType })
  })
  .then(async res => {
    if (!res.ok) {
      const errorMsg = await res.text();
      throw new Error(errorMsg || "복구 실패");
    }

    let msg;
    try {
      msg = await res.text();
    } catch (e) {
      console.warn("응답 파싱 오류 (무시 가능):", e);
    }

    alert(msg || "복구 완료!");
    loadTrashBin();
    $('#shared-tree').jstree(true).refresh();
    loadUsageChart();
  })
  .catch(err => {
    console.error("복구 실패", err);
    alert("복구 중 오류 발생: " + err.message);
  });
}

// 휴지통 삭제 버튼
function deleteSelected() {
  const checkboxes = document.querySelectorAll(".trash-checkbox:checked");
  if (checkboxes.length === 0) {
    alert("삭제할 항목을 선택해주세요.");
    return;
  }

  if (!confirm("선택한 항목을 완전히 삭제하시겠습니까? 복구할 수 없습니다.")) return;

  const folderIds = [];
  const fileIds = [];

  checkboxes.forEach(cb => {
    const id = parseInt(cb.dataset.id);
    const type = cb.dataset.type;
    if (type === "folder") folderIds.push(id);
    else fileIds.push(id);
  });

  fetch("/shared/delete/permanent", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      [document.querySelector('meta[name="_csrf_header"]').content]:
        document.querySelector('meta[name="_csrf"]').content
    },
	body: JSON.stringify({ folderIds, fileIds, type: currentType })
  })
  .then(async res => {
    if (!res.ok) {
      const errorMsg = await res.text();
      throw new Error(errorMsg || "삭제 실패");
    }

    let msg;
    try {
      msg = await res.text();
    } catch (e) {
      console.warn("응답 파싱 오류 (무시 가능):", e);
    }

    alert(msg || "삭제 완료!");
    loadTrashBin();
    $('#shared-tree').jstree(true).refresh();
    loadUsageChart();
  })
  .catch(err => {
    console.error("삭제 실패", err);
    alert("삭제 중 오류 발생: " + err.message);
  });
}
