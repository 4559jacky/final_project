function loadUsageChart() {
  const chartArea = document.querySelector("#chart-pie-simple");
  if (!chartArea) return;
  chartArea.innerHTML = "";

  fetch(`/shared/trash/usage?type=${window.currentType || 'personal'}`)
    .then(res => res.json())
    .then(data => {
      const active = data.active || 0;
      const trash = data.trash || 0;
      const remain = data.remain || 0;

      const currentTheme = document.documentElement.getAttribute("data-bs-theme") || "light";
      const isDark = currentTheme === "dark";

      const chartOptions = {
        series: [active, trash, remain],
        chart: {
          type: "donut",
          height: 300,
          background: "transparent",
          fontFamily: "inherit"
        },
        theme: {
          mode: currentTheme
        },
        labels: ["문서함", "휴지통", "남은 용량"],
        colors: [
          getComputedStyle(document.documentElement).getPropertyValue('--bs-secondary').trim(), // 문서함
          "rgb(250, 137, 107)",   // 휴지통
          "rgb(255, 174, 31)"     // 남은 용량
        ],
        stroke: {
          show: true,
          colors: ["rgb(255, 255, 255)"], // ✅ 조각 경계선 흰색
          width: 2
        },
		dataLabels: {
		  enabled: true,
		  style: {
		    fontSize: "13px",
		    fontWeight: "bold",
		    colors: ["#fff"]  // 밝은 색으로 덮기
		  },
		  dropShadow: {
		    enabled: false
		  },
		  background: {
		    enabled: false
		  }
		},
        legend: {
          position: "bottom",
          horizontalAlign: "center",
          fontSize: "13px",
          labels: {
            useSeriesColors: true
          }
        },
        tooltip: {
          
        }
      
      };

      const chart = new ApexCharts(chartArea, chartOptions);
      chart.render();

	  const toMB = (val) => (val / 1048576).toFixed(2);

	  document.getElementById("total-size").textContent = `${toMB(active + trash + remain)}MB`;
	  document.getElementById("active-size").textContent = `${toMB(active)}MB`;
	  document.getElementById("trash-size").textContent = `${toMB(trash)}MB`;
	  document.getElementById("remain-size").textContent = `${toMB(remain)}MB`;

    })
    .catch(err => {
      console.error("📉 차트 로딩 실패:", err);
    });
}

// 전역 등록
window.loadUsageChart = loadUsageChart;

if (typeof ApexCharts !== "undefined") {
  loadUsageChart();
} else {
  window.addEventListener("load", () => {
    if (typeof loadUsageChart === "function") {
      loadUsageChart();
    }
  });
}
