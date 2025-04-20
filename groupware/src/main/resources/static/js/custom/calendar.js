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
  var calendar = new FullCalendar.Calendar(calendarEl, {
    selectable: true,
	locale:'ko',
	dayMaxEvents:true,
	eventDisplay: 'block',
	editable:true,
	expandRows: true,
	allDaySlot: false,
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
					/*console.log("이벤트데이터:"+data);*/
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
    eventClassNames: function ({ event: calendarEvent }) {
      const getColorValue =
        calendarsEvents[calendarEvent._def.extendedProps.calendar];
      return ["event-fc-color fc-bg-" + getColorValue];
    },
    windowResize: function (arg) {
      if (checkWidowWidth()) {
        calendar.changeView("listWeek");
        calendar.setOption("height", 900);
      } else {
        calendar.changeView("dayGridMonth");
        calendar.setOption("height", 1052);
      }
    },
  });
  /*=====================*/
  // Update Calender Event
  /*=====================*/
	  /*상세모달창 삭제버튼 클릭시 동작*/
	 /* getModalDeleteBtnEl.addEventListener("click", function () {
	    const planId = document.getElementById("detail-event-id").value;
		
	    if (!planId) {
	      alert("삭제할 일정이 없습니다.");
	      return;
	    }
	
	    fetch("/plan/"+planId, {
	      method: "DELETE",
	      headers: {
	        'header': document.querySelector('meta[name="_csrf_header"]').content,
	        'X-CSRF-Token': document.querySelector('meta[name="_csrf"]').content
	      }
	    })
	      .then(res => res.json())
	      .then(data => {
	        alert(data.res_msg);
	        if (data.res_code === "200") {
				if (getEvent) {
				  getEvent.remove(); // 달력에서 해당 이벤트 제거
				} else {
				  // 대안: 달력 전체 새로고침
				  calendar.refetchEvents();
				}
	          // 모달 닫기
	          bootstrap.Modal.getInstance(document.getElementById("eventModaldetail")).hide();
	        }
	      })
	      .catch(err => {
	        console.error("서버 삭제 실패", err);
	        alert("삭제 중 오류가 발생했습니다.");
	      });
	  });*/

	  /*상세모달창 수정버튼 클릭시 동작*/
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