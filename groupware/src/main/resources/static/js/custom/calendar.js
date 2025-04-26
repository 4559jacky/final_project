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
  var today = new Date(); //현재 날짜 객체(주,일 날짜변경하기 위한거)
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
  // Active Calender
  /*=====================*/
  let getEvent = null;
  var selectedType = ""; // 현재 선택된 타입
  var calendar = new FullCalendar.Calendar(calendarEl, {
    selectable: true,
	locale:'ko',
	dayMaxEvents: 5,
	dayMaxEventRows:true,
	eventDisplay: 'block',
	editable:true,
	allDaySlot: false,
	slotDuration: '00:30:00',
	navLinks: true,
	nowIndicator:true,
    height: checkWidowWidth() ? 900 : 1052,
    initialView: checkWidowWidth() ? "listWeek" : "dayGridMonth",
	initialDate: today,
	// 주,일 현재날짜를 기준으로 셋팅하는 코드
	windowResize: function () {
		if (checkWidowWidth()) {
		    calendar.changeView("listWeek", new Date()); // 주간 뷰 + 오늘 날짜 기준
		    calendar.setOption("height", 900);
		} else {
		    calendar.changeView("dayGridMonth", new Date()); // 월간 뷰 + 오늘 기준
		    calendar.setOption("height", 1052);
		}
	},
	// 뷰가 바뀔 때마다 실행
	  viewDidMount: function (info) {
	    const viewType = info.view.type;

	    // 만약 주간 뷰로 전환되었다면 오늘 날짜로 강제 이동
	    if (viewType === "timeGridWeek" || viewType === "listWeek") {
	      setTimeout(() => {
	        calendar.gotoDate(today); // 주간 뷰에서도 오늘이 포함된 주 보이기
	      }, 0);
	    }
	  },
    headerToolbar: calendarHeaderToolbar,buttonText,
	eventSources:[
		// DB 이벤트 API 호출
		{
	    events: function(fetchInfo, successCallback,failureCallback){
			fetch(`/calendar/events?start=${fetchInfo.startStr}&end=${fetchInfo.endStr}&type=all`)
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
			}
		},
		{
		// 구글 공휴일 API 호출
		events: function(fetchInfo, successCallback, failureCallback){
			const apiKey = 'AIzaSyDMSm14t7HAsnm6mKP_PxZ-O65c_077ZtQ';
			const calendarId = 'ko.south_korea%23holiday@group.v.calendar.google.com';
			const timeMin = new Date(fetchInfo.start).toISOString();
			const timeMax = new Date(fetchInfo.end).toISOString();
			const url = `https://www.googleapis.com/calendar/v3/calendars/${calendarId}/events?key=${apiKey}&timeMin=${timeMin}&timeMax=${timeMax}&orderBy=startTime&singleEvents=true`;

			fetch(url)
				.then(response => response.json())
				.then(data => {
					if (!data.items) {
					   console.warn("공휴일 데이터 없음", data);
					   return successCallback([]);
					}
					const holidays = data.items.map(item => ({
					    title: item.summary,
					    start: item.start.date,
					    allDay: true,
						color: '#ffcccc',
					    extendedProps: {
					    	type: 'holiday'
					    }
					}));
					 successCallback(holidays);
					})
					 .catch(error => {
					    console.error("공휴일 불러오기 실패", error);
					    failureCallback(error);
					 });
		}
	  }
	],
	// 달력에 있는 일정클릭시 상세모달창open 및 db데이터 화면에 출력
	eventClick:function(info){
		// 본인일정과 본인이 속한 부서의 일정만 수정,삭제 가능하게
		const calendarEl = document.getElementById("calendar");
		if (!calendarEl) {
		        console.error("#calendar 요소를 찾을 수 없습니다.");
		        return;
		    }
			const currentMemberNo = parseInt(calendarEl.dataset.memberNo);  // 로그인한 사용자의 memberNo
			    const currentDeptNo = parseInt(calendarEl.dataset.deptNo);  // 로그인한 사용자의 deptNo

			    const regMemberNo = info.event.extendedProps.regMemberNo;  // 일정 등록자 (작성자) memberNo
			    const eventDeptNo = info.event.extendedProps.deptNo;  // 일정의 부서 번호

			    // 수정, 삭제 버튼 활성화 여부 결정
			    const btnDeleteEvent = document.getElementById('btn-delete-event');
			    const btnUpdateEvent = document.getElementById('btn-update-event');

			    if (btnDeleteEvent && btnUpdateEvent) {
			        // 조건: 본인 작성한 일정이거나, 자신이 속한 부서의 일정
			        if (currentMemberNo === regMemberNo || currentDeptNo === eventDeptNo) {
			            btnDeleteEvent.style.display = 'inline-block'; // 보이기
			            btnUpdateEvent.style.display = 'inline-block';
			        } else {
			            btnDeleteEvent.style.display = 'none'; // 숨기기
			            btnUpdateEvent.style.display = 'none';
			        }
			    }
		//
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
			document.getElementById("detail-event-writer").value = `${data.member_name} (${data.dept_name})`;
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
			/*document.getElementById("detail-event-modified-history").innerHTML = `
				  <ul>
				    <li>${data.member_name} - ${data.mod_date}</li>
				    <li>김민수 - 2024-02-15 수정</li>
				    <li>박지현 - 2024-03-22 수정</li>
				  </ul>
				`;*/

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
	// now Date를 기준으로 이전날짜 클릭 불가능하게 하는 코드
	selectAllow: function(selectInfo) {
		const now = new Date();
		  now.setHours(0, 0, 0, 0); // 오늘 자정 기준으로 시간 초기화
		const start = new Date(selectInfo.start);
		  start.setHours(0, 0, 0, 0); // 선택한 날짜도 자정 기준
		  return start >= now; // 오늘 날짜는 OK, 과거는 막힘
	},
	// 일정바앞 부서명 넣어주는 코드
	eventContent: function(arg) {
	   const planType = arg.event.extendedProps.planType;
	   const department = arg.event.extendedProps.deptName || "";
	   const title = arg.event.title;
		console.log("부서 확인 : ", arg.event.extendedProps.deptName);
	   
		// 부서 일정일 때만 부서명을 앞에 붙임
		   const displayTitle = (planType === "부서" || planType ==='휴가')
		     ? `<strong>[${department}]</strong> ${title}`
		     : title;
		   return {
		     html: `<div>${displayTitle}</div>`
	   };
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
  
  // 선택된 타입에 따라 달력 이벤트 필터링
  document.querySelectorAll('.legend-item').forEach(item => {
  		item.addEventListener('click', function () {
  		const selectedType = this.getAttribute('data-type');
			
  		calendar.getEvents().forEach(event => {
  			const eventType = event.extendedProps.planType;
			//planType이 없는 이벤트는 공휴일로 간주해서 무조건 표시되도록 처리
			const isHoliday = event.extendedProps.type === 'holiday'; // 공휴일 여부
			
			let shouldShow = false;

			if (isHoliday) {
			        shouldShow = true;
			      } else {
			        switch (selectedType) {
			          case '개인':
			            shouldShow = true;
			            break;
			          case '부서':
			            shouldShow = eventType === '부서' || eventType === '회사';
			            break;
			          case '회사':
			            shouldShow = eventType === '회사';
			            break;
			          case '휴가':
			            shouldShow = eventType === '휴가';
			            break;
			          default:
			            shouldShow = false;
			            break;
			        }
			      }
			      event.setProp('display', shouldShow ? 'block' : 'none');
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