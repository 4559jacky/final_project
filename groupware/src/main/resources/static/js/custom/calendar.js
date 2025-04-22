/*========Calender Js=========*/
document.addEventListener("DOMContentLoaded", function () {
  /*=================*/
  //  Calender Date variable
  /*=================*/
  var newDate = new Date();
  function getDynamicMonth() {
    getMonthValue = newDate.getMonth();
    _getUpdatedMonthValue = getMonthValue + 1;
    if (_getUpdatedMonthValue < 10) {
      return `0${_getUpdatedMonthValue}`;
    } else {
      return `${_getUpdatedMonthValue}`;
    }
  }
  /*=================*/
  // Calender Modal Elements
  /*=================*/
  var getModalTitleEl = document.querySelector("#event-title");
  var getModalStartDateEl = document.querySelector("#event-start-date");
  var getModalEndDateEl = document.querySelector("#event-end-date");
  var getModalAddBtnEl = document.querySelector(".btn-add-event");
  var getModalUpdateBtnEl = document.querySelector(".btn-update-event");
  var getModalDeleteBtnEl = document.querySelector(".btn-delete-event");
  var calendarsEvents = {
    Danger: "danger",
    Success: "success",
    Primary: "primary",
    Warning: "warning",
  };
  /*=====================*/
  // Calendar Elements and options
  /*=====================*/
  var calendarEl = document.querySelector("#calendar");
  var checkWidowWidth = function () {
    if (window.innerWidth <= 1199) {
      return true;
    } else {
      return false;
    }
  };
  var calendarHeaderToolbar = {
    left: "prev next addEventButton",
    center: "title",
    right: "dayGridMonth,timeGridWeek,timeGridDay",
  };
  var buttonText = {
		today : '현재날짜',
		month : '월',
		week : '주',
		day : '일',
  };
  var calendarEventsList = "";

  /*=====================*/
  // Calendar Select fn.
  /*=====================*/
  // 캘린더 일정추가시 시간셋팅
  var calendarSelect = function (info) {
    getModalAddBtnEl.style.display = "block";
    getModalUpdateBtnEl.style.display = "none";
    myModal.show();

    // 시작일시: 현재 시간
	const startDate = new Date(info.start); // 드래그한 날짜
    const now = new Date();
	startDate.setHours(0, 0, 0, 0); // 현재 시간만 적용

    // 종료일시: 드래그 끝 날짜의 오후 11:59
    const rawEndDate = new Date(info.end);
    rawEndDate.setDate(rawEndDate.getDate() - 1); //FullCalendar는 end를 다음 날로 넘겨서 하루뺌
    rawEndDate.setHours(23, 59, 0, 0); // 오후 11:59로 설정

    // input에 넣을 형식: YYYY-MM-DDTHH:mm
    const formatToLocalDatetime = (date) => {
    const offset = date.getTimezoneOffset();
    const local = new Date(date.getTime() - offset * 60 * 1000);
    return local.toISOString().slice(0, 16);
    };
    getModalStartDateEl.value = formatToLocalDatetime(startDate);
    getModalEndDateEl.value = formatToLocalDatetime(rawEndDate);
  };

  /*=====================*/
  // Calendar AddEvent fn.
  /*=====================*/
  var calendarAddEvent = function () {
    var currentDate = new Date();
    var dd = String(currentDate.getDate()).padStart(2, "0");
    var mm = String(currentDate.getMonth() + 1).padStart(2, "0"); //January is 0!
    var yyyy = currentDate.getFullYear();
    var combineDate = `${yyyy}-${mm}-${dd}T00:00`;
    getModalAddBtnEl.style.display = "block";
    getModalUpdateBtnEl.style.display = "none";
    myModal.show();
    getModalStartDateEl.value = combineDate;
  };
  /*=====================*/
  // Calender Event Function
  /*=====================*/
 /* 	var calendarEventClick = function (info) {
		console.log("이벤트 클릭됨:", info.event);
    var eventObj = info.event;

	document.getElementById("event-writer").value = eventObj.extendedProps.writer || "";
	 document.getElementById("event-department").value = eventObj.extendedProps.department || "";
	 document.getElementById("event-title").value = eventObj.title || "";
	 document.getElementById("event-start-date").value = eventObj.startStr.slice(0, 10);
	 document.getElementById("event-end-date").value = eventObj.endStr ? eventObj.endStr.slice(0, 10) : "";
	 document.getElementById("event-description").value = eventObj.extendedProps.description || "";
	 
	 const detailModal = new bootstrap.Modal(document.getElementById("eventModaldetail"));
	 detailModal.show();
	 
    if (eventObj.url) {
      window.open(eventObj.url);

      info.jsEvent.preventDefault();
    } else {
      var getModalEventId = eventObj._def.publicId;
      var getModalEventLevel = eventObj._def.extendedProps["calendar"];
      var getModalCheckedRadioBtnEl = document.querySelector(
        `input[value="${getModalEventLevel}"]`
      );

      getModalTitleEl.value = eventObj.title;
      getModalStartDateEl.value = eventObj.startStr.slice(0, 10);
      getModalEndDateEl.value = eventObj.endStr.slice(0, 10);
      getModalCheckedRadioBtnEl.checked = true;
      getModalUpdateBtnEl.setAttribute(
        "data-fc-event-public-id",
        getModalEventId
      );
      getModalAddBtnEl.style.display = "none";
      getModalUpdateBtnEl.style.display = "block";
    }
  };*/
  /*=====================*/
  // Active Calender
  /*=====================*/
  let getEvent = null;
  var selectedType = ""; // 현재 선택된 타입
  var calendar = new FullCalendar.Calendar(calendarEl, {
    selectable: true,
	locale:'ko',
	dayMaxEventRows: true,
	eventDisplay: 'block',
	editable:true,
	/*expandRows: true,*/
	allDaySlot: false,
	displayEventTime: false, //일정바 앞 시간
	slotDuration: '00:30:00',
	initialView: 'dayGridMonth',
	navLinks: true,
	nowIndicator:true,
	googleCalendarApiKey:'',
    height: checkWidowWidth() ? 900 : 1052,
    initialView: checkWidowWidth() ? "listWeek" : "dayGridMonth",
    initialDate: `${newDate.getFullYear()}-${getDynamicMonth()}-07`,
    headerToolbar: calendarHeaderToolbar,buttonText,
    events: function(fetchInfo, successCallback,failureCallback){
		fetch(`/calendar/events?start=${fetchInfo.startStr}&end=${fetchInfo.endStr}`)
		        .then(response => {
		            if (!response.ok) {
		                throw new Error("Network response was not ok");
		            }
		            return response.json();
		        })
		        .then(data => {
					console.log("이벤트데이터:"+data);
		            successCallback(data); // FullCalendar에 이벤트 전달
		        })
		        .catch(error => {
		            console.error("Error fetching calendar events:", error);
		            failureCallback(); // 오류 처리
		        });
	},
	// 달력에 있는 일정클릭시 상세모달창open 및 db데이터 화면에 출력
	eventClick:function(info){
		const eventId = info.event.id;
		getEvent = info.event;
		
		fetch('/plan/detail/'+eventId, {
			method:'get'
		})
		  .then(res =>{
			if (!res.ok) throw new Error("요청 실패");
			return res.json();
		  })
		  .then(data => {
			new bootstrap.Modal(document.getElementById("eventModaldetail")).show();
			
			document.querySelector("#eventModaldetail .btn-update-event").dataset.id = data.plan_no;
			console.log("가져온 데이터:", data);
			
			// 고정값
			document.getElementById("detail-event-id").value = data.plan_id;
			document.getElementById("detail-event-writer").value = data.member_name;
		    document.getElementById("detail-event-department").value = data.dept_name;
			document.getElementById("detail-event-created-date").value = data.reg_date;
			document.getElementById("detail-event-modified-date").value = data.mod_date;
			// 수정가능값
		    document.getElementById("detail-event-title").value = data.plan_title;
		    document.getElementById("detail-event-description").value = data.plan_content;
		    document.getElementById("detail-event-start-date").value = data.start_date.split('T')[0];;
		    document.getElementById("detail-event-end-date").value = data.end_date.split('T')[0];
			const planType = data.plan_type;
			    if (planType === "회사") {
			      document.getElementById("detail-type-company").checked = true;
			    } else if (planType === "부서") {
			      document.getElementById("detail-type-team").checked = true;
			    } else if (planType === "개인") {
			      document.getElementById("detail-type-personal").checked = true;
			    } else if (planType === "휴가") {
			      document.getElementById("detail-type-leave").checked = true;
			    }
		  })
		  .catch(err => console.error("디테일 로딩 실패", err));
	},
	// 이미 추가된 일정을 다른날로 드래그해서 이동
	/*eventDrop: function(info) {
	  const movedEvent = info.event;

	  const planId = movedEvent.id;
	  const newStartDate = movedEvent.start.toISOString().slice(0, 10);
	  const newEndDate = movedEvent.end
	    ? movedEvent.end.toISOString().slice(0, 10)
	    : newStartDate;

	  fetch("/plan/" + planId + "/update", {
	    method: "POST",
	    headers: {
	      "Content-Type": "application/json",
	      "header": document.querySelector('meta[name="_csrf_header"]').content,
	      "X-CSRF-Token": document.querySelector('meta[name="_csrf"]').content,
	    },
	    body: JSON.stringify({
	      id: planId,
	      startDate: newStartDate,
	      endDate: newEndDate
	    }),
	  })
	    .then((res) => res.json())
	    .then((data) => {
	      if (data.res_code === "200") {
	        alert("일정 날짜가 수정되었습니다.");
	      } else {
	        alert("일정 수정 실패: " + data.res_msg);
	        info.revert(); // 서버 저장 실패 시 되돌리기
	      }
	    })
	    .catch((err) => {
	      console.error("드래그 날짜 이동 실패", err);
	      alert("서버 오류 발생. 다시 시도해 주세요.");
	      info.revert(); // 에러 발생 시 되돌리기
	    });
	},*/
    select: calendarSelect,
    unselect: function () {
      console.log("unselected");
    },
    customButtons: {
      addEventButton: {
        text: "일정 추가",
        click: calendarAddEvent,
      },
    },
    /*eventClassNames: function ({ event: calendarEvent }) {
		const planType = calendarEvent._def.extendedProps.planType;

		 // 일정 타입에 따라 클래스 반환
		 switch (planType) {
		   case "휴가":
		     return ["event-vacation-bg"];         // 배경 스타일용 클래스
		   case "부서":
		     return ["event-department-bg"];
		   case "개인":
		     return ["event-personal-bg"];
		   case "회사":
		     return ["event-company-bg"];
		   default:
		     return ["event-default-bg"];          // 혹시 모를 기본값
		 }
    },*/
    windowResize: function (arg) {
      if (checkWidowWidth()) {
        calendar.changeView("listWeek");
        calendar.setOption("height", 900);
      } else {
        calendar.changeView("dayGridMonth");
        calendar.setOption("height", 1052);
      }
    },
	// now Date를 기준으로 이전날짜 클릭 불가능하게 하는 코드
	selectAllow: function(selectInfo) {
		const now = new Date();
		  now.setHours(0, 0, 0, 0); // 오늘 자정 기준으로 시간 초기화
		const start = new Date(selectInfo.start);
		  start.setHours(0, 0, 0, 0); // 선택한 날짜도 자정 기준
		  return start >= now; // 오늘 날짜는 OK, 과거는 막힘
	}
  });
  /*=====================*/
  // Update Calender Event
  /*=====================*/
	////*상세모달창 수정버튼 클릭시 동작*////
	  getModalUpdateBtnEl.addEventListener("click", function () {
		  var planId = document.getElementById("detail-event-id").value;
		  var newTitle = document.getElementById("detail-event-title").value;
		  var newDescription = document.getElementById("detail-event-description").value;
		  var newStartDate = document.getElementById("detail-event-start-date").value;
		  var newEndDate = document.getElementById("detail-event-end-date").value;
		  var newCalendarType = document.querySelector('input[name="plan_type"]:checked')?.value;
		  console.log("value값 확인:",newCalendarType);

	  // 날짜 포맷 수정
	      const toISOFormat = (dateStr) => {
	          // 예: '2025-04-21 21:12' => '2025-04-21T21:12'
	          return dateStr.replace('T', ' ');
	      };
	      const formattedStartDate = toISOFormat(newStartDate);
	      const formattedEndDate = toISOFormat(newEndDate);
	  
	  // 수정일 자동 반영
		  var now = new Date();
		  var formattedDateTime =
		    now.getFullYear() + "-" +
		    String(now.getMonth() + 1).padStart(2, '0') + "-" +
		    String(now.getDate()).padStart(2, '0') + " " +
		    String(now.getHours()).padStart(2, '0') + ":" +
		    String(now.getMinutes()).padStart(2, '0');
		  document.getElementById("detail-event-modified-date").value = formattedDateTime;
	  
	  // 서버에 수정된 값 전달..
		  const payload = {
			plan_title: newTitle,
			        plan_content: newDescription,
			        plan_type: newCalendarType,
			        start_date: formattedStartDate,
			        end_date: formattedEndDate,
			        mod_date: formattedDateTime
		          };
	
		    fetch("/plan/"+planId+"/update", {
		      method: "POST",
		      headers: {
				'Content-Type': 'application/json',
				'header': document.querySelector('meta[name="_csrf_header"]').content,
				'X-CSRF-Token': document.querySelector('meta[name="_csrf"]').content
		      },
		      body: JSON.stringify(payload)
		    })
		      .then(res => res.json())
		      .then(data => {
		        alert(data.res_msg);
		        if (data.res_code === "200") {
		          bootstrap.Modal.getInstance(document.getElementById("eventModaldetail")).hide();
				  location.reload();
		        }
		      })
		      .catch(err => {
		        console.error("서버 수정 실패", err);
		      });
  });
  /*=====================*/
  // Add Calender Event
  /*=====================*/
  getModalAddBtnEl.addEventListener("click", function () {
    var getTitleValue = getModalTitleEl.value;
    var setModalStartDateValue = getModalStartDateEl.value;
    var setModalEndDateValue = getModalEndDateEl.value;
    var getModalCheckedRadioBtnEl = document.querySelector('input[name="event-level"]:checked');
    var getModalCheckedRadioBtnValue = getModalCheckedRadioBtnEl ? getModalCheckedRadioBtnEl.value : "";

    var description = document.getElementById("event-description").value;
    var writer = document.getElementById("event-writer").value;
    var department = document.getElementById("event-department").value;

	// 서버에 보낼 데이터
	 var requestData = {
	   plan_title: getTitleValue,
	   plan_content: description,
	   plan_type: getModalCheckedRadioBtnValue,
	   start_date: setModalStartDateValue,
	   end_date: setModalEndDateValue,
	   reg_member_no: 1 // 로그인된 사용자 번호 (임시)
	 };

	 // 서버로 저장 요청 보내기
	 fetch("/plan/create", {
	   method: "POST",
	   headers: {
	     'Content-Type': 'application/json',
	     'header': document.querySelector('meta[name="_csrf_header"]').content,
	     'X-CSRF-Token': document.querySelector('meta[name="_csrf"]').content
	   },
	   body: JSON.stringify(requestData)
	 })
	   .then(res => res.json())
	   .then(data => {
	     if (data.res_code === "200") {
	       const savedPlan = data.saved_plan; // 서버에서 받은 저장된 plan 정보

	       // FullCalendar에 추가 (plan_no를 id로 사용!)
	       calendar.addEvent({
	         id: savedPlan.plan_no,
	         title: savedPlan.plan_title,
	         start: savedPlan.start_date,
	         end: savedPlan.end_date,
	         allDay: true,
	         extendedProps: {
	           calendar: savedPlan.plan_type,
	           description: savedPlan.plan_content,
	           writer: savedPlan.member_name,
	           department: savedPlan.dept_name
	         }
	       });

	       alert("일정이 등록되었습니다.");
	       bootstrap.Modal.getInstance(document.getElementById("eventModal")).hide();
	     } else {
	       alert("일정 등록 실패: " + data.res_msg);
	     }
	   })
	   .catch(err => {
	     console.error("일정 등록 중 오류", err);
	     alert("서버 오류로 등록 실패");
	   });
  });

  /*=====================*/
  // Calendar Initd
  /*=====================*/
  window.calendar = calendar;
  calendar.render();
  
  // 각 타입별 일정 화면에 출력
  document.querySelectorAll('.legend-item').forEach(item => {
  		item.addEventListener('click', function () {
  		const selectedType = this.getAttribute('data-type'); // 예: "회사"
			console.log("selectedType확인:",selectedType);
			
  		calendar.getEvents().forEach(event => {
  			const eventType = event.extendedProps.planType;
			console.log("eventType확인용:",eventType);
		
			let shouldShow = false;

			      if (!selectedType || selectedType === "개인") {
			        shouldShow = true;
			      } else if (selectedType === "부서") {
			        shouldShow = eventType === "부서" || eventType === "회사";
			      } else {
			        shouldShow = eventType === selectedType;
			      } 
			      event.setProp("display", shouldShow ? "block" : "none");
  		});
  	});
  });
  
  var myModal = new bootstrap.Modal(document.getElementById("eventModal"));
  var modalToggle = document.querySelector(".fc-addEventButton-button ");
  document
    .getElementById("eventModal")
    .addEventListener("hidden.bs.modal", function (event) {
      getModalTitleEl.value = "";
      getModalStartDateEl.value = "";
      getModalEndDateEl.value = "";
	  document.getElementById("event-description").value = "";
      var getModalIfCheckedRadioBtnEl = document.querySelector(
        'input[name="event-level"]:checked'
      );
      if (getModalIfCheckedRadioBtnEl !== null) {
        getModalIfCheckedRadioBtnEl.checked = false;
      }
    });
});