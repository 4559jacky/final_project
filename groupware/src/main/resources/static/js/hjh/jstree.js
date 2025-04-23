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
      window.location.href = '/shared?folderNo=' + folderId;
    }
  });

  $('#expand_all').click(() => $('#shared-tree').jstree('open_all'));
  $('#collapse_all').click(() => $('#shared-tree').jstree('close_all'));
});
