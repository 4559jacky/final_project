
function changeToDark() {
	document.documentElement.setAttribute("data-bs-theme", "dark");
	setCookie("theme", "dark", 30);
	setThemeAttributes("dark", "dark-logo", "light-logo", "moon", "sun");
}

function changeToLight() {
	document.documentElement.setAttribute("data-bs-theme", "light");
	setCookie("theme", "light", 30);
	setThemeAttributes("light", "light-logo", "dark-logo", "sun", "moon");
}

function setCookie(name, value, days) {
	const date = new Date();
	date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
	document.cookie = `${name}=${value}; path=/; expires=${date.toUTCString()}`;
}

function setThemeAttributes(theme, hideClass, showClass, hideIcon, showIcon) {
	// 로고 토글
	document.querySelectorAll(`.${hideClass}`).forEach(el => el.style.display = "none");
	document.querySelectorAll(`.${showClass}`).forEach(el => el.style.display = "flex");

	// 아이콘 토글
	document.querySelectorAll(`.${hideIcon}`).forEach(el => el.style.display = "none");
	document.querySelectorAll(`.${showIcon}`).forEach(el => el.style.display = "flex");
}
