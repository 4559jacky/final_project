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
  	var calendarEventClick = function (info) {
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
  };

  /*=====================*/
  // Active Calender
  /*=====================*/
  var calendar = new FullCalendar.Calendar(calendarEl, {
    selectable: true,
	locale:'ko',
	dayMaxEvents:true,
    height: checkWidowWidth() ? 900 : 1052,
    initialView: checkWidowWidth() ? "listWeek" : "dayGridMonth",
    initialDate: `${newDate.getFullYear()}-${getDynamicMonth()}-07`,
    headerToolbar: calendarHeaderToolbar,
    events: calendarEventsList,
	eventClick:calendarEventClick,
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
      regMemberNo: 1 // 로그인 정보에서 가져오기
/*      planType: "P"*/
    };
  });

  /*getModalAddBtnEl.addEventListener("click", function () {
    var getModalCheckedRadioBtnEl = document.querySelector(
      'input[name="event-level"]:checked'
    );

    var getTitleValue = getModalTitleEl.value;
    var setModalStartDateValue = getModalStartDateEl.value;
    var setModalEndDateValue = getModalEndDateEl.value;
    var getModalCheckedRadioBtnValue =
      getModalCheckedRadioBtnEl !== null ? getModalCheckedRadioBtnEl.value : "";

	var description = document.getElementById("event-description").value;
	var writer = document.getElementById("event-writer").value;
	var department = document.getElementById("event-department").value;  
	  
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
    myModal.hide();
  });*/
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
