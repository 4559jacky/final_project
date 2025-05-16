//  공유문서함 JS 전체 리팩토링

window.currentType = 'personal';
window.initialFolderId = null;
window.currentUserDeptNo = parseInt(document.getElementById("current-user-dept").value);
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

  function loadTree(type, callback) {
    // ✅ 기존 트리 제거 후 초기화
    $('#shared-tree').jstree('destroy').empty();

    // ✅ 트리 생성
    $('#shared-tree').jstree({
      core: {
        check_callback: function (operation, node, parent, position, more) {
          if (operation === "move_node") {
            const isFile = node.id.startsWith("file-");
            if (isFile && parent.id === "#") return false;
            if (parent.id.startsWith("file-")) return false;

            if (type === "department") {
              const nodeDeptNo = parseInt(node.original?.dept_no ?? -1);
              const parentDeptNo = parseInt(parent.original?.dept_no ?? -1);
              if (nodeDeptNo !== currentUserDeptNo || parentDeptNo !== currentUserDeptNo) {
                console.warn(`🚫 부서 이동 차단: nodeDeptNo=${nodeDeptNo}, parentDeptNo=${parentDeptNo}, current=${currentUserDeptNo}`);
                return false;
              }
            }
          }
          return true;
        },
        data: {
          url: `/shared/main/tree?type=${type}`,
          dataType: 'json',
          dataFilter: function (data) {
            const parsed = JSON.parse(data);
            const targetType = folderTypeMap[type] ?? 1;
            const filtered = parsed.filter(item => {
              if (item.id.toString().startsWith("file-")) return true;
              return parseInt(item.folder_type) === targetType;
            });
            window.rootNodeList = filtered
              .filter(item => !item.id.toString().startsWith("file-") && item.parent === "#")
              .map(item => item.id);
            return JSON.stringify(filtered);
          }
        },
        themes: { dots: true, icons: true }
      },
      plugins: ['dnd'],
      dnd: {
        is_draggable: function (node) {
          if (type === "department") {
            const deptNo = parseInt(node[0]?.original?.dept_no ?? -1);
            return deptNo === currentUserDeptNo;
          }
          return true;
        }
      }
    })

    // ✅ 트리 로딩 완료
    .on('ready.jstree', function () {
      console.log("✅ 트리 로드 완료됨");
      if (typeof callback === 'function') callback();
    })

    // ✅ 폴더 클릭 시 리스트 변경
    .on('changed.jstree', function (e, data) {
      console.log("✅ changed 이벤트 발생", data.selected);
      if (suppressChangeEvent) return;
      const folderId = data.selected[0];
      if (typeof folderId === 'string' && folderId.startsWith("file-")) {
        $('#shared-tree').jstree('deselect_node', folderId);
        return;
      }
      loadFolderList(folderId ?? null);
    })

    // ✅ 다른 부서 폴더 펼침 차단
    .on('before_open.jstree', function (e, data) {
      if (type === "department") {
        const deptNo = parseInt(data.node.original?.dept_no ?? -1);
        if (deptNo !== currentUserDeptNo) {
          console.warn("🚫 다른 부서 폴더 펼치기 차단됨:", data.node.text);
          e.preventDefault(); // 열기 차단
        }
      }
    });
  }
 
  $('#shared-tree').jstree({
    core: {
		check_callback: function (operation, node, parent, position, more) {
		  if (operation === "move_node") {
		    const isFile = node.id.startsWith("file-");

		    // 🔒 파일을 루트로 이동 금지
		    if (isFile && parent.id === "#") return false;

		    // 🔒 파일을 파일에 넣는 건 금지
		    if (parent.id.startsWith("file-")) return false;

		    // 🔒 부서 간 이동 차단
		    if (currentType === "department") {
		      const nodeDeptNo = parseInt(node.original && node.original.dept_no != null ? node.original.dept_no : -1);
		      const parentDeptNo = parseInt(parent.original && parent.original.dept_no != null ? parent.original.dept_no : -1);

		      if (nodeDeptNo !== currentUserDeptNo || parentDeptNo !== currentUserDeptNo) {
		        console.warn(`🚫 이동 차단됨 - nodeDeptNo: ${nodeDeptNo}, parentDeptNo: ${parentDeptNo}, 내 부서: ${currentUserDeptNo}`);
		        return false;
		      }
		    }
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

			  const filtered = parsed.filter(item => {
			    // 폴더만 필터링, 파일은 그대로 통과
			    if (item.id.toString().startsWith("file-")) return true;
				return parseInt(item.folder_type) === targetType;
			  });

			  // 루트 노드 업데이트
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
    plugins: ['dnd'],
  /*  contextmenu: {
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
    },*/
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

  let suppressChangeEvent = false; // 🚫 중복 방지용 플래그 추가
  $('#shared-tree').on("changed.jstree", function (e, data) {
    if (suppressChangeEvent) return; // 중복 방지
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
  
  if (selectedParentNo == null) {
    alert("상위 폴더를 선택해주세요.");
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

  $('#modal-folder-tree').on('changed.jstree', function (e, data) {
    if (data.selected.length === 0) {
      selectedParentNo = null;
      lastSelectedId = null;
      return;
    }

    const clickedId = data.selected[0];
    const tree = $(this).jstree(true);
    const node = tree.get_node(clickedId);

    // 🔥 다른 부서 폴더는 선택 불가
    if (currentType === "department") {
      const folderDeptNo = node.original.dept_no ?? null;
      if (folderDeptNo !== currentUserDeptNo) {
        alert("🚫 다른 부서의 폴더는 선택할 수 없습니다.");
        tree.deselect_node(clickedId);
        selectedParentNo = null;
        lastSelectedId = null;
        return;
      }
    }

    // ✅ 같은 폴더 다시 클릭하면 해제
    if (lastSelectedId === clickedId) {
      tree.deselect_node(clickedId);
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
    alert("업로드할 폴더를 선택해주세요");
		
    return;
  }
	
  if (files.length === 0) {
    alert("업로드할 파일을 선택해주세요.");
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
	  console.error("❌ 목록 조회 실패:", error.message);
	  alert("🚫 접근 권한이 없습니다.");

	  suppressChangeEvent = true; // 이벤트 무시 시작
	  $('#shared-tree').jstree('deselect_all');
	  setTimeout(() => suppressChangeEvent = false, 100); 
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
		  <td class="doc-checkbox-cell"><input type="checkbox" class="doc-checkbox" data-id="${item.id}" data-type="${item.type}"></td>
		  <td title="${item.name}">${icon} ${item.name}</td>
		  <td>${typeLabel}</td>
		  <td>${size}</td>
		  <td>${regDate}</td>
		`;
		
		const checkbox = tr.querySelector(".doc-checkbox");
		checkbox.addEventListener("click", function(e) {
		  e.stopPropagation(); // ✅ 체크박스 클릭 시 tr 이벤트 막기
		});
		
		// ✅ 여기가 핵심 조건 분기
			if (currentType === "department" && 
		  item.type === "folder" &&
		  item.deptNo !== undefined &&
		  item.deptNo !== currentUserDeptNo) {
			
				tr.style.opacity = 0.5;
				tr.style.cursor = "not-allowed";
				tr.title = "다른 부서의 폴더입니다.";
				tr.addEventListener("click", function(e) {
					alert("🚫 다른 부서의 폴더에는 접근할 수 없습니다.");
					e.stopPropagation();
				});
			} else {
				tr.addEventListener("click", function(){
					if(item.type === "folder"){
						onFolderClick(item.id);
					}else{
						onFileClick(item.id);
					}
				});
			}
		
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

function onFolderClick(folderId){
	suppressChangeEvent = true;
	$('#shared-tree').jstree('deselect_all');
	$('#shared-tree').jstree('select_node', folderId); // ✅ 이러면 changed.jstree가 호출됨
	loadFolderList(folderId);
	setTimeout(() => suppressChangeEvent = false, 100);
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

document.addEventListener("DOMContentLoaded", function () {
  const btnStart = document.getElementById("btn-start-delete");
  const btnConfirm = document.getElementById("btn-confirm-delete");
  const btnCancel = document.getElementById("btn-cancel-delete");
  const wrapper = document.querySelector(".table-wrapper");

  btnStart.addEventListener("click", () => {
    wrapper.classList.add("delete-mode");
    btnStart.classList.add("d-none");
    btnConfirm.classList.remove("d-none");
    btnCancel.classList.remove("d-none");
  });

  btnCancel.addEventListener("click", () => {
    wrapper.classList.remove("delete-mode");
    btnStart.classList.remove("d-none");
    btnConfirm.classList.add("d-none");
    btnCancel.classList.add("d-none");

    // 체크 해제
    document.querySelectorAll(".doc-checkbox").forEach(cb => cb.checked = false);
  });

  btnConfirm.addEventListener("click", () => {
    deleteSelectedDocs(); // 기존 함수 그대로 사용
    btnCancel.click(); // 완료 후 취소 동작으로 되돌림
  });
});

window.deleteSelectedDocs = async function () {
  const checkboxes = document.querySelectorAll(".doc-checkbox:checked");
  if (checkboxes.length === 0) {
    alert("삭제할 항목을 선택해주세요.");
    return;
  }

  if (!confirm("선택한 항목을 삭제하시겠습니까?")) return;

  const folderIds = [];
  const fileIds = [];

  checkboxes.forEach(cb => {
    const id = parseInt(cb.dataset.id);
    const type = cb.dataset.type;
    if (type === "folder") folderIds.push(id);
    else fileIds.push(id);
  });

  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
  const csrfToken = document.querySelector('meta[name="_csrf"]').content;

  try {
    // 🔁 폴더 삭제
    for (const id of folderIds) {
      await fetch("/shared/folder/delete", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          [csrfHeader]: csrfToken
        },
        body: JSON.stringify({ id })
      });
    }

    // 🔁 파일 삭제
    for (const id of fileIds) {
      await fetch("/shared/file/delete", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          [csrfHeader]: csrfToken
        },
        body: JSON.stringify({ id })
      });
    }

    alert("삭제 완료!");
    const currentFolderId = $('#shared-tree').jstree('get_selected')[0];
    loadFolderList(currentFolderId);
    $('#shared-tree').jstree(true).refresh();
    loadUsageChart();
  } catch (err) {
    console.error("삭제 중 오류 발생:", err);
    alert("삭제 중 오류 발생: " + err.message);
  }
}

/*// 💥 alert 완전 대체
window.alert = function(message) {
  const msgEl = document.getElementById("customAlertMsg");
  msgEl.textContent = message;

  const modal = new bootstrap.Modal(document.getElementById("customAlertModal"));
  modal.show();
};

// 💥 confirm 완전 대체 (비동기 Promise 버전)
window.confirm = function(message) {
  return new Promise((resolve) => {
    const msgEl = document.getElementById("customConfirmMsg");
    msgEl.textContent = message;

    const modalEl = document.getElementById("customConfirmModal");
    const modal = new bootstrap.Modal(modalEl);
    modal.show();

    const yesBtnOld = document.getElementById("confirmYesBtn");
    const yesBtnNew = yesBtnOld.cloneNode(true);
    yesBtnOld.parentNode.replaceChild(yesBtnNew, yesBtnOld);

    yesBtnNew.addEventListener("click", () => {
      modal.hide();
      resolve(true);
    });

    modalEl.addEventListener("hidden.bs.modal", () => {
      resolve(false);
    }, { once: true });
  });
};*/

document.addEventListener("DOMContentLoaded", function () {
  const fileInput = document.getElementById("fileUpload");
  const fileNameSpan = document.getElementById("selectedFileNames");

  fileInput.addEventListener("change", function () {
    const files = Array.from(fileInput.files);
    if (files.length === 0) {
      fileNameSpan.textContent = "선택된 파일 없음";
    } else if (files.length === 1) {
      fileNameSpan.textContent = `선택된 파일: ${files[0].name}`;
    } else {
      fileNameSpan.innerHTML = `선택된 파일 (${files.length}개):<br>` +
        files.map(f => f.name).join("<br>");
    }
  });
});

