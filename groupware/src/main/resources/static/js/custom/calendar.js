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
  // 캘린더 일정추가
  var calendarSelect = function (info) {
    getModalAddBtnEl.style.display = "block";
    getModalUpdateBtnEl.style.display = "none";
    myModal.show();
    getModalStartDateEl.value = info.startStr;
    getModalEndDateEl.value = info.endStr;
  };
  /*=====================*/
  // Calendar AddEvent fn.
  /*=====================*/
  var calendarAddEvent = function () {
    var currentDate = new Date();
    var dd = String(currentDate.getDate()).padStart(2, "0");
    var mm = String(currentDate.getMonth() + 1).padStart(2, "0"); //January is 0!
    var yyyy = currentDate.getFullYear();
    var combineDate = `${yyyy}-${mm}-${dd}T00:00:00`;
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
  var calendar = new FullCalendar.Calendar(calendarEl, {
    selectable: true,
	locale:'ko',
	dayMaxEvents:3,
	eventDisplay: 'block',
	editable:true,
	expandRows: true,
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

	/*eventClick:calendarEventClick,*/
	eventClick:function(info){
		console.log("클릭이벤트 작동 확인",info);
		
		const eventId = info.event.id;
		console.log("eventId체크",eventId);
		
		fetch('/plan/detail/'+eventId, {
			method:'get'
		})
		  .then(res =>{
			if (!res.ok) throw new Error("요청 실패");
			return res.json();
		  })
		  .then(data => {
			new bootstrap.Modal(document.getElementById("eventModaldetail")).show();
			
			console.log("가져온 데이터:", data);
			document.getElementById("detail-event-writer").value = data.member_name;
		    document.getElementById("detail-event-department").value = data.dept_name;
		    document.getElementById("detail-event-title").value = data.plan_title;
		    document.getElementById("detail-event-description").value = data.plan_content;
			/*document.getElementById("type-company").value = data.plan_type;
			document.getElementById("type-team").value = data.plan_type;
			document.getElementById("type-personal").value = data.plan_type;
			document.getElementById("type-leave").value = data.plan_type;*/
			
			document.getElementById("detail-event-created-date").value = data.reg_date;
			document.getElementById("detail-event-modified-date").value = data.mod_date;
			
		    document.getElementById("detail-event-start-date").value = data.start_date;
		    document.getElementById("detail-event-end-date").value = data.end_date;
		    
		  })
		  .catch(err => console.error("디테일 로딩 실패", err));

	},

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
  getModalUpdateBtnEl.addEventListener("click", function () {
	var getPublicID = this.dataset.fcEventPublicId;
	  var getEvent = calendar.getEventById(getPublicID);

	  var newTitle = document.getElementById("event-title").value;
	  var newDescription = document.getElementById("event-description").value;
	  var newStartDate = document.getElementById("event-start-date").value;
	  var newEndDate = document.getElementById("event-end-date").value;
	  var newCalendarType = document.querySelector('input[name="event-level"]:checked')?.value;

	  // 수정일 자동 반영
	  var today = new Date().toISOString().split("T")[0];
	  document.getElementById("event-modified-date").value = today;

	  // 캘린더 업데이트
	  getEvent.setProp("title", newTitle);
	  getEvent.setExtendedProp("description", newDescription);
	  getEvent.setExtendedProp("calendar", newCalendarType);
	  getEvent.setDates(newStartDate, newEndDate);

	  bootstrap.Modal.getInstance(document.getElementById("eventModaldetail")).hide();
	
	/*var getPublicID = this.dataset.fcEventPublicId;
    var getTitleUpdatedValue = getModalTitleEl.value;
    var setModalStartDateValue = getModalStartDateEl.value;
    var setModalEndDateValue = getModalEndDateEl.value;
    var getEvent = calendar.getEventById(getPublicID);
    var getModalUpdatedCheckedRadioBtnEl = document.querySelector(
      'input[name="event-level"]:checked'
    );

    var getModalUpdatedCheckedRadioBtnValue =
      getModalUpdatedCheckedRadioBtnEl !== null
        ? getModalUpdatedCheckedRadioBtnEl.value
        : "";

    getEvent.setProp("title", getTitleUpdatedValue);
    getEvent.setDates(setModalStartDateValue, setModalEndDateValue);
    getEvent.setExtendedProp("calendar", getModalUpdatedCheckedRadioBtnValue);
    myModal.hide();*/
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

    var requestData = {
      planName: getTitleValue,
      planContent: description,
      startDate: setModalStartDateValue,
      endDate: setModalEndDateValue,
      color: getModalCheckedRadioBtnValue,
      regMemberNo: 1
    };
	calendar.addEvent({
	      id: 12,
	      title: getTitleValue,
	      start: setModalStartDateValue,
	      end: setModalEndDateValue,
	      allDay: true,
		  extendedProps: {
		      calendar: getModalCheckedRadioBtnValue,
		      description: description,
			  writer: writer,
			  department: department
		    },
	    });
  });

    /*myModal.hide();*/
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