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

  $('#shared-tree').on("changed.jstree", function (e, data) {
    const folderId = data.selected[0];
    if (folderId) {
      window.location.href = '/shared?folderNo=' + folderId;
    }
  });

  // 2. 폴더 모달창
  $('#folderModal').on('shown.bs.modal', function (){
    $('#new-folder-name').val('');    // 폴더 이름 초기화
    $('#folder-type').val('1');        // 폴더 타입 초기화
    selectedParentNo = null;           // 선택 초기화

    // 트리 초기화
    $('#modal-folder-tree').jstree("destroy").empty();
    $('#modal-folder-tree').jstree({
      core: {
        multiple: false,  // 🔥 핵심 추가: 하나만 선택
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
