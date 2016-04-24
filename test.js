var now = new Date();

function onTimeout() {
	var later = new Date();
	var timediff = later - now;
	
	console.log(timediff);
}

setTimeout(onTimeout, 5000);

