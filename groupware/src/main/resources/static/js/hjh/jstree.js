// ✅ 분기별 트리, 차트, 휴지통 처리를 위한 통합 JS

let currentDocType = 'personal';

function getTreeId() {
  return `#${currentDocType}-tree`;
}

function getChartId() {
  return `#${currentDocType}-chart`;
}

function getTrashTableId() {
  return `#trash-table-${currentDocType}`;
}

$(document).ready(function () {
  document.querySelectorAll('#shared-tab button[data-bs-toggle="tab"]').forEach(btn => {
    btn.addEventListener("shown.bs.tab", function (e) {
      const tabId = e.target.id;
      if (tabId.includes("personal")) currentDocType = "personal";
      else if (tabId.includes("department")) currentDocType = "department";
      else if (tabId.includes("public")) currentDocType = "public";
      refreshAllForCurrentType();
    });
  });
  refreshAllForCurrentType();
});

function refreshAllForCurrentType() {
  showTree(currentDocType);
  loadTree(currentDocType);
  showChart(currentDocType);
  loadChart(currentDocType);
  loadTrash(currentDocType);
}

function showTree(type) {
  document.querySelectorAll('.shared-tree').forEach(el => el.style.display = 'none');
  document.getElementById(`${type}-tree`).style.display = 'block';
}

function showChart(type) {
  document.querySelectorAll('.chart-container').forEach(el => el.style.display = 'none');
  document.getElementById(`${type}-chart`).style.display = 'block';
}

function loadTree(type) {
  const id = `#${type}-tree`;
  $(id).jstree("destroy").empty();
  $(id).jstree({
    core: {
      data: {
        url: `/shared/main/tree?type=${type}`,
        dataType: 'json'
      }
    },
    plugins: ['dnd']
  });
}

function loadChart(type) {
  fetch(`/shared/trash/usage?type=${type}`)
    .then(res => res.json())
    .then(data => {
      const chartArea = document.getElementById(`${type}-chart`);
      chartArea.innerHTML = "";
      const chart = new ApexCharts(chartArea, {
        series: [data.active, data.trash, data.remain],
        chart: { type: 'donut', height: 220 },
        labels: ["문서함", "휴지통", "남은 용량"]
      });
      chart.render();
    });
}

function loadTrash(type) {
  fetch(`/shared/trash/list?type=${type}`)
    .then(res => res.json())
    .then(data => renderTrash(data.items, type));
}

function renderTrash(items, type) {
  const tbody = document.querySelector(`#trash-table-${type} tbody`);
  tbody.innerHTML = "";
  if (items.length === 0) {
    const row = document.createElement("tr");
    row.innerHTML = `<td colspan='5'>휴지된 항목이 없습니다.</td>`;
    tbody.appendChild(row);
    return;
  }
  items.forEach(item => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td><input type='checkbox' class='trash-checkbox' data-id='${item.id}' data-type='${item.type}'></td>
      <td>${item.name}</td>
      <td>${item.type === 'folder' ? '폴더' : '파일'}</td>
      <td>${item.size ? formatFileSize(item.size) : '-'}</td>
      <td>${formatDate(item.deletedAt)}</td>`;
    tbody.appendChild(tr);
  });
}

function restoreSelected(type) {
  const checked = document.querySelectorAll(`#trash-table-${type} .trash-checkbox:checked`);
  const folderIds = [], fileIds = [];
  checked.forEach(cb => {
    const id = parseInt(cb.dataset.id);
    cb.dataset.type === 'folder' ? folderIds.push(id) : fileIds.push(id);
  });
  fetch("/shared/restore", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json',
      [document.querySelector('meta[name="_csrf_header"]').content]:
        document.querySelector('meta[name="_csrf"]').content
    },
    body: JSON.stringify({ folderIds, fileIds, type })
  }).then(() => loadTrash(type));
}

function deleteSelected(type) {
  const checked = document.querySelectorAll(`#trash-table-${type} .trash-checkbox:checked`);
  const folderIds = [], fileIds = [];
  checked.forEach(cb => {
    const id = parseInt(cb.dataset.id);
    cb.dataset.type === 'folder' ? folderIds.push(id) : fileIds.push(id);
  });
  fetch("/shared/delete/permanent", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json',
      [document.querySelector('meta[name="_csrf_header"]').content]:
        document.querySelector('meta[name="_csrf"]').content
    },
    body: JSON.stringify({ folderIds, fileIds, type })
  }).then(() => loadTrash(type));
}

function formatFileSize(bytes) {
  if (bytes < 1024) return bytes + "B";
  else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + "KB";
  else return (bytes / 1048576).toFixed(1) + "MB";
}

function formatDate(dateStr) {
  const date = new Date(dateStr);
  return date.toLocaleDateString();
}
