let selectedParentNo = null;
$(document).ready(function () {
  console.log("✅ jstree.js 로딩됨");
  // 1 공유 트리
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

 /*  // 개인 폴더 tree
  $('#personal-tree').jstree({
	core : {
	 data : {
		url: '/shared/main/tree/personal', // 개인 폴더용 API
		dataType: 'json'
	 },
	 themes: {dots:true, icons: true}
	}
  })
  $('#personal-tree').on("changed.jstree", function (e, data){
	const folderId = data.selected[0];
	if (folderId) {
	      window.location.href = '/shared?folderNo=' + folderId; // 나중에 에디터 영역 만들때 쓰자.
	    }
  })  */


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

