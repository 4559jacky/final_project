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
	  left: "prev next", // addEventButton 제거!
	  center: "title",
	  right: "", // 아까처럼 월간만 보여주게
  };
  var calendarEventsList = [
    
  ];
  /*=====================*/
  // Calendar Select fn.
  /*=====================*/
  /*var calendarSelect = function (info) {
	// 기존 eventModal은 안 띄움
	  // 대신 새로운 모달에 날짜 정보를 넣고 띄움
	  const selectedDateText = document.getElementById("selected-date-text");
	  selectedDateText.innerText = info.startStr;

	  const dateInfoModal = new bootstrap.Modal(document.getElementById("dateInfoModal"));
	  dateInfoModal.show();
  };*/
  
  /*var calendarSelect = function (info) {
    // 선택한 날짜를 텍스트로 표시
    const selectedDateText = document.getElementById("selected-date-text");
    selectedDateText.innerText = info.startStr;

    // 요청에 보낼 데이터 구성
    const payload = new URLSearchParams();
    payload.append("date", info.startStr);

	// fetch POST 요청
    fetch("/selectMeetingRoom", {
      method: "POST",
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'header': document.querySelector('meta[name="_csrf_header"]').content,
        'X-CSRF-Token': document.querySelector('meta[name="_csrf"]').content
      },
      body: payload
    })
      .then(response => response.json())
      .then(data => {
        console.log("받은 회의실 목록 👉", data);
        renderMeetingRoomButtons(data);
      })

    // 모달 열기
    const dateInfoModal = new bootstrap.Modal(document.getElementById("dateInfoModal"));
    dateInfoModal.show();
  };*/

  
  
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
    var eventObj = info.event;

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
      myModal.show();
    }
  };

  /*=====================*/
  // Active Calender
  /*=====================*/
  var calendar = new FullCalendar.Calendar(calendarEl, {
    locale: 'ko',
    selectable: true,
    dayMaxEvents: true,
    height: checkWidowWidth() ? 900 : 1052,
    initialView: checkWidowWidth() ? "listWeek" : "dayGridMonth",
    initialDate: `${newDate.getFullYear()}-${getDynamicMonth()}-07`,
    headerToolbar: calendarHeaderToolbar,
	events: function (fetchInfo, successCallback, failureCallback) {
	  fetch('/selectReservation', {
	    method: 'GET',
	    headers: {
	      'X-Custom-Ajax': 'true'
	    }
	  })
	  .then(response => {
	    if (!response.ok) {
	      throw new Error('네트워크 오류');
	    }
	    return response.json();
	  })
	  .then(data => {
	    successCallback(data);
	  })
	  .catch(error => {
	    console.error('이벤트 로딩 실패:', error);
	    failureCallback(error);
	  });
	},
    select: calendarSelect,
    unselect: function () {
      console.log("unselected");
    },

    // ✅ 회의실 이름 같이 표시하는 부분 추가
    eventDidMount: function(info) {
      const roomName = info.event.extendedProps.roomName; // 백엔드에서 넘긴 값
      const titleEl = info.el.querySelector('.fc-event-title');

      if (titleEl && roomName) {
        titleEl.innerText += ` [${roomName}]`;  // ex: 회의 제목 (1회의실)
      }
    },

    eventClassNames: function ({ event: calendarEvent }) {
      const getColorValue =
        calendarsEvents[calendarEvent._def.extendedProps.calendar];
      return ["event-fc-color fc-bg-" + getColorValue];
    },

    eventClick: calendarEventClick,

    windowResize: function (arg) {
      calendar.changeView("dayGridMonth");
      calendar.setOption("height", checkWidowWidth() ? 900 : 1052);
    },
  });


  /*=====================*/
  // Update Calender Event
  /*=====================*/
  getModalUpdateBtnEl.addEventListener("click", function () {
    var getPublicID = this.dataset.fcEventPublicId;
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
    myModal.hide();
  });
  /*=====================*/
  // Add Calender Event
  /*=====================*/
  getModalAddBtnEl.addEventListener("click", function () {
    var getModalCheckedRadioBtnEl = document.querySelector(
      'input[name="event-level"]:checked'
    );

    var getTitleValue = getModalTitleEl.value;
    var setModalStartDateValue = getModalStartDateEl.value;
    var setModalEndDateValue = getModalEndDateEl.value;
    var getModalCheckedRadioBtnValue =
      getModalCheckedRadioBtnEl !== null ? getModalCheckedRadioBtnEl.value : "";

    calendar.addEvent({
      id: 12,
      title: getTitleValue,
      start: setModalStartDateValue,
      end: setModalEndDateValue,
      allDay: true,
      extendedProps: { calendar: getModalCheckedRadioBtnValue },
    });
    myModal.hide();
  });
  /*=====================*/
  // Calendar Init
  /*=====================*/
  calendar.render();
  var myModal = new bootstrap.Modal(document.getElementById("eventModal"));
  var modalToggle = document.querySelector(".fc-addEventButton-button ");
  document
    .getElementById("eventModal")
    .addEventListener("hidden.bs.modal", function (event) {
      getModalTitleEl.value = "";
      getModalStartDateEl.value = "";
      getModalEndDateEl.value = "";
      var getModalIfCheckedRadioBtnEl = document.querySelector(
        'input[name="event-level"]:checked'
      );
      if (getModalIfCheckedRadioBtnEl !== null) {
        getModalIfCheckedRadioBtnEl.checked = false;
      }
    });
});
